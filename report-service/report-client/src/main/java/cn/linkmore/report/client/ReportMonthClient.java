package cn.linkmore.report.client;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.linkmore.feign.FeignConfiguration;
import cn.linkmore.report.client.hystrix.ReportMonthClientHystrix;
import cn.linkmore.report.request.ReqReportMonth;
import cn.linkmore.report.response.ResAveragePrice;
import cn.linkmore.report.response.ResCity;
import cn.linkmore.report.response.ResCost;
import cn.linkmore.report.response.ResIncome;
import cn.linkmore.report.response.ResNewUser;
import cn.linkmore.report.response.ResOrder;
import cn.linkmore.report.response.ResPre;
import cn.linkmore.report.response.ResPull;
import cn.linkmore.report.response.ResPullCost;
import cn.linkmore.report.response.ResRunTime;
import cn.linkmore.report.response.ResStallAverage;
import cn.linkmore.report.response.ResUserNum;
/**
 * 远程调用 - 月报
 * @author jiaohanbin
 * @version 2.0
 *
 */ 
@FeignClient(value = "report-server", path = "/ops/report_month", fallback=ReportMonthClientHystrix.class,configuration = FeignConfiguration.class)
public interface ReportMonthClient {
	
	@RequestMapping(value = "/v2.0/city_list", method = RequestMethod.GET)
	@ResponseBody
	public List<ResCity> cityList();
	
	@RequestMapping(value = "/v2.0/pre_list", method = RequestMethod.POST)
	@ResponseBody
	public List<ResPre> preList(@RequestBody Map<String,Object> param);
	
	@RequestMapping(value = "/v2.0/total_count", method = RequestMethod.GET)
	@ResponseBody
	public Integer totalCount();
	
	@RequestMapping(value = "/v2.0/user_num", method = RequestMethod.POST)
	@ResponseBody
	public List<ResUserNum> userNumList(@RequestBody ReqReportMonth reportMonth);
	
	@RequestMapping(value = "/v2.0/new_user", method = RequestMethod.POST)
	@ResponseBody
	public List<ResNewUser> newUserList(@RequestBody ReqReportMonth reportMonth);
	
	@RequestMapping(value = "/v2.0/pull", method = RequestMethod.POST)
	@ResponseBody
	public List<ResPull> pullList(@RequestBody ReqReportMonth reportMonth);
	
	@RequestMapping(value = "/v2.0/stall_average", method = RequestMethod.POST)
	@ResponseBody
	public List<ResStallAverage> stallAverageList(@RequestBody ReqReportMonth reportMonth);
	
	@RequestMapping(value = "/v2.0/order", method = RequestMethod.POST)
	@ResponseBody
	public List<ResOrder> orderList(@RequestBody ReqReportMonth reportMonth);
	
	@RequestMapping(value = "/v2.0/yl_order", method = RequestMethod.POST)
	@ResponseBody
	public List<ResOrder> ylOrderList(@RequestBody ReqReportMonth reportMonth);
	
	@RequestMapping(value = "/v2.0/newuser_order", method = RequestMethod.POST)
	@ResponseBody
	public List<ResOrder> newUserOrderList(@RequestBody ReqReportMonth reportMonth);
	
	@RequestMapping(value = "/v2.0/olduser_order", method = RequestMethod.POST)
	@ResponseBody
	public List<ResOrder> oldUserOrderList(@RequestBody ReqReportMonth reportMonth);
	
	@RequestMapping(value = "/v2.0/runtime", method = RequestMethod.POST)
	@ResponseBody
	public List<ResRunTime> runtimeList(@RequestBody ReqReportMonth reportMonth);
	
	@RequestMapping(value = "/v2.0/average_price", method = RequestMethod.POST)
	@ResponseBody
	public List<ResAveragePrice> averagePriceList(@RequestBody ReqReportMonth reportMonth);
	
	@RequestMapping(value = "/v2.0/cost", method = RequestMethod.POST)
	@ResponseBody
	public List<ResCost> costList(@RequestBody ReqReportMonth reportMonth);
	
	@RequestMapping(value = "/v2.0/income", method = RequestMethod.POST)
	@ResponseBody
	public List<ResIncome> incomeList(@RequestBody ReqReportMonth reportMonth);
	
	@RequestMapping(value = "/v2.0/pull_cost", method = RequestMethod.POST)
	@ResponseBody
	public List<ResPullCost> pullCostList(@RequestBody ReqReportMonth reportMonth);
	
}
