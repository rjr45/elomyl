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
@Table(name = "player_elo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlayerElo {

    @Id
    @Column(name = "person_id")
    private Long personId;

    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "full_name", length = 200)
    private String fullName;

    @Column(name = "elo_current")
    private double eloCurrent;

    @Column(name = "elo_previous")
    private double eloPrevious;

    @Column(name = "elo_change")
    private double eloChange;

    @Column(name = "elo_peak")
    private double eloPeak;

    @Column(name = "matches_played")
    private int matchesPlayed;

    @Column(name = "wins")
    private int wins;

    @Column(name = "losses")
    private int losses;

    @Column(name = "draws")
    private int draws;

    @Column(name = "is_provisional")
    private boolean provisional;

    public PlayerElo(Long personId, String code, String fullName) {
        this.personId = personId;
        this.code = code;
        this.fullName = fullName;
        this.eloCurrent = 1000; 
        this.eloPrevious = 1000;
        this.eloChange = 0;
        this.eloPeak = 1000; 
        this.matchesPlayed = 0;
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
        this.provisional = true;
    }

}
