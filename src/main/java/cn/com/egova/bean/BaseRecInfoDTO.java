package cn.com.egova.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class BaseRecInfoDTO {
	private String eventId;
	private String eventType;
	private String eventSubType;
	private String eventLevel;
	private String eventSource;
	private String eventTile;
	private String eventDesc;
	private String gridCode;
	private String gridName;
	private String communityCode;
	private String streetCode;
	private String districtCode;
	private String cityCode;
	private String eventAddress;
	private double coordX;
	private double coordY;
	private String handlerStatus;
	@JSONField(format ="yyyy-MM-dd HH:mm:ss")
	private Date reportTime;
	private String reportUserPhone;
	private String reportUserUnit;
	private String reportUserUnitId;
	private String reportUserPost;
	private String reportUserPostId;
	private String reportType = "0";
	private String attachments;
	private List<ProcessInfoDTO> processActs;

	public List<ProcessInfoDTO> getProcessActs() {
		return processActs;
	}

	public void setProcessActs(List<ProcessInfoDTO> processActs) {
		this.processActs = processActs;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEventSubType() {
		return eventSubType;
	}

	public void setEventSubType(String eventSubType) {
		this.eventSubType = eventSubType;
	}

	public String getEventLevel() {
		return eventLevel;
	}

	public void setEventLevel(String eventLevel) {
		this.eventLevel = eventLevel;
	}

	public String getEventSource() {
		return eventSource;
	}

	public void setEventSource(String eventSource) {
		this.eventSource = eventSource;
	}

	public String getEventTile() {
		return eventTile;
	}

	public void setEventTile(String eventTile) {
		this.eventTile = eventTile;
	}

	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public String getGridCode() {
		return gridCode;
	}

	public void setGridCode(String gridCode) {
		this.gridCode = gridCode;
	}

	public String getGridName() {
		return gridName;
	}

	public void setGridName(String gridName) {
		this.gridName = gridName;
	}

	public String getCommunityCode() {
		return communityCode;
	}

	public void setCommunityCode(String communityCode) {
		this.communityCode = communityCode;
	}

	public String getStreetCode() {
		return streetCode;
	}

	public void setStreetCode(String streetCode) {
		this.streetCode = streetCode;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getEventAddress() {
		return eventAddress;
	}

	public void setEventAddress(String eventAddress) {
		this.eventAddress = eventAddress;
	}

	public double getCoordX() {
		return coordX;
	}

	public void setCoordX(double coordX) {
		this.coordX = coordX;
	}

	public double getCoordY() {
		return coordY;
	}

	public void setCoordY(double coordY) {
		this.coordY = coordY;
	}

	public String getHandlerStatus() {
		return handlerStatus;
	}

	public void setHandlerStatus(String handlerStatus) {
		this.handlerStatus = handlerStatus;
	}

	public Date getReportTime() {
		return reportTime;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	public void setReportTime(Date reportTime) {
		this.reportTime = reportTime;
	}

	public String getReportUserPhone() {
		return reportUserPhone;
	}

	public void setReportUserPhone(String reportUserPhone) {
		this.reportUserPhone = reportUserPhone;
	}

	public String getReportUserUnit() {
		return reportUserUnit;
	}

	public void setReportUserUnit(String reportUserUnit) {
		this.reportUserUnit = reportUserUnit;
	}

	public String getReportUserUnitId() {
		return reportUserUnitId;
	}

	public void setReportUserUnitId(String reportUserUnitId) {
		this.reportUserUnitId = reportUserUnitId;
	}

	public String getReportUserPost() {
		return reportUserPost;
	}

	public void setReportUserPost(String reportUserPost) {
		this.reportUserPost = reportUserPost;
	}

	public String getReportUserPostId() {
		return reportUserPostId;
	}

	public void setReportUserPostId(String reportUserPostId) {
		this.reportUserPostId = reportUserPostId;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}
}
