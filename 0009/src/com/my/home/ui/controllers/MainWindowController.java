package com.my.home.ui.controllers;

import com.my.home.factory.SpringBeanFactory;
import com.my.home.ui.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;

public class MainWindowController implements IUIController
{

    @FXML
    private TreeView logTree;
    private App mainApp;
    public MainWindowController()
    {
        super();
        mainApp = (App) SpringBeanFactory.getInstance().getBean("Application");
        System.out.println("Test controller created");
    }
    @FXML
    private void handleOptionButton(ActionEvent event)
    {
        mainApp.openModal("option");
    }

    @Override
    public void init() {

    }
}
