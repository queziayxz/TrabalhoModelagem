package org.trab.demo.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.repository.AgendaRepository;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.util.HorariosAgendaEnum;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.sql.Date;

public class CadastrarHorariosController implements Initializable {
    @FXML
    private Button btn_agenda;

    @FXML
    private Button btn_cadHorarios;

    @FXML
    private Button btn_dash;

    @FXML
    private Button btn_mostrarHorarios;

    @FXML
    private Button btn_perfil;

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

            resetaCampos();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
            String formattedDate = dateFormat.format(Date.valueOf(this.data_picker.getValue()));
            this.lb_title.setText(formattedDate);

            Date dataSelecionada = Date.valueOf(this.data_picker.getValue());
            List<Consulta> consultas = selecionaHorarios(dataSelecionada);

            for (int i = 0; i < consultas.size(); i++) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                String formattedTime = timeFormat.format(consultas.get(i).getHorarioConsulta().getHora());
                Button button = new Button(formattedTime);
                button.setStyle("-fx-font-size:18");

                Label label = new Label();
                label.setStyle("-fx-font-size:18");

                if(consultas.get(i).getPaciente() != null) {
                    label.setText(consultas.get(i).getPaciente().getNome());
                } else {
                    label.setText("Horário Livre");
                }

                this.grid_horarios.add(button,0,i);
                this.grid_horarios.add(label,1,i);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private List<Consulta> selecionaHorarios(Date dataSelecionada) throws SQLException
    {
        try {
            List<Consulta> consultasAgendadas = ConsultaRepository.getConsultasData(dataSelecionada);

            List<Consulta> consultasFinais = new ArrayList<>();

            int index = 0;
            for(HorariosAgendaEnum horarioEnum : HorariosAgendaEnum.values()) {
                if(!consultasAgendadas.isEmpty()) {
                    for(int i = 0; i < consultasAgendadas.size(); i++) {
                        if(horarioEnum.getHorario().equals(consultasAgendadas.get(i).getHorarioConsulta().getHora().toString())) {
                            consultasFinais.add(consultasAgendadas.get(index));
                            consultasAgendadas.remove(i);
                            break;
                        } else {
                            Time time = Time.valueOf(horarioEnum.getHorario());

                            Agenda novoHorario = new Agenda();
                            novoHorario.setHora(time);

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

    private void resetaCampos()
    {
        this.data_picker.getEditor().clear();
        this.lb_title.setText("Selecione uma Data!");
        this.grid_horarios.getChildren().clear();
    }
    public void telaAgenda() throws IOException
    {
        try {
            Telas.getTelaAgendaPsicologo();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void telaPerfilPsi() throws IOException
    {
        try {
            Telas.getTelaEditPsi();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void telaDashPsicologo() throws IOException
    {
        try {
            Telas.getTelaDashPsi();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}
