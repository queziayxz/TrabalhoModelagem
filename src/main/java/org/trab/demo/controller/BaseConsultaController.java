package org.trab.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Consulta;
import org.trab.demo.model.Paciente;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public abstract class BaseConsultaController {

    // Métodos comuns para exibição de alertas
    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Métodos de navegação comuns
    public void onDeslogar(MouseEvent mouseEvent) {
        try {
            Sessao.getInstance().setUser(null);
            Telas.getTelaLogin(null);
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível sair da sua conta");
        }
    }

    public void onInicio(MouseEvent mouseEvent) {
        try {
            Telas.getTelaDashPaci();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível voltar para a tela inicial");
        }
    }

    public void onPerfil(MouseEvent mouseEvent) {
        try {
            Telas.getTelaPerfil();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível acessar seu perfil");
        }
    }

    public void onAgendar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaAgendamento();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível acessar a tela de agendamento");
        }
    }

    public void onRemarcar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaRemarcacao();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível acessar a tela de remarcação");
        }
    }

    public void onCancelar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaCancelamento();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível acessar a tela de cancelamento");
        }
    }

    // Método para "reabrir" a tela atual
    protected void reabrirTela() {
    }

    // Métodos utilitários comuns
    protected String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    protected String formatTime(Time time) {
        if (time == null) return "";
        return new SimpleDateFormat("HH:mm").format(time);
    }

    // Método para carregar consultas do paciente
    protected ObservableList<Consulta> carregarConsultasPaciente() throws SQLException {
        Sessao sessao = Sessao.getInstance();
        Paciente paciente = sessao.getUser(Paciente.class);

        List<Consulta> consultas = ConsultaRepository.getConsultasByPaciente(paciente.getId());
        ObservableList<Consulta> consultasList = FXCollections.observableArrayList();

        for (Consulta consulta : consultas) {
            if (consulta.getHorarioConsulta().getStatus().equals(StatusConsultaEnum.AGENDADO.toString())) {
                consultasList.add(consulta);
            }
        }

        return consultasList;
    }

    // Método para verificar se uma consulta pode ser cancelada/remarcada
    protected boolean podeCancelarOuRemarcar(LocalDate dataConsulta) {
        return dataConsulta.isAfter(LocalDate.now());
    }

    // Método abstrato para cada controller implementar sua lógica específica
    protected abstract void verificarSelecao();
}