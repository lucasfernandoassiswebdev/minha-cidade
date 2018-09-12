package br.com.eddydata.minhacidade.api;

import br.com.eddydata.minhacidade.util.EddyServerException;
import br.com.eddydata.minhacidade.util.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class TokenJWTUtil {

    private static final KeyGenerator keyGenerator = new KeyGenerator();

    public static String gerarToken(String username, List<String> roles) throws EddyServerException {
        try {
            Key key = keyGenerator.generateKey();

            String jwtToken = Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, key)
                    .setHeaderParam("typ", "JWT")
                    .setSubject(username)
                    .setIssuer("Eddydata")
                    .setIssuedAt(new Date())
                    .setExpiration(toDate(LocalDateTime.now().plusMinutes(1060L)))
                    .claim("roles", roles)
                    .compact();
            if (jwtToken == null) {
                throw new EddyServerException("Falha ao gerar Token", ErrorCode.SERVER_ERROR.getCode());
            }
            return jwtToken;
        } catch (Exception ex) {
            throw new EddyServerException("Falha ao gerar Token\n" + ex.getMessage(), ErrorCode.SERVER_ERROR.getCode());
        }
    }

    private static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static boolean tokenValido(String token, Key key) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static String recuperarNome(String token, Key key) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
        return claimsJws.getBody().getSubject();
    }

    public static List<String> recuperarRoles(String token, Key key) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
        return claimsJws.getBody().get("roles", List.class);
    }
}
