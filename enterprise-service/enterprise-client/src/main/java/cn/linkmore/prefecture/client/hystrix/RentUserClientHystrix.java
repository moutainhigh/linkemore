package cn.linkmore.prefecture.client.hystrix;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.enterprise.request.ReqRentUser;
import cn.linkmore.prefecture.client.OpsRentUserClient;

/**
 * 长租用户
 * @author   GFF
 * @Date     2018年8月1日
 * @Version  v2.0
 */
@Component
public class RentUserClientHystrix implements OpsRentUserClient {

	private  final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public ViewPage findList(ViewPageable pageable) {
		log.info("enterprise service ViewPage findList(ViewPageable pageable) hystrix");
		return null;
	}

	@Override
	public void save(ReqRentUser user) {
		log.info("enterprise service void save(ReqRentUser user) hystrix");
		
	}

	@Override
	public void update(ReqRentUser user) {
		log.info("enterprise service void update(ReqRentUser user) hystrix");
		
	}

	@Override
	public void delete(List<Long> ids) {
		log.info("enterprise service void delete(List<Long> ids) hystrix");
		
	}
	
	
	
}