package org.rj.mylelo.elomyl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.rj.mylelo.elomyl.dto.AllTournamentRoundsDto.AllTournamentRounds;
import org.rj.mylelo.elomyl.dto.TournamentDataDto.Tournaments;
import org.rj.mylelo.elomyl.dto.TournamentInfoDto;
import org.rj.mylelo.elomyl.dto.TournamentMatchesDto.PersonDto;
import org.rj.mylelo.elomyl.dto.TournamentMatchesDto.TournamentMatches;
import org.rj.mylelo.elomyl.model.Game;
import org.rj.mylelo.elomyl.model.Person;
import org.rj.mylelo.elomyl.model.RaceGame;
import org.rj.mylelo.elomyl.model.Store;
import org.rj.mylelo.elomyl.model.Tournament;
import org.rj.mylelo.elomyl.model.TournamentMatch;
import org.rj.mylelo.elomyl.model.TournamentPlayer;
import org.rj.mylelo.elomyl.model.TournamentRound;
import org.rj.mylelo.elomyl.model.TournamentType;

public class DtoMapper {

    public static Tournament getTournamentFromDto(Tournaments tournamentDto) {
        Tournament tournament = new Tournament();
        tournament.setId(tournamentDto.id);
        tournament.setName(tournamentDto.name);
        tournament.setUrlView(tournamentDto.urlView);
        tournament.setStartDate(tournamentDto.startDate);
        tournament.setRounds(tournamentDto.rounds);
        tournament.setIsPresential(tournamentDto.isPresential);
        tournament.setFormatName(tournamentDto.Format.name);
        tournament.setStoreName(tournamentDto.Store == null ? "sin nombre" : tournamentDto.Store.name);
        tournament.setScraped(false);
        tournament.setStatus(tournamentDto.statusId);
        return tournament;
    }

    public static TournamentRound getRoundFromTournamentDto(AllTournamentRounds roundDto) {
        TournamentRound round = new TournamentRound();
        round.setId(roundDto.id);
        round.setTournamentId(roundDto.tournamentId);
        round.setName(roundDto.name);
        round.setDescription(roundDto.description);
        round.setSortOrder(roundDto.sortOrder);
        round.setStatusId(roundDto.statusId);
        round.setTotalMatches(roundDto.totalMatches);
        round.setTotalMatchPendings(roundDto.totalMatchPendings);
        round.setMinutes(roundDto.minutes);
        round.setCreatedAt(roundDto.created);
        round.setUrlView(roundDto.urlView);
        round.setUrlStanding(roundDto.urlStanding);

        return round;
    }

    public static Person getRoundFromTournamentMatchDto(PersonDto personDto) {
        Person person = new Person();
        person.setId(personDto.id);
        person.setName(personDto.name);
        person.setFullName(personDto.fullName);
        person.setCode(personDto.code);
        person.setPhone(personDto.phone);
        return person;
    }

    public static TournamentPlayer getTournamentPlayerFromTournamentMatchDto(TournamentMatches tournamentMatch, String role) {
        TournamentPlayer tournamentPlayer = new TournamentPlayer();
        tournamentPlayer.setId("player".equals(role) ? tournamentMatch.playerId : tournamentMatch.opponentId);
        tournamentPlayer.setDropFlag("player".equals(role) ? tournamentMatch.dropPlayer : tournamentMatch.dropOpponent);
        tournamentPlayer.setPersonId("player".equals(role) ? tournamentMatch.Player.Person.id : tournamentMatch.Opponent.Person.id);
        tournamentPlayer.setPoints("player".equals(role) ? tournamentMatch.Player.points : tournamentMatch.Opponent.points);
        tournamentPlayer.setRaceId("player".equals(role) ? tournamentMatch.Player.raceId : tournamentMatch.Opponent.raceId);
        tournamentPlayer.setTournamentId(tournamentMatch.tournamentId);
        return tournamentPlayer;
    }

    public static TournamentMatch getTournamentMatchFromTournamentMatchDto(TournamentMatches match) {
        TournamentMatch tournamentMatch = new TournamentMatch();
        tournamentMatch.setId(match.id);
        tournamentMatch.setDraw(match.draw);
        tournamentMatch.setDropOpponent(match.dropOpponent);
        tournamentMatch.setDropPlayer(match.dropPlayer);
        tournamentMatch.setLoserId(match.loserId);
        tournamentMatch.setLoserVictories(match.loserVictories);
        tournamentMatch.setModified(match.modified);
        tournamentMatch.setOpponentId(match.opponentId);
        tournamentMatch.setOpponentPersonId(match.Opponent.Person.id);
        tournamentMatch.setPlayerId(match.playerId);
        tournamentMatch.setPlayerPersonId(match.Player.Person.id);
        tournamentMatch.setReported(match.reported);
        tournamentMatch.setRoundId(match.roundId);
        tournamentMatch.setTableNum(match.table);
        tournamentMatch.setTournamentId(match.tournamentId);
        tournamentMatch.setWinnerId(match.winnerId);
        tournamentMatch.setWinnerVictories(match.winnerVictories);
        return tournamentMatch;
    }

    public static Game getGameFromTournamentInfoDto(TournamentInfoDto data) {
        Game game = new Game();
        game.setId(data.data.TournamentInfo.Tournament.Game.id);
        game.setName(data.data.TournamentInfo.Tournament.Game.name);

        return game;
    }

    public static List<RaceGame> getRaceGamesFromTournamentInfoDto(TournamentInfoDto data) {
        Integer raceId = data.data.TournamentInfo.Tournament.gameId;

        List<RaceGame> raceGames = data.data.TournamentInfo.Tournament.Game.RaceGames.stream().map(rg -> {
            RaceGame raceGame = new RaceGame();
            raceGame.setGameId(raceId);
            raceGame.setId(rg.raceId);
            raceGame.setSortOrder(rg.sortOrder);
            raceGame.setName(rg.Race.name);
            return raceGame;
        }).collect(Collectors.toList());

        return raceGames;
    }

    public static Store getStoreFromTournamentInfoDto(TournamentInfoDto data) {
        Store store = new Store();
        store.setId(data.data.TournamentInfo.Tournament.Store.id);
        store.setName(data.data.TournamentInfo.Tournament.Store.name);
        store.setDescription(data.data.TournamentInfo.Tournament.Store.description);
        store.setPhone(data.data.TournamentInfo.Tournament.Store.phone);
        store.setLevelId(data.data.TournamentInfo.Tournament.Store.levelId);
        store.setActive(data.data.TournamentInfo.Tournament.Store.active);
        store.setEmail(data.data.TournamentInfo.Tournament.Store.email);
        store.setSiteUrl(data.data.TournamentInfo.Tournament.Store.siteUrl);
        store.setRegion(data.data.TournamentInfo.Tournament.Address.Region);
        store.setCountryId(data.data.TournamentInfo.Tournament.Address.countryId);
        return store;
    }

    public static TournamentType getTournamentTypeFromTournamentInfoDto(TournamentInfoDto data) {
        TournamentType tournamentType = new TournamentType();
        tournamentType.setId(data.data.TournamentInfo.Tournament.typeId);
        tournamentType.setName(data.data.TournamentInfo.Tournament.TournamentType.name);
        tournamentType.setDescription(data.data.TournamentInfo.Tournament.TournamentType.description);
        tournamentType.setWinnerPoints(data.data.TournamentInfo.Tournament.TournamentType.winnerPoints);
        tournamentType.setMinPlayers(data.data.TournamentInfo.Tournament.TournamentType.minPlayers);
        tournamentType.setMaxTopPlayers(data.data.TournamentInfo.Tournament.TournamentType.maxTopPlayers);
        tournamentType.setLoserPoints(data.data.TournamentInfo.Tournament.TournamentType.loserPoints);
        tournamentType.setDrawPoints(data.data.TournamentInfo.Tournament.TournamentType.drawPoints);
        tournamentType.setMultiplier(data.data.TournamentInfo.Tournament.TournamentType.multiplier);
        tournamentType.setStatus(data.data.TournamentInfo.Tournament.TournamentType.status);
        return tournamentType;
    }

    public static Tournament updateTournamentFromTournamentInfoDto(TournamentInfoDto data, Tournament tournament) {
        tournament.setUrlRound(data.data.TournamentInfo.Tournament.urlRounds);
        tournament.setFormatId(data.data.TournamentInfo.Tournament.formatId);
        tournament.setGameId(data.data.TournamentInfo.Tournament.gameId);
        tournament.setMaxPlayers(data.data.TournamentInfo.Tournament.maxPlayers);
        tournament.setRankingValid(data.data.TournamentInfo.Tournament.rankingValid);
        tournament.setSeasonId(data.data.TournamentInfo.Tournament.seasonId);
        tournament.setStoreId(data.data.TournamentInfo.Tournament.storeId);
        tournament.setTypeId(data.data.TournamentInfo.Tournament.typeId);
        tournament.setUrlStandings(data.data.TournamentInfo.Tournament.urlStandings);
        tournament.setStatus(data.data.TournamentInfo.Tournament.statusId);
        tournament.setScraped(true);
        return tournament;
    }

}
