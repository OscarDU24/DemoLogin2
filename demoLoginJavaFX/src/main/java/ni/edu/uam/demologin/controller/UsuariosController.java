package ni.edu.uam.demologin.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ni.edu.uam.demologin.model.Usuario;
import ni.edu.uam.demologin.service.UsuarioService;

import java.io.IOException;
import java.util.Optional;

public class UsuariosController {

    // Componentes del usuarios.fxml.
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

    @FXML
    private TableColumn<Usuario, Void> colAcciones;

    // Servicio para trabajar con la base de datos.
    private final UsuarioService service = new UsuarioService();

    // Configuración inicial de la tabla.
    @FXML
    public void initialize() {

        colNombre.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getNombre()));

        colApellido.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getApellido()));

        colUsuario.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getUsuario()));

        colCorreo.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getCorreo()));

        configurarBotones();
        cargarUsuarios();
    }

    // Consulta los usuarios y los presenta en la tabla.
    private void cargarUsuarios() {

        try {
            tablaUsuarios.setItems(
                    FXCollections.observableArrayList(
                            service.findAll()));
        } catch (Exception e) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudieron cargar los usuarios: "
                            + e.getMessage()
            );
        }
    }

    // Agrega los botones de cada fila.
    private void configurarBotones() {

        colAcciones.setCellFactory(columna -> new TableCell<>() {

            private final Button btnActualizar =
                    new Button("Actualizar");

            private final Button btnBorrar =
                    new Button("Borrar");

            private final HBox contenedor =
                    new HBox(8, btnActualizar, btnBorrar);

            {
                btnActualizar.setOnAction(event -> {
                    Usuario usuario = getTableView()
                            .getItems().get(getIndex());

                    actualizarUsuario(usuario);
                });

                btnBorrar.setOnAction(event -> {
                    Usuario usuario = getTableView()
                            .getItems().get(getIndex());

                    borrarUsuario(usuario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(contenedor);
                }
            }
        });
    }

    // Solicita confirmación antes de eliminar.
    private void borrarUsuario(Usuario usuario) {

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(
                "¿Desea borrar este usuario?");
        confirmacion.setContentText(
                "Usuario: " + usuario.getUsuario()
                        + "\nEsta acción no se puede deshacer."
        );

        Optional<ButtonType> respuesta =
                confirmacion.showAndWait();

        if (respuesta.isPresent()
                && respuesta.get() == ButtonType.OK) {

            try {
                service.delete(usuario.getId());

                UtilController.mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Usuario eliminado",
                        "El usuario se eliminó correctamente."
                );

                cargarUsuarios();

            } catch (Exception e) {
                UtilController.mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "Error",
                        "No se pudo eliminar el usuario: "
                                + e.getMessage()
                );
            }
        }
    }

    // Abre el formulario para editar un usuario.
    private void actualizarUsuario(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/ni/edu/uam/demologin/registro.fxml"
                    )
            );
            Parent root = loader.load();

            RegistroController controller = loader.getController();

            // Carga los datos del usuario seleccionado.
            controller.setUsuarioParaEditar(usuario);

            Stage stage = new Stage();
            stage.setTitle("Actualizar usuario");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recarga la tabla al cerrar el formulario.
            cargarUsuarios();

        } catch (Exception e) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo abrir el formulario de actualización: "
                            + e.getMessage()
            );
        }
    }

    // Abre el formulario para registrar un usuario nuevo.
    @FXML
    public void registrarUsuario() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("registro.fxml"));

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Registrar usuario");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarUsuarios();

        } catch (IOException e) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo abrir la ventana de registro: "
                            + e.getMessage()
            );
        }
    }
}