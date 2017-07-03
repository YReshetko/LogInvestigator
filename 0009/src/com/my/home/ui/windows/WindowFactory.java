package com.my.home.ui.windows;

import com.my.home.ui.controllers.IUIController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *
 */
public class WindowFactory
{
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
            controller.init();
        }
        return node;
    }

}
