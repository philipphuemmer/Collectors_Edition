package controller;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ErrorController {

    public Stage errorStage;

    @FXML
    private Button okayButton;
    @FXML
    private TextField errorTextField;

    public void setErrorTextField(String error) {
        errorTextField.setText(error);
    }

    @FXML
    public void closeErrorWindow() {
        errorStage.close();
    }
}
