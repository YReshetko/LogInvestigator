package com.my.home.ui.controllers.result;

import com.my.home.plugin.model.TableResult;
import javafx.scene.Parent;

/**
 * Created with IntelliJ IDEA.
 * User: Yurchik
 * Date: 15.09.17
 * Time: 14:18
 * To change this template use File | Settings | File Templates.
 */
public class TableResultSample
{
    private Parent child;
    private TableResultController controller;

    public TableResultSample(Parent child, TableResultController controller) {
        this.child = child;
        this.controller = controller;
    }
    public Parent getNode()
    {
        return child;
    }
    public void setValue(TableResult value)
    {
        controller.setTable(value);
    }
}
