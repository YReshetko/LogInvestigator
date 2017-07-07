package com.my.home.ui;

import com.my.home.log.LogIdentifierImpl;
import com.my.home.log.beans.LogFilesDescriptor;
import com.my.home.parser.ParserManager;
import com.my.home.plugin.IPluginStorage;
import com.my.home.plugin.PluginFactoryImpl;
import com.my.home.plugin.model.PluginToStore;
import com.my.home.processor.ILogProgress;
import com.my.home.processor.ILogStorage;
import com.my.home.progress.ProgressManager;
import com.my.home.storage.*;
import com.my.home.storage.mongo.commands.FindAllProcessedFilesCommand;
import com.my.home.storage.mongo.impl.MongoConnection;
import com.my.home.storage.mongo.impl.MongoLogRetriever;
import com.my.home.storage.mongo.impl.MongoLogSaver;
import com.my.home.storage.mongo.plugin.MongoPluginStorage;
import com.my.home.task.AfterParseLogTask;
import com.my.home.task.executor.AppTaskExecutor;
import com.my.home.ui.controllers.IUIController;
import com.my.home.ui.controllers.MainWindowController;
import com.my.home.ui.tree.LogTreeController;
import com.my.home.ui.windows.WindowDescriptor;
import com.my.home.ui.windows.WindowFactory;
import com.my.home.util.FileChooserUtil;
import javafx.scene.Parent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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
    private IUIController primaryController;
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

    private IPluginStorage pluginStorage;
    private PluginFactoryImpl pluginFactory;

    private AppTaskExecutor taskExecutor;

    private LogTreeController treeController;



    public void init(Stage primaryStage)
    {
        isStageInitialized = false;
        modalWindows = new HashMap<>();
        this.primaryStage = primaryStage;
        taskExecutor = new AppTaskExecutor();
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
        primaryController.update();
    }

    private void initNewContext()
    {
        final ILogSaver saver = new MongoLogSaver(connection);
        final ILogRetriever retriever = new MongoLogRetriever(connection);
        final ILogNodeParser parser = parserManager.getParser("default");
        final ILogProgress progress = new ProgressManager(((MainWindowController) primaryController).getProgressBar());
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

            @Override
            public ILogProgress getProgress() {
                return progress;
            }
        };

        if (storage == null)
        {
            storage = new LogStorageImpl();
        }
        storage.setStorageContext(context);
        pluginStorage = new MongoPluginStorage(connection);
        pluginFactory = new PluginFactoryImpl(pluginStorage, pluginDir);
        //  INIT Tree view by all loaded files
        Iterator<LogFilesDescriptor> files = storage.getIterator(null, new FindAllProcessedFilesCommand());
        List<ILogIdentifier> descriptors = new LinkedList<>();
        files.forEachRemaining(descriptor -> descriptors.add(new LogIdentifierImpl(descriptor)));
        treeController.addAll(descriptors);
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
            primaryController = WindowFactory.getController(windows.get("main"));
            MainWindowController castController = (MainWindowController) primaryController;
            treeController = new LogTreeController(castController.getLogTreeView());
            castController.getRootElement().setOnDragOver(this::handleDragEvent);
            castController.getRootElement().setOnDragDropped(this::handleDropEvent);
            this.primaryStage.setMaximized(true);
            this.primaryStage.show();
            primaryStage.setOnCloseRequest(event -> {
                taskExecutor.interrupt();
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

    /**
     * Choose plugin from main menu
     */
    public void choosePlugin()
    {
        File plugin = FileChooserUtil.getFile(getStageByName(null), "Plugins", "*.jar");
        savePlugin(plugin);
    }

    /**
     * Handle drag event for files on stage
     * @param event - drag event
     */
    private void handleDragEvent(DragEvent event)
    {
        boolean canDrop = canHandleDragAndDropForFiles(event);
        if (canDrop)
        {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    /**
     * Handle drop event for files
     * @param event - event;
     */
    private void handleDropEvent(DragEvent event)
    {
        boolean canDrop = canHandleDragAndDropForFiles(event);
        if (canDrop)
        {
            Dragboard dragboard = event.getDragboard();
            List<File> files = dragboard.getFiles();
            List<File> plugins = files.stream().filter(file ->
                    file.getName().endsWith(".jar")).collect(Collectors.toList());
            List<File> logs = files.stream().filter(file ->
                    file.getName().endsWith(".log") ||
                    file.getName().endsWith(".txt")).collect(Collectors.toList());
            plugins.forEach(this::savePlugin);
            saveLog(logs);
        }
        event.consume();
    }

    /**
     * Check if this files can be dropped to program
     * @param event - event
     * @return - true if can drop
     */
    private boolean canHandleDragAndDropForFiles(DragEvent event)
    {
        Dragboard dragboard = event.getDragboard();
        boolean canDrop = true;
        if (dragboard.hasFiles())
        {
            List<File> files = dragboard.getFiles();
            long unexpectedFilesCount = files.stream().filter(file ->
                    !file.getName().endsWith(".log") &&
                            !file.getName().endsWith(".txt") &&
                            !file.getName().endsWith(".jar")).count();
            if(unexpectedFilesCount > 0)
            {
                canDrop = false;
            }
        }
        else
        {
            canDrop = false;
        }
        return canDrop;
    }

    /**
     * Save file as plugin
     * @param plugin - plugin
     */
    private void savePlugin(File plugin)
    {
        pluginFactory.savePlugin(plugin);
        primaryController.update();
    }

    /**
     * Choose log from main menu
     */
    public void chooseLog()
    {
        List<File> logFiles = FileChooserUtil.getFiles(getStageByName(null), "Log", "*.log", "*.txt");
        if(logFiles != null)
        {
            saveLog(logFiles);
        }
    }

    /**
     * Save selected log
     * @param files - log files
     */
    private void saveLog(List<File> files)
    {
        ILogIdentifier identifier = new LogIdentifierImpl(files, files.get(0).getParentFile().getAbsolutePath());
        Future<ILogIdentifier> future = storage.process(identifier, files);
        taskExecutor.addTask(new AfterParseLogTask(future, treeController));
    }

    /**
     * Retrieve parent object to construct window or part of window
     * @param doc - name of window
     * @return - parent object for UI
     */
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

    /**
     * Setup windows map
     * @param windows - new windows map
     */
    public void setWindows(Map<String, WindowDescriptor> windows)
    {
        this.windows = windows;
    }

    /**
     * Setaup storage manager
     * @param storageManager - storage manager
     */
    public void setStorageManager(MongoStorageManager storageManager)
    {
        this.storageManager = storageManager;
    }

    /**
     * Setup parser manager
     * @param parserManager - parser manager
     */
    public void setParserManager(ParserManager parserManager)
    {
        this.parserManager = parserManager;
    }

    /**
     * Setup directory where will be copying plugins
     * @param pluginDir
     */
    public void setPluginDir(String pluginDir)
    {
        this.pluginDir = pluginDir;
    }

    /**
     * Retrieve mongo properties
     * @return - mongo properties
     */
    public Properties getStorageProps()
    {
        return this.storageManager.getProps();
    }

    /**
     * Setup new mongo properties
     * @param newProps - new props
     */
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

    /**
     * Retrieve stage by it's name in cache. If it doesn't exist then return main stage
     * Need this method to apply modal window
     * @param name - name of stage which should be primary for modal window
     * @return - Stage
     */
    public Stage getStageByName(String name)
    {
        Stage currStage = modalWindows.get(name);
        if(currStage != null && currStage.isShowing())
        {
            return modalWindows.get(name);
        }
        else if(primaryStage != null)
        {
            return primaryStage;
        }
        return null;
    }

    /**
     * Retrieve parser manager
     * @return - ParserManager
     */
    public ParserManager getParserManager()
    {
        return parserManager;
    }

    /**
     * retrieve descriptions of all loaded plugins
     * @return - list of plugins description
     */
    public List<PluginToStore> getPlugins()
    {
        return pluginStorage.get();
    }
}
