package com.workout_tracker_app.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.workout_tracker_app.Config.detailsService;
import com.workout_tracker_app.Service.jwtService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class jwtAuthFilter extends OncePerRequestFilter {
    private final jwtService jwtService;
    private final detailsService DetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Get the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // JWT tokens are sent as "Bearer <token>"
        // If header is missing or does not start with Bearer, skip this filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token (everything after "Bearer ")
        final String jwt = authHeader.substring(7);
        try {
            final String username = jwtService.extractUsername(jwt);

            // If we have a username and no authentication exists yet
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details from database
                UserDetails userDetails = this.DetailsService.loadUserByUsername(username);

                // Validate the token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // No credentials needed - JWT already validated
                            userDetails.getAuthorities());

                    // Attach request details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the authentication in the security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

}
