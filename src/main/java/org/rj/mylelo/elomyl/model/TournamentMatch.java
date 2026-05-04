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
@Table(name = "tournament_match")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TournamentMatch {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;

    @Column(name = "round_id", nullable = false)
    private Long roundId;

    @Column(name = "table_num")
    private int tableNum;

    @Column(name = "reported")
    private Integer reported;

    @Column(name = "draw")
    private Integer draw;

    @Column(name = "player_id")
    private Long playerId;

    @Column(name = "opponent_id")
    private Long opponentId;

    @Column(name = "player_person_id")
    private Long playerPersonId;

    @Column(name = "opponent_person_id")
    private Long opponentPersonId;

    @Column(name = "winner_id")
    private Long winnerId;

    @Column(name = "loser_id")
    private Long loserId;

    @Column(name = "winner_victories")
    private Integer winnerVictories;

    @Column(name = "loser_victories")
    private Integer loserVictories;

    @Column(name = "drop_player")
    private Integer dropPlayer;

    @Column(name = "drop_opponent")
    private Integer dropOpponent;

    @Column(name = "modified", length = 50)
    private String modified;
    
    @Column(name = "elo_calculated")
    private Integer eloCalculated;

}
