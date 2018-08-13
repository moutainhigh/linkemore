package cn.linkmore.ops.biz.service.impl;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import cn.linkmore.account.client.UserClient;
import cn.linkmore.account.response.ResPageUser;
import cn.linkmore.account.response.ResUser;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.ops.biz.service.UserService;
/**
 * 用户信息接口 实现
 * @author   GFF
 * @Date     2018年5月26日
 * @Version  v2.0
 */
@Service
public class UserServiceImpl implements UserService {

	@Resource
	private UserClient client;

	@Override
	public ViewPage findPage(ViewPageable pageable) {
		return client.findPage(pageable);
	}

	@Override
	public List<ResPageUser> export(ViewPageable pageable) {
		return client.export(pageable);
	}

	@Override
	public List<ResUser> findAll() {
		//List<ResUser> list = client.findAll();
		return null;//client.findAll();
	}
	
	
	
	
}
