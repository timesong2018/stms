package com.ukefu.webim.web.handler.apps.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ukefu.core.UKDataContext;
import com.ukefu.util.Menu;
import com.ukefu.util.UKTools;
import com.ukefu.webim.service.repository.AgentServiceRepository;
import com.ukefu.webim.service.repository.AgentStatusRepository;
import com.ukefu.webim.service.repository.AgentUserRepository;
import com.ukefu.webim.service.repository.ChatMessageRepository;
import com.ukefu.webim.service.repository.OnlineUserHisRepository;
import com.ukefu.webim.service.repository.OnlineUserRepository;
import com.ukefu.webim.service.repository.TagRelationRepository;
import com.ukefu.webim.service.repository.TagRepository;
import com.ukefu.webim.service.repository.UserRepository;
import com.ukefu.webim.web.handler.Handler;
import com.ukefu.webim.web.model.AgentService;
import com.ukefu.webim.web.model.AgentUser;
import com.ukefu.webim.web.model.OnlineUser;

@Controller
@RequestMapping("/service")
public class OnlineUserController extends Handler{
	@Autowired
	private AgentServiceRepository agentServiceRes ;
	
	@Autowired
	private AgentUserRepository agentUserRes ;
	
	@Autowired
	private OnlineUserRepository onlineUserRes; 
	
	@Autowired
	private AgentStatusRepository agentStatusRepository ;
	
	@Autowired
	private OnlineUserHisRepository onlineUserHisRes;
	
	@Autowired
	private UserRepository userRes ;
	
	@Autowired
	private TagRepository tagRes ;
	
	@Autowired
	private TagRelationRepository tagRelationRes ;
	
	@Autowired
	private ChatMessageRepository chatMessageRepository ;
	
	@RequestMapping("/online/index")
    @Menu(type = "service" , subtype = "online" , admin= true)
    public ModelAndView index(ModelMap map , HttpServletRequest request , String userid , String agentservice) {
		OnlineUser onlineUser = onlineUserRes.findByUserid(userid) ; 
		if(onlineUser!=null){
			map.put("onlineUser", onlineUser) ;
			map.put("tags", tagRes.findByOrgi(super.getOrgi(request))) ;
			map.put("tagRelationList", tagRelationRes.findByUserid(onlineUser.getUserid())) ;
			map.put("onlineUserHistList", onlineUserHisRes.findByUserid(onlineUser.getUserid())) ;
			map.put("agentServicesAvg", onlineUserRes.countByUserForAvagTime(super.getOrgi(request), UKDataContext.AgentUserStatusEnum.END.toString(),onlineUser.getUserid())) ;
			
			List<AgentService> agentServiceList = agentServiceRes.findByUserid(onlineUser.getUserid()) ; 
			map.put("inviteResult", UKTools.getWebIMInviteResult(onlineUserRes.findByOrgiAndUserid(super.getOrgi(request), onlineUser.getUserid()))) ;
			map.put("agentServiceList", agentServiceList) ;
			if(agentServiceList.size()>0){
				AgentService agentService = agentServiceList.get(0) ;
				if(!StringUtils.isBlank(agentservice)){
					for(AgentService as : agentServiceList){
						if(as.getId().equals(agentservice)){
							agentService = as ; break ;
						}
					}
				}
				AgentUser curragentuser = agentUserRes.findByUserid(agentService.getUserid()) ;
				map.put("curAgentService", agentService) ;
				map.put("curragentuser", curragentuser) ;
				map.put("agentUserMessageList", chatMessageRepository.findByAgentserviceid(agentService.getId() , new PageRequest(0, 50, Direction.DESC , "createtime")));
			}
		}
        return request(super.createAppsTempletResponse("/apps/service/online/index"));
    }
	
	@RequestMapping("/online/chatmsg")
    @Menu(type = "service" , subtype = "chatmsg" , admin= true)
    public ModelAndView onlinechat(ModelMap map , HttpServletRequest request , String id) {
		AgentService agentService = agentServiceRes.getOne(id) ; 
		AgentUser curragentuser = agentUserRes.findByUserid(agentService.getUserid()) ;
		
		map.put("curAgentService", agentService) ;
		map.put("curragentuser", curragentuser) ;
		
		map.put("agentUserMessageList", chatMessageRepository.findByAgentserviceid(agentService.getId() , new PageRequest(0, 50, Direction.DESC , "createtime")));
		
        return request(super.createRequestPageTempletResponse("/apps/service/online/chatmsg"));
    }
	
	
}
