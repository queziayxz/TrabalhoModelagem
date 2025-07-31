package org.trab.demo.controller;

import javafx.beans.property.SimpleObjectProperty;
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

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public class RemarcacaoController {

    @FXML private DatePicker datePickerAtual;
    @FXML private DatePicker datePickerNova;
    @FXML private Button btnExibirConsultasAtuais;
    @FXML private Button btnExibirHorariosNovos;
    @FXML private Button btnRemarcarConsulta;
    @FXML private TableView<Consulta> tableViewConsultasAtuais;
    @FXML private TableColumn<Consulta, Date> colDiaAtual;
    @FXML private TableColumn<Consulta, Time> colHoraAtual;
    @FXML private TableView<Agenda> tableViewHorariosNovos;
    @FXML private TableColumn<Agenda, Date> colDiaNovo;
    @FXML private TableColumn<Agenda, Time> colHoraNovo;

    private ObservableList<Consulta> consultasAtuaisList = FXCollections.observableArrayList();
    private ObservableList<Agenda> horariosNovosList = FXCollections.observableArrayList();
    private Consulta consultaSelecionadaAtual;
    private Agenda horarioSelecionadoNovo;

    @FXML
    public void initialize() {

        // Configurar tabela de consultas atuais
        colDiaAtual.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getHorarioConsulta().getData()));
        colHoraAtual.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getHorarioConsulta().getHora()));
        tableViewConsultasAtuais.setItems(consultasAtuaisList);

        // Configurar tabela de novos horários
        colDiaNovo.setCellValueFactory(new PropertyValueFactory<>("data"));
        colHoraNovo.setCellValueFactory(new PropertyValueFactory<>("hora"));
        tableViewHorariosNovos.setItems(horariosNovosList);

        btnRemarcarConsulta.setDisable(true);

        tableViewConsultasAtuais.getSelectionModel().selectedItemProperty().addListener((obs, old, newSelection) -> {
            consultaSelecionadaAtual = newSelection;
            verificarSelecoes();
        });

        tableViewHorariosNovos.getSelectionModel().selectedItemProperty().addListener((obs, old, newSelection) -> {
            horarioSelecionadoNovo = newSelection;
            verificarSelecoes();
        });
    }

    private void verificarSelecoes() {
        btnRemarcarConsulta.setDisable(consultaSelecionadaAtual == null || horarioSelecionadoNovo == null);
    }

    @FXML
    private void exibirConsultasAtuais(ActionEvent event) {
        Sessao sessao = Sessao.getInstance();
        Paciente paciente = sessao.getUser(Paciente.class);

        try {
            List<Consulta> consultas = ConsultaRepository.getConsultasByPaciente(paciente.getId());
            consultasAtuaisList.clear();

            for (Consulta consulta : consultas) {
                if (consulta.getHorarioConsulta().getStatus().equals(StatusConsultaEnum.AGENDADO.toString())) {
                    consultasAtuaisList.add(consulta);
                }
            }

            if (consultasAtuaisList.isEmpty()) {
                showAlert("Informação", "Você não tem consultas agendadas.");
            }
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao buscar consultas: " + e.getMessage());
        }
    }

    @FXML
    private void exibirHorariosNovos(ActionEvent event) {
        LocalDate selectedDate = datePickerNova.getValue();
        if (selectedDate == null) {
            showAlert("Erro", "Por favor, selecione uma data para a nova consulta.");
            return;
        }

        try {
            List<Agenda> horarios = AgendaRepository.getHorariosData(Date.valueOf(selectedDate));
            horariosNovosList.clear();

            for (Agenda horario : horarios) {
                if (horario.getStatus().equals(StatusConsultaEnum.LIVRE.toString())) {
                    horariosNovosList.add(horario);
                }
            }

            if (horariosNovosList.isEmpty()) {
                showAlert("Informação", "Não há horários disponíveis para a data selecionada.");
            }
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao buscar horários: " + e.getMessage());
        }
    }

    @FXML
    private void remarcarConsulta(ActionEvent event) {
        Sessao sessao = Sessao.getInstance();
        Paciente paciente = sessao.getUser(Paciente.class);

        try {
            // Cancelar consulta atual
            ConsultaRepository.cancelarConsulta(consultaSelecionadaAtual.getId());
            AgendaRepository.atualizarStatusHorario(
                    consultaSelecionadaAtual.getHorarioConsulta().getId(),
                    StatusConsultaEnum.LIVRE.toString()
            );

            // Criar nova consulta
            Consulta novaConsulta = new Consulta();
            novaConsulta.setPaciente(paciente);
            novaConsulta.setHorarioConsulta(horarioSelecionadoNovo);

            // Agendar nova consulta
            ConsultaRepository.agendarConsulta(novaConsulta);
            AgendaRepository.atualizarStatusHorario(
                    horarioSelecionadoNovo.getId(),
                    StatusConsultaEnum.AGENDADO.toString()
            );

            showAlert("Sucesso", "Consulta remarcada com sucesso!");

            // Atualizar as listas
            exibirConsultasAtuais(null);
            exibirHorariosNovos(null);
            btnRemarcarConsulta.setDisable(true);
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao remarcar consulta: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}