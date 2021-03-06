package cn.linkmore.report.client.hystrix;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import cn.linkmore.report.client.ReportMonthClient;
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
 * 远程调用实现 - 月报信息
 * @author jiaohanbin
 * @version 2.0
 *
 */ 
@Component
public class ReportMonthClientHystrix implements ReportMonthClient {
	private  final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public List<ResCity> cityList() {
		log.info("report-server cityList () hystrix");
		return null;
	}

	@Override
	public List<ResPre> preList(Map<String, Object> param) {
		log.info("report-server preList () hystrix");
		return null;
	}

	@Override
	public Integer totalCount() {
		log.info("report-server totalCount () hystrix");
		return null;
	}

	@Override
	public List<ResUserNum> userNumList(ReqReportMonth reportMonth) {
		log.info("report-server userNumList () hystrix");
		return null;
	}

	@Override
	public List<ResNewUser> newUserList(ReqReportMonth reportMonth) {
		log.info("report-server newUserList () hystrix");
		return null;
	}

	@Override
	public List<ResPull> pullList(ReqReportMonth reportMonth) {
		log.info("report-server pullList () hystrix");
		return null;
	}

	@Override
	public List<ResStallAverage> stallAverageList(ReqReportMonth reportMonth) {
		log.info("report-server stallAverageList () hystrix");
		return null;
	}

	@Override
	public List<ResOrder> orderList(ReqReportMonth reportMonth) {
		log.info("report-server orderList () hystrix");
		return null;
	}

	@Override
	public List<ResOrder> ylOrderList(ReqReportMonth reportMonth) {
		log.info("report-server ylOrderList () hystrix");
		return null;
	}

	@Override
	public List<ResOrder> newUserOrderList(ReqReportMonth reportMonth) {
		log.info("report-server newUserOrderList () hystrix");
		return null;
	}

	@Override
	public List<ResOrder> oldUserOrderList(ReqReportMonth reportMonth) {
		log.info("report-server oldUserOrderList () hystrix");
		return null;
	}

	@Override
	public List<ResRunTime> runtimeList(ReqReportMonth reportMonth) {
		log.info("report-server runtimeList () hystrix");
		return null;
	}

	@Override
	public List<ResAveragePrice> averagePriceList(ReqReportMonth reportMonth) {
		log.info("report-server averagePriceList () hystrix");
		return null;
	}

	@Override
	public List<ResCost> costList(ReqReportMonth reportMonth) {
		log.info("report-server costList () hystrix");
		return null;
	}

	@Override
	public List<ResIncome> incomeList(ReqReportMonth reportMonth) {
		log.info("report-server incomeList () hystrix");
		return null;
	}

	@Override
	public List<ResPullCost> pullCostList(ReqReportMonth reportMonth) {
		log.info("report-server pullCostList () hystrix");
		return null;
	}
}
