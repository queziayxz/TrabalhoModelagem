package org.trab.demo;

import javafx.application.Application;
import javafx.stage.Stage;
import org.trab.demo.util.Conexao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            Telas.getTelaLogin(stage);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws SQLException {
        Conexao.getConn();
        launch();
    }
}