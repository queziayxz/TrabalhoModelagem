package org.trab.demo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.repository.AgendaRepository;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class AgendaController implements Initializable {
    @FXML
    private Text btn_agenda;

    @FXML
    private Button btn_cancelarConsulta;

    @FXML
    private Button btn_deletarHorario;
    @FXML
    private Button btn_consultaRealizada;
    @FXML
    private Button btn_consultaNaoRealizada;

    @FXML
    private Button btn_mostraHorarios;

    @FXML
    private DatePicker data_picker;

    @FXML
    private Label lb_data;

    @FXML
    private Label lb_semHorario;
    @FXML
    private Label lb_selectData;
    @FXML
    private Label lb_dataSelecionada;

    @FXML
    private GridPane grid_horarios;

    @FXML
    private TextField tf_email;

    @FXML
    private TextField tf_telefone;

    @FXML
    private TextField tf_nome;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        btn_agenda.setDisable(true);
    }

    public void mostrarHorariosDia()
    {
        try {
            this.validaCampos();

            LocalDate data = this.data_picker.getValue();
            Date dateSql = Date.valueOf(data);
            List<Consulta> consultas = ConsultaRepository.getConsultasHorariosData(dateSql);

            resetaCampos();

            String formattedDate = formattedDateString(dateSql);
            this.lb_dataSelecionada.setText(formattedDate);
            this.lb_dataSelecionada.setVisible(true);

            this.lb_selectData.setVisible(false);

            if(consultas.isEmpty()) {
                this.lb_semHorario.setVisible(true);
            } else {
                for(int i = 0; i < consultas.size(); i++) {
                    String formattedTime = formattedTimeString(consultas.get(i).getHorarioConsulta().getHora());

                    Button button = new Button(formattedTime);
                    button.setPrefWidth(85);
                    button.setPrefHeight(26);
                    button.setStyle("-fx-font-size:18");

                    button.setOnAction(this::selecionaHorario);

                    Label label = new Label();
                    label.setStyle("-fx-font-size:18");

                    if(consultas.get(i).getPaciente() != null) {
                        button.setUserData(consultas.get(i));

                        final String agendado = StatusConsultaEnum.AGENDADO.toString();

                        switch (StatusConsultaEnum.valueOf(consultas.get(i).getHorarioConsulta().getStatus())) {
                            case AGENDADO:
                                label.setText(consultas.get(i).getPaciente().getNome());
                                break;
                            case CONCLUIDO:
                                label.setText("Consulta Realizada");
                                break;
                            case NAO_REALIZADO:
                                label.setText("Consulta não Realizada");
                                break;
                        }
                    } else {
                        this.btn_deletarHorario.setUserData(consultas.get(i).getHorarioConsulta());
                        label.setText("Horário Livre");
                    }

                    this.grid_horarios.add(button, 0, i);
                    this.grid_horarios.add(label, 1, i);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlertWarning("Error",e.getMessage(),"");
        }
    }

    public void selecionaHorario(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        Consulta consulta = (Consulta) button.getUserData();

        if(consulta != null) {
            this.tf_nome.setText(consulta.getPaciente().getNome());
            this.tf_telefone.setText(consulta.getPaciente().getTelefone());
            this.tf_email.setText(consulta.getPaciente().getEmail());
            this.btn_deletarHorario.setDisable(true);

            if(consulta.getHorarioConsulta().getStatus().equals(StatusConsultaEnum.AGENDADO.toString())) {
                this.btn_cancelarConsulta.setDisable(false);
                this.btn_cancelarConsulta.setUserData(consulta);

                this.btn_consultaRealizada.setDisable(false);
                this.btn_consultaRealizada.setUserData(consulta);

                this.btn_consultaNaoRealizada.setDisable(false);
                this.btn_consultaNaoRealizada.setUserData(consulta);
            } else {
                this.btn_cancelarConsulta.setDisable(true);
                this.btn_consultaRealizada.setDisable(true);
                this.btn_consultaNaoRealizada.setDisable(true);
            }

        } else {
            this.tf_nome.clear();
            this.tf_telefone.clear();
            this.tf_email.clear();
            this.btn_deletarHorario.setDisable(false);
            this.btn_cancelarConsulta.setDisable(true);
            this.btn_consultaRealizada.setDisable(true);
            this.btn_consultaNaoRealizada.setDisable(true);
        }
    }

    public void concluirConsulta(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        Consulta consulta = (Consulta) button.getUserData();

        java.sql.Date sqlDate = consulta.getHorarioConsulta().getData();
        LocalTime horaSelecionada = consulta.getHorarioConsulta().getHora().toLocalTime();

        if(!validaDataHora(sqlDate,horaSelecionada)) {
            showAlertInformation("Erro Finalização Consulta","Não foi possível marcar a consulta como concluída!",
                    "Foi selecionado uma consulta em datas futuras.");
        } else {

            String dateF = formattedDateString(consulta.getHorarioConsulta().getData());
            String timeF = formattedTimeString(consulta.getHorarioConsulta().getHora());

            boolean btnClick = showAlertConfirmation("Marcar como Realizado","Finalizando Consulta",
                    "Tem certeza que deseja marcar como realizado a consulta no dia "+dateF+" as "+timeF+"?","");
            if(btnClick) {
                try {
                    AgendaRepository.finalizaConsulta(consulta.getHorarioConsulta().getId(), StatusConsultaEnum.CONCLUIDO.toString());

                    showAlertInformation("Agendamento","Consulta Finalizada com Sucesso!","");
                    resetaCampos();
                } catch (SQLException e) {
                    showAlertWarning("Error","Não foi possível marcar como concluída a consulta selecionada!","");
                }
            }
        }

    }

    public void marcarNaoRealizadaConsulta(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        Consulta consulta = (Consulta) button.getUserData();

        java.sql.Date sqlDate = consulta.getHorarioConsulta().getData();
        LocalTime horaSelecionada = consulta.getHorarioConsulta().getHora().toLocalTime();

        if(!validaDataHora(sqlDate,horaSelecionada)) {
            showAlertInformation("Erro Finalização Consulta","Não foi possível marcar a consulta como não concluída!",
                    "Foi selecionado uma consulta em datas futuras.");
        } else {
            String dateF = formattedDateString(consulta.getHorarioConsulta().getData());
            String timeF = formattedTimeString(consulta.getHorarioConsulta().getHora());

            boolean btnClick = showAlertConfirmation("Marcar como Não Realizado","Deletando Horário",
                    "","Tem certeza que deseja marcar como não realizado a consulta no dia "+dateF+" as "+timeF+"?");

            if(btnClick) {
                try {
                    AgendaRepository.finalizaConsulta(consulta.getHorarioConsulta().getId(), StatusConsultaEnum.NAO_REALIZADO.toString());
                    showAlertInformation("Finalização","","Consulta Finalizada com Sucesso!");
                    resetaCampos();
                } catch (SQLException e) {
                    showAlertWarning("Error","Não foi possível marcar como não concluída a consulta selecionada!","");
                }
            }
        }

    }

    public void deletarHorario(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        Agenda horario = (Agenda) button.getUserData();

        String dateF = formattedDateString(horario.getData());
        String timeF = formattedTimeString(horario.getHora());

        boolean clickBtn = showAlertConfirmation("Deletar","Deletando Horário","",
                "Tem certeza que deseja detelar o horário de "+timeF+" no dia "+dateF+"?");

        if(clickBtn) {
            try {
                AgendaRepository.deleteHorario(horario.getId());
                showAlertInformation("Deletando","","Horário Deletado com Sucesso!");
                resetaCampos();
            } catch (SQLException e) {
                showAlertWarning("Error","","Não foi possível deletar o horário selecionado!");
            }
        }
    }

    public void cancelarConsulta(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        Consulta consulta = (Consulta) button.getUserData();

        String dateF = formattedDateString(consulta.getHorarioConsulta().getData());
        String timeF = formattedTimeString(consulta.getHorarioConsulta().getHora());

        boolean clickBtn = showAlertConfirmation("Sim","Cancelando Consulta","",
                "Tem certeza que deseja cancelar a consulta de "+timeF+" no dia "+dateF+"?");

        if(clickBtn) {
            try {
                ConsultaRepository.cancelarConsulta(consulta.getId());
                showAlertInformation("Cancelamento","","Consulta Cancelada com Sucesso!");
                resetaCampos();
            } catch (SQLException e) {
                showAlertWarning("Error","","Não foi possível cancelar a consulta selecionada!");
            }
        }
    }

    private void validaCampos() throws IllegalArgumentException
    {
        if(this.data_picker.getEditor().getText().isEmpty()) {
            throw new IllegalArgumentException("Selecione uma Data!");
        }
    }

    private boolean validaDataHora(Date date, LocalTime time)
    {
        if(date.toLocalDate().isAfter(LocalDate.now()) ||
                (date.toLocalDate().isEqual(LocalDate.now()) && time.isAfter(LocalTime.now()))) {
            return false;
        }
        return true;
    }

    private void showAlertWarning(String title, String header, String content)
    {
        Alert dialogoExe = new Alert(Alert.AlertType.WARNING);
        dialogoExe.setTitle(title);
        dialogoExe.setHeaderText(header);
        dialogoExe.setContentText(content);
        dialogoExe.showAndWait();
    }

    private void showAlertInformation(String title, String header, String content)
    {
        Alert dialogoInfo = new Alert(Alert.AlertType.INFORMATION);
        dialogoInfo.setTitle(title);
        dialogoInfo.setHeaderText(header);
        dialogoInfo.setContentText(content);
        dialogoInfo.showAndWait();
    }

    private boolean showAlertConfirmation(String btnConfirmText, String title, String header, String content)
    {
        Alert dialogoExe = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType btnConfirm = new ButtonType(btnConfirmText);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialogoExe.setTitle(title);
        dialogoExe.setContentText(content);
        dialogoExe.getButtonTypes().setAll(btnConfirm, btnCancelar);

        Optional<ButtonType> result = dialogoExe.showAndWait();

        return (result.isPresent() && result.get() == btnConfirm);
    }

    private String formattedDateString(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    private String formattedTimeString(Time time)
    {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String formattedTime = timeFormat.format(time);

        return formattedTime;
    }

    public void delogarSistema(MouseEvent mouseEvent) throws IOException
    {
        try {
            Sessao.getInstance().deslogar();
            Telas.getTelaLogin(null);
        } catch (IOException e) {

        }
    }

    public void telaCadHorarios(MouseEvent mouseEvent) throws IOException
    {
        try {
            Telas.getTelaCadHorarios();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void telaPerfilPsi(MouseEvent mouseEvent) throws IOException
    {
        try {
            Telas.getTelaEditPsi();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void telaDashPsicologo(MouseEvent mouseEvent) throws IOException
    {
        try {
            Telas.getTelaDashPsi();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void telaHistoricoConsultas(MouseEvent mouseEvent) throws IOException
    {
        try {
            Telas.getTelaHistoricoConsultas();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void resetaCampos()
    {
        this.grid_horarios.getChildren().clear();
        this.lb_semHorario.setVisible(false);
        this.data_picker.getEditor().clear();
        this.tf_nome.clear();
        this.tf_telefone.clear();
        this.tf_email.clear();
        this.btn_cancelarConsulta.setDisable(true);
        this.btn_deletarHorario.setDisable(true);
        this.btn_consultaRealizada.setDisable(true);
        this.btn_consultaNaoRealizada.setDisable(true);

    }
}
