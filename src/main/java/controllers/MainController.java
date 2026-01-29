package controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import copiaPelicula.CopiaPelicula;
import pelicula.Pelicula;
import user.User;
import user.UserService;
import utils.JavaFXUtil;
import session.SimpleSessionService;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador principal de la aplicación.
 * <p>
 * Gestiona la vista principal donde se muestra el catálogo de películas y los alquileres del usuario.
 * Controla la navegación hacia otras vistas (detalles, añadir película) y las acciones de alquiler y devolución.
 * </p>
 */
public class MainController implements Initializable {
    @FXML private Button btnEliminar, btnAlquilar;
    @FXML private Menu menuAdmin;
    @FXML private TableView<CopiaPelicula> table;
    @FXML private TableView<Pelicula> tablaCatalogo;

    // Columnas Alquileres
    @FXML private TableColumn<CopiaPelicula, String> titulo, genero, director, descripcion;
    @FXML private TableColumn<CopiaPelicula, Integer> año;

    // Columnas Catalogo
    @FXML private TableColumn<Pelicula, Integer> colCatAnio;
    @FXML private TableColumn<Pelicula, String> colCatTitulo, colCatGenero;

    private User currentUser;
    private final SimpleSessionService sessionService = new SimpleSessionService();
    private final UserService userService = new UserService();

    /**
     * Inicializa el controlador principal.
     * <p>
     * Verifica la sesión del usuario, carga sus datos y dependencias (copias alquiladas),
     * configura las tablas y refresca la interfaz. Si la carga de dependencias falla,
     * intenta continuar con una funcionalidad limitada.
     * </p>
     *
     * @param url            La ubicación utilizada para resolver rutas relativas.
     * @param resourceBundle Los recursos utilizados para la localización.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User tempUser = sessionService.getActive();
        if (tempUser == null) {
            // Si no hay usuario en sesión, no intentamos cargar nada más
            return;
        }

        // Intentamos cargar el usuario con dependencias
        try {
            currentUser = userService.getUserWithDependencies(tempUser.getId());
        } catch (Exception e) {
            System.err.println("Error al cargar dependencias del usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Si falla la carga de dependencias o devuelve null, usamos el usuario de la sesión como fallback
        if (currentUser == null) {
            System.out.println("Usando usuario de sesión (sin dependencias cargadas) como fallback.");
            currentUser = tempUser;
        }

        configurarTablas();
        configurarEventosTabla();
        refrescarInterfaz();
    }

    /**
     * Configura las columnas de las tablas de catálogo y alquileres.
     * <p>
     * Define cómo se obtienen los valores de las celdas a partir de los objetos {@link Pelicula} y {@link CopiaPelicula}.
     * Incluye protecciones contra valores nulos.
     * </p>
     */
    private void configurarTablas() {
        // Configurar catálogo
        colCatTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colCatGenero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGenero()));
        colCatAnio.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getAño()).asObject());

        // Configurar mis alquileres (CopiaPelicula -> Pelicula)
        // IMPORTANTE: Verificar nulos en la cadena de llamadas para evitar NullPointerException
        titulo.setCellValueFactory(c -> {
            if (c.getValue() != null && c.getValue().getPelicula() != null) {
                return new SimpleStringProperty(c.getValue().getPelicula().getTitulo());
            }
            return new SimpleStringProperty("Sin Título");
        });
        
        genero.setCellValueFactory(c -> {
            if (c.getValue() != null && c.getValue().getPelicula() != null) {
                return new SimpleStringProperty(c.getValue().getPelicula().getGenero());
            }
            return new SimpleStringProperty("-");
        });
        
        director.setCellValueFactory(c -> {
            if (c.getValue() != null && c.getValue().getPelicula() != null) {
                return new SimpleStringProperty(c.getValue().getPelicula().getDirector());
            }
            return new SimpleStringProperty("-");
        });
        
        año.setCellValueFactory(c -> {
            if (c.getValue() != null && c.getValue().getPelicula() != null) {
                return new SimpleIntegerProperty(c.getValue().getPelicula().getAño()).asObject();
            }
            return new SimpleIntegerProperty(0).asObject();
        });
    }

    /**
     * Actualiza los datos mostrados en la interfaz.
     * <p>
     * Recarga la lista de películas del catálogo y la copia alquilada por el usuario actual.
     * Ajusta la visibilidad de los botones y menús según si el usuario es administrador.
     * </p>
     */
    private void refrescarInterfaz() {
        if (currentUser == null) return;

        // Cargar Catálogo (Usando tu método findAllPeliculas)
        try {
            tablaCatalogo.setItems(FXCollections.observableArrayList(userService.findAllPeliculas()));
        } catch (Exception e) {
            System.err.println("Error al cargar el catálogo de películas: " + e.getMessage());
            e.printStackTrace();
        }

        // Cargar Copia del usuario
        ObservableList<CopiaPelicula> lista = FXCollections.observableArrayList();
        // Verificamos si getCopiaAsignada() es accesible (podría lanzar excepción si es Lazy y la sesión está cerrada)
        try {
            if (currentUser.getCopiaAsignada() != null) {
                lista.add(currentUser.getCopiaAsignada());
            }
        } catch (Exception e) {
             System.err.println("No se pudo cargar la copia asignada (posible LazyInitializationException): " + e.getMessage());
             // No hacemos nada, simplemente la lista queda vacía
        }
        table.setItems(lista);

        // Ajustar visibilidad según rol
        boolean isAdmin = currentUser.isAdmin();
        if (menuAdmin != null) menuAdmin.setVisible(isAdmin);
        if (btnEliminar != null) btnEliminar.setVisible(isAdmin);
        if (btnAlquilar != null) btnAlquilar.setVisible(!isAdmin);
    }

    /**
     * Maneja la acción de alquilar una película seleccionada.
     * <p>
     * Asigna una copia disponible de la película al usuario actual.
     * </p>
     *
     * @param event El evento de acción.
     */
    @FXML
    void alquilarPelicula(ActionEvent event) {
        Pelicula sel = tablaCatalogo.getSelectionModel().getSelectedItem();
        if (sel != null) {
            try {
                // Usando tu método addPeliculaOrCopia
                userService.addPeliculaOrCopia(currentUser, sel);
                // Recargamos el usuario completo para ver la nueva copia
                currentUser = userService.getUserWithDependencies(currentUser.getId());
                refrescarInterfaz();
            } catch (Exception e) {
                JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", null, e.getMessage());
            }
        }
    }

    /**
     * Maneja la acción de eliminar una película (solo administradores).
     * <p>
     * Elimina la película seleccionada y sus copias de la base de datos.
     * </p>
     *
     * @param event El evento de acción.
     */
    @FXML
    void eliminarPelicula(ActionEvent event) {
        Pelicula sel = tablaCatalogo.getSelectionModel().getSelectedItem();
        if (sel != null) {
            // Usando tu método removePeliculaOrCopia
            userService.removePeliculaOrCopia(currentUser, sel);
            refrescarInterfaz();
        }
    }

    /**
     * Cierra la sesión del usuario actual y vuelve a la pantalla de login.
     *
     * @param e El evento de acción.
     */
    @FXML void cerrarSesion(ActionEvent e) {
        sessionService.logout();
        JavaFXUtil.setScene("/login-view.fxml");
    }

    /**
     * Cierra la aplicación.
     *
     * @param e El evento de acción.
     */
    @FXML void salir(ActionEvent e) { System.exit(0); }

    /**
     * Navega a la vista para añadir una nueva película.
     *
     * @param e El evento de acción.
     */
    @FXML void añadirPelicula(ActionEvent e) {
        JavaFXUtil.setScene("/newFilmForm-view.fxml");
    }
    
    /**
     * Configura los eventos de interacción con la tabla del catálogo.
     * <p>
     * Habilita el doble clic sobre una fila para ver los detalles de la película.
     * </p>
     */
    private void configurarEventosTabla() {
        // Detectar doble clic en la tabla del catálogo
        tablaCatalogo.setRowFactory(tv -> {
            TableRow<Pelicula> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Pelicula seleccionada = row.getItem();
                    verDetallePelicula(seleccionada);
                }
            });
            return row;
        });
    }

    /**
     * Navega a la vista de detalles de la película seleccionada.
     *
     * @param pelicula La película a visualizar.
     */
    private void verDetallePelicula(Pelicula pelicula) {
        if (pelicula != null) {
            // Guardamos la película en la sesión para que DetailController la pueda leer
            sessionService.setObject("pelicula_seleccionada", pelicula);
            // Navegamos a la vista de detalles (asegúrate de que el nombre coincida con tu archivo)
            JavaFXUtil.setScene("/Detail-view.fxml");
        }
    }
}