package br.com.eddydata.minhacidade.api;

import br.com.eddydata.minhacidade.util.EddyServerException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Key;
import java.util.List;

//@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthenticationFilter implements ContainerRequestFilter {

    private final KeyGenerator keyGenerator = new KeyGenerator();

    @Context
    private UriInfo uriInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        /*
         if (acessoParaLoginNaAPI(requestContext)) { //não obriga token para o LoginServer
         return;
         }
         */

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.contains("Bearer ")) {
            String token = authorizationHeader.substring("Bearer".length()).trim();

            Key key = keyGenerator.generateKey();

            if (TokenJWTUtil.tokenValido(token, key)) {
                String nome = TokenJWTUtil.recuperarNome(token, key);
                List<String> regras = TokenJWTUtil.recuperarRoles(token, key);
                UsuarioAcessos userDetails = new UsuarioAcessos(nome, regras);

                boolean secure = requestContext.getSecurityContext().isSecure();
                requestContext.setSecurityContext(new JWTSecurityContext(userDetails, secure));
                return;
            }
        }

        throw new EddyServerException("Token inválido/expirado ou usuário não autenticado!");
    }

    /*
     private boolean acessoParaLoginNaAPI(ContainerRequestContext requestContext) {
     String uri1 = requestContext.getUriInfo().getAbsolutePath().toString();
     String uri2 = uriInfo.getBaseUriBuilder().path(LoginServer.class).build().toString();
     if (uri1.endsWith("/")) {
     uri1 = uri1.substring(0, uri1.length() - 1);
     }
     if (uri2.endsWith("/")) {
     uri2 = uri2.substring(0, uri2.length() - 1);
     }
     return uri1.equals(uri2);
     }

     private boolean acessoParaMetodosDeConsulta(ContainerRequestContext requestContext) {
     return "GET".equalsIgnoreCase(requestContext.getMethod());
     }
     */
}
