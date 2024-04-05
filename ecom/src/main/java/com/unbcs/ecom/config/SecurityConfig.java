package com.unbcs.ecom.config;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		RequestMatcher matcher = new OrRequestMatcher(new AntPathRequestMatcher("/feedback/**", HttpMethod.GET.name()),
				new AntPathRequestMatcher("/feedback/**", HttpMethod.POST.name()),
				new AntPathRequestMatcher("/product/**", HttpMethod.GET.name()),
				new AntPathRequestMatcher("/product/**", HttpMethod.POST.name()),
				new AntPathRequestMatcher("/product/**", HttpMethod.PUT.name()),
				new AntPathRequestMatcher("/product/**", HttpMethod.DELETE.name()),
				new AntPathRequestMatcher("/cart/**", HttpMethod.GET.name()),
				new AntPathRequestMatcher("/cart/**", HttpMethod.POST.name()),
				new AntPathRequestMatcher("/cart/**", HttpMethod.DELETE.name()),
				new AntPathRequestMatcher("/order/**"));
		httpSecurity.securityMatcher(matcher)
				.authorizeHttpRequests(request -> request.requestMatchers("/feedback/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/product/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/product/**").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/product/**").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/product/**").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.GET, "/cart/**").hasAuthority("USER")
						.requestMatchers(HttpMethod.POST, "/cart/**").hasAuthority("USER")
						.requestMatchers(HttpMethod.DELETE, "/cart/**").hasAuthority("USER")
						.requestMatchers(new AntPathRequestMatcher("/order/**")).hasAuthority("USER")
				)
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt.jwtAuthenticationConverter(new CustomAuthenticationConverter())))
				.csrf(csrf -> csrf.ignoringRequestMatchers(matcher));
//				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
		return httpSecurity.build();
	}

	static class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
		public AbstractAuthenticationToken convert(Jwt jwt) {
			Collection<String> authorities = jwt.getClaimAsStringList("roles");
			Collection<GrantedAuthority> grantedAuthorities = authorities.stream().map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());
			return new JwtAuthenticationToken(jwt, grantedAuthorities);
		}
	}
}
