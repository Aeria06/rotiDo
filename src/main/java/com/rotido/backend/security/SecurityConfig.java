package com.rotido.backend.security;

        import com.rotido.backend.filter.JwtRequestFilter;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.context.annotation.*;
        import org.springframework.security.authentication.AuthenticationManager;
        import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
        import org.springframework.security.config.annotation.web.builders.HttpSecurity;
        import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
        import org.springframework.security.config.http.SessionCreationPolicy;
        import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.security.web.*;
        import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
        import org.springframework.web.cors.CorsConfiguration;
        import org.springframework.web.cors.CorsConfigurationSource;
        import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

        import jakarta.servlet.http.HttpServletResponse;
        import java.util.Arrays;

        @Configuration
        @EnableWebSecurity
        public class SecurityConfig {

            @Autowired
            private JwtRequestFilter jwtRequestFilter;

            @Bean
            public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf.disable())
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/login_combined").permitAll()
                        .requestMatchers("/auth/encrypt").permitAll()
                        .requestMatchers("/auth/decrypt").permitAll()
                        .requestMatchers("/debug/**").permitAll()
                        .requestMatchers("/company/**").hasRole("admin")
                        .requestMatchers("/auth/logout").authenticated()
                        .requestMatchers("/dashboard/profile").authenticated()

                        .requestMatchers("/auth/register").hasRole("admin")
                        // .requestMatchers("/admin/dashboard", "/admin/dashboard/**").permitAll() //for testing 
                        .requestMatchers("/dashboard/customer/**").hasRole("customer")
    
                        // .requestMatchers("/customer/profile").hasRole("customer")
                        // .requestMatchers("/dashboard/admin").hasRole("admin")
                        // .requestMatchers("/dashboard/admin/**").hasRole("admin")
                        .requestMatchers("/dashboard/admin").hasRole("admin")
                        .requestMatchers("/dashboard/admin/**").hasRole("admin")


                   

                        // .requestMatchers("/dashboard/admin/**","/dashboard/admin").hasRole("admin")
                        // .requestMatchers("/company/subscriptions","/company/subscriptions/day","/company/subscriptions/month","company/subscriptions/debug").hasRole("admin")
                        .requestMatchers("/dashboard/service/**").hasRole("service")
                        .requestMatchers("/dashboard/company/**").hasRole("admin")
                        // .requestMatchers("/dashboard/admin","dashboard/admin/" ,"/dashboard/admin/**").permitAll() // for testing
                        
                        .requestMatchers("/api/customer/**").hasRole("customer")
                        .requestMatchers("/api/service/**").hasRole("service")
                        .requestMatchers("/company/**").hasRole("admin")
                        .requestMatchers("/api/mqtt/send").permitAll()
                        
                        .anyRequest().authenticated()
                    )
                    .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Unauthorized access\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Access denied\"}");
                        })
                    );

                http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
            }

            @Bean
            public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOriginPatterns(Arrays.asList("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
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

            @Bean
            public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
            }
        }