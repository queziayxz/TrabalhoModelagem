package org.trab.demo.controller;

import javafx.fxml.FXML;

import javafx.scene.control.*;

public class LoginController {

    @FXML
    private TextField tf_email;

    @FXML
    private PasswordField tf_password;

    @FXML
    private Button btn_login;

    @FXML
    protected void login() {
        System.out.println("email: "+tf_email.getText());
        System.out.println("senha: "+tf_password.getText());
    }
}
