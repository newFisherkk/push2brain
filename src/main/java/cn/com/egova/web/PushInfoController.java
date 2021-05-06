package cn.com.egova.web;

import cn.com.egova.service.PushBaseInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PushInfoController {
	
	@Autowired
	PushBaseInfoManager pushBaseInfoManager;
	
	
	@PostMapping(value = "/pushrec")
	private void pushRec(String recInfoUrl, String mediaUrl){
		pushBaseInfoManager.pushBaseRecInfo(recInfoUrl,mediaUrl);
	}

	@PostMapping(value = "/pushregioneval")
	private void pushRegionEval(String regionEvalUrl){
		pushBaseInfoManager.pushRegionEvalInfo(regionEvalUrl);
	}

	@PostMapping(value = "/pushuniteval")
	private void pushUnitEval(String unitEvalUrl){
		pushBaseInfoManager.pushUnitEvalInfo(unitEvalUrl);
	}

	@PostMapping(value = "/pushfailmedia")
	private void pushMedia(String mediaUrl){
		pushBaseInfoManager.pushRecMediaTrans(mediaUrl);
	}
	
}
