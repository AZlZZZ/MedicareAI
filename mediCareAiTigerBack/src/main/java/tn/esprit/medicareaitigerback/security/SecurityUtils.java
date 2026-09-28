package tn.esprit.medicareaitigerback.security;


import tn.esprit.medicareaitigerback.exceptions.TokenInvalidException;
import tn.esprit.medicareaitigerback.models.entities.Agent;
import tn.esprit.medicareaitigerback.models.entities.User;
import tn.esprit.medicareaitigerback.models.enumeration.UserType;
import tn.esprit.medicareaitigerback.repositories.AgentRepository;
import tn.esprit.medicareaitigerback.utilities.Log;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
@Configuration
public class SecurityUtils {

    @Autowired
    public AgentRepository agentRepository;


    private static final String JWT_HEADER_NAME = "Authorization";
    private static final String HEADER_PREFIX = "Bearer ";
    private static final String SECRET = "ZwHqm8F3roWRnYjTJe5Zyw==";
    private Long SESSION_EXPIRATION = 1000L * 3600 * 24 * 365;
    public static final Long Password_EXPIRATION = 1000L * 60 * 5;
    public static final Integer RESET_PWD_TOKEN_EXPIRATION = 1000 * 60 * 30;
    public static final Integer SET_PWD_TOKEN_EXPIRATION = 1000 * 3600 * 24 * 5;
    private static final String USER_TYPE_KEY = "userType";
    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String TOKEN_TYPE_KEY = "tokenType";

    public static final String[] AUTH_WHITELIST = {
            "/authenticate",
            "/v2/api-docs",
            "/swagger-resources",
            "**/swagger-resources/**",
            "/configuration/ui",
            "/configuration/**",
            "/swagger-ui.html/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api/v1/test/**",
            "/api/v1/client/auth/**",
            "/api/v1/agent/auth/**",
            "/api/v1/doctor/auth/**",
            "/api/v1/client/partners/**",
            "/api/v1/client/products/**",
            "/api/v1/client/category/**",
            "/api/v1/websocket/**",
            "/chat**",
            "/client/pdp/**",
            "/badge/pdp/**",
            "/product/pdp/**",
            "/category/pdp/**",
            "/golden/pdp/**",
            "/api/v1/client/review/by-product",

            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/configuration/ui",
            "/configuration/security"
    };

    public UserDetailsImpl build(User user, UserType type) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        switch (type) {
            case AGENT:
                authorities.add(new SimpleGrantedAuthority("ROLE_AGENT"));
                if (user instanceof tn.esprit.medicareaitigerback.models.entities.Agent agent
                        && agent.getRole() != null) {
                    try {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + agent.getRole().getName().toUpperCase()));
                    } catch (Exception ignored) {
                        // Role is lazy-loaded and session may be closed — skip business role
                    }
                }
                break;
            case PATIENT:
                authorities.add(new SimpleGrantedAuthority("ROLE_PATIENT"));
                break;
            case DOCTOR:
                authorities.add(new SimpleGrantedAuthority("ROLE_DOCTOR"));
                break;
        }

        return new UserDetailsImpl(
                user.getId(),
                user.getUserName(),
                user.getPassword(),
                authorities,
                type,
                user.getState()
        );
    }

    public String generatePinPassword() {
        int m = (int) Math.pow(10, 4 - 1);
        return "" + (m + new Random().nextInt(9 * m));
    }

    public String generateJwtToken(Authentication authentication, UserType userType, String deviceId) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Map<String, Object> info = new HashMap<>();
        if (userType == UserType.AGENT) {
            var agent = (Agent) agentRepository.findUserByUserName(userPrincipal.getUsername()).orElse(null);
            info.put("agent_id", agent.getId());
            this.SESSION_EXPIRATION = 1000L * 60 * 60 * 8;
            info.put(USER_TYPE_KEY, userType);
            return Jwts.builder()
                    .setSubject(userPrincipal.getUsername())
                    .setIssuer(userPrincipal.getAuthorities().toString())
                    .setIssuedAt(new Date())
                    .addClaims(info)
                    .setExpiration(new Date(new Date().getTime() + SESSION_EXPIRATION))
                    .signWith(SignatureAlgorithm.HS512, SECRET).compact();
        } else {
            this.SESSION_EXPIRATION = 1000L * 3600 * 24 * 365;
            info.put(USER_TYPE_KEY, userType);
            return Jwts.builder()
                    .setSubject(userPrincipal.getUsername())
                    .setIssuer(userPrincipal.getAuthorities().toString())
                    .setIssuedAt(new Date())
                    .addClaims(info)
                    .setExpiration(new Date(new Date().getTime() + SESSION_EXPIRATION))
                    .signWith(SignatureAlgorithm.HS512, SECRET).compact();
        }
    }

    public Map<String, String> generateJwtToken2(Authentication authentication, UserType userType, String deviceId) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Map<String, Object> info = new HashMap<>();
        if (userType == UserType.PATIENT || userType == UserType.AGENT || userType == UserType.DOCTOR) {
            // Set expiration per user type — AGENT gets 8h, others get 1 year
            long expiration = (userType == UserType.AGENT)
                    ? 1000L * 60 * 60 * 8
                    : 1000L * 3600 * 24 * 365;
            info.put(USER_TYPE_KEY, userType);
            info.put(TOKEN_TYPE_KEY, ACCESS_TOKEN_KEY);
            String refreshToken = Jwts.builder()
                    .setSubject(userPrincipal.getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(new Date().getTime() + (7 * 24 * 60 * 60 * 1000)))
                    .claim(USER_TYPE_KEY, userType)
                    .claim(TOKEN_TYPE_KEY, "refresh")
                    .signWith(SignatureAlgorithm.HS512, SECRET).compact();
            String accessToken = Jwts.builder()
                    .setSubject(userPrincipal.getUsername())
                    .setIssuer(userPrincipal.getAuthorities().toString())
                    .setIssuedAt(new Date())
                    .addClaims(info)
                    .setExpiration(new Date(new Date().getTime() + expiration))
                    .signWith(SignatureAlgorithm.HS512, SECRET).compact();
            Map<String, String> tokens = new HashMap<>();
            tokens.put(ACCESS_TOKEN_KEY, accessToken);
            tokens.put("refreshToken", refreshToken);

            return tokens;
        } else {
            return null;
        }
    }

    public Claims getClaimsFromJwt(String token) throws ExpiredJwtException {
        try {
            return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(JWT_HEADER_NAME);

        if (StringUtils.hasText(headerAuth)) {
            // Handle both "Bearer <token>" and accidental "Bearer Bearer <token>"
            if (headerAuth.startsWith("Bearer Bearer ")) {
                return headerAuth.substring(14);
            }
            if (headerAuth.startsWith(HEADER_PREFIX)) {
                return headerAuth.substring(7);
            }
        }

        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> ACCESS_TOKEN_KEY.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getSubject();
    }

    public UserType getUserTypeFromJwtToken(String token) {

        return UserType.valueOf(Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().get("userType").toString());
    }

    public boolean validateJwtToken(String authToken) throws TokenInvalidException {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            Log.instance.e("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            Log.instance.e("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            Log.instance.e("JWT token is expired: {}", e.getMessage());
            throw new TokenInvalidException("Token expired");
        } catch (UnsupportedJwtException e) {
            Log.instance.e("JWT token is unsupported: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            Log.instance.e("JWT claims string is empty: {}", e.getMessage());
            return false;
        }

    }
}
