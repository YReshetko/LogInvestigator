package com.my.home.ui.controllers.result;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

/**
 * Created with IntelliJ IDEA.
 * User: Yurchik
 * Date: 12.09.17
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */
public class SimpleResultController
{
    @FXML
    private Label description;
    @FXML
    private TextArea value;

    public String getValue() {
        return value.getText();
    }

    public void setValue(String value) {
        this.value.setText(value);
    }

    public String getDescription() {
        return description.getText();
    }

    public void setDescription(String description) {
        this.description.setText(description);
    }

}
