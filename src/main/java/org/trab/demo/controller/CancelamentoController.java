package org.trab.demo.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.model.Paciente;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

public class CancelamentoController {

    @FXML private TableView<Consulta> tableViewConsultas;
    @FXML private TableColumn<Consulta, String> colDia;
    @FXML private TableColumn<Consulta, String> colHora;
    @FXML private Button btnCancelarConsulta;

    private ObservableList<Consulta> consultasList = FXCollections.observableArrayList();
    private Consulta consultaSelecionada;

    @FXML
    public void initialize() {
        // Configurar as colunas usando as propriedades aninhadas
        colDia.setCellValueFactory(cellData -> {
            Agenda agenda = cellData.getValue().getHorarioConsulta();
            return new SimpleStringProperty(formatDate(agenda.getData()));
        });

        colHora.setCellValueFactory(cellData -> {
            Agenda agenda = cellData.getValue().getHorarioConsulta();
            return new SimpleStringProperty(formatTime(agenda.getHora()));
        });

        tableViewConsultas.setItems(consultasList);
        btnCancelarConsulta.setDisable(true);

        tableViewConsultas.getSelectionModel().selectedItemProperty().addListener((obs, old, newSelection) -> {
            consultaSelecionada = newSelection;
            verificarSelecao();
        });

        carregarConsultas();
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    private String formatTime(Time time) {
        if (time == null) return "";
        return new SimpleDateFormat("HH:mm").format(time);
    }

    private void verificarSelecao() {
        if (consultaSelecionada != null) {
            LocalDate dataConsulta = consultaSelecionada.getHorarioConsulta().getData().toLocalDate();
            boolean podeCancelar = dataConsulta.isAfter(LocalDate.now());
            btnCancelarConsulta.setDisable(!podeCancelar);
        } else {
            btnCancelarConsulta.setDisable(true);
        }
    }

    private void carregarConsultas() {
        Sessao sessao = Sessao.getInstance();
        Paciente paciente = sessao.getUser(Paciente.class);

        try {
            List<Consulta> consultas = ConsultaRepository.getConsultasByPaciente(paciente.getId());
            consultasList.clear();

            for (Consulta consulta : consultas) {
                if (consulta.getHorarioConsulta().getStatus().equals(StatusConsultaEnum.AGENDADO.toString())) {
                    consultasList.add(consulta);
                }
            }

            if (consultasList.isEmpty()) {
                showAlert("Informação", "Você não tem consultas agendadas para cancelar.");
            }
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao buscar consultas: " + e.getMessage());
        }
    }

    @FXML
    private void cancelarConsulta() {
        if (consultaSelecionada == null) {
            showAlert("Erro", "Selecione uma consulta para cancelar.");
            return;
        }

        LocalDate dataConsulta = consultaSelecionada.getHorarioConsulta().getData().toLocalDate();
        if (!dataConsulta.isAfter(LocalDate.now())) {
            showAlert("Erro", "Só é possível cancelar consultas com pelo menos 1 dia de antecedência.");
            return;
        }

        try {
            ConsultaRepository.cancelarConsulta(consultaSelecionada.getId());
            showAlert("Sucesso", "Consulta cancelada com sucesso!");
            carregarConsultas();
            btnCancelarConsulta.setDisable(true);
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao cancelar consulta: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            showError("Erro de Navegação", "Não foi possível deslogar");
        }
    }

    public void onInicio(MouseEvent mouseEvent) {
        try {
            Telas.getTelaDashPaci();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível ir para a tela inicial");
        }
    }

    public void onPerfil(MouseEvent mouseEvent) {
        try {
            Telas.getTelaPerfil();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível acessar o perfil");
        }
    }

    public void onAgendar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaAgendamento();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível acessar o agendamento");
        }
    }

    public void onRemarcar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaRemarcacao();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível acessar a remarcação");
        }
    }

    public void onCancelar(MouseEvent mouseEvent) {
        showAlert("Cancelamento", "Você já está na tela de cancelamento");
    }
}