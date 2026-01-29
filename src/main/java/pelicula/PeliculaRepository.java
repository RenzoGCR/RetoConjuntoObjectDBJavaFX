package pelicula;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import session.JPAUtil;
import utils.Repository;


import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link Pelicula}.
 * <p>
 * Proporciona acceso a datos para objetos Pelicula utilizando JPA y ObjectDB.
 * Implementa operaciones CRUD estándar definidas en la interfaz {@link Repository}.
 * </p>
 */
public class PeliculaRepository implements Repository<Pelicula> {

    /**
     * Guarda o actualiza una película en la base de datos.
     * <p>
     * Utiliza {@code persist} para nuevas entidades (ID nulo) y {@code merge} para entidades existentes.
     * </p>
     *
     * @param entity La película a guardar.
     * @return La película persistida.
     */
    @Override
    public Pelicula save(Pelicula entity) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            // Si el objeto ya existe (tiene ID), usamos merge, si no, persist
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

    /**
     * Elimina una película de la base de datos.
     * <p>
     * Primero adjunta la entidad al contexto de persistencia usando {@code merge} y luego la elimina.
     * </p>
     *
     * @param entity La película a eliminar.
     * @return Un {@link Optional} con la película eliminada si tuvo éxito.
     */
    @Override
    public Optional<Pelicula> delete(Pelicula entity) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            // En JPA, el objeto debe estar en estado 'Managed' para ser eliminado
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

    /**
     * Elimina una película por su ID.
     *
     * @param id El identificador de la película a eliminar.
     * @return Un {@link Optional} con la película eliminada si se encontró.
     */
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

    /**
     * Busca una película por su ID.
     *
     * @param id El identificador de la película.
     * @return Un {@link Optional} con la película encontrada.
     */
    @Override
    public Optional<Pelicula> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // find() devuelve el objeto directamente por su clave primaria
            return Optional.ofNullable(em.find(Pelicula.class, id.intValue()));
        } finally {
            em.close();
        }
    }

    /**
     * Recupera todas las películas almacenadas.
     *
     * @return Una lista de todas las películas.
     */
    @Override
    public List<Pelicula> findAll() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Usamos TypedQuery para mayor seguridad de tipos en JPQL
            TypedQuery<Pelicula> query = em.createQuery("SELECT p FROM Pelicula p", Pelicula.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Cuenta el número total de películas.
     *
     * @return El total de películas en la base de datos.
     */
    @Override
    public Long count() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Las funciones agregadas como COUNT funcionan igual en JPQL
            return em.createQuery("SELECT COUNT(p) FROM Pelicula p", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}