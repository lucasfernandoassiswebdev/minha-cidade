package br.com.eddydata.minhacidade.api;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class JWTSecurityContext implements SecurityContext {

    private final UsuarioAcessos userDetails;
    private final boolean secure;

    public JWTSecurityContext(UsuarioAcessos userDetails, boolean secure) {
        this.userDetails = userDetails;
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.userDetails;
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.userDetails.getAcessos().contains(role);
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }
}
