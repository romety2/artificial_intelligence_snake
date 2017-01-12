package com.nanovash.snakenn;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application
{
    @Override
    public void start(Stage window) throws Exception
    {
        Parent c = FXMLLoader.load(getClass().getResource("/main.fxml"));
        window.setTitle("Snake");
        window.setScene(new Scene(c));
        window.setResizable(false);
        window.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
