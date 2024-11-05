package com.example.bookreadingonline.config;

import com.example.bookreadingonline.config.properties.JwtProperties;
import com.example.bookreadingonline.web.ExceptionHandlerAuthenticationEntryPoint;
import com.example.bookreadingonline.web.filter.ExceptionHandlerFilter;
import com.example.bookreadingonline.web.filter.JwtAuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.security.core.context.SecurityContextHolder.MODE_INHERITABLETHREADLOCAL;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint(
      HandlerExceptionResolver handlerExceptionResolver
  ) {
    return new ExceptionHandlerAuthenticationEntryPoint(handlerExceptionResolver);
  }

  @Bean
  SecurityContextHolderStrategy securityContextHolderStrategy() {
    SecurityContextHolder.setStrategyName(MODE_INHERITABLETHREADLOCAL);
    return SecurityContextHolder.getContextHolderStrategy();
  }

  @Bean
  MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
    return new DefaultMethodSecurityExpressionHandler();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      CorsConfigurationSource corsConfigurationSource,
      AuthenticationProvider authenticationProvider,
      UserDetailsService userDetailsService,
      AuthenticationEntryPoint authenticationEntryPoint,
      HandlerExceptionResolver handlerExceptionResolver,
      JwtProperties jwtProperties
  ) throws Exception {
    JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter = new JwtAuthenticationTokenFilter(
        jwtProperties.getSecretKey(), userDetailsService);
    return http.cors(cors -> cors.configurationSource(corsConfigurationSource))
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .exceptionHandling(c -> c.authenticationEntryPoint(authenticationEntryPoint))
        .addFilterBefore(new ExceptionHandlerFilter(handlerExceptionResolver), LogoutFilter.class)
        .addFilterBefore(jwtAuthenticationTokenFilter, BasicAuthenticationFilter.class)
        .authorizeHttpRequests(c -> c
            .requestMatchers(
                "/public/**",
                "/error/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui/**"
            ).permitAll()
            .anyRequest().authenticated())
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider(
      UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder
  ) {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder);
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    long MAX_AGE_SECS = 3600;
    List<String> allowedMethods = Arrays.stream(RequestMethod.values())
        .map(Enum::name)
        .toList();
    List<String> allowedHeaders = Arrays.asList(
        ACCESS_CONTROL_REQUEST_METHOD,
        ORIGIN,
        CONTENT_TYPE,
        AUTHORIZATION
    );

    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
    configuration.setAllowedMethods(allowedMethods);
    configuration.setAllowedHeaders(allowedHeaders);
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(MAX_AGE_SECS);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;

  }

}
