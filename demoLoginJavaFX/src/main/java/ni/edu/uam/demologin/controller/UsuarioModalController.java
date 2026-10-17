package ni.edu.uam.demologin.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ni.edu.uam.demologin.model.Usuario;
import ni.edu.uam.demologin.service.VentanaService;

import java.io.File;

public class UsuarioModalController {

    @FXML
    private Label lblNombre;
    @FXML
    private Label lblApellido;
    @FXML
    private Label lblCorreo;
    @FXML
    private Label lblUsuario;
    @FXML
    private Label lblRutaFoto;
    @FXML
    private ImageView imageFoto;

    private final VentanaService ventanaService
            = new VentanaService();

    public void setUsuario(Usuario usuario) {
        lblNombre.setText(usuario.getNombre());
        lblApellido.setText(usuario.getApellido());
        lblCorreo.setText(usuario.getCorreo());
        lblUsuario.setText(usuario.getUsuario());
        lblRutaFoto.setText(usuario.getRutafoto());

        mostrarImagen(usuario.getRutafoto());
    }

    // Muestra la fotografía del usuario si existe
    private void mostrarImagen(String rutaFoto) {
        if (rutaFoto == null || rutaFoto.trim().isEmpty()) {
            imageFoto.setImage(null);
            return;
        }
        File archivo = new File(rutaFoto);
        if (archivo.exists() && archivo.isFile()) {
            Image image = new Image(
                    archivo.toURI().toString()
            );
            imageFoto.setImage(image);
        } else {
            imageFoto.setImage(null);
        }
    }
    @FXML
    private void mostrarFoto(ActionEvent event) {
        ventanaService.cerrarVentana(event);
    }
}
