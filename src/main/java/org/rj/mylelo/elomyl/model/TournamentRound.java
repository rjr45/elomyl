package org.rj.mylelo.elomyl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tournament_round")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TournamentRound {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "status_id")
    private int statusId;

    @Column(name = "total_matches")
    private int totalMatches;
    
    @Column(name = "total_match_pending")
    private int totalMatchPendings;

    @Column(name = "minutes")
    private int minutes;

    @Column(name = "created_at", length = 50)
    private String createdAt;
    
    @Column(name = "url_view")
    private String urlView;
    
    @Column(name = "url_standing")
    private String urlStanding;
    
    
}
