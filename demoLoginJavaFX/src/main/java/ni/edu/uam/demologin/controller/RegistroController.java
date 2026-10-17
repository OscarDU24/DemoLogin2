package ni.edu.uam.demologin.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ni.edu.uam.demologin.model.Usuario;
import ni.edu.uam.demologin.service.UsuarioService;

import java.io.File;
import java.util.UUID;

public class RegistroController {

    // Campos del registro.fxml.
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirmaPassword;
    @FXML
    private ImageView imageFoto;

    // Servicio para trabajar con la base de datos.
    private final UsuarioService service = new UsuarioService();

    // Usuario que se está editando.
    // Si es null, se registrará un usuario nuevo.
    private Usuario usuarioEditar;

    // Ruta de la foto seleccionada.
    private String rutaFoto;

    // Permite distinguir entre registro y actualización.
    public void setUsuarioParaEditar(Usuario usuario) {

        this.usuarioEditar = usuario;

        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtCorreo.setText(usuario.getCorreo());
        txtUsuario.setText(usuario.getUsuario());

        // Conserva la ruta de la foto actual.
        rutaFoto = usuario.getRutafoto();

        if (rutaFoto != null && !rutaFoto.isBlank()) {
            File archivo = new File(rutaFoto);

            if (archivo.exists()) {
                imageFoto.setImage(
                        new Image(archivo.toURI().toString())
                );
            }
        }
    }

    // Abre el selector de imágenes.
    @FXML
    public void seleccionarFoto() {

        FileChooser selector = new FileChooser();

        selector.setTitle("Seleccionar fotografía");

        // Solo permite archivos PNG, JPG y JPEG.
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imágenes PNG, JPG y JPEG",
                        "*.png", "*.PNG",
                        "*.jpg", "*.JPG",
                        "*.jpeg", "*.JPEG"
                )
        );

        File archivo = selector.showOpenDialog(imageFoto.getScene().getWindow());

        if (archivo == null) {
            return;
        }

        // Verifica la extensión del archivo seleccionado.
        String nombreArchivo = archivo.getName().toLowerCase();

        if (!nombreArchivo.endsWith(".png")
                && !nombreArchivo.endsWith(".jpg")
                && !nombreArchivo.endsWith(".jpeg")) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Formato no válido",
                    "Solo se permiten imágenes PNG, JPG y JPEG."
            );

            return;
        }

        try {
            Image imagen = new Image(archivo.toURI().toString());

            if (imagen.isError()) {
                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "Imagen no válida",
                        "No se pudo cargar la imagen seleccionada."
                );
                return;
            }

            // Muestra la imagen y guarda su ruta.
            imageFoto.setImage(imagen);
            rutaFoto = archivo.getAbsolutePath();

        } catch (Exception e) {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo cargar la fotografía."
            );
        }
    }

    // Registra un usuario nuevo o actualiza uno existente.
    @FXML
    public void registrar() {

        // Validación de campos obligatorios.
        if (txtNombre.getText().isBlank()
                || txtApellido.getText().isBlank()
                || txtCorreo.getText().isBlank()
                || txtUsuario.getText().isBlank()) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Campos obligatorios",
                    "Completa todos los campos de nombre, apellido, "
                            + "correo y usuario."
            );

            return;
        }

        String password = txtPassword.getText();
        String confirmaPassword = txtConfirmaPassword.getText();

        // En el registro nuevo, la contraseña es obligatoria.
        if (usuarioEditar == null
                && (password.isBlank() || confirmaPassword.isBlank())) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Contraseña requerida",
                    "Debes ingresar y confirmar la contraseña."
            );

            return;
        }

        // Si se escribe una contraseña, debe confirmarse.
        if (!password.isBlank() || !confirmaPassword.isBlank()) {

            if (!password.equals(confirmaPassword)) {
                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "Contraseñas diferentes",
                        "Las contraseñas no coinciden."
                );

                return;
            }

        } else if (usuarioEditar != null) {

            // Si se está editando y no se cambia la contraseña,
            // se conserva la contraseña existente.
            password = usuarioEditar.getPassword();
        }

        try {

            Usuario usuario = new Usuario(
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtCorreo.getText().trim(),
                    txtUsuario.getText().trim(),
                    password,
                    rutaFoto
            );

            if (usuarioEditar == null) {

                // Registra un usuario nuevo.
                service.save(usuario);

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Registro exitoso",
                        "El usuario se registró correctamente."
                );

            } else {

                // Conserva el ID del registro original.
                UUID idOriginal = usuarioEditar.getId();
                usuario.setId(idOriginal);

                // Actualiza el usuario existente.
                service.update(usuario);

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Actualización exitosa",
                        "Los datos del usuario se actualizaron correctamente."
                );
            }

            // Cierra la ventana después de guardar.
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.close();

        } catch (Exception e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo guardar el usuario: " + e.getMessage()
            );
        }
    }

    // Regresa a la ventana anterior.
    @FXML
    public void regresar() {

        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    // Muestra mensajes al usuario.
    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
