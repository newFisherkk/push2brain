package cn.com.egova.service.impl;

import cn.com.egova.bean.*;
import cn.com.egova.config.NamedThreadPoolFactory;
import cn.com.egova.constant.SqlConst;
import cn.com.egova.service.PushBaseInfoManager;
import cn.com.egova.tools.HttpClientPoolUtils;
import cn.com.egova.tools.HttpFileUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;


@Service
@PropertySource("classpath:config.properties")
@Transactional(value = "bizTransactionManager")
public class PushBaseInfoManagerImpl implements PushBaseInfoManager {
	
	private static final Logger logger = LoggerFactory.getLogger(PushBaseInfoManagerImpl.class);

	@Autowired
	@Qualifier("bizJdbcTemplate")
	JdbcTemplate jtBiz;
	@Autowired
	@Qualifier("statJdbcTemplate")
	JdbcTemplate jtStat;
	
	@Autowired
	ShareFileInfo shareFileInfo;
	ExecutorService singleThreadExecutor = new ThreadPoolExecutor(5, 5, 0, TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(50), new NamedThreadPoolFactory("push-media-pool"));
	private static final String FILE_SEPERATOR = "/";
	
	private String token;

	@Value("${push.rec.auth.code}")
	private String code;

	@Value("${push.rec.auth.tokenKey}")
	private String tokenKey;

	@Value("${push.rec.auth.url}")
	private String getTokenUrl;

	@Value("${senderCode}")
	private String senderCode;

	@Value("${push.regionCode}")
	private String regionCode;
	
	private Integer regionID;
	
	private static final String PUSH_TO_CITY_BRAIN = "推送至城市大脑";

	private void initToken() {
		if(token==null || token.length()==0){
			token = getAuthToken(code,tokenKey,getTokenUrl);
		}
	}

	private void initRegionCode() {
		if(StringUtils.isEmpty(senderCode) && !StringUtils.isEmpty(regionCode)){
			List<Map<String, Object>> maps = jtBiz.queryForList("select region_id,region_name from tc_region where region_code=?", regionCode);
			if(maps.size() > 0) {
				senderCode = "推送至" + maps.get(0).get("region_name");
				regionID = (Integer) maps.get(0).get("region_id");
			}
		}
	}
	
	@Override
	public String getAuthToken(String code, String tokenKey, String getTokenUrl) {
		String token = "";
		Map<String, String> otherParams = new HashMap<>();
		otherParams.put("code",code);
		otherParams.put("key",tokenKey);
		String result = HttpClientPoolUtils.sendPostWithFile(getTokenUrl, null,otherParams,null);
		ResultInfo httpResult = JSONObject.parseObject(result, ResultInfo.class);
		if(httpResult!=null && httpResult.isSuccess()){
			token = httpResult.getData();
		}
		logger.info("获取鉴权result信息：{},token：{}",result,token);
		return token;
	}
	
	@Override
	public void pushBaseRecInfo(String recInfoUrl, final String mediaUrl) {
		initRegionCode();
		if(PUSH_TO_CITY_BRAIN.equals(senderCode)){
			initToken();
		}
		LocalDate localDate = LocalDate.now();
		List<Map<String, Object>> recList = new ArrayList<>();
		//过滤案件上报区域
		if(regionID != null){
			recList = jtBiz.queryForList("SELECT a.* from to_rec a where a.create_time > ? and district_id=? or a.rec_id in (select b.rec_id from to_rec_transit b where b.syn_flag=0 and b.sender_code=?)", localDate, regionID, senderCode);
		} else {
			recList = jtBiz.queryForList("SELECT a.* from to_rec a where a.create_time > ? or a.rec_id in (select b.rec_id from to_rec_transit b where b.syn_flag=0 and b.sender_code=?)", localDate, senderCode);
		}
		logger.info("{} 需推送基本案件信息数据数量为：{},senderCoder:{}",localDate,recList.size(),senderCode);
		if(recList.size() == 0){
			return;
		}
		buildDTOAndPush(recInfoUrl, mediaUrl, recList,0);
	}

	private void buildDTOAndPush(String recInfoUrl, String mediaUrl, List<Map<String, Object>> recList,Integer archiveFlag) {
		ArrayList<BaseRecInfoDTO> recInfoDTOS = new ArrayList<>();
		for(Map<String, Object> map : recList){
			Object recID = map.get("rec_id");
			BaseRecInfoDTO baseRecInfoDTO = new BaseRecInfoDTO();
			baseRecInfoDTO.setEventId(String.valueOf(recID));
			baseRecInfoDTO.setEventType((String) map.get("main_type_name"));
			baseRecInfoDTO.setEventSubType(String.valueOf(map.get("sub_type_id")));
			baseRecInfoDTO.setEventLevel(String.valueOf(map.get("event_level_id")));
			baseRecInfoDTO.setEventSource(String.valueOf(map.get("event_src_id")));
			baseRecInfoDTO.setEventTile((String) map.get("rec_type_name"));//标题选择案件类型
			baseRecInfoDTO.setEventDesc((String) map.get("event_desc"));
			baseRecInfoDTO.setEventAddress((String) map.get("address"));
			baseRecInfoDTO.setCoordX( null == map.get("coordinate_x") ? 0.0f : (double)map.get("coordinate_x"));
			baseRecInfoDTO.setCoordY(null == map.get("coordinate_y") ? 0.0f : (double)map.get("coordinate_y"));
			baseRecInfoDTO.setGridName((String) map.get("duty_grid_name"));
			baseRecInfoDTO.setReportTime((Date) map.get("create_time"));
			baseRecInfoDTO.setGridCode(getRegionCode(map.get("cell_id")));
			baseRecInfoDTO.setCommunityCode(getRegionCode(map.get("community_id")));
			baseRecInfoDTO.setStreetCode(getRegionCode(map.get("street_id")));
			baseRecInfoDTO.setDistrictCode(getRegionCode(map.get("district_id")));
			baseRecInfoDTO.setCityCode(regionCode);
			setReportInfo(baseRecInfoDTO,recID,map.get("patrol_id"));
			setRecExtInfo(recID,baseRecInfoDTO);
			setProcessInfo(baseRecInfoDTO,recID);
			List<Map<String, Object>> mediaList = new ArrayList<>();
			if(archiveFlag == 0){
				mediaList = jtBiz.queryForList("SELECT * from to_media where relation_id=? and delete_flag=0", recID);
			} else {
				mediaList = jtBiz.queryForList("SELECT * from to_his_media where relation_id=? and delete_flag=0", recID);
			}
			if(mediaList.size() > 0){
				ArrayList<MediaInfo> mediaInfos = new ArrayList<>(mediaList.size());                   	
				for(Map<String, Object> media : mediaList){
					MediaInfo mediaInfo = new MediaInfo();
					mediaInfo.setMediaURL(shareFileInfo.getMediaUrlPrefix()+FILE_SEPERATOR+media.get("media_path")+FILE_SEPERATOR+media.get("media_name"));
					mediaInfo.setMediaUsage((String)media.get("media_usage"));
					mediaInfo.setMediaType((String)media.get("media_type"));
					mediaInfos.add(mediaInfo);
				}
				baseRecInfoDTO.setAttachments(mediaInfos);
			}
			recInfoDTOS.add(baseRecInfoDTO);
			if(recInfoDTOS.size() == 50){
				pushRecInfoDTOS(recInfoUrl,recInfoDTOS,archiveFlag);
			}
			//推送多媒体
			if(!StringUtils.isEmpty(mediaUrl)){
				List<Map<String, Object>> finalMediaList = mediaList;
				singleThreadExecutor.execute(() -> {
					for(Map<String, Object> media : finalMediaList){
						Object mediaID = media.get("media_id");
						String mediaName = (String) media.get("media_name");
						String filePath = media.get("media_path") +FILE_SEPERATOR+mediaName;
						InputStream inputStream = null;
						try {
							inputStream = HttpFileUtils.getFileInputStream(filePath, shareFileInfo);
							MockMultipartFile mockMultipartFile = new MockMultipartFile(mediaName,media.get("media_usage")+mediaName,"",inputStream);
							Map<String, MultipartFile> fileHashMap = new HashMap<>(1);
							fileHashMap.put("files",mockMultipartFile);
							Map<String, String> headParams = new HashMap<>(1);
							headParams.put("token",token);
							Map<String, String> otherParams = new HashMap<>(1);
							otherParams.put("eventId", String.valueOf(recID));
							String resultStr = HttpClientPoolUtils.sendPostWithFile(mediaUrl, fileHashMap, otherParams, headParams);
							ResultInfo mediaResult = JSON.parseObject(resultStr, ResultInfo.class);
							if (mediaResult == null){
								logger.info("http上传图片失败，返回值为空，请求url:{}",mediaUrl);
								updateRecMediaTrans("http推送图片返回值为空，请尝试ping第三方ip："+mediaUrl,mediaID,0);
							} else if (mediaResult.isSuccess()) {
								logger.info("http上传图片请求成功，返回值：{}", resultStr);
								updateRecMediaTrans("http上传图片请求成功",mediaID,1);
							} else {
								logger.info("http上传图片失败，返回值：{}",resultStr);
								updateRecMediaTrans(resultStr,mediaID,0);
							}
						} catch (IOException e) {
							logger.error("推送多媒体服务出错",e);
							updateRecMediaTrans("推送多媒体服务出错："+e.getMessage(),mediaID,0);
							break;
						}
					}
				});
			}
		}
		//不足50个的还要推一次
		if(recInfoDTOS.size() > 0){
			pushRecInfoDTOS(recInfoUrl,recInfoDTOS,archiveFlag);
		}
	}

	private void pushRecInfoDTOS(String recInfoUrl,ArrayList<BaseRecInfoDTO> recInfoDTOS,Integer archiveFlag){
		String result = HttpClientPoolUtils.sendPostJson(recInfoUrl, JSONObject.toJSONString(recInfoDTOS),token);
		ResultInfo httpResult = JSON.parseObject(result, ResultInfo.class);
		if(httpResult == null) {
			logger.info("【事件详情数据推送】发送http请求失败，请求url:{}，请求数据JSON:{}",recInfoUrl,JSONObject.toJSONString(recInfoDTOS));
			for(BaseRecInfoDTO  recInfoDTO: recInfoDTOS){
				Integer eventID = Integer.valueOf(recInfoDTO.getEventId());
				updateRecTran("【事件详情数据推送】返回信息为空，请尝试ping第三方ip:"+recInfoUrl,eventID,0,archiveFlag);
			}
		} else if(httpResult.isSuccess()){
			logger.info("【事件详情数据推送】发送http请求成功，返回值：{}",result);
			for(BaseRecInfoDTO recInfoDTO : recInfoDTOS) {
				Integer eventID = Integer.valueOf(recInfoDTO.getEventId());
				updateRecTran("推送成功", eventID, 1,archiveFlag);
			}
		} else {
			if(PUSH_TO_CITY_BRAIN.equals(senderCode)){
				//重新请求授权-推送城市大脑需要
				token = getAuthToken(code,tokenKey,getTokenUrl);
				logger.info("事件详情数据推送发送http请求失败，重新获取token");
			}
			for(BaseRecInfoDTO recInfoDTO : recInfoDTOS) {
				Integer eventID = Integer.valueOf(recInfoDTO.getEventId());
				updateRecTran(result, eventID, 0,archiveFlag);
			}
		}
		recInfoDTOS.clear();
	}

	private void setRecExtInfo(Object recID, BaseRecInfoDTO baseRecInfoDTO) {
		List<Map<String, Object>> bizMap = jtBiz.queryForList(SqlConst.getRecBaseExtSql, recID);
		if(bizMap.size() == 0) bizMap = jtBiz.queryForList(SqlConst.getHisRecBaseExtSql, recID);
		if(bizMap.size() > 0){
			Map<String, Object> map = bizMap.get(0);
			baseRecInfoDTO.setTaskId(String.valueOf(map.get("taskId")));
			baseRecInfoDTO.setEventSorB((String) map.get("eventSorB"));
			baseRecInfoDTO.setRegion((String) map.get("region"));
			baseRecInfoDTO.setStreetName((String) map.get("streetName"));
			baseRecInfoDTO.setCommunityName((String) map.get("communityName"));
			baseRecInfoDTO.setFirstHandleUnit((String) map.get("firstHandleUnit"));
			baseRecInfoDTO.setSupervisorName((String) map.get("supervisorName"));
			baseRecInfoDTO.setSecondHandleUnit((String) map.get("secondHandleUnit"));
		}
		List<Map<String, Object>> statMap = jtStat.queryForList(SqlConst.getRecBaseExtSqlInStat, recID);
		if(statMap.size() > 0){
			Map<String, Object> map = statMap.get(0);
			baseRecInfoDTO.setEventStatus((String) map.get("eventStatus"));
			baseRecInfoDTO.setHandleNumber(String.valueOf(map.get("handleNumber")));
			baseRecInfoDTO.setReworkNumber(String.valueOf(map.get("reworkNumber")));
			baseRecInfoDTO.setNewInstTime((Date) map.get("newInstTime"));
			baseRecInfoDTO.setHandleUnitName((String) map.get("handleUnitName"));
			baseRecInfoDTO.setResultEventTime((Date) map.get("resultEventTime"));
			baseRecInfoDTO.setResultEventUserName((String) map.get("resultEventUserName"));
			baseRecInfoDTO.setCancelEventTime((Date) map.get("cancelEventTime"));
			baseRecInfoDTO.setCancelOpinion((String) map.get("cancelOpinion"));
		}
	}

	@Override
	public void pushRegionEvalInfo(String regionEvalUrl) {
		if(StringUtils.isEmpty(regionEvalUrl)) return;
		initRegionCode();
		if(PUSH_TO_CITY_BRAIN.equals(senderCode)){
			initToken();
		}
		List<RegionEvalDTO> regionEvalDTOS = jtStat.query(SqlConst.getRegionEvalSql, new BeanPropertyRowMapper<>(RegionEvalDTO.class));
		logger.info("【区域评价】数据推送定时任务开始,需推送数据：{}",regionEvalDTOS.size());
		if(regionEvalDTOS.size()>0){
			String result = HttpClientPoolUtils.sendPostJson(regionEvalUrl, JSONObject.toJSONString(regionEvalDTOS),token);
			ResultInfo httpResult = JSON.parseObject(result, ResultInfo.class);
			if(httpResult == null) {
				logger.info("【区域评价】数据推送发送http请求失败，请求url:{}，请求数据JSON:{}",regionEvalUrl,JSONObject.toJSONString(regionEvalDTOS));
			} else if (httpResult.isSuccess()){
				logger.info("【区域评价】数据推送发送http请求成功");
			} else {
				logger.info("【区域评价】数据推送发送http请求失败，返回信息：{}",result);
			}
		}
	}

	@Override
	public void pushUnitEvalInfo(String unitEvalUrl) {
		if(StringUtils.isEmpty(unitEvalUrl)) return;
		initRegionCode();
		if(PUSH_TO_CITY_BRAIN.equals(senderCode)){
			initToken();
		}
		List<UnitEvalDTO> unitEvalDTOS = new ArrayList<>();
		if(regionID!=null){
			unitEvalDTOS = jtStat.query(SqlConst.getExactlyUnitEvalSql, new BeanPropertyRowMapper<>(UnitEvalDTO.class), regionID);
		} else {
			unitEvalDTOS = jtStat.query(SqlConst.getUnitEvalSql, new BeanPropertyRowMapper<>(UnitEvalDTO.class));
		}
		logger.info("【部门评价】数据推送定时任务开始,需推送数据：{}",unitEvalDTOS.size());
		if(unitEvalDTOS.size()>0){
			String result = HttpClientPoolUtils.sendPostJson(unitEvalUrl, JSONObject.toJSONString(unitEvalDTOS),token);
			ResultInfo httpResult = JSON.parseObject(result, ResultInfo.class);
			if(httpResult == null) {
				logger.info("【部门评价】数据推送发送http请求失败，请求url:{}，请求数据JSON:{}",unitEvalUrl,JSONObject.toJSONString(unitEvalDTOS));
			} else if (httpResult.isSuccess()){
				logger.info("【部门评价】数据推送发送http请求成功");
			} else {
				logger.info("【部门评价】数据推送发送http请求失败，返回信息：{}",result);
			}
		}
	}

	private String getRegionCode(Object regionID) {
		String code = "";
		if(null != regionID){
			List<Map<String, Object>> list = jtBiz.queryForList("select * from tc_region where region_id = ?", regionID);
			if(list.size() > 0){
				code = (String) list.get(0).get("region_code");
			}
		}
		return code;
	}

	/**
	 * 设置上报人信息
	 * @param baseRecInfoDTO
	 * @param recID
	 * @param patrolID
	 */
	private void setReportInfo(BaseRecInfoDTO baseRecInfoDTO, Object recID, Object patrolID) {
		List<Map<String, Object>> callRecordList = jtBiz.queryForList("select  * from to_call_record where rec_id = ?", recID);
		if (callRecordList.size() > 0) {
			if (null != callRecordList.get(0).get("tel_call")){
				baseRecInfoDTO.setReportUserPhone((String) callRecordList.get(0).get("tel_call"));
			} else if (null != callRecordList.get(0).get("reporter_id")){
				List<Map<String, Object>> reporterList = jtBiz.queryForList("select * from tc_reporter where reporter_id = ?", callRecordList.get(0).get("reporter_id"));
				if(reporterList.size() > 0){
					baseRecInfoDTO.setReportUserPhone((String) reporterList.get(0).get("contact"));
				}
			}
		} else {
			List<Map<String, Object>> humanList = jtBiz.queryForList("select * from tc_human where human_id=?", patrolID);
			if (humanList.size() > 0) {
				baseRecInfoDTO.setReportUserPhone((String) humanList.get(0).get("tel_mobile"));
				baseRecInfoDTO.setReportUserUnitId((String) humanList.get(0).get("unit_id"));
				baseRecInfoDTO.setReportUserUnit((String) humanList.get(0).get("unit_name"));
				//人员和岗位为多对多关系，这里选择第一个就行了
				List<Map<String, Object>> humanRoleList = jtBiz.queryForList("select * from tc_human_role where human_id=?", patrolID);
				if (humanRoleList.size() > 0) {
					Object roleID = humanRoleList.get(0).get("role_id");
					baseRecInfoDTO.setReportUserPostId((String) roleID);
					List<Map<String, Object>> roleList = jtBiz.queryForList("select * from tc_role where role_id = ?", roleID);
					if(roleList.size() > 0) {
						baseRecInfoDTO.setReportUserPost((String) roleList.get(0).get("role_name"));
					}
				}
			}
		}
	}

	/**
	 * 包装案件基本信息中的办理过程信息
	 * @param baseRecInfoDTO
	 * @param recID
	 */
	private void setProcessInfo(BaseRecInfoDTO baseRecInfoDTO, Object recID) {
		List<ProcessInfoDTO> processInfoList = jtBiz.query(SqlConst.getProcessSql,new BeanPropertyRowMapper<>(ProcessInfoDTO.class),recID);
		boolean hisFlag = false;
		if(processInfoList.size() == 0){
			//考虑历史案卷
			processInfoList = jtBiz.query(SqlConst.getHisProcessSql,new BeanPropertyRowMapper<>(ProcessInfoDTO.class),recID);
			hisFlag = true;
		}
		if(processInfoList.size()>0){
			Integer lastActID;
			try{
				if(hisFlag){
					lastActID = jtBiz.queryForObject("SELECT act_id from to_his_wf_act_inst where last_act_flag=1 and rec_id=" + recID, Integer.class);
				} else {
					lastActID = jtBiz.queryForObject("SELECT act_id from to_wf_act_inst where last_act_flag=1 and rec_id=" + recID, Integer.class);
				}
			} catch (Exception e){
				logger.error("to_wf_act_inst和to_his_wf_act_inst表中未找到last_act_flag为1的案卷信息，recID:{}",recID,e);
				lastActID = Integer.valueOf(processInfoList.get(processInfoList.size()-1).getActId());
			}
			for (ProcessInfoDTO processInfo : processInfoList) {
				if(processInfo.getActId().equals(String.valueOf(lastActID))){
					baseRecInfoDTO.setHandlerStatus(processInfo.getHandlerStatus());
				}
				List<Map<String, Object>> humanRoleList = jtBiz.queryForList("select * from tc_human_role where human_id=?", processInfo.getHandlerUserId());
				if (humanRoleList.size() > 0) {
					Object roleID = humanRoleList.get(0).get("role_id");
					processInfo.setHandlerUserRoleId(String.valueOf(roleID));
					List<Map<String, Object>> roleList = jtBiz.queryForList("select * from tc_role where role_id = ?", roleID);
					if (roleList.size() > 0) {
						processInfo.setHandlerUserRole((String) roleList.get(0).get("role_name"));
					}
				}
			}
			baseRecInfoDTO.setProcessActs(processInfoList);
		}
	}
	
	
	/**
	 * 更新推送案卷表
	 * 
	 * @param result
	 * @param recIDs
	 * @param synFlag
	 */
	private void updateRecTran(String result, Object recID,Integer synFlag,Integer archiveFlag) {
		Integer count = jtBiz.queryForObject("select count(1) from to_rec_transit where rec_id=" + recID, Integer.class);
		if(count==0){
			jtBiz.update("INSERT into to_rec_transit(rec_id,create_time,syn_flag,syn_date,sender_code,call_result,archive_flag) values (?,now(),?,now(),?,?,?)",recID,synFlag, senderCode,result,archiveFlag);
		} else if(count!=0){
			jtBiz.update("update to_rec_transit set syn_flag=?,syn_date=now(),call_result=?,archive_flag=? where rec_id=? and sender_code=?", synFlag,result,archiveFlag,recID, senderCode);
		}
	}
	
	/**
	 * 更新推送多媒体表
	 * @param result
	 * @param mediaIDs
	 * @param synFlag
	 */
	private void updateRecMediaTrans(String result, Object mediaID ,Integer synFlag) {
		Integer count = jtBiz.queryForObject("select count(1) from to_rec_media_transit where media_id=" + mediaID, Integer.class);
		if(count==0 && synFlag==0){
			jtBiz.update("INSERT into to_rec_media_transit(media_id,create_time,syn_flag,sender_code,call_result) values (?,now(),?,?,?)",mediaID,synFlag, senderCode,result);
		} else if(count!=0){
			jtBiz.update("update to_rec_media_transit set syn_flag=?,syn_date=now(),call_result=? where media_id=? and sender_code=?", synFlag,result,mediaID,senderCode);
		}
	}
	
	/**
	 * 推送未传送成功的多媒体
	 */
	@Override
	public void pushRecMediaTrans(String mediaUrl){
		List<Map<String, Object>> mapList = jtBiz.queryForList("select * from to_rec_media_transit where syn_flag=0 and sender_code=?", senderCode);
		for(Map<String, Object> map : mapList){
			Object mediaId = map.get("media_id");
			List<Map<String, Object>> list = jtBiz.queryForList("select * from to_media where media_id=? and delete_flag=0", mediaId);
			if(list.size()>0){
				Map<String, Object> media = list.get(0);
				String mediaName = (String) media.get("media_name");
				String filePath = media.get("media_path") + FILE_SEPERATOR + mediaName;
				InputStream inputStream = null;
				try {
					inputStream = HttpFileUtils.getFileInputStream(filePath, shareFileInfo);
					MockMultipartFile mockMultipartFile = new MockMultipartFile(mediaName,media.get("media_usage")+mediaName,"",inputStream);
					Map<String, MultipartFile> fileHashMap = new HashMap<>(1);
					fileHashMap.put("files",mockMultipartFile);
					Map<String, String> headParams = new HashMap<>(1);
					headParams.put("token",token);
					Map<String, String> otherParams = new HashMap<>(1);
					otherParams.put("eventId", String.valueOf(media.get("relation_id")));
					String resultStr = HttpClientPoolUtils.sendPostWithFile(mediaUrl, fileHashMap, otherParams, headParams);
					ResultInfo mediaResult = JSON.parseObject(resultStr, ResultInfo.class);
					if (mediaResult == null){
						logger.info("http再次上传图片失败，请求终止，请求url:{}",mediaUrl);
						break;
					} else if (mediaResult.isSuccess()) {
						logger.info("http再次上传图片成功，返回值：{}", resultStr);
						updateRecMediaTrans("http上传图片请求成功",mediaId,1);
					} else {
						logger.info("http上传图片失败，返回值：{}",resultStr);
					}
				} catch (IOException e) {
					logger.error("推送多媒体服务出错",e);
					break;
				}
			}
		}
	}

	@Override
	public void pushHisRecInfo(String recInfoUrl, String mediaUrl) {
		initRegionCode();
		if(PUSH_TO_CITY_BRAIN.equals(senderCode)){
			initToken();
		}
		//删除表中已推送完成并且已结案的案件，避免数据积压导致表过大
		int update = jtBiz.update("DELETE from to_rec_transit where syn_flag=1 and archive_flag=1");
		logger.info("删除已推送完成并且已结案的案件信息数量：{}",update);
		//加上推送失败syn_flag=0,避免失败已结案的案卷滞留
		List<Map<String, Object>> hisRecList = jtBiz.queryForList("SELECT * from to_his_rec a where a.rec_id in (select b.rec_id from to_rec_transit b where b.sender_code=? and (b.archive_flag=0 or b.syn_flag=0))", senderCode);
		logger.info("需更新【已结案】基本案件信息数据数量为：{}",hisRecList.size());
		if(hisRecList.size()==0){
			return;
		}
		buildDTOAndPush(recInfoUrl,mediaUrl,hisRecList,1);
	}

	@Override
	public void testRollback() {
		jtBiz.update("update to_rec_transit set call_result='哈哈哈哈回滚吗？' where rec_id=236498");
		int error = 1/0;
		//已验证@Transactional(value = "bizTransactionManager")事物有效
	}

}
