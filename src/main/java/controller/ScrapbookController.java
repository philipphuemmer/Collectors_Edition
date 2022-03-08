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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private Button addCollectableButton;
    @FXML
    private Button changeImageButton;
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
    // Queue for Updated Collectables
    private final Queue updateList = new LinkedList<>();
    //
    private String saveFilePath;
    // DataFile of the TableView
    private File saveFile;
    // Data of the TableView
    private StringBuilder data;

    public TableColumn<Collectable, String> imageTableColumn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        saveButton.disableProperty().bind( dirtyFlag.not() );
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
        /*for (String s: createColumns) {
            TableColumn<Collectable, String> column = new TableColumn<>(s);
            column.setCellValueFactory(new PropertyValueFactory<>(s));//Factory(TextFieldTableCell.forTableColumn());
            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit( dataEditCommitHandler );
            tableView.getColumns().add(column);
            allColumns.add(s);
        }*/

        //imageTableColumn.setCellValueFactory();

        /*imageTableColumn = new TableColumn<>("imageFiles/test.png");
        imageTableColumn.setMinWidth(200);
        imageTableColumn.setCellFactory(new Callback<TableColumn<Collectable, Image>, TableCell<Collectable, Image>>() {
            public TableCell<Collectable, Image> call(TableColumn<Collectable, Image> param) {

                final ImageView imageView = new ImageView();
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);

                TableCell<Collectable, Image> cell = new TableCell<Collectable, Image>(){

                    @Override
                    protected void updateItem(Image item, boolean empty) {
                        if(item != null)
                            imageView.setImage(new Image("imageFiles/test.png"));
                    }

                };
                cell.setGraphic(imageView);
                return cell;
            }
        });
        imageTableColumn.setCellValueFactory(new PropertyValueFactory<Collectable, Image>("imageFiles/test.png"));
        tableView.getColumns().add(imageTableColumn);*/

        imageTableColumn = new TableColumn<>("Image");
        imageTableColumn.setMinWidth(107);
        imageTableColumn.setMaxWidth(107);
        imageTableColumn.setResizable(false);
        imageTableColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));

        imageTableColumn.getStyleClass().add("imageColumn");

        tableView.getColumns().add(imageTableColumn);

        for (int i = 0; i < createColumns.size(); i++) {
            final int j = i;
            TableColumn<Collectable, String> column = new TableColumn<>(createColumns.get(i));
            column.getStyleClass().add("textColumn");
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Collectable, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Collectable, String> param) {
                    return new SimpleStringProperty(String.valueOf(param.getValue().getContent().get(j)));
                }
            });
            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit(dataEditCommitHandler);
            tableView.getColumns().add(column);
            allColumns.add(createColumns.get(i));
        }
    }

    // Add 5 Rows (Collectables)
    public void addRows() {

        int x = tableView.getColumns().size() - 1;
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
            System.out.println("true");
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

            // Third Part: Path to Image and Content of all Rows (Collectables)
            for (Collectable collectable : allCollectables) {
                ObservableList<String> content = collectable.getContent();
                String imagePath = collectable.getPhoto().getImage().getUrl();

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
    public void addCollectable() {
        ObservableList<String> dataCollectable = FXCollections.observableArrayList();
        for (int i = 0; i < allColumns.size(); i++) {
            dataCollectable.add("");
        }
        Collectable collectable = new Collectable(dataCollectable, allColumns);
        allCollectables.add(collectable);
        tableView.setItems(allCollectables);
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
}
