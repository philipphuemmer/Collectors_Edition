package program;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class UpdateCollectable {

    public ObservableList<String> columns;
    public ObservableList<String> oldContent;
    public ObservableList<String> newContent;

    public SimpleStringProperty Name;
    public SimpleStringProperty Year;
    public SimpleStringProperty Price;
    public SimpleStringProperty oldName;
    public SimpleStringProperty oldYear;
    public SimpleStringProperty oldPrice;

    public UpdateCollectable(ObservableList<String> oldContent, ObservableList<String> newContent, ObservableList<String> columns) {
        this.columns = columns;
        this.oldContent = oldContent;
        this.newContent = newContent;
    }

    public UpdateCollectable(String name, String year, String price, String oldName, String oldYear, String oldPrice) {
        this.Name = new SimpleStringProperty(name);
        this.Year = new SimpleStringProperty(year);
        this.Price = new SimpleStringProperty(price);
        this.oldName = new SimpleStringProperty(oldName);
        this.oldYear = new SimpleStringProperty(oldYear);
        this.oldPrice = new SimpleStringProperty(oldPrice);

    }

}
