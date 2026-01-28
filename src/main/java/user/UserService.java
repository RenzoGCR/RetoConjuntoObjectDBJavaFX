package user;

import jakarta.persistence.EntityManager;
import copiaPelicula.CopiaPelicula;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import pelicula.Pelicula;
import session.JPAUtil;

import java.util.List;

public class UserService {

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

    public List<Pelicula> findAllPeliculas() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Pelicula p", Pelicula.class).getResultList();
        } finally {
            em.close();
        }
    }
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