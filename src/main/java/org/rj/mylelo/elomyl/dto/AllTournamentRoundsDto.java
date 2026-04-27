package org.rj.mylelo.elomyl.dto;

import java.util.List;
import lombok.ToString;

@ToString
public class AllTournamentRoundsDto {

    public Data data;

    @ToString
    public static class Data {

        public List<AllTournamentRounds> allTournamentRounds;
    }

    @ToString
    public static class AllTournamentRounds {

        public Long id;
        public String name;
        public String description;
        public Integer sortOrder;
        public String urlView;
        public String urlStanding;
        public Long tournamentId;
        public Integer minutes;
        public Integer statusId;
        public Integer totalMatches;
        public Integer totalMatchPendings;
        public String created;
    }

}
