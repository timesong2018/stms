package com.ukefu.webim.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ukefu.core.UKDataContext;
import com.ukefu.util.BrowserClient;
import com.ukefu.util.CheckMobile;
import com.ukefu.util.IP;
import com.ukefu.util.IPTools;
import com.ukefu.util.UKTools;
import com.ukefu.util.webim.WebIMClient;
import com.ukefu.webim.service.impl.AgentUserService;
import com.ukefu.webim.service.repository.OnlineUserHisRepository;
import com.ukefu.webim.service.repository.OnlineUserRepository;
import com.ukefu.webim.util.router.RouterHelper;
import com.ukefu.webim.util.server.message.NewRequestMessage;
import com.ukefu.webim.web.model.AgentUser;
import com.ukefu.webim.web.model.MessageDataBean;
import com.ukefu.webim.web.model.MessageInContent;
import com.ukefu.webim.web.model.OnlineUser;
import com.ukefu.webim.web.model.OnlineUserHis;
import com.ukefu.webim.web.model.User;

public class OnlineUserUtils {
	public static WebSseEmitterClient webIMClients = new WebSseEmitterClient();
	/**
	 * 
	 * @param user
	 * @param orgi
	 * @param id
	 * @param service
	 * @return
	 * @throws Exception
	 */
	public static OnlineUser user(com.ukefu.webim.web.model.User user,
			String orgi, String id, OnlineUserRepository service)
			throws Exception {
		return service.findByUserid(id);
	}

	/**
	 * 
	 * @param user
	 * @param orgi
	 * @param optype
	 * @param request
	 * @param service
	 * @throws Exception
	 */
	public static OnlineUser online(User user, String orgi, String sessionid,String optype, HttpServletRequest request , String channel , String appid) {
		OnlineUser onlineUser = null;
		if (UKDataContext.getContext() != null) {
			OnlineUserRepository service = (OnlineUserRepository) UKDataContext
					.getContext().getBean(OnlineUserRepository.class);

			OnlineUser tempOnlineUser = service.findByUserid(user.getId());
			if (tempOnlineUser == null) {
				onlineUser = new OnlineUser();
				onlineUser.setId(user.getId());
				onlineUser.setCreater(user.getId());
				onlineUser.setUsername(user.getUsername());
				onlineUser.setCreatetime(new Date());
				onlineUser.setUpdatetime(new Date());
				onlineUser.setUpdateuser(user.getUsername());
				onlineUser.setSessionid(sessionid);

				onlineUser.setOrgi(orgi);
				onlineUser.setChannel(channel);
				
				String cookie = getCookie(request, "R3GUESTUSEKEY");
				if ((StringUtils.isBlank(cookie))
						|| (user.getSessionid().equals(cookie))) {
					onlineUser.setOlduser("0");
				} else {
					onlineUser.setOlduser("1");
				}
				onlineUser.setMobile(CheckMobile.check(request
						.getHeader("User-Agent")) ? "1" : "0");

				// onlineUser.setSource(user.getId());

				String url = request.getHeader("Referer");
				onlineUser.setUrl(url);
				if (!StringUtils.isBlank(url)) {
					try {
						URL referer = new URL(url);
						onlineUser.setSource(referer.getHost());
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				onlineUser.setAppid(appid);
				onlineUser.setUserid(user.getId());
				onlineUser.setUsername(user.getUsername());

				onlineUser.setLogintime(new Date());
				onlineUser.setIp(request.getRemoteAddr());

				IP ipdata = IPTools.getInstance().findGeography(
						request.getRemoteAddr());
				onlineUser.setCountry(ipdata.getCountry());
				onlineUser.setProvince(ipdata.getProvince());
				onlineUser.setCity(ipdata.getCity());
				onlineUser.setIsp(ipdata.getIsp());
				onlineUser.setRegion(ipdata.toString() + "（"
						+ request.getRemoteAddr() + "）");

				onlineUser.setDatestr(new SimpleDateFormat("yyyMMdd")
						.format(new Date()));

				onlineUser.setHostname(request.getRemoteHost());
				onlineUser.setSessionid(sessionid);
				onlineUser.setOptype(optype);
				onlineUser
						.setStatus(UKDataContext.OnlineUserOperatorStatus.ONLINE
								.toString());
				BrowserClient client = UKTools.parseClient(request);
				onlineUser.setOpersystem(client.getOs());
				onlineUser.setBrowser(client.getBrowser());
				onlineUser.setUseragent(client.getUseragent());

				service.save(onlineUser);
			}else{
				onlineUser = tempOnlineUser ;
				if((!StringUtils.isBlank(onlineUser.getSessionid()) && !onlineUser.getSessionid().equals(sessionid)) || !UKDataContext.OnlineUserOperatorStatus.ONLINE.toString().equals(onlineUser.getStatus())){
					onlineUser.setStatus(UKDataContext.OnlineUserOperatorStatus.ONLINE.toString());
					onlineUser.setChannel(channel);
					onlineUser.setAppid(appid);
					onlineUser.setUpdatetime(new Date());
					if(!StringUtils.isBlank(onlineUser.getSessionid()) && !onlineUser.getSessionid().equals(sessionid)){
						onlineUser.setInvitestatus(UKDataContext.OnlineUserInviteStatus.DEFAULT.toString());
						onlineUser.setSessionid(sessionid);
						onlineUser.setLogintime(new Date());
						onlineUser.setInvitetimes(0);
					}
					service.save(onlineUser);
				}
			}
		}
		return onlineUser;
	}

	/**
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	public static String getCookie(HttpServletRequest request, String key) {
		Cookie data = null;
		if (request != null && request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(key)) {
					data = cookie;
					break;
				}
			}
		}
		return data != null ? data.getValue() : null;
	}

	/**
	 * 
	 * @param user
	 * @param orgi
	 * @throws Exception
	 */
	public static void offline(String user, String orgi) throws Exception {
		if(UKDataContext.getContext()!=null){
			OnlineUserRepository service = UKDataContext.getContext().getBean(
					OnlineUserRepository.class);
			OnlineUserHisRepository onlineHisUserRes = UKDataContext.getContext().getBean(OnlineUserHisRepository.class) ;
			OnlineUser onlineUser = service.findByUserid(user);
			if (onlineUser != null) {
				List<OnlineUserHis> hisList = onlineHisUserRes.findBySessionid(onlineUser.getSessionid()) ;
				OnlineUserHis his = null ;
				if(hisList.size() > 0){
					his = hisList.get(0) ;
				}else{
					his = new OnlineUserHis();
				}
				
				onlineUser.setStatus(UKDataContext.OnlineUserOperatorStatus.OFFLINE.toString());
				onlineUser.setInvitestatus(UKDataContext.OnlineUserInviteStatus.DEFAULT.toString());
				onlineUser.setBetweentime((int) (new Date().getTime() - onlineUser.getLogintime().getTime()));
				onlineUser.setUpdatetime(new Date());
				service.save(onlineUser) ;
				
				UKTools.copyProperties(onlineUser, his);
				his.setDataid(onlineUser.getId());
				onlineHisUserRes.save(his);
			}
		}
	}
	
	/**
	 * 
	 * @param user
	 * @param orgi
	 * @throws Exception
	 */
	public static void refuseInvite(String user, String orgi) {
		OnlineUserRepository service = UKDataContext.getContext().getBean(
				OnlineUserRepository.class);

		OnlineUser onlineUser = service.findByUserid(user);
		if (onlineUser != null) {
			onlineUser.setInvitestatus(UKDataContext.OnlineUserInviteStatus.REFUSE.toString());
			onlineUser.setRefusetimes(onlineUser.getRefusetimes()+1);
			service.save(onlineUser) ;
		}
	}

	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		try {
			tmp.append(java.net.URLDecoder.decode(src, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return tmp.toString();
	}

	public static String getKeyword(String url) {
		Map<String, String[]> values = new HashMap<String, String[]>();
		try {
			parseParameters(values, url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		StringBuffer strb = new StringBuffer();
		String[] data = values.get("q");
		if (data != null) {
			for (String v : data) {
				strb.append(v);
			}
		}
		return strb.toString();
	}

	public static String getSource(String url) {
		String source = "0";
		try {
			URL addr = new URL(url);
			source = addr.getHost();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return source;
	}

	private static NewRequestMessage newRequestMessage(String user , String nickname, String orgi,
			String session, String appid, String ip, String osname,
			String browser , String headimg , IP ipdata , String channel) throws Exception {
		// 坐席服务请求，分配 坐席
		NewRequestMessage data = new NewRequestMessage();
		data.setAppid(appid);
		data.setOrgi(orgi);
		data.setUserid(user);
		data.setSession(session);
		data.setType(UKDataContext.MessageTypeEnum.NEW.toString());
		data.setId(UKTools.genID());

		AgentUserService service = UKDataContext.getContext().getBean(
				AgentUserService.class);
		AgentUser agentUser = service.findByUserid(user);
		if (agentUser == null) {
			agentUser = new AgentUser(data.getUserid(),channel,
					data.getUserid(), null, data.getOrgi(), data.getAppid()); // 创建排队用户的信息，当前用户只能在队列里存在一次，用
																				// UserID作为主键ID存储
			agentUser.setNickname(nickname);
			agentUser.setUsername(nickname);
			
			agentUser.setOsname(osname);
			agentUser.setBrowser(browser);
			agentUser.setAppid(appid);
			
			if (ipdata != null) {
				agentUser.setCountry(ipdata.getCountry());
				agentUser.setProvince(ipdata.getProvince());
				agentUser.setCity(ipdata.getCity());
				if(!StringUtils.isBlank(ip)){
					agentUser.setRegion(ipdata.toString() + "[" + ip + "]");
				}else{
					agentUser.setRegion(ipdata.toString());
				}
			}

			// agentUser.setContextid(session);
			agentUser.setHeadimgurl(headimg);
			// agentUser.setId(data.getUserid());
		}
		agentUser.setStatus(null); // 修改状态

		MessageInContent inMessage = new MessageInContent();
		inMessage.setChannelMessage(data);
		inMessage.setAgentUser(agentUser);
		inMessage.setMessage(data.getMessage());
		inMessage.setFromUser(data.getUserid());
		inMessage.setToUser(data.getAppid());
		inMessage.setId(data.getId());
		inMessage.setMessageType(data.getType());
		inMessage.setNickName(agentUser.getNickname());
		inMessage.setOrgi(data.getOrgi());
		inMessage.setUser(agentUser);

		MessageDataBean outMessageDataBean = RouterHelper.getRouteInstance()
				.handler(inMessage);

		if (outMessageDataBean != null) {
			data.setMessage(outMessageDataBean.getMessage());
		}
		return data;
	}
	
	
	public static NewRequestMessage newRequestMessage(String userid, String orgi,
			String session, String appid, String ip, String osname,
			String browser , String channel) throws Exception {
		IP ipdata = null ;
		if(!StringUtils.isBlank(ip)){
			ipdata = IPTools.getInstance().findGeography(ip);
		}
		String userID = UKTools.genIDByKey(session);
		String nickname = "Guest_" + userID;
		
		return newRequestMessage(userid , nickname, orgi, session, appid, ip, osname, browser , "" , ipdata , channel) ;
	}
	
	public static NewRequestMessage newRequestMessage(String openid , String nickname, String orgi,
			String session, String appid , String headimg , String country , String province , String city , String channel) throws Exception {
		IP ipdata = new IP() ;
		ipdata.setCountry(country);
		ipdata.setProvince(province);
		ipdata.setCity(city);
		return newRequestMessage(openid , nickname , orgi, session, appid, null , null , null , headimg , ipdata , channel) ;
	}

	public static void parseParameters(Map<String, String[]> map, String data,
			String encoding) throws UnsupportedEncodingException {
		if ((data == null) || (data.length() <= 0)) {
			return;
		}

		byte[] bytes = null;
		try {
			if (encoding == null)
				bytes = data.getBytes();
			else
				bytes = data.getBytes(encoding);
		} catch (UnsupportedEncodingException uee) {
		}
		parseParameters(map, bytes, encoding);
	}

	public static void parseParameters(Map<String, String[]> map, byte[] data,
			String encoding) throws UnsupportedEncodingException {
		if ((data != null) && (data.length > 0)) {
			int ix = 0;
			int ox = 0;
			String key = null;
			String value = null;
			while (ix < data.length) {
				byte c = data[(ix++)];
				switch ((char) c) {
				case '&':
					value = new String(data, 0, ox, encoding);
					if (key != null) {
						putMapEntry(map, key, value);
						key = null;
					}
					ox = 0;
					break;
				case '=':
					if (key == null) {
						key = new String(data, 0, ox, encoding);
						ox = 0;
					} else {
						data[(ox++)] = c;
					}
					break;
				case '+':
					data[(ox++)] = 32;
					break;
				case '%':
					data[(ox++)] = (byte) ((convertHexDigit(data[(ix++)]) << 4) + convertHexDigit(data[(ix++)]));

					break;
				default:
					data[(ox++)] = c;
				}
			}

			if (key != null) {
				value = new String(data, 0, ox, encoding);
				putMapEntry(map, key, value);
			}
		}
	}

	private static void putMapEntry(Map<String, String[]> map, String name,
			String value) {
		String[] newValues = null;
		String[] oldValues = (String[]) (String[]) map.get(name);
		if (oldValues == null) {
			newValues = new String[1];
			newValues[0] = value;
		} else {
			newValues = new String[oldValues.length + 1];
			System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
			newValues[oldValues.length] = value;
		}
		map.put(name, newValues);
	}

	private static byte convertHexDigit(byte b) {
		if ((b >= 48) && (b <= 57))
			return (byte) (b - 48);
		if ((b >= 97) && (b <= 102))
			return (byte) (b - 97 + 10);
		if ((b >= 65) && (b <= 70))
			return (byte) (b - 65 + 10);
		return 0;
	}

	// public static void main(String[] args){
	// System.out.println(getKeyword("http://www.so.com/link?url=http%3A%2F%2Fwww.r3yun.com%2F&q=R3+Query%E5%AE%98%E7%BD%91&ts=1484181457&t=e2ad49617cd5de0eb0937f3e2a84669&src=haosou"))
	// ;
	// System.out.println(getSource("https://www.google.com.hk/")) ;
	// }
	
	/**
	 * 发送邀请
	 * @param userid
	 * @throws Exception 
	 */
	public static void sendWebIMClients(String userid , String msg) throws Exception{
		List<WebIMClient> clients = OnlineUserUtils.webIMClients.getClients(userid) ;
		if(clients!=null && clients.size()>0){
			for(WebIMClient client : clients){
				try{
					client.getSse().send(SseEmitter.event().reconnectTime(0).data(msg));
				}catch(Exception ex){
					OnlineUserUtils.webIMClients.removeClient(userid , client.getClient() , true) ;
				}finally{
					client.getSse().complete();
				}
			}
		}
	}
}
