package org.trab.demo.controller;

import org.trab.demo.util.Telas;

import java.io.IOException;

public class RegisterController {
    public void linkPossuiCadastro()
    {
        try {
            Telas.getTelaLogin(null);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
