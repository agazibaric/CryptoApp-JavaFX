import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CryptController implements Initializable {

    @FXML
    private ComboBox<String> symEncryptCombo;

    @FXML
    private ComboBox<String> asymEncryptCombo;

    @FXML
    private ComboBox<String> symDecryptCombo;

    @FXML
    private ComboBox<String> asymDecryptCombo;

    @FXML
    private RadioButton symEncrypt;

    @FXML
    private RadioButton symDecrypt;

    @FXML
    private Label labelFile;
    @FXML
    private Label labelDestEncrypt;
    @FXML
    private Label labelEncrypted;
    @FXML
    private Label labelDestDecrypt;

    private File selectedSecretKeyEncrypt = new File("./Crypto Files/3des112cbc.txt");;
    private File selectedPublicKey = new File("./Crypto Files/rsa1024public.txt");;;
    private File selectedFile = new File("./Crypto Files/lorem.txt");;
    private File selectedDestinationEncrypt = new File("./Crypto Files/encrypted-lorem-3des112cbc.txt");;;

    private File selectedSecretKeyDecrypt = new File("./Crypto Files/3des112cbc.txt");;
    private File selectedPrivateKey = new File("./Crypto Files/rsa1024private.txt");;;
    private File selectedDestinationDecrypt = new File("./Crypto Files/decrypt-dest.txt");;;
    private File selectedCryptedFile = new File("./Crypto Files/encrypted-lorem-3des112cbc.txt");;;


    private void initCombos() {
        symEncryptCombo.getItems().addAll("3DES", "AES");
        symEncryptCombo.getSelectionModel().selectFirst();
        asymEncryptCombo.getItems().addAll("RSA");
        asymEncryptCombo.getSelectionModel().selectFirst();

        symDecryptCombo.getItems().addAll("3DES", "AES");
        symDecryptCombo.getSelectionModel().selectFirst();
        asymDecryptCombo.getItems().addAll("RSA");
        asymDecryptCombo.getSelectionModel().selectFirst();
    }

    private void setLabels() {
        Utils.setLabelFor(labelFile, selectedFile);
        Utils.setLabelFor(labelDestEncrypt, selectedDestinationEncrypt);
        Utils.setLabelFor(labelEncrypted, selectedCryptedFile);
        Utils.setLabelFor(labelDestDecrypt, selectedDestinationDecrypt);
    }

    @FXML
    private void showMoreSecretEncrypt() {
        if (selectedSecretKeyEncrypt != null)
            Utils.showMore("Secret key", selectedSecretKeyEncrypt);
    }

    @FXML
    private void showMorePublic() {
        if (selectedPublicKey != null)
            Utils.showMore("Public key", selectedPublicKey);
    }

    @FXML
    private void showMoreSecretDecrypt() {
        if (selectedSecretKeyDecrypt != null)
            Utils.showMore("Secret key", selectedSecretKeyDecrypt);
    }

    @FXML
    private void showMorePrivate() {
        if (selectedPrivateKey != null)
            Utils.showMore("Private key", selectedPrivateKey);
    }

    @FXML
    private void onEncrypt() {
        if (!checkEncryptConditions())
            return;

        try {
            CryptoParser parser = new CryptoParser(".");
            CryptoTool tool;
            if (symEncrypt.isSelected()) {
                tool = loadSymmetric(parser, selectedSecretKeyEncrypt, symEncryptCombo);
            } else {
                tool = loadAsymmetricPublic(parser);
            }
            parser.clear();
            CryptoFile encryptor = new CryptoFile(tool, selectedFile.toString());
            parser.setFilePath(selectedDestinationEncrypt.toString());
            encryptor.storeData(parser);
            parser.writeToFile();

            Utils.showConfirmationAlert(
                    "Encryption",
                    "File is successfully encrypted!",
                    "Do you want to take a look at the encrypted content?",
                    parser
            );
        } catch (Exception e) {
            Utils.showErrorAlert("Encryption of the file failed!", e.getMessage());
            e.printStackTrace();
        }
    }

    private CryptoTool loadSymmetric(CryptoParser parser, File selectedSecretKey,
                                     ComboBox<String> symCombo) {
        // Load symmetric algorithm
        parser.clear();
        parser.setFilePath(selectedSecretKey.toString());
        SymmetricCryptoTool stool;
        String selectedSym = symCombo.getSelectionModel().getSelectedItem();
        if (selectedSym.equals("3DES")) {
            stool = new DESCryptoTool();
        } else {
            stool = new AESCrpytoTool();
        }
        parser.parseFile();
        stool.readSecretKey(parser);
        return stool;
    }

    private CryptoTool loadAsymmetricPublic(CryptoParser parser) {
        // Load asymmetric algorithm
        parser.clear();
        parser.setFilePath(selectedPublicKey.toString());
        AsymmetricCryptoTool atool = new RSACryptoTool();
        parser.parseFile();
        atool.readPublicKey(parser);
        return atool;
    }

    private CryptoTool loadAsymmetricPrivate(CryptoParser parser) {
        // Load asymmetric algorithm
        parser.clear();
        parser.setFilePath(selectedPrivateKey.toString());
        AsymmetricCryptoTool atool = new RSACryptoTool();
        parser.parseFile();
        atool.readPrivateKey(parser);
        return atool;
    }

    @FXML
    private void onDecrypt() {
        if (!checkDecryptConditions())
            return;

        try {
            CryptoParser parser = new CryptoParser(".");
            CryptoTool tool;
            if (symDecrypt.isSelected()) {
                tool = loadSymmetric(parser, selectedSecretKeyDecrypt, symDecryptCombo);
            } else {
                tool = loadAsymmetricPrivate(parser);
            }
            parser.clear();


            parser.setFilePath(selectedCryptedFile.toString());
            parser.parseFile();
            String decryptedData = tool.decrypt(parser.getValue("Data"));
            parser.clear();
            parser.setWriteRaw(true);
            parser.setFilePath(selectedDestinationDecrypt.toString());
            parser.addPair("Original data", decryptedData);
            parser.writeToFile();

            Utils.showConfirmationAlert(
                    "Decryption",
                    "File is successfully decrypted!",
                    "Do you want to take a look at the decrypted content?",
                    parser
            );
        } catch (Exception e) {
            Utils.showErrorAlert("Decryption of the file failed!", e.getMessage());
            e.printStackTrace();
        }

    }

    private boolean checkEncryptConditions() {
        if (symEncrypt.isSelected()) {
            if (selectedSecretKeyEncrypt == null) {
                Utils.showWarningAlert("Symmetric algorithm file is not selected!",
                        "Click on LOAD SECRET KEY to load a file");
                return false;
            }
        } else {
            if (selectedPublicKey == null) {
                Utils.showWarningAlert("Asymmetric algorithm file is not selected!",
                        "Click on LOAD PUBLIC KEY to load a file");
                return false;
            }
        }
        if (selectedFile == null) {
            Utils.showWarningAlert("File for that will be encrypted is not selected!",
                    "Click on SELECT FILE to load a file");
            return false;
        }
        if (selectedDestinationEncrypt == null) {
            Utils.showWarningAlert("Destination where to save encrypted file is not selected!",
                    "Click on SELECT DESTINATION");
            return false;
        }
        return true;
    }

    private boolean checkDecryptConditions() {
        if (symDecrypt.isSelected()) {
            if (selectedSecretKeyDecrypt == null) {
                Utils.showWarningAlert("Symmetric algorithm file is not selected!",
                        "Click on LOAD SECRET KEY to load a file");
                return false;
            }
        } else {
            if (selectedPrivateKey == null) {
                Utils.showWarningAlert("Asymmetric algorithm file is not selected!",
                        "Click on LOAD PRIVATE KEY to load a file");
                return false;
            }
        }
        if (selectedDestinationDecrypt == null) {
            Utils.showWarningAlert("Destination where to save decrypted file is not selected!",
                    "Click on SELECT DESTINATION");
            return false;
        }
        return true;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initCombos();
        setLabels();
    }

    @FXML
    private void onSecretKeyEncryptLoad() {
        selectedSecretKeyEncrypt = Utils.chooseFile(true,
                "Choose symmetric algorithm secret key file");
    }

    @FXML
    private void onPublicKeyLoad() {
        selectedPublicKey = Utils.chooseFile(true,
                "Choose asymmetric algorithm public key file");
    }

    @FXML
    private void onSecretKeyDecryptLoad() {
        selectedSecretKeyDecrypt = Utils.chooseFile(true,
                "Choose symmetric algorithm secret key file");
    }

    @FXML
    private void onPrivateKeyLoad() {
        selectedPrivateKey = Utils.chooseFile(true,
                "Choose asymmetric algorithm private key file");
    }

    @FXML
    private void onSelectFileEncrypt() {
        selectedFile = Utils.chooseFile(true,
                "Choose file that will be encrypted");
        Utils.setLabelFor(labelFile, selectedFile);
    }

    @FXML
    private void onSelectFileDecrypt() {
        selectedCryptedFile = Utils.chooseFile(true,
                "Choose file that will be decrypted");
        Utils.setLabelFor(labelEncrypted, selectedCryptedFile);
    }

    @FXML
    private void onSelectDestinationEncrypt() {
        selectedDestinationEncrypt = Utils.chooseFile(false,
                "Choose save destination for encrypted file");
        Utils.setLabelFor(labelDestEncrypt, selectedDestinationEncrypt);
    }

    @FXML
    private void onSelectDestinationDecrypt() {
        selectedDestinationDecrypt = Utils.chooseFile(false,
                "Choose save destination for decrypted file");
        Utils.setLabelFor(labelDestDecrypt, selectedDestinationDecrypt);
    }

}
