package org.rj.mylelo.elomyl.dto;

import java.util.List;
import lombok.ToString;

@ToString
public class TournamentInfoDto {

    public Data data;

    @ToString
    public static class Data {

        public TournamentInfo TournamentInfo;
    }

    @ToString
    public static class TournamentInfo {

        public Tournament Tournament;
    }

    @ToString
    public static class Tournament {

        public Long id;
        public String description;
        public String eventUrl;
        public Integer gameId;
        public Integer formatId;
        public Integer statusId;
        public Long storeId;
        public Integer typeId;
        public Integer seasonId;
        public Integer maxPlayers;
        public String name;
        public Boolean rankingValid;
        public Boolean isPresential;
        public Integer rounds;
        public String startDate;
        public String urlRounds;
        public String urlStandings;
        public String urlView;
        public Store Store;
        public Long addressId;
        public Address Address;
        public Season Season;
        public Game Game;
        public TournamentType TournamentType;
        public Format Format;
    }

    @ToString
    public static class Store {

        public Long id;
        public String name;
        public String description;
        public String phone;
        public Integer levelId;
        public Integer active;
        public String email;
        public String siteUrl;

    }

    @ToString
    public static class Address {

        public String name;
        public String address;
        public Integer countryId;
        public Integer regionId;
        public Integer locationId;
        public String Region;
        public String Location;
    }

    @ToString
    public static class Season {

        public String name;
        public String description;
        public Integer isArchived;
        public Integer isActive;
        public String rankingUrl;
    }

    @ToString
    public static class Game {

        public Long id;
        public String name;
        public List<RaceGames> RaceGames;
    }

    @ToString
    public static class RaceGames {

        public Long raceId;
        public Integer sortOrder;
        public Race Race;
    }

    @ToString
    public static class Race {

        public String name;
    }

    @ToString
    public static class TournamentType {

        public String name;
        public String description;
        public Integer winnerPoints;
        public Integer minPlayers;
        public Integer maxTopPlayers;
        public Integer loserPoints;
        public Integer drawPoints;
        public Integer multiplier;
        public Integer status;
    }

    @ToString
    public static class Format {

        public Integer id;
        public String name;
        public String description;
    }

}
