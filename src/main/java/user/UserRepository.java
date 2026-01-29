package user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import session.JPAUtil;
import utils.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link User}.
 * <p>
 * Proporciona operaciones de acceso a datos para gestionar usuarios, incluyendo
 * operaciones CRUD básicas y búsquedas específicas como buscar por nombre de usuario.
 * </p>
 */
public class UserRepository implements Repository<User> {

    /**
     * Guarda o actualiza un usuario en la base de datos.
     *
     * @param entity El usuario a guardar.
     * @return El usuario persistido.
     */
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

    /**
     * Busca un usuario por su ID.
     *
     * @param id El identificador del usuario.
     * @return Un {@link Optional} con el usuario encontrado.
     */
    @Override
    public Optional<User> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(User.class, id.intValue()));
        } finally {
            em.close();
        }
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param nombreUsuario El nombre de usuario a buscar.
     * @return Un {@link Optional} con el usuario si existe.
     */
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

    /**
     * Recupera todos los usuarios registrados.
     *
     * @return Una lista de todos los usuarios.
     */
    @Override
    public List<User> findAll() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param entity El usuario a eliminar.
     * @return Un {@link Optional} con el usuario eliminado si tuvo éxito.
     */
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

    /**
     * Elimina un usuario por su ID.
     *
     * @param id El identificador del usuario a eliminar.
     * @return Un {@link Optional} con el usuario eliminado.
     */
    @Override
    public Optional<User> deleteById(Long id) {
        return findById(id).flatMap(this::delete);
    }

    /**
     * Cuenta el número total de usuarios.
     *
     * @return El total de usuarios.
     */
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