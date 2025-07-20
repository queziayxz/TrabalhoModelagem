package org.trab.demo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ActionsPsiController {
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabEditPsicologo;
    @FXML
    private Tab tabAgendaPsicologo;
    @FXML
    private Tab tabCadHorarios;

    public void selectScreen(Screen s)
    {
        System.out.println("chamou");
        switch(s) {
            case AGENDA:   tabPane.getSelectionModel().select(this.tabAgendaPsicologo);   break;
            case CADASTRO_HORARIOS: tabPane.getSelectionModel().select(this.tabCadHorarios); break;
            case PERFIL:
                System.out.println("entou perfil");
                tabPane.getSelectionModel().select(this.tabEditPsicologo);
                break;
        }
    }

    public enum Screen { AGENDA, CADASTRO_HORARIOS, PERFIL }
}
