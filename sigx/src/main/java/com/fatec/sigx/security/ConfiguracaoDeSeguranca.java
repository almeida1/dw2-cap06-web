package com.fatec.sigx.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Configuration
@EnableWebSecurity
public class ConfiguracaoDeSeguranca extends WebSecurityConfigurerAdapter {
     //configuracao de autorizacao
	@Autowired
	UserDetailsServiceImpl userDetailsService;
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/clientes").hasAnyRole("ADMIN", "VEND") 
		.antMatchers("/fornecedores").hasRole("ADMIN")
		//.antMatchers("/api/v1/clientes").permitAll()
		.anyRequest().authenticated()
		.and()
		.formLogin().loginPage("/login").permitAll().and()
		.logout().logoutSuccessUrl("/login?logout").permitAll()
		.and()
		.csrf().disable();
	}
    //configuracao de autenticacao em memoria
	/*
	 * configuracao de autenticacao em memoria com a password criptografada
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication()
//			.withUser("jose").password(pc().encode("123")).roles("ADMIN")
//			.and()
//			.withUser("maria").password(pc().encode("456")).roles("VEND"); //nao tem acesso as funcoes de fornecedores
		auth.userDetailsService(userDetailsService).passwordEncoder(pc());
	}

	@Bean
	public BCryptPasswordEncoder pc() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/h2-console/**", "/api/v1/clientes/**");
		
	}
	
}



