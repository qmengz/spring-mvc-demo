package com.demo.mobel;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "xml")
public class Msg implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String ToUserName;
	private String FromUserName;
	private String CreateTime;
	private String MsgType;
	private String Event;
	private String EventKey;
	private String Ticket;
	private String Content;
	private String MsgId;

	public String getMsgId() {
		return MsgId;
	}

	public void setMsgId(String msgId) {
		MsgId = msgId;
	}

	public String getToUserName() {
		return ToUserName;
	}
	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}
	public String getFromUserName() {
		return FromUserName;
	}
	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public String getMsgType() {
		return MsgType;
	}
	public void setMsgType(String msgType) {
		MsgType = msgType;
	}
	public String getEvent() {
		return Event;
	}
	public void setEvent(String event) {
		Event = event;
	}
	public String getEventKey() {
		return EventKey;
	}
	public void setEventKey(String eventKey) {
		EventKey = eventKey;
	}
	public String getTicket() {
		return Ticket;
	}
	public void setTicket(String ticket) {
		Ticket = ticket;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}

	@Override
	public String toString() {
		return "Msg{" +
				"ToUserName='" + ToUserName + '\'' +
				", FromUserName='" + FromUserName + '\'' +
				", CreateTime='" + CreateTime + '\'' +
				", MsgType='" + MsgType + '\'' +
				", Event='" + Event + '\'' +
				", EventKey='" + EventKey + '\'' +
				", Ticket='" + Ticket + '\'' +
				", Content='" + Content + '\'' +
				", MsgId='" + MsgId + '\'' +
				'}';
	}
}
