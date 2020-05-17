import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    static final int WIDTH = 800;
    static final int HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = FXMLLoader.load(getClass().getClassLoader().getResource("mainframe.fxml"));
        primaryStage.setTitle("Crypto App");
        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        primaryStage.show();
    }

}
