package utils;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para el patrón Repository[cite: 395].
 * @param <T> Tipo de la entidad (Pelicula, User, etc.)
 */
public interface Repository<T> {
    Optional<T> findById(Long id);
    List<T> findAll();
    T save(T entity);
    Optional<T> delete(T entity);
    Optional<T> deleteById(Long id);
    Long count();
}