package com.ukefu.webim.service.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ukefu.webim.web.model.AgentUserTask;

public abstract interface AgentUserTaskRepository  extends JpaRepository<AgentUserTask, String>{
	
	public abstract AgentUserTask findById(String id);
	
	public List<AgentUserTask> findByLastmessageLessThanAndStatus(Date start , String status) ;
	
	public List<AgentUserTask> findByLastgetmessageLessThanAndStatus(Date start , String status) ;
	
}

