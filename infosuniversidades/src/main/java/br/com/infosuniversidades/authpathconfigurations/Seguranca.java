package br.com.infosuniversidades.authpathconfigurations;

import br.com.infosuniversidades.models.Usuario;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class Seguranca {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable())//desativa proteção CSRF, útil para APIs REST
                 .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // permite tudo, sem exigir login

        return http.build();
    }

}
