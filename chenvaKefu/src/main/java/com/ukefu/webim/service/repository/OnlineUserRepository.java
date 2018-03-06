package com.ukefu.webim.service.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ukefu.webim.web.model.OnlineUser;

public abstract interface OnlineUserRepository extends JpaRepository<OnlineUser, String> {
	public abstract OnlineUser findBySessionid(String paramString);
	
	public abstract OnlineUser findByUserid(String userid);
	
	public abstract OnlineUser findByOrgiAndSessionid(String orgi , String sessionid);
	
	public abstract Page<OnlineUser> findByOrgiAndStatusAndCreatetimeLessThan(String orgi , String status , Date createtime , Pageable paramPageable);

	public abstract Page<OnlineUser> findByOrgiAndStatus(String paramString1,String paramString2, Pageable paramPageable);
	
	@Query("select invitestatus , count(id) as users from OnlineUser where orgi = ?1 and status = ?2 group by invitestatus")
	List<Object> findByOrgiAndStatus(String orgi ,String status);
	
	@Query("select result , count(id) as records from InviteRecord where orgi = ?1 and agentno = ?2 and createtime > ?3 and createtime < ?4 group by result")
	List<Object> findByOrgiAndAgentnoAndCreatetimeRange(String orgi ,String agentno , Date start , Date end);
	
	@Query("select result , count(id) as records from InviteRecord where orgi = ?1 and agentno = ?2 group by result")
	List<Object> findByOrgiAndUserid(String orgi ,String userid);

	@Query("select count(id) from AgentService where orgi = ?1 and status = ?2 and agentno = ?3 and createtime > ?4 and createtime < ?5")
	Long countByAgentForAgentUser(String orgi ,String status,String agentno , Date start , Date end);
	
	@Query("select count(id) from AgentService where orgi = ?1 and status = ?2 and agentno = ?3 and createtime > ?4 and createtime < ?5")
	Long countByAgentForAgentService(String orgi ,String status,String agentno , Date start , Date end);
	
	@Query("select avg(sessiontimes) from AgentService where orgi = ?1 and status = ?2 and agentno = ?3 and createtime > ?4 and createtime < ?5")
	Long countByAgentForAvagTime(String orgi ,String status,String agentno , Date start , Date end);
	
	
	@Query("select avg(sessiontimes) from AgentService where orgi = ?1 and status = ?2 and userid = ?3")
	Long countByUserForAvagTime(String orgi ,String status,String userid);
	
	@Query("select date_format(createtime , '%Y-%m-%d') as dt, count(distinct ip) as ips ,  count(id) as records from UserHistory where orgi = ?1 and model = ?2 and createtime > ?3 and createtime < ?4 group by date_format(createtime , '%Y-%m-%d') order by dt asc")
	List<Object> findByOrgiAndCreatetimeRange(String orgi , String model ,Date start , Date end);
	
	@Query("select date_format(createtime , '%Y-%m-%d') as dt, count(id) as users from AgentService where orgi = ?1 and createtime > ?2 and createtime < ?3 group by date_format(createtime , '%Y-%m-%d') order by dt asc")
	List<Object> findByOrgiAndCreatetimeRangeForAgent(String orgi , Date start , Date end);
	
	@Query("select osname, count(id) as users from AgentService where orgi = ?1 and createtime > ?2 and createtime < ?3 group by osname")
	List<Object> findByOrgiAndCreatetimeRangeForClient(String orgi , Date start , Date end);
	
	@Query("select browser, count(id) as users from AgentService where orgi = ?1 and createtime > ?2 and createtime < ?3 group by browser")
	List<Object> findByOrgiAndCreatetimeRangeForBrowser(String orgi , Date start , Date end);
	
	@Query("select agentno, count(id) as users from AgentService where orgi = ?1 and userid = ?2 group by agentno")
	List<Object> findByOrgiForDistinctAgent(String orgi , String userid);
}
