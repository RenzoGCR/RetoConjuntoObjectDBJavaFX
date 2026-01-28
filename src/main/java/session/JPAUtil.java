package session;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    // El nombre debe coincidir EXACTAMENTE con el <persistence-unit name="..."> de tu persistence.xml
    private static final String PERSISTENCE_UNIT_NAME = "objectdbPU";
    private static EntityManagerFactory factory;

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

    public static void shutdown() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}