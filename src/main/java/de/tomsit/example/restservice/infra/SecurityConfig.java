package de.tomsit.example.restservice.infra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
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
                                      .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtKeycloakAuthenticationConverter()))
        )
    ;

    return http.build();
  }

  @Bean
  JwtAuthenticationConverter jwtKeycloakAuthenticationConverter() {
    var converter = new JwtAuthenticationConverter();
    converter.setPrincipalClaimName("preferred_username");

    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
      var roles = new ArrayList<GrantedAuthority>();

      var realmAccess = (Map<String, Object>) jwt.getClaim("realm_access");
      if (realmAccess != null && realmAccess.get("roles") instanceof List<?> list) {
        list.forEach(r ->
                         roles.add(new SimpleGrantedAuthority("ROLE_" + r))
        );
      }

      return roles;
    });

    return converter;
  }

}
