package org.rj.mylelo.elomyl.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.rj.mylelo.elomyl.model.Tournament;
import org.rj.mylelo.elomyl.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.rj.mylelo.elomyl.model.TournamentView;

@Slf4j
public class GenericDao {

    public static void executeInTransaction(Consumer<Session> action) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            action.accept(session);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    public static <T> void updateEntity(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            log.error(ex.getMessage());
            throw new RuntimeException("updateEntity error", ex);
        }
    }

    public static <T> void updateEntity(List<T> entities) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            for (int i = 0; i < entities.size(); i++) {
                session.merge(entities.get(i));

                if (i % 50 == 0) {
                    session.flush();
                    session.clear();
                }
            }

            tx.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
            log.error(ex.getMessage());
            throw new RuntimeException("updateEntity error", ex);
        }
    }

    public static List<TournamentView> getAllTournamentsView() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM TournamentView WHERE elo_calculated <> 1 OR elo_calculated IS NULL", TournamentView.class).list();
        }
    }

    //de aqui pa abajo se encargo el compare chatgpt, quedo como el pico, mañana lo arreglo, 2026-04-23
    public static <T> int insertNewOnly(List<T> entities, List<Long> ids) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }

        List<Long> existing = getExistingIds(ids, entities.get(0).getClass());

        List<T> toInsert = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            if (!existing.contains(ids.get(i))) {
                toInsert.add(entities.get(i));
            }
        }

        if (toInsert.isEmpty()) {
            return 0;
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            int n = 0;
            for (T entity : toInsert) {
                session.persist(entity);
                if (++n % 50 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            tx.commit();
            return n;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new RuntimeException("insertNewOnly error: " + e.getMessage(), e);
        }
    }

    public static void insertIfNotExists(Object entity, Long id) {
        List<Long> existing = getExistingIds(List.of(id), entity.getClass());
        if (!existing.isEmpty()) {
            return;
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new RuntimeException("insertIfNotExists error: " + e.getMessage(), e);
        }
    }

    public static boolean allTournamentsExist(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(t.id) FROM Tournament t WHERE t.id IN :ids", Long.class
            ).setParameter("ids", ids).uniqueResult();
            return count != null && count == ids.size();
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Long> getExistingIds(List<Long> ids, Class<?> entityClass) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT e.id FROM " + entityClass.getSimpleName() + " e WHERE e.id IN :ids",
                    Long.class
            ).setParameter("ids", ids).list();
        }
    }

    public static List<Tournament> getPendingTournaments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Tournament t WHERE t.scraped = false ORDER BY t.id",
                    Tournament.class
            ).list();
        }
    }
}
