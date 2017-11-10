package Main;

import Controller.Controller;
import Model.Model;
import View.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {

        View view = new View();
        Scene scene = new Scene(view);
        Model model = new Model();
        Controller controller = new Controller();
        
        controller.link(model,view);

        primaryStage.setTitle("DonutDownloader v1."+model.ID);
        primaryStage.getIcons().add(new Image("Main/Donuts.png"));

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> Platform.exit());
    }
}
