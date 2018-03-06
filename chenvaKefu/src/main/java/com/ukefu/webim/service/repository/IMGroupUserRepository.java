package com.ukefu.webim.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ukefu.webim.web.model.IMGroup;
import com.ukefu.webim.web.model.IMGroupUser;
import com.ukefu.webim.web.model.User;

public abstract interface IMGroupUserRepository extends
		JpaRepository<IMGroupUser, String> {
	
	
	public abstract IMGroupUser findById(String id);

	public List<IMGroupUser> findByImgroup(IMGroup imgroup);
	
	public IMGroupUser findByImgroupAndUser(IMGroup imgroup , User user);
	
	public List<IMGroupUser> findByUser(User user);
	
}
