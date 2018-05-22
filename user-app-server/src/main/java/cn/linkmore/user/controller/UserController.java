package cn.linkmore.user.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.linkmore.account.request.ReqUpdateVehicle;
import cn.linkmore.account.response.ResUser;
import cn.linkmore.account.response.ResUserDetails;
import cn.linkmore.bean.common.ResponseEntity;
import cn.linkmore.bean.exception.BusinessException;
import cn.linkmore.bean.exception.StatusEnum;
import cn.linkmore.user.request.ReqMobileBind;
import cn.linkmore.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
/**
 * Controller - 用户
 * @author liwenlong
 * @version 2.0
 *
 */
@Api(tags="User",description="用户信息")
@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService; 
	
	@ApiOperation(value="绑定手机号",notes="手机号不能为空,短信验证码不能为空", consumes = "application/json")
	@RequestMapping(value = "/v2.0/mobile", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> bindMobile(@RequestBody ReqMobileBind rmb, HttpServletRequest request){
		ResponseEntity<?> response = null; 
		try {
			this.userService.bindMobile(rmb,request);
			response = ResponseEntity.success(null, request);
		}catch(BusinessException e){
			e.printStackTrace();
			response = ResponseEntity.fail(e.getStatusEnum(), request);
		}catch(Exception e){
			e.printStackTrace();
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		return response;
	}
	
	@ApiOperation(value="发短信验证码",notes="手机号不能为空", consumes = "application/json")
	@RequestMapping(value = "/v2.0/sms", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> sms(@RequestParam(value="mobile" ,required=true) String mobile,HttpServletRequest request){
		ResponseEntity<?> response = null; 
		this.userService.send(mobile,request);
		return response;
	}
	

	/**
	 * @Description  更新昵称
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@ApiOperation(value="更新昵称",notes="昵称不能为空，用户需要登录", consumes = "application/json")
	@RequestMapping(value = "/v2.0/nickname", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> updateNickname(@RequestParam("nickname") String nickname,HttpServletRequest request) {
		this.userService.updateNickname(nickname,request);
		return new ResponseEntity<>();
	}
	
	/**
	 * @Description  更新性别
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@ApiOperation(value="更新性别",notes="性别不能为空，用户需要登录", consumes = "application/json")
	@RequestMapping(value = "/v2.0/sex", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> updateSex(@RequestParam("sex") Integer sex,HttpServletRequest request) {
		this.userService.updateSex(sex,request);
		return new ResponseEntity<>();
	}
	
	/**
	 * @Description  更新车牌号
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@ApiOperation(value="更新车牌号",notes="车牌号不能为空，用户需要登录", consumes = "application/json")
	@RequestMapping(value = "/v2.0/vehicle", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> updateVehicle(@RequestBody ReqUpdateVehicle vehicle,HttpServletRequest request) {
		this.userService.updateVehicle(vehicle,request);
		return new ResponseEntity<>();
	}
	
	
	/**
	 * @Description  查询详情
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@ApiOperation(value="查询详情",notes="用户需要登录", consumes = "application/json")
	@RequestMapping(value = "/v2.0/detail", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> detail(HttpServletRequest request) {
		ResUserDetails details = this.userService.detail(request);
		ResponseEntity<ResUserDetails> response = new ResponseEntity<>();
		response.setData(details);
		return response;
	}
	
	/**
	 * @Description  删除微信号
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@ApiOperation(value="删除微信号",notes="用户需要登录", consumes = "application/json")
	@RequestMapping(value = "/v2.0/wechat", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> removeWechat(HttpServletRequest request) {
		this.userService.removeWechat(request);
		ResponseEntity<?> response = ResponseEntity.success(null, request);
		return response;
	}
	
	/**
	 * @Description  根据手机号查询
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@ApiOperation(value="根据手机号查询",notes="用户需要登录", consumes = "application/json")
	@RequestMapping(value = "/v2.0/mobile", method = RequestMethod.GET)
	public ResponseEntity<?> selectByMobile(@RequestParam("mobile") String mobile,HttpServletRequest request) {
		System.out.println(request.getSession().getId());
		ResponseEntity<?> response = null; 
		ResUser user = this.userService.selectByMobile(mobile);
		response = ResponseEntity.success(user, request);
		return response;
	}
	
}
