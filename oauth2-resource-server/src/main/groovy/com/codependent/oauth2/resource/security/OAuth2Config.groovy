package com.codependent.oauth2.resource.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.RemoteTokenServices

@Configuration
class OAuth2Config extends ResourceServerConfigurerAdapter{

	@Value('${security.oauth2.resource.token-info-uri}')
	private String checkTokenEndpointUrl
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
			// Since we want the protected resources to be accessible in the UI as well we need
			// session creation to be allowed (it's disabled by default in 2.0.6)
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
		.and()
			.requestMatchers().antMatchers("/users/**")
		.and()
			.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/users").access("#oauth2.hasScope('read_users')")
				.antMatchers(HttpMethod.PUT, "/users/**").access("#oauth2.hasScope('write_users')")
				.antMatchers(HttpMethod.POST, "/users/**").access("#oauth2.hasScope('write_users')")
		/* Examples:
		   .and()
				.authorizeRequests()
					.antMatchers("/me").access("#oauth2.hasScope('read')")					
					.antMatchers("/photos").access("#oauth2.hasScope('read') or (!#oauth2.isOAuth() and hasRole('ROLE_USER'))")                                        
					.antMatchers("/photos/trusted/**").access("#oauth2.hasScope('trust')")
					.antMatchers("/photos/user/**").access("#oauth2.hasScope('trust')")					
					.antMatchers("/photos/**").access("#oauth2.hasScope('read') or (!#oauth2.isOAuth() and hasRole('ROLE_USER'))")
					.regexMatchers(HttpMethod.DELETE, "/oauth/users/([^/].*?)/tokens/.*")
						.access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('write')")
					.regexMatchers(HttpMethod.GET, "/oauth/clients/([^/].*?)/users/.*")
						.access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('read')")
					.regexMatchers(HttpMethod.GET, "/oauth/clients/.*")
						.access("#oauth2.clientHasRole('ROLE_CLIENT') and #oauth2.isClient() and #oauth2.hasScope('read')");
		 */
	}
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		RemoteTokenServices remoteTokenServices = new RemoteTokenServices()
		remoteTokenServices.setCheckTokenEndpointUrl(checkTokenEndpointUrl)
		remoteTokenServices.setClientId("usersResourceProvider")
		remoteTokenServices.setClientSecret("usersResourceProviderSecret")
		resources.tokenServices(remoteTokenServices)
	}
}
