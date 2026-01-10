package org.trab.demo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.model.Psicologo;
import org.trab.demo.repository.AgendaRepository;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.enums.HorariosAgendaEnum;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.sql.Date;

public class CadastrarHorariosController implements Initializable {

    @FXML
    private Text btn_cadHorarios;

    @FXML
    private DatePicker data_picker;

    @FXML
    private GridPane grid_horarios;

    @FXML
    private Label lb_title;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetaCampos();
        this.btn_cadHorarios.setDisable(true);
    }

    public void mostrarHorarios()
    {
        try {
            this.validaCampos();
            this.resetaCampos();

            String formattedDate = this.formattedDateString(Date.valueOf(this.data_picker.getValue()));
            this.lb_title.setText(formattedDate);

            Date dataSelecionada = Date.valueOf(this.data_picker.getValue());

            if(dataSelecionada.toLocalDate().isBefore(LocalDate.now())) {
                this.showAlertWarning("Erro Buscar Horários",
                        "Não foi possível buscar os horários da data selecionada!",
                        "Por favor, selecione datas futuras.");
            } else {
                List<Consulta> consultas = this.selecionaHorarios(dataSelecionada);

                for (int i = 0; i < consultas.size(); i++) {
                    String formattedTime = formattedTimeString(consultas.get(i).getHorarioConsulta().getHora());
                    Button button = new Button(formattedTime);
                    button.setStyle("-fx-font-size:18");
                    button.setOnAction(this::cadastraHorario);

                    Label label = new Label();
                    label.setStyle("-fx-font-size:18");

                    if(consultas.get(i).getPaciente() != null) {
                        button.setDisable(true);
                        label.setText(consultas.get(i).getPaciente().getNome());
                    } else if (consultas.get(i).getHorarioConsulta().getStatus() != null) {
                        label.setText("Horário já Cadastrado");
                        button.setDisable(true);
                    } else {
                        label.setText("Horário Livre");
                        button.setUserData(consultas.get(i));
                    }

                    this.grid_horarios.add(button,0,i);
                    this.grid_horarios.add(label,1,i);
                }
            }



        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            this.showAlertWarning("Error",e.getMessage(),"Erro ao mostrar horários");
        }
    }

    private List<Consulta> selecionaHorarios(Date dataSelecionada) throws SQLException
    {
        try {
            List<Consulta> consultasAgendadas = ConsultaRepository.getConsultasHorariosData(dataSelecionada);

            List<Consulta> consultasFinais = new ArrayList<>();

            for(HorariosAgendaEnum horarioEnum : HorariosAgendaEnum.values()) {
                if(!consultasAgendadas.isEmpty()) {
                    for(int i = 0; i < consultasAgendadas.size(); i++) {
                        if(horarioEnum.getHorario().equals(consultasAgendadas.get(i).getHorarioConsulta().getHora().toString())) {
                            consultasFinais.add(consultasAgendadas.get(i));
                            consultasAgendadas.remove(i);
                            break;
                        } else {
                            Time time = Time.valueOf(horarioEnum.getHorario());

                            Agenda novoHorario = new Agenda();
                            novoHorario.setHora(time);
                            novoHorario.setData(dataSelecionada);
                            novoHorario.setIdPsicologo(Sessao.getInstance().getUser(Psicologo.class).getId());

                            Consulta novaCon = new Consulta();
                            novaCon.setHorarioConsulta(novoHorario);

                            consultasFinais.add(novaCon);
                            break;
                        }
                    }
                } else {
                    Time time = Time.valueOf(horarioEnum.getHorario());

                    Agenda novoHorario = new Agenda();
                    novoHorario.setHora(time);
                    novoHorario.setData(dataSelecionada);
                    novoHorario.setIdPsicologo(Sessao.getInstance().getUser(Psicologo.class).getId());

                    Consulta novaCon = new Consulta();
                    novaCon.setHorarioConsulta(novoHorario);

                    consultasFinais.add(novaCon);
                }
            }

            return consultasFinais;

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void cadastraHorario(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        Consulta consulta = (Consulta) button.getUserData();

        java.sql.Date sqlDate = consulta.getHorarioConsulta().getData();
        LocalTime horaSelecionada = consulta.getHorarioConsulta().getHora().toLocalTime();

        if(sqlDate.toLocalDate().isEqual(LocalDate.now()) && !horaSelecionada.isAfter(LocalTime.now())) {
            this.showAlertWarning("Erro Cadastro Horário", "Não foi possível cadastrar o horário selecionado!",
                    "Por favor, selecione horários futuros.");
        } else {
            String dateF = this.formattedDateString(consulta.getHorarioConsulta().getData());
            String timeF = this.formattedTimeString(consulta.getHorarioConsulta().getHora());

            boolean btnClick = this.showAlertConfirmation("Cadastrar","Cadastrar Horário", "",
                    "Tem certeza que deseja cadastrar o horário no dia "+dateF+" as "+timeF+"?");

            if(btnClick) {
                try {
                    AgendaRepository.cadastraHorario(consulta.getHorarioConsulta());

                    this.showAlertInformation("Cadastro","","Horário Cadastrado com Sucesso!");
                    resetaCampos();
                } catch (SQLException e) {
                    this.showAlertWarning("Error","Não foi possível cadastrar esse horário!","");
                }

            }
        }
    }

    private void resetaCampos()
    {
        this.data_picker.getEditor().clear();
        this.lb_title.setText("Selecione uma Data!");
        this.grid_horarios.getChildren().clear();
    }

    private void validaCampos() throws IllegalArgumentException
    {
        if(this.data_picker.getEditor().getText().isEmpty()) {
            throw new IllegalArgumentException("Selecione uma Data!");

        }
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

    public void delogarSistema(MouseEvent mouseEvent) throws IOException
    {
        try {
            Sessao.getInstance().deslogar();
            Telas.getTelaLogin(null);
        } catch (IOException e) {

        }
    }
    public void telaAgenda(MouseEvent mouseEvent) throws IOException
    {
        try {
            Telas.getTelaAgendaPsicologo();
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
}
