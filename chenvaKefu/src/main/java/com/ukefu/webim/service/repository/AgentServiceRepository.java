package com.ukefu.webim.service.repository;

import com.ukefu.webim.web.model.AgentService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract interface AgentServiceRepository
  extends JpaRepository<AgentService, String>
{
  public abstract AgentService findById(String paramString);
  
  public abstract List<AgentService> findByUserid(String paramString);
  
  public abstract Page<AgentService> findByOrgi(String paramString, Pageable paramPageable);
  
  public abstract Page<AgentService> findByOrgiAndStatus(String orgi ,String status , Pageable paramPageable);
  
  public abstract List<AgentService> findByAgentnoAndStatus(String paramString1, String paramString2);
  
  public abstract int countByUseridAndOrgiAndStatus(String paramString1, String paramString2, String paramString3);
}
