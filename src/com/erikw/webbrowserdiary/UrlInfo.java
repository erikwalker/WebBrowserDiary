/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erikw.webbrowserdiary;

/**
 *
 * @author Erik.Walker
 */
public class UrlInfo {
    
    // this will hold the data of the URL's that we are going to browse
    // part of the model
    
    private String url;
    private String headline;
    private String summary;
    
    public UrlInfo(String url)
    {
        this.url = url;
        this.headline = url;
        this.summary = "";
}

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the headline
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * @param headline the headline to set
     */
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }
}