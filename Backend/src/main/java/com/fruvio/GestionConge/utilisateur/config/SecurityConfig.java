package com.fruvio.GestionConge.utilisateur.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Requêtes préflight CORS et authentification publique
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/utilisateur/login").permitAll()
                .requestMatchers("/utilisateur/forgot-password").permitAll() // Autorise la réinitialisation sans token
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/error").permitAll()

                // Configuration des types de congés : consultation libre, écriture Admin
                .requestMatchers(HttpMethod.GET, "/type-conge/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYE")
                .requestMatchers("/type-conge/**").hasRole("ADMIN")

                // Gestion des utilisateurs : Admin
                .requestMatchers(HttpMethod.POST, "/utilisateur/create").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/utilisateur/update").hasAnyRole("ADMIN", "MANAGER", "EMPLOYE")
                .requestMatchers("/utilisateur/all").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.GET, "/utilisateur/{id}").hasAnyRole("ADMIN", "MANAGER", "EMPLOYE")

                // Soldes : Le manager ne contrôle pas le solde -> ajustement/init réservés à ADMIN
                .requestMatchers(HttpMethod.POST, "/solde/initialiser/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/solde/ajuster/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/solde/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYE")

                // Demandes de congé : Traitement pour Admin et Manager
                .requestMatchers(HttpMethod.PUT, "/conge/*/traiter").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.GET, "/conge/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.GET, "/conge/en-attente").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.GET, "/conge/all").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/conge/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYE")

                // Notifications et Historique
                .requestMatchers("/notification/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYE")
                .requestMatchers("/historiques/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYE")
                .requestMatchers("/services/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYE")

                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // On vide explicitement toute configuration d'origines précédente
        configuration.setAllowedOrigins(null); 
        
        // On définit les origines autorisées via Pattern
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}