
package com.ukefu.webim.web.handler.apps.internet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ukefu.core.UKDataContext;
import com.ukefu.util.BrowserClient;
import com.ukefu.util.CheckMobile;
import com.ukefu.util.IP;
import com.ukefu.util.IPTools;
import com.ukefu.util.Menu;
import com.ukefu.util.UKTools;
import com.ukefu.util.webim.WebIMClient;
import com.ukefu.webim.service.repository.ChatMessageRepository;
import com.ukefu.webim.service.repository.ConsultInviteRepository;
import com.ukefu.webim.service.repository.InviteRecordRepository;
import com.ukefu.webim.util.MessageUtils;
import com.ukefu.webim.util.OnlineUserUtils;
import com.ukefu.webim.web.handler.Handler;
import com.ukefu.webim.web.model.CousultInvite;
import com.ukefu.webim.web.model.InviteRecord;
import com.ukefu.webim.web.model.UploadStatus;
import com.ukefu.webim.web.model.User;
import com.ukefu.webim.web.model.UserHistory;

@Controller
@RequestMapping("/im")
@EnableAsync
public class IMController extends Handler{
	
	@Value("${uk.im.server.host}")  
    private String host;  
  
    @Value("${uk.im.server.port}")  
    private Integer port; 
    
    @Value("${web.upload-path}")
    private String path;
    
	@Autowired
	private ConsultInviteRepository inviteRepository;
	
	@Autowired
	private ChatMessageRepository chatMessageRes ;
	
	@Autowired
	private InviteRecordRepository inviteRecordRes ;

    @RequestMapping("/{id}")
    @Menu(type = "im" , subtype = "point" , access = true)
    public ModelAndView point(HttpServletRequest request , HttpServletResponse response, @PathVariable String id , @Valid String orgi , @Valid String userid , @Valid String title) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/point")) ; 
    	
    	view.addObject("hostname", request.getServerName()) ;
		view.addObject("port", request.getServerPort()) ;
		view.addObject("schema", request.getScheme()) ;
		view.addObject("appid", id) ;
		view.addObject("client", UKTools.getUUID()) ;
		view.addObject("sessionid", request.getSession().getId()) ;
		
		CousultInvite invite = inviteRepository.findOne(id) ;
    	if(invite!=null){
    		view.addObject("inviteData", invite);
    		view.addObject("orgi",invite.getOrgi());
    		view.addObject("appid",id);
    	//记录用户行为日志
			UserHistory userHistory = new UserHistory() ;
			String url = request.getRequestURL().toString() ;
			if(url.length() >255){
				userHistory.setUrl(url.substring( 0 , 255));
			}else{
				userHistory.setUrl(url);
			}
			userHistory.setParam(UKTools.getParameter(request));
			if(userHistory!=null){
				userHistory.setMaintype("im");
				userHistory.setSubtype("point");
				userHistory.setName("online");
				userHistory.setAdmin(false);
				userHistory.setAccess(true);
			}
			User imUser = super.getIMUser(request , userid) ;
			if(imUser!=null){
				userHistory.setCreater(imUser.getId());
				userHistory.setUsername(imUser.getUsername());
				userHistory.setOrgi(orgi);
			}
			if(!StringUtils.isBlank(title)){
				if(title.length() > 255){
					userHistory.setTitle(title.substring(0 , 255));
				}else{
					userHistory.setTitle(title);
				}
			}
			userHistory.setOrgi(invite.getOrgi());
			userHistory.setAppid(id);
			userHistory.setSessionid(request.getSession().getId());
			userHistory.setHostname(request.getRemoteHost());
			userHistory.setIp(request.getRemoteAddr());
			IP ipdata = IPTools.getInstance().findGeography(request.getRemoteAddr());
			userHistory.setCountry(ipdata.getCountry());
			userHistory.setProvince(ipdata.getProvince());
			userHistory.setCity(ipdata.getCity());
		    userHistory.setIsp(ipdata.getIsp());
		    
		    BrowserClient client = UKTools.parseClient(request) ;
		    userHistory.setOstype(client.getOs());
		    userHistory.setBrowser(client.getBrowser());
		    userHistory.setMobile(CheckMobile.check(request.getHeader("User-Agent")) ? "1" : "0");
		    
			UKTools.published(userHistory);
		}
		
        return view;
    }
    
    @RequestMapping(value="/chat")
    @ResponseBody
    @Menu(type = "im" , subtype = "point" , access = true)
    public Map<String,Object> chat(HttpServletRequest request , HttpServletResponse response,  @Valid String id , @Valid String orgi , @Valid String userid , @Valid String title) {
    	Map<String,Object> result = new HashMap<>();
    	result.put("hostname", request.getServerName()) ;
    	result.put("port", request.getServerPort()) ;
    	result.put("schema", request.getScheme()) ;
    	result.put("appid", id) ;
    	result.put("client", UKTools.getUUID()) ;
    	result.put("sessionid", request.getSession().getId()) ;
		
		CousultInvite invite = inviteRepository.findOne(id) ;
    	if(invite!=null){
    		result.put("inviteData", invite);
    		result.put("orgi",invite.getOrgi());
    		result.put("appid",id);
    	//记录用户行为日志
			UserHistory userHistory = new UserHistory() ;
			String url = request.getRequestURL().toString() ;
			if(url.length() >255){
				userHistory.setUrl(url.substring( 0 , 255));
			}else{
				userHistory.setUrl(url);
			}
			userHistory.setParam(UKTools.getParameter(request));
			if(userHistory!=null){
				userHistory.setMaintype("im");
				userHistory.setSubtype("point");
				userHistory.setName("online");
				userHistory.setAdmin(false);
				userHistory.setAccess(true);
			}
			User imUser = super.getIMUser(request , userid) ;
			if(imUser!=null){
				userHistory.setCreater(imUser.getId());
				userHistory.setUsername(imUser.getUsername());
				userHistory.setOrgi(orgi);
			}
			if(!StringUtils.isBlank(title)){
				if(title.length() > 255){
					userHistory.setTitle(title.substring(0 , 255));
				}else{
					userHistory.setTitle(title);
				}
			}
			userHistory.setOrgi(invite.getOrgi());
			userHistory.setAppid(id);
			userHistory.setSessionid(request.getSession().getId());
			userHistory.setHostname(request.getRemoteHost());
			userHistory.setIp(request.getRemoteAddr());
			IP ipdata = IPTools.getInstance().findGeography(request.getRemoteAddr());
			userHistory.setCountry(ipdata.getCountry());
			userHistory.setProvince(ipdata.getProvince());
			userHistory.setCity(ipdata.getCity());
		    userHistory.setIsp(ipdata.getIsp());
		    
		    BrowserClient client = UKTools.parseClient(request) ;
		    userHistory.setOstype(client.getOs());
		    userHistory.setBrowser(client.getBrowser());
		    userHistory.setMobile(CheckMobile.check(request.getHeader("User-Agent")) ? "1" : "0");
		    
			UKTools.published(userHistory);
		}
		
        return result;
    }
    /**
     * 延时获取用户端浏览器的跟踪ID
     * @param request
     * @param response
     * @param orgi
     * @param appid
     * @param userid
     * @param sign
     * @return
     */
    @RequestMapping("/online")
    @Menu(type = "im" , subtype = "online" , access = true)
    public SseEmitter callable(HttpServletRequest request , HttpServletResponse response ,final @Valid String orgi , @Valid String appid, final @Valid String userid , @Valid String sign , final @Valid String client) {
		final SseEmitter emitter = new SseEmitter();
		emitter.onCompletion(new Runnable() {
			@Override
			public void run() {	
				try {
					OnlineUserUtils.webIMClients.removeClient(userid , client , false) ; //执行了 邀请/再次邀请后终端的
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
		});
		emitter.onTimeout(new Runnable() {	
			@Override
			public void run() {
				emitter.complete();
				try {
					OnlineUserUtils.webIMClients.removeClient(userid , client , true) ;	//正常的超时断开
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
    	if(!StringUtils.isBlank(sign)){
    		OnlineUserUtils.online(super.getIMUser(request , sign) , orgi , request.getSession().getId() , UKDataContext.OnlineUserTypeStatus.WEBIM.toString(), request , UKDataContext.ChannelTypeEnum.WEBIM.toString() , appid);
    	}
		
    	OnlineUserUtils.webIMClients.putClient(userid, new WebIMClient(userid  , client , emitter)) ;
		return emitter;
	}
    
    @RequestMapping("/index")
    @Menu(type = "im" , subtype = "index" , access = true)
    public ModelAndView index(HttpServletRequest request , HttpServletResponse response, @Valid String orgi , @Valid String appid, @Valid String userid, @Valid String sessionid) throws Exception {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/index")) ; 
    	
    	view.addObject("hostname", request.getServerName()) ;
		view.addObject("port", port) ;
		view.addObject("appid", appid) ;
		view.addObject("userid", userid) ;
		view.addObject("schema", request.getScheme()) ;
		view.addObject("sessionid", sessionid) ;
		view.addObject("chatMessageList", chatMessageRes.findBySession(sessionid , new PageRequest(0, 20, Direction.DESC , "createtime"))) ;
		CousultInvite invite = inviteRepository.findOne(appid) ;
    	if(invite!=null){
    		view.addObject("inviteData", invite);
    		view.addObject("orgi",invite.getOrgi());
    	}
//    	OnlineUserUtils.sendWebIMClients(userid , "accept");
    	Page<InviteRecord> inviteRecordList = inviteRecordRes.findByUseridAndOrgi(userid, orgi , new PageRequest(0, 1, Direction.DESC, "createtime")) ;
    	if(inviteRecordList.getContent()!=null && inviteRecordList.getContent().size()>0){
    		InviteRecord record = inviteRecordList.getContent().get(0) ;
    		record.setUpdatetime(new Date());
    		record.setResponsetime((int) (System.currentTimeMillis() - record.getCreatetime().getTime()));
    		record.setResult(UKDataContext.OnlineUserInviteStatus.ACCEPT.toString());
    		inviteRecordRes.save(record) ;
    	}
        return view;
    }
    
    @RequestMapping("/refuse")
    @Menu(type = "im" , subtype = "refuse" , access = true)
    public void refuse(HttpServletRequest request , HttpServletResponse response, @Valid String orgi , @Valid String appid, @Valid String userid, @Valid String sessionid, @Valid String client) throws Exception {
    	OnlineUserUtils.refuseInvite(userid, orgi);
//    	OnlineUserUtils.sendWebIMClients(userid , "refuse");
    	Page<InviteRecord> inviteRecordList = inviteRecordRes.findByUseridAndOrgi(userid, orgi , new PageRequest(0, 1, Direction.DESC, "createtime")) ;
    	if(inviteRecordList.getContent()!=null && inviteRecordList.getContent().size()>0){
    		InviteRecord record = inviteRecordList.getContent().get(0) ;
    		record.setUpdatetime(new Date());
    		record.setResponsetime((int) (System.currentTimeMillis() - record.getCreatetime().getTime()));
    		record.setResult(UKDataContext.OnlineUserInviteStatus.REFUSE.toString());
    		inviteRecordRes.save(record) ;
    	}
        return;
    }
    
    @RequestMapping("/image/upload")
    @Menu(type = "im" , subtype = "image" , access = true)
    public ModelAndView upload(ModelMap map,HttpServletRequest request , @RequestParam(value = "imgFile", required = false) MultipartFile imgFile , @Valid String userid) throws IOException {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/upload")) ; 
    	UploadStatus upload = null ;
    	String fileName = null ;
    	if(imgFile!=null && imgFile.getOriginalFilename().lastIndexOf(".") > 0 && !StringUtils.isBlank(userid)){
    		File uploadDir = new File(path , "upload");
    		if(!uploadDir.exists()){
    			uploadDir.mkdirs() ;
    		}
    		fileName = "upload/"+UKTools.md5(imgFile.getBytes())+imgFile.getOriginalFilename().substring(imgFile.getOriginalFilename().lastIndexOf(".")) ;
    		FileCopyUtils.copy(imgFile.getBytes(), new File(path , fileName));
    		upload = new UploadStatus("0" , "/res/image.html?id="+fileName);
    		
    		String image =  request.getScheme()+"://"+request.getServerName()+"/res/image.html?id="+fileName ;
    		if(request.getServerPort() == 80){
    			image = request.getScheme()+"://"+request.getServerName()+"/res/image.html?id="+fileName;
			}else{
				image = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/res/image.html?id="+fileName;
			}
    		MessageUtils.uploadImage(image, userid);
    	}else{
    		upload = new UploadStatus("请选择图片文件");
    	}
    	map.addAttribute("upload", upload) ;
        return view ; 
    }
}