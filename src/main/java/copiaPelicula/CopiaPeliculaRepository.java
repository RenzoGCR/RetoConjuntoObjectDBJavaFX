package copiaPelicula;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import session.JPAUtil;
import utils.Repository;

import java.util.List;
import java.util.Optional;

public class CopiaPeliculaRepository implements Repository<CopiaPelicula> {

    @Override
    public CopiaPelicula save(CopiaPelicula entity) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            if (entity.getId() == null) {
                em.persist(entity);
            } else {
                entity = em.merge(entity);
            }
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<CopiaPelicula> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(CopiaPelicula.class, id.intValue()));
        } finally {
            em.close();
        }
    }

    @Override
    public List<CopiaPelicula> findAll() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<CopiaPelicula> query = em.createQuery("SELECT c FROM CopiaPelicula c", CopiaPelicula.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<CopiaPelicula> delete(CopiaPelicula entity) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            CopiaPelicula managed = em.merge(entity);
            em.remove(managed);
            em.getTransaction().commit();
            return Optional.of(entity);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<CopiaPelicula> deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            CopiaPelicula c = em.find(CopiaPelicula.class, id.intValue());
            if (c != null) {
                em.remove(c);
                em.getTransaction().commit();
                return Optional.of(c);
            }
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Long count() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(c) FROM CopiaPelicula c", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}
