package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import program.Collectable;
import program.ResizeHelper;


import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class MenuController implements Initializable{

    public static Stage menuStage;
    public CreateScrapbookController createScrapbookController;
    public ScrapbookController scrapbookController;
    public ErrorController errorController;

    @FXML
    private Button closeButton;
    @FXML
    private Button minimizeButton;
    @FXML
    public Button newCollectionButton;
    @FXML
    public Button newScrapbookButton;
    @FXML
    public Button openButton;
    @FXML
    public Button galleryButton;
    @FXML
    public VBox openScrapbookVBox;
    @FXML
    public ListView<String> scrapbookNames;
    @FXML
    public Button openScrapbook;
    @FXML
    public Button deleteScrapbook;

    // List of all Scrapbooks
    public ObservableList<String> scrapbooks = FXCollections.observableArrayList();
    //
    private File saveDirectory;
    //
    private String saveDirectoryPath;
    //
    private File saveFile;
    // Counter of existing Scrapbooks
    private int scrapbookCounter;
    //
    private HashMap<String, Boolean> isInitialized = new HashMap<>();
    // Directory Path
    public String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Collector's Edition";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setSaveDirectoryonDesktop();
        createSaveFile();
        readSaveFile();
        openScrapbookVBox.setVisible(false);

        for (String s : scrapbooks) {
            isInitialized.put(s, false);
        }
    }

    // If Button newScrapbook is clicked, a new Scrapbook will be created
    @FXML
    public void newScrapbook() {
            try {
                createScrapbookController = new CreateScrapbookController();
                //creatingScrapbookController.menuController = this;

                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxmlFiles/CreateScrapbook.fxml")));
                createScrapbookController.createScrapbookStage = new Stage();
                createScrapbookController.createScrapbookStage.initStyle(StageStyle.UNDECORATED);
                createScrapbookController.createScrapbookStage.setScene(new Scene(root));
                createScrapbookController.createScrapbookStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    // Show list of all Scrapbooks when openButton is clicked
    @FXML
    public void open() {
        int counter = 1;
        // String saveFilePath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Collector's Edition" + File.separator + saveFileName;
        java.util.Collections.sort(scrapbooks);
        scrapbookNames.setItems(scrapbooks);
        openScrapbookVBox.setVisible(true);
    }

    // Create "Collector's Edition" and "Scrapbooks" Folder if not existent
    public void setSaveDirectoryonDesktop() {
        saveDirectoryPath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Collector's Edition" + File.separator + "Scrapbooks";
        //saveDirectoryPath = System.getenv("ProgramFiles") + File.separator + "Collector's Edition";
        saveDirectory = new File(saveDirectoryPath);
        if (!saveDirectory.exists()) {
            saveDirectory.mkdirs();
        }
    }

    // Create a saveFile for the Program in "Collector's Edition" if not existent
    public void createSaveFile() {
        try {
            String saveFileName = "saveFile.txt";
            String saveFilePath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Collector's Edition" + File.separator + saveFileName;

            File checkFile = new File(saveFilePath);
            if (checkFile.exists()) {
                return;
            }

            saveFile = new File(saveFilePath);
            FileOutputStream outputStream = new FileOutputStream(saveFile);
            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
            Writer writer = new BufferedWriter(streamWriter);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update saveFile (e.g. if saveButton is clicked) -> not complete
    public void updateSaveFile() {
        try {
            String saveFileName = "saveFile.txt";
            String saveFilePath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Collector's Edition" + File.separator + saveFileName;
            saveFile = new File(saveFilePath);

            FileOutputStream outputStream = new FileOutputStream(saveFile);
            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter writer = new BufferedWriter(streamWriter);

            // Number of Scrapbooks
            writer.write(String.valueOf(scrapbooks.size()));
            writer.newLine();

            // Name of all Scrapbooks
            for (String s : scrapbooks) {
                writer.write(s);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read saveFile when a scrapbook is opened -> not complete
    public void readSaveFile() {
        try {
            String saveFileName = "saveFile.txt";
            String saveFilePath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Collector's Edition" + File.separator + saveFileName;

            String line;
            int counter = 1;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(saveFilePath));

            while ((line = bufferedReader.readLine()) != null) {
                if (counter == 1) {
                    if (line.matches("[0-9]+")) {
                        scrapbookCounter++;
                        counter++;
                        continue;
                    }
                }
                if (counter == 2) {
                    scrapbooks.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read saveFile for selected Scrapbook and load Scrapbook
    @FXML
    public void openScrapbook() {
        try {
            String scrapbook = scrapbookNames.getSelectionModel().getSelectedItem();

            // Get the ScrapbookController
            FXMLLoader loader = new FXMLLoader((Objects.requireNonNull(getClass().getClassLoader().getResource("fxmlFiles/Scrapbook.fxml"))));
            Parent root = loader.load();

            scrapbookController = loader.getController();
            scrapbookController.setName(scrapbook);

            String saveFileName = scrapbook + ".txt";
            String saveFilePath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Collector's Edition" + File.separator + "Scrapbooks" + File.separator + saveFileName;

            String line;
            int counter = 1;
            int numberColumns = 0;
            int counterColumns = 1;
            int counterCollection = 1;
            ObservableList<Collectable> collectables = FXCollections.observableArrayList();
            ObservableList<String> data = FXCollections.observableArrayList();
            ObservableList<String> columns = FXCollections.observableArrayList();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(saveFilePath));

            while ((line = bufferedReader.readLine()) != null) {
                // First Part: Number of Columns (Excluding Image Column)
                if (counter == 1) {
                    if (line.matches("[0-9]+")) {
                        numberColumns = Integer.parseInt(line);
                        counter++;
                        continue;
                    }
                }
                // Second Part: Names of all Columns
                if (counter == 2) {
                    columns.addAll(line.split(";"));
                    System.out.println(columns.size());
                    counter++;
                    continue;

                        /*columns.add(line);
                        if (counterColumns == numberColumns) {
                            counter++;
                            continue;
                        }
                        counterColumns++;*/

                }
                // Third Part: Path to Image and Content of all Rows (Collectables)
                if (counter == 3) {
                    data.addAll(line.split(";", -1));

                    int id = Integer.parseInt(data.get(0));
                    String imagePath = data.get(1);

                    data.remove(0);
                    data.remove(imagePath);

                    ImageView photo = new ImageView(new Image(imagePath));
                    photo.setFitHeight(100);
                    photo.setFitWidth(100);

                    Collectable collectable = new Collectable(data, columns, photo);
                    collectable.setId(id);
                    collectables.add(collectable);
                    data.clear();


                    /*if (data.size() != numberColumns) {
                        data.add(line);
                        if (data.size() == numberColumns) {
                            Collectable lastCollectable = new Collectable(data, columns);
                            collectables.add(lastCollectable);
                            data.clear();
                        }
                    } else {
                        Collectable collectable = new Collectable(data, columns);
                        collectables.add(collectable);
                        data.clear();
                        data.add(line);
                    }*/
                }
            }

            for (Collectable collectable: collectables) {
                System.out.println(collectable.toString());
            }

            // Closing Menu Stage
            menuStage.close();

            // Set Columns and Rows for the selected Scrapbook
            scrapbookController.setColumnChoices(columns);
            scrapbookController.setColumns();
            if (!isInitialized.get(scrapbookController.getName())) {
                scrapbookController.loadData(collectables);
            }

            // Open New Scrapbook
            scrapbookController.scrapbookStage = new Stage();
            scrapbookController.scrapbookStage.initStyle(StageStyle.UNDECORATED);
            scrapbookController.scrapbookStage.setScene(new Scene(root));
            scrapbookController.scrapbookStage.setTitle(scrapbookController.name);

            ResizeHelper.addResizeListener(scrapbookController.scrapbookStage);

            scrapbookController.scrapbookStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteScrapbook() {
        String scrapbook = scrapbookNames.getSelectionModel().getSelectedItem();

        scrapbooks.removeIf(s -> s.matches(scrapbook));

        updateSaveFile();

        String deletePath = path + File.separator + "Scrapbooks" + File.separator + scrapbook + ".txt";

        File deleteFile = new File(deletePath);
        deleteFile.delete();
    }

    public boolean addScrapbookName(String name) {
        if (scrapbooks.contains(name)) {
            try {
                FXMLLoader loader = new FXMLLoader((Objects.requireNonNull(getClass().getClassLoader().getResource("fxmlFiles/Error.fxml"))));
                Parent root = loader.load();
                errorController = loader.getController();
                errorController.setErrorTextField("Name already exists!");
                errorController.errorStage = new Stage();
                errorController.errorStage.setScene(new Scene(root));
                errorController.errorStage.setTitle("Error");
                errorController.errorStage.show();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!scrapbooks.contains(name)) {
            scrapbooks.add(name);
        }
        return false;
    }

    // Closing the Application
    public void closeApplication() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    // Minimize the Application
    public void minimizeApplication() {
        menuStage.setIconified(true);
    }

    public HashMap<String, Boolean> getIsInitialized() {
        return isInitialized;
    }

    public void setIsInitialized(HashMap<String, Boolean> isInitialized) {
        this.isInitialized = isInitialized;
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
