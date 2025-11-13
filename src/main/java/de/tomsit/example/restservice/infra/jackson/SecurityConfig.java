package de.tomsit.example.restservice.infra.jackson;

import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@ConditionalOnWebApplication //needed so ITs w/o dont error out when running this config
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder encoder) {
    UserDetails user = User.withUsername("user")
                           .password(encoder.encode("password"))
                           .roles("USER")
                           .build();
    UserDetails admin = User.withUsername("admin")
                            .password(encoder.encode("adminpass"))
                            .roles("ADMIN")
                            .build();
    return new InMemoryUserDetailsManager(user, admin);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable) // disable for REST APIs
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**").permitAll() // allow unauthenticated
            .anyRequest().authenticated()
        )
        .httpBasic() // simple username/password via HTTP Basic
        .and()
        .oauth2ResourceServer(oauth ->
                                  oauth
                                      .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        )
    ;

    return http.build();
  }

  private JwtAuthenticationConverter jwtAuthenticationConverter() {
    var converter = new JwtGrantedAuthoritiesConverter();
    converter.setAuthoritiesClaimName("roles");    // claim to read
    converter.setAuthorityPrefix("");         // add prefix if needed

    var jwtAuthConverter = new JwtAuthenticationConverter();
    jwtAuthConverter.setJwtGrantedAuthoritiesConverter(converter);
    return jwtAuthConverter;
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    //bean must be defined here b/c overriding it in a test causes an IllegalStateException
    var secret = "testsecret1234567890testsecret1234567890";
    var key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");

    return NimbusJwtDecoder
        .withSecretKey(key)
        .build();
  }


}
