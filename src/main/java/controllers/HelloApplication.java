package controllers;

import jakarta.persistence.EntityManager;
import javafx.application.Application;
import javafx.stage.Stage;
import session.JPAUtil;
import utils.JavaFXUtil;
import java.io.IOException;

/**
 * Clase principal de la aplicación JavaFX que extiende de {@link Application}.
 * <p>
 * Esta clase se encarga de iniciar la aplicación, configurar el escenario principal (Stage),
 * cargar la vista inicial (Login) y gestionar el ciclo de vida de la aplicación,
 * incluyendo la inicialización de datos de prueba y el cierre de conexiones.
 * </p>
 */
public class HelloApplication extends Application {

    /**
     * Método de inicio de la aplicación JavaFX.
     * <p>
     * Se ejecuta al lanzar la aplicación. Realiza las siguientes tareas:
     * <ol>
     *     <li>Llama a {@link #seedData()} para verificar y poblar la base de datos si es necesario.</li>
     *     <li>Inicializa la utilidad {@link JavaFXUtil} con el escenario principal.</li>
     *     <li>Carga y muestra la vista de inicio de sesión (login-view.fxml).</li>
     * </ol>
     * </p>
     *
     * @param stage El escenario principal (Stage) proporcionado por el runtime de JavaFX.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // 1. Inicializamos los datos antes de cargar la interfaz
        seedData();

        // 2. Configuramos JavaFX
        JavaFXUtil.initStage(stage);

        // Importante: Asegúrate de que la ruta al FXML sea la correcta
        // Si moviste los archivos a la raíz de resources, sería "/login-view.fxml"
        JavaFXUtil.setScene("/login-view.fxml");
    }

    /**
     * Método auxiliar para poblar la base de datos con datos iniciales de prueba.
     * <p>
     * Verifica si existen usuarios en la base de datos. Si la base de datos está vacía (0 usuarios),
     * crea e inserta:
     * <ul>
     *     <li>Un usuario administrador (admin1 / root).</li>
     *     <li>Un usuario normal (user1 / 1234).</li>
     *     <li>Una película de ejemplo ("Inception").</li>
     *     <li>Una copia disponible para dicha película.</li>
     * </ul>
     * Utiliza una transacción JPA para asegurar la atomicidad de la operación.
     * </p>
     */
    private void seedData() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Comprobamos si ya existen usuarios
            long userCount = em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();

            // Si no hay usuarios, o si queremos forzar la creación de datos (para depuración)
            // Nota: Si ya existen datos, esto podría duplicarlos si no se controla bien,
            // pero como userCount == 0 es la condición, solo entra si está vacía.
            if (userCount == 0) {
                System.out.println("Base de datos vacía. Generando datos de prueba...");
                em.getTransaction().begin();

                // Crear Usuario Admin
                user.User admin = new user.User();
                admin.setNombreUsuario("admin1");
                admin.setContraseña("root");
                admin.setAdmin(true);
                em.persist(admin);

                // 2. Crear Usuario NORMAL (No es admin)
                user.User normalUser = new user.User();
                normalUser.setNombreUsuario("user1");
                normalUser.setContraseña("1234");
                normalUser.setAdmin(false); // <--- Importante: false
                em.persist(normalUser);

                // Crear una Película de ejemplo
                pelicula.Pelicula p1 = new pelicula.Pelicula();
                p1.setTitulo("Inception");
                p1.setGenero("Ciencia Ficción");
                p1.setDirector("Christopher Nolan");
                p1.setAño(2010);
                p1.setDescripcion("Un ladrón que roba secretos...");
                em.persist(p1);

                // Crear una copia disponible para esa película
                copiaPelicula.CopiaPelicula c1 = new copiaPelicula.CopiaPelicula();
                c1.setPelicula(p1);
                c1.setEstado("Disponible");
                c1.setSoporte("DVD");
                em.persist(c1);

                em.getTransaction().commit();
                System.out.println(">>> Usuarios creados:");
                System.out.println("  - Admin: admin1 / root");
                System.out.println("  - Normal: user1 / 1234");
            } else {
                System.out.println("La base de datos ya contiene " + userCount + " usuarios. No se generaron datos de prueba.");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Método llamado automáticamente cuando la aplicación se detiene.
     * <p>
     * Se encarga de cerrar la factoría de EntityManagers de JPA (ObjectDB)
     * para liberar los recursos de la base de datos de forma ordenada.
     * </p>
     */
    @Override
    public void stop() {
        JPAUtil.shutdown();
        System.out.println("Conexión con ObjectDB cerrada.");
    }

    /**
     * Punto de entrada principal de la aplicación Java.
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        launch();
    }
}