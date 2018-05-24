package cn.linkmore.prefecture.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.linkmore.bean.common.Constants.RedisKey;
import cn.linkmore.bean.common.Constants.StallStatus;
import cn.linkmore.prefecture.dao.cluster.PrefectureClusterMapper;
import cn.linkmore.prefecture.dao.cluster.StallClusterMapper;
import cn.linkmore.prefecture.entity.Stall;
import cn.linkmore.redis.RedisService;

@Component
public class FreeStallInitRunner implements ApplicationRunner {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private StallClusterMapper stallClusterMapper;

	@Autowired
	private RedisService redisService;

	@Autowired
	private PrefectureClusterMapper prefectureClusterMapper;

	@Override
	public void run(ApplicationArguments args) throws Exception { 
		log.info("free stall init ");
		List<Long> preIds = this.prefectureClusterMapper.findPreIdList();
		List<Stall> list = this.stallClusterMapper.findByStatus(StallStatus.FREE.status);
		Map<Long, Set<String>> map = new HashMap<Long, Set<String>>();
		Set<String> sns = null;
		for (Stall stall : list) {
			sns = map.get(stall.getPreId());
			if (sns == null) {
				sns = new HashSet<>();
				map.put(stall.getPreId(), sns);
			}
			sns.add(stall.getLockSn());
		}
		Set<Long> keys = map.keySet();
		for (Long key : keys) {
			preIds.remove(key);
			redisService.remove(RedisKey.PREFECTURE_FREE_STALL.key+key);
			redisService.add(RedisKey.PREFECTURE_FREE_STALL.key + key, map.get(key).toArray());
		}
		for(Long id:preIds) {
			redisService.remove(RedisKey.PREFECTURE_FREE_STALL.key+id);
		}
		Set<Object> bindLockSet = this.redisService.members(RedisKey.ORDER_ASSIGN_STALL.key);
		for (Object bindLock : bindLockSet) {
			Map<String, Object> params = JSONObject.parseObject(bindLock.toString());
			if (params.get("preId") != null && params.get("lockSn") != null) {
				this.redisService.remove(RedisKey.PREFECTURE_FREE_STALL.key + params.get("preId").toString(),
						params.get("lockSn").toString());
			}
		}
	}

}