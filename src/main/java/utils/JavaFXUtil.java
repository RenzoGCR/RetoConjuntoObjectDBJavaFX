package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Clase de utilidad para operaciones comunes de JavaFX.
 * Gestiona el escenario (Stage) principal de la aplicación, permitiendo cambiar
 * de escena y mostrar ventanas modales de forma centralizada.
 */
public class JavaFXUtil {

    private static Stage stage;

    /**
     * Constructor privado para prevenir la instanciación.
     */
    private JavaFXUtil() {}

    /**
     * Inicializa la utilidad con el escenario principal de la aplicación.
     * Este método debe ser llamado en el método {@code start} de la clase Application.
     *
     * @param stage El escenario principal (Stage) proporcionado por JavaFX.
     */
    public static void initStage(Stage stage) {
        JavaFXUtil.stage = stage;
    }

    /**
     * Obtiene la instancia del escenario principal.
     *
     * @return El escenario (Stage) principal de la aplicación.
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * Carga un archivo FXML y lo establece como la nueva escena en el escenario principal.
     * Aplica una hoja de estilos global a cada escena cargada.
     *
     * @param fxml La ruta al archivo FXML, relativa al classpath.
     * @param <T>  El tipo del controlador asociado al FXML.
     * @return El controlador de la nueva escena, o {@code null} si ocurre un error de carga.
     */
    public static <T> T setScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXUtil.class.getResource(fxml));
            Parent root = loader.load();
            T controller = loader.getController();
            Scene scene = new Scene(root);

            // Añadir la hoja de estilos global a la escena
            URL css = JavaFXUtil.class.getResource("/org/example/retoconjuntojavafxhibernate/style.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            } else {
                System.err.println("No se encontró el archivo de estilos: style.css");
            }

            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
            return controller;
        } catch (IOException ex) {
            System.err.println("Error al cargar el archivo FXML: " + fxml);
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Muestra una ventana de alerta modal.
     * La ventana se mostrará como hija del escenario principal.
     *
     * @param type    El tipo de alerta (p. ej., Alert.AlertType.INFORMATION, Alert.AlertType.ERROR).
     * @param title   El título de la ventana de alerta.
     * @param header  El texto de la cabecera de la alerta (puede ser null).
     * @param content El mensaje principal de la alerta.
     */
    public static void showModal(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(stage);
        alert.showAndWait();
    }
}
