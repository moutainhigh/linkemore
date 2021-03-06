package cn.linkmore.order.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.feign.FeignConfiguration;
import cn.linkmore.order.client.hystrix.OrderClientHystrix;
import cn.linkmore.order.request.ReqDataCount;
import cn.linkmore.order.request.ReqOrderExcel;
import cn.linkmore.order.response.ResOrder;
import cn.linkmore.order.response.ResOrderExcel;
import cn.linkmore.order.response.ResOrderOperateLog;
import cn.linkmore.order.response.ResUserOrder;

@FeignClient(value = "order-server", path = "/feign/orders", fallback=OrderClientHystrix.class,configuration = FeignConfiguration.class)
public interface OrderClient { 
	
	/**
	 * 最近订单
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/v2.0/last", method = RequestMethod.GET)
	@ResponseBody
	ResUserOrder last(@RequestParam("userId") Long userId); 
	
	/**
	 * @Description  消息推送
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/down-msg-push", method = RequestMethod.POST)
	@ResponseBody
	void downMsgPush(@RequestParam("orderId")Long orderId, @RequestParam("stallId")Long stallId);
	
	/**
	 * @Description  分页查询
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public ViewPage findPage(@RequestBody ViewPageable pageable);

	@RequestMapping(value = "/export", method = RequestMethod.POST)
	@ResponseBody
	public List<ResOrderExcel> exportList(@RequestBody ReqOrderExcel bean);
	
	/**
	 * 当前车牌号最近订单状态 是否存在未结账订单
	 * @param carno
	 * @return
	 */
	@RequestMapping(value = "/v2.0/last-order", method = RequestMethod.GET)
	@ResponseBody
	Integer getPlateLastOrderStatus(@RequestParam("carno") String carno);

	@RequestMapping(value = "/v2.0/by-id", method = RequestMethod.GET)
	@ResponseBody
	ResOrder findById(@RequestParam("id")Long id); 
	

	
	@RequestMapping(value = "/stall-latest", method = RequestMethod.GET)
	@ResponseBody
	public ResUserOrder findStallLatest(@RequestParam("stallId") Long stallId);
	
	
	@RequestMapping(value = "/findOrderById", method = RequestMethod.GET)
	@ResponseBody
	public ResUserOrder findOrderById(@RequestParam("id") Long id);
	

	@RequestMapping(value = "/v2.0/update-lock-status", method = RequestMethod.POST)
	@ResponseBody
	public void updateLockStatus(@RequestBody Map<String, Object> param);

	@RequestMapping(value = "/updateClose", method = RequestMethod.POST)
	@ResponseBody
	public void updateClose(@RequestBody Map<String, Object> param );
	
	@RequestMapping(value = "/update-order-detail", method = RequestMethod.POST)
	@ResponseBody
	public void updateDetail(@RequestBody Map<String, Object> param );
	
	@RequestMapping(value = "/save-order-log", method = RequestMethod.POST)
	@ResponseBody
	public void savel(@RequestBody  ResOrderOperateLog resOrderOperateLog);
	
	@RequestMapping(value = "/save-virtual-data", method = RequestMethod.POST)
	@ResponseBody
	public void saveVirtualData(@RequestBody ReqDataCount copyObject);

	@RequestMapping(value = "/stop", method = RequestMethod.GET)
	@ResponseBody
	void stop();
	@RequestMapping(value = "/start", method = RequestMethod.GET)
	@ResponseBody
	void start();
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	@ResponseBody
	void delete(@RequestBody Long ids);
	
}
