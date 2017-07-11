package com.my.home.log.manager.web;

import com.my.home.factory.SpringBeanFactory;
import com.my.home.log.beans.ThreadsInfo;
import com.my.home.log.manager.ILogManager;
import com.my.home.storage.ILogIdentifier;
import com.my.home.ui.App;
import com.my.home.util.JsonUtils;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class WebLogManager implements ILogManager, IWebJsLogManager
{
    private final WebView webView;
    private final WebEngine webEngine;
    private List<String> selectedThreads;
    private App app;
    public WebLogManager(WebView webView)
    {
        this.webView = webView;
        webEngine = webView.getEngine();


        webEngine.setOnError(error -> System.out.println(error.getMessage()));
        webEngine.setJavaScriptEnabled(true);

        URL url = getClass().getResource("../../../../../../web/index.html");
        try {
            System.out.println("Url: " + url);
            webEngine.load(url.toExternalForm());
            JSObject jsObject = (JSObject) webEngine.executeScript("window");
            jsObject.setMember("app", this);
            //webEngine.executeScript("init()");
        } catch (Exception e) {
            e.printStackTrace();
        }
        app = (App) SpringBeanFactory.getInstance().getBean("Application");
    }

    @Override
    public void setThreadsInfo(ThreadsInfo value) {
        webEngine.executeScript("setThreads("+ JsonUtils.getJson(value)+")");
    }


    /**
     * Call back from JS
     * Download separate thread
     * @param value
     */
    @Override
    public void download(String value) {
        System.out.println("Returned value from JS: " + value);
        app.downloadThreadAndOpen(value);
    }

    /**
     * This method will be called from JavaScript to fill list of selected threads
     * @param threadName - thread name to add
     */
    @Override
    public void addSelectedThread(String threadName) {
        if(selectedThreads!=null)
        {
            selectedThreads.add(threadName);
        }
    }

    /**
     * After execution of this JS method, it will call back:
     * @see {@link com.my.home.log.manager.web.WebLogManager#addSelectedThread}
     * To fill new List with selected thread names
     * @return - list of selected thread names
     */
    @Override
    public List<String> getSelectedThreads()
    {
        selectedThreads = new ArrayList<>();
        //  After execution of this JS method, it will call back java method
        webEngine.executeScript("getSelectedThreads()");
        return selectedThreads;
    }
}
