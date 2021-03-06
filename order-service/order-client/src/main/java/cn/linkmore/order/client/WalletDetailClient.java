package cn.linkmore.order.client;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.feign.FeignConfiguration;
import cn.linkmore.order.client.hystrix.WalletDetailClientHystrix;
import cn.linkmore.order.request.ReqWalletDetailExport;
import cn.linkmore.order.response.ResWalletDetailExport;
 
/**
 * 充值明细--远程调用
 * @author   GFF
 * @Date     2018年5月18日
 * @Version  v2.0
 */
@FeignClient(value = "order-server", path = "/feign/wallet_detail", fallback=WalletDetailClientHystrix.class,configuration = FeignConfiguration.class)
public interface WalletDetailClient {
	
	/**
	 * @Description  分页查询
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/page", method = RequestMethod.POST)
	public ViewPage list(@RequestBody ViewPageable pageable);

	/**
	 * @Description  根据条件查询导出数据
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/v2.0/export", method = RequestMethod.POST)
	public List<ResWalletDetailExport> getListByTime(@RequestBody ReqWalletDetailExport bean);

	
}
