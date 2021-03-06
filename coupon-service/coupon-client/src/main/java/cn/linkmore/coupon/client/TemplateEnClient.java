package cn.linkmore.coupon.client;

import java.util.List;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.coupon.client.hystrix.TemplateEnClientHystrix;
import cn.linkmore.coupon.request.ReqCheck;
import cn.linkmore.coupon.request.ReqTemplate;
import cn.linkmore.coupon.response.ResTemplate;
import cn.linkmore.feign.FeignConfiguration;
/**
 * Client  - 企业停车券
 * @author jiaohanbin
 * @version 2.0
 *
 */
@FeignClient(value = "coupon-server", path = "/ops/coupon_enterprise", fallback=TemplateEnClientHystrix.class,configuration = FeignConfiguration.class)
public interface TemplateEnClient {
	
	@RequestMapping(value = "/v2.0/save", method = RequestMethod.POST)
	@ResponseBody
	public int save(@RequestBody ReqTemplate record);
	@RequestMapping(value = "/v2.0/saveBusiness", method = RequestMethod.POST)
	@ResponseBody
	public int saveBusiness(@RequestBody ReqTemplate record);

	@RequestMapping(value = "/v2.0/detail", method = RequestMethod.GET)
	@ResponseBody
	public ResTemplate detail(@RequestParam("id") Long id) ;

	@RequestMapping(value = "/v2.0/update", method = RequestMethod.POST)
	@ResponseBody
	public int update(@RequestBody ReqTemplate record);

	@RequestMapping(value = "/v2.0/delete", method = RequestMethod.POST)
	@ResponseBody
	public int delete(@RequestBody List<Long> ids);

	@RequestMapping(value = "/v2.0/check", method = RequestMethod.POST)
	@ResponseBody
	public Boolean check(ReqCheck reqCheck);

	@RequestMapping(value = "/v2.0/list", method = RequestMethod.POST)
	@ResponseBody
	public ViewPage list(@RequestBody ViewPageable pageable) ;
	
	@RequestMapping(value = "/v2.0/listBusiness", method = RequestMethod.POST)
	@ResponseBody
	public ViewPage listBusiness(@RequestBody ViewPageable pageable);

	/*
	 * 启用
	 */
	@RequestMapping(value = "/v2.0/start", method = RequestMethod.GET)
	@ResponseBody
	public int start(@RequestParam("id") Long id);
	/*
	 * 禁用
	 */
	@RequestMapping(value = "/v2.0/stop", method = RequestMethod.GET)
	@ResponseBody
	public int down(@RequestParam("id") Long id);
	
	@RequestMapping(value = "/v2.0/selectByEnterpriseId", method = RequestMethod.GET)
	@ResponseBody
	public List<ResTemplate> findByEnterpriseId(@RequestParam("id") Long id);
	
}
