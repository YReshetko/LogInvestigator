package com.my.home.task;

import com.my.home.storage.ILogIdentifier;
import com.my.home.ui.tree.LogTreeController;

import java.util.concurrent.Future;

/**
 *
 */
public class AfterParseLogTask extends AbstractAppTask<ILogIdentifier>
{

    private LogTreeController controller;
    public AfterParseLogTask(Future<ILogIdentifier> future, LogTreeController controller)
    {
        super(future);
        this.controller = controller;
    }

    @Override
    protected void executeResult(ILogIdentifier result)
    {
        controller.add(result);
    }
}
