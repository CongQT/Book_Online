package com.example.bookreadingonline.security.core.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService extends UserDetailsService {

  UserDetails loadUserByUsernameAndAccessToken(String username, String accessToken);

}
