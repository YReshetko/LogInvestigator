package com.my.home.ui.controllers;

import com.my.home.factory.SpringBeanFactory;
import com.my.home.ui.App;
import com.my.home.util.FileChooserUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.Properties;

/**
 *
 */
public class StorageController implements IUIController
{
    private final static String MONGO_BIN_TITLE = "Select <%Mongo root%>/bin";
    private final static String MONGO_DB_TITLE = "Select db location";
    @FXML
    private TextField mongoLocation;
    @FXML
    private TextField dbLocation;
    @FXML
    private CheckBox isStartMongo;
    @FXML
    private TextField host;
    @FXML
    private TextField port;
    @FXML
    private TextField dbName;
    private App app;
    public StorageController()
    {
        app = (App) SpringBeanFactory.getInstance().getBean("Application");
    }

    @FXML
    private void onConnection(ActionEvent event)
    {
        app.setStorageProps(prepareProperties());
    }

    @FXML
    private void selectMongoDir(ActionEvent event)
    {
        selectDir(MONGO_BIN_TITLE, mongoLocation);
    }
    @FXML
    private void selectDbDir(ActionEvent event)
    {
        selectDir(MONGO_DB_TITLE, dbLocation);
    }

    private void selectDir(String title, TextField dest)
    {
        File selectedDirectory = FileChooserUtil.showDirChooser(app.getStageByName("storageOption"), title);
        if(selectedDirectory != null)
        {
            dest.setText(selectedDirectory.getAbsolutePath());
        }
    }
    private void showProps()
    {
        Properties props = app.getStorageProps();
        mongoLocation.setText(props.getProperty("storage.mongo.path"));
        dbLocation.setText(props.getProperty("storage.mongo.db.path"));
        host.setText(props.getProperty("storage.mongo.host"));
        port.setText(props.getProperty("storage.mongo.port"));
        dbName.setText(props.getProperty("storage.mongo.db"));

        isStartMongo.setSelected(Boolean.valueOf(props.getProperty("storage.mongo.openAtStart")));
    }

    private Properties prepareProperties()
    {
        Properties out = new Properties();
        out.setProperty("storage.mongo.path", mongoLocation.getText());
        out.setProperty("storage.mongo.db.path", dbLocation.getText());
        out.setProperty("storage.mongo.host", host.getText());
        out.setProperty("storage.mongo.port", port.getText());
        out.setProperty("storage.mongo.db", dbName.getText());
        out.setProperty("storage.mongo.openAtStart", String.valueOf(isStartMongo.isSelected()));
        return out;
    }


    @Override
    public void init() {
        showProps();
    }

    @Override
    public void update() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
