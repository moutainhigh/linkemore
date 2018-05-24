package cn.linkmore.security.client;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.feign.FeignConfiguration;
import cn.linkmore.security.client.hystrix.PersonClientHystrix;
import cn.linkmore.security.request.ReqCheck;
import cn.linkmore.security.request.ReqPerson;
import cn.linkmore.security.response.ResPersonRole;
import cn.linkmore.security.response.ResRole;
/**
 * 远程调用 - 账户
 * @author jiaohanbin
 * @version 2.0
 *
 */ 
@FeignClient(value = "security-server", path = "/person", fallback=PersonClientHystrix.class,configuration = FeignConfiguration.class)
public interface PersonClient {
	
	@RequestMapping(value = "/v2.0/save", method = RequestMethod.POST)
	@ResponseBody
	public void save(@RequestBody ReqPerson reqPerson);
	
	@RequestMapping(value = "/v2.0/update", method = RequestMethod.POST)
	@ResponseBody
	public void update(@RequestBody ReqPerson reqPerson);
	
	@RequestMapping(value = "/v2.0/delete", method = RequestMethod.POST)
	@ResponseBody
	public void delete(@RequestBody List<Long> ids);
	
	
	@RequestMapping(value = "/v2.0/check", method = RequestMethod.POST)
	@ResponseBody
	public Boolean check(@RequestBody ReqCheck reqCheck);
	
	@RequestMapping(value = "/v2.0/list", method = RequestMethod.POST)
	@ResponseBody
	public ViewPage list(@RequestBody ViewPageable pageable);
	
	@RequestMapping(value = "/v2.0/unlock", method = RequestMethod.GET)
	@ResponseBody
	public void unlock(@RequestParam("id") Long id);
	
	@RequestMapping(value = "/v2.0/role_list", method = RequestMethod.GET)
	@ResponseBody
	public List<ResRole> roleList();
	
	@RequestMapping(value = "/v2.0/bind", method = RequestMethod.GET)
	@ResponseBody
	public void bind(@RequestParam("id") Long id,@RequestParam("ids") String ids);
	
	@RequestMapping(value = "/v2.0/person_role_list", method = RequestMethod.POST)
	@ResponseBody
	public List<ResPersonRole> personRolList(@RequestParam("id") Long id);
	
}