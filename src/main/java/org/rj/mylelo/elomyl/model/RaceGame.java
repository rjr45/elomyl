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
@Table(name = "race_game")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RaceGame {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "name")
    private String name;

}
