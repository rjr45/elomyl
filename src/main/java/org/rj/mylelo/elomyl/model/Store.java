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
@Table(name = "store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Store {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "phone")
    private String phone;

    @Column(name = "level_id")
    private Integer levelId;

    @Column(name = "active")
    private Integer active;

    @Column(name = "email")
    private String email;

    @Column(name = "site_url")
    private String siteUrl;

    @Column(name = "url_view")
    private String urlView;
    
    @Column(name = "region")
    private String region;
    
    @Column(name = "country_id")
    private Integer countryId;

}
