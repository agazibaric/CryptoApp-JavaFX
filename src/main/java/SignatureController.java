import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SignatureController implements Initializable {

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

    private File selectedSymFileCreate = new File("./Crypto Files/3des112cbc.txt");

    private File selectedSymFileVerify = new File("./Crypto Files/3des112cbc.txt");;

    private File selectedFile = new File("./Crypto Files/lorem.txt");;

    private File selectedFileVerify = new File("./Crypto Files/lorem.txt");

    private File selectedDestination = new File("./Crypto Files/signature-lorem-sha3-224-3des112cbc.txt");

    private File selectedSignature = new File("./Crypto Files/signature-lorem-sha3-224-3des112cbc.txt");
    @FXML
    private Label labelFileCreate;
    @FXML
    private Label labelDestCreate;
    @FXML
    private Label labelFileVerify;
    @FXML
    private Label labelSignature;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initHash();
        initSymAlgorithm();
        setLabels();
    }

    private void setLabels() {
        Utils.setLabelFor(labelFileCreate, selectedFile);
        Utils.setLabelFor(labelDestCreate, selectedDestination);
        Utils.setLabelFor(labelFileVerify, selectedFileVerify);
        Utils.setLabelFor(labelSignature, selectedSignature);
    }

    private void initSymAlgorithm() {
        symCreateCombo.getItems().addAll("3DES", "AES");
        symCreateCombo.getSelectionModel().selectFirst();
        symVerifyCombo.getItems().addAll("3DES", "AES");
        symVerifyCombo.getSelectionModel().selectFirst();
    }

    private void initHash() {
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
            CryptoHash hasher = new SHA3CryptoHash(hashSizeCreateCombo.getSelectionModel().getSelectedItem());
            SymmetricCryptoTool tool = null;
            String selectedTool = symCreateCombo.getSelectionModel().getSelectedItem();
            if (selectedTool.equals("3DES")) {
                tool = new DESCryptoTool();
            } else {
                tool = new AESCrpytoTool();
            }
            CryptoParser parser = new CryptoParser(selectedSymFileCreate.toString());
            parser.parseFile();
            tool.readSecretKey(parser);
            parser.clear();

            DigitalSignature ds = new DigitalSignature(tool, hasher);
            ds.storeData(parser, selectedFile.toString());
            parser.setFilePath(selectedDestination.toString());
            parser.writeToFile();

            Utils.showConfirmationAlert(
                    "Digital signature",
                    "Digital signature successfully created!",
                    "Do you want to take a look at the signature?",
                    parser
            );
        } catch (Exception e) {
            Utils.showErrorAlert("Creation of digital signature failed!", e.getMessage());
        }
    }

    private boolean checkCreateConditions() {
        if (selectedSymFileCreate == null) {
            Utils.showWarningAlert("Symmetric algorithm file is not selected!",
                    "Click on LOAD ALGORITHM to load a file");
            return false;
        }
        if (selectedFile == null) {
            Utils.showWarningAlert("File for which digital signature will be created  is not selected",
                    "Click on SELECT FILE to load a file");
            return false;
        }
        if (selectedDestination == null) {
            Utils.showWarningAlert("Destination where to store digital signature is not selected",
                    "Click on CHOOSE DESTINATION to load a file");
            return false;
        }
        return true;
    }

    private boolean checkVerifyConditions() {
        if (selectedSymFileVerify == null) {
            Utils.showWarningAlert("Symmetric algorithm file is not selected!",
                    "Click on LOAD ALGORITHM to load a file");
            return false;
        }
        if (selectedFileVerify == null) {
            Utils.showWarningAlert("Original file for which digital signature will be verified  is not selected",
                    "Click on SELECT FILE to load a file");
            return false;
        }
        if (selectedSignature == null) {
            Utils.showWarningAlert("Signature that is verified is not selected",
                    "Click on SELECT SIGNATURE to load a file");
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
            CryptoParser parser = new CryptoParser(selectedSymFileVerify.toString());
            parser.parseFile();
            tool.readSecretKey(parser);
            parser.clear();

            DigitalSignature ds = new DigitalSignature(tool, hasher);
            parser.clear();
            parser.setFilePath(selectedSignature.toString());
            parser.parseFile();
            parser.addPair("File name", selectedFileVerify.toString());
            boolean verified = ds.verify(parser);

            if (verified) {
                Utils.showConfirmationAlert(
                        "Digital signature",
                        "Digital signature successfully verified!",
                        "Do you want to take a look at the signature?",
                        parser
                );
            } else {
                Utils.showConfirmationAlert(
                        "Digital signature",
                        "Digital signature verification FAILED",
                        "Do you want to take a look at the signature?",
                        parser
                );
            }

        } catch (Exception e) {
            Utils.showErrorAlert("Creation of digital signature failed!",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onSelectFile() {
        selectedFile = Utils.chooseFile(true,
                "Choose file for which digital signature will be created");
        Utils.setLabelFor(labelFileCreate, selectedFile);
    }

    @FXML
    private void onSelectFileVerify() {
        selectedFileVerify = Utils.chooseFile(true,
                "Choose original file for which digital signature will be verified");
        Utils.setLabelFor(labelFileVerify, selectedFileVerify);
    }

    @FXML
    private void onSelectDestination() {
        selectedDestination = Utils.chooseFile(false,
                "Choose a destination where the digital signature will be stored");
        Utils.setLabelFor(labelDestCreate, selectedDestination);
    }

    @FXML
    private void onSelectSignature() {
        selectedSignature = Utils.chooseFile(true,
                "Choose the signature that will be verified");
        Utils.setLabelFor(labelSignature, selectedSignature);
    }

    @FXML
    private void onLoadAlgCreate() {
        selectedSymFileCreate = Utils.chooseFile(true,
                "Choose a symmetric algorithm file");
    }

    @FXML
    private void onLoadAlgVerify() {
        selectedSymFileVerify = Utils.chooseFile(true,
                "Choose a symmetric algorithm file");
    }

    @FXML
    private void showMoreCreate() {
        if (selectedSymFileCreate != null)
            Utils.showMore("Secret key", selectedSymFileCreate);
    }

    @FXML
    private void showMoreVerify() {
        if (selectedSymFileVerify != null)
            Utils.showMore("Secret key", selectedSymFileVerify);
    }

}
