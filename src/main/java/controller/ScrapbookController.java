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
import javafx.scene.control.Button;
import javafx.scene.control.TablePosition;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

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

    @FXML
    private Button saveButton;
    @FXML
    private Button backToMenuButton;
    @FXML
    private Button saveLocationButton;
    @FXML
    private Button addCollectableButton;
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

        for (int i = 0; i < createColumns.size(); i++) {
            final int j = i;
            TableColumn<Collectable, String> column = new TableColumn<>(createColumns.get(i));
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

    // Set the Columns, selected in the CeckComboBox
    public void setColumnChoices(ObservableList<String> columns) {
        createColumns = columns;
    }

    // Add 5 Rows (Collectables)
    public void addRows() {
        int x = tableView.getColumns().size();
        ObservableList<String> data = FXCollections.observableArrayList();
        String ergebnis = "";
        for (int i = 0; i < x; i++) {
            data.add(ergebnis);
        }

        for (int j = 0; j < 5; j++) {
            Collectable collectable = new Collectable(data, allColumns);
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

            // First Part: Number of all Columns
            writer.write(String.valueOf(allColumns.size()));
            writer.newLine();

            // Second Part: Name of all Columns
            for (String allColumn : allColumns) {
                writer.write(allColumn + ";");
                //writer.newLine();
            }

            writer.newLine();

            // Third Part: Content of all Rows (Collectables)
            for (Collectable collectable : allCollectables) {
                ObservableList<String> content = collectable.getContent();

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
}
