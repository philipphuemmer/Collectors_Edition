package program;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class Collectable {

    private int id;
    public ObservableList<String> columns;
    public ObservableList<String> content = FXCollections.observableArrayList();

    public ImageView photo;
    //public ObservableList<StringProperty> contentProperty = FXCollections.observableArrayList();

    public int dataSize;

    public Collectable(ObservableList<String> data, ObservableList<String> allColumns) {
        columns = allColumns;
        dataSize = data.size();
        content.addAll(data);
    }

    public Collectable(ObservableList<String> data, ObservableList<String> allColumns, ImageView photo) {
        this.photo = photo;
        columns = allColumns;
        dataSize = data.size();
        content.addAll(data);
    }

    public void updateColumns() {

    }

    public ObservableList<String> getContent() {
        return content;
    }

    public void setContent(String s, int id) {
        //SimpleStringProperty simpleStringProperty = new SimpleStringProperty(s);
        content.set(id, s);
    }

    public ImageView getPhoto() {
        return photo;
    }

    public void setPhoto(ImageView photo) {
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return this.columns.toString() + this.content.toString();
    }

    /*private int id = 0;
    private int cellID = 0;
    private ObservableList<TextCell> textCells = FXCollections.observableArrayList();

    public SimpleStringProperty string1;
    public SimpleStringProperty string2;
    public SimpleStringProperty string3;

    public Collectable(String s) {
        this.string1 = new SimpleStringProperty(s);
    }

    public Collectable(String string1, String string2, String string3) {
        this.string1 = new SimpleStringProperty(string1);
        this.string2 = new SimpleStringProperty(string2);
        this.string3 = new SimpleStringProperty(string3);
    }

    public String getString1() {
        return string1.get();
    }

    public String getString2() {
        return string2.get();
    }

    public String getString3() {
        return string3.get();
    }


    public StringProperty string1Property() {
        return string1;
    }


    public SimpleStringProperty string2Property() {
        return string2;
    }

    public SimpleStringProperty string3Property() {
        return string3;
    }

    public void setString1(String string1) {
        this.string1.set(string1);
    }

    public void setString2(String string2) {
        this.string2.set(string2);
    }

    public void setString3(String string3) {
        this.string3.set(string3);
    }

    public void addTextCell(TextCell textCell) {
        textCells.add(textCell);
    }
    */

}
