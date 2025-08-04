package org.trab.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
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
            // Criar nova consulta usando os IDs
            Consulta novaConsulta = new Consulta();
            novaConsulta.setIdPaciente(paciente.getId());
            novaConsulta.setIdAgenda(horarioSelecionado.getId());

            // Inserir consulta no banco
            ConsultaRepository.agendarConsulta(novaConsulta);

            // Atualizar status do horário
            AgendaRepository.finalizaConsulta(horarioSelecionado.getId(), StatusConsultaEnum.AGENDADO.toString());

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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void onDeslogar(MouseEvent mouseEvent) {
        try {
            Sessao.getInstance().setUser(null); //define paciente da sessão NULL;
            Telas.getTelaLogin(null); //chama tela de login
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi deslogar do sistema");
        }
    }

    public void onInicio(MouseEvent mouseEvent) {
        try {
            Telas.getTelaDashPaci();//chama tela de paciente
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível ir para a tela inicial");
        }
    }

    public void onPerfil(MouseEvent mouseEvent) {
        try {
            Telas.getTelaPerfil(); //chama tela de Editar Perfil
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível ir para a tela de edição de perfil");
        }
    }

    public void onAgendar(MouseEvent mouseEvent) {
        showAlert("Agendamento", "você já está na tela de agendamento");
    }

    public void onRemarcar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaRemarcacao(); //chama tela de Editar Perfil
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de remarcação");
        }
    }

    public void onCancelar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaCancelamento(); //chama tela de Editar Perfil
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de cancelamento");
        }
    }
}