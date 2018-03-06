package com.ukefu.webim.web.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Proxy;

@Entity
@Table(name = "uk_agentservice")
@Proxy(lazy = false)
public class AgentService implements Serializable {
	private static final long serialVersionUID = -5052623717164550681L;
	private String agentusername;
	private String agentno;
	private String status;
	private long times;
	private Date servicetime;
	private String orgi;
	private String id;
	private String username;
	private String userid;
	private String channel;
	private Date logindate;
	private String source;
	private Date endtime;
	private String ipaddr;
	private String osname;
	private String browser;
	private String nickname;
	protected String city;
	protected String province;
	protected String country;
	protected String headimgurl;
	private String region;
	private long sessiontimes = 0L;
	private int waittingtime;
	private int tokenum;
	private Date createtime = new Date();
	private Date updatetime;
	private String appid;
	private String sessiontype;
	private String contextid;
	private String dataid; // 用户记录 OnlineUser对象的ID
	private String agentserviceid;
	private long ordertime = System.currentTimeMillis();
	private String snsuser;
	@Transient
	private Date lastmessage = new Date();
	private Date waittingtimestart = new Date();
	@Transient
	private Date lastgetmessage = new Date();
	@Transient
	private String lastmsg;
	@Transient
	private boolean tip = false;
	@Transient
	private String agentservice;
	@Transient
	private boolean agentTip = false;

	public String getAgentno() {
		return this.agentno;
	}

	public void setAgentno(String agentno) {
		this.agentno = agentno;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getTimes() {
		return this.times;
	}

	public void setTimes(long times) {
		this.times = times;
	}

	public String getOrgi() {
		return this.orgi;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}

	public Date getServicetime() {
		return this.servicetime;
	}

	public void setServicetime(Date servicetime) {
		this.servicetime = servicetime;
	}

	public String getAgentusername() {
		return agentusername;
	}

	public void setAgentusername(String agentusername) {
		this.agentusername = agentusername;
	}

	@Transient
	public String getTopic() {
		return "/" + this.orgi + "/" + this.agentno;
	}

	@Transient
	public boolean isAgentTip() {
		return this.agentTip;
	}

	public void setAgentTip(boolean agentTip) {
		this.agentTip = agentTip;
	}

	@Transient
	private boolean fromhis = false;
	@Transient
	private boolean online = false;
	@Transient
	private boolean disconnect = false;
	@Transient
	private String agentskill;

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	public String getId() {
		return this.id;
	}

	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getUpdatetime() {
		return this.updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserid() {
		return this.userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getChannel() {
		return this.channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Date getLogindate() {
		return this.logindate;
	}

	public void setLogindate(Date logindate) {
		this.logindate = logindate;
	}

	public String getContextid() {
		return this.contextid;
	}

	public void setContextid(String contextid) {
		this.contextid = contextid;
	}

	public String getAgentserviceid() {
		return this.agentserviceid;
	}

	public void setAgentserviceid(String agentserviceid) {
		this.agentserviceid = agentserviceid;
	}

	@Transient
	public Date getLastmessage() {
		return this.lastmessage;
	}

	public void setLastmessage(Date lastmessage) {
		this.lastmessage = lastmessage;
	}

	@Transient
	public boolean isTip() {
		return this.tip;
	}

	public void setTip(boolean tip) {
		this.tip = tip;
	}

	@Transient
	public boolean isDisconnect() {
		return this.disconnect;
	}

	public void setDisconnect(boolean disconnect) {
		this.disconnect = disconnect;
	}

	public Date getEndtime() {
		return this.endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public long getSessiontimes() {
		return this.sessiontimes;
	}

	public void setSessiontimes(long sessiontimes) {
		this.sessiontimes = sessiontimes;
	}

	@Transient
	public String getSessiontype() {
		return this.sessiontype;
	}

	public void setSessiontype(String sessiontype) {
		this.sessiontype = sessiontype;
	}

	public String getAgentskill() {
		return this.agentskill;
	}

	public void setAgentskill(String agentskill) {
		this.agentskill = agentskill;
	}

	@Transient
	public boolean isOnline() {
		return this.online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	@Transient
	public boolean isFromhis() {
		return this.fromhis;
	}

	public void setFromhis(boolean fromhis) {
		this.fromhis = fromhis;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Transient
	public Date getLastgetmessage() {
		return this.lastgetmessage;
	}

	public void setLastgetmessage(Date lastgetmessage) {
		this.lastgetmessage = lastgetmessage;
	}

	@Transient
	public String getLastmsg() {
		return this.lastmsg;
	}

	public void setLastmsg(String lastmsg) {
		this.lastmsg = lastmsg;
	}

	public String getSnsuser() {
		return this.snsuser;
	}

	public void setSnsuser(String snsuser) {
		this.snsuser = snsuser;
	}

	public int getWaittingtime() {
		return this.waittingtime;
	}

	public void setWaittingtime(int waittingtime) {
		this.waittingtime = waittingtime;
	}

	public int getTokenum() {
		return this.tokenum;
	}

	public void setTokenum(int tokenum) {
		this.tokenum = tokenum;
	}

	@Transient
	public Date getWaittingtimestart() {
		return this.waittingtimestart;
	}

	public void setWaittingtimestart(Date waittingtimestart) {
		this.waittingtimestart = waittingtimestart;
	}

	public String getAppid() {
		return this.appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHeadimgurl() {
		return this.headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getIpaddr() {
		return this.ipaddr;
	}

	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}

	public String getOsname() {
		return this.osname;
	}

	public void setOsname(String osname) {
		this.osname = osname;
	}

	public String getBrowser() {
		return this.browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	@Transient
	public long getOrdertime() {
		return this.ordertime;
	}

	public void setOrdertime(long ordertime) {
		this.ordertime = ordertime;
	}

	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Transient
	public String getAgentservice() {
		return this.agentservice;
	}

	public void setAgentservice(String agentservice) {
		this.agentservice = agentservice;
	}

	public String getDataid() {
		return dataid;
	}

	public void setDataid(String dataid) {
		this.dataid = dataid;
	}
}
