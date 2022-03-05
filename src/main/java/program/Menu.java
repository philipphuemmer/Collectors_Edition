package program;

import controller.MenuController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.stage.StageStyle;

import java.util.Objects;

public class Menu extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        MenuController.menuStage = primaryStage;

        primaryStage.initStyle(StageStyle.UNDECORATED);

        //Menu will open using the fxml file Menu.fxml
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxmlFiles/Menu.fxml")));

        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = MenuController.menuStage.getX() - event.getScreenX();
                yOffset = MenuController.menuStage.getY() - event.getScreenY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MenuController.menuStage.setX(event.getScreenX() + xOffset);
                MenuController.menuStage.setY(event.getScreenY() + yOffset);
            }
        });

        MenuController.menuStage.setTitle("Collector's Edition");
        MenuController.menuStage.setScene(new Scene(root));
        MenuController.menuStage.show();
    }
}
