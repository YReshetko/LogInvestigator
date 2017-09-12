package com.my.home.ui.controllers.result;

import com.my.home.ui.controllers.PluginController;
import javafx.scene.Parent;

/**
 * Created with IntelliJ IDEA.
 * User: Yurchik
 * Date: 12.09.17
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */
public class SimpleResultSample
{
    private Parent child;
    private SimpleResultController controller;

    public SimpleResultSample(Parent child, SimpleResultController controller) {
        this.child = child;
        this.controller = controller;
    }
    public Parent getNode()
    {
        return child;
    }

    public void setDescription(String value)
    {
        controller.setDescription(value);
    }
    public void setValue(String value)
    {
        controller.setValue(value);
    }
}
