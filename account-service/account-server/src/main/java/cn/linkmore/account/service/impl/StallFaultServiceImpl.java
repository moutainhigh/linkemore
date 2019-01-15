package cn.linkmore.account.service.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import cn.linkmore.account.controller.app.request.ReqStallFault;
import cn.linkmore.account.dao.master.StallFaultFeedBackMasterMapper;
import cn.linkmore.account.entity.StallFaultFeedBack;
import cn.linkmore.account.service.StallFaultService;
import cn.linkmore.bean.common.Constants.RedisKey;
import cn.linkmore.bean.common.security.CacheUser;
import cn.linkmore.enterprise.response.ResEntExcStallStatus;
import cn.linkmore.prefecture.client.FeignStallExcStatusClient;
import cn.linkmore.redis.RedisService;
import cn.linkmore.util.TokenUtil;

@Service
public class StallFaultServiceImpl implements StallFaultService {

	@Resource
	private FeignStallExcStatusClient stallExcStatusClient;
	@Resource
	private StallFaultFeedBackMasterMapper backMasterMapper;
	@Resource
	private RedisService redisService;
	@Override
	public void save(ReqStallFault fault , HttpServletRequest request) {
		String key = TokenUtil.getKey(request);
		CacheUser user = (CacheUser) this.redisService.get(RedisKey.USER_APP_AUTH_USER.key+key); 
		StallFaultFeedBack feedback = new StallFaultFeedBack();
		feedback.setCreateTime(new Date());
		feedback.setDictId(fault.getDictId());
		feedback.setDictName(fault.getDictName());
		feedback.setExtra(fault.getExtra());
		feedback.setUserId(user.getId());
		feedback.setMobile(user.getMobile());
		feedback.setStallId(fault.getStallId());
		backMasterMapper.insert(feedback);
		ResEntExcStallStatus cause = new ResEntExcStallStatus();
		cause.setCreateTime(new Date());
		cause.setExcRemark(feedback.getDictName());
		cause.setExcStatus(feedback.getDictId());
		cause.setStallId(feedback.getStallId());
		cause.setStatus((short)0);
		stallExcStatusClient.save(cause);
	}

	
	
}
