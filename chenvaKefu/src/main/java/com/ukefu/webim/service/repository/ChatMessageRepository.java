package com.ukefu.webim.service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ukefu.webim.util.server.message.ChatMessage;

public abstract interface ChatMessageRepository
  extends JpaRepository<ChatMessage, String>
{
  public abstract List<ChatMessage> findBySession(String session);
  
  public abstract Page<ChatMessage> findBySession(String session , Pageable page);
  
  public abstract List<ChatMessage> findByContextid(String contextid);
  
  public abstract Page<ChatMessage> findByContextid(String contextid , Pageable page);
  
  public abstract Page<ChatMessage> findByAgentserviceid(String agentserviceid , Pageable page);
  
  public abstract Page<ChatMessage> findByContextidAndUserid(String contextid ,String userid, Pageable page);
}
