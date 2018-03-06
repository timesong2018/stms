package com.ukefu.webim.service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ukefu.webim.web.model.AgentUser;

public abstract interface AgentUserRepository  extends JpaRepository<AgentUser, String>
{
	public abstract AgentUser findById(String paramString);

	public abstract AgentUser findByUserid(String paramString);

	public abstract List<AgentUser> findByAgentno(String paramString);

	public abstract Page<AgentUser> findByOrgiAndStatus(String orgi ,String status , Pageable page);
	
	public abstract List<AgentUser> findByAgentnoAndStatus(String agentno ,String status);
	
	public abstract int countByAgentnoAndStatus(String agentno ,String status);

}
