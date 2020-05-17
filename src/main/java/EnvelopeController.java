import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class EnvelopeController implements Initializable {

    @FXML
    private ComboBox<String> symCreateCombo;

    @FXML
    private ComboBox<String> asymCreateCombo;

    @FXML
    private ComboBox<String> symVerifyCombo;

    @FXML
    private ComboBox<String> asymVerifyCombo;

    @FXML
    private Label labelFile;
    @FXML
    private Label labelDestination;
    @FXML
    private Label labelEnvelope;
    @FXML
    private Label labelDestinationDecrypt;

    private File selectedSecretKeyCreate = new File("./Crypto Files/3des112cbc.txt");
    private File selectedPublicKey = new File("./Crypto Files/rsa1024public.txt");
    private File selectedFile = new File("./Crypto Files/lorem.txt");
    private File selectedDestination = new File("./Crypto Files/envelope-3des112cbc-rsa1024.txt");

    private File selectedSecretKeyVerify = new File("./Crypto Files/3des112cbc.txt");
    private File selectedPrivateKey = new File("./Crypto Files/rsa1024private.txt");
    private File selectedEnvelope = new File("./Crypto Files/envelope-3des112cbc-rsa1024.txt");
    private File selectedDestinationDecrypt = new File("./Crypto Files/env-dest.txt");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initCombos();
        setLabels();
    }

    private void setLabels() {
        Utils.setLabelFor(labelFile, selectedFile);
        Utils.setLabelFor(labelDestination, selectedDestination);
        Utils.setLabelFor(labelEnvelope, selectedEnvelope);
        Utils.setLabelFor(labelDestinationDecrypt, selectedDestinationDecrypt);
    }

    private void initCombos() {
        symCreateCombo.getItems().addAll("3DES", "AES");
        symCreateCombo.getSelectionModel().selectFirst();
        asymCreateCombo.getItems().addAll("RSA");
        asymCreateCombo.getSelectionModel().selectFirst();

        symVerifyCombo.getItems().addAll("3DES", "AES");
        symVerifyCombo.getSelectionModel().selectFirst();
        asymVerifyCombo.getItems().addAll("RSA");
        asymVerifyCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void onCreate() {
        if (!checkCreateConditions())
            return;

        try {
            // Load symmetric algorithm
            SymmetricCryptoTool stool = null;
            String selectedSym = symCreateCombo.getSelectionModel().getSelectedItem();
            if (selectedSym.equals("3DES")) {
                stool = new DESCryptoTool();
            } else {
                stool = new AESCrpytoTool();
            }
            CryptoParser parser = new CryptoParser(selectedSecretKeyCreate.toString());
            parser.parseFile();
            stool.readSecretKey(parser);

            // Load asymmetric algorithm
            AsymmetricCryptoTool atool = new RSACryptoTool();
            parser.clear();
            parser.setFilePath(selectedPublicKey.toString());
            parser.parseFile();
            atool.readPublicKey(parser);

            // Create envelope
            parser.clear();
            parser.setFilePath(selectedDestination.toString());
            DigitalEnvelope de = new DigitalEnvelope(stool, atool);
            de.storeData(parser, selectedFile.toString());
            parser.writeToFile();

            Utils.showConfirmationAlert(
                    "Digital envelope",
                    "Digital envelope successfully created!",
                    "Do you want to take a look at the envelope content?",
                    parser
            );
        } catch (Exception e) {
            Utils.showErrorAlert("Creation of digital envelope failed!", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onVerify() {
        if (!checkVerifyConditions())
            return;

        try {
            // Load symmetric algorithm
            SymmetricCryptoTool stool = null;
            String selectedSym = symVerifyCombo.getSelectionModel().getSelectedItem();
            if (selectedSym.equals("3DES")) {
                stool = new DESCryptoTool();
            } else {
                stool = new AESCrpytoTool();
            }
            CryptoParser parser = new CryptoParser(selectedSecretKeyVerify.toString());
            parser.parseFile();
            stool.readSecretKey(parser);

            // Load asymmetric algorithm
            AsymmetricCryptoTool atool = new RSACryptoTool();
            parser.clear();
            parser.setFilePath(selectedPrivateKey.toString());
            parser.parseFile();
            atool.readPrivateKey(parser);

            // Decrypt envelope
            DigitalEnvelope de = new DigitalEnvelope(stool, atool);
            parser.clear();
            parser.setFilePath(selectedEnvelope.toString());
            parser.parseFile();
            String originalText = de.decryptEnvelope(parser);
            parser.clear();
            parser.setFilePath(selectedDestinationDecrypt.toString());
            parser.addPair("Original data", originalText);
            parser.setWriteRaw(true);
            parser.writeToFile();
            Utils.showConfirmationAlert(
                    "Digital envelope",
                    "Digital envelope is decrypted!",
                    "Do you want to take a look at the original content?",
                    parser
            );

        } catch (Exception e) {
            Utils.showErrorAlert("Verification of digital envelope failed!", e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean checkCreateConditions() {
        if (selectedSecretKeyCreate == null) {
            Utils.showWarningAlert("Symmetric algorithm file is not selected!",
                    "Click on LOAD ALGORITHM to load a file");
            return false;
        }
        if (selectedPublicKey == null) {
            Utils.showWarningAlert("Asymmetric algorithm file is not selected!",
                    "Click on LOAD ALGORITHM to load a file");
            return false;
        }
        if (selectedFile == null) {
            Utils.showWarningAlert("File for which digital envelope will be created is not selected!",
                    "Click on SELECT FILE to load a file");
            return false;
        }
        if (selectedDestination == null) {
            Utils.showWarningAlert("Destination where to save digital envelope is not selected!",
                    "Click on SELECT DESTINATION");
            return false;
        }
        return true;
    }

    private boolean checkVerifyConditions() {
        if (selectedSecretKeyVerify == null) {
            Utils.showWarningAlert("Symmetric algorithm file is not selected!",
                    "Click on LOAD SECRET KEY to load a file");
            return false;
        }
        if (selectedPrivateKey == null) {
            Utils.showWarningAlert("Asymmetric algorithm file is not selected!",
                    "Click on LOAD PRIVATE KEY to load a file");
            return false;
        }
        if (selectedEnvelope == null) {
            Utils.showWarningAlert("Digital envelope that will be verified is not selected!",
                    "Click on SELECT FILE to load a file");
            return false;
        }
        return true;
    }

    @FXML
    private void showMoreSecretCreate() {
        if (selectedSecretKeyCreate != null)
            Utils.showMore("Secret key", selectedSecretKeyCreate);
    }

    @FXML
    private void showMorePublic() {
        if (selectedPublicKey != null)
            Utils.showMore("Public key", selectedPublicKey);
    }

    @FXML
    private void showMoreSecretVerify() {
        if (selectedSecretKeyVerify != null)
            Utils.showMore("Secret key", selectedSecretKeyVerify);
    }

    @FXML
    private void showMorePrivate() {
        if (selectedPrivateKey != null)
            Utils.showMore("Private key", selectedPrivateKey);
    }

    @FXML
    private void onSecretKeyCreateLoad() {
        selectedSecretKeyCreate = Utils.chooseFile(true,
                "Choose symmetric algorithm file");
    }

    @FXML
    private void onPublicKeyLoad() {
        selectedPublicKey = Utils.chooseFile(true,
                "Choose asymmetric algorithm file");
    }

    @FXML
    private void onSecretKeyVerifyLoad() {
        selectedSecretKeyVerify = Utils.chooseFile(true,
                "Choose symmetric algorithm file");
    }

    @FXML
    private void onPrivateKeyLoad() {
        selectedPrivateKey = Utils.chooseFile(true,
                "Choose asymmetric algorithm file");
    }

    @FXML
    private void onSelectFile() {
        selectedFile = Utils.chooseFile(true,
                "Choose file for which digital envelope will be created");
        Utils.setLabelFor(labelFile, selectedFile);
    }

    @FXML
    private void onSelectDestination() {
        selectedDestination = Utils.chooseFile(false,
                "Choose destination for digital envelope");
        Utils.setLabelFor(labelDestination, selectedDestination);
    }

    @FXML
    private void onSelectEnvelope() {
        selectedEnvelope = Utils.chooseFile(true,
                "Select digital envelope");
        Utils.setLabelFor(labelEnvelope, selectedEnvelope);
    }

    @FXML
    private void onSelectDestinationDecrypt() {
        selectedDestinationDecrypt = Utils.chooseFile(false,
                "Choose destination for decrypted digital envelope");
        Utils.setLabelFor(labelDestinationDecrypt, selectedDestinationDecrypt);
    }

}
