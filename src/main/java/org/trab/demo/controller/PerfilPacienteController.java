package org.trab.demo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.trab.demo.model.Paciente;
import org.trab.demo.model.User;
import org.trab.demo.repository.UserRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class PerfilPacienteController {

    @FXML private TextField tfNome;
    @FXML private TextField tfEmail;
    @FXML private TextField tfTelefone;
    @FXML private TextField tfCPF;
    @FXML private DatePicker dpDataNascimento;
    @FXML private PasswordField tfSenha;
    @FXML private Button btnSalvar;

    private Paciente paciente;
    private String cpfOriginal;

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
            cpfOriginal = paciente.getCpf();

            if (paciente.getDataNascimento() != null) {
                dpDataNascimento.setValue(paciente.getDataNascimento().toLocalDate());
            }
        }
    }

    @FXML
    private void salvarAlteracoes() {
        if (validarCampos()) {
            try {

                String novoCPF = tfCPF.getText().replaceAll("[^\\d]", "");
                boolean cpfAlterado = !novoCPF.equals(cpfOriginal);
                //verificamos se o CPF foi alterado
                if (cpfAlterado) {
                    User.validarCPF(novoCPF);

                    if (UserRepository.cpfExiste(novoCPF)) {
                        mostrarAlerta(Alert.AlertType.ERROR,
                                "CPF já cadastrado",
                                "Este CPF já está cadastrado para outro usuário.");
                        return;
                    }
                }

                // Atualizar dados do paciente
                paciente.setNome(tfNome.getText());
                paciente.setEmail(tfEmail.getText());
                paciente.setTelefone(tfTelefone.getText());
                paciente.setCpf(novoCPF);

                // Atualizar data de nascimento
                if (dpDataNascimento.getValue() != null) {
                    paciente.setDataNascimento(java.sql.Date.valueOf(dpDataNascimento.getValue()));
                }

                // Atualizar senha se fornecida
                if (!tfSenha.getText().isEmpty()) {
                    paciente.setSenha(tfSenha.getText());
                }

                // Atualizar no banco de dados usando o mesmo método
                UserRepository.updatePaciente(paciente);

                // Atualizar sessão e CPF original
                Sessao.getInstance().setUser(paciente);
                cpfOriginal = novoCPF;

                mostrarAlerta(Alert.AlertType.INFORMATION,
                        "Sucesso",
                        "Dados atualizados com sucesso!");

            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.ERROR,
                        "CPF Inválido",
                        e.getMessage());
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR,
                        "Erro no Banco de Dados",
                        "Falha ao atualizar dados: " + e.getMessage());
            }
        }
    }

    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();

        // Padrões de validação
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        Pattern telefonePattern = Pattern.compile("^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$");
        Pattern cpfPattern = Pattern.compile("^\\d{11}$");

        // Validação de nome
        if (tfNome.getText() == null || tfNome.getText().trim().isEmpty()) {
            erros.append("- Nome é obrigatório\n");
        }

        // Validação de email
        if (tfEmail.getText() == null || tfEmail.getText().trim().isEmpty()) {
            erros.append("- Email é obrigatório\n");
        } else if (!emailPattern.matcher(tfEmail.getText()).matches()) {
            erros.append("- Email inválido\n");
        }

        // Validação de telefone
        if (tfTelefone.getText() == null || tfTelefone.getText().trim().isEmpty()) {
            erros.append("- Telefone é obrigatório\n");
        } else if (!telefonePattern.matcher(tfTelefone.getText()).matches()) {
            erros.append("- Telefone inválido (formato: (XX) XXXXX-XXXX)\n");
        }

        // Validação de CPF
        String cpf = tfCPF.getText().replaceAll("[^\\d]", "");
        if (cpf.isEmpty()) {
            erros.append("- CPF é obrigatório\n");
        } else if (!cpfPattern.matcher(cpf).matches()) {
            erros.append("- CPF inválido (deve ter 11 dígitos)\n");
        }

        // Validação de data de nascimento
        if (dpDataNascimento.getValue() == null) {
            erros.append("- Data de nascimento é obrigatória\n");
        } else if (dpDataNascimento.getValue().isAfter(LocalDate.now().minusYears(10))) {
            erros.append("- Data de nascimento inválida (mínimo 10 anos)\n");
        }

        // Exibir erros se houver
        if (erros.length() > 0) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Campos Inválidos",
                    "Corrija os seguintes erros:\n\n" + erros.toString());
            return false;
        }

        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    public void onDeslogar(MouseEvent mouseEvent) {
        try {
            Sessao.getInstance().setUser(null);
            Telas.getTelaLogin(null);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Erro de Navegação",
                    "Não foi possível abrir a tela de login");
        }
    }

    public void onInicio(MouseEvent mouseEvent) {
        try {
            Telas.getTelaDashPaci();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Erro de Navegação",
                    "Não foi possível abrir a tela inicial");
        }
    }

    public void onPerfil(MouseEvent mouseEvent) {
        mostrarAlerta(Alert.AlertType.INFORMATION,
                "Edição de perfil",
                "Você já está na tela de edição de perfil");
    }

    public void onAgendar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaAgendamento();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Erro de Navegação",
                    "Não foi possível abrir a tela de agendamento");
        }
    }

    public void onRemarcar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaRemarcacao();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Erro de Navegação",
                    "Não foi possível abrir a tela de remarcação");
        }
    }

    public void onCancelar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaCancelamento();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Erro de Navegação",
                    "Não foi possível abrir a tela de cancelamento");
        }
    }
}