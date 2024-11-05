package com.example.bookreadingonline.service.impl;

import com.example.bookreadingonline.entity.Role;
import com.example.bookreadingonline.repository.RoleRepository;
import com.example.bookreadingonline.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepo;

  @Override
  public JpaRepository<Role, Integer> getRepository() {
    return roleRepo;
  }

}