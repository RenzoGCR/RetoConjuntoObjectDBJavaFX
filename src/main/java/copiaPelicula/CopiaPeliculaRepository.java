package copiaPelicula;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import session.JPAUtil;
import utils.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link CopiaPelicula}.
 * <p>
 * Implementa la interfaz {@link Repository} para proporcionar operaciones CRUD
 * (Crear, Leer, Actualizar, Borrar) sobre las copias de películas en la base de datos ObjectDB.
 * </p>
 */
public class CopiaPeliculaRepository implements Repository<CopiaPelicula> {

    /**
     * Guarda o actualiza una copia de película en la base de datos.
     * <p>
     * Si la entidad no tiene ID, se persiste como nueva. Si ya tiene ID, se actualiza (merge).
     * </p>
     *
     * @param entity La entidad {@link CopiaPelicula} a guardar.
     * @return La entidad guardada o actualizada.
     */
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

    /**
     * Busca una copia de película por su identificador.
     *
     * @param id El ID de la copia a buscar.
     * @return Un {@link Optional} que contiene la copia si se encuentra, o vacío si no.
     */
    @Override
    public Optional<CopiaPelicula> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(CopiaPelicula.class, id.intValue()));
        } finally {
            em.close();
        }
    }

    /**
     * Recupera todas las copias de películas existentes en la base de datos.
     *
     * @return Una lista con todas las copias encontradas.
     */
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

    /**
     * Elimina una copia de película de la base de datos.
     *
     * @param entity La entidad {@link CopiaPelicula} a eliminar.
     * @return Un {@link Optional} con la entidad eliminada si la operación fue exitosa.
     */
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

    /**
     * Elimina una copia de película por su identificador.
     *
     * @param id El ID de la copia a eliminar.
     * @return Un {@link Optional} con la entidad eliminada si se encontró y borró correctamente.
     */
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

    /**
     * Cuenta el número total de copias de películas en la base de datos.
     *
     * @return El número total de copias.
     */
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