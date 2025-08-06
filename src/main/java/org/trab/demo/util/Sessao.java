package org.trab.demo.util;

import org.trab.demo.model.Paciente;
import org.trab.demo.model.Psicologo;
import org.trab.demo.model.User;

public class Sessao {

    private static Sessao instance = null;
    private User usuario;

    public Sessao() {
    }

    public <T extends User> T getUser(Class<T> tipo) {
        return tipo.cast(this.usuario);
    }

    public void setUser(User usuario) {
        this.usuario = usuario;
    }

    public void deslogar()
    {
        this.usuario = null;
    }

    public static Sessao getInstance(){
        if(instance == null){
            instance = new Sessao();
        }
        return instance;
    }
}
