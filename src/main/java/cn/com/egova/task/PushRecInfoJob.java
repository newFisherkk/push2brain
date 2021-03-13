package cn.com.egova.task;

import cn.com.egova.service.PushBaseInfoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:config.properties")
public class PushRecInfoJob {
	
	@Autowired
	PushBaseInfoManager pushBaseInfoManager;

	@Value("${push.regionCode}")
	private String regionCode;
	
	@Value("${push.rec.baseInfo.url}")
	private String recInfoUrl;

	@Value("${push.rec.media.url}")
	private String mediaUrl;

	@Value("${push.rec.region.eval.url}")
	private String regionEvalUrl;

	@Value("${push.rec.unit.eval.url}")
	private String unitEvalUrl;
	
	/**
	 * 事件详情数据推送定时任务
	 */
	@Scheduled(cron= "${push.rec.baseInfo.cron}")
	public void pushBaseRecInfo(){
		pushBaseInfoManager.pushBaseRecInfo(regionCode,recInfoUrl,mediaUrl);
	}
	
	/**
	 * 事件区域评价数据推送定时任务
	 */
	@Scheduled(cron = "${push.eval.region.cron}")
	public void pushRegionEvalInfo(){
		pushBaseInfoManager.pushRegionEvalInfo(regionEvalUrl);
	}

	/**
	 * 事件部门评价数据推送定时任务
	 */
	@Scheduled(cron = "${push.eval.unit.cron}")
	public void pushUnitEvalInfo(){
		pushBaseInfoManager.pushUnitEvalInfo(unitEvalUrl);
	}
}
