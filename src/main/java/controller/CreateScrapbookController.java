package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.CheckComboBox;
import program.ResizeHelper;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class CreateScrapbookController implements Initializable {

    public Stage createScrapbookStage;
    public ScrapbookController scrapbookController;
    public MenuController menuController;

    @FXML
    private Button closeButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private ListView<String> columnList;
    @FXML
    private ComboBox<String> generalColumnsCombo;
    @FXML
    public Button createButton;
    @FXML
    public Button cancelButton;
    @FXML
    private TextField scrapbookName;

    //
    public ObservableList<String> generalColumns = FXCollections.observableArrayList();
    //
    public ObservableList<String> allColumnChoices = FXCollections.observableArrayList();
    //
    private File saveFile;
    //
    private File saveDirectory;
    //
    private String saveDirectoryPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setGeneralColumns();
        columnList.setPlaceholder(new Label("No Columns selected!"));
        generalColumnsCombo.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty) ;
                if (empty || item == null) {
                    setText("Select General Column");
                } else {
                    setText(item);
                }
            }
        });
    }

    //
    @FXML
    public void create() {
        try {
            // Get the ScrapbookController
            FXMLLoader loader = new FXMLLoader((Objects.requireNonNull(getClass().getClassLoader().getResource("fxmlFiles/Scrapbook.fxml"))));
            Parent root = loader.load();
            scrapbookController = loader.getController();

            // Get Data from TextField Name
            scrapbookController.setName(scrapbookName.getText());

            FXMLLoader loaderMenu = new FXMLLoader((Objects.requireNonNull(getClass().getClassLoader().getResource("fxmlFiles/Menu.fxml"))));
            Parent rootMenu = loaderMenu.load();
            menuController = loaderMenu.getController();

            if (menuController.addScrapbookName(scrapbookController.getName())) {
                return;
            }
            menuController.getIsInitialized().put(scrapbookController.getName(), true);
            menuController.updateSaveFile();
            scrapbookController.menuController = menuController;

            // Close Menu
            MenuController.menuStage.close();

            // Close CreatingScrapbook
            Stage stage = (Stage) createButton.getScene().getWindow();
            stage.close();

            // Get Data from CheckComboBox
            scrapbookController.setColumnChoices(columnList.getItems());
            scrapbookController.setColumns();
            scrapbookController.addRows();

            // Open New Scrapbook
            scrapbookController.scrapbookStage = new Stage();
            scrapbookController.scrapbookStage.initStyle(StageStyle.UNDECORATED);
            scrapbookController.scrapbookStage.setScene(new Scene(root));
            scrapbookController.scrapbookStage.setTitle(scrapbookController.name);

            ResizeHelper.addResizeListener(scrapbookController.scrapbookStage);

            scrapbookController.scrapbookStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cancel() {
        // Close CreatingScrapbook
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.close();
    }

    private void setGeneralColumns() {
        generalColumns.add("Name");
        generalColumns.add("Year");
        generalColumns.add("Price");
        generalColumnsCombo.setItems(generalColumns);
    }

    @FXML
    public void addGeneralColumn() {
        if (generalColumnsCombo.getSelectionModel().getSelectedItem() != null) {
            String column = generalColumnsCombo.getSelectionModel().getSelectedItem();
            allColumnChoices.add(column);
            columnList.setItems(allColumnChoices);

            generalColumns.remove(column);
            generalColumnsCombo.setItems(generalColumns);
        }
    }

    @FXML
    public void addIndividualColumn() {
        if (!scrapbookName.getText().isEmpty()) {
            allColumnChoices.add(scrapbookName.getText());
            columnList.setItems(allColumnChoices);
            scrapbookName.clear();
        }
    }

    // Closing the Application
    public void closeApplication() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    // Minimize the Application
    public void minimizeApplication() {
        createScrapbookStage.setIconified(true);
    }

    @FXML
    public void changeWhiteCloseButton() {
        ImageView whiteCloseSign = new ImageView(new Image("imageFiles/WhiteCloseButton.png"));
        //whiteCloseSign.setPreserveRatio(preserveRatio);
        whiteCloseSign.setFitWidth(30);
        whiteCloseSign.setFitHeight(30);

        closeButton.setGraphic(whiteCloseSign);
        closeButton.setStyle("-fx-background-color: rgba(242, 38, 19, 1)");
    }

    @FXML
    public void changeRedCloseButton() {
        ImageView redCloseSign = new ImageView("imageFiles/RedCloseButton.png");
        redCloseSign.setFitWidth(30);
        redCloseSign.setFitHeight(30);

        closeButton.setGraphic(redCloseSign);
        closeButton.setStyle("-fx-background-color: transparent");
    }
}
