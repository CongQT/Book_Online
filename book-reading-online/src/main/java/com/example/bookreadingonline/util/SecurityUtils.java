package com.example.bookreadingonline.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

  public static Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public static <T extends Authentication> T getAuthentication(Class<T> clazz) {
    return clazz.cast(getAuthentication());
  }

  public static Object getPrincipal() {
    Authentication authentication = getAuthentication();
    return authentication != null ? getAuthentication().getPrincipal() : null;
  }

  public static <T> T getPrincipal(Class<T> clazz) {
    Object principal = getPrincipal();
    return clazz.isInstance(principal) ? clazz.cast(principal) : null;
  }

  public static UserDetails getUserDetails() {
    return getPrincipal(UserDetails.class);
  }

  public static <T extends UserDetails> T getUserDetails(Class<T> clazz) {
    return clazz.cast(getUserDetails());
  }

  public static String getUserName() {
    UserDetails userDetails = getUserDetails();
    return userDetails != null ? getUserDetails().getUsername() : null;
  }

}
