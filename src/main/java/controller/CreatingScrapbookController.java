package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CreatingScrapbookController implements Initializable {

    public Stage creatingScrapbookStage;
    public ScrapbookController scrapbookController;
    public MenuController menuController;

    @FXML
    public Button createButton;
    @FXML
    public Button cancelButton;
    @FXML
    private TextField scrapbookName;
    @FXML
    private CheckComboBox<String> columnChoice;

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
        setColumnChoices();
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
            scrapbookController.setColumnChoices(columnChoice.getCheckModel().getCheckedItems());
            scrapbookController.setColumns();
            scrapbookController.addRows();

            // Open New Scrapbook
            scrapbookController.scrapbookStage = new Stage();
            scrapbookController.scrapbookStage.setScene(new Scene(root));
            scrapbookController.scrapbookStage.setTitle(scrapbookController.name);
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

    /*@FXML
    public void setSaveLocationButton() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.setTitle("Save");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            saveFile = fileChooser.showSaveDialog(creatingScrapbookStage);
            if (saveFile != null) {
                createSaveFile(saveFile);
            }
            System.out.println(saveFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    // Set the Save Location and Create the Save File
    /*@FXML
    public void setSaveLocation() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extensionFilter);
        ScrapbookController.file = fileChooser.showSaveDialog(creatingScrapbookStage);

        if (ScrapbookController.file != null) {
            createSaveFile(ScrapbookController.getContent(), ScrapbookController.file);
        }
    }

    private void createSaveFile(String content, File file) {
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
            saveLocationIsSet = true;
        } catch (IOException e) {
            Logger.getLogger(CreatingScrapbookController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    */

    private void setColumnChoices() {
        allColumnChoices.clear();
        allColumnChoices.add("Name");
        allColumnChoices.add("Year");
        allColumnChoices.add("Price");
        columnChoice.getItems().addAll(allColumnChoices);
    }
}
