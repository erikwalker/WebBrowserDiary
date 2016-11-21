/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxdocumentcurator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Erik.Walker
 */
public class FxDocumentCurator extends Application {
    
    private static Map<TreeItem, UrlInfo> urlInfoMap = new HashMap<>();
    private static Map<String, TreeItem> treeNodeMap = new HashMap<>();
    
    // as before, we use 2 maps to save state - one holds the mapping from 
    // treenode to UrlInfo, and the other from Url to treeNode
    
    private static String currentUrl = "https://en.wikipedia.org/wiki/Main_Page";
    
    private final TextField urlTextField = new TextField(currentUrl);
    private final Button goButton = new Button("Go!");
    
    private final TextField currentDisplayedUrlText = new TextField(currentUrl);
    private final WebView browser = new WebView();
    
    private final TextArea summaryTextArea = new TextArea();
    
    private final TreeView treeView = new TreeView<>(new TreeItem<>("Urls"));
    
    // properties can bind objects together -uni or bidirectional bindings 
    
    
    @Override
    public void start(Stage primaryStage) {
        
        // As before - menu bar
        MenuBar menuBar = new MenuBar();
        
        Menu fileMenu = new Menu("File");
        menuBar.getMenus().add(fileMenu);
        
        MenuItem saveMenuItem = new MenuItem("Save");
        MenuItem exitMenuItem = new MenuItem("Exit");
        fileMenu.getItems().add(saveMenuItem);
        fileMenu.getItems().add(exitMenuItem);
        
        
        // URL text area, go button, etc.
        // let's make the member variables, easier that way
        
        //sets width of text field
        urlTextField.setPrefColumnCount(30);

        HBox addressBarBox = new HBox(urlTextField, goButton,
                currentDisplayedUrlText);
        
        HBox.setMargin(urlTextField, new Insets(4));
        HBox.setMargin(goButton, new Insets(4));
        HBox.setMargin(currentDisplayedUrlText, new Insets(4));
        
        VBox browserBox = new VBox(addressBarBox, browser, summaryTextArea);
        
        // tree view
        
        // set the dimesions of the tree view and only one element can 
        // be selected at one time.
        treeView.setMinHeight(600);
        treeView.setMinWidth(200);
        treeView.setShowRoot(true);
        treeView.setEditable(true);
        treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // summary text area
        summaryTextArea.setWrapText(true);
        
        // the border pane and setting the layout for the UI
        
        BorderPane rootPane = new BorderPane();
        rootPane.setTop(menuBar);
        rootPane.setCenter(browserBox);
        rootPane.setLeft(treeView);
        
        Scene scene = new Scene(rootPane, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Note Taking Application");
        primaryStage.show();
        
        
        // connect the listeners
        
        // file menu listeners
        
        saveMenuItem.setOnAction(actionEvent -> {
            // get the file to save to, and write out state using the htmlwriter
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Notes");
            File selectedFile = fileChooser.showSaveDialog(primaryStage);
            
            if(selectedFile !=null)
            {
                HtmlWriter.writeToHTML(selectedFile.getAbsolutePath(),
                        urlInfoMap.values());
                
                // this line below, makes a call to a method called 'go' that
                // does not exist yet. (we are about to write to it) 
                // that will update all of the UI to the new URL
                go(selectedFile.toURI().toString());
                
            }
        });
        exitMenuItem.setOnAction(e -> Platform.exit());
        
        // address box listeners 
        goButton.setOnAction(e -> go(urlTextField.getText()));
        
        // this line binds the two textboxes and can show a link that we 
        // clicked in the browser -- properties and binding
        
        currentDisplayedUrlText.textProperty().
                bind(browser.getEngine().locationProperty());
        
        // treeview listeners
        
        // so for the treeView, we will need to write an entire class to make 
        // tree nodes editable 
        // the new class needs to intersect and handle the edit from this class
        treeView.setOnEditCommit(new EventHandler<TreeView.EditEvent>() {
            @Override
            public void handle(TreeView.EditEvent event) {
                UrlInfo urlInfo = urlInfoMap.get(event.getTreeItem());
                urlInfo.setHeadline(event.getNewValue().toString());
            }
        });
                         
        treeView.setCellFactory(p -> new TextFieldTreeCell());

        // one last bit of tree-related wiring: when the user clicks on a tree, the entire UI
        // should shift to reflect that new node
        
        // we find the new value of the listener and what has been selected 
        // then we find the url info object
        treeView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue ov, Object oldValue, Object newValue) -> {
                    TreeItem treeItem = (TreeItem) newValue;
                    if(newValue != null && urlInfoMap.containsKey(newValue)) {
                        UrlInfo urlInfo = urlInfoMap.get(treeItem);
                        urlTextField.setText(urlInfo.getUrl());
                        summaryTextArea.setText(urlInfo.getSummary());
                        browser.getEngine().load(urlInfo.getUrl());
                    }
                }
        ); 
        
        


        // summarytext area listeners
        // as the user edits the summary text area, update the underlying 
        // urlInfo object 
        
        summaryTextArea. textProperty().addListener( e -> {
            currentUrl = urlTextField.getText();
            if (treeNodeMap.containsKey(currentUrl)){
                UrlInfo urlInfo = urlInfoMap.get(treeNodeMap.get(currentUrl));
                urlInfo.setSummary(summaryTextArea.getText());
            }
        });
        
        
        
        
    }

    private void go(String url){
        // we need to update all of our state to point to the new URL.
        currentUrl = url;
        urlTextField.setText(currentUrl);
        browser.getEngine().load(currentUrl);
        TreeItem childNode = null;
        
        if(treeNodeMap.containsKey(currentUrl))
        {
            // this url has been added to the tree before, simply select it
            childNode = treeNodeMap.get(currentUrl);
        }
        else {
            // node has not yet been added to the tree, so add now
            childNode = new TreeItem<>(currentUrl);
            treeNodeMap.put(currentUrl, childNode);
            treeView.getRoot().getChildren().add(childNode);
        }
        // if the url is not in the tree we add it..
        if (!urlInfoMap.containsKey(childNode)){
            urlInfoMap.put(childNode, new UrlInfo(currentUrl));
        }    
        // by this pointm the node definitely exists in the tree - now select it
        treeView.getSelectionModel().select(childNode);
        
        // if the node has a summary we update the node with the summary
        summaryTextArea.setText(urlInfoMap.get(childNode).getSummary());
        
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
