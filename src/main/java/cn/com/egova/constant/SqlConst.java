package cn.com.egova.constant;



public class SqlConst {

	public static final String getRegionEvalSql = "SELECT district_id regionId,district_name regionName,sum(report_num) reportCount,sum(verify_num) verifyCount," +
			"sum(operate_num) handlerCount,sum(inst_num) filingCount,sum(dispatch_num) dispatchCount,sum(check_num) checkCount,sum(archive_num) closedCount," +
			"SUM(dispose_num)/sum(need_dispose_num) disposalRate,SUM(archive_num)/sum(need_archive_num) closedRate," +
			"SUM(valid_public_report_num)/sum(valid_report_num) publicReportRate,SUM(intime_archive_num)/sum(need_archive_num) closedInTimeRate," +
			"LEFT (create_time_month, 4) year, RIGHT (create_time_month, 2) month FROM to_stat_info WHERE  1 = 1 AND district_name IS NOT NULL GROUP BY  DISTRICT_NAME ORDER BY DISTRICT_NAME";
	
	public static final String getUnitEvalSql = "SELECT dispose_unit_id unitId,dispose_unit_name unitName,sum(need_dispose_num) needHandlerCount," +
			"sum(intime_dispose_num) handlerInTimeCount,sum(intime_dispose_num) / sum(need_dispose_num) handlerInTimeRate,sum(rework_num) reworkCount," +
			"sum(rework_num) / sum(through_dispose_num) reworkRate,((sum(intime_dispose_num) / sum(need_dispose_num))*60 + (sum(archive_num) / sum(need_archive_num))*30 + (1 - (sum(rework_num) / sum(through_dispose_num)))*10) comIndex," +
			"LEFT (create_time_month, 4) year,RIGHT (create_time_month, 2) month " +
			"FROM  to_stat_info WHERE  1 = 1 AND dispose_unit_name IS NOT NULL GROUP BY  dispose_unit_name ORDER BY dispose_unit_name";
	
	public static final String getProcessSql = "SELECT a.act_id actId,a.rec_id eventId,a.pre_act_id seniorActId,a.act_def_name actName,a.act_def_name handlerStatus,a.human_id handlerUserId,a.human_name handlerUser,h.tel_mobile handlerUserUhone," +
			"h.unit_id handlerUserUnitId,h.unit_name handlerUserUnit,a.pre_act_opinion handlerOpinion," +
			"a.create_time handlerStartTime,a.refresh_time handlerEndTime,a.act_deadline_char handlerLimitTime,(case when a.deadline_time is null or a.deadline_time < a.refresh_time then '已超期' else '未超期' end) handlerOverTime" +
			" FROM to_wf_act_inst a,tc_human h" +
			" WHERE h.human_id = a.human_id and a.rec_id=?";

	public static final String getHisProcessSql = "SELECT a.act_id actId,a.rec_id eventId,a.pre_act_id seniorActId,a.act_def_name actName,a.act_def_name handlerStatus,a.human_id handlerUserId,a.human_name handlerUser,h.tel_mobile handlerUserUhone," +
			"h.unit_id handlerUserUnitId,h.unit_name handlerUserUnit,a.pre_act_opinion handlerOpinion," +
			"a.create_time handlerStartTime,a.refresh_time handlerEndTime,a.act_deadline_char handlerLimitTime,(case when a.deadline_time is null or a.deadline_time < a.refresh_time then '已超期' else '未超期' end) handlerOverTime" +
			" FROM to_his_wf_act_inst a,tc_human h" +
			" WHERE h.human_id = a.human_id and a.rec_id=?";
}
