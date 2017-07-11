package com.my.home.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;

/**
 *
 */
public class PluginController
{
    @FXML
    private CheckBox label;
    @FXML
    private AnchorPane sample;

    public boolean isSelected()
    {
        return label.isSelected();
    }
    public void setLabel(String value)
    {
        label.setText(value);
    }
    public void setToolTip(String text)
    {
        final Tooltip tooltip = new Tooltip();
        tooltip.setText(text);
        label.setTooltip(tooltip);
    }

}
