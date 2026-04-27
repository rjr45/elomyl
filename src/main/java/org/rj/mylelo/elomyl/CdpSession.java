package org.rj.mylelo.elomyl;

import com.google.gson.*;
import java.net.URI;
import java.net.http.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@Slf4j
public class CdpSession implements AutoCloseable {

    private final int debugPort;
    private ChromeDriver driver;
    private WebSocket ws;
    private HttpClient httpClient;

    private volatile String capturedJson = null;
    private volatile String waitKeyword = null;
    private volatile CountDownLatch latch = null;

    private final StringBuilder buffer = new StringBuilder();
    private final AtomicInteger msgId = new AtomicInteger(10);

    public CdpSession(int debugPort) {
        this.debugPort = debugPort;
    }

    public void start() throws Exception {

        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--start-minimized");
        options.addArguments("--remote-debugging-port=" + debugPort);
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        httpClient = HttpClient.newHttpClient();

        Thread.sleep(800);
    }

    public String navigateAndCapture(String url, String keyword, int timeoutSec) throws Exception {
        capturedJson = null;
        waitKeyword = keyword;
        latch = new CountDownLatch(1);

        driver.get(url);
        Thread.sleep(1500);

        connectToCorrectTab();

        boolean ok = latch.await(timeoutSec, TimeUnit.SECONDS);

        return ok ? capturedJson : null;
    }

    private void connectToCorrectTab() throws Exception {

        HttpResponse<String> resp = httpClient.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + debugPort + "/json"))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        JsonArray pages = JsonParser.parseString(resp.body()).getAsJsonArray();

        String wsUrl = null;

        for (int i = 0; i < pages.size(); i++) {
            JsonObject page = pages.get(i).getAsJsonObject();
            String url = page.get("url").getAsString();

            if (url.contains("torneos.myl.cl")) {
                wsUrl = page.get("webSocketDebuggerUrl").getAsString();
                break;
            }
        }

        if (wsUrl == null) {
            throw new RuntimeException("No se encontró tab correcto");
        }

        ws = httpClient.newWebSocketBuilder()
                .buildAsync(URI.create(wsUrl), new WsListener())
                .join();

        ws.sendText("{\"id\":1,\"method\":\"Network.enable\",\"params\":{}}", true);
        Thread.sleep(300);
    }

    public String reloadAndCapture(String keyword, int timeoutSec) throws Exception {
        return navigateAndCapture(driver.getCurrentUrl(), keyword, timeoutSec);
    }

    public void close() {
        try {
            if (ws != null) {
                ws.sendClose(WebSocket.NORMAL_CLOSURE, "done");
            }
        } catch (Exception ignored) {
        }
        try {
            driver.quit();
        } catch (Exception ignored) {
        }
    }

    private class WsListener implements WebSocket.Listener {

        @Override
        public void onOpen(WebSocket webSocket) {
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {

            buffer.append(data);

            if (last) {
                String message = buffer.toString();
                buffer.setLength(0);

                try {
                    JsonObject json = JsonParser.parseString(message).getAsJsonObject();

                    if (json.has("method")
                            && "Network.responseReceived".equals(json.get("method").getAsString())) {

                        JsonObject params = json.getAsJsonObject("params");
                        String requestId = params.get("requestId").getAsString();
                        String url = params.getAsJsonObject("response").get("url").getAsString();

                        if (url.contains("graphql")) {
                            sendGetBody(webSocket, requestId);
                        }
                    }

                    if (json.has("method")
                            && "Network.loadingFinished".equals(json.get("method").getAsString())) {

                        String requestId = json.getAsJsonObject("params").get("requestId").getAsString();
                        sendGetBody(webSocket, requestId);
                    }

                    if (json.has("id")
                            && json.has("result")
                            && json.getAsJsonObject("result").has("body")) {

                        String body = json.getAsJsonObject("result").get("body").getAsString();

                        if (waitKeyword != null && body.contains(waitKeyword)) {
                            capturedJson = body;
                            latch.countDown();
                        }
                    }

                } catch (JsonSyntaxException ex) {
                }
            }

            webSocket.request(1);
            return null;
        }

        private void sendGetBody(WebSocket ws, String requestId) {
            int id = msgId.getAndIncrement();
            ws.sendText(
                    "{\"id\":" + id + ","
                    + "\"method\":\"Network.getResponseBody\","
                    + "\"params\":{\"requestId\":\"" + requestId + "\"}}",
                    true
            );
        }
    }
}
