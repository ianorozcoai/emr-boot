package com.cdsi.emr.config.security;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] PUBLIC = new String[] 
			{"/login","/logout","/error", "/registration"};
	private static final String[] ASSETS = new String[]
			{"/assets/**","/global_assets/**",
					"/admin_js/**","/parent_css/**","/parent_js/**",
					"/js/*","/js/**","/css/*","/css/**","/images/*","/images/**"};
	
	private UserDetailsService ehrUserDetailsService;
	
	public SecurityConfig(UserDetailsService userDetailsService) {
		this.ehrUserDetailsService = userDetailsService;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// configure AuthenticationManager so that it knows from where to load
		// user for matching credentials
		// Use BCryptPasswordEncoder
		auth.userDetailsService(ehrUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			//.csrf().disable()
			.authorizeRequests()
				.antMatchers(PUBLIC).permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.defaultSuccessUrl("/dashboard", true)
				//.successForwardUrl("/success");
				.failureUrl("/login?error")
				.and()
			.logout()
				//.and()
			;
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers(ASSETS);
	}

	@Bean
	public PasswordEncoder passwordEncoder() throws NoSuchAlgorithmException {
		int strength = 11;
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(strength); 
		return encoder;
	}
}
