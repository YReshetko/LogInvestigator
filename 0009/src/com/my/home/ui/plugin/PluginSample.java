package com.my.home.ui.plugin;

import com.my.home.plugin.model.PluginToStore;
import com.my.home.ui.controllers.PluginController;
import javafx.scene.Parent;
import javafx.scene.input.TransferMode;

/**
 *
 */
public class PluginSample
{

    private PluginToStore pluginDescriptor;
    private Parent child;
    private PluginController controller;

    public PluginSample(PluginToStore pluginDescriptor, Parent child, PluginController controller)
    {
        this.pluginDescriptor = pluginDescriptor;
        this.child = child;
        this.controller = controller;
        controller.setLabel(pluginDescriptor.getPlugin().getLabel());
        controller.setToolTip(pluginDescriptor.getPlugin().getDescription());
    }
    public Parent getNode()
    {
        return child;
    }
    public boolean isSelected()
    {
        return controller.isSelected();
    }

}
