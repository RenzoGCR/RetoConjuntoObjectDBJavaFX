package pelicula;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import session.JPAUtil;
import utils.Repository;


import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Pelicula adaptado a ObjectDB.
 * Utiliza EntityManager para interactuar con la base de datos de objetos.
 */
public class PeliculaRepository implements Repository<Pelicula> {

    /**
     * Guarda o actualiza una película.
     * En JPA, persist() se usa para nuevos y merge() para existentes[cite: 279, 281].
     */
    @Override
    public Pelicula save(Pelicula entity) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            // Si el objeto ya existe (tiene ID), usamos merge, si no, persist [cite: 367]
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
    public Optional<Pelicula> delete(Pelicula entity) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            // En JPA, el objeto debe estar en estado 'Managed' para ser eliminado [cite: 334, 351]
            Pelicula managedEntity = em.merge(entity);
            em.remove(managedEntity);
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
    public Optional<Pelicula> deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Pelicula pelicula = em.find(Pelicula.class, id.intValue()); // ObjectDB usa int/long según tu @Id
            if (pelicula != null) {
                em.remove(pelicula);
                em.getTransaction().commit();
                return Optional.of(pelicula);
            }
            return Optional.empty();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Pelicula> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // find() devuelve el objeto directamente por su clave primaria [cite: 280]
            return Optional.ofNullable(em.find(Pelicula.class, id.intValue()));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Pelicula> findAll() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Usamos TypedQuery para mayor seguridad de tipos en JPQL [cite: 298, 304]
            TypedQuery<Pelicula> query = em.createQuery("SELECT p FROM Pelicula p", Pelicula.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Long count() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Las funciones agregadas como COUNT funcionan igual en JPQL [cite: 614, 650]
            return em.createQuery("SELECT COUNT(p) FROM Pelicula p", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}