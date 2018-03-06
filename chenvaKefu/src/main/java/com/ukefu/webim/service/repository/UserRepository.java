package com.ukefu.webim.service.repository;

import com.ukefu.webim.web.model.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract interface UserRepository
  extends JpaRepository<User, String>
{
  public abstract User findById(String paramString);
  
  public abstract User findByUsername(String paramString);
  
  public abstract User findByEmail(String paramString);
  
  public abstract User findByUsernameAndPassword(String paramString1, String password);
  
  public abstract Page<User> findAll(Pageable paramPageable);
  
  public abstract Page<User> findByDatastatus(boolean datastatus , Pageable paramPageable);
  
  public abstract Page<User> findByDatastatusAndUsernameLike(boolean datastatus ,String username ,Pageable paramPageable);
  
  public abstract List<User> findByOrgan(String paramString);
  
  public abstract List<User> findByOrganAndDatastatus(String paramString , boolean datastatus);
  
  public abstract List<User> findByOrgi(String orgi);
  
  public abstract Page<User> findByOrgiAndAgent(String orgi , boolean agent , Pageable paramPageable);
  
  public abstract long countByOrgiAndAgent(String orgi , boolean agent) ;
}
