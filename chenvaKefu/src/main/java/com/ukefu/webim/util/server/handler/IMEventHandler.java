package com.ukefu.webim.util.server.handler;

import java.net.InetSocketAddress;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.ukefu.core.UKDataContext;
import com.ukefu.util.UKTools;
import com.ukefu.util.client.NettyClients;
import com.ukefu.webim.service.acd.ServiceQuene;
import com.ukefu.webim.service.cache.CacheHelper;
import com.ukefu.webim.util.MessageUtils;
import com.ukefu.webim.util.OnlineUserUtils;
import com.ukefu.webim.util.server.message.AgentStatusMessage;
import com.ukefu.webim.util.server.message.ChatMessage;
import com.ukefu.webim.util.server.message.NewRequestMessage;
import com.ukefu.webim.web.model.AgentUser;
import com.ukefu.webim.web.model.MessageOutContent;

public class IMEventHandler     
{  
	protected SocketIOServer server;
	
    @Autowired  
    public IMEventHandler(SocketIOServer server)   
    {  
        this.server = server ;
    }  
    
    @OnConnect  
    public void onConnect(SocketIOClient client)  
    {  
    	try {
			String user = client.getHandshakeData().getSingleUrlParam("userid") ;
			String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
			String session = client.getHandshakeData().getSingleUrlParam("session") ;
			String appid = client.getHandshakeData().getSingleUrlParam("appid") ;
			
			if(!StringUtils.isBlank(user)){
				/**
				 * 用户进入到对话连接 ， 排队用户请求 , 如果返回失败，表示当前坐席全忙，用户进入排队状态，当前提示信息 显示 当前排队的队列位置，不可进行对话，用户发送的消息作为留言处理
				 */
				InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress()  ;
				NewRequestMessage newRequestMessage = OnlineUserUtils.newRequestMessage(user, orgi , session , appid , address.getHostString() , client.getHandshakeData().getSingleUrlParam("osname") , client.getHandshakeData().getSingleUrlParam("browser") , UKDataContext.ChannelTypeEnum.WEBIM.toString()) ;
//				/**
//				 * 加入到 缓存列表
//				 */
				NettyClients.getInstance().putIMEventClient(user, client);
//				
				if(newRequestMessage!=null && !StringUtils.isBlank(newRequestMessage.getMessage())){
					MessageOutContent outMessage = new MessageOutContent() ;
			    	outMessage.setMessage(newRequestMessage.getMessage());
			    	outMessage.setMessageType(UKDataContext.MessageTypeEnum.MESSAGE.toString());
			    	outMessage.setCalltype(UKDataContext.CallTypeEnum.IN.toString());
			    	outMessage.setNickName(newRequestMessage.getUsername());
					outMessage.setCreatetime(UKTools.dateFormate.format(new Date()));
					
					client.sendEvent(UKDataContext.MessageTypeEnum.STATUS.toString(), outMessage);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }  
      
    //添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息  
    @OnDisconnect  
    public void onDisconnect(SocketIOClient client)  
    {  
    	String user = client.getHandshakeData().getSingleUrlParam("userid") ;
		String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
		if(user!=null){
			try {
				/**
				 * 用户主动断开服务
				 */
				ServiceQuene.serviceFinish((AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(user, UKDataContext.SYSTEM_ORGI), orgi); 
			} catch (Exception e) {
				e.printStackTrace();
			}
			NettyClients.getInstance().removeIMEventClient(user , client.getSessionId().toString());
		}
    }  
      
    //消息接收入口，网站有新用户接入对话  
    @OnEvent(value = "new")  
    public void onEvent(SocketIOClient client, AckRequest request, NewRequestMessage data)   
    {
    	
    }  
    
  //消息接收入口，坐席状态更新  
    @OnEvent(value = "agentstatus")  
    public void onEvent(SocketIOClient client, AckRequest request, AgentStatusMessage data)   
    {
    	System.out.println(data.getMessage());
    } 
    
    //消息接收入口，收发消息，用户向坐席发送消息和 坐席向用户发送消息  
    @OnEvent(value = "message")  
    public void onEvent(SocketIOClient client, AckRequest request, ChatMessage data)   
    {
    	if(data.getType() == null){
    		data.setType("message");
    	}
    	if(!StringUtils.isBlank(data.getMessage()) && data.getMessage().length() > 300){
    		data.setMessage(data.getMessage().substring(0 , 300));
    	}
    	MessageUtils.createMessage(data , UKDataContext.MediaTypeEnum.TEXT.toString(), data.getUserid());
    } 
}  