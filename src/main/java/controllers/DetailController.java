package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import pelicula.Pelicula;
import session.SimpleSessionService;
import user.User;
import utils.JavaFXUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de detalles de una película.
 * <p>
 * Esta clase gestiona la visualización de la información detallada de una película seleccionada,
 * incluyendo título, género, director, año y descripción.
 * También controla la visibilidad del botón de edición según los permisos del usuario (administrador).
 * </p>
 */
public class DetailController implements Initializable {
    @FXML private Button btnVolver;
    @FXML private Button btnEditar;
    @FXML private Label lblGenero;
    @FXML private TextArea taDescripcion;
    @FXML private Label lblTitulo;
    @FXML private Label lblAño;
    @FXML private Label lblDirector;

    private final SimpleSessionService sessionService = new SimpleSessionService();
    private Pelicula peliculaActual;

    /**
     * Inicializa el controlador.
     * <p>
     * Recupera la película seleccionada de la sesión y el usuario actual.
     * Configura la visibilidad del botón de edición (solo visible para administradores)
     * y carga los datos de la película en los componentes de la interfaz.
     * </p>
     *
     * @param url            La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si no se localizó.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Recuperamos la película que guardamos en la sesión en el MainController
        peliculaActual = (Pelicula) sessionService.getObject("pelicula_seleccionada");
        User currentUser = sessionService.getActive();

        // Control de visibilidad del botón editar según rol
        if (currentUser != null && currentUser.isAdmin()) {
            btnEditar.setVisible(true);
        } else {
            btnEditar.setVisible(false);
        }

        cargarDatosPelicula(peliculaActual);
    }

    /**
     * Carga los datos de la película en los elementos de la interfaz gráfica.
     *
     * @param pelicula La película cuyos datos se van a mostrar.
     */
    private void cargarDatosPelicula(Pelicula pelicula) {
        if (pelicula != null) {
            lblTitulo.setText(pelicula.getTitulo());
            lblGenero.setText(pelicula.getGenero());
            lblDirector.setText(pelicula.getDirector());
            lblAño.setText(String.valueOf(pelicula.getAño()));
            taDescripcion.setText(pelicula.getDescripcion());
            taDescripcion.setEditable(false);
            taDescripcion.setWrapText(true);
        }
    }

    /**
     * Maneja el evento de clic en el botón "Editar".
     * <p>
     * Redirige al usuario a la vista de edición de la película actual.
     * </p>
     *
     * @param event El evento de acción generado por el botón.
     */
    @FXML
    void editarPelicula(ActionEvent event) {
        // Ruta simplificada al FXML
        JavaFXUtil.setScene("/editar-view.fxml");
    }

    /**
     * Maneja el evento de clic en el botón "Volver".
     * <p>
     * Limpia la selección de película en la sesión y redirige al usuario a la vista principal.
     * </p>
     *
     * @param actionEvent El evento de acción generado por el botón.
     */
    @FXML
    public void volver(ActionEvent actionEvent) {
        // Limpiamos la selección al volver
        sessionService.setObject("pelicula_seleccionada", null);
        JavaFXUtil.setScene("/main-view.fxml");
    }
}