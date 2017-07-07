package com.my.home.ui.tree;

import com.my.home.storage.ILogIdentifier;
import javafx.scene.control.TreeItem;

/**
 *
 */
public class LogTreeLeaf implements ILogTreeElement
{
    private final ILogIdentifier identifier;
    private ILogTreeListener listener;
    private final TreeItem<ILogTreeElement> item;
    private final String label;
    public LogTreeLeaf(ILogIdentifier identifier)
    {
        this.identifier = identifier;
        label = identifier.getLogDescriptor().getName();
        item = new TreeItem<ILogTreeElement>(this);
    }
    @Override
    public void select()
    {
        listener.dispatch(identifier);
    }
    public void addEventListener(ILogTreeListener listener)
    {
        this.listener = listener;
    }
    public ILogIdentifier getIdentifier()
    {
        return identifier;
    }
    public TreeItem<ILogTreeElement> getItem()
    {
        return item;
    }
    public String toString()
    {
        return label;
    }
}
