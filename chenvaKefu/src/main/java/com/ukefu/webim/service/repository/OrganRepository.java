package com.ukefu.webim.service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ukefu.webim.web.model.Organ;

public abstract interface OrganRepository
  extends JpaRepository<Organ, String>
{
  public abstract Organ findById(String paramString);
  
  public abstract Page<Organ> findAll(Pageable paramPageable);
  
  public abstract Organ findByName(String paramString);
  
  public abstract List<Organ> findByOrgi(String orgi);
}
