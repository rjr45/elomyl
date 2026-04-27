package org.rj.mylelo.elomyl.dto;

import java.util.List;
import lombok.ToString;

@ToString
public class TournamentMatchesDto {

    public Data data;

    @ToString
    public static class Data {

        public TournamentRound TournamentRound;

    }

    @ToString
    public static class TournamentRound {

        public Long id;
        public String name;
        public Integer sortOrder;
        public String description;
        public String urlView;
        public String urlStanding;
        public Integer statusId;

        public List<TournamentMatches> TournamentMatches;
    }

    @ToString
    public static class TournamentMatches {

        public Long id;
        public Long tournamentId;
        public Long roundId;
        public Integer table;
        public Integer reported;
        public Integer draw;
        public Integer dropPlayer;
        public Integer dropOpponent;
        public Integer winnerVictories;
        public Integer loserVictories;
        public Long playerId;
        public Long opponentId;
        public Long winnerId;
        public Long loserId;
        public String modified;

        public Player Player;
        public Opponent Opponent;

    }

    @ToString
    public static class Player {

        public Long id;
        public Integer drop;
        public Integer raceId;
        public Integer points;
        public PersonDto Person;
    }

    @ToString
    public static class Opponent {

        public Long id;
        public Integer drop;
        public Integer raceId;
        public Integer points;
        public PersonDto Person;
    }

    @ToString
    public static class PersonDto {

        public Long id;
        public String name;
        public String fullName;
        public String phone;
        public String code;
    }

}
