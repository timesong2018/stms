package com.ukefu.webim.service.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ukefu.webim.web.model.WxMpEvent;

public abstract interface WxMpEventRepository extends JpaRepository<WxMpEvent, String>{
	
	public abstract WxMpEvent findById(String id);
	
	@Query("select event, count(distinct creater) as users from WxMpEvent where orgi = ?1 and createtime > ?2 and createtime < ?3 group by event")
	List<Object> findByOrgiAndCreatetimeRangeForClient(String orgi , Date start , Date end);

}
