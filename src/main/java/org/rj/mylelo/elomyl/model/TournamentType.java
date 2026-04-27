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
@Table(name = "tournament_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TournamentType {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "winner_points")
    private Integer winnerPoints;

    @Column(name = "min_players")
    private Integer minPlayers;

    @Column(name = "max_top_players")
    private Integer maxTopPlayers;

    @Column(name = "loser_points")
    private Integer loserPoints;

    @Column(name = "draw_points")
    private Integer drawPoints;

    @Column(name = "multiplier")
    private Integer multiplier;

    @Column(name = "status")
    private Integer status;
}
