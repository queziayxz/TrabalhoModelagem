package org.trab.demo.controller;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import org.trab.demo.model.Psicologo;
import org.trab.demo.model.User;
import org.trab.demo.repository.UserRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
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
            dialogoErro.setContentText("Informe todos os campos para realizar o login");
            dialogoErro.showAndWait();
        } else {
            try {
                User user =  UserRepository.autenticate(tf_email.getText(),tf_password.getText());
                if(user != null) {
                    Sessao sessao = Sessao.getInstance();
                    sessao.setUser(user);
                    if(user.getIsPsicologo()) {
                        Telas.getTelaDashPsi();
                    } else {
                        System.out.println("entrou na dash paciente");
                        Telas.getTelaDashPaci();
                    }
                } else {
                    dialogoErro.setContentText("Usuário não encontrado!");
                    dialogoErro.showAndWait();
                }
            } catch (SQLException e) {
//                dialogoErro.setContentText("Error na consulta ao banco"+e.getMessage());
//                dialogoErro.showAndWait();
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
//                dialogoErro.setContentText("Error na consulta ao banco"+e.getMessage());
//                dialogoErro.showAndWait();
            }
        }
    }

    public void linkSemCadastro()
    {
        try {
            Telas.getTelaCadastro();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
