package com.ukefu.webim.web.handler.admin.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.corundumstudio.socketio.SocketIOServer;
import com.ukefu.core.UKDataContext;
import com.ukefu.util.Menu;
import com.ukefu.util.UKTools;
import com.ukefu.webim.service.cache.CacheHelper;
import com.ukefu.webim.service.repository.SystemConfigRepository;
import com.ukefu.webim.web.handler.Handler;
import com.ukefu.webim.web.model.SystemConfig;

@Controller
@RequestMapping("/admin/config")
public class SystemConfigController extends Handler{
	
	@Value("${uk.im.server.port}")  
    private Integer port;

	@Value("${web.upload-path}")
    private String path;
	
	@Autowired
	private SocketIOServer server ;
	
	@Autowired
	private SystemConfigRepository systemConfigRes ;
	
    @RequestMapping("/index")
    @Menu(type = "admin" , subtype = "config" , admin = true)
    public ModelAndView index(ModelMap map , HttpServletRequest request) throws SQLException {
    	map.addAttribute("server", server) ;
    	map.addAttribute("imServerStatus", UKDataContext.getIMServerStatus()) ;
        return request(super.createAdminTempletResponse("/admin/config/index"));
    }
    
    @RequestMapping("/stopimserver")
    @Menu(type = "admin" , subtype = "stopimserver" , access = false , admin = true)
    public ModelAndView stopimserver(ModelMap map , HttpServletRequest request) throws SQLException {
    	server.stop();
    	UKDataContext.setIMServerStatus(false);
        return request(super.createRequestPageTempletResponse("redirect:/admin/config/index.html"));
    }
    
    /**
     * 危险操作，请谨慎调用 ， WebLogic/WebSphere/Oracle等中间件服务器禁止调用
     * @param map
     * @param request
     * @return
     * @throws SQLException
     */
    @RequestMapping("/stop")
    @Menu(type = "admin" , subtype = "stop" , access = false , admin = true)
    public ModelAndView stop(ModelMap map , HttpServletRequest request) throws SQLException {
    	/**
    	 * 先调用 IMServer 
    	 */
    	server.stop();
    	UKDataContext.setIMServerStatus(false);
    	System.exit(0);
        return request(super.createRequestPageTempletResponse("redirect:/admin/config/index.html"));
    }
    
    
    @RequestMapping("/save")
    @Menu(type = "admin" , subtype = "save" , admin = true)
    public ModelAndView save(ModelMap map , HttpServletRequest request , @Valid SystemConfig config , @RequestParam(value = "keyfile", required = false) MultipartFile keyfile) throws SQLException, IOException, NoSuchAlgorithmException {
    	SystemConfig systemConfig = systemConfigRes.findByOrgi(super.getOrgi(request)) ;
    	config.setOrgi(super.getOrgi(request));
    	if(StringUtils.isBlank(config.getJkspassword())){
    		config.setJkspassword(null);
    	}
    	if(systemConfig == null){
    		config.setCreater(super.getUser(request).getId());
    		config.setCreatetime(new Date());
    		systemConfig = config ;
    	}else{
    		UKTools.copyProperties(config,systemConfig);
    	}
    	if(config.isEnablessl()){
	    	if(keyfile!=null && keyfile.getBytes()!=null && keyfile.getBytes().length > 0 && keyfile.getOriginalFilename()!=null && keyfile.getOriginalFilename().length() > 0){
		    	FileUtils.writeByteArrayToFile(new File(path , "ssl/"+keyfile.getOriginalFilename()), keyfile.getBytes());
		    	systemConfig.setJksfile(keyfile.getOriginalFilename());
	    	}
	    	Properties prop = new Properties();     
	    	FileOutputStream oFile = new FileOutputStream(new File(path , "ssl/https.properties"));//true表示追加打开
	    	prop.setProperty("key-store-password", UKTools.encryption(systemConfig.getJkspassword())) ;
	    	prop.setProperty("key-store",systemConfig.getJksfile()) ;
	    	prop.store(oFile , "SSL Properties File");
	    	oFile.close();
    	}else if(new File(path , "ssl").exists()){
    		File[] sslFiles = new File(path , "ssl").listFiles() ;
    		for(File sslFile : sslFiles){
    			sslFile.delete();
    		}
    	}
    	systemConfigRes.save(systemConfig) ;
    	
    	CacheHelper.getSystemCacheBean().put("systemConfig", systemConfig , super.getOrgi(request));
        return request(super.createAdminTempletResponse("/admin/config/index"));
    }
}