package com.my.home.ui.tree;

import javafx.scene.control.TreeItem;

import java.util.*;

/**
 *
 */
public class LogTreeRoot implements ILogTreeElement
{
    private final Map<String, LogTreeRoot> roots;
    private final List<LogTreeLeaf> leafs;
    private final String label;
    private final TreeItem<ILogTreeElement> item;
    public LogTreeRoot(String label) {
        this.label = label;
        roots = new HashMap<>();
        leafs = new LinkedList<>();
        item = new TreeItem<ILogTreeElement>(this);
    }

    public void add(Deque<String> dirNames, LogTreeLeaf leaf)
    {
        if (dirNames.size() > 0)
        {
            String name = dirNames.pollFirst();
            LogTreeRoot root = roots.get(name);
            if (root == null)
            {
                root = new LogTreeRoot(name);
                roots.put(name, root);
                item.getChildren().add(root.item);
            }
            root.add(dirNames, leaf);
        }
        else if(leaf != null)
            {
                leafs.add(leaf);
                item.getChildren().add(leaf.getItem());
            }
    }

    public TreeItem<ILogTreeElement> getItem()
    {
        return item;
    }

    @Override
    public String toString()
    {
        return label;
    }

    @Override
    public void select() {
        //Nothing to do
    }
}
