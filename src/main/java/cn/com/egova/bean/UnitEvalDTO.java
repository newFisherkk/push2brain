package cn.com.egova.bean;

public class UnitEvalDTO {
	private String unitId;
	private String unitName;
	private Integer needHandlerCount;
	private Integer handlerInTimeCount;
	private Integer reworkCount;
	private Integer comIndex;
	private Float handlerInTimeRate;
	private Float reworkRate;
	private Integer year;
	private Integer month;

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Integer getNeedHandlerCount() {
		return needHandlerCount;
	}

	public void setNeedHandlerCount(Integer needHandlerCount) {
		this.needHandlerCount = needHandlerCount;
	}

	public Integer getHandlerInTimeCount() {
		return handlerInTimeCount;
	}

	public void setHandlerInTimeCount(Integer handlerInTimeCount) {
		this.handlerInTimeCount = handlerInTimeCount;
	}

	public Integer getReworkCount() {
		return reworkCount;
	}

	public void setReworkCount(Integer reworkCount) {
		this.reworkCount = reworkCount;
	}

	public Integer getComIndex() {
		return comIndex;
	}

	public void setComIndex(Integer comIndex) {
		this.comIndex = comIndex;
	}

	public Float getHandlerInTimeRate() {
		return handlerInTimeRate;
	}

	public void setHandlerInTimeRate(Float handlerInTimeRate) {
		this.handlerInTimeRate = handlerInTimeRate;
	}

	public Float getReworkRate() {
		return reworkRate;
	}

	public void setReworkRate(Float reworkRate) {
		this.reworkRate = reworkRate;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}
}
