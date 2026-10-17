package ni.edu.uam.demologin.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ni.edu.uam.demologin.model.Usuario;
import ni.edu.uam.demologin.service.UsuarioService;

public class UsuariosController {

    // Componentes del usuarios.fxml (el nombre debe ser igual al fx:id)
    @FXML
    private TableView<Usuario> tablaUsuarios;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colApellido;
    @FXML
    private TableColumn<Usuario, String> colUsuario;
    @FXML
    private TableColumn<Usuario, String> colCorreo;

    // Servicio que ya existe, lo usamos para traer los usuarios de la BD
    private final UsuarioService service = new UsuarioService();

    // JavaFX llama este método automáticamente al cargar el FXML
    @FXML
    public void initialize() {
        // Le decimos a cada columna qué dato del Usuario mostrar
        colNombre.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getNombre()));
        colApellido.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getApellido()));
        colUsuario.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getUsuario()));
        colCorreo.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getCorreo()));

        try {
            // findAll() ya existe en UsuarioService: devuelve todos los usuarios
            tablaUsuarios.setItems(
                    FXCollections.observableArrayList(service.findAll()));
        } catch (Exception e) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudieron cargar los usuarios: " + e.getMessage()
            );
        }
    }
}