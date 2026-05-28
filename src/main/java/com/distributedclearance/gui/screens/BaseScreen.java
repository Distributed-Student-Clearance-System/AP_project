package com.distributedclearance.gui.screens;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

public abstract class BaseScreen extends BorderPane {

    public BaseScreen() {
        setPadding(new Insets(20));
    }

    protected abstract void initialize();
}