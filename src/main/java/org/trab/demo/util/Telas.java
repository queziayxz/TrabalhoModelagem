package org.trab.demo.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.trab.demo.controller.ActionsPsiController;
import org.trab.demo.controller.DashboardAdm;

import java.io.IOException;

public class Telas {

    private static Stage stageC;
    private static int width = 900;
    private static int height = 700;
    public static void getTelaLogin(Stage stage) throws IOException
    {
        if (stage != null) {
            stageC = stage;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);

        stageC.setScene(scene);
        stageC.show();
    }
    public static void getTelaCadastro() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/cadastro.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);

        stageC.setScene(scene);
        stageC.show();
    }

    public static void getTelaDashPsi() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/dash_adm.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);

        stageC.setScene(scene);
        stageC.show();
    }

    public static void getTelaDashPaci() throws IOException
   {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/dashPaciente.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stageC.setScene(scene);
        stageC.show();
    }

    public static void getTelaEditPsi() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/edit_psicologo.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stageC.setScene(scene);
        stageC.show();
    }

    public static void getTelaCadHorarios() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/cadastra_horario.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stageC.setScene(scene);
        stageC.show();
    }

    public static void getTelaAgendaPsicologo() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/agenda.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stageC.setScene(scene);
        stageC.show();
    }
    public static void getTelaHistoricoConsultas() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/historico_consultas.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stageC.setScene(scene);
        stageC.show();
    }

    public static void getTelaPerfil() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/view/dashPerfilPaciente.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stageC.setScene(scene);
        stageC.show();
    }

    public static void getTelaAgendamento() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/view/dashAgendamento.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stageC.setScene(scene);
        stageC.show();
    }

    public static void getTelaRemarcacao() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/view/dashRemarcação.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stageC.setScene(scene);
        stageC.show();
    }

    public static void getTelaCancelamento() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/view/dashCancelamento.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stageC.setScene(scene);
        stageC.show();
    }

}
