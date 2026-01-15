package org.trab.demo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.trab.demo.model.Paciente;
import org.trab.demo.model.User;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Optional;

public class RegisterController {

    @FXML private TextField tf_name;
    @FXML private TextField tf_email;
    @FXML private TextField tf_phone;
    @FXML private TextField tf_cpf;
    @FXML private DatePicker dp_birth_date;
    @FXML private PasswordField tf_password;
    @FXML private Button btn_register;
    @FXML private Hyperlink link_login;

    @FXML
    public void initialize() {
        // Configura ação do botão de cadastro
        btn_register.setOnAction(event -> handleRegister());
    }

    private void handleRegister() {
        boolean btnClick = showAlertConfirmation("Cadastrar","Cadastro Paciente","",
                "Certeza que deseja se cadastrar no sistema?");
        if(btnClick) {
            try {
                // Validar campos obrigatórios
                if (!validateFields()) {
                    showAlert("Erro", "Por favor, preencha todos os campos corretamente");
                    return;
                }

                // Validar CPF
                User.validarCPF(tf_cpf.getText());

                // Criar objeto Paciente
                Paciente paciente = new Paciente();
                paciente.setNome(tf_name.getText());
                paciente.setEmail(tf_email.getText());
                paciente.setTelefone(tf_phone.getText());
                paciente.setCpf(tf_cpf.getText().replaceAll("[^\\d]", ""));
                paciente.setSenha(tf_password.getText());
                paciente.setIsPsicologo(false);
                paciente.setDataNascimento(
                        Date.valueOf(dp_birth_date.getValue())
                );

                // Inserir no banco de dados
                insertPaciente(paciente);

                // Feedback e redirecionamento
                showAlert("Sucesso", "Cadastro realizado com sucesso!");
                Telas.getTelaLogin(null);

            } catch (IllegalArgumentException e) {
                showAlert("Erro de CPF", e.getMessage());
            } catch (Exception e) {
                showAlert("Erro", "Ocorreu um erro: " + e.getMessage());
            }
        }
    }

    private boolean validateFields() {
        return !tf_name.getText().isEmpty() &&
                !tf_email.getText().isEmpty() &&
                !tf_phone.getText().isEmpty() &&
                !tf_cpf.getText().isEmpty() &&
                !tf_password.getText().isEmpty() &&
                dp_birth_date.getValue() != null;
    }

    private void insertPaciente(Paciente paciente) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, telefone, cpf, senha, data_nascimento, is_psicologo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (var conn = org.trab.demo.util.Conexao.getConn();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getEmail());
            stmt.setString(3, paciente.getTelefone());
            stmt.setString(4, paciente.getCpf());
            stmt.setString(5, paciente.getSenha());
            stmt.setDate(6, paciente.getDataNascimento());
            stmt.setBoolean(7, false); // is_psicologo = false

            stmt.executeUpdate();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected boolean showAlertConfirmation(String btnConfirmText, String title, String header, String content)
    {
        Alert dialogoExe = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType btnConfirm = new ButtonType(btnConfirmText);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialogoExe.setTitle(title);
        dialogoExe.setHeaderText(header);
        dialogoExe.setContentText(content);
        dialogoExe.getButtonTypes().setAll(btnConfirm, btnCancelar);

        Optional<ButtonType> result = dialogoExe.showAndWait();

        return (result.isPresent() && result.get() == btnConfirm);
    }

    public void linkPossuiCadastro() {
        try {
            Telas.getTelaLogin(null);
        } catch (IOException e) {
            showAlert("Erro", "Não foi possível abrir a tela de login");
        }
    }
}