package org.rj.mylelo.elomyl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.rj.mylelo.elomyl.model.Person;
import org.rj.mylelo.elomyl.model.PlayerElo;
import org.rj.mylelo.elomyl.model.TournamentMatch;
import org.rj.mylelo.elomyl.util.HibernateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


//esta wea la construyo el compare chatgpt, le falta harto, mañana lo arreglo 23-04-2026

@Slf4j
public class EloCalculator {

    static final double ELO_INICIAL = 1000.0;
    static final int K_INICIAL = 40;   // primeras 30 partidas
    static final int K_NORMAL = 20;   // después de 30 partidas
    static final int UMBRAL_K = 30;   // partidas hasta K_INICIAL

    public void calculate() {
        // 1. Cargar todos los jugadores en memoria
        Map<Long, PlayerElo> eloMap = loadPlayers();
        log.info("Jugadores cargados: {}", eloMap.size());

        // 2. Cargar matches válidos ordenados cronológicamente
        List<TournamentMatch> matches = loadValidMatches();
        log.info("Matches a procesar: {}", matches.size());

        // 3. Procesar match por match
        int processed = 0;
        for (TournamentMatch m : matches) {
            processMatch(m, eloMap);
            processed++;
            if (processed % 1000 == 0) {
                log.info("  {} matches procesados...", processed);
            }
        }

        // 4. Guardar todos los ELOs en BD
        saveAllElos(eloMap);
        log.info("ELOs guardados para {} jugadores", eloMap.size());
    }

    // ─────────────────────────────────────────────────────────────────
    // Carga de datos
    // ─────────────────────────────────────────────────────────────────
    private Map<Long, PlayerElo> loadPlayers() {
        Map<Long, PlayerElo> map = new HashMap<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Person> persons = session.createQuery("FROM Person", Person.class).list();
            for (Person p : persons) {
                map.put(p.getId(), new PlayerElo(p.getId(), p.getCode(), p.getFullName()));
            }
        }
        return map;
    }

    private List<TournamentMatch> loadValidMatches() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT m FROM TournamentMatch m "
                    + "JOIN Tournament t ON t.id = m.tournamentId "
                    + "WHERE "
                    + "t.typeId IN (4) "
                    //+ " t.seasonId IN (72) "
                    + "ORDER BY t.startDate ASC, m.tournamentId ASC, m.roundId ASC, m.id ASC",
                    TournamentMatch.class
            ).list();
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Lógica ELO
    // ─────────────────────────────────────────────────────────────────
    private void processMatch(TournamentMatch m, Map<Long, PlayerElo> eloMap) {

        Long playerPersonId = m.getPlayerPersonId();
        Long opponentPersonId = m.getOpponentPersonId();

        // Ignorar byes (oponente = 0)
        if (playerPersonId == 0 || opponentPersonId == 0) {
            return;
        }

        PlayerElo playerElo = eloMap.get(playerPersonId);
        PlayerElo opponentElo = eloMap.get(opponentPersonId);

        // Si por alguna razón no están en el mapa, crearlos
        if (playerElo == null) {
            playerElo = new PlayerElo(playerPersonId, "", "");
            eloMap.put(playerPersonId, playerElo);
        }
        if (opponentElo == null) {
            opponentElo = new PlayerElo(opponentPersonId, "", "");
            eloMap.put(opponentPersonId, opponentElo);
        }

        double eloA = playerElo.getEloCurrent();
        double eloB = opponentElo.getEloCurrent();

        // Probabilidad esperada
        double expectedA = expectedScore(eloA, eloB);
        double expectedB = expectedScore(eloB, eloA);

        // Resultado real
        double scoreA, scoreB;

        if (m.getDraw() == 1 || m.getWinnerId() == 0) {
            scoreA = 0.5;
            scoreB = 0.5;
            playerElo.setDraws(playerElo.getDraws() + 1);
            opponentElo.setDraws(opponentElo.getDraws() + 1);
        } else if (m.getWinnerId().equals(m.getPlayerId())) {
            scoreA = 1.0;
            scoreB = 0.0;
            playerElo.setWins(playerElo.getWins() + 1);
            opponentElo.setLosses(opponentElo.getLosses() + 1);
        } else {
            scoreA = 0.0;
            scoreB = 1.0;
            opponentElo.setWins(opponentElo.getWins() + 1);
            playerElo.setLosses(playerElo.getLosses() + 1);
        }
        int kA = getK(playerElo.getMatchesPlayed());
        int kB = getK(opponentElo.getMatchesPlayed());

        double newEloA = eloA + kA * (scoreA - expectedA);
        double newEloB = eloB + kB * (scoreB - expectedB);

        playerElo.setEloPrevious(eloA);
        playerElo.setEloCurrent(newEloA);
        playerElo.setEloChange(newEloA - eloA);
        playerElo.setMatchesPlayed(playerElo.getMatchesPlayed() + 1);
        if (newEloA > playerElo.getEloPeak()) {
            playerElo.setEloPeak(newEloA);
        }

        opponentElo.setEloPrevious(eloB);
        opponentElo.setEloCurrent(newEloB);
        opponentElo.setEloChange(newEloB - eloB);
        opponentElo.setMatchesPlayed(opponentElo.getMatchesPlayed() + 1);
        if (newEloB > opponentElo.getEloPeak()) {
            opponentElo.setEloPeak(newEloB);
        }
    }

    private double expectedScore(double eloA, double eloB) {
        return 1.0 / (1.0 + Math.pow(10.0, (eloB - eloA) / 400.0));
    }

    private int getK(int matchesPlayed) {
        return matchesPlayed < UMBRAL_K ? K_INICIAL : K_NORMAL;
    }

    private void saveAllElos(Map<Long, PlayerElo> eloMap) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery("DELETE FROM PlayerElo").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error limpiando tabla elo: " + e.getMessage(), e);
        }

        tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            int count = 0;
            for (PlayerElo elo : eloMap.values()) {
                if (elo.getMatchesPlayed() == 0) {
                    continue;
                }
                session.persist(elo);
                count++;
                if (count % 100 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            tx.commit();
            log.info("  {} registros ELO guardados", count);
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error guardando ELOs: " + e.getMessage(), e);
        }

    }
}
