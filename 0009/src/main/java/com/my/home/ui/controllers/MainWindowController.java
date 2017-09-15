package com.my.home.ui.controllers;

import com.my.home.factory.SpringBeanFactory;
import com.my.home.plugin.model.PluginToStore;
import com.my.home.plugin.model.PluginType;
import com.my.home.ui.App;
import com.my.home.ui.controllers.result.SimpleResultSample;
import com.my.home.ui.controllers.result.TableResultSample;
import com.my.home.ui.plugin.PluginSample;
import com.my.home.ui.windows.WindowFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import java.util.*;
import java.util.stream.Collectors;

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
    private VBox blockSelector;
    @FXML
    private VBox blockFilters;
    @FXML
    private VBox blockProcessors;
    @FXML
    private VBox blockPostProcessors;


    @FXML
    private VBox pluginPanel;

    @FXML
    private VBox results;

    @FXML
    private BorderPane rootElement;

    @FXML
    private WebView webLog;

    @FXML
    private ProgressBar progress;

    @FXML
    private Button logProcessBtn;
    @FXML
    private Button logDownloadBtn;
    @FXML
    private Button logRemoveBtn;
    @FXML
    private Button logRestoreBtn;
    @FXML
    private Button addSelectedPluginsBtn;
    @FXML
    private Button clearPluginsBlockBtn;
    @FXML
    private Button savePluginsBlockBtn;
    @FXML
    private TreeView logTree;
    private App mainApp;
    private Map<PluginType, List<PluginSample>> allPlugins;
    private Map<PluginType, List<PluginSample>> blockPlugins;
    public MainWindowController()
    {
        super();
        mainApp = (App) SpringBeanFactory.getInstance().getBean("Application");
        allPlugins = new HashMap<>();
        blockPlugins = new HashMap<>();
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

    public Button getLogProcessBtn() {
        return logProcessBtn;
    }

    public Button getLogDownloadBtn() {
        return logDownloadBtn;
    }

    public Button getLogRemoveBtn() {
        return logRemoveBtn;
    }
    public Button getLogRestoreBtn() {
        return logRestoreBtn;
    }

    public Button getAddSelectedPluginsBtn() {
        return addSelectedPluginsBtn;
    }

    public Button getClearPluginsBlockBtn() {
        return clearPluginsBlockBtn;
    }

    public Button getSavePluginsBlockBtn() {
        return savePluginsBlockBtn;
    }

    public void addSelectedPluginsToBlock()
    {
        //  Prepare map of selected plugins
        Map<PluginType, List<PluginSample>> currentlySelected = new HashMap<>();
        allPlugins.entrySet().forEach(entry ->
            currentlySelected.put(entry.getKey(), entry.getValue().stream().filter(list -> list.isSelected()).collect(Collectors.toList()))
            );
        //  Check if we have only one selector
        List<PluginSample> currentSelectors = blockPlugins.get(PluginType.SELECTOR);
        List<PluginSample> newSelectors = currentlySelected.get(PluginType.SELECTOR);
        if (newSelectors != null && newSelectors.size() > 0)
        {
            blockPlugins.put(PluginType.SELECTOR, Arrays.asList(newSelectors.get(0)));
        }
        currentlySelected.remove(PluginType.SELECTOR);
        // Add selected plugins into block and array
        currentlySelected.entrySet().forEach(entry ->
                {
                    List<PluginSample> testList = blockPlugins.get(entry.getKey());
                    if (testList == null)
                    {
                        testList = new ArrayList<>();
                        blockPlugins.put(entry.getKey(), testList);
                    }
                    List<PluginSample> currentList = blockPlugins.get(entry.getKey());
                    entry.getValue().stream().filter(sample -> !currentList.contains(sample)).forEach(sample -> currentList.add(sample));
                    if(currentList != null)
                    {
                        System.out.println("new " +entry.getKey()+ " size = " + currentList.size());
                    }
                });
        clearBlockOfPlugins();
        updateBlockOfPlugins();
    }
    public void removeAllPluginsFromBlock()
    {
        blockPlugins.clear();
        clearBlockOfPlugins();
    }
    public void clearBlockOfPlugins()
    {
        blockSelector.getChildren().clear();
        blockFilters.getChildren().clear();
        blockProcessors.getChildren().clear();
        blockPostProcessors.getChildren().clear();
    }
    public void updateBlockOfPlugins()
    {
        blockPlugins.entrySet().forEach(entry -> {
            if(entry.getKey() == PluginType.SELECTOR)
            {
                entry.getValue().forEach(plugin -> addPlugin(blockSelector, WindowFactory.getPluginSample(plugin.getPluginDescriptor())));
            }
            else if(entry.getKey() == PluginType.FILTER)
            {
                entry.getValue().forEach(plugin -> addPlugin(blockFilters, WindowFactory.getPluginSample(plugin.getPluginDescriptor())));
            }
            else if(entry.getKey() == PluginType.PROCESSOR)
            {
                entry.getValue().forEach(plugin -> addPlugin(blockProcessors, WindowFactory.getPluginSample(plugin.getPluginDescriptor())));
            }
            else if(entry.getKey() == PluginType.POST_PROCESSOR)
            {
                entry.getValue().forEach(plugin -> addPlugin(blockPostProcessors, WindowFactory.getPluginSample(plugin.getPluginDescriptor())));
            }
        });
    }

    public Map<PluginType, List<PluginToStore>> getPluginsToProcess()
    {
        Map<PluginType,  List<PluginToStore>> out = new HashMap<>();
        if (!isBlockPluginEmpty())
        {
            blockPlugins.entrySet().forEach(entry -> {
                List<PluginToStore> list = new ArrayList<>();
                entry.getValue().forEach(plugin -> list.add(plugin.getPluginDescriptor()));
                if(!list.isEmpty())
                {
                    out.put(entry.getKey(), list);
                }
            });
        }
        else
        {
            allPlugins.entrySet().forEach(entry ->{
                List<PluginToStore> list = new ArrayList<>();
                entry.getValue().stream().filter(plugin -> plugin.isSelected()).forEach(plugin -> list.add(plugin.getPluginDescriptor()));
                if (!list.isEmpty())
                {
                    out.put(entry.getKey(), list);
                }
            });
        }
        return out;
    }

    private boolean isBlockPluginEmpty()
    {
        return !blockPlugins.entrySet().stream().anyMatch(entry -> entry.getValue() != null && entry.getValue().size() > 0);
    }


    public void cleanResultsPanel()
    {
        //results.getChildren().clear();
        Platform.runLater(() -> {
            results.getChildren().clear();
        });
    }

    public void addResult(SimpleResultSample result)
    {
        addResult(result.getNode());
    }
    public void addResult(TableResultSample result)
    {
        addResult(result.getNode());
    }
    private void addResult(Node node)
    {
        Platform.runLater(() -> {
            results.getChildren().add(node);
        });
    }
}
