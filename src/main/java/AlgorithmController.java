import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AlgorithmController implements Initializable {

    @FXML
    private ComboBox<String> symAlgCombo;
    @FXML
    private ComboBox<Integer> symKeyCombo;
    @FXML
    private ComboBox<String> symModeCombo;

    private File selectedSymFile = null;

    @FXML
    private ComboBox<String> asymAlgCombo;
    @FXML
    private ComboBox<Integer> asymKeyCombo;

    private File selectedAsymPublicFile = null;
    private File selectedAsymPrivateFile = null;

    @FXML
    private Label labelSecret;
    @FXML
    private Label labelPublic;
    @FXML
    private Label labelPrivate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initCombos();
        setLabels();
    }

    private void initCombos() {
        symAlgCombo.getItems().addAll("3DES", "AES");
        symAlgCombo.getSelectionModel().selectFirst();
        symAlgorithmChanged();

        asymAlgCombo.getItems().addAll("RSA");
        asymAlgCombo.getSelectionModel().selectFirst();
        asymAlgorithmChanged();
    }

    private void setLabels() {
        Utils.setLabelFor(labelSecret, selectedSymFile);
        Utils.setLabelFor(labelPublic, selectedAsymPublicFile);
        Utils.setLabelFor(labelPrivate, selectedAsymPrivateFile);
    }

    @FXML
    private void onSymCreate() {
        if (!checkSymConditions())
            return;

        createSymFile();
    }

    @FXML
    private void onAsymCreate() {
        if (!checkAsymConditions())
            return;

        createAsymFile();
    }

    private boolean checkSymConditions() {
        if (selectedSymFile == null) {
            Utils.showWarningAlert("Destination file for secret key is not choosen!",
                    "To choose a destination click on SELECT DESTINATION");
            return false;
        }
        return true;
    }

    private boolean checkAsymConditions() {
        if (selectedAsymPublicFile == null) {
            Utils.showWarningAlert("Destination file for public key is not choosen!",
                    "To choose a destination click on SELECT PUBLIC KEY DESTINATION");
            return false;
        }
        if (selectedAsymPrivateFile == null) {
            Utils.showWarningAlert("Destination file is not choosen!",
                    "To choose a destination click on SELECT PRIVATE KEY DESTINATION");
            return false;
        }
        return true;
    }

    private void createAsymFile() {
        try {
            String selected = asymAlgCombo.getSelectionModel().getSelectedItem();
            Integer keySize = asymKeyCombo.getSelectionModel().getSelectedItem();

            AsymmetricCryptoTool tool = new RSACryptoTool(keySize);
            CryptoParser parser = new CryptoParser(selectedAsymPrivateFile.toString());
            tool.storePrivateKey(parser);
            parser.writeToFile();

            Utils.showConfirmationAlert(
                    "Asymmetric algorithm",
                    "Private key file is successfully created!",
                    "Do you want to take a look at the file?",
                    parser
            );

            parser.clear();
            parser.setFilePath(selectedAsymPublicFile.toString());
            tool.storePublicKey(parser);
            parser.writeToFile();

            Utils.showConfirmationAlert(
                    "Asymmetric algorithm",
                    "Public key file is successfully created!",
                    "Do you want to take a look at the file?",
                    parser
            );

        } catch (Exception e) {
            Utils.showErrorAlert("Creation failed!", e.getMessage());
            e.printStackTrace();
        }
    }

    private void createSymFile() {
        try {
            String selected = symAlgCombo.getSelectionModel().getSelectedItem();
            Integer keySize = symKeyCombo.getSelectionModel().getSelectedItem();
            String mode = symModeCombo.getSelectionModel().getSelectedItem();

            SymmetricCryptoTool tool = null;
            if (selected.equals("3DES")) {
                tool = new DESCryptoTool(keySize, mode);
            } else {
                tool = new AESCrpytoTool(keySize, mode);
            }

            CryptoParser parser = new CryptoParser(selectedSymFile.toString());
            tool.storeSecretKey(parser);
            parser.writeToFile();

            Utils.showConfirmationAlert(
                    "Symmetric algorithm",
                    "File is successfully created!",
                    "Do you want to take a look at the file?",
                    parser
            );
        } catch (Exception e) {
            Utils.showErrorAlert("Creation failed!", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void symDestChoose() {
        selectedSymFile = Utils.chooseFile(false,
                "Choose destination file for symmetric algorithm");
        Utils.setLabelFor(labelSecret, selectedSymFile);
    }

    @FXML
    private void asymPublicChoose() {
        selectedAsymPublicFile = Utils.chooseFile(false,
                "Choose destination file for asymmetric algorithm public key");
        Utils.setLabelFor(labelPublic, selectedAsymPublicFile);
    }

    @FXML
    private void asymPrivateChoose() {
        selectedAsymPrivateFile = Utils.chooseFile(false,
                "Choose destination file for asymmetric algorithm private key");
        Utils.setLabelFor(labelPrivate, selectedAsymPrivateFile);
    }

    @FXML
    private void symAlgorithmChanged() {
        setSymKeyCombo();
        setSymModeCombo();
    }

    @FXML
    private void asymAlgorithmChanged() {
        setAsymKeyCombo();
    }

    private void setAsymKeyCombo() {
        // Only option is RSA
        asymKeyCombo.getItems().clear();
        asymKeyCombo.getItems().addAll(RSACryptoTool.KEY_SIZES);
        asymKeyCombo.getSelectionModel().selectFirst();
    }

    private void setSymKeyCombo() {
        String selected = symAlgCombo.getSelectionModel().getSelectedItem();
        symKeyCombo.getItems().clear();
        if (selected.equals("3DES")) {
            symKeyCombo.getItems().addAll(DESCryptoTool.KEY_SIZES);
        } else {
            symKeyCombo.getItems().addAll(AESCrpytoTool.KEY_SIZES);
        }
        symKeyCombo.getSelectionModel().selectFirst();
    }

    private void setSymModeCombo() {
        String selected = symAlgCombo.getSelectionModel().getSelectedItem();
        symModeCombo.getItems().clear();
        if (selected.equals("3DES")) {
            symModeCombo.getItems().addAll(DESCryptoTool.MODES);
        } else {
            symModeCombo.getItems().addAll(AESCrpytoTool.MODES);
        }
        symModeCombo.getSelectionModel().selectFirst();
    }

}
