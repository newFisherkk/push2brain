package cn.com.egova.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class BaseRecInfoExtDTO {
	private String taskId;//任务号
	private String eventSorB; //问题类型（事件/部件）
	private String region; //所属区域
	private String streetName; //所属街道
	private String communityName; //所属社区
	private String eventStatus; //问题状态
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	@JSONField(format ="yyyy-MM-dd HH:mm:ss")
	private Date newInstTime;  //立案时间
	private String handleUnitName; //处置部门
	private String handleNumber;  //处置数
	private String reworkNumber;  //返工数
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	@JSONField(format ="yyyy-MM-dd HH:mm:ss")
	private Date resultEventTime; //结案时间
	private String resultEventUserName; //结案人
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	@JSONField(format ="yyyy-MM-dd HH:mm:ss")
	private Date cancelEventTime; //作废时间
	private String cancelOpinion; //作废意见
	private String supervisorName; //监督员
	private String firstHandleUnit; //一级处置部门
	private String secondHandleUnit; //二级处置部门

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getCommunityName() {
		return communityName;
	}

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}

	public String getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}


	public String getHandleUnitName() {
		return handleUnitName;
	}

	public void setHandleUnitName(String handleUnitName) {
		this.handleUnitName = handleUnitName;
	}

	public String getHandleNumber() {
		return handleNumber;
	}

	public void setHandleNumber(String handleNumber) {
		this.handleNumber = handleNumber;
	}

	public String getReworkNumber() {
		return reworkNumber;
	}

	public void setReworkNumber(String reworkNumber) {
		this.reworkNumber = reworkNumber;
	}

	public Date getResultEventTime() {
		return resultEventTime;
	}

	public void setResultEventTime(Date resultEventTime) {
		this.resultEventTime = resultEventTime;
	}

	public String getResultEventUserName() {
		return resultEventUserName;
	}

	public void setResultEventUserName(String resultEventUserName) {
		this.resultEventUserName = resultEventUserName;
	}
	
	public String getCancelOpinion() {
		return cancelOpinion;
	}

	public void setCancelOpinion(String cancelOpinion) {
		this.cancelOpinion = cancelOpinion;
	}

	public String getSupervisorName() {
		return supervisorName;
	}

	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}

	public String getFirstHandleUnit() {
		return firstHandleUnit;
	}

	public void setFirstHandleUnit(String firstHandleUnit) {
		this.firstHandleUnit = firstHandleUnit;
	}

	public String getSecondHandleUnit() {
		return secondHandleUnit;
	}

	public void setSecondHandleUnit(String secondHandleUnit) {
		this.secondHandleUnit = secondHandleUnit;
	}

	public String getEventSorB() {
		return eventSorB;
	}

	public void setEventSorB(String eventSorB) {
		this.eventSorB = eventSorB;
	}

	public Date getNewInstTime() {
		return newInstTime;
	}

	public void setNewInstTime(Date newInstTime) {
		this.newInstTime = newInstTime;
	}

	public Date getCancelEventTime() {
		return cancelEventTime;
	}

	public void setCancelEventTime(Date cancelEventTime) {
		this.cancelEventTime = cancelEventTime;
	}
}
