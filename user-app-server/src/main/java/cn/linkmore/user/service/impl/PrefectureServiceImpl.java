package cn.linkmore.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.linkmore.bean.constant.RedisKey;
import cn.linkmore.prefecture.client.PrefectureClient;
import cn.linkmore.redis.RedisService;
import cn.linkmore.user.common.UserCache;
import cn.linkmore.user.request.ReqPrefecture;
import cn.linkmore.user.response.ResPrefecture;
import cn.linkmore.user.response.ResUser;
import cn.linkmore.user.service.PrefectureService;
import cn.linkmore.util.ObjectUtils;
/**
 * Service实现 - 车区
 * @author liwenlong
 * @version 2.0
 */
@Service
public class PrefectureServiceImpl implements PrefectureService {
	@Autowired
	private PrefectureClient preClient;
	@Resource
	private RedisService redisService;
	
	private  final Logger log = LoggerFactory.getLogger(this.getClass());
	@Override
	public List<ResPrefecture> list(ReqPrefecture rp, HttpServletRequest request) {
		cn.linkmore.prefecture.request.ReqPrefecture reqPre = new cn.linkmore.prefecture.request.ReqPrefecture();
		reqPre.setLatitude(rp.getLatitude());
		reqPre.setLongitude(rp.getLongitude());
		String key = UserCache.getCacheKey(request);
		ResUser ru = (ResUser)this.redisService.get(RedisKey.USER_APP_AUTH_USER + key);
		if(ru!=null) {
			reqPre.setUserId(ru.getId());
		}
		List<cn.linkmore.prefecture.response.ResPrefecture> preList = this.preClient.findPreListByLoc(reqPre);
		List<ResPrefecture> resPrefectureList = new ArrayList<ResPrefecture>();
		ResPrefecture resPrefecture = null;
		for(int i=0;i<preList.size();i++) {
			resPrefecture = ObjectUtils.copyObject(preList.get(i), new ResPrefecture());
			resPrefectureList.add(resPrefecture);
		}
		log.info("=================="+ resPrefectureList.size());
		return resPrefectureList;
	}

}
