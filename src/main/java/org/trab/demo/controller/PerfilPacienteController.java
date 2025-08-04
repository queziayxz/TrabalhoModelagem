package org.trab.demo.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.trab.demo.model.Paciente;
import org.trab.demo.repository.UserRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;


import java.time.LocalDate;

import java.util.Calendar;
import java.util.Date;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

public class PerfilPacienteController {

    @FXML private TextField tfNome;
    @FXML private TextField tfEmail;
    @FXML private TextField tfTelefone;
    @FXML private TextField tfCPF;
    @FXML private DatePicker dpDataNascimento;
    @FXML private PasswordField tfSenha;
    @FXML private Button btnSalvar;
    @FXML private Button btnVoltar;

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

            if (paciente.getDataNascimento() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(paciente.getDataNascimento());
                LocalDate dataNasc = LocalDate.of(
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH) + 1,
                        cal.get(Calendar.DAY_OF_MONTH)
                );
                dpDataNascimento.setValue(dataNasc);
            }
        }
    }

    @FXML
    private void salvarAlteracoes() {
        if (validarCampos()) {
            try {
                paciente.setNome(tfNome.getText());
                paciente.setEmail(tfEmail.getText());
                paciente.setTelefone(tfTelefone.getText());

                if (dpDataNascimento.getValue() != null) {
                    LocalDate localDate = dpDataNascimento.getValue();
                    Calendar cal = Calendar.getInstance();
                    cal.set(localDate.getYear(),
                            localDate.getMonthValue() - 1,
                            localDate.getDayOfMonth());
                    paciente.setDataNascimento(cal.getTime());
                }

                if (!tfSenha.getText().isEmpty()) {
                    paciente.setSenha(tfSenha.getText());
                }

                UserRepository.updatePaciente(paciente);

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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onDeslogar(MouseEvent mouseEvent) {
        try {
            Sessao.getInstance().setUser(null);
            Telas.getTelaLogin(null);
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de login");
        }
    }

    public void onInicio(MouseEvent mouseEvent) {
        try {
            Telas.getTelaDashPaci();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela inicial");
        }
    }

    public void onPerfil(MouseEvent mouseEvent) {
        showError("Edição de perfil", "você já está na tela de edição de perfil");
    }

    public void onAgendar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaAgendamento();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de agendamento");
        }
    }

    public void onRemarcar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaRemarcacao();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de remarcação");
        }
    }

    public void onCancelar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaCancelamento();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de cancelamento");
        }
    }
}
