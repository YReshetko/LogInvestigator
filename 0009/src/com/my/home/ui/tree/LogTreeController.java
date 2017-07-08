package com.my.home.ui.tree;

import com.my.home.storage.ILogIdentifier;
import com.my.home.util.JsonUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.*;
import java.util.regex.Pattern;

/**
 *
 */
public class LogTreeController
{
    private final static String FOLDER_SPLITTER = "[\\\\|/]{1,2}";
    private final static Pattern PATTERN = Pattern.compile(FOLDER_SPLITTER);

    private static final String LABEL = "Parsed log";
    //private final TreeView view;
    private final LogTreeRoot root;
    private ILogTreeListener listener;
    public LogTreeController(TreeView view)
    {
        //this.view = view;
        root = new LogTreeRoot(LABEL);
        view.setRoot(root.getItem());
        view.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ILogTreeElement>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<ILogTreeElement>> observable,
                                TreeItem<ILogTreeElement> oldValue,
                                TreeItem<ILogTreeElement> newValue) {
                ILogTreeElement selectedItem = newValue.getValue();
                selectedItem.select();
            }
        });
    }
    public void setTreeListener(ILogTreeListener listener)
    {
        this.listener = listener;
    }

    public void add(ILogIdentifier identifier)
    {
        LogTreeLeaf leaf = new LogTreeLeaf(identifier);
        leaf.addEventListener(listener);
        String path = identifier.getLogDescriptor().getDirectory();
        Deque<String> deque = new LinkedList<>();
        deque.addAll(Arrays.asList(path.split(PATTERN.pattern())));
        root.add(deque, leaf);
    }
    public void addAll(List<ILogIdentifier> identifiers)
    {
        identifiers.forEach(this::add);
    }
}
