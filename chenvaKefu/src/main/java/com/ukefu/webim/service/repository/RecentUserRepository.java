package com.ukefu.webim.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ukefu.webim.web.model.RecentUser;
import com.ukefu.webim.web.model.User;

public interface RecentUserRepository extends JpaRepository<RecentUser, String> {
	
	public List<RecentUser> findByCreater(String creater);
	
	public RecentUser findByCreaterAndUser(String creater , User user);
}
