package org.trab.demo.controller;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.trab.demo.model.Psicologo;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditPsicologoController implements Initializable {
    @FXML
    private Button btn_salvar;
    @FXML
    private Button btn_perfil;

    @FXML
    private TextField tf_cpf;

    @FXML
    private TextField tf_crp;

    @FXML
    private TextField tf_email;

    @FXML
    private TextField tf_nome;

    @FXML
    private TextField tf_senha;

    @FXML
    private TextField tf_telefone;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        btn_perfil.setDisable(true);

        this.tf_nome.setText(Sessao.getInstance().getUser(Psicologo.class).getNome());
        this.tf_telefone.setText(Sessao.getInstance().getUser(Psicologo.class).getTelefone());
        this.tf_crp.setText(Sessao.getInstance().getUser(Psicologo.class).getCrp());
        this.tf_cpf.setText(Sessao.getInstance().getUser(Psicologo.class).getCpf());
        this.tf_email.setText(Sessao.getInstance().getUser(Psicologo.class).getEmail());
        this.tf_senha.setText(Sessao.getInstance().getUser(Psicologo.class).getSenha());

    }

    public void telaDashPsicologo() throws IOException
    {
        try {
            Telas.getTelaDashPsi();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
    public void telaAgenda() throws IOException
    {
        try {
            Telas.getTelaAgendaPsicologo();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void telaCadHorarios() throws IOException
    {
        try {
            Telas.getTelaCadHorarios();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void telaHistoricoConsultas() throws IOException
    {
        try {
            Telas.getTelaHistoricoConsultas();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}
