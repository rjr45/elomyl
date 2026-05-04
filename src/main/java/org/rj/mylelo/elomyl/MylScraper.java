package org.rj.mylelo.elomyl;

import com.google.gson.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.rj.mylelo.elomyl.dao.GenericDao;
import org.rj.mylelo.elomyl.dto.AllTournamentRoundsDto;
import org.rj.mylelo.elomyl.dto.AllTournamentRoundsDto.AllTournamentRounds;
import org.rj.mylelo.elomyl.dto.TournamentDataDto;
import org.rj.mylelo.elomyl.dto.TournamentInfoDto;
import org.rj.mylelo.elomyl.dto.TournamentMatchesDto;
import org.rj.mylelo.elomyl.dto.TournamentMatchesDto.TournamentMatches;
import org.rj.mylelo.elomyl.model.*;
import org.rj.mylelo.elomyl.util.DtoMapper;
import static org.rj.mylelo.elomyl.util.DtoMapper.getRaceGamesFromTournamentInfoDto;
import static org.rj.mylelo.elomyl.util.DtoMapper.getRoundFromTournamentDto;
import static org.rj.mylelo.elomyl.util.DtoMapper.getRoundFromTournamentMatchDto;
import static org.rj.mylelo.elomyl.util.DtoMapper.getStoreFromTournamentInfoDto;
import static org.rj.mylelo.elomyl.util.DtoMapper.getTournamentMatchFromTournamentMatchDto;
import static org.rj.mylelo.elomyl.util.DtoMapper.getTournamentPlayerFromTournamentMatchDto;
import static org.rj.mylelo.elomyl.util.DtoMapper.getTournamentTypeFromTournamentInfoDto;
import static org.rj.mylelo.elomyl.util.DtoMapper.updateTournamentFromTournamentInfoDto;
import org.rj.mylelo.elomyl.util.HibernateUtil;

@Slf4j
public class MylScraper {

    static final String BASE_URL = "https://torneos.myl.cl";
    static final int GAME_ID = 11;
    static final int THREADS = 1;
    static final int DEBUG_PORT_BASE = 9222;
    static final int SEASON_ID = 72;
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        log.info("=== EloCalculator iniciado ===");
        new CalcularElo().calcularElo();
        //new EloCalculator().calculate();
        log.info("=== EloCalculator finalizado ===");

        log.info("=== MylScraper iniciado ===");

        log.info("[FASE 1] Recolectando torneos nuevos...");
        collectAllTournaments();

        log.info("[FASE 2] Scrapeando detalle de torneos pendientes...");
        scrapeTournamentsParallel();

        log.info("=== FIN ===");

        HibernateUtil.shutdown();
    }

    static void collectAllTournaments() throws Exception {
        try (CdpSession session = new CdpSession(DEBUG_PORT_BASE)) {
            session.start();

            int page = 1;
            int totalPages = 1;
            int totalNuevos = 0;

            while (page <= totalPages) {
                log.info("Pagina {}/{}", page, totalPages);

                String url = BASE_URL + "/tournaments?gameId=" + GAME_ID + "&page=" + page + "&seasonId=" + SEASON_ID;
                String json = session.navigateAndCapture(url, "TournamentListV2", 5);

                if (json == null) {
                    log.warn("Reintentando pagina {}", page);
                    int intento = 1;
                    for (int i = 0; i < 5; i++) {
                        log.info("Reintento {}", intento);
                        json = session.reloadAndCapture("TournamentListV2", 3);
                        if (json != null) {
                            if (json.trim().startsWith("{")) {
                                break;
                            }
                        }
                        intento++;
                    }
                }

                if (json == null) {
                    log.error("No se pudo obtener página {}, saltando", page);
                    page++;
                    continue;
                }

                TournamentDataDto data;
                try {
                    data = gson.fromJson(json, TournamentDataDto.class);
                } catch (JsonSyntaxException e) {
                    log.error("JSON invalido en pagina {}, saltando", page);
                    log.error(e.getMessage());
                    page++;
                    continue;
                }

                if (data == null || data.data == null || data.data.TournamentListV2 == null) {
                    log.error("Estructura inesperada en pagina {}, saltando", page);
                    page++;
                    continue;
                }

                totalPages = data.data.TournamentListV2.pages;

                List<Tournament> tournamentEntities = data.data.TournamentListV2.Tournaments
                        .stream()
                        .map(DtoMapper::getTournamentFromDto)
                        .collect(Collectors.toList());

                List<Long> ids = tournamentEntities.stream()
                        .map(Tournament::getId)
                        .collect(Collectors.toList());

                if (GenericDao.allTournamentsExist(ids)) {
                    log.info("Pagina {} completamente conocida → deteniendo colección", page);
                    break;
                }

                int insertados = GenericDao.insertNewOnly(tournamentEntities, ids);
                totalNuevos += insertados;
                log.info("{} torneos nuevos en pagina {}", insertados, page);

                page++;
            }

            log.info("Total torneos nuevos insertados: {}", totalNuevos);
        }
    }

    static void scrapeTournamentsParallel() throws Exception {
        List<Tournament> pending = GenericDao.getPendingTournaments();
        log.info("Torneos pendientes: {}", pending.size());

        if (pending.isEmpty()) {
            log.info("Nada que scrapear.");
            return;
        }

        BlockingQueue<Tournament> queue = new LinkedBlockingQueue<>(pending);
        AtomicInteger done = new AtomicInteger(0);
        int total = pending.size();

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);

        for (int i = 0; i < THREADS; i++) {
            final int idx = i;
            pool.submit(() -> {
                try (CdpSession session = new CdpSession(DEBUG_PORT_BASE + idx)) {
                    session.start();

                    Tournament t;
                    while ((t = queue.poll()) != null) {
                        try {
                            scrapeTournament(session, t);

                            //TODO: aqui falta saber si el torneo qlo se proceso o nel, si no se procesa ofrecerlo al pool 2026-04-23
                            int d = done.incrementAndGet();
                            if (d % 10 == 0 || d == total) {
                                log.info("[{}/{}] torneos procesados", d, total);
                            }

                            int delay = 800 + new Random().nextInt(1200);
                            Thread.sleep(delay);
                        } catch (Exception e) {
                            log.error("Error torneo {}", t.getId(), e);
                            queue.offer(t);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error iniciando CDP thread {}", idx, e);
                }
            });
        }

        pool.shutdown();
        while (!pool.awaitTermination(1, TimeUnit.MINUTES)) {
            log.info("Esperando que finalicen los hilos...");
        }
    }

    static void scrapeTournament(CdpSession session, Tournament t) throws Exception {
        if (t.getUrlView() == null || t.getUrlView().isEmpty()) {
            log.warn("Torneo {} sin urlView, saltando", t.getId());
            return;
        }

        String urlRounds = "";
        TournamentInfoDto data;

        if (t.getUrlRound() == null || "".equals(t.getUrlRound())) {
            String viewUrl = BASE_URL + t.getUrlView();
            String infoJson = session.navigateAndCapture(viewUrl, "TournamentInfo", 5);

            if (infoJson == null) {
                log.warn("Reintentando torneo {}", t.toString());
                int intento = 1;
                for (int i = 0; i < 10; i++) {
                    log.info("Reintento {}", intento);
                    infoJson = session.reloadAndCapture("TournamentInfo", 3);
                    if (infoJson != null) {
                        if (infoJson.trim().startsWith("{\"data\"")) {
                            break;
                        }
                    }
                    intento++;
                    Thread.sleep(1000);
                }
            }

            if (infoJson == null || !infoJson.trim().startsWith("{\"data\"")) {
                log.error("Sin TournamentInfo para torneo {}", t.getId());
                return;
            }

            data = gson.fromJson(infoJson, TournamentInfoDto.class);

            if (data == null) {
                log.warn("JSON no contiene TournamentInfo, torneo {}", t.getId());
                return;
            }

            urlRounds = data.data.TournamentInfo.Tournament.urlRounds;

            //actualizar o crear el resto de objetos
            Game game = DtoMapper.getGameFromTournamentInfoDto(data);
            List<RaceGame> raceGames = getRaceGamesFromTournamentInfoDto(data);
            Store store = getStoreFromTournamentInfoDto(data);
            TournamentType tournamentType = getTournamentTypeFromTournamentInfoDto(data);
            t = updateTournamentFromTournamentInfoDto(data, t);

            if (t.getStatus() < 5) {
                log.info("Torneo {} con estado {}: {}", t.getId(), t.getStatus(), t.getUrlView());
                return;
            }

            GenericDao.updateEntity(game);
            GenericDao.updateEntity(raceGames);
            GenericDao.updateEntity(store);
            GenericDao.updateEntity(tournamentType);
        } else {
            urlRounds = t.getUrlRound();
        }

        if (urlRounds.isEmpty()) {
            log.warn("Torneo {} sin urlRounds, saltando", t.getId());
            return;
        }

        String roundsUrl = BASE_URL + urlRounds;

        String roundsJson = session.navigateAndCapture(roundsUrl, "allTournamentRounds", 5);
        if (roundsJson == null) {
            log.warn("Reintentando rondas {}", t.toString());
            int intento = 1;
            for (int i = 0; i < 10; i++) {
                log.info("Reintento {}", intento);
                roundsJson = session.reloadAndCapture("allTournamentRounds", 3);
                if (roundsJson != null) {
                    if (roundsJson.trim().startsWith("{\"data\"")) {
                        break;
                    }
                }
                intento++;
                Thread.sleep(1000);
            }
        }

        if (roundsJson == null || !roundsJson.trim().startsWith("{\"data\"")) {
            log.warn("Sin rondas para torneo {}", t.getId());
            return;
        }

        AllTournamentRoundsDto rounds = gson.fromJson(roundsJson, AllTournamentRoundsDto.class);

        if (rounds == null) {
            log.warn("JSON no contiene allTournamentRounds, torneo {}", t.getId());
            return;
        }

        for (AllTournamentRounds round : rounds.data.allTournamentRounds) {
            TournamentRound tournamentRound = getRoundFromTournamentDto(round);
            Long roundId = tournamentRound.getId();
            GenericDao.insertIfNotExists(tournamentRound, roundId);

            String roundViewUrl = BASE_URL + tournamentRound.getUrlView() + "/" + tournamentRound.getTournamentId();
            String roundData = session.navigateAndCapture(roundViewUrl, "TournamentRound", 20);

            if (roundData == null) {
                log.warn("Reintentando match {}", t.toString());
                int intento = 1;
                for (int i = 0; i < 10; i++) {
                    log.info("Reintento {}", intento);
                    roundData = session.reloadAndCapture("TournamentRound", 3);
                    if (roundData != null) {
                        if (roundData.trim().startsWith("{\"data\"")) {
                            break;
                        }
                    }
                    intento++;
                    Thread.sleep(1500);
                }
            }

            if (roundData == null || !roundData.trim().startsWith("{\"data\"")) {
                log.warn("Sin match para ronda {}, url: {}", roundId, roundViewUrl);
                continue;
            }

            TournamentMatchesDto matches = gson.fromJson(roundData, TournamentMatchesDto.class);

            List<Person> persons = new ArrayList<>();
            List<TournamentPlayer> players = new ArrayList<>();
            List<TournamentMatch> matchList = new ArrayList<>();

            for (TournamentMatches tournamentMatch : matches.data.TournamentRound.TournamentMatches) {
                if (tournamentMatch.opponentId != 0L) {
                    Person playerPerson = getRoundFromTournamentMatchDto(tournamentMatch.Player.Person);
                    TournamentPlayer tournamentPlayer = getTournamentPlayerFromTournamentMatchDto(tournamentMatch, "player");
                    persons.add(playerPerson);
                    players.add(tournamentPlayer);

                    Person opponentPerson = getRoundFromTournamentMatchDto(tournamentMatch.Opponent.Person);
                    TournamentPlayer tournamentOpponent = getTournamentPlayerFromTournamentMatchDto(tournamentMatch, "opponent");
                    persons.add(opponentPerson);
                    players.add(tournamentOpponent);

                    TournamentMatch match = getTournamentMatchFromTournamentMatchDto(tournamentMatch);
                    matchList.add(match);
                }
            }

            GenericDao.updateEntity(persons);
            GenericDao.updateEntity(players);
            GenericDao.updateEntity(matchList);

            t.setScraped(true);
            GenericDao.updateEntity(t);
            log.info("Ronda {}, {} match nuevos", roundId, matchList.size());

            Thread.sleep(1000);
        }

    }

}
