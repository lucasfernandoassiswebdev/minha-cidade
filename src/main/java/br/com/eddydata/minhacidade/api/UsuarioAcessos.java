package br.com.eddydata.minhacidade.api;

import java.security.Principal;
import java.util.List;

public class UsuarioAcessos implements Principal {
    
    private final String name;
    private final List<String> roles;

    public UsuarioAcessos(String name, List<String> roles) {
        this.name = name;
        this.roles = roles;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<String> getAcessos() {
        return roles;
    }
}
