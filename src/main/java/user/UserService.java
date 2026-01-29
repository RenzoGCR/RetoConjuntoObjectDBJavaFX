package user;

import jakarta.persistence.EntityManager;
import copiaPelicula.CopiaPelicula;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import pelicula.Pelicula;
import session.JPAUtil;

import java.util.List;

/**
 * Servicio de negocio para la gestión de usuarios y operaciones relacionadas con películas.
 * <p>
 * Encapsula la lógica de negocio compleja que involucra transacciones, relaciones entre entidades
 * (Usuario, Película, Copia) y consultas específicas que van más allá de un simple CRUD.
 * </p>
 */
public class UserService {

    /**
     * Asigna una copia disponible de una película a un usuario (alquiler).
     * <p>
     * Verifica si el usuario ya tiene una copia asignada. Si no, busca una copia disponible
     * de la película solicitada y la asigna al usuario, cambiando su estado a "Alquilada".
     * Todo el proceso se realiza dentro de una transacción.
     * </p>
     *
     * @param actor    El usuario que realiza el alquiler.
     * @param pelicula La película que se desea alquilar.
     * @throws RuntimeException Si el usuario ya tiene un alquiler activo o no hay copias disponibles.
     */
    public void addPeliculaOrCopia(User actor, Pelicula pelicula) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            User persistentUser = em.find(User.class, actor.getId());
            if (persistentUser.getCopiaAsignada() != null) {
                throw new RuntimeException("El usuario ya tiene una copia asignada.");
            }

            // Buscamos una copia disponible para esa película
            List<CopiaPelicula> copiasDisponibles = em.createQuery(
                            "SELECT c FROM CopiaPelicula c WHERE c.pelicula = :p AND c.usuario IS NULL", CopiaPelicula.class)
                    .setParameter("p", pelicula)
                    .getResultList();

            if (copiasDisponibles.isEmpty()) {
                throw new RuntimeException("No hay copias disponibles.");
            }

            CopiaPelicula copia = copiasDisponibles.get(0);
            copia.setUsuario(persistentUser);
            copia.setEstado("Alquilada");

            em.merge(copia); // Actualizamos la copia vinculándola al usuario

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Recupera todas las películas disponibles en el catálogo.
     *
     * @return Una lista de todas las películas.
     */
    public List<Pelicula> findAllPeliculas() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Pelicula p", Pelicula.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene un usuario con sus dependencias cargadas (Copia asignada y Película asociada).
     * <p>
     * Utiliza {@code JOIN FETCH} para cargar ansiosamente (EAGER) las relaciones que normalmente son LAZY,
     * evitando excepciones de inicialización perezosa fuera de la sesión.
     * </p>
     *
     * @param userId El ID del usuario a buscar.
     * @return El usuario con sus datos completos, o {@code null} si no se encuentra.
     */
    public User getUserWithDependencies(Integer userId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // ObjectDB no soporta alias en JOIN FETCH de la misma forma que Hibernate.
            // La sintaxis correcta para ObjectDB es simplemente JOIN FETCH sin alias,
            // o si se necesita alias, usarlo con cuidado.
            // Simplificamos la consulta para evitar el error "Unexpected query token 'c'"
            
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u " +
                            "LEFT JOIN FETCH u.copiaAsignada " +
                            "LEFT JOIN FETCH u.copiaAsignada.pelicula " +
                            "WHERE u.id = :id", User.class);

            query.setParameter("id", userId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Elimina una película y gestiona sus copias asociadas.
     * <p>
     * Busca la película y la elimina. Debido a la configuración de cascada, las copias
     * asociadas también deberían ser gestionadas según se haya definido en la entidad.
     * </p>
     *
     * @param admin    El usuario administrador que realiza la acción (actualmente no se valida aquí, pero se pasa por contexto).
     * @param pelicula La película a eliminar.
     */
    public void removePeliculaOrCopia(User admin, Pelicula pelicula) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // Buscamos la película en la base de datos para que esté en estado 'Managed' [cite: 280, 334]
            Pelicula p = em.find(Pelicula.class, pelicula.getId());

            if (p != null) {
                // Si la película tiene copias, ObjectDB las gestionará según el CascadeType definido [cite: 502, 531]
                em.remove(p);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Autentica a un usuario en el sistema.
     *
     * @param username El nombre de usuario.
     * @param password La contraseña.
     * @return El usuario autenticado si las credenciales son correctas, o {@code null} si no.
     */
    public User login(String username, String password) {
        EntityManager em = session.JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Usamos una consulta simple sin JOIN FETCH para el login
            // Esto evita problemas si el usuario no tiene copias o si la estructura es compleja
            return em.createQuery("SELECT u FROM User u WHERE u.nombreUsuario = :user AND u.contraseña = :pass", User.class)
                    .setParameter("user", username)
                    .setParameter("pass", password)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Actualiza los datos de una película existente.
     *
     * @param p La película con los datos modificados.
     */
    public void updatePelicula(Pelicula p) {
        EntityManager em = session.JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(p); // 'merge' actualiza el objeto en la base de datos
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Guarda una nueva película en la base de datos.
     *
     * @param nuevaPelicula La nueva película a persistir.
     * @return La película guardada.
     */
    public Pelicula savePelicula(Pelicula nuevaPelicula) {
        EntityManager em = session.JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(nuevaPelicula); // ObjectDB guarda el objeto directamente
            em.getTransaction().commit();
            return nuevaPelicula;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

}