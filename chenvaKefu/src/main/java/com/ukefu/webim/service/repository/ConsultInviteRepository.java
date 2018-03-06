package com.ukefu.webim.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ukefu.webim.web.model.CousultInvite;

public abstract interface ConsultInviteRepository
  extends JpaRepository<CousultInvite, String>
{
  public abstract CousultInvite findBySnsaccountid(String paramString);
  
  public abstract List<CousultInvite> findByOrgi(String orgi);
}

