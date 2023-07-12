package org.example.authenticationservice.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;


        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            log.info("ok wtf now");
            filterChain.doFilter(request, response);
            log.info("ayoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");
            return ;
        }

        log.info("Hellooooooooooooooooooooooooooooo");

        jwt = authHeader.substring(7);
        log.info("tfffffffffff");
        userEmail = jwtService.extractUsername(jwt);
        log.info("testestestest  "+String.valueOf(SecurityContextHolder.getContext().getAuthentication()));
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            log.info("jjjjjjjjjjj");
            log.info(userEmail);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            log.info(userDetails.getAuthorities().toString());
            log.info("just testing shit out");
            if (jwtService.isTokenValid(jwt , userDetails)){
                log.info("lmaoooooooooooooooooooooooo");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(),
                        null,
                        userDetails.getAuthorities()
                );
                log.info("FUCKING FINAAAAAAAAAAAAAAAAAALY");
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("i mean here anyway POOP");
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
            }
        }
        filterChain.doFilter(request,response);
    }


}
