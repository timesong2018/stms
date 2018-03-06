package com.ukefu.webim.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ukefu.webim.web.model.OnlineUserHis;

public abstract interface OnlineUserHisRepository extends JpaRepository<OnlineUserHis, String>
{
	public abstract OnlineUserHis findById(String paramString);

	public abstract List<OnlineUserHis> findByUserid(String userid);

	public abstract List<OnlineUserHis> findBySessionid(String paramString);
}
