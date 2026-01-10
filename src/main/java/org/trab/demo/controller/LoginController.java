package org.trab.demo.controller;

import javafx.fxml.FXML;

import javafx.scene.control.*;
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
    protected void login() {

        if(!validaCampos()) {
            showAlertWarning("Erro Login","","Informe todos os campos para realizar o login");
        } else {
            try {
                User user =  UserRepository.autenticate(this.tf_email.getText(),this.tf_password.getText());
                if(user != null) {
                    Sessao sessao = Sessao.getInstance();
                    sessao.setUser(user);
                    if(user.getIsPsicologo()) {
                        Telas.getTelaDashPsi();
                    } else {
                        Telas.getTelaDashPaci();
                    }
                } else {
                    showAlertWarning("Erro Login","","Usuário não encontrado!");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void showAlertWarning(String title, String header, String content)
    {
        Alert dialogoExe = new Alert(Alert.AlertType.WARNING);
        dialogoExe.setTitle(title);
        dialogoExe.setHeaderText(header);
        dialogoExe.setContentText(content);
        dialogoExe.showAndWait();
    }

    private boolean validaCampos()
    {
        if(tf_email.getText().isEmpty() || tf_password.getText().isEmpty()) {
            return false;
        }

        return true;
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
