/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package di02_reservascoche;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Diego
 */
public class CocheElectricoController implements Initializable {

    @FXML
    private Button btnReservar;
    @FXML
    private ComboBox cbTipoVehiculo;
    @FXML
    private Label lbEdadConductor;
    @FXML
    private TextField txEdadConductor;
    @FXML
    private CheckBox cbCableCarga;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbTipoVehiculo.getItems().addAll("Berlina", "Compacto", "Deportivo", "SUV", "Tesla");
        colocarIconosBotones();
    }

    @FXML
    public void handleCbTipoVehiculo(ActionEvent event) throws IOException {
        if (cbTipoVehiculo.getValue().equals("Tesla")) {
            lbEdadConductor.setDisable(false);
            txEdadConductor.setDisable(false);
            cbCableCarga.setDisable(false);
        } else {
            lbEdadConductor.setDisable(true);
            txEdadConductor.setDisable(true);
            txEdadConductor.setText("");
            cbCableCarga.setDisable(true);
            cbCableCarga.setSelected(false);
        }
    }

    @FXML
    public void handleBtnCancelar(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleBtnReservar(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmación");
        alert.setContentText("Su coche eléctrico ha sido reservado con exito!");
        alert.showAndWait();

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void colocarIconosBotones() {
        URL linkIcono = getClass().getResource("/img/Icono.png");
        Image imagenIcono = new Image(linkIcono.toString(), 20, 20, false, true);

        btnReservar.setGraphic(new ImageView(imagenIcono));
        btnReservar.setContentDisplay(ContentDisplay.RIGHT);
    }

}
