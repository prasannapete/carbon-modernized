package com.pcpl.carbon.authserver.Config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.pcpl.carbon.authserver.User.Model.User;
import com.pcpl.carbon.authserver.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import com.pcpl.carbon.authserver.Tenant.Service.TenantService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private static final String AUTHORITIES_CLAIM = "authorities";
    private final PasswordEncoder passwordEncoder;
    private final TenantService tenantService;

    /** Resolve the current request's tenant id (from its sub-domain) for token stamping. */
    private Long resolveCurrentTenantId() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return tenantService.resolveTenantId(attributes.getRequest());
        }
        return null;
    }

    @Autowired
    CBConfig cbConfig;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();
        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        http
                .securityMatcher(endpointsMatcher)
                // Allow the SRL Dashboard SPA to call the token/OAuth2 endpoints cross-origin.
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                .csrf((csrf) -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer.oidc(Customizer.withDefaults())    // Enable OpenID Connect 1.0
                );
//                .tokenGenerator(tokenGenerator()); //Custom token generator
        http
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                // Accept access tokens for User Info and/or Client Registration
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()));

        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/library/**").permitAll()
                        .requestMatchers("/forgot-password-screen").permitAll()
                        // Benign Chrome DevTools probe (see LoginController) - answer without auth.
                        .requestMatchers("/.well-known/appspecific/com.chrome.devtools.json").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin.loginPage("/login").permitAll()
                )
                .logout(logout ->
                        // The SRL Dashboard SPA triggers logout with a top-level GET redirect to
                        // /logout (a cross-origin XHR cannot send the SRLSESSION cookie, so it could
                        // never invalidate the SSO session). Invalidate the session, clear the real
                        // session cookie (SRLSESSION), and return to the dashboard so it re-authenticates.
                        logout.logoutRequestMatcher(PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/logout"))
                                .logoutSuccessUrl(cbConfig.getLoginRedirectURL())
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .deleteCookies("SRLSESSION", "JSESSIONID")
                                .permitAll()
                );
        return http.build();
    }


    /**
     * CORS for the SRL Dashboard SPA, which performs the authorization-code -> token
     * exchange from the browser (cross-origin) against the OAuth2 endpoints.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001"));
        configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtEncoder jwtEncoder() throws Exception{
        return new NimbusJwtEncoder(jwkSource());
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public OAuth2TokenGenerator<?> tokenGenerator() {
       try {
           JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder());
           jwtGenerator.setJwtCustomizer(jwtCustomizer());
           OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
           accessTokenGenerator.setAccessTokenCustomizer(accessTokenCustomizer());
           OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
           return new DelegatingOAuth2TokenGenerator(
                   jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
       }catch (Exception ex){
           log.error(ex.getMessage());
           log.error(Arrays.toString(ex.getStackTrace()));
       }
       return null;
    }

    @Bean
    public OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer() {
        return context -> {
            if ("id_token".equals(context.getTokenType().getValue())) {
                context.getClaims().claims((claims) -> {
                    Set<String> roles = AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
                            .stream()
                            .filter(authority -> !authority.startsWith("FACTOR_"))
                            .map(authority -> authority.replace("ROLE_", ""))
                            .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
                    claims.put("roles", roles);

                    Optional<User> optionalUser = userRepository.findByUserNameAndIsDeleted(context.getPrincipal().getName(), 0);
                    Map<String, String> userData = new HashMap<>();
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();
                        userData.put("firstName", user.getFirstName());
                        userData.put("lastName", user.getLastName());
                        userData.put("userName",user.getUserName());
                        claims.put("custom_value", userData);
                    }
                });
            }
            if (context.getTokenType().getValue().equals(OAuth2TokenType.ACCESS_TOKEN.getValue())) {
                context.getClaims().claims((claims) -> {
                    if (context.getAuthorizationGrantType().getValue().equals(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue())) {
                        Set<String> roles = context.getClaims().build().getClaim("scope");
                        claims.put("roles", roles);
                    } else if (context.getAuthorizationGrantType().getValue().equals(AuthorizationGrantType.AUTHORIZATION_CODE.getValue())) {
                        Set<String> roles =  AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
                                .stream()
                                .filter(authority -> !authority.startsWith("FACTOR_"))
                                .map(authority -> authority.replace("ROLE_",""))
                                .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
                        claims.put("roles", roles);
                        Optional< User> optionalUser = userRepository.findByUserNameAndIsDeleted(context.getPrincipal().getName(),0);
                        Map<String,String> userData = new HashMap<>();
                        if(optionalUser.isPresent()){
                            User user = optionalUser.get();
                            userData.put("firstName",user.getFirstName());
                            userData.put("lastName",user.getLastName());
                            userData.put("userName",user.getUserName());
                            claims.put("custom_value", userData);
                            // Users with a tenant get a tenantId claim (scoped access).
                            // Users with tenant_id null (e.g. super-admin) get no claim,
                            // so downstream services grant them cross-tenant (all data) access.
                            if (user.getTenantId() != null) {
                                claims.put("tenantId", user.getTenantId());
                            }

                        }
                    }

                });
            }
            // Customize claims

        };
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return (context) -> {
            if ("id_token".equals(context.getTokenType().getValue())) {
                context.getClaims().claims((claims) -> {
                    Authentication principal = context.getPrincipal();
                    Set<String> authorities = principal.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .filter(authority -> !authority.startsWith("FACTOR_"))
                            .collect(Collectors.toSet());
                    context.getClaims().claim(AUTHORITIES_CLAIM, authorities);
                    Authentication authentication = context.getAuthorizationGrant();
                });
            }
            if (context.getTokenType().getValue().equals(OAuth2TokenType.ACCESS_TOKEN.getValue())) {
                context.getClaims().claims((claims) -> {
                    if (context.getAuthorizationGrantType().getValue().equals(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue())) {
                        Set<String> roles = context.getClaims().build().getClaim("scope");
                        claims.put("roles", roles);
                    } else if (context.getAuthorizationGrantType().getValue().equals(AuthorizationGrantType.AUTHORIZATION_CODE.getValue())) {
                        Set<String> roles =  AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
                               .stream()
                               .filter(authority -> !authority.startsWith("FACTOR_"))
                                .map(authority -> authority.replace("ROLE_",""))
                               .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
                        claims.put("roles", roles);
                        Optional< User> optionalUser = userRepository.findByUserNameAndIsDeleted(context.getPrincipal().getName(),0);
                        Map<String,String> userData = new HashMap<>();
                        if(optionalUser.isPresent()){
                            User user = optionalUser.get();
                            userData.put("firstName",user.getFirstName());
                            userData.put("lastName",user.getLastName());
                            userData.put("userName",user.getUserName());
                            claims.put("custom_value", userData);
                            // Users with a tenant get a tenantId claim (scoped access).
                            // Users with tenant_id null (e.g. super-admin) get no claim,
                            // so downstream services grant them cross-tenant (all data) access.
                            if (user.getTenantId() != null) {
                                claims.put("tenantId", user.getTenantId());
                            }

                        }
                    }

                });
            }

        };
    }
}