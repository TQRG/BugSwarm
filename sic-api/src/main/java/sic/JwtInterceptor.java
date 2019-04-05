package sic;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import sic.controller.UnauthorizedException;

public class JwtInterceptor extends HandlerInterceptorAdapter {
    
    @Value("${SIC_JWT_KEY}")
    private String secretkey;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_error_token_vacio_invalido"));
        }
        final String token = authHeader.substring(7); // The part after "Bearer "
        try {
            Claims claims = Jwts.parser()
                                .setSigningKey(secretkey)
                                .parseClaimsJws(token)
                                .getBody();
            request.setAttribute("claims", claims);
        } catch (JwtException ex) {
            throw new UnauthorizedException(ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_error_token_vacio_invalido"), ex);
        }
        return true;
    }
}
