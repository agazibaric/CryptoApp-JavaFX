import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public class Utils {

    public static String readFile(String filename) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            String data = IOUtils.toString(fis, "UTF-8");
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String byteToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] hexToByte(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    public static void showFileDialog(String title, CryptoParser parser) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText("The content of the file:");

        TextArea textArea = new TextArea(parser.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setExpanded(true);

        alert.showAndWait();
    }

    public static void showWarningAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showConfirmationAlert(String title, String header, String content, CryptoParser parser) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Utils.showFileDialog(title, parser);
        }
    }

    public static void showMore(String title, File file) {
        CryptoParser parser = new CryptoParser(file.toString());
        parser.parseFile();
        showFileDialog(title, parser);
    }

    public static File chooseFile(boolean existingFile, String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File("./Crypto Files"));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        if (existingFile) {
            return fileChooser.showOpenDialog(null);
        }
        return fileChooser.showSaveDialog(null);
    }

    public static void setLabelFor(Label label, File file) {
        label.setText(file == null ? "File is not selected" : file.getName());
    }
}
