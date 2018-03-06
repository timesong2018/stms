package com.ukefu.webim.web.handler.admin.webim;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ukefu.util.Menu;
import com.ukefu.util.UKTools;
import com.ukefu.webim.service.repository.ConsultInviteRepository;
import com.ukefu.webim.web.handler.Handler;
import com.ukefu.webim.web.model.CousultInvite;

@Controller
@RequestMapping("/admin/webim")
public class WebIMController extends Handler{
	
	@Autowired
	private ConsultInviteRepository invite;
	
	@Value("${web.upload-path}")
    private String path;

    @RequestMapping("/index")
    @Menu(type = "admin" , subtype = "webim" , admin= true)
    public ModelAndView index(ModelMap map , HttpServletRequest request) {
    	List<CousultInvite> settingList = invite.findByOrgi(super.getUser(request).getOrgi()) ;
    	if(settingList.size() > 0){
    		map.addAttribute("inviteData", settingList.get(0));
    	}
    	map.addAttribute("import", request.getServerPort()) ;
        return request(super.createAdminTempletResponse("/admin/webim/index"));
    }
    
    @RequestMapping("/save")
    @Menu(type = "admin" , subtype = "webim" , admin= true)
    public ModelAndView save(HttpServletRequest request , @Valid CousultInvite inviteData , @RequestParam(value = "webimlogo", required = false) MultipartFile webimlogo,@RequestParam(value = "agentheadimg", required = false) MultipartFile agentheadimg) throws IOException {
    	if(!StringUtils.isBlank(inviteData.getSnsaccountid())){
    		CousultInvite tempData = invite.findBySnsaccountid(inviteData.getSnsaccountid()) ;
    		if(tempData!=null){
    			UKTools.copyProperties(inviteData,tempData);
    			inviteData = tempData ;
    		}
    	}else{
    		inviteData.setSnsaccountid(super.getUser(request).getId());
    	}
    	inviteData.setOrgi(super.getUser(request).getOrgi());
    	if(webimlogo!=null && webimlogo.getOriginalFilename().lastIndexOf(".") > 0){
    		File logoDir = new File(path , "logo");
    		if(!logoDir.exists()){
    			logoDir.mkdirs() ;
    		}
    		String fileName = "logo/"+inviteData.getId()+webimlogo.getOriginalFilename().substring(webimlogo.getOriginalFilename().lastIndexOf(".")) ;
    		FileCopyUtils.copy(webimlogo.getBytes(), new File(path , fileName));
    		inviteData.setConsult_dialog_logo(fileName);
    	}
    	if(agentheadimg!=null && agentheadimg.getOriginalFilename().lastIndexOf(".") > 0){
    		File headimgDir = new File(path , "headimg");
    		if(!headimgDir.exists()){
    			headimgDir.mkdirs() ;
    		}
    		String fileName = "headimg/"+inviteData.getId()+agentheadimg.getOriginalFilename().substring(agentheadimg.getOriginalFilename().lastIndexOf(".")) ;
    		FileCopyUtils.copy(agentheadimg.getBytes(), new File(path , fileName));
    		inviteData.setConsult_dialog_headimg(fileName);
    	}
    	invite.save(inviteData) ;
        return request(super.createRequestPageTempletResponse("redirect:/admin/webim/index.html"));
    }
    
    @RequestMapping("/profile")
    @Menu(type = "admin" , subtype = "profile" , admin= true)
    public ModelAndView profile(ModelMap map , HttpServletRequest request) {
    	List<CousultInvite> settingList = invite.findByOrgi(super.getUser(request).getOrgi()) ;
    	if(settingList.size() > 0){
    		map.addAttribute("inviteData", settingList.get(0));
    	}
    	map.addAttribute("import", request.getServerPort()) ;
        return request(super.createAdminTempletResponse("/admin/webim/profile"));
    }
    
    @RequestMapping("/profile/save")
    @Menu(type = "admin" , subtype = "profile" , admin= true)
    public ModelAndView saveprofile(HttpServletRequest request , @Valid CousultInvite inviteData, @RequestParam(value = "dialogad", required = false) MultipartFile dialogad) throws IOException {
    	CousultInvite tempInviteData  ;
    	if(inviteData!=null && !StringUtils.isBlank(inviteData.getId())){
    		tempInviteData = invite.findOne(inviteData.getId()) ;
    		if(tempInviteData!=null){
    			tempInviteData.setDialog_name(inviteData.getDialog_name());
    			tempInviteData.setDialog_address(inviteData.getDialog_address());
    			tempInviteData.setDialog_phone(inviteData.getDialog_phone());
    			tempInviteData.setDialog_mail(inviteData.getDialog_mail());
    			tempInviteData.setDialog_introduction(inviteData.getDialog_introduction());
    			tempInviteData.setDialog_message(inviteData.getDialog_message());
    			
    			if(dialogad!=null && !StringUtils.isBlank(dialogad.getName()) && dialogad.getBytes()!=null && dialogad.getBytes().length >0){
	    			String fileName = "ad/"+inviteData.getId()+dialogad.getOriginalFilename().substring(dialogad.getOriginalFilename().lastIndexOf(".")) ;
	    			File file = new File(path , fileName) ;
	    			if(!file.getParentFile().exists()){
	    				file.getParentFile().mkdirs();
	    			}
	        		FileCopyUtils.copy(dialogad.getBytes(), file);
	        		tempInviteData.setDialog_ad(fileName);
    			}
        		invite.save(tempInviteData) ;
    		}
    	}else{
    		invite.save(inviteData) ;
    	}
        return request(super.createRequestPageTempletResponse("redirect:/admin/webim/profile.html"));
    }
    
    @RequestMapping("/invote")
    @Menu(type = "admin" , subtype = "invote" , admin= true)
    public ModelAndView invote(ModelMap map , HttpServletRequest request) {
    	List<CousultInvite> settingList = invite.findByOrgi(super.getUser(request).getOrgi()) ;
    	if(settingList.size() > 0){
    		map.addAttribute("inviteData", settingList.get(0));
    	}
    	map.addAttribute("import", request.getServerPort()) ;
        return request(super.createAdminTempletResponse("/admin/webim/invote"));
    }
    
    @RequestMapping("/invote/save")
    @Menu(type = "admin" , subtype = "profile" , admin= true)
    public ModelAndView saveinvote(HttpServletRequest request , @Valid CousultInvite inviteData, @RequestParam(value = "invotebg", required = false) MultipartFile invotebg) throws IOException {
    	CousultInvite tempInviteData  ;
    	if(inviteData!=null && !StringUtils.isBlank(inviteData.getId())){
    		tempInviteData = invite.findOne(inviteData.getId()) ;
    		if(tempInviteData!=null){
    			tempInviteData.setConsult_invite_enable(inviteData.isConsult_invite_enable());
    			tempInviteData.setConsult_invite_content(inviteData.getConsult_invite_content());
    			tempInviteData.setConsult_invite_accept(inviteData.getConsult_invite_accept());
    			tempInviteData.setConsult_invite_later(inviteData.getConsult_invite_later());
    			tempInviteData.setConsult_invite_delay(inviteData.getConsult_invite_delay());
    			
    			tempInviteData.setConsult_invite_color(inviteData.getConsult_invite_color());
    			
    			if(invotebg!=null && !StringUtils.isBlank(invotebg.getName()) && invotebg.getBytes()!=null && invotebg.getBytes().length >0){
	    			String fileName = "invote/"+inviteData.getId()+invotebg.getOriginalFilename().substring(invotebg.getOriginalFilename().lastIndexOf(".")) ;
	    			File file = new File(path , fileName) ;
	    			if(!file.getParentFile().exists()){
	    				file.getParentFile().mkdirs();
	    			}
	        		FileCopyUtils.copy(invotebg.getBytes(), file);
	        		tempInviteData.setConsult_invite_bg(fileName);
    			}
        		invite.save(tempInviteData) ;
    		}
    	}else{
    		invite.save(inviteData) ;
    	}
        return request(super.createRequestPageTempletResponse("redirect:/admin/webim/invote.html"));
    }
}