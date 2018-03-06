package com.ukefu.webim.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ukefu.webim.web.model.WeiXinUser;

public abstract interface WeiXinUserRepository extends JpaRepository<WeiXinUser, String>{
	
	public abstract WeiXinUser findById(String id);

	public abstract WeiXinUser findByOpenid(String openid);
	
	public abstract long countBySnsid(String snsid);
	
	public abstract Page<WeiXinUser> findBySnsid(String snsid , Pageable page);
}
