package cn.com.egova.service;

public interface PushBaseInfoManager {
	
	void pushBaseRecInfo(String recInfoUrl, String mediaUrl);

	void pushRegionEvalInfo(String regionEvalUrl);

	void pushUnitEvalInfo(String unitEvalUrl);

	String getAuthToken(String code, String tokenKey, String getTokenUrl);

	void pushRecMediaTrans(String mediaUrl);

	void pushHisRecInfo(String recInfoUrl, String mediaUrl);

	void testRollback();
}
