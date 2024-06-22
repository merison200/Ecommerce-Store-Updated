package com.emma.Ecommerce.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.emma.Ecommerce.model.LocalUser;
import com.emma.Ecommerce.repository.LocalUserRepository;
import com.emma.Ecommerce.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

   @Autowired
   private JWTService jwtService;

   @Autowired
   private LocalUserRepository localUserRepository;

    /*This method controls the bearer token authorization.
    TODO:   Will still learn more about jwt.
    */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String tokenHeader = request.getHeader("Authorization");

        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);

            try {
                String username = jwtService.getUsername(token);
                Optional<LocalUser> upUser = localUserRepository.findByUsernameIgnoreCase(username);

                if (upUser.isPresent()) {
                    LocalUser user = upUser.get();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken
                                    (user, null, new ArrayList<>());

                    authentication.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JWTDecodeException ex) {
                ResponseEntity.badRequest().build();
            }
        }
        filterChain.doFilter(request, response);
    }
}
