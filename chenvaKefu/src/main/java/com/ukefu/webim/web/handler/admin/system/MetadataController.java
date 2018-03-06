package com.ukefu.webim.web.handler.admin.system;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ukefu.util.Menu;
import com.ukefu.util.UKTools;
import com.ukefu.util.metadata.DatabaseMetaDataHandler;
import com.ukefu.util.metadata.UKColumnMetadata;
import com.ukefu.util.metadata.UKTableMetaData;
import com.ukefu.webim.service.repository.MetadataRepository;
import com.ukefu.webim.service.repository.SysDicRepository;
import com.ukefu.webim.service.repository.TablePropertiesRepository;
import com.ukefu.webim.web.handler.Handler;
import com.ukefu.webim.web.model.MetadataTable;
import com.ukefu.webim.web.model.TableProperties;

@Controller
@RequestMapping("/admin/metadata")
public class MetadataController extends Handler{
	
	@Autowired
	private MetadataRepository metadataRes ;
	
	@Autowired
	private SysDicRepository sysDicRes ;
	
	@Autowired
	private TablePropertiesRepository tablePropertiesRes ;
	
	@Autowired
	@PersistenceContext
	private EntityManager em;

    @RequestMapping("/index")
    @Menu(type = "admin" , subtype = "metadata" , admin = true)
    public ModelAndView index(ModelMap map , HttpServletRequest request) throws SQLException {
    	map.addAttribute("metadataList", metadataRes.findAll(new PageRequest(super.getP(request), super.getPs(request)))) ;
        return request(super.createAdminTempletResponse("/admin/system/metadata/index"));
    }
    
    @RequestMapping("/edit")
    @Menu(type = "admin" , subtype = "metadata" , admin = true)
    public ModelAndView edit(ModelMap map , HttpServletRequest request , @Valid String id) {
    	map.addAttribute("metadata", metadataRes.findById(id)) ;
    	return request(super.createRequestPageTempletResponse("/admin/system/metadata/edit"));
    }
    
    @RequestMapping("/update")
    @Menu(type = "admin" , subtype = "metadata" , admin = true)
    public ModelAndView update(ModelMap map , HttpServletRequest request , @Valid MetadataTable metadata) throws SQLException {
    	MetadataTable table = metadataRes.findById(metadata.getId()) ;
    	table.setName(metadata.getName());
    	metadataRes.save(table);
    	return request(super.createRequestPageTempletResponse("redirect:/admin/metadata/index.html"));
    }
    
    @RequestMapping("/properties/edit")
    @Menu(type = "admin" , subtype = "metadata" , admin = true)
    public ModelAndView propertiesedit(ModelMap map , HttpServletRequest request , @Valid String id) {
    	map.addAttribute("tp", tablePropertiesRes.findById(id)) ;
    	map.addAttribute("sysdicList", sysDicRes.findByParentid("0")) ;
    	return request(super.createRequestPageTempletResponse("/admin/system/metadata/tpedit"));
    }
    
    @RequestMapping("/properties/update")
    @Menu(type = "admin" , subtype = "metadata" , admin = true)
    public ModelAndView propertiesupdate(ModelMap map , HttpServletRequest request , @Valid TableProperties tp) throws SQLException {
    	TableProperties tableProperties = tablePropertiesRes.findById(tp.getId()) ;
    	tableProperties.setName(tp.getName());
    	tableProperties.setSeldata(tp.isSeldata());
    	tableProperties.setSeldatacode(tp.getSeldatacode());
    	tableProperties.setImpfield(tp.isImpfield());
    	
    	tablePropertiesRes.save(tableProperties);
    	return request(super.createRequestPageTempletResponse("redirect:/admin/metadata/table.html?id="+tableProperties.getDbtableid()));
    }
    
    @RequestMapping("/delete")
    @Menu(type = "admin" , subtype = "metadata" , admin = true)
    public ModelAndView delete(ModelMap map , HttpServletRequest request , @Valid String id) throws SQLException {
    	MetadataTable table = metadataRes.findById(id) ;
    	metadataRes.delete(table);
    	return request(super.createRequestPageTempletResponse("redirect:/admin/metadata/index.html"));
    }
    
    @RequestMapping("/batdelete")
    @Menu(type = "admin" , subtype = "metadata" , admin = true)
    public ModelAndView batdelete(ModelMap map , HttpServletRequest request , @Valid String[] ids) throws SQLException {
    	if(ids!=null && ids.length>0){
    		metadataRes.delete(metadataRes.findAll(Arrays.asList(ids)) );
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/metadata/index.html"));
    }
    
    @RequestMapping("/properties/delete")
    @Menu(type = "admin" , subtype = "metadata" , admin = true)
    public ModelAndView propertiesdelete(ModelMap map , HttpServletRequest request , @Valid String id , @Valid String tbid) throws SQLException {
    	TableProperties prop = tablePropertiesRes.findById(id) ;
    	tablePropertiesRes.delete(prop);
        return request(super.createRequestPageTempletResponse("redirect:/admin/metadata/table.html?id="+ (!StringUtils.isBlank(tbid) ? tbid : prop.getDbtableid())));
    }
    
    @RequestMapping("/properties/batdelete")
    @Menu(type = "admin" , subtype = "metadata" , admin = true)
    public ModelAndView propertiesbatdelete(ModelMap map , HttpServletRequest request , @Valid String[] ids, @Valid String tbid) throws SQLException {
    	if(ids!=null && ids.length>0){
    		tablePropertiesRes.delete(tablePropertiesRes.findAll(Arrays.asList(ids)) );
    	}
        return request(super.createRequestPageTempletResponse("redirect:/admin/metadata/table.html?id="+ tbid));
    }
    
    @RequestMapping("/table")
    @Menu(type = "admin" , subtype = "metadata" , admin = true)
    public ModelAndView table(ModelMap map , HttpServletRequest request , @Valid String id) throws SQLException {
    	map.addAttribute("propertiesList", tablePropertiesRes.findByDbtableid(id)) ;
    	map.addAttribute("tbid", id) ;
        return request(super.createAdminTempletResponse("/admin/system/metadata/table"));
    }
    
    @RequestMapping("/imptb")
    @Menu(type = "admin" , subtype = "metadata" , admin = true)
    public ModelAndView imptb(ModelMap map , HttpServletRequest request) throws Exception {
    	
    	Session session = (Session) em.getDelegate();
    	SessionFactory sessionFactory = session.getSessionFactory();
    	Connection connection = SessionFactoryUtils.getDataSource(sessionFactory).getConnection();
    	map.addAttribute("tablesList", DatabaseMetaDataHandler.getTables(connection) );
    	
        return request(super.createRequestPageTempletResponse("/admin/system/metadata/imptb"));
    }
    
    @RequestMapping("/imptbsave")
    @Menu(type = "admin" , subtype = "metadata" , admin = true)
    public ModelAndView imptb(ModelMap map , HttpServletRequest request , @Valid String[] tables) throws Exception {
    	if(tables!=null && tables.length > 0){
	    	Session session = (Session) em.getDelegate();
	    	SessionFactory sessionFactory = session.getSessionFactory();
	    	Connection connection = SessionFactoryUtils.getDataSource(sessionFactory).getConnection();
	    	try{
		    	for(String table : tables){
		    		int count = metadataRes.countByTablename(table) ;
		    		if(count == 0){
	 		    		MetadataTable metaDataTable = new MetadataTable();
		  				//当前记录没有被添加过，进行正常添加
		  				metaDataTable.setTablename(table);
		  				metaDataTable.setOrgi(super.getOrgi(request));
		  				metaDataTable.setId(UKTools.md5(metaDataTable.getTablename()));
		  				metaDataTable.setTabledirid("0");
		  				metaDataTable.setCreater(super.getUser(request).getId());
		  				metaDataTable.setCreatername(super.getUser(request).getUsername());
		  				metaDataTable.setName(table);
		  				metaDataTable.setUpdatetime(new Date());
		  				metaDataTable.setCreatetime(new Date());
		  				metadataRes.save(processMetadataTable( DatabaseMetaDataHandler.getTable(connection, metaDataTable.getTablename()) , metaDataTable));
		    		}
		    	}
	    	}catch(Exception ex){
	    		ex.printStackTrace();
	    	}finally{
	    		connection.close();
	    	}
    	}
    	
        return request(super.createRequestPageTempletResponse("redirect:/admin/metadata/index.html"));
    }
    
    private MetadataTable processMetadataTable(UKTableMetaData metaData , MetadataTable table){
    	table.setTableproperty(new ArrayList<TableProperties>()); 
    	for(UKColumnMetadata colum : metaData.getColumnMetadatas()){
    		TableProperties tablePorperties = new TableProperties(colum.getName() , colum.getTypeName() , colum.getColumnSize() , metaData.getName()) ;
			tablePorperties.setOrgi(table.getOrgi()) ;
			
			tablePorperties.setDatatypecode(0);
			tablePorperties.setLength(colum.getColumnSize());
			tablePorperties.setDatatypename(getDataTypeName(colum.getTypeName()));
			tablePorperties.setName(colum.getTitle());
			if(tablePorperties.getFieldname().equals("create_time") || tablePorperties.getFieldname().equals("createtime") || tablePorperties.getFieldname().equals("update_time")){
				tablePorperties.setDatatypename(getDataTypeName("datetime"));
			}
			if(colum.getName().startsWith("field")){
				tablePorperties.setFieldstatus(false);
			}else{
				tablePorperties.setFieldstatus(true);
			}
			table.getTableproperty().add(tablePorperties) ;
		}
    	return table ;
    }
    
    public String getDataTypeName(String type){
    	String typeName = "text" ;
    	if(type.indexOf("varchar")>=0){
    		typeName = "text" ;
    	}else if(type.equalsIgnoreCase("date") || type.equalsIgnoreCase("datetime")){
    		typeName = type.toLowerCase() ;
    	}else if(type.equalsIgnoreCase("int") || type.equalsIgnoreCase("float")  || type.equalsIgnoreCase("number")){
    		typeName = "number" ;
    	}
    	return typeName ;
    }
    
}