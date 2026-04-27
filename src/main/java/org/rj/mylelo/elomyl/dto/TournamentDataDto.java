package org.rj.mylelo.elomyl.dto;

import java.util.List;
import lombok.ToString;

@ToString
public class TournamentDataDto {

    public Data data;

    @ToString
    public static class Data {

        public TournamentListV2 TournamentListV2;
    }

    @ToString
    public static class TournamentListV2 {

        public int total;
        public int pages;
        public List<Tournaments> Tournaments;
    }

    @ToString
    public static class Tournaments {

        public Long id;
        public String name;
        public String urlView;
        public String startDate;
        public Integer rounds;
        public Boolean isPresential;
        public Integer statusId;
        public Format Format;
        public TournamentType TournamentType;
        public Address Address;
        public Store Store;

    }

    @ToString
    public static class Format {

        public String name;
    }

    @ToString
    public static class TournamentType {

        public String name;
    }

    @ToString
    public static class Address {

        public Integer countryId;
        public String countryName;
    }
    
    public static class Store {
        public String urlView;
        public String name;
    }

}
