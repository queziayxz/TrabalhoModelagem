package org.trab.demo.controller;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import org.trab.demo.repository.UserRepository;

import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField tf_email;

    @FXML
    private PasswordField tf_password;

    @FXML
    private Button btn_login;

    @FXML
    protected void login() {
        Alert dialogoErro = new Alert(Alert.AlertType.INFORMATION);
        dialogoErro.setTitle("Erro Login");

        if(tf_email.getText().isEmpty() || tf_password.getText().isEmpty()) {
//            dialogoErro.setHeaderText("Esse é o cabeçalho...");
            dialogoErro.setContentText("Informe todos os campos para realizar o login");
            dialogoErro.showAndWait();
        } else {
            try {
                UserRepository.verifyLogin(tf_email.getText(),tf_password.getText());
            } catch (SQLException e) {
                dialogoErro.setContentText("Error na consulta ao banco"+e.getMessage());
                dialogoErro.showAndWait();
            }
        }
    }
}
