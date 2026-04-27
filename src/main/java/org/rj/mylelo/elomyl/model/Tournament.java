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
@Table(name = "tournament")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Tournament {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 300)
    private String name;

    @Column(name = "format_name", length = 100)
    private String formatName;

    @Column(name = "store_name", length = 200)
    private String storeName;

    @Column(name = "rounds")
    private int rounds;

    @Column(name = "is_presential")
    private Boolean isPresential;

    @Column(name = "start_date", length = 50)
    private String startDate;

    @Column(name = "url_view", length = 500)
    private String urlView;

    @Column(name = "scraped")
    private boolean scraped = false;

    @Column(name = "url_round")
    private String urlRound;

    @Column(name = "url_standings")
    private String urlStandings;

    @Column(name = "format_id")
    private Integer formatId;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "is_private")
    private Integer isPrivate;

    @Column(name = "max_players")
    private Integer maxPlayers;

    @Column(name = "ranking_valid")
    private Boolean rankingValid;

    @Column(name = "season_id")
    private Integer seasonId;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "status_id")
    private Integer status;

}
