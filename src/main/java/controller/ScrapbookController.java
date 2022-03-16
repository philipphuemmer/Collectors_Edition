package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.ResourceBundle;

import javafx.util.Callback;
import program.Collectable;
import program.ResizeHelper;
import program.TextAreaTableCell;
import program.UpdateCollectable;

public class ScrapbookController implements Initializable {

    // Stage
    public Stage scrapbookStage;
    // Name of the Scrapbook
    public String name;
    //
    public MenuController menuController;

    //
    @FXML
    private Button saveButton;
    @FXML
    private Button backToMenuButton;
    @FXML
    private Button saveLocationButton;
    //
    @FXML
    private Button changeImageButton;

    @FXML
    private Button addCollectableButton;

    @FXML
    private Button deleteCollectableButton;
    // Title Bar as HBox
    @FXML
    private HBox titleBar;
    // Title Bar Close Button
    @FXML
    private Button closeButton;
    // Title Bar Minimize Button
    @FXML
    private Button minimizeButton;
    // Title Bar Maximize Button
    @FXML
    private Button maximizeButton;
    //
    @FXML
    public TableView<Collectable> tableView;

    // Columns that were selected in the CheckComboBox
    public ObservableList<String> createColumns = FXCollections.observableArrayList();
    // All Columns (if you want to add columns later)
    public ObservableList<String> allColumns = FXCollections.observableArrayList();
    // All Collectables (Rows) of the TableView
    private ObservableList<Collectable> allCollectables = FXCollections.observableArrayList();
    // BooleanProperty for the SaveButton
    private final BooleanProperty dirtyFlag = new SimpleBooleanProperty();
    //
    private String saveFilePath;
    // DataFile of the TableView
    private File saveFile;
    // Data of the TableView
    private StringBuilder data;


    // Used for Drag and Drop of TableView Rows
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    private double xOffset = 0;
    private double yOffset = 0;
    private boolean fullscreen = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        saveButton.disableProperty().bind( dirtyFlag.not() );

        tableView.setRowFactory(tv -> {
            TableRow<Collectable> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (! row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer)db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    Collectable draggedPerson = tableView.getItems().remove(draggedIndex);

                    int dropIndex ;

                    if (row.isEmpty()) {
                        dropIndex = tableView.getItems().size() ;
                    } else {
                        dropIndex = row.getIndex();
                    }

                    tableView.getItems().add(dropIndex, draggedPerson);

                    event.setDropCompleted(true);
                    tableView.getSelectionModel().select(dropIndex);
                    event.consume();

                    dirtyFlag.set(true);
                }
            });

            return row ;
        });
    }

    // Get the Name of the Scrapbook
    public String getName() {
        return name;
    }

    // Set the Name of the Scrapbook
    public void setName(String name) {
        this.name = name;
    }

    public void openMenu() {
        try {
            scrapbookStage.close();
            FXMLLoader loaderMenu = new FXMLLoader((Objects.requireNonNull(getClass().getClassLoader().getResource("fxmlFiles/Menu.fxml"))));
            Parent rootMenu = loaderMenu.load();
            MenuController menuController = loaderMenu.getController();
            MenuController.menuStage.setScene(new Scene(rootMenu));
            MenuController.menuStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Set the Columns, selected in the CeckComboBox
    public void setColumnChoices(ObservableList<String> columns) {
        createColumns = columns;
    }

    // Set the Columns in the TableView
    public void setColumns() {
        TableColumn<Collectable, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setMinWidth(40);
        idColumn.setMaxWidth(40);
        idColumn.setResizable(false);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.getStyleClass().add("textColumn");
        idColumn.setSortable(false);
        tableView.getColumns().add(idColumn);

        idColumn.setSortType(TableColumn.SortType.ASCENDING);
        tableView.getSortOrder().add(idColumn);

        TableColumn<Collectable, String> imageTableColumn = new TableColumn<>("Image");

        imageTableColumn.setMinWidth(105);
        imageTableColumn.setMaxWidth(105);
        imageTableColumn.setResizable(false);
        imageTableColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));
        imageTableColumn.getStyleClass().add("imageColumn");
        imageTableColumn.setSortable(false);

        tableView.getColumns().add(imageTableColumn);

        for (int i = 0; i < createColumns.size(); i++) {
            final int j = i;
            TableColumn<Collectable, String> column = new TableColumn<>(createColumns.get(i));
            column.setPrefWidth(150);
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Collectable, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Collectable, String> param) {
                    return new SimpleStringProperty(String.valueOf(param.getValue().getContent().get(j)));
                }
            });
            column.setCellFactory(TextAreaTableCell.forTableColumn());
            column.getStyleClass().add("textColumn");
            column.setOnEditCommit(dataEditCommitHandler);
            column.setSortable(false);
            tableView.getColumns().add(column);
            allColumns.add(createColumns.get(i));
        }
    }

    // Add 5 Rows (Collectables)
    public void addRows() {

        int x = tableView.getColumns().size() - 2;
        ObservableList<String> data = FXCollections.observableArrayList();
        String ergebnis = "";
        for (int i = 0; i < x; i++) {
            data.add(ergebnis);
        }

        for (int j = 0; j < 5; j++) {
            ImageView photo = new ImageView(new Image("imageFiles/noImagePlaceholder.jpg"));
            photo.setFitHeight(100);
            photo.setFitWidth(100);
            Collectable collectable = new Collectable(data, allColumns, photo);
            allCollectables.add(collectable);
            collectable.setId(allCollectables.indexOf(collectable) + 1);
        }
        tableView.setItems(allCollectables);
    }

    static <T, S> T getObjectAtEvent(TableColumn.CellEditEvent<T, S> evt) {

        TableView<T> tableView = evt.getTableView();

        ObservableList<T> items = tableView.getItems();

        TablePosition<T,S> tablePosition = evt.getTablePosition();

        int rowId = tablePosition.getRow();

        T obj = items.get(rowId);

        return obj;
    }

    // EventHandler for Editing a Cell
    private final EventHandler<TableColumn.CellEditEvent<Collectable,String> > dataEditCommitHandler = (evt) -> {
        if( !dirtyFlag.get() ) {
            dirtyFlag.set(true);
            //System.out.println("true");
        }
        Collectable collectable = getObjectAtEvent(evt);

        ObservableList<StringProperty> oldData = FXCollections.observableArrayList();
        for (String s: collectable.getContent()) {
            oldData.add(new SimpleStringProperty(s));
        }

        for (int i = 0; i < collectable.getContent().size(); i++) {
            if (evt.getTableColumn().getText().equals(collectable.columns.get(i))) {
                collectable.setContent(evt.getNewValue(), i);
            }
        }
        //updateList.add( new UpdateCollectable(collectable.getName(), collectable.getYear(), collectable.getPrice(), oldData.get(0).get(), oldData.get(1).get(), oldData.get(2).get()));
    };

    @FXML
    public void save() {
        try {
            createSaveFile();
            dirtyFlag.set(false);

            FileOutputStream outputStream = new FileOutputStream(saveFile);
            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter writer = new BufferedWriter(streamWriter);

            // First Part: Number of Columns (Excluding Image Columns)
            writer.write(String.valueOf(allColumns.size()));
            writer.newLine();

            // Second Part: Names of all Columns
            for (String allColumn : allColumns) {
                writer.write(allColumn + ";");
                //writer.newLine();
            }

            writer.newLine();

            // Third Part: ID, Path to Image and Content of all Rows (Collectables)
            for (Collectable collectable : allCollectables) {
                ObservableList<String> content = collectable.getContent();
                String imagePath = collectable.getPhoto().getImage().getUrl();

                writer.write(collectable.getId() + ";");
                writer.write(imagePath + ";");

                int counterString = 1;
                for (String stringProperty : content) {
                    if (counterString < content.size()) {
                        writer.write(stringProperty + ";");
                        counterString++;
                    } else {
                        writer.write(stringProperty);
                    }
                }
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createSaveFile() {
        try {
            String saveFileName = name + ".txt";
            saveFilePath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Collector's Edition" + File.separator + "Scrapbooks" + File.separator + saveFileName;
            saveFile = new File(saveFilePath);
            saveFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadData(ObservableList<Collectable> allCollectables) {
        this.allCollectables = allCollectables;
        tableView.setItems(this.allCollectables);
    }

    @FXML
    public void backToMenu() {
        String saveFileName = name + ".txt";
        saveFilePath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Collector's Edition" + File.separator + "Scrapbooks" + File.separator + saveFileName;
        saveFile = new File(saveFilePath);

        openMenu();
        /*if (saveFile.exists()) {
            openMenu();
        }
        if (!saveFile.exists()) {
            ObservableList<String> scrapbooks = menuController.scrapbooks;
            scrapbooks.remove(name);
            menuController.scrapbooks = scrapbooks;
            menuController.updateSaveFile();
            openMenu();
        }*/
    }

    @FXML
    public void changeImage() {
        Collectable collectable = tableView.getSelectionModel().getSelectedItem();

        FileChooser fileChooser = new FileChooser();
        File newImage = fileChooser.showOpenDialog(scrapbookStage);

        if (newImage != null) {
            ImageView newPhoto = new ImageView(new Image(newImage.toURI().toString()));
            newPhoto.setFitHeight(100);
            newPhoto.setFitWidth(100);
            collectable.setPhoto(newPhoto);
            dirtyFlag.set(true);
        }
    }

    @FXML
    public void addCollectable() {
        ObservableList<String> dataCollectable = FXCollections.observableArrayList();
        for (int i = 0; i < allColumns.size(); i++) {
            dataCollectable.add("");
        }
        ImageView photo = new ImageView(new Image("imageFiles/noImagePlaceholder.jpg"));
        photo.setFitHeight(100);
        photo.setFitWidth(100);
        Collectable collectable = new Collectable(dataCollectable, allColumns, photo);
        allCollectables.add(collectable);
        collectable.setId(allCollectables.indexOf(collectable) + 1);
        updateID();
        tableView.setItems(allCollectables);
        dirtyFlag.set(true);
    }

    @FXML
    public void deleteCollectable() {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Collectable collectable = tableView.getSelectionModel().getSelectedItem();
            allCollectables.remove(collectable);
            updateID();
            tableView.setItems(allCollectables);
            dirtyFlag.set(true);
        }
    }

    // Closing the Application
    @FXML
    public void closeApplication() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    // Minimize the Application
    @FXML
    public void maximizeApplication() {
        if (!fullscreen) {
            scrapbookStage.setFullScreen(true);
            fullscreen = true;
        } else {
            scrapbookStage.setFullScreen(false);
            fullscreen = false;
        }
    }

    // Minimize the Application
    @FXML
    public void minimizeApplication() {
        scrapbookStage.setIconified(true);
    }

    @FXML
    public void doubleClickTitleBar(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            maximizeApplication();
        }
    }

    @FXML
    public void draggedMouse (MouseEvent event) {
        //Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scrapbookStage.setX(event.getScreenX() - xOffset);
        scrapbookStage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    public void pressedMouse (MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
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

    public void updateID() {
        for (Collectable collectable: allCollectables) {
            collectable.setId(allCollectables.indexOf(collectable) + 1);
        }
    }
}


