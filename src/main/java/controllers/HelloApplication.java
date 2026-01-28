package controllers;

import jakarta.persistence.EntityManager;
import javafx.application.Application;
import javafx.stage.Stage;
import session.JPAUtil;
import utils.JavaFXUtil;
import java.io.IOException;

public class HelloApplication extends Application {

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

    @Override
    public void stop() {
        JPAUtil.shutdown();
        System.out.println("Conexión con ObjectDB cerrada.");
    }

    public static void main(String[] args) {
        launch();
    }
}