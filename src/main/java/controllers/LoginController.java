package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import user.User;
import user.UserService;
import session.SimpleSessionService;
import utils.JavaFXUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private PasswordField txtContraseña; // Cambiado a PasswordField por seguridad
    @FXML private TextField txtCorreo;
    @FXML private Label info;

    private final UserService userService = new UserService();
    private final SimpleSessionService sessionService = new SimpleSessionService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Limpiamos el mensaje de error al iniciar
        info.setText("");
    }

    @FXML
    public void entrar(ActionEvent actionEvent) {
        String username = txtCorreo.getText();
        String password = txtContraseña.getText();

        if (username.isEmpty() || password.isEmpty()) {
            info.setText("Por favor, rellena todos los campos.");
            info.setStyle("-fx-text-fill: orange;");
            return;
        }

        // Buscamos el usuario usando el UserService que ya tienes
        // Nota: Si no tienes un método login en UserService, podrías añadir uno simple.
        User user = userService.login(username, password);

        if (user != null) {
            sessionService.login(user);
            // IMPORTANTE: Ruta simplificada del FXML
            JavaFXUtil.setScene("/main-view.fxml");
        } else {
            info.setText("Usuario o contraseña incorrectos.");
            info.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void Salir(ActionEvent actionEvent) {
        System.exit(0);
    }
}