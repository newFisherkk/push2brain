package cn.com.egova.bean;

public class RegionEvalDTO {
	private String regionId;
	private String regionName;
	private Integer reportCount;
	private Integer handlerCount;
	private Integer filingCount;
	private Integer dispatchCount;
	private Integer checkCount;
	private Integer closedCount;
	private Float disposalRate;
	private Float closedRate;
	private Float publicReportRate;
	private Float closedInTimeRate;
	private Integer year;
	private Integer month;

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public Integer getReportCount() {
		return reportCount;
	}

	public void setReportCount(Integer reportCount) {
		this.reportCount = reportCount;
	}

	public Integer getHandlerCount() {
		return handlerCount;
	}

	public void setHandlerCount(Integer handlerCount) {
		this.handlerCount = handlerCount;
	}

	public Integer getFilingCount() {
		return filingCount;
	}

	public void setFilingCount(Integer filingCount) {
		this.filingCount = filingCount;
	}

	public Integer getDispatchCount() {
		return dispatchCount;
	}

	public void setDispatchCount(Integer dispatchCount) {
		this.dispatchCount = dispatchCount;
	}

	public Integer getCheckCount() {
		return checkCount;
	}

	public void setCheckCount(Integer checkCount) {
		this.checkCount = checkCount;
	}

	public Integer getClosedCount() {
		return closedCount;
	}

	public void setClosedCount(Integer closedCount) {
		this.closedCount = closedCount;
	}

	public Float getDisposalRate() {
		return disposalRate;
	}

	public void setDisposalRate(Float disposalRate) {
		this.disposalRate = disposalRate;
	}

	public Float getClosedRate() {
		return closedRate;
	}

	public void setClosedRate(Float closedRate) {
		this.closedRate = closedRate;
	}

	public Float getPublicReportRate() {
		return publicReportRate;
	}

	public void setPublicReportRate(Float publicReportRate) {
		this.publicReportRate = publicReportRate;
	}

	public Float getClosedInTimeRate() {
		return closedInTimeRate;
	}

	public void setClosedInTimeRate(Float closedInTimeRate) {
		this.closedInTimeRate = closedInTimeRate;
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
