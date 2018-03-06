package com.ukefu.webim.service.repository;

import com.ukefu.webim.web.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract interface RoleRepository
  extends JpaRepository<Role, String>
{
  public abstract Role findById(String paramString);
  
  public abstract Role findByName(String paramString);
}

