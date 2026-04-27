package org.rj.mylelo.elomyl.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.rj.mylelo.elomyl.model.Game;
import org.rj.mylelo.elomyl.model.Person;
import org.rj.mylelo.elomyl.model.PlayerElo;
import org.rj.mylelo.elomyl.model.RaceGame;
import org.rj.mylelo.elomyl.model.Store;
import org.rj.mylelo.elomyl.model.Tournament;
import org.rj.mylelo.elomyl.model.TournamentMatch;
import org.rj.mylelo.elomyl.model.TournamentPlayer;
import org.rj.mylelo.elomyl.model.TournamentRound;
import org.rj.mylelo.elomyl.model.TournamentType;

public class HibernateUtil {

    private static StandardServiceRegistry standardServiceRegistry;
    private static SessionFactory sessionFactory;

    static {
        if (sessionFactory == null) {
            try {
                standardServiceRegistry = new StandardServiceRegistryBuilder()
                        .build();

                MetadataSources metadataSources = new MetadataSources(standardServiceRegistry);
                metadataSources.addAnnotatedClass(Person.class);
                metadataSources.addAnnotatedClass(Tournament.class);
                metadataSources.addAnnotatedClass(TournamentMatch.class);
                metadataSources.addAnnotatedClass(TournamentPlayer.class);
                metadataSources.addAnnotatedClass(TournamentRound.class);
                metadataSources.addAnnotatedClass(PlayerElo.class);
                metadataSources.addAnnotatedClass(Game.class);
                metadataSources.addAnnotatedClass(RaceGame.class);
                metadataSources.addAnnotatedClass(Store.class);
                metadataSources.addAnnotatedClass(TournamentType.class);

                Metadata metadata = metadataSources.getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (Exception e) {
                if (standardServiceRegistry != null) {
                    StandardServiceRegistryBuilder.destroy(standardServiceRegistry);
                }
            }
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
