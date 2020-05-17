import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class StampController implements Initializable {

    @FXML
    private ComboBox<String> hashCreateCombo;

    @FXML
    private ComboBox<String> hashVerifyCombo;

    @FXML
    private ComboBox<Integer> hashSizeCreateCombo;

    @FXML
    private ComboBox<Integer> hashSizeVerifyCombo;

    @FXML
    private ComboBox<String> symCreateCombo;

    @FXML
    private ComboBox<String> symVerifyCombo;

    @FXML
    private ComboBox<String> asymCreateCombo;

    @FXML
    private ComboBox<String> asymVerifyCombo;


    private File selectedSecretCreate = new File("./Crypto Files/3des112cbc.txt");

    private File selectedSecretVerify = new File("./Crypto Files/3des112cbc.txt");

    private File selectedFile = new File("./Crypto Files/lorem.txt");

    private File selectedEnvelopeDestination = new File("./Crypto Files/envelope-stamp-3des112cbc-rsa1024-sha3-224.txt");

    private File selectedStampDestination = new File("./Crypto Files/stamp-3des112cbc-rsa1024-sha224.txt");

    private File selectedPublic = new File("./Crypto Files/rsa1024public.txt");

    private File selectedPrivate = new File("./Crypto Files/rsa1024private.txt");

    private File selectedStampVerify = new File("./Crypto Files/stamp-3des112cbc-rsa1024-sha224.txt");

    private File selectedEnvelopeVerify = new File("./Crypto Files/envelope-stamp-3des112cbc-rsa1024-sha3-224.txt");

    @FXML
    private Label labelFileCreate;
    @FXML
    private Label labelEnvelopeCreate;
    @FXML
    private Label labelStampCreate;
    @FXML
    private Label labelEnvelopeVerify;
    @FXML
    private Label labelStampVerify;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initCombos();
        setLabels();
    }

    private void setLabels() {
        Utils.setLabelFor(labelFileCreate, selectedFile);
        Utils.setLabelFor(labelEnvelopeCreate, selectedEnvelopeDestination);
        Utils.setLabelFor(labelStampCreate, selectedStampDestination);

        Utils.setLabelFor(labelEnvelopeVerify, selectedEnvelopeVerify);
        Utils.setLabelFor(labelStampVerify, selectedStampVerify);
    }

    private void initCombos() {
        symCreateCombo.getItems().addAll("3DES", "AES");
        symCreateCombo.getSelectionModel().selectFirst();
        symVerifyCombo.getItems().addAll("3DES", "AES");
        symVerifyCombo.getSelectionModel().selectFirst();

        asymCreateCombo.getItems().addAll("RSA");
        asymCreateCombo.getSelectionModel().selectFirst();
        asymVerifyCombo.getItems().addAll("RSA");
        asymVerifyCombo.getSelectionModel().selectFirst();

        hashCreateCombo.getItems().addAll("SHA3");
        hashCreateCombo.getSelectionModel().selectFirst();
        hashSizeCreateCombo.getItems().addAll(SHA3CryptoHash.HASH_SIZES);
        hashSizeCreateCombo.getSelectionModel().selectFirst();

        hashVerifyCombo.getItems().addAll("SHA3");
        hashVerifyCombo.getSelectionModel().selectFirst();
        hashSizeVerifyCombo.getItems().addAll(SHA3CryptoHash.HASH_SIZES);
        hashSizeVerifyCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void onCreate() {
        if (!checkCreateConditions())
            return;

        try {
            // Load symmetric algorithm
            SymmetricCryptoTool stool = null;
            String selectedTool = symCreateCombo.getSelectionModel().getSelectedItem();
            if (selectedTool.equals("3DES")) {
                stool = new DESCryptoTool();
            } else {
                stool = new AESCrpytoTool();
            }
            CryptoParser parser = new CryptoParser(selectedSecretCreate.toString());
            parser.parseFile();
            stool.readSecretKey(parser);
            parser.clear();

            // Load asymmetric algorithm
            AsymmetricCryptoTool atool = new RSACryptoTool();
            parser.clear();
            parser.setFilePath(selectedPublic.toString());
            parser.parseFile();
            atool.readPublicKey(parser);

            // Create envelope
            parser.clear();
            parser.setFilePath(selectedEnvelopeDestination.toString());
            DigitalEnvelope de = new DigitalEnvelope(stool, atool);
            de.storeData(parser, selectedFile.toString());
            parser.writeToFile();

            Utils.showConfirmationAlert(
                    "Digital envelope",
                    "Digital envelope successfully created!",
                    "Do you want to take a look at the envelope content?",
                    parser
            );

            CryptoHash hasher = new SHA3CryptoHash(hashSizeCreateCombo.getSelectionModel().getSelectedItem());
            DigitalSignature ds = new DigitalSignature(stool, hasher);
            parser.clear();
            parser.setFilePath(selectedStampDestination.toString());
            ds.storeData(parser, selectedEnvelopeDestination.toString());
            parser.writeToFile();

            Utils.showConfirmationAlert(
                    "Digital stamp",
                    "Digital stamp successfully created!",
                    "Do you want to take a look at the stamp?",
                    parser
            );

        } catch (Exception e) {
            Utils.showErrorAlert("Creation of digital stamp failed!", e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean checkCreateConditions() {
        if (selectedSecretCreate == null) {
            Utils.showWarningAlert("Symmetric algorithm file is not selected!",
                    "Click on LOAD SECRET KEY to load a file");
            return false;
        }
        if (selectedPublic == null) {
            Utils.showWarningAlert("Symmetric algorithm file is not selected!",
                    "Click on LOAD SECRET KEY to load a file");
            return false;
        }
        if (selectedFile == null) {
            Utils.showWarningAlert("File for which digital envelope and stamp will be created is not selected",
                    "Click on SELECT FILE to load a file");
            return false;
        }
        if (selectedEnvelopeDestination == null) {
            Utils.showWarningAlert("Envelope destination is not selected",
                    "Click on ENVELOPE DESTINATION");
            return false;
        }
        if (selectedStampDestination == null) {
            Utils.showWarningAlert("Stamp destination is not selected",
                    "Click on STAMP DESTINATION");
            return false;
        }
        return true;
    }

    private boolean checkVerifyConditions() {
        if (selectedSecretVerify == null) {
            Utils.showWarningAlert("Symmetric algorithm file is not selected!",
                    "Click on LOAD SECRET KEY to load a file");
            return false;
        }
        if (selectedPrivate == null) {
            Utils.showWarningAlert("ASymmetric algorithm file is not selected!",
                    "Click on LOAD PRIVATE KEY to load a file");
            return false;
        }
        if (selectedEnvelopeVerify == null) {
            Utils.showWarningAlert("Envelope that is verified is not selected",
                    "Click on SELECT ENVELOPE to load a file");
            return false;
        }
        if (selectedStampVerify == null) {
            Utils.showWarningAlert("Stamp that is verified is not selected",
                    "Click on SELECT STAMP to load a file");
            return false;
        }
        return true;
    }

    @FXML
    private void onVerify() {
        if (!checkVerifyConditions())
            return;

        try {
            CryptoHash hasher = new SHA3CryptoHash(hashSizeVerifyCombo.getSelectionModel().getSelectedItem());
            SymmetricCryptoTool tool = null;
            String selectedTool = symVerifyCombo.getSelectionModel().getSelectedItem();
            if (selectedTool.equals("3DES")) {
                tool = new DESCryptoTool();
            } else {
                tool = new AESCrpytoTool();
            }
            CryptoParser parser = new CryptoParser(selectedSecretVerify.toString());
            parser.parseFile();
            tool.readSecretKey(parser);
            parser.clear();

            DigitalSignature ds = new DigitalSignature(tool, hasher);
            parser.clear();
            parser.setFilePath(selectedStampVerify.toString());
            parser.parseFile();
            parser.addPair("File name", selectedEnvelopeVerify.toString());
            boolean verified = ds.verify(parser);

            if (verified) {
                Utils.showConfirmationAlert(
                        "Digital stamp",
                        "Digital stamp successfully verified!",
                        "Do you want to take a look at the stamp?",
                        parser
                );
            } else {
                Utils.showConfirmationAlert(
                        "Digital stamp",
                        "Digital stamp verification FAILED!",
                        "Do you want to take a look at the stamp?",
                        parser
                );
            }

        } catch (Exception e) {
            Utils.showErrorAlert("Verification of digital stamp FAILED!",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onSelectFile() {
        selectedFile = Utils.chooseFile(true,
                "Choose file for which digital stamp and envelope will be created");
        Utils.setLabelFor(labelFileCreate, selectedFile);
    }

    @FXML
    private void onSelectEnvelopeDestination() {
        selectedEnvelopeDestination = Utils.chooseFile(false,
                "Select envelope destination");
        Utils.setLabelFor(labelEnvelopeCreate, selectedEnvelopeDestination);
    }

    @FXML
    private void onSelectStampDestination() {
        selectedStampDestination = Utils.chooseFile(false,
                "Select stamp destination");
        Utils.setLabelFor(labelStampCreate, selectedStampDestination);
    }

    @FXML
    private void onSelectEnvelopeVerify() {
        selectedEnvelopeVerify = Utils.chooseFile(true,
                "Choose the envelope that will be verified");
        Utils.setLabelFor(labelEnvelopeVerify, selectedEnvelopeVerify);
    }

    @FXML
    private void onSelectStampVerify() {
        selectedStampVerify = Utils.chooseFile(true,
                "Choose the signature that will be verified");
        Utils.setLabelFor(labelStampVerify, selectedStampVerify);
    }

    @FXML
    private void onLoadSecretCreate() {
        selectedSecretCreate = Utils.chooseFile(true,
                "Choose a symmetric secret key file");
    }

    @FXML
    private void onLoadSecretVerify() {
        selectedSecretVerify = Utils.chooseFile(true,
                "Choose a symmetric secret key file");
    }

    @FXML
    private void onLoadPublic() {
        selectedPublic = Utils.chooseFile(true,
                "Choose a asymmetric public key file");
    }

    @FXML
    private void onLoadPrivate() {
        selectedPrivate = Utils.chooseFile(true,
                "Choose a asymmetric private key file");
    }

    @FXML
    private void showMoreSecretCreate() {
        if (selectedSecretCreate != null)
            Utils.showMore("Secret key", selectedSecretCreate);
    }

    @FXML
    private void showMoreSecretVerify() {
        if (selectedSecretVerify != null)
            Utils.showMore("Secret key", selectedSecretVerify);
    }

    @FXML
    private void showMorePublic() {
        if (selectedPublic != null)
            Utils.showMore("Public key", selectedPublic);
    }

    @FXML
    private void showMorePrivate() {
        if (selectedPrivate != null)
            Utils.showMore("Private key", selectedPrivate);
    }

}
