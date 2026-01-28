package user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import session.JPAUtil;
import utils.Repository;

import java.util.List;
import java.util.Optional;

public class UserRepository implements Repository<User> {

    @Override
    public User save(User entity) {
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
    public Optional<User> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(User.class, id.intValue()));
        } finally {
            em.close();
        }
    }

    public Optional<User> findByNombreUsuario(String nombreUsuario) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<User> q = em.createQuery(
                    "SELECT u FROM User u WHERE u.nombreUsuario = :nombre", User.class);
            q.setParameter("nombre", nombreUsuario);
            return q.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> findAll() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<User> delete(User entity) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            User managed = em.merge(entity);
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
    public Optional<User> deleteById(Long id) {
        return findById(id).flatMap(this::delete);
    }

    @Override
    public Long count() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}