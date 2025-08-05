package org.trab.demo.controller;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Psicologo;
import org.trab.demo.model.User;
import org.trab.demo.repository.AgendaRepository;
import org.trab.demo.repository.UserRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.sql.Date;
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

//    @FXML
//    private PasswordField tf_senha;

    @FXML
    private TextField tf_telefone;
    @FXML
    private DatePicker tf_dataNascimento;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        btn_perfil.setDisable(true);

        this.tf_nome.setText(Sessao.getInstance().getUser(Psicologo.class).getNome());
        this.tf_telefone.setText(Sessao.getInstance().getUser(Psicologo.class).getTelefone());

        if(Sessao.getInstance().getUser(Psicologo.class).getDataNascimento() != null) {
            this.tf_dataNascimento.setValue(Sessao.getInstance().getUser(Psicologo.class).getDataNascimento().toLocalDate());
        }

        this.tf_crp.setText(Sessao.getInstance().getUser(Psicologo.class).getCrp());
        this.tf_cpf.setText(Sessao.getInstance().getUser(Psicologo.class).getCpf());
        this.tf_email.setText(Sessao.getInstance().getUser(Psicologo.class).getEmail());
//        this.tf_senha.setText(Sessao.getInstance().getUser(Psicologo.class).getSenha());

    }

    public void salvarDados()
    {
        try {

            this.validaCampos();

            Psicologo psicologo = new Psicologo();
            psicologo.setId(Sessao.getInstance().getUser(Psicologo.class).getId());
            psicologo.setNome(this.tf_nome.getText());
            psicologo.setTelefone(this.tf_telefone.getText());

            Date data = Date.valueOf(this.tf_dataNascimento.getValue());
            psicologo.setDataNascimento(data);

            psicologo.setCrp(this.tf_crp.getText());
            psicologo.setCpf(this.tf_cpf.getText());
            psicologo.setEmail(this.tf_email.getText());
            psicologo.setSenha(Sessao.getInstance().getUser(Psicologo.class).getSenha());

            Alert dialogoExe = new Alert(Alert.AlertType.CONFIRMATION);
            ButtonType btnEditar = new ButtonType("Editar");
            ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            dialogoExe.setTitle("Editando Perfil");
            dialogoExe.setContentText("Tem certeza que deseja editar suas informações?");
            dialogoExe.getButtonTypes().setAll(btnEditar, btnCancelar);
            dialogoExe.showAndWait().ifPresent(b -> {
                if (b == btnEditar) {
                    try {
                        UserRepository.updatePsicologo(psicologo);
                    } catch (SQLException e) {
                        Alert dialogoInfo = new Alert(Alert.AlertType.WARNING);
                        dialogoInfo.setTitle("Error");
                        dialogoInfo.setHeaderText("Não foi possível editar as informações!");
                        dialogoInfo.showAndWait();
                    }

                    Sessao.getInstance().setUser(psicologo);

                    Alert dialogoInfo = new Alert(Alert.AlertType.INFORMATION);
                    dialogoInfo.setContentText("Perfil Editado com Sucesso!");
                    dialogoInfo.showAndWait();
                }
            });
        } catch (IllegalArgumentException e) {
            Alert dialogoInfo = new Alert(Alert.AlertType.WARNING);
            dialogoInfo.setTitle("Error");
            dialogoInfo.setHeaderText(e.getMessage());
            dialogoInfo.showAndWait();
        }
    }

    private void validaCampos() throws IllegalArgumentException
    {
        if(this.tf_nome.getText().isEmpty() || this.tf_telefone.getText().isEmpty() ||
                this.tf_dataNascimento.getEditor().getText().isEmpty() || this.tf_crp.getText().isEmpty() ||
                this.tf_cpf.getText().isEmpty() || this.tf_email.getText().isEmpty()) {

            throw new IllegalArgumentException("Campos Vazios!");
        }

        try {
            User.validarCPF(this.tf_cpf.getText());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
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
