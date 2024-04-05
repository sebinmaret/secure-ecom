package com.unbcs.oauth.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.unbcs.oauth.service.JpaUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JpaUserDetailsManager jpaUserDetailsManager;

	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

		OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
		RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
		RequestMatcher newEndPointMatcher = new OrRequestMatcher(endpointsMatcher,
				new AntPathRequestMatcher("/user", HttpMethod.POST.name()),
				new AntPathRequestMatcher("/user/admin", HttpMethod.POST.name()),
				new AntPathRequestMatcher("/user/changepassword", HttpMethod.POST.name()));

		http.securityMatcher(newEndPointMatcher)
				.authorizeHttpRequests(authorize -> authorize.requestMatchers(HttpMethod.POST, "/user").permitAll()
						.requestMatchers(HttpMethod.POST, "/user/changepassword").hasAnyAuthority("USER", "ADMIN")
						.requestMatchers(HttpMethod.POST, "/user/admin").hasAuthority("ADMIN").anyRequest()
						.authenticated())
				.csrf(csrf -> csrf.ignoringRequestMatchers(newEndPointMatcher)).apply(authorizationServerConfigurer);

		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class).oidc(Customizer.withDefaults()); // Enable
																										// OpenID
																										// Connect
																										// 1.0
		http.logout(Customizer.withDefaults())
				// Redirect to the login page when not authenticated from the
				// authorization endpoint
				.exceptionHandling((exceptions) -> exceptions.accessDeniedHandler(new AccessDeniedHandlerImpl())
						.defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint("/login"),
								new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
				// Accept access tokens for User Info and/or Client Registration
				.oauth2ResourceServer((resourceServer) -> resourceServer
						.jwt(jwt -> jwt.jwtAuthenticationConverter(new CustomAuthenticationConverter())));

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authorize) -> authorize.requestMatchers(HttpMethod.POST, "/user").permitAll()
				.requestMatchers("/user/admin/**").hasAuthority("ADMIN").anyRequest().authenticated())
				// Form login handles the redirect to the login page from the
				// authorization server filter chain
				.formLogin(formLogin -> formLogin.loginPage("/login").permitAll());
//				.formLogin(Customizer.withDefaults());

		return http.build();
	}

	static class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
		public AbstractAuthenticationToken convert(Jwt jwt) {
			Collection<String> authorities = jwt.getClaimAsStringList("roles");
			Collection<GrantedAuthority> grantedAuthorities = authorities.stream().map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());
			return new JwtAuthenticationToken(jwt, grantedAuthorities);
		}
	}

//	@Bean
//	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//		http.authorizeHttpRequests(
//				(authorize) -> authorize.requestMatchers("/error").permitAll().anyRequest().authenticated());
//
//		// Form login handles the redirect to the login page from the
//		// authorization server filter chain
//		http.formLogin(Customizer.withDefaults());
//
//		return http.build();
//
//	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.debug(false).ignoring().requestMatchers("/webjars/**", "/images/**", "/css/**",
				"/assets/**", "/favicon.ico");
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(4);
	}

	@Bean
	public DaoAuthenticationProvider jpaDaoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(jpaUserDetailsManager);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(jpaDaoAuthenticationProvider());
	}

//	.cors(cors -> cors.configurationSource(corsConfigurationSource()))
//    @Bean
//	CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.addAllowedOriginPattern("*");
//        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
//        configuration.setAllowCredentials(false);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

//	@Bean
//	OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(AuthGrantedAuthorityRepository authorityRepo) {
//		return context -> {
//			if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN) {
//				Authentication principal = context.getPrincipal();
//				Set<String> authorities = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
//						.collect(Collectors.toSet());
////				System.out.println("SIZE"
////						+ authorityRepo.findGrantedAuthorityByUserId(((AuthUserDetails) principal).getId()).size());
//
//				context.getClaims().claim("roles", authorities);
//			}
//		};
//	}

	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
		return (context) -> {
			if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
				context.getClaims().claims((claims) -> {
					Set<String> roles = AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
							.stream().map(c -> c.replaceFirst("^ROLE_", ""))
							.collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
					claims.put("roles", roles);
				});
			}
		};
	}

//	@Bean
//	ApplicationRunner clientsRunner(RegisteredClientRepository registeredClientRepository,
//			UserDetailsManager userDetailsManager) {
//		return args -> {
//			var clientId = "demo";
////			if (registeredClientRepository.findByClientId(clientId) == null) {
//			registeredClientRepository.save(RegisteredClient.withId(UUID.randomUUID().toString()).clientId(clientId)
//					.clientSecret("blaaah")
//					.tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(30)).build())
////						.authorizationGrantTypes(authorizationGrantTypes -> authorizationGrantTypes.addAll(Set.of(
////								AuthorizationGrantType.CLIENT_CREDENTIALS, AuthorizationGrantType.AUTHORIZATION_CODE,
////								AuthorizationGrantType.REFRESH_TOKEN)))
//					.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
//					.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//					.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
//					.authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
//					.redirectUris(uri -> uri.add("http://localhost:3000/signin-oidc"))
//					.scopes(scopes -> scopes.addAll(Set.of("user.read", "user.write", "openid")))
////						.clientAuthenticationMethods(cam -> cam.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC))
//					.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//					.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
//					.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
//					.postLogoutRedirectUri("http://localhost:3000/logout/callback")
//					.clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build()).build());
////			}
//
//		};
//	}

}
