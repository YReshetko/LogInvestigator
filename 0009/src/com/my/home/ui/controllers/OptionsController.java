package com.my.home.ui.controllers;

import com.my.home.factory.SpringBeanFactory;
import com.my.home.ui.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 *
 * */
public class OptionsController implements IUIController
{


    @FXML
    private Pane optionView;

    private App app;
    public OptionsController()
    {
        app = (App) SpringBeanFactory.getInstance().getBean("Application");
    }

    @FXML
    private void handleStorageOption(ActionEvent event) throws IOException
    {
        Parent node = app.getNodeFromTemplate("storageOption");
        optionView.getChildren().clear();
        optionView.getChildren().add(node);

    }
    @FXML
    private void handleParserOption(ActionEvent event) throws IOException
    {
        Parent node = app.getNodeFromTemplate("parserOption");
        optionView.getChildren().clear();
        optionView.getChildren().add(node);
    }

    @Override
    public void init() {
        try
        {
            handleStorageOption(null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
