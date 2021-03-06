package cn.linkmore.ops.ent.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import cn.linkmore.bean.view.Tree;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.enterprise.request.ReqOperateAuth;
import cn.linkmore.enterprise.request.ReqOperateBind;
import cn.linkmore.ops.ent.service.OperateService;
import cn.linkmore.prefecture.client.OpsOperateAuthClient;
import cn.linkmore.security.response.ResPerson;
@Service
public class OperateServiceImpl implements OperateService {
	@Resource
	private OpsOperateAuthClient opsOperateAuthClient;

	@Override
	public ViewPage findPage(ViewPageable pageable) {
		ViewPage page = this.opsOperateAuthClient.list(pageable);
		return page;
	}

	@Override
	public List<Tree> tree(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		ResPerson person = (ResPerson)subject.getSession().getAttribute("person"); 
		Long entId = null;
		if(person.getType() == 0 || person.getType() == 1) {
			entId = 0L;
		}else {
			entId = person.getId();
		}
		List<Tree> list = this.opsOperateAuthClient.tree(entId);
		return list;
	}

	@Override
	public void bind(ReqOperateBind staffAuth) {
		this.opsOperateAuthClient.bind(staffAuth);
		
	}

	@Override
	public Map<String, Object> resource(Long id) {
		Map<String, Object> map = this.opsOperateAuthClient.resource(id);
		return map;
	}

	@Override
	public void save(ReqOperateAuth auth) {
		this.opsOperateAuthClient.save(auth);
	}

	@Override
	public void update(ReqOperateAuth auth) {
		this.opsOperateAuthClient.update(auth);
	}

	@Override
	public void delete(Long id) {
		this.opsOperateAuthClient.delete(id);
	}

	@Override
	public void stop(Long id) {
		this.opsOperateAuthClient.stop(id);
	}

	@Override
	public void start(Long id) {
		this.opsOperateAuthClient.start(id);
	}

	
}
