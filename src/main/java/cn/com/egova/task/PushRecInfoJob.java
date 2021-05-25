package cn.com.egova.task;

import cn.com.egova.service.PushBaseInfoManager;
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
		pushBaseInfoManager.pushBaseRecInfo(recInfoUrl,mediaUrl);
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

	/**
	 * 推送已结案的案件信息
	 */
	@Scheduled(cron = "${push.eval.archive.cron}")
	public void pushHisRecInfo(){
		pushBaseInfoManager.pushHisRecInfo(recInfoUrl,mediaUrl);
	}
}
