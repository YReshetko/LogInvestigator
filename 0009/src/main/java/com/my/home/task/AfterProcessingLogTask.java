package com.my.home.task;

import com.my.home.plugin.model.PluginOutput;

import java.util.List;
import java.util.concurrent.Future;

/**
 * App task for post processing plugin results
 */
public class AfterProcessingLogTask extends AbstractAppTask<List<PluginOutput>> {

    // TODO add objects to save and/or output results
    public AfterProcessingLogTask(Future<List<PluginOutput>> future)
    {
        super(future);
    }

    @Override
    protected void executeResult(List<PluginOutput> result)
    {
        result.forEach(System.out::println);
        // TODO implement result processing (saving, showing, etc)
    }
}
