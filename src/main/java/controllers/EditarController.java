package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import pelicula.Pelicula;
import session.SimpleSessionService;
import user.UserService;
import utils.JavaFXUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de edición de películas.
 * <p>
 * Permite a los usuarios administradores modificar los detalles de una película existente,
 * como título, género, director, año y descripción.
 * </p>
 */
public class EditarController implements Initializable {

    @FXML private TextField tfTitulo;
    @FXML private TextField tfGenero;
    @FXML private TextField tfDirector;
    @FXML private TextField tfAño;
    @FXML private TextArea taDescripcion;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final SimpleSessionService sessionService = new SimpleSessionService();
    private final UserService userService = new UserService();
    private Pelicula peliculaAEditar;

    /**
     * Inicializa el controlador.
     * <p>
     * Recupera la película seleccionada de la sesión y rellena los campos del formulario
     * con sus datos actuales para ser editados.
     * </p>
     *
     * @param url            La ubicación utilizada para resolver rutas relativas.
     * @param resourceBundle Los recursos utilizados para la localización.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Recuperamos la película que el DetailController dejó en la sesión
        peliculaAEditar = (Pelicula) sessionService.getObject("pelicula_seleccionada");

        if (peliculaAEditar != null) {
            rellenarCampos();
        }
    }

    /**
     * Rellena los campos de texto de la interfaz con los datos de la película a editar.
     */
    private void rellenarCampos() {
        tfTitulo.setText(peliculaAEditar.getTitulo());
        tfGenero.setText(peliculaAEditar.getGenero());
        tfDirector.setText(peliculaAEditar.getDirector());
        tfAño.setText(String.valueOf(peliculaAEditar.getAño()));
        taDescripcion.setText(peliculaAEditar.getDescripcion());
    }

    /**
     * Guarda los cambios realizados en la película.
     * <p>
     * Valida los datos introducidos, actualiza el objeto película y lo persiste en la base de datos
     * a través del {@link UserService}. Muestra alertas de éxito o error según el resultado.
     * </p>
     *
     * @param event El evento de acción generado por el botón "Guardar".
     */
    @FXML
    void guardarCambios(ActionEvent event) {
        try {
            // Actualizamos el objeto con los nuevos datos de los campos
            peliculaAEditar.setTitulo(tfTitulo.getText());
            peliculaAEditar.setGenero(tfGenero.getText());
            peliculaAEditar.setDirector(tfDirector.getText());
            peliculaAEditar.setAño(Integer.parseInt(tfAño.getText()));
            peliculaAEditar.setDescripcion(taDescripcion.getText());

            // Llamamos al servicio (asegúrate de que UserService tenga el método updatePelicula)
            userService.updatePelicula(peliculaAEditar);

            JavaFXUtil.showModal(Alert.AlertType.INFORMATION, "Éxito", "Película actualizada", "Los cambios se han guardado correctamente.");

            volver(null);

        } catch (NumberFormatException e) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error de formato", "Campo 'Año' inválido", "El año debe ser un número válido.");
        } catch (Exception e) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "Error al guardar", "No se pudieron guardar los cambios.");
            e.printStackTrace();
        }
    }

    /**
     * Cancela la edición y vuelve a la vista principal.
     * <p>
     * Limpia la selección de película en la sesión antes de navegar.
     * </p>
     *
     * @param event El evento de acción generado por el botón "Cancelar" o tras guardar.
     */
    @FXML
    void volver(ActionEvent event) {
        // Limpiamos la película seleccionada para evitar conflictos futuros
        sessionService.setObject("pelicula_seleccionada", null);
        // Ruta corregida a la raíz de resources
        JavaFXUtil.setScene("/main-view.fxml");
    }
}