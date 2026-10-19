package ni.edu.uam.demologin.service;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ni.edu.uam.demologin.Main;

import java.io.IOException;

public class VentanaService {

    public void cerrarVentana(ActionEvent actionEvent) {
        Node node = (Node)actionEvent.getSource();
        Window window = (Window)node.getScene().getWindow();
        window.hide();
    }

    public void abrirVentana(String fxml, Node node, String titulo) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource(fxml)
        );

        Parent root = loader.load();

        Stage ventana = new Stage();

        ventana.setTitle(titulo);
        ventana.setScene(new Scene(root, 600, 500));

        //ambas ventanas estan asociadas
        Stage ventanaPrincipal = (Stage) node
                .getScene()
                .getWindow();

        ventana.initOwner(ventanaPrincipal);

        //esto evita que puedas hacer cosas en el home sin antes cerrar la ventana secundaria
        ventana.initModality(Modality.WINDOW_MODAL);

        ventana.show();
    }
}
