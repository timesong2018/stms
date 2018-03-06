package com.ukefu.util.business;

import java.util.Date;

import com.ukefu.core.UKDataContext;
import com.ukefu.webim.service.cache.CacheHelper;
import com.ukefu.webim.service.repository.GenerationRepository;
import com.ukefu.webim.web.model.Generation;

public class WorkOrdersUtils {
	
	/**
	 * 加了同步功能 ， 写入数据库仅仅是为了保存，下次重启的时候 能够读取到正确的  开始位置 ， 写数据库的功能可以 放到异步执行中去
	 * @param orgi
	 * @return
	 */
	public synchronized static int getWorkOrderNumber(String orgi){
		GenerationRepository generationRes = UKDataContext.getContext().getBean(GenerationRepository.class) ;
		Generation generation = (Generation) CacheHelper.getSystemCacheBean().getCacheObject(UKDataContext.TagType.WORKORDERS.toString(), orgi) ;
		long orderNumber = CacheHelper.getSystemCacheBean().getAtomicLong(UKDataContext.TagType.WORKORDERS.toString()) ;
		if(generation == null){
			generation = generationRes.findByOrgiAndModel(orgi, UKDataContext.TagType.WORKORDERS.toString()) ;
			if(generation == null){
				generation = new Generation() ;
				generation.setCreatetime(new Date());
				generation.setOrgi(orgi);
			}
		}
		generation.setModel(UKDataContext.TagType.WORKORDERS.toString());
		generation.setStartinx((int) orderNumber);
		generationRes.save(generation) ;
		CacheHelper.getSystemCacheBean().put(UKDataContext.TagType.WORKORDERS.toString(), generation, orgi);
		
		return (int) orderNumber ;
	}
}
