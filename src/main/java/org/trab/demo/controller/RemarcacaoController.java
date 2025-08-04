package org.trab.demo.controller;

import javafx.beans.property.SimpleObjectProperty;
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
            // Cancelar consulta selecionada
            ConsultaRepository.cancelarConsulta(consultaSelecionadaAtual.getId());

            // Criar nova consulta com IDs
            Consulta novaConsulta = new Consulta();
            novaConsulta.setIdPaciente(paciente.getId());
            novaConsulta.setIdAgenda(horarioSelecionadoNovo.getId());

            // Agendar nova consulta
            ConsultaRepository.agendarConsulta(novaConsulta);


            AgendaRepository.finalizaConsulta(
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
            showError("Erro de Navegação", "Não foi possível deslogar a sua conta");
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
            showError("Erro de Navegação", "Não foi possível ir para a tela de editar perfil");
        }
    }

    public void onAgendar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaAgendamento(); //chama tela de Editar Perfil
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de agendamento");
        }
    }

    public void onRemarcar(MouseEvent mouseEvent) {
        showAlert("Remarcação", "você já está na tela de remarcação");
    }

    public void onCancelar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaCancelamento(); //chama tela de Editar Perfil
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de Cancelamento");
        }
    }
}