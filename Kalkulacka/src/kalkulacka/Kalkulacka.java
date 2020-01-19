package kalkulacka;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * Tato trieda bola vygenerovana pri vytvoreni JavaFX projektu.
 * Nacitava fxml subor, ktory je zhotoveny pomocou SceneBuilder
 * programu. Jednoducho paruje frontend s backendom.
 */
public class Kalkulacka extends Application {

    @Override	//anotacia
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Bitcoin Calculator");  
    }

    public static void main(String[] args) {
        launch(args);	
    }
}
