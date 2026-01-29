package session;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase de utilidad para gestionar la factoría de EntityManagers de JPA (EntityManagerFactory).
 * <p>
 * Implementa el patrón Singleton para asegurar que solo exista una instancia de {@link EntityManagerFactory}
 * durante el ciclo de vida de la aplicación, optimizando recursos.
 * Se encarga de inicializar la conexión con ObjectDB y proporcionar la factoría para crear EntityManagers.
 * </p>
 */
public class JPAUtil {

    // El nombre debe coincidir EXACTAMENTE con el <persistence-unit name="..."> de tu persistence.xml
    private static final String PERSISTENCE_UNIT_NAME = "objectdbPU";
    private static EntityManagerFactory factory;

    /**
     * Obtiene la instancia única de {@link EntityManagerFactory}.
     * <p>
     * Si la factoría no ha sido creada, la inicializa utilizando la configuración definida
     * en {@code persistence.xml} o mediante propiedades programáticas si es necesario.
     * </p>
     *
     * @return La instancia de {@link EntityManagerFactory}.
     * @throws RuntimeException Si ocurre un error crítico al crear la factoría.
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (factory == null) {
            try {
                // Intentamos pasar la URL explícitamente si el persistence.xml falla
                Map<String, String> properties = new HashMap<>();
                properties.put("javax.persistence.jdbc.url", "objectdb:db/ad.odb");
                properties.put("jakarta.persistence.jdbc.url", "objectdb:db/ad.odb");
                
                factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
                
                if (factory == null) {
                    throw new RuntimeException("Persistence.createEntityManagerFactory devolvió null. Revisa persistence.xml");
                }
            } catch (Exception e) {
                System.err.println("--- ERROR CRÍTICO AL INICIAR LA FACTORÍA ---");
                e.printStackTrace();
                throw new RuntimeException("No se pudo iniciar la base de datos", e);
            }
        }
        return factory;
    }

    /**
     * Cierra la {@link EntityManagerFactory} y libera los recursos asociados.
     * <p>
     * Debe llamarse al finalizar la aplicación para asegurar un cierre limpio de la conexión a la base de datos.
     * </p>
     */
    public static void shutdown() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}