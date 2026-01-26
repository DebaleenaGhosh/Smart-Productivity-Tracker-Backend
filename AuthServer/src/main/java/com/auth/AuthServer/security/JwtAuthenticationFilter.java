package com.auth.AuthServer.security;

import com.auth.AuthServer.repository.BlackListedTokenRepository;
import com.auth.AuthServer.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final BlackListedTokenRepository blackListedTokenRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver,
            BlackListedTokenRepository blackListedTokenRepository
    )
    {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.blackListedTokenRepository = blackListedTokenRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    )
    throws ServletException, IOException
    {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request, response);
            return;
        }
        try
        {
            final String jwt = authHeader.substring(7);

            if (blackListedTokenRepository.existsByToken(jwt)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            final String userName = jwtService.extractUsername(jwt);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userName != null && authentication == null)
            {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);

                if (jwtService.isTokenValid(jwt, userDetails))
                {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        }
        catch (Exception exception)
        {
//            handlerExceptionResolver.resolveException(request, response, null, exception);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = new ObjectMapper().writeValueAsString(
                    Map.of("error", "Authentication failed: " + exception.getMessage())
            );
            response.getWriter().write(jsonResponse);        }
    }
}