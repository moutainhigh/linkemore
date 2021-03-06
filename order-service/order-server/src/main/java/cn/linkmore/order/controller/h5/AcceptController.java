package cn.linkmore.order.controller.h5;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.linkmore.bean.common.ResponseEntity;
import cn.linkmore.bean.exception.BusinessException;
import cn.linkmore.bean.exception.StatusEnum;
import cn.linkmore.order.controller.h5.request.ReqPayParm;
import cn.linkmore.order.controller.h5.request.ReqSerch;
import cn.linkmore.order.controller.h5.response.ResPayParm;
import cn.linkmore.order.controller.h5.response.ResSearch;
import cn.linkmore.order.service.RedirectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Controller -支付分发
 * 
 * @author 常磊
 * @version 2.0
 */
@Api(tags = "H5", description = "H5支付")
@Controller
@CrossOrigin
@RequestMapping("/h5")
public class AcceptController {

	@Autowired
	RedirectService redirectService;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@ApiOperation(value = "获取订单详情", notes = "获取订单详情", consumes = "application/json")
	@RequestMapping(value = "/g", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> serch(@RequestBody ReqSerch reqSerch, HttpServletRequest request)
			throws IOException {
		log.info("获取订单详情");
		ResponseEntity<ResSearch> response = null;
		try {
			ResSearch res =	redirectService.getOrder(reqSerch);
			response = ResponseEntity.success(res, request);
		} catch (BusinessException e) {
			response = ResponseEntity.fail(e.getStatusEnum(), request);
		} catch (Exception e) {
			e.getMessage();
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		return response;
	}
	
	@ApiOperation(value = "是否有订单", notes = "是否有订单", consumes = "application/json")
	@RequestMapping(value = "/n", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> serchPlate(@RequestBody ReqSerch reqSerch, HttpServletRequest request)
			throws IOException {
		log.info("是否有订单");
		ResponseEntity<Boolean> response = null;
		try {
			Boolean res =	redirectService.serchPlate(reqSerch);
			response = ResponseEntity.success(res, request);
		} catch (BusinessException e) {
			response = ResponseEntity.fail(e.getStatusEnum(), request);
		} catch (Exception e) {
			e.getMessage();
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		return response;
	}

	@ApiOperation(value = "二维码入口（Oath2授权获取openid）", notes = "Oath2授权获取openid", consumes = "application/json")
	@RequestMapping(value = "/d", method = RequestMethod.GET)
	public String distributed(@RequestParam Map<String, String> params, HttpServletRequest request) throws IOException {
		log.info("二维码入口");
		return redirectService.distributed(params, request);
	}

	@ApiIgnore
	@RequestMapping(value = "/a", method = RequestMethod.GET)
	public String auth(@RequestParam Map<String, String> params) throws IOException {
		log.info("转发到微信页面");
		params.put("type", "wx");
		return redirectService.auth(params);
	}

	
	@ApiOperation(value = "jsapi参数集", notes = "jsapi参数集", consumes = "application/json")
	@RequestMapping(value = "/o", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResPayParm> wxparm(@RequestBody ReqPayParm reqPayParm,HttpServletRequest request) throws IOException {
		log.info("获取jsapi参数集");		
		ResponseEntity<ResPayParm> response = null;
		try {
			ResPayParm res =	redirectService.wxparm(reqPayParm);
			response = ResponseEntity.success(res, request);
		} catch (BusinessException e) {
			response = ResponseEntity.fail(e.getStatusEnum(), request);
		} catch (Exception e) {
			e.getMessage();
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		return response;
	}
	
	@ApiIgnore
	@ResponseBody
	@RequestMapping(value = "/r", method = RequestMethod.POST)
	public void wxNotify(@RequestBody String payResult,HttpServletResponse response) throws IOException {
		log.info("wx通知");
		redirectService.wxNotify(payResult,response);
	}

	@ApiOperation(value = "表单参数集", notes = "jsapi参数集", consumes = "application/json")
	@RequestMapping(value = "/t", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> aliparm(@RequestBody ReqPayParm reqPayParm,HttpServletRequest request) throws IOException {
		log.info("获取jsapi参数集");
		ResponseEntity<String> response = null;
		try {
			String res =	redirectService.aliparm(reqPayParm);
			response = ResponseEntity.success(res, request);
		} catch (BusinessException e) {
			response = ResponseEntity.fail(e.getStatusEnum(), request);
		} catch (Exception e) {
			e.getMessage();
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		return response;
	}

	@ApiIgnore
	@RequestMapping(value = "/b", method = RequestMethod.GET)
	public String aliauth(@RequestParam Map<String, String> params) throws IOException {
		log.info("转发到支付宝");
		params.put("type", "zfb");
		return redirectService.auth(params);
	}

	@ApiIgnore
	@RequestMapping(value = "/m", method = RequestMethod.POST)
	public void aliNotify(@RequestParam Map<String, Object> params,HttpServletResponse response) throws IOException {
		log.info("ali通知");
	     redirectService.aliNotify(params, response);
	}
	
	@ApiIgnore
	@RequestMapping(value = "/f", method = RequestMethod.GET)
	public String returnIndex()  {
		log.info("转发到支付宝成功页面");
		//String retURI = "redirect:http://order.linkmoreparking.cn/#/paysuccess";//支付成功后的返回页面
		//String retURI = "redirect:http://qrcode.linkmoreparking.com/#/paysuccess";//支付成功后的返回页面
		String retURI = "redirect:http://qrcode.linkmoretech.cn/#/paysuccess";//支付成功后的返回页面
		return retURI;
	}

}
