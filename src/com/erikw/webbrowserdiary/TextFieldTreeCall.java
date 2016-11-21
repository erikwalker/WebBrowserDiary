package com.erikw.webbrowserdiary;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Created by Erik Walker on 11/19/16.
 */
public class TextFieldTreeCall extends TreeCell<String> {
    private TextField textField;

    public TextFieldTreeCall() {}

    // we need to override 3 methods - the 3 below. Basically, when the user
    // clicks on a tree node, its up to us to provde the functionality needed to
    // edit that node. We will do this by holding a textfield as a member variable,
    // and using this textfield to accept user edits.
    // That's the broad idea, but there is a lot of boilerplate needed to get this
    // seemingly simple task done.

    @Override
    public void startEdit() {
        super.startEdit();
        // always start by calling the base class method to make
        // sure we don't miss anything
        if(textField == null) {
            createTextField();
        }
        setText(null);
        setGraphic(textField);
        textField.selectAll();

        // the net effect of this method: once the user starts editing, we take
        // over the look and feel of the treecell, and use the textfield to represent
        // its contents, and to accept edits

    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        // as always use the base class method to make sure we don't miss anything

        setText(getString());
        setGraphic(getTreeItem().getGraphic());

        // net effect of this method: once the user bails out of editing,
        // go back to the default look and feel associated with the treecell.
    }

    @Override
    public void updateItem(String item, boolean isEmpty) {
        super.updateItem(item,isEmpty);
        // as always use the base class method to make sure we don't miss anything

        if(isEmpty) {
            setText(null);
            setGraphic(null);
        } else {
            if(isEditing()) {
                if(textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(getTreeItem().getGraphic());
            }

        }

    }

    private String getString() {
        return getItem() == null? "" : getItem().toString();
    }

    private void createTextField() {
        textField = new TextField();
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    // if the user hit Enter, use the commitEdit
                    // method (from the base class) to make sure that
                    // edit is saved
                    commitEdit(textField.getText());
                } else if(event.getCode() == KeyCode.ESCAPE) {
                    // if the user hits Escape, again use the base class method
                    // to make sure the right thing happens - this time use cancelEdit
                    cancelEdit();
                }
            }
        });
    }


}
