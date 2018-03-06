package com.ukefu.webim.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;

import com.ukefu.webim.web.model.PropertiesEvent;

public class PropertiesEventUtils {
	public static List<PropertiesEvent> processPropertiesModify(Object newobj , Object oldobj,String... ignoreProperties){
		List<PropertiesEvent> events = new ArrayList<PropertiesEvent>() ;
		//
		String[] fields = new String[]{"id" , "orgi" , "creater" ,"createtime" , "updatetime"} ; 
		List<String> ignoreList = new ArrayList<String>(); 
		ignoreList.addAll(Arrays.asList(fields)) ;
		if((ignoreProperties != null)){
			ignoreList.addAll(Arrays.asList(ignoreProperties));
		}
		PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(newobj.getClass());
		for (PropertyDescriptor targetPd : targetPds) {  
	        Method newReadMethod = targetPd.getReadMethod();  
	        if (oldobj!=null && newReadMethod != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {  
	            PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(oldobj.getClass(), targetPd.getName());  
	            if (sourcePd != null && !targetPd.getName().equalsIgnoreCase("id")) {  
	                Method readMethod = sourcePd.getReadMethod();  
	                if (readMethod != null) {  
	                    try {  
	                    	Object newValue = readMethod.invoke(newobj);
	                        Object oldValue = readMethod.invoke(oldobj);
	                        
	                        if(newValue != null && !newValue.equals(oldValue)){
	                        	PropertiesEvent event = new PropertiesEvent();
	                        	event.setField(targetPd.getName());
	                        	event.setCreatetime(new Date());
	                        	event.setName(targetPd.getName());
	                        	event.setPropertity(targetPd.getName());
	                        	event.setOldvalue(oldValue!=null && oldValue.toString().length()<100 ? oldValue.toString() : null);
	                        	event.setNewvalue(newValue!=null && newValue.toString().length()<100 ? newValue.toString() : null);
	                        	events.add(event) ;
	                        }
	                    }  
	                    catch (Throwable ex) {  
	                        throw new FatalBeanException(  
	                                "Could not copy property '" + targetPd.getName() + "' from source to target", ex);  
	                    }  
	                }  
	            }  
	        }  
	    }  
		return events ;
	}
}
