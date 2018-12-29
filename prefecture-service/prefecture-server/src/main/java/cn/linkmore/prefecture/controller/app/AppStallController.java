package cn.linkmore.prefecture.controller.app;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import cn.linkmore.bean.common.ResponseEntity;
import cn.linkmore.bean.exception.BusinessException;
import cn.linkmore.bean.exception.StatusEnum;
import cn.linkmore.prefecture.service.StallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * Controller - 临停车位
 * 
 * @author jiaohanbin
 * @version 2.0
 *
 */
@Api(tags = "Stall", description = "扫码降锁停车")
@RestController
@RequestMapping("/app/stall")
public class AppStallController {

	@Autowired
	private StallService stallService;


	@ApiOperation(value = "用户操作车位锁", notes = "用户操作车位锁", consumes = "application/json")
	@RequestMapping(value = "/v2.0/control", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Boolean> controlLock(@Validated @RequestParam(value="stallId", required=true) Long stallId ,HttpServletRequest request) {
		ResponseEntity<Boolean> response = null;
		 try {
			 boolean flag = stallService.control(stallId, request);
			 response = ResponseEntity.success(flag, request);
		}  catch (BusinessException e) {
			response = ResponseEntity.fail( e.getStatusEnum(),  request);
		} catch (Exception e) { 
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		 return response;
	}

	@ApiOperation(value = "查看操作结果", notes = "查看操作结果", consumes = "application/json")
	@RequestMapping(value = "/v2.0/watch", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Boolean> watch(@Validated @RequestParam(value="stallId", required=true) Long stallId,
			HttpServletRequest request) {
		ResponseEntity<Boolean> response = null;
		 try {
			 stallService.watchDownResult(stallId,request);
			 response  =	 ResponseEntity.success(true, request);
		}  catch (BusinessException e) {
			response = ResponseEntity.fail( e.getStatusEnum(),  request);
		} catch (Exception e) { 
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		 return response;
	}
}
