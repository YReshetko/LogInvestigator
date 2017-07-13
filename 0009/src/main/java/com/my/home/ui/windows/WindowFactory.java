package com.my.home.ui.windows;

import com.my.home.plugin.model.PluginToStore;
import com.my.home.ui.controllers.IUIController;
import com.my.home.ui.controllers.PluginController;
import com.my.home.ui.plugin.PluginSample;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class WindowFactory
{
    private static final String PLUGIN_SAMPLE_FXML = "plugin/pluginSample.fxml";
    public static Map<WindowDescriptor, IUIController> controllerMap = new HashMap<>();
    public static Stage getStage(WindowDescriptor descriptor) throws IOException
    {
        Stage out = new Stage();
        fillStage(out, descriptor);
        return out;
    }
    public static void fillStage(Stage stage, WindowDescriptor descriptor) throws IOException
    {
        Parent root = getNode(descriptor);
        Scene scene = new Scene(root, descriptor.getWidth(), descriptor.getHeight());
        stage.setTitle(descriptor.getTitle());
        stage.setScene(scene);
    }
    public static Parent getNode(WindowDescriptor descriptor) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(WindowFactory.class.getResource(descriptor.getFxmlDoc()));
        Parent node = loader.load();
        IUIController controller = loader.getController();
        if (controller != null)
        {
            controllerMap.put(descriptor, controller);
            controller.init();
        }
        return node;
    }
    public static PluginSample getPluginSample(PluginToStore pluginDescriptor)
    {
        try{
            FXMLLoader loader = new FXMLLoader(WindowFactory.class.getResource(PLUGIN_SAMPLE_FXML));
            Parent node = loader.load();
            PluginController controller = loader.getController();
            PluginSample sample = new PluginSample(pluginDescriptor, node, controller);
            return sample;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

    }

    public static IUIController getController(WindowDescriptor descriptor)
    {
        IUIController controller = controllerMap.get(descriptor);
        return controller;
    }

}