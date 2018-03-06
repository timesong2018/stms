package com.ukefu.webim.util.server.handler;

import java.util.List;

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
import com.ukefu.webim.service.repository.ChatMessageRepository;
import com.ukefu.webim.service.repository.IMGroupUserRepository;
import com.ukefu.webim.service.repository.RecentUserRepository;
import com.ukefu.webim.util.server.message.ChatMessage;
import com.ukefu.webim.web.model.IMGroupUser;
import com.ukefu.webim.web.model.MessageOutContent;
import com.ukefu.webim.web.model.RecentUser;
import com.ukefu.webim.web.model.User;

public class EntIMEventHandler     
{  
	protected SocketIOServer server;
	
    @Autowired  
    public EntIMEventHandler(SocketIOServer server)   
    {  
        this.server = server ;
    }  
    
    @OnConnect  
    public void onConnect(SocketIOClient client)  
    {  
    	try {
			String user = client.getHandshakeData().getSingleUrlParam("userid") ;
			String name = client.getHandshakeData().getSingleUrlParam("name") ;
			String group = client.getHandshakeData().getSingleUrlParam("group") ;
			if(!StringUtils.isBlank(group)){
				client.joinRoom(group);
			}else{
				if(NettyClients.getInstance().getEntIMClientsNum(user) == 0){
					MessageOutContent outMessage = new MessageOutContent() ;
			    	outMessage.setMessage("online");
			    	outMessage.setNickName(name);
			    	outMessage.setContextid(user);
			    	outMessage.setMessageType(UKDataContext.MessageTypeEnum.MESSAGE.toString());
			    	outMessage.setCalltype(UKDataContext.CallTypeEnum.IN.toString());
					client.getNamespace().getBroadcastOperations().sendEvent("status", outMessage); //广播所有人，用户上线
				}
				if(!StringUtils.isBlank(user)){
					NettyClients.getInstance().putEntIMEventClient(user, client);
				}
				IMGroupUserRepository imGroupUserRes = UKDataContext.getContext().getBean(IMGroupUserRepository.class) ;
				User imUser = new User(user);
				List<IMGroupUser> imGroupUserList = imGroupUserRes.findByUser(imUser) ;
				for(IMGroupUser imGroupUser : imGroupUserList){
					if(imGroupUser.getImgroup()!=null){
						client.joinRoom(imGroupUser.getImgroup().getId());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }  
      
    @OnDisconnect  
    public void onDisconnect(SocketIOClient client)  
    {  
    	try {
			String user = client.getHandshakeData().getSingleUrlParam("userid") ;
			String name = client.getHandshakeData().getSingleUrlParam("name") ;
			String group = client.getHandshakeData().getSingleUrlParam("group") ;
			if(!StringUtils.isBlank(group)){
				client.leaveRoom(group);
			}else{
				if(!StringUtils.isBlank(user)){
					NettyClients.getInstance().removeEntIMEventClient(user, client.getSessionId().toString());
				}
				if(NettyClients.getInstance().getEntIMClientsNum(user) == 0){
					MessageOutContent outMessage = new MessageOutContent() ;
			    	outMessage.setMessage("offline");
			    	outMessage.setNickName(name);
			    	outMessage.setContextid(user);
			    	outMessage.setMessageType(UKDataContext.MessageTypeEnum.MESSAGE.toString());
			    	outMessage.setCalltype(UKDataContext.CallTypeEnum.IN.toString());
					client.getNamespace().getBroadcastOperations().sendEvent("status", outMessage); //广播所有人，用户上线
				}
				
				IMGroupUserRepository imGroupUserRes = UKDataContext.getContext().getBean(IMGroupUserRepository.class) ;
				User imUser = new User(user);
				List<IMGroupUser> imGroupUserList = imGroupUserRes.findByUser(imUser) ;
				for(IMGroupUser imGroupUser : imGroupUserList){
					if(imGroupUser.getImgroup()!=null){
						client.leaveRoom(imGroupUser.getImgroup().getId());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }  
    
    @OnEvent(value = "message")  
    public void onEvent(SocketIOClient client, AckRequest request, ChatMessage data)   
    {
    	if(data.getType() == null){
    		data.setType("message");
    	}
    	String user = client.getHandshakeData().getSingleUrlParam("userid") ;
//		String name = client.getHandshakeData().getSingleUrlParam("name") ;
		String group = client.getHandshakeData().getSingleUrlParam("group") ;
		
    	data.setUserid(user);
//		data.setUsername(name);
		data.setId(UKTools.getUUID());
		data.setSession(user);	
		data.setCalltype(UKDataContext.CallTypeEnum.OUT.toString());
		
		
		if(UKDataContext.getContext()!=null){
			ChatMessageRepository chatMessageRes = UKDataContext.getContext().getBean(ChatMessageRepository.class) ;
			RecentUserRepository recentUserRes = UKDataContext.getContext().getBean(RecentUserRepository.class) ;
			
			if(!StringUtils.isBlank(group)){	//如果是群聊
				data.setContextid(group);
				chatMessageRes.save(data) ;
				client.getNamespace().getRoomOperations(group).sendEvent(UKDataContext.MessageTypeEnum.MESSAGE.toString(), data);
			}else{	//单聊
				data.setContextid(data.getTouser());
				chatMessageRes.save(data) ;
				NettyClients.getInstance().sendEntIMEventMessage(data.getUserid(), UKDataContext.MessageTypeEnum.MESSAGE.toString(), data);	//同时将消息发送给自己
				data.setCalltype(UKDataContext.CallTypeEnum.IN.toString());
				data.setContextid(user);
				data.setUserid(data.getTouser());
				data.setId(null);
				chatMessageRes.save(data) ; 	//每条消息存放两条，一个是我的对话记录 ， 另一条是对方的对话历史， 情况当前聊天记录的时候，只清理自己的
				NettyClients.getInstance().sendEntIMEventMessage(data.getTouser(), UKDataContext.MessageTypeEnum.MESSAGE.toString(), data);	//发送消息给目标用户
				
				RecentUser recentUser = recentUserRes.findByCreaterAndUser(data.getTouser() , new User(user)) ;
				if(recentUser!=null){
					recentUser.setNewmsg(recentUser.getNewmsg()+1);
					if(data.getMessage()!=null && data.getMessage().length()>50){
						recentUser.setLastmsg(data.getMessage().substring(0 , 50));
					}else{
						recentUser.setLastmsg(data.getMessage());
					}
					recentUserRes.save(recentUser) ;
				}
			}
		}
    } 
}  