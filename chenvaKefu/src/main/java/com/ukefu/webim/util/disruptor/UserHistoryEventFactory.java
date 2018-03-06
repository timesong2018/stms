package com.ukefu.webim.util.disruptor;

import com.lmax.disruptor.EventFactory;
import com.ukefu.util.event.UserHistoryEvent;

public class UserHistoryEventFactory implements EventFactory<UserHistoryEvent>{

	@Override
	public UserHistoryEvent newInstance() {
		return new UserHistoryEvent();
	}
}
