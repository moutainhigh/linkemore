package cn.linkmore.security.client;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.linkmore.bean.view.Tree;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.feign.FeignConfiguration;
import cn.linkmore.security.client.hystrix.InterfaceClientHystrix;
import cn.linkmore.security.request.ReqCheck;
import cn.linkmore.security.request.ReqInterface;
import cn.linkmore.security.response.ResInterface;
/**
 * 远程调用 - 接口
 * @author jiaohanbin
 * @version 2.0
 *
 */ 
@FeignClient(value = "security-server", path = "/interface", fallback=InterfaceClientHystrix.class,configuration = FeignConfiguration.class)
public interface InterfaceClient {
	
	@RequestMapping(value = "/v2.0/save", method = RequestMethod.POST)
	@ResponseBody
	public int save(@RequestBody ReqInterface reqInterface);
	
	@RequestMapping(value = "/v2.0/update", method = RequestMethod.POST)
	@ResponseBody
	public int update(@RequestBody ReqInterface reqInterface);
	
	@RequestMapping(value = "/v2.0/delete", method = RequestMethod.POST)
	@ResponseBody
	public int delete(@RequestBody List<Long> ids);
	
	
	@RequestMapping(value = "/v2.0/check", method = RequestMethod.POST)
	@ResponseBody
	public Boolean check(@RequestBody ReqCheck reqCheck);
	
	@RequestMapping(value = "/v2.0/list", method = RequestMethod.POST)
	@ResponseBody
	public ViewPage list(@RequestBody ViewPageable pageable);
	
	@RequestMapping(value = "/v2.0/tree", method = RequestMethod.GET)
	@ResponseBody
	public Tree tree();
	
	@RequestMapping(value = "/v2.0/findAll", method = RequestMethod.GET)
	@ResponseBody
	public List<ResInterface> findAll();

	@RequestMapping(value = "/v2.0/person_auth_list", method = RequestMethod.GET)
	@ResponseBody
	public List<ResInterface> findPersonAuthList(@RequestParam("id") Long id);
	
}
