package org.trab.demo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.model.Paciente;
import org.trab.demo.repository.AgendaRepository;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.util.Sessao;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AgendamentoController extends BaseConsultaController {

    @FXML private DatePicker datePicker;
    @FXML private Button btnExibirConsultas;
    @FXML private Button btnAgendarConsulta;
    @FXML private TableView<Agenda> tableViewHorarios;
    @FXML private TableColumn<Agenda, Date> colDia;
    @FXML private TableColumn<Agenda, Date> colHora;
    @FXML private Text textAjuda;
    @FXML private Button btnAgendarConsulta1;

    private ObservableList<Agenda> horariosList;

    @FXML
    public void initialize() {
        // Configuração para exibir data no formato DD/MM/YYYY
        colDia.setCellValueFactory(new PropertyValueFactory<>("data"));
        colDia.setCellFactory(column -> {
            return new TableCell<Agenda, Date>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        // Converte java.sql.Date para LocalDate e formata
                        LocalDate data = item.toLocalDate();
                        setText(data.format(formatter));
                    }
                }
            };
        });

        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));

        tableViewHorarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnAgendarConsulta.setDisable(newSelection == null);
        });

        // Configurar clique no texto de ajuda
        if (textAjuda != null) {
            textAjuda.setOnMouseClicked(event -> onAjuda());
        }
    }

    @Override
    protected void verificarSelecao() {
        // Implementação específica se necessário
    }

    @FXML
    private void exibirHorarios(ActionEvent event) {
        LocalDate selectedDate = datePicker.getValue();
        LocalDate hoje = LocalDate.now();

        if (selectedDate == null) {
            showAlert("Atenção", "Por favor, selecione uma data.");
            return;
        }

        if (selectedDate.isBefore(hoje.plusDays(1))) {
            showAlert("Atenção", "Você só pode agendar consultas a partir de amanhã (" +
                    hoje.plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ").");
            return;
        }

        try {
            List<Agenda> horarios = AgendaRepository.getHorariosData(Date.valueOf(selectedDate));
            horariosList = FXCollections.observableArrayList();

            for (Agenda horario : horarios) {
                if (horario.getStatus().equals(StatusConsultaEnum.LIVRE.toString())) {
                    horariosList.add(horario);
                }
            }

            tableViewHorarios.setItems(horariosList);

            if (horariosList.isEmpty()) {
                showAlert("Informação", "Não há horários disponíveis para a data selecionada.");
            }
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao buscar horários disponíveis: " + e.getMessage());
        }
    }

    @FXML
    private void agendarConsulta(ActionEvent event) {
        Agenda horarioSelecionado = tableViewHorarios.getSelectionModel().getSelectedItem();

        if (horarioSelecionado == null) {
            showAlert("Atenção", "Por favor, selecione um horário.");
            return;
        }

        if (!validarAgendamento(horarioSelecionado)) return;

        try {
            Consulta novaConsulta = new Consulta();
            novaConsulta.setIdPaciente(Sessao.getInstance().getUser(Paciente.class).getId());
            novaConsulta.setIdAgenda(horarioSelecionado.getId());

            ConsultaRepository.agendarConsulta(novaConsulta);
            AgendaRepository.finalizaConsulta(horarioSelecionado.getId(), StatusConsultaEnum.AGENDADO.toString());

            showAlert("Sucesso", "Sua consulta foi agendada com sucesso!");
            reabrirTela();
        } catch (SQLException e) {
            showAlert("Erro", "Não foi possível agendar sua consulta: " + e.getMessage());
        }
    }

    private boolean validarAgendamento(Agenda horario) {
        LocalDate dataConsulta = horario.getData().toLocalDate();

        if (dataConsulta.isBefore(LocalDate.now().plusDays(1))) {
            showAlert("Atenção", "Não é possível agendar consultas para hoje. Selecione uma data a partir de amanhã.");
            return false;
        }
        return true;
    }

    // Método de ajuda com texto amigável
    @FXML
    private void onAjuda() {
        showAlert("Como agendar uma consulta",
                "Passo a passo para agendar uma nova consulta:\n\n" +
                        "1. Escolha uma DATA usando o calendário acima da tabela.\n" +
                        "2. Clique em 'Exibir Horários' para ver os horários disponíveis na data escolhida.\n" +
                        "3. Na tabela abaixo, você verá todos os horários disponíveis.\n" +
                        "4. Clique em um horário para selecioná-lo.\n" +
                        "5. Clique em 'Agendar Consulta' para confirmar o agendamento.\n\n" +
                        "Importante: Você só pode agendar consultas a partir do dia seguinte ao atual.\n\n");
    }

    // Implementação do método reabrirTela
    @Override
    protected void reabrirTela() {
        // Limpar seleção
        tableViewHorarios.getSelectionModel().clearSelection();

        // Limpar date picker
        datePicker.setValue(null);

        // Limpar lista de horários
        if (horariosList != null) {
            horariosList.clear();
            tableViewHorarios.setItems(horariosList);
        }

        // Desabilitar botão
        btnAgendarConsulta.setDisable(true);
    }

    // Sobrescreve apenas o método específico da tela atual
    @Override
    public void onAgendar(MouseEvent mouseEvent) {
        showAlert("Agendamento", "Você já está na tela de agendamento de consultas");
    }
}