package com.my.home.ui;

import com.my.home.log.LogIdentifierImpl;
import com.my.home.log.beans.LogFilesDescriptor;
import com.my.home.log.beans.LogNode;
import com.my.home.log.beans.ThreadsInfo;
import com.my.home.log.manager.ILogManager;
import com.my.home.log.manager.MainLogManager;
import com.my.home.log.manager.web.WebLogManager;
import com.my.home.parser.ParserManager;
import com.my.home.plugin.IPluginStorage;
import com.my.home.plugin.PluginFactoryImpl;
import com.my.home.plugin.model.PluginToStore;
import com.my.home.plugin.model.PluginType;
import com.my.home.processor.ILogProgress;
import com.my.home.processor.ILogStorage;
import com.my.home.processor.ILogStorageCommand;
import com.my.home.progress.ProgressManager;
import com.my.home.storage.*;
import com.my.home.storage.mongo.commands.*;
import com.my.home.storage.mongo.impl.MongoConnection;
import com.my.home.storage.mongo.impl.MongoLogRetriever;
import com.my.home.storage.mongo.impl.MongoLogSaver;
import com.my.home.storage.mongo.plugin.MongoPluginStorage;
import com.my.home.task.AfterParseLogTask;
import com.my.home.task.executor.AppTaskExecutor;
import com.my.home.ui.controllers.IUIController;
import com.my.home.ui.controllers.MainWindowController;
import com.my.home.ui.tree.ILogTreeListener;
import com.my.home.ui.tree.LogTreeController;
import com.my.home.ui.windows.WindowDescriptor;
import com.my.home.ui.windows.WindowFactory;
import com.my.home.util.FileChooserUtil;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 *
 */
public class App implements ILogTreeListener
{

    private Map<String, WindowDescriptor> windows;

    /**
     * Stages
     */
    private Stage primaryStage;
    private boolean isStageInitialized;
    private MainWindowController primaryController;
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

    private MainLogManager logManager;

    /**
     * Init application, runs once when application starts
     * @param primaryStage - primary stage comes from JavaFX framework
     */
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

    /**
     * Initialization of Mongo DB, its manager, stage and app context
     * Should be executed when we change mongo connection (location, db etc.)
     * @throws Exception - unexpected exception
     */
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

    /**
     * Initialization of main part of program:
     * - storage
     * - plugin storage and factory
     * - // TODO processors
     */
    private void initNewContext()
    {
        final ILogSaver saver = new MongoLogSaver(connection);
        final ILogRetriever retriever = new MongoLogRetriever(connection);
        final ILogNodeParser parser = parserManager.getParser("default");
        final ILogProgress progress = new ProgressManager(primaryController.getProgressBar());
        taskExecutor.setProgressManager(progress);
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

    /**
     * Initialization of main stage of the application must be done only once
     * If the method runs again it should clean all dynamics elements on stage
     * @throws Exception - unexpected exception
     */
    private void initPrimaryStage() throws Exception
    {
        if(!isStageInitialized)
        {
            if (modalWindows.get("storageOption") != null && modalWindows.get("storageOption").isShowing())
            {
                modalWindows.get("storageOption").close();
            }
            isStageInitialized = true;
            //  Init primary stage
            WindowFactory.fillStage(this.primaryStage, windows.get("main"));
            // Init UI controller for primary stage
            primaryController = (MainWindowController) WindowFactory.getController(windows.get("main"));
            //  Init Controller of log tree
            treeController = new LogTreeController(primaryController.getLogTreeView());
            treeController.setTreeListener(this);
            //Init log manager
            initLogManager(primaryController);
            //  Init listeners for drag and drop files to application (plugins, log)
            primaryController.getRootElement().setOnDragOver(this::handleDragEvent);
            primaryController.getRootElement().setOnDragDropped(this::handleDropEvent);

            //  Plugin handlers
            primaryController.getAddSelectedPluginsBtn().setOnAction(event -> primaryController.addSelectedPluginsToBlock());
            primaryController.getClearPluginsBlockBtn().setOnAction(event -> primaryController.removeAllPluginsFromBlock());
            //  Setup primary stage
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

    /**
     * Initialization of log manager, which contains different viewports of log
     * @param mainController - main controller to retrieve UI elements
     */
    private void initLogManager(MainWindowController mainController)
    {
        logManager = new MainLogManager();
        ILogManager webLogManager = new WebLogManager(mainController.getWebLog());
        logManager.addLogManger(webLogManager);

        //  Init button handlers
        mainController.getLogProcessBtn().setOnAction(this::handleProcessSelectedLog);
        mainController.getLogDownloadBtn().setOnAction(this::handleDownloadSelectedLog);
        mainController.getLogRemoveBtn().setOnAction(this::handleDeleteSelectedLog);
        mainController.getLogRestoreBtn().setOnAction(this::handleRestoreSelectedLog);
    }

    /**
     * Show modal window by its name
     * @param modal - name of modal window (setup in spring)
     */
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
        if(plugin != null)
        {
            savePlugin(plugin);
        }
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

    /**
     * Implement listen method of selected tree
     * @param identifier - log identifier
     */
    @Override
    public void dispatch(ILogIdentifier identifier) {
        if (identifier != null)
        {
            Iterator<ThreadsInfo> threadsInfoIterator = storage.getIterator(identifier, new FindThreadsInfoCommand());
            ThreadsInfo selectedLog = null;

            while (threadsInfoIterator.hasNext())
            {
                selectedLog = threadsInfoIterator.next();
            }
            if(selectedLog != null)
            {
                logManager.setIdentifier(identifier);
                logManager.setThreadsInfo(selectedLog);
            }
            else
            {
                logManager.setIdentifier(null);
            }
        }
    }

    private void handleProcessSelectedLog(ActionEvent event)
    {
        ILogIdentifier identifier = logManager.getIdentifier();
        List<String> logThreads = logManager.getSelectedThreads();

        if (logThreads.size() > 0)
        {
            System.out.println("Prepare selector for processing:");
            logThreads.forEach(System.out::println);

            //  TODO Implement pass selected threads into plugin processing
            Map<PluginType, List<PluginToStore>> plugins = primaryController.getPluginsToProcess();
            plugins.entrySet().forEach(entry -> System.out.println("Plugin type: " + entry.getKey() + "; number: " + entry.getValue().size()));
        }

    }

    private void handleDeleteSelectedLog(ActionEvent event)
    {
        ILogIdentifier identifier = logManager.getIdentifier();
        List<String> logThreads = logManager.getSelectedThreads();
        if (logThreads.size() > 0)
        {
            System.out.println("Prepare selector for processing:");
            logThreads.forEach(System.out::println);

            Iterator<ThreadsInfo> threadsInfoIterator = storage.getIterator(identifier, new FindThreadsInfoCommand());
            ThreadsInfo selectedLog = null;

            while (threadsInfoIterator.hasNext())
            {
                selectedLog = threadsInfoIterator.next();
            }
            if(selectedLog != null)
            {
                if (storage.changeLog(identifier, new RemoveThreadsCommand(selectedLog, logThreads)))
                {
                    dispatch(identifier);
                }
            }
        }

    }
    private void handleRestoreSelectedLog(ActionEvent event)
    {
        ILogIdentifier identifier = logManager.getIdentifier();

        Iterator<ThreadsInfo> threadsInfoIterator = storage.getIterator(identifier, new FindThreadsInfoCommand());
        ThreadsInfo selectedLog = null;

        while (threadsInfoIterator.hasNext())
        {
            selectedLog = threadsInfoIterator.next();
        }
        if(selectedLog != null)
        {
            if (storage.changeLog(identifier, new RestoreAllThreadsCommand(selectedLog)))
            {
                dispatch(identifier);
            }
        }

    }
    private void handleDownloadSelectedLog(ActionEvent event)
    {
        ILogIdentifier identifier = logManager.getIdentifier();
        List<String> logThreads = logManager.getSelectedThreads();
        downloadThreads(identifier, logThreads);
    }
    public void downloadThreadAndOpen(String threadName)
    {
        ILogIdentifier identifier = logManager.getIdentifier();
        List<String> logThreads = Arrays.asList(threadName);
        File fileToOpen = downloadThreads(identifier, logThreads);
        if (fileToOpen != null)
        {
            try
            {
                Desktop.getDesktop().open(fileToOpen);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public File downloadThreads(ILogIdentifier identifier, List<String> logThreads)
    {
        if(logThreads.size() > 0)
        {
            List<LogNode> nodes = new ArrayList<>(logThreads.size());
            logThreads.forEach(thread -> {
                LogNode node = new LogNode();
                node.setThread(thread);
                nodes.add(node);
            });
            ILogStorageCommand command = new FindNodesCommand();
            command.setData(identifier, nodes.toArray());
            String fileTestToSave = storage.getLog(identifier, command);
            File file = FileChooserUtil.saveFile(getStageByName(null), "Save file", logThreads.get(0) + ".log", "*.log");
            FileWriter writer = null;
            try
            {
                if(file != null)
                {
                    writer = new FileWriter(file);
                    writer.write(fileTestToSave);
                    writer.flush();
                }
            }
            catch (IOException e)
            {
                file = null;
                e.printStackTrace();
            }
            finally
            {
                if(writer != null)
                {
                    try
                    {
                        writer.close();
                    }
                    catch (IOException e)
                    {
                        file = null;
                        e.printStackTrace();
                    }
                }
                return file;
            }

        }
        return null;
    }
}
