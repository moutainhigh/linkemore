package cn.linkmore.common.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.linkmore.bean.common.security.Token;
import cn.linkmore.common.response.ResVersionBean;
import cn.linkmore.common.service.BeanVersionService;

@RestController
@RequestMapping("version")
public class BeanVersionController {
	
	@Resource
	private BeanVersionService beanVersionService;
	
	
	@RequestMapping(value="/current/{requestSource}",method = RequestMethod.GET)
	@ResponseBody
	public ResVersionBean current(@PathParam("requestSource")Short requestSource){
		int appType = 0;
		if(Token.OS_ANDROID == requestSource){
			appType=1;
		}else if(Token.OS_IOS == requestSource){
			appType = 2;
		}
		ResVersionBean app = this.beanVersionService.currentAppVersion(appType);
		return app;
	}
	
	
	/*@RequestMapping(method = RequestMethod.GET)
	public void report(HttpServletRequest request,@RequestBody ReqVersion vrb){
		this.beanVersionService.report(vrb);
	}*/
}
