package tn.esprit.medicareaitigerback.security;


import tn.esprit.medicareaitigerback.exceptions.TokenInvalidException;
import tn.esprit.medicareaitigerback.models.dto.business.AuthDTO;
import tn.esprit.medicareaitigerback.services.implementaions.UserDetailsServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthenticationJwtTokenFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationJwtTokenFilter.class);
    private final UserDetailsServiceImpl userDetailsService;
    private final SecurityUtils securityUtils;

    AuthenticationJwtTokenFilter(UserDetailsServiceImpl userDetailsService, SecurityUtils securityUtils) {
        this.userDetailsService = userDetailsService;
        this.securityUtils = securityUtils;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = securityUtils.parseJwt(request);
            LOG.warn("JWT filter — path={} method={} jwt={}", 
                request.getRequestURI(), request.getMethod(),
                jwt != null ? "present(len=" + jwt.length() + ")" : "NULL — authHeader=" + request.getHeader("Authorization"));
            if (jwt != null && securityUtils.validateJwtToken(jwt)) {
                String username = securityUtils.getUserNameFromJwtToken(jwt);
                var userType = securityUtils.getUserTypeFromJwtToken(jwt);
                var authDTO = new AuthDTO();
                authDTO.setUsername(username);
                authDTO.setUserType(userType);
                UserDetails userDetails = userDetailsService.loadUserByUsername(new Gson().toJson(authDTO));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (TokenInvalidException ex) {
            LOG.error("Token expired: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"TokenExpired\",\"message\":\"" + ex.getMessage() + "\"}");
            return;
        } catch (Exception e) {
            LOG.error("Cannot set user authentication — {}: {}", e.getClass().getSimpleName(), e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
