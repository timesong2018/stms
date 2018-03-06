package com.ukefu.webim.util.disruptor;

import com.lmax.disruptor.EventHandler;
import com.ukefu.core.UKDataContext;
import com.ukefu.util.event.UserHistoryEvent;
import com.ukefu.webim.service.repository.UserEventRepository;
import com.ukefu.webim.web.model.UserHistory;

public class UserEventHandler implements EventHandler<UserHistoryEvent>{

	@Override
	public void onEvent(UserHistoryEvent arg0, long arg1, boolean arg2)
			throws Exception {
		UserEventRepository userEventRes = UKDataContext.getContext().getBean(UserEventRepository.class) ;
		userEventRes.save((UserHistory)arg0.getEvent()) ;
	}

}
