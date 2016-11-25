/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erikw.webbrowserdiary;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author Erik.Walker
 */
public class DiaryController {
    // wire up the private member variables for different UI elements 
    // we use the @FXML annotation. THis helps the FXMLLoader to match the 
    // UI elements to the correct number of member variables 
    // IT checks the names of the member variables and matches to name of
    // UI elements 
    
    @FXML
    private TextField urlTextField;
    
    @FXML
    private Button goButton;
    
    @FXML
    private TextField currentUrlTextField;
    
    @FXML
    private WebView browser;
    
    @FXML
    private TextArea summaryTextArea;
    
    @FXML
    private TreeView treeView;
    
    private String currentUrl = "https://en.wikipedia.org/wiki/Main_Page";
    
    
    // setup the maps with UrlInfo and TreeNode information
    private Map<TreeItem, UrlInfo> urlInfoMap = new HashMap<>();
    private Map<String, TreeItem> treeNodeMap = new HashMap<>();
    
    @FXML 
    public void initialize()
    {
        //browser.getEngine().load(currentUrl);
        // the initial method wit the @FXML notation from the FXMLLoader
        
        // now we know that the basic wiring works lets connect the logic
        
        // wire up the code to change the url . .when the go button clicked
        treeView.setRoot(new TreeItem<>("URLs"));
        // when application starts we will load first URL
        go(currentUrl);
        
        // lets bind the txt currentDisplayedURL to the contents
        // Url text field . The initialize() function 
        currentUrlTextField.textProperty().bind(browser.getEngine().locationProperty());
        
        // there are two more listeners to connect in the initialize method for
        // the treeView
        treeView.setCellFactory(p -> new RenameTextFieldTreeCell());
        
        treeView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue observable, Object oldValue, Object newValue) ->
                {
                    TreeItem treeItem = (TreeItem) newValue;
                    if(newValue!=null && urlInfoMap.containsKey(newValue)){
                        UrlInfo urlInfo = urlInfoMap.get(treeItem);
                        urlTextField.setText(urlInfo.getUrl());
                        summaryTextArea.setText(urlInfo.getSummary());
                        browser.getEngine().load(urlInfo.getUrl());
                    }
                });

    }
    
    private void go(String someUrl)
    {
        // update currentUrl internal state variable 
        this.currentUrl = someUrl;
        
        // update the url text field - this line is only needed if the 
        // change in URL is via the tree (and not urlTextField iteself)
        
        urlTextField.setText(currentUrl);
        
        // update the main browser display
        browser.getEngine().load(currentUrl);
        
        // figure out whether a new tree node needs to be added and we do this 
        // by checking the map and in either way we need to update
        
        TreeItem childNode = null;
        if(treeNodeMap.containsKey(currentUrl))
        {
            childNode = treeNodeMap.get(currentUrl);
        }
        else
        {
            childNode = new TreeItem(currentUrl);
            treeNodeMap.put(currentUrl, childNode);
            treeView.getRoot().getChildren().add(childNode);
        }
        
        // if the urlInfoMap does not contain childNode we update the maps
        if(!urlInfoMap.containsKey(childNode))
        {
            urlInfoMap.put(childNode,new UrlInfo(currentUrl));
        }
        
        // update the tree - again this line is not needed if the source of 
        // the update was the tree
        
        treeView.getSelectionModel().select(childNode);
        
        // update the summary text area so it shows the new summary 
        summaryTextArea.setText(urlInfoMap.get(childNode).getSummary());
    
        // update the selectionArea with the url to update the contents 
        // of the underlying urlInfo object
        summaryTextArea.textProperty().addListener(
            e -> {
                currentUrl = urlTextField.getText();
                if(treeNodeMap.containsKey(currentUrl))
                {
                    urlInfoMap.get(treeNodeMap.get(currentUrl))
                            .setSummary(summaryTextArea.getText());
                }
            });
    }
    
    
    
    @FXML
    public void handleExitMenuAction(){
        Platform.exit();
    }
 
    @FXML
    public void handleSaveMenuAction(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save note to file");
        
        // want to hardcode the name of the treeNodeName.. 
        
        fileChooser.setInitialFileName(LocalDateTime.now().toString() + ".html");
        //ArrayList<String> extensions = new ArrayList<>();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Web Page, complete"
                        + " (*.htm;*.html)"),
                new FileChooser.ExtensionFilter("Text", "*.txt"));
        
        //extensions.add("html");
//        fileChooser.setSelectedExtensionFilter(
//                new FileChooser.ExtensionFilter(currentUrl, extensions)
//        );
        File selectedFile = fileChooser.showSaveDialog(null);
        
        // setup teh save menu handler
        if(selectedFile !=null)
        {
            HtmlWriter.writeToHTML(selectedFile.getAbsolutePath(),
                    urlInfoMap.values());
            // saves an HTML file with a name user specifies.
            go(selectedFile.toURI().toString());
        
        }
    }
    @FXML
    public void handleGoButtonClick()
    {
        // we pass in the command in the urlTextField
        go(urlTextField.getText());
    }
    
    // code to handle the tree name edit on commiting change
     @FXML
    public void handleOnEditCommit(TreeView.EditEvent event){
        urlInfoMap.get(event.getTreeItem()).setHeadline(event.getNewValue().toString());
    }
    
  
}

    