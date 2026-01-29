package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import pelicula.Pelicula;
import user.UserService;
import utils.JavaFXUtil;

/**
 * Controlador para el formulario de creación de nuevas películas.
 * <p>
 * Permite a los usuarios administradores introducir los datos de una nueva película
 * y guardarla en la base de datos.
 * </p>
 */
public class NewFilmFormController {

    @FXML private TextField tfTitulo;
    @FXML private TextArea taDescripcion;
    @FXML private Button btnCancelar;
    @FXML private TextField tfGenero;
    @FXML private Button btnAgregar;
    @FXML private TextField tfDirector;
    @FXML private TextField tfAño;

    private final UserService userService = new UserService();

    /**
     * Cancela la operación de creación y vuelve a la vista principal.
     *
     * @param actionEvent El evento de acción generado por el botón "Cancelar".
     */
    @FXML
    public void cancelar(ActionEvent actionEvent) {
        // Navegación con ruta simplificada
        JavaFXUtil.setScene("/main-view.fxml");
    }

    /**
     * Procesa la creación de una nueva película.
     * <p>
     * Valida que todos los campos obligatorios estén rellenos y que el año sea un número válido.
     * Si la validación es correcta, crea un nuevo objeto {@link Pelicula} y lo persiste
     * utilizando {@link UserService}.
     * </p>
     *
     * @param actionEvent El evento de acción generado por el botón "Agregar".
     */
    @FXML
    public void agregar(ActionEvent actionEvent) {
        String titulo = tfTitulo.getText();
        String genero = tfGenero.getText();
        String añoStr = tfAño.getText();
        String director = tfDirector.getText();
        String descripcion = taDescripcion.getText();

        if (titulo.isEmpty() || genero.isEmpty() || añoStr.isEmpty() || director.isEmpty() || descripcion.isEmpty()) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "Campos incompletos", "Por favor, completa todos los campos.");
            return;
        }

        try {
            int año = Integer.parseInt(añoStr);
            if (año <= 0) {
                JavaFXUtil.showModal(Alert.AlertType.WARNING, "Dato inválido", "Año incorrecto", "El año debe ser mayor que 0.");
                return;
            }

            Pelicula nuevaPelicula = new Pelicula();
            nuevaPelicula.setTitulo(titulo);
            nuevaPelicula.setGenero(genero);
            nuevaPelicula.setAño(año);
            nuevaPelicula.setDirector(director);
            nuevaPelicula.setDescripcion(descripcion);

            // Persistencia mediante el servicio adaptado
            userService.savePelicula(nuevaPelicula);

            JavaFXUtil.showModal(Alert.AlertType.INFORMATION, "Éxito", "Película guardada", "La película ha sido añadida al catálogo.");
            JavaFXUtil.setScene("/main-view.fxml");

        } catch (NumberFormatException e) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error de formato", "Campo 'Año' inválido", "El año debe ser un número válido.");
        }
    }
}