import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Button btnAlgorithms;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    private void setAlgorithms() {
        BorderPane pane = null;
        try {
            pane = FXMLLoader.load(getClass().getClassLoader().getResource("algorithm.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setMainFrame(pane);
    }

    @FXML
    private void setCrypt() throws IOException {
        BorderPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("crypt.fxml"));
        setMainFrame(pane);
    }

    @FXML
    private void setEnvelope() throws IOException {
        BorderPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("envelope.fxml"));
        setMainFrame(pane);
    }

    @FXML
    private void setSignature() throws IOException {
        BorderPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("signature.fxml"));
        setMainFrame(pane);
    }

    @FXML
    private void setStamp() throws IOException {
        BorderPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("stamp.fxml"));
        setMainFrame(pane);
    }

    private void setMainFrame(BorderPane pane) {
        Scene scene = getScene();
        BorderPane root = (BorderPane) scene.getRoot();
        root.setCenter(pane.getCenter());
    }

    private Scene getScene() {
        // any of the controls can retrieve scene
        return this.btnAlgorithms.getScene();
    }

}

