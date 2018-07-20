package cn.linkmore.prefecture.client;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.linkmore.bean.view.Tree;
import cn.linkmore.enterprise.request.ReqStaffAuthBind;
import cn.linkmore.feign.FeignConfiguration;
import cn.linkmore.prefecture.client.hystrix.OpsStaffAuthClientHystrix;
/**
 * 远程调用 - 企业优惠劵
 * @author jiaohanbin
 * @version 2.0
 *
 */ 
@FeignClient(value = "enterprise-server", path = "/ops/staff/auth", fallback=OpsStaffAuthClientHystrix.class,configuration = FeignConfiguration.class)
public interface OpsStaffAuthClient {
	
	
	@RequestMapping(value="/bind",method=RequestMethod.POST)
	@ResponseBody
	public void bind(@RequestBody ReqStaffAuthBind staff);

	@RequestMapping(value="/tree",method=RequestMethod.GET)
	@ResponseBody
	public Tree tree();
	
	@RequestMapping(value="/resouce",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> resouce(@RequestParam("id")Long staffId);
}
