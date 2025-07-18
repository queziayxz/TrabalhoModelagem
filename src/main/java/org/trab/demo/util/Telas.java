package org.trab.demo.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.trab.demo.HelloApplication;

import java.io.IOException;

public class Telas {

    private static Stage stageC;
    public static void getTelaLogin(Stage stage) throws IOException
    {
        stageC = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
//        stage.setTitle("Hello!");
        stageC.setScene(scene);
        stageC.show();
    }

    public static void getTelaDashPsi() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/dash_adm.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
//        stage.setTitle("Hello!");
        stageC.setScene(scene);
        stageC.show();
    }

    public static void getTelaDashPaci() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/dash_Paciente.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
//        stage.setTitle("Hello!");
        stageC.setScene(scene);
        stageC.show();
    }

}
