package cn.linkmore.user.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.linkmore.bean.common.ResponseEntity;
import cn.linkmore.bean.exception.BusinessException;
import cn.linkmore.bean.exception.StatusEnum;
import cn.linkmore.user.request.ReqPayConfirm;
import cn.linkmore.user.response.ResPayCheckout;
import cn.linkmore.user.response.ResPayConfirm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Controller - 支付
 * @author liwenlong
 * @version 2.0
 *
 */
@Api(tags="Pay",description="订单结账")
@RestController
@RequestMapping("/pay")
public class PayController {
	
	@ApiOperation(value = "生成账单", notes = "生成账单[订单ID不为空]", consumes = "application/json")
	@RequestMapping(value = "/v2.0/checkout", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ResPayCheckout> checkout(@RequestParam("orderId") Long orderId, HttpServletRequest request) {
		ResponseEntity<ResPayCheckout> response = null;
		try {
			
			response = ResponseEntity.success(null, request);
		} catch (BusinessException e) { 
			response = ResponseEntity.fail(e.getStatusEnum(), request);
		} catch (Exception e) { 
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		return response;
	}
	@ApiOperation(value = "确认支付", notes = "确认支付", consumes = "application/json")
	@RequestMapping(value = "/v2.0/confirm", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResPayConfirm> confirm(@RequestBody ReqPayConfirm roc, HttpServletRequest request) {
		ResponseEntity<ResPayConfirm> response = null;
		try {
			
			response = ResponseEntity.success(null, request);
		} catch (BusinessException e) { 
			response = ResponseEntity.fail(e.getStatusEnum(), request);
		} catch (Exception e) { 
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		return response;
	}
	
	@ApiOperation(value = "校验支付", notes = "校验支付[订单ID不为空]", consumes = "application/json")
	@RequestMapping(value = "/v2.0/verify", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> verify(@RequestParam("orderId") Long orderId, HttpServletRequest request) {
		ResponseEntity<?> response = null;
		try { 
			response = ResponseEntity.success(null, request);
		} catch (BusinessException e) { 
			response = ResponseEntity.fail(e.getStatusEnum(), request);
		} catch (Exception e) { 
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		return response;
	}
}
