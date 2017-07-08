package com.my.home.log.manager.web;

import com.my.home.log.beans.ThreadsInfo;
import com.my.home.log.manager.ILogManager;
import com.my.home.util.JsonUtils;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 *
 */
public class WebLogManager implements ILogManager, IWebJsLogManager
{
    private final WebView webView;
    private final WebEngine webEngine;
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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //webEngine.load("http://www.tut.by");
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
    }

    @Override
    public List<String> getSelectedThreads() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
