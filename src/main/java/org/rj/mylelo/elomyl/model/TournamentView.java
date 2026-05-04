package org.rj.mylelo.elomyl.model;

import com.google.errorprone.annotations.Immutable;
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
@Table(name = "tournament_view")
@Immutable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TournamentView {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "tournament_id")
    private Long tournamentId;

    @Column(name = "tournament_date")
    private String tournamentDate;

    @Column(name = "player_person_id")
    private Long playerPersonId;

    @Column(name = "player_wins")
    private Integer playerWins;

    @Column(name = "opponent_person_id")
    private Long opponentPersonId;

    @Column(name = "opponent_wins")
    private Integer opponentWins;

    @Column(name = "draw")
    private Integer draw;

    @Column(name = "winner_id")
    private Long winnerId;

    @Column(name = "loser_id")
    private Long loserId;

    @Column(name = "player_code")
    private String playerCode;

    @Column(name = "opponent_code")
    private String opponentCode;

    @Column(name = "url")
    private String url;
}
