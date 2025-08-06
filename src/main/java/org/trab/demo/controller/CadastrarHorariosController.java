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

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
            String formattedDate = dateFormat.format(Date.valueOf(this.data_picker.getValue()));
            this.lb_title.setText(formattedDate);

            Date dataSelecionada = Date.valueOf(this.data_picker.getValue());

            if(dataSelecionada.toLocalDate().isBefore(LocalDate.now())) {
                Alert dialogoExe = new Alert(Alert.AlertType.WARNING);
                dialogoExe.setTitle("Erro Buscar Horários");
                dialogoExe.setHeaderText("Não foi possível buscar os horários da data selecionada!");
                dialogoExe.setContentText("Por favor, selecione datas futuras.");
                dialogoExe.showAndWait();
            } else {
                List<Consulta> consultas = this.selecionaHorarios(dataSelecionada);

                for (int i = 0; i < consultas.size(); i++) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    String formattedTime = timeFormat.format(consultas.get(i).getHorarioConsulta().getHora());
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
                        if(consultas.get(i).getHorarioConsulta().getStatus() != null) {
                            button.setDisable(true);
                        } else {
                            button.setUserData(consultas.get(i));
                        }
                    }

                    this.grid_horarios.add(button,0,i);
                    this.grid_horarios.add(label,1,i);
                }
            }



        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            Alert dialogoInfo = new Alert(Alert.AlertType.WARNING);
            dialogoInfo.setTitle("Error");
            dialogoInfo.setHeaderText(e.getMessage());
            dialogoInfo.showAndWait();
        }
    }

    private List<Consulta> selecionaHorarios(Date dataSelecionada) throws SQLException
    {
        try {
            List<Consulta> consultasAgendadas = ConsultaRepository.getConsultasHorariosData(dataSelecionada);

            List<Consulta> consultasFinais = new ArrayList<>();

            int index = 0;
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
                            index++;
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
                    index++;
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
            Alert dialogoExe = new Alert(Alert.AlertType.WARNING);
            dialogoExe.setTitle("Erro Cadastro Horário");
            dialogoExe.setHeaderText("Não foi possível cadastrar o horário selecionado!");
            dialogoExe.setContentText("Por favor, selecione horários futuros.");
            dialogoExe.showAndWait();
        } else {

            Alert dialogoExe = new Alert(Alert.AlertType.CONFIRMATION);
            ButtonType btnCadastrar = new ButtonType("Cadastrar");
            ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String dateF = dateFormat.format(consulta.getHorarioConsulta().getData());
            String timeF = timeFormat.format(consulta.getHorarioConsulta().getHora());

            dialogoExe.setTitle("Cadastrar Horário");
            dialogoExe.setContentText("Tem certeza que deseja cadastrar o horário no dia "+dateF+" as "+timeF+"?");
            dialogoExe.getButtonTypes().setAll(btnCadastrar, btnCancelar);
            dialogoExe.showAndWait().ifPresent(b -> {
                if (b == btnCadastrar) {
                    try {
                        AgendaRepository.cadastraHorario(consulta.getHorarioConsulta());

                        Alert dialogoInfo = new Alert(Alert.AlertType.INFORMATION);
                        dialogoInfo.setContentText("Horário Cadastrado com Sucesso!");
                        dialogoInfo.showAndWait();
                        resetaCampos();
                    } catch (SQLException e) {
                        Alert dialogoInfo = new Alert(Alert.AlertType.WARNING);
                        dialogoInfo.setTitle("Error");
                        dialogoInfo.setHeaderText("Não foi possível cadastrar esse horário!");
                        dialogoInfo.showAndWait();
                    }
                }
            });
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
