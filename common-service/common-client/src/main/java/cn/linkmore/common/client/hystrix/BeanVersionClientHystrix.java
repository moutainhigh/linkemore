package cn.linkmore.common.client.hystrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import cn.linkmore.common.client.BeanVersionClient;
import cn.linkmore.common.request.ReqVersion;
import cn.linkmore.common.response.ResVersionBean;

@Component
public class BeanVersionClientHystrix implements BeanVersionClient {
	private  final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public ResVersionBean current(@PathVariable("source") Short source) {
		return null;
	}

	@Override
	public void report(@RequestBody ReqVersion vrb) {
		
	}

	
}
