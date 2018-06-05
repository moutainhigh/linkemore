package cn.linkmore.account.client.hystrix;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.linkmore.account.client.WalletDetailClient;
import cn.linkmore.account.request.ReqWalletDetailExport;
import cn.linkmore.account.response.ResWalletDetailExport;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;

/**
 * 充值明细--容错
 * @author   GFF
 * @Date     2018年5月30日
 * @Version  v2.0
 */
@Component
public class WalletDetailClientHystrix implements WalletDetailClient {
	private  final Logger log = LoggerFactory.getLogger(this.getClass());
	@Override
	public ViewPage list(ViewPageable pageable) {
		log.info("account service walletDetail list(ViewPageable pageable) hystrix");
		return null;
	}
	@Override
	public List<ResWalletDetailExport> getListByTime(ReqWalletDetailExport bean) {
		log.info("account service walletDetail getListByTime(ReqWalletDetailExport bean) hystrix");
		return null;
	}
	
	

}
