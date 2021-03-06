package cn.linkmore.common.client.hystrix;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import cn.linkmore.common.client.BaseDictClient;
import cn.linkmore.common.request.ReqBaseDict;
import cn.linkmore.common.response.ResBaseDict;
import cn.linkmore.common.response.ResOldDict;

/**
 * 数据词典容错
 * @author   GFF
 * @Date     2018年5月18日
 * @Version  v2.0
 */
@Component
public class BaseDictClientHystrix implements BaseDictClient {
	
	private  final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public List<ResBaseDict> findList(@PathVariable("code") String code) {
		log.info("common service dict  findList(String code) hystrix");
		return null;
	}

	@Override
	public List<ResBaseDict> findListByCodes(List<String> codes) {
		log.info("common service dict  findListByCodes(List<String> codes) hystrix");
		return null;
	}
	@Override
	public void save(ReqBaseDict baseDict) {
		log.info("common service dict  save(ReqBaseDict baseDict) hystrix");
	}

	@Override
	public void update(ReqBaseDict baseDict) {
		log.info("common service dict  update(ReqBaseDict baseDict) hystrix");
	}

	@Override
	public void update(Long id) {
		log.info("common service dict  update(Long id) hystrix");
	}
	@Override
	public ResBaseDict find(@PathVariable("id") Long id) {
		log.info("common service dict  find(Long id) hystrix");
		return null;
	}

	@Override
	public ResOldDict findOld(Long id) { 
		log.info("common service old dict  findOld(Long id) hystrix");
		return null;
	}

	@Override
	public List<ResOldDict> findBillSystemList() {
		log.info("common service old dict  findBillSystemList() hystrix");
		return new ArrayList<ResOldDict>();
	}

}
