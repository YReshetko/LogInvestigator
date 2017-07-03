package com.my.home.ui;

import com.my.home.parser.ParserManager;
import com.my.home.plugin.IPluginStorage;
import com.my.home.plugin.PluginFactoryImpl;
import com.my.home.processor.ILogStorage;
import com.my.home.storage.*;
import com.my.home.storage.mongo.impl.MongoConnection;
import com.my.home.storage.mongo.impl.MongoLogRetriever;
import com.my.home.storage.mongo.impl.MongoLogSaver;
import com.my.home.storage.mongo.plugin.MongoPluginStorage;
import com.my.home.ui.windows.WindowDescriptor;
import com.my.home.ui.windows.WindowFactory;
import javafx.scene.Parent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class App
{

    private Map<String, WindowDescriptor> windows;

    /**
     * Stages
     */
    private Stage primaryStage;
    private boolean isStageInitialized;
    private String pluginDir;



    /**
     * The map of all child windows
     */
    private Map<String, Stage> modalWindows;
    /**
     * Storage manager to work with storage properties and DB connections
     */
    private MongoStorageManager storageManager;

    /**
     * The parser manager
     */
    private ParserManager parserManager;

    /**
     * Connection to some mongo DB
     */
    private MongoConnection connection;

    /**
     * Log storage
     */
    private ILogStorage storage;

    private PluginFactoryImpl pluginFactory;

    public void init(Stage primaryStage)
    {
        isStageInitialized = false;
        modalWindows = new HashMap<>();
        this.primaryStage = primaryStage;
        try
        {
            initApplicationContext();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            openModal("storageOption");
        }
    }

    public void initApplicationContext() throws Exception
    {
        if (storageManager.needAutoRunDB())
        {
            storageManager.runDB();
        }
        connection = storageManager.getConnection();
        initPrimaryStage();
        initNewContext();
    }

    private void initNewContext()
    {
        final ILogSaver saver = new MongoLogSaver(connection);
        final ILogRetriever retriever = new MongoLogRetriever(connection);
        final ILogNodeParser parser = parserManager.getParser("default");
        ILogStorageContext context = new ILogStorageContext()
        {

            @Override
            public ILogNodeParser getParser() {
                return parser;
            }

            @Override
            public ILogSaver getSaver() {
                return saver;
            }

            @Override
            public ILogRetriever getRetriever() {
                return retriever;
            }
        };

        if (storage == null)
        {
            storage = new LogStorageImpl();
        }
        storage.setStorageContext(context);
        IPluginStorage pluginStorage = new MongoPluginStorage(connection);
        pluginFactory = new PluginFactoryImpl(pluginStorage, pluginDir);
    }
    private void initPrimaryStage() throws Exception
    {
        if(!isStageInitialized)
        {
            if (modalWindows.get("storageOption") != null && modalWindows.get("storageOption").isShowing())
            {
                modalWindows.get("storageOption").close();
            }
            isStageInitialized = true;
            WindowFactory.fillStage(this.primaryStage, windows.get("main"));
            this.primaryStage.setMaximized(true);
            this.primaryStage.show();
            primaryStage.setOnCloseRequest(event -> {
                storageManager.closeDB();
            });
        }
        else
        {
            // TODO action when stage already initialized but connection and context was changed somehow
            // TODO clean all windows
            // TODO setup new windows
        }
    }

    public void openModal(String modal)
    {
        try {
            Stage modalStage = modalWindows.get(modal);
            if (modalStage == null)
            {
                System.out.println("Init modal stage: " + modal);
                modalStage = WindowFactory.getStage(windows.get(modal));
                modalStage.initModality(Modality.WINDOW_MODAL);
                modalStage.initOwner(primaryStage);
                modalWindows.put(modal, modalStage);
            }
            primaryStage.toFront();
            System.out.println("Show modal stage: " + modal);
            modalStage.show();
        }
        catch (IOException e)
        {
            System.out.println("Cannot open modal window: " + modal);
            e.printStackTrace();
        }

    }
    public File showDirChooser(DirectoryChooser chooser, String parentW)
    {
        Stage currStage = modalWindows.get(parentW);
        if(currStage != null && currStage.isShowing())
        {
            return chooser.showDialog(modalWindows.get(parentW));
        }
        else if(primaryStage != null)
        {
            return chooser.showDialog(primaryStage);
        }
        return null;
    }

    public Parent getNodeFromTemplate(String doc)
    {
        try
        {
            return WindowFactory.getNode(windows.get(doc));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void setWindows(Map<String, WindowDescriptor> windows)
    {
        this.windows = windows;
    }

    public void setStorageManager(MongoStorageManager storageManager)
    {
        this.storageManager = storageManager;
    }

    public void setParserManager(ParserManager parserManager)
    {
        this.parserManager = parserManager;
    }
    public void setPluginDir(String pluginDir)
    {
        this.pluginDir = pluginDir;
    }

    public Properties getStorageProps()
    {
        return this.storageManager.getProps();
    }
    public void setStorageProps(Properties newProps)
    {
        try {
            this.storageManager.closeDB();
            this.storageManager.changeProperties(newProps);
            initApplicationContext();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public ParserManager getParserManager()
    {
        return parserManager;
    }
}
