package org.trab.demo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.trab.demo.model.Paciente;
import org.trab.demo.repository.UserRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;

public class PerfilPacienteController {

    @FXML
    private TextField tfNome;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfTelefone;
    @FXML
    private TextField tfCPF;
    @FXML
    private DatePicker dpDataNascimento;
    @FXML
    private PasswordField tfSenha;
    @FXML
    private Button btnSalvar;
    @FXML
    private Button btnVoltar;

    private Paciente paciente;

    @FXML
    public void initialize() {
        carregarDadosUsuario();
    }

    private void carregarDadosUsuario() {
        paciente = Sessao.getInstance().getUser(Paciente.class);

        if (paciente != null) {
            tfNome.setText(paciente.getNome());
            tfEmail.setText(paciente.getEmail());
            tfTelefone.setText(paciente.getTelefone());
            tfCPF.setText(paciente.getCpf());

            // Converter java.util.Date para LocalDate
            if (paciente.getDataNascimento() != null) {
                LocalDate dataNasc = paciente.getDataNascimento().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                dpDataNascimento.setValue(dataNasc);
            }

        }
    }

    @FXML
    private void salvarAlteracoes() {
        if (validarCampos()) {
            try {
                // Atualizar dados do paciente
                paciente.setNome(tfNome.getText());
                paciente.setEmail(tfEmail.getText());
                paciente.setTelefone(tfTelefone.getText());

                // Atualizar data de nascimento se selecionada
                if (dpDataNascimento.getValue() != null) {
                    paciente.setDataNascimento(
                            Date.valueOf(dpDataNascimento.getValue())
                    );
                }

                // Garante a atualização da senha, apenas quando é preenchida
                if (!tfSenha.getText().isEmpty()) {
                    paciente.setSenha(tfSenha.getText());
                }

                UserRepository.atualizarUsuario(paciente);

                mostrarAlerta(Alert.AlertType.INFORMATION,
                        "Sucesso",
                        "Dados atualizados com sucesso!");

            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR,
                        "Erro no Banco de Dados",
                        "Falha ao atualizar dados: " + e.getMessage());
            }
        }
    }

    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();

        if (tfNome.getText().isEmpty()) {
            erros.append("- Nome é obrigatório\n");
        }

        if (tfEmail.getText().isEmpty()) {
            erros.append("- Email é obrigatório\n");
        } else if (!tfEmail.getText().contains("@")) {
            erros.append("- Email inválido\n");
        }

        if (tfTelefone.getText().isEmpty()) {
            erros.append("- Telefone é obrigatório\n");
        }

        if (tfCPF.getText().isEmpty()) {
            erros.append("- CPF é obrigatório\n");
        }

        if (erros.length() > 0) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Campos Inválidos",
                    "Corrija os seguintes erros:\n\n" + erros.toString());
            return false;
        }

        return true;
    }

    @FXML
    private void voltarParaDashboard() throws IOException {
        Telas.getTelaDashPaci();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}