package com.my.home.ui.controllers;

import com.my.home.factory.SpringBeanFactory;
import com.my.home.plugin.model.PluginToStore;
import com.my.home.plugin.model.PluginType;
import com.my.home.ui.App;
import com.my.home.ui.plugin.PluginSample;
import com.my.home.ui.windows.WindowFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainWindowController implements IUIController
{

    @FXML
    private VBox selectors;
    @FXML
    private VBox filters;
    @FXML
    private VBox processors;
    @FXML
    private VBox postProcessors;
    @FXML
    private VBox pluginPanel;

    @FXML
    private BorderPane rootElement;

    @FXML
    private WebView webLog;

    @FXML
    private ProgressBar progress;

    @FXML
    private TreeView logTree;
    private App mainApp;
    private Map<PluginType, List<PluginSample>> allPlugins;
    public MainWindowController()
    {
        super();
        mainApp = (App) SpringBeanFactory.getInstance().getBean("Application");
        allPlugins = new HashMap<>();
        System.out.println("Main window controller has been created");
    }
    @FXML
    private void handleOptionButton(ActionEvent event)
    {
        mainApp.openModal("option");
    }
    @FXML
    private void handleImportLog(ActionEvent event)
    {
        mainApp.chooseLog();
    }
    @FXML
    private void handleImportPlugin(ActionEvent event)
    {
        mainApp.choosePlugin();
    }

    @Override
    public void init() {

    }

    @Override
    public void update() {
        // TODO Clear all UI elements to update
        // TODO Update UI from plugin storage
        List<PluginToStore> plugins = mainApp.getPlugins();
        plugins.forEach(plugin -> {
            List<PluginSample> plugs = allPlugins.get(plugin.getType());
            if(plugs == null)
            {
                plugs = new ArrayList<>();
                allPlugins.put(plugin.getType(), plugs);
            }
            plugs.add(WindowFactory.getPluginSample(plugin));
        });
        allPlugins.entrySet().forEach(entry -> {
            if(entry.getKey() == PluginType.SELECTOR)
            {
                entry.getValue().forEach(plugin -> addPlugin(selectors, plugin));
            }
            else if(entry.getKey() == PluginType.FILTER)
            {
                entry.getValue().forEach(plugin -> addPlugin(filters, plugin));
            }
            else if(entry.getKey() == PluginType.PROCESSOR)
            {
                entry.getValue().forEach(plugin -> addPlugin(processors, plugin));
            }
            else if(entry.getKey() == PluginType.POST_PROCESSOR)
            {
                entry.getValue().forEach(plugin -> addPlugin(postProcessors, plugin));
            }
        });
        // TODO Update UI from log storage
    }
    private void addPlugin(VBox parent, PluginSample plugin)
    {
        parent.getChildren().add(plugin.getNode());
    }

    public ProgressBar getProgressBar()
    {
        return progress;
    }

    public TreeView getLogTreeView()
    {
        return logTree;
    }
    public VBox getPluginPanel()
    {
        return pluginPanel;
    }
    public BorderPane getRootElement()
    {
        return rootElement;
    }

    public WebView getWebLog()
    {
        return webLog;
    }
}
