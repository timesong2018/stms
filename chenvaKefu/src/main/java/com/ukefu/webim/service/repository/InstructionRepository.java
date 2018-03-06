package com.ukefu.webim.service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ukefu.webim.web.model.Instruction;

public abstract interface InstructionRepository  extends JpaRepository<Instruction, String>
{
  public abstract Instruction findById(String id);
  
  public abstract Page<Instruction> findAll(Pageable paramPageable);
  
  public abstract Instruction findByName(String name);
  
  public abstract List<Instruction> findByOrgi(String orgi);
  
  public abstract List<Instruction> findBySnsid(String snsid);
  
  public abstract long countByName(String name);
  
  public abstract long countByNameAndIdNot(String name , String id);
}
