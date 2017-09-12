package com.my.home.task;

import com.my.home.plugin.model.PluginOutput;
import com.my.home.plugin.model.Result;
import com.my.home.ui.App;

import java.util.List;
import java.util.concurrent.Future;

/**
 * App task for post processing plugin results
 */
public class AfterProcessingLogTask extends AbstractAppTask<List<PluginOutput>>
{

    private App application;
    // TODO add objects to save and/or output results
    public AfterProcessingLogTask(Future<List<PluginOutput>> future, App app)
    {
        super(future);
        application = app;
    }

    @Override
    protected void executeResult(List<PluginOutput> result)
    {
        application.setPluginWorkOutput(result);
        // TODO implement result processing (saving, showing, etc)
    }
}
