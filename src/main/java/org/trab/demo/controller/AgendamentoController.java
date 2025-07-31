package org.trab.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.model.Paciente;
import org.trab.demo.repository.AgendaRepository;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public class AgendamentoController {

    @FXML private DatePicker datePicker;
    @FXML private Button btnExibirConsultas;
    @FXML private Button btnAgendarConsulta;
    @FXML private TableView<Agenda> tableViewHorarios;
    @FXML private TableColumn<Agenda, Date> colDia;
    @FXML private TableColumn<Agenda, Time> colHora;

    private ObservableList<Agenda> horariosList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colDia.setCellValueFactory(new PropertyValueFactory<>("data"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        tableViewHorarios.setItems(horariosList);
        btnAgendarConsulta.setDisable(true);
        tableViewHorarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnAgendarConsulta.setDisable(newSelection == null);
        });
    }

    @FXML
    private void exibirHorarios(ActionEvent event) {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            showAlert("Erro", "Por favor, selecione uma data.");
            return;
        }

        try {
            List<Agenda> horarios = AgendaRepository.getHorariosData(Date.valueOf(selectedDate));
            horariosList.clear();

            for (Agenda horario : horarios) {
                if (horario.getStatus().equals(StatusConsultaEnum.LIVRE.toString())) {
                    horariosList.add(horario);
                }
            }

            if (horariosList.isEmpty()) {
                showAlert("Informação", "Não há horários disponíveis para a data selecionada.");
            }
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao buscar horários: " + e.getMessage());
        }
    }

    @FXML
    private void agendarConsulta(ActionEvent event) {
        Agenda horarioSelecionado = tableViewHorarios.getSelectionModel().getSelectedItem();
        if (horarioSelecionado == null) {
            showAlert("Erro", "Por favor, selecione um horário.");
            return;
        }

        Sessao sessao = Sessao.getInstance();
        Paciente paciente = sessao.getUser(Paciente.class);

        try {
            // Criar nova consulta usando o modelo existente
            Consulta novaConsulta = new Consulta();
            novaConsulta.setPaciente(paciente);
            novaConsulta.setHorarioConsulta(horarioSelecionado);

            // Inserir consulta no banco
            ConsultaRepository.agendarConsulta(novaConsulta);

            // Atualizar status do horário
            AgendaRepository.atualizarStatusHorario(horarioSelecionado.getId(), StatusConsultaEnum.AGENDADO.toString());

            showAlert("Sucesso", "Consulta agendada com sucesso!");
            tableViewHorarios.getSelectionModel().clearSelection();
            horariosList.clear();
            btnAgendarConsulta.setDisable(true);
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao agendar consulta: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void telaPerfil() throws IOException {
        Telas.getTelaPerfil();
    }

    @FXML
    private void telaAgendamento() throws IOException {
        Telas.getTelaAgendamento();
    }

    @FXML
    private void telaRemarcacao() throws IOException {
        Telas.getTelaRemarcacao();
    }

    @FXML
    private void telaCancelamento() throws IOException {
        Telas.getTelaCancelamento();
    }

    @FXML
    private void deslogar() throws IOException {
        Sessao.getInstance().setUser(null);
        Telas.getTelaLogin(null);
    }
}