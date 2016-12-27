package com.codependent.oauth2.client.web

import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientRestController {

	@Autowired
	private OAuth2RestTemplate restTemplate

	@ExceptionHandler(UserRedirectRequiredException.class)
	void userRedirectRequiredException(UserRedirectRequiredException ex, HttpServletResponse response){
		println ex
		String redirectUri = ex.getRedirectUri()
		for(Entry<String, String> entry : ex.getRequestParams().entrySet()){
			if(redirectUri.indexOf("?")==-1){
				redirectUri += "?" + entry.getKey() + "=" + entry.getValue()
			}else{
				redirectUri += "&" + entry.getKey() + "=" + entry.getValue()
			}
		}
		println 'Redirecting to: '+ redirectUri + "&" + ex.getStateKey() + "="+ ex.getStateToPreserve()
		response.sendRedirect(redirectUri)
	}
		
	@GetMapping("/users")
	def getUsers(HttpSession session){
		println 'Session id: '+ session.getId()
		//TODO Move to after authentication
		Authentication auth = SecurityContextHolder.getContext().getAuthentication()
		restTemplate.getOAuth2ClientContext().getAccessTokenRequest().setAll(['username': auth.name, 'password': auth.credentials])
		
		HttpHeaders headers = new HttpHeaders()
		ResponseEntity<List<String>> response = restTemplate.exchange('http://localhost:8081/users', HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>(){}, [])
		response.getBody()
	}
	
	@GetMapping("/users/update")
	def getUsers(@RequestParam String user, HttpSession session){
		println 'Session id: '+ session.getId()
		try{
			//TODO Move to after authentication
			Authentication auth = SecurityContextHolder.getContext().getAuthentication()
			restTemplate.getOAuth2ClientContext().getAccessTokenRequest().setAll(['username': auth.name, 'password': auth.credentials])
			
			HttpHeaders headers = new HttpHeaders()
			ResponseEntity<List<String>> response = restTemplate.exchange('http://localhost:8081/users/'+user, HttpMethod.PUT, null, new ParameterizedTypeReference<List<String>>(){}, [])
			response.getBody()
		}catch(Exception e){
			e.printStackTrace()
			e
		}
	}
	
}
