package cn.linkmore.account.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.linkmore.account.client.hystrix.UserClientHystrix;
import cn.linkmore.account.request.ReqBind;
import cn.linkmore.account.request.ReqUpdateMobile;
import cn.linkmore.account.request.ReqUpdateNickname;
import cn.linkmore.account.request.ReqUpdateSex;
import cn.linkmore.account.request.ReqUpdateVehicle;
import cn.linkmore.account.request.ReqUserAppfans;
import cn.linkmore.account.response.ResUser;
import cn.linkmore.account.response.ResUserDetails;
import cn.linkmore.account.response.ResUserLogin;
import cn.linkmore.account.response.ResVechicleMark;
import cn.linkmore.feign.FeignConfiguration;
 
/**
 * 用户
 * @author   GFF
 * @Date     2018年5月18日
 * @Version  v2.0
 */
@FeignClient(value = "account-server", path = "/user", fallback=UserClientHystrix.class,configuration = FeignConfiguration.class)
public interface UserClient {
	
	
	/**
	 * @Description	更新昵称  
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/nickname", method = RequestMethod.PUT)
	@ResponseBody
	public void updateNickname(@RequestBody ReqUpdateNickname nickname);
	
	/**
	 * @Description  更新性别
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/sex", method = RequestMethod.PUT)
	@ResponseBody
	public void updateSex(@RequestBody ReqUpdateSex reqSex);
	
	/**
	 * @Description  更新车牌号
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/vehicle", method = RequestMethod.PUT)
	public void updateVehicle(@RequestBody ReqUpdateVehicle req);
	
	/**
	 * @Description  查询详情
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/detail/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public ResUserDetails detail(@PathVariable("userId") Long userId) ;
	
	/**
	 * @Description  根据手机号查询
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/mobile/{mobile}", method = RequestMethod.GET)
	public ResUser selectByMobile(@PathVariable("mobile") String mobile);
	
	/**
	 * @Description  发送短信
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/send_code", method = RequestMethod.GET)
	@ResponseBody
	public void sendCode( @RequestBody ReqBind bean) ;

	/**
	 * @Description  更新手机号
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/mobile", method = RequestMethod.PUT)
	@ResponseBody
	public void updateMobile(@RequestBody ReqUpdateMobile bean);
	
	/**
	 * @Description  更新微信
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/wechat", method = RequestMethod.PUT)
	@ResponseBody
	public void updateAppfans(@RequestBody ReqUserAppfans bean) ;
	
	/**
	 * @Description  删除微信
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/wechat/{userId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void removeWechat(@PathVariable("userId") Long userId);
	
	/**
	 * @Description  查询缓存用户
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/cache/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public ResUser getUserCacheKey(@PathVariable("userId") Long userId);

	/**
	 * @Description app登录
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/login/{mobile}", method = RequestMethod.GET)
	@ResponseBody
	public ResUserLogin appLogin(@PathVariable("mobile") String mobile);

	/**
	 * @Description  微信登录
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value="/v2.0/login",method = RequestMethod.POST)
	public ResUserLogin wxLogin(@RequestBody ReqUserAppfans appfans);
	
	/**
	 * @Description  根据id查询
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/{id}", method = RequestMethod.GET)
	public ResVechicleMark selectById(@PathVariable("id") Long id);
}