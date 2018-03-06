package com.ukefu.webim.util.server.message;

import com.ukefu.util.UKTools;

public class NewRequestMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3520656734252136303L;
	private String id = UKTools.getUUID();
	private String type ;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
