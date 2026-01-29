package session;

import user.User;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio simple para gestionar la sesión del usuario en la aplicación.
 * <p>
 * Almacena el usuario actualmente autenticado y permite compartir objetos entre diferentes
 * controladores o vistas de la aplicación (como una "mochila" de datos).
 * </p>
 */
public class SimpleSessionService {
    private static User activeUser;
    private static final Map<String, Object> sessionData = new HashMap<>();

    /**
     * Inicia sesión estableciendo el usuario activo.
     *
     * @param user El usuario que ha iniciado sesión.
     */
    public void login(User user) {
        activeUser = user;
    }

    /**
     * Obtiene el usuario actualmente autenticado.
     *
     * @return El usuario activo, o {@code null} si no hay sesión iniciada.
     */
    public User getActive() {
        return activeUser;
    }

    /**
     * Cierra la sesión actual.
     * <p>
     * Elimina el usuario activo y limpia todos los datos temporales almacenados en la sesión.
     * </p>
     */
    public void logout() {
        activeUser = null;
        sessionData.clear();
    }

    /**
     * Almacena un objeto en la sesión para ser recuperado posteriormente.
     *
     * @param key   La clave identificadora del objeto.
     * @param value El objeto a almacenar.
     */
    public void setObject(String key, Object value) {
        sessionData.put(key, value);
    }

    /**
     * Recupera un objeto almacenado en la sesión.
     *
     * @param key La clave identificadora del objeto.
     * @return El objeto almacenado, o {@code null} si no existe.
     */
    public Object getObject(String key) {
        return sessionData.get(key);
    }
}