package com.my.home.task;

import com.my.home.storage.ILogIdentifier;

import java.util.concurrent.Future;

/**
 *
 */
public class AfterParseLogTask extends AbstractAppTask<ILogIdentifier>
{

    public AfterParseLogTask(Future<ILogIdentifier> future)
    {
        super(future);
    }

    @Override
    protected void executeResult(ILogIdentifier result)
    {
        System.out.println("Execute task for:");
        result.getLogDescriptor().getFiles().forEach(System.out::println);
    }
}
