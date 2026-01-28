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

    @FXML
    void editarPelicula(ActionEvent event) {
        // Ruta simplificada al FXML
        JavaFXUtil.setScene("/editar-view.fxml");
    }

    @FXML
    public void volver(ActionEvent actionEvent) {
        // Limpiamos la selección al volver
        sessionService.setObject("pelicula_seleccionada", null);
        JavaFXUtil.setScene("/main-view.fxml");
    }
}