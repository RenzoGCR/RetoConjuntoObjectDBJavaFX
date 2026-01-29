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

/**
 * Controlador para la vista de inicio de sesión (Login).
 * <p>
 * Gestiona la autenticación de usuarios, validando las credenciales contra la base de datos
 * y estableciendo la sesión de usuario activa.
 * </p>
 */
public class LoginController implements Initializable {
    @FXML private PasswordField txtContraseña; // Cambiado a PasswordField por seguridad
    @FXML private TextField txtCorreo;
    @FXML private Label info;

    private final UserService userService = new UserService();
    private final SimpleSessionService sessionService = new SimpleSessionService();

    /**
     * Inicializa el controlador.
     * <p>
     * Limpia cualquier mensaje de información o error previo al cargar la vista.
     * </p>
     *
     * @param url            La ubicación utilizada para resolver rutas relativas.
     * @param resourceBundle Los recursos utilizados para la localización.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Limpiamos el mensaje de error al iniciar
        info.setText("");
    }

    /**
     * Maneja el proceso de inicio de sesión.
     * <p>
     * Valida que los campos no estén vacíos, intenta autenticar al usuario mediante {@link UserService},
     * y si es exitoso, guarda el usuario en la sesión y redirige a la vista principal.
     * En caso contrario, muestra un mensaje de error.
     * </p>
     *
     * @param actionEvent El evento de acción generado por el botón "Entrar".
     */
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

    /**
     * Cierra la aplicación.
     *
     * @param actionEvent El evento de acción generado por el botón "Salir".
     */
    @FXML
    public void Salir(ActionEvent actionEvent) {
        System.exit(0);
    }
}