package com.example.bookreadingonline.config;

import com.example.bookreadingonline.entity.User;
import com.example.bookreadingonline.security.core.userdetails.impl.UserDetailsImpl;
import com.example.bookreadingonline.util.SecurityUtils;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {

  @Bean
  public AuditorAware<Integer> auditorProvider() {
    return new AuditorAware<>() {
      @NonNull
      @Override
      public Optional<Integer> getCurrentAuditor() {
        return Optional.ofNullable(SecurityUtils.getUserDetails())
            .filter(UserDetailsImpl.class::isInstance)
            .map(UserDetailsImpl.class::cast)
            .map(UserDetailsImpl::getUser)
            .map(User::getId);
      }
    };
  }

  @Bean
  public MethodInvokingFactoryBean methodInvokingFactoryBean() {
    MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
    methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
    methodInvokingFactoryBean.setTargetMethod("setStrategyName");
    methodInvokingFactoryBean.setArguments(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    return methodInvokingFactoryBean;
  }

}
