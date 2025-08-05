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
import java.time.format.DateTimeFormatter;
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

        // Configurar data mínima para seleção (amanhã)
        datePickerNova.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now().plusDays(1)));
            }
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
        LocalDate hoje = LocalDate.now();

        if (selectedDate == null) {
            showAlert("Erro", "Por favor, selecione uma data para a nova consulta.");
            return;
        }

        // Verificar se a data selecionada é pelo menos amanhã
        if (selectedDate.isBefore(hoje.plusDays(1))) {
            showAlert("Erro", "Só é possível remarcar para datas a partir de " +
                    hoje.plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
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
        LocalDate hoje = LocalDate.now();

        // Verificar se a consulta atual pode ser cancelada (pelo menos 1 dia de antecedência)
        LocalDate dataConsultaAtual = consultaSelecionadaAtual.getHorarioConsulta().getData().toLocalDate();
        if (dataConsultaAtual.isBefore(hoje.plusDays(1))) {
            showAlert("Erro", "Não é possível remarcar consultas com menos de 1 dia de antecedência.");
            return;
        }

        // Verificar se a nova data é pelo menos amanhã
        LocalDate novaData = horarioSelecionadoNovo.getData().toLocalDate();
        if (novaData.isBefore(hoje.plusDays(1))) {
            showAlert("Erro", "A nova data deve ser a partir de " +
                    hoje.plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return;
        }

        // Verificar se não está tentando remarcar para o mesmo horário
        if (consultaSelecionadaAtual.getHorarioConsulta().getId().equals(horarioSelecionadoNovo.getId())) {
            showAlert("Erro", "Selecione um horário diferente para remarcar.");
            return;
        }

        try {
            // Cancelar consulta selecionada
            ConsultaRepository.cancelarConsulta(consultaSelecionadaAtual.getId());

            // Criar nova consulta com IDs
            Consulta novaConsulta = new Consulta();
            novaConsulta.setIdPaciente(paciente.getId());
            novaConsulta.setIdAgenda(horarioSelecionadoNovo.getId());

            // Agendar nova consulta
            ConsultaRepository.agendarConsulta(novaConsulta);

            // Atualizar status do novo horário
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
            Sessao.getInstance().setUser(null);
            Telas.getTelaLogin(null);
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível deslogar a sua conta");
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
            showError("Erro de Navegação", "Não foi possível ir para a tela de editar perfil");
        }
    }

    public void onAgendar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaAgendamento();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de agendamento");
        }
    }

    public void onRemarcar(MouseEvent mouseEvent) {
        showAlert("Remarcação", "Você já está na tela de remarcação");
    }

    public void onCancelar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaCancelamento();
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de Cancelamento");
        }
    }
}