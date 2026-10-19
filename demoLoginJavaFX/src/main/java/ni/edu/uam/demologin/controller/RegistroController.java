package ni.edu.uam.demologin.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality; // CAMBIO: import nuevo
import javafx.stage.Stage;
import ni.edu.uam.demologin.model.Usuario;
import ni.edu.uam.demologin.service.NavegationService; // CAMBIO: import nuevo
import ni.edu.uam.demologin.service.UsuarioService;

import java.io.File;
import java.io.IOException; // CAMBIO: import nuevo
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

    // CAMBIO: campo nuevo para poder volver al login
    private final NavegationService navegationService = new NavegationService();

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
        // CAMBIO: nombre y apellido solo con letras
        if (!txtNombre.getText().trim().matches("[\\p{L} ]+")
                || !txtApellido.getText().trim().matches("[\\p{L} ]+")) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Nombre no válido",
                    "El nombre y el apellido solo pueden contener letras."
            );

            return;
        }
        // CAMBIO: el correo siempre debe llevar @
        if (!txtCorreo.getText().trim().matches("[^@\\s]+@[^@\\s]+")) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Correo no válido",
                    "El correo debe llevar @, por ejemplo: nombre@correo.com"
            );

            return;
        }
        // CAMBIO: la foto también es obligatoria
        if (rutaFoto == null || rutaFoto.isBlank()) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Foto requerida",
                    "Debes seleccionar una fotografía."
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

            // CAMBIO: antes aquí se cerraba siempre la ventana (stage.close)
            cerrarOVolverAlLogin();

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
        // CAMBIO: antes aquí se cerraba siempre la ventana (stage.close)
        cerrarOVolverAlLogin();
    }

    // CAMBIO: metodo nuevo, si la ventana es modal la cierra y si es la principal vuelve al login
    private void cerrarOVolverAlLogin() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();

        if (stage.getModality() != Modality.NONE) {
            stage.close();
        } else {
            try {
                navegationService.cambiarVista("login.fxml", txtNombre);
            } catch (IOException e) {
                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "Error",
                        "No se pudo volver al login: " + e.getMessage()
                );
            }
        }
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