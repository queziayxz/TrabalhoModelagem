package org.trab.demo.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.repository.AgendaRepository;
import org.trab.demo.repository.ConsultaRepository;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RemarcacaoController extends BaseConsultaController {

    @FXML private DatePicker datePickerNova;
    @FXML private Button btnExibirHorariosNovos;
    @FXML private Button btnRemarcarConsulta;
    @FXML private TableView<Consulta> tableViewConsultasAtuais;
    @FXML private TableColumn<Consulta, String> colDiaAtual;
    @FXML private TableColumn<Consulta, String> colHoraAtual;
    @FXML private TableView<Agenda> tableViewHorariosNovos;
    @FXML private TableColumn<Agenda, String> colDiaNovo;
    @FXML private TableColumn<Agenda, String> colHoraNovo;
    @FXML private Text textAjuda;
    @FXML private Button btnAgendarConsulta1;

    private ObservableList<Consulta> consultasAtuaisList;
    private ObservableList<Agenda> horariosNovosList;
    private Consulta consultaSelecionadaAtual;
    private Agenda horarioSelecionadoNovo;

    @FXML
    public void initialize() {
        configurarTabelas();

        tableViewConsultasAtuais.getSelectionModel().selectedItemProperty().addListener((obs, old, newSelection) -> {
            consultaSelecionadaAtual = newSelection;
            verificarSelecao();
        });

        tableViewHorariosNovos.getSelectionModel().selectedItemProperty().addListener((obs, old, newSelection) -> {
            horarioSelecionadoNovo = newSelection;
            verificarSelecao();
        });

        // Configurar clique no texto de ajuda
        if (textAjuda != null) {
            textAjuda.setOnMouseClicked(event -> onAjuda());
        }

        carregarConsultasAtuais();
    }

    private void configurarTabelas() {
        colDiaAtual.setCellValueFactory(cellData -> {
            Agenda agenda = cellData.getValue().getHorarioConsulta();
            return new SimpleStringProperty(formatDate(agenda.getData()));
        });

        colHoraAtual.setCellValueFactory(cellData -> {
            Agenda agenda = cellData.getValue().getHorarioConsulta();
            return new SimpleStringProperty(formatTime(agenda.getHora()));
        });

        colDiaNovo.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatDate(cellData.getValue().getData()))
        );

        colHoraNovo.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatTime(cellData.getValue().getHora()))
        );
    }

    @Override
    protected void verificarSelecao() {
        btnRemarcarConsulta.setDisable(consultaSelecionadaAtual == null || horarioSelecionadoNovo == null);
    }

    private void carregarConsultasAtuais() {
        try {
            consultasAtuaisList = carregarConsultasPaciente();
            tableViewConsultasAtuais.setItems(consultasAtuaisList);

            if (consultasAtuaisList.isEmpty()) {
                showAlert("Informação", "Você não tem consultas agendadas para remarcar.");
            }
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao buscar suas consultas: " + e.getMessage());
        }
    }

    @FXML
    private void exibirHorariosNovos(ActionEvent event) {
        LocalDate selectedDate = datePickerNova.getValue();
        LocalDate hoje = LocalDate.now();

        if (selectedDate == null) {
            showAlert("Atenção", "Por favor, selecione uma data para a nova consulta.");
            return;
        }

        if (selectedDate.isBefore(hoje.plusDays(1))) {
            showAlert("Atenção", "Você só pode remarcar para datas a partir de " +
                    hoje.plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return;
        }

        try {
            List<Agenda> horarios = AgendaRepository.getHorariosData(Date.valueOf(selectedDate));
            horariosNovosList = FXCollections.observableArrayList();

            for (Agenda horario : horarios) {
                if (horario.getStatus().equals(StatusConsultaEnum.LIVRE.toString())) {
                    horariosNovosList.add(horario);
                }
            }

            tableViewHorariosNovos.setItems(horariosNovosList);

            if (horariosNovosList.isEmpty()) {
                showAlert("Informação", "Não há horários disponíveis para a data selecionada.");
            }
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao buscar horários disponíveis: " + e.getMessage());
        }
    }

    @FXML
    private void remarcarConsulta(ActionEvent event) {
        if (!validarRemarcacao()) return;

        try {
            ConsultaRepository.cancelarConsulta(consultaSelecionadaAtual.getId());

            Consulta novaConsulta = new Consulta();
            novaConsulta.setIdPaciente(consultaSelecionadaAtual.getIdPaciente());
            novaConsulta.setIdAgenda(horarioSelecionadoNovo.getId());

            ConsultaRepository.agendarConsulta(novaConsulta);
            AgendaRepository.finalizaConsulta(
                    horarioSelecionadoNovo.getId(),
                    StatusConsultaEnum.AGENDADO.toString()
            );

            showAlert("Sucesso", "Sua consulta foi remarcada com sucesso!");
            reabrirTela();
        } catch (SQLException e) {
            showAlert("Erro", "Não foi possível remarcar sua consulta: " + e.getMessage());
        }
    }

    private boolean validarRemarcacao() {
        LocalDate hoje = LocalDate.now();
        LocalDate dataConsultaAtual = consultaSelecionadaAtual.getHorarioConsulta().getData().toLocalDate();
        LocalDate novaData = horarioSelecionadoNovo.getData().toLocalDate();

        if (!podeCancelarOuRemarcar(dataConsultaAtual)) {
            showAlert("Atenção", "Você só pode remarcar consultas com pelo menos 1 dia de antecedência.");
            return false;
        }

        if (novaData.isBefore(hoje.plusDays(1))) {
            showAlert("Atenção", "A nova data deve ser a partir de " +
                    hoje.plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            return false;
        }

        if (consultaSelecionadaAtual.getHorarioConsulta().getId().equals(horarioSelecionadoNovo.getId())) {
            showAlert("Atenção", "Selecione um horário diferente para remarcar.");
            return false;
        }

        return true;
    }

    // Método de ajuda com texto amigável
    @FXML
    private void onAjuda() {
        showAlert("Como remarcar uma consulta",
                "Passo a passo para remarcar uma consulta:\n\n" +
                        "1. Na tabela da ESQUERDA, selecione a consulta que deseja remarcar.\n" +
                        "2. Escolha uma NOVA DATA usando o calendário acima da tabela da DIREITA.\n" +
                        "3. Clique em 'Exibir Horários' para ver os horários disponíveis na nova data.\n" +
                        "4. Na tabela da DIREITA, selecione um novo horário disponível.\n" +
                        "5. Clique em 'Remarcar Consulta' para confirmar a alteração.\n" +
                        "Importante:\n" +
                        "- Você só pode remarcar consultas com pelo menos 1 dia de antecedência\n" +
                        "- A nova data deve ser pelo menos amanhã\n" +
                        "- Não é possível selecionar o mesmo horário atual\n\n\n");
    }

    // Implementação do método reabrirTela
    @Override
    protected void reabrirTela() {
        // Limpar seleções
        tableViewConsultasAtuais.getSelectionModel().clearSelection();
        tableViewHorariosNovos.getSelectionModel().clearSelection();
        consultaSelecionadaAtual = null;
        horarioSelecionadoNovo = null;

        // Limpar date picker
        datePickerNova.setValue(null);

        // Recarregar consultas atuais
        carregarConsultasAtuais();

        // Limpar horários novos
        if (horariosNovosList != null) {
            horariosNovosList.clear();
            tableViewHorariosNovos.setItems(horariosNovosList);
        }

        // Desabilitar botão
        btnRemarcarConsulta.setDisable(true);
    }

    // Sobrescreve apenas o método específico da tela atual
    @Override
    public void onRemarcar(MouseEvent mouseEvent) {
        showAlert("Remarcação", "Você já está na tela de remarcação de consultas");
    }
}