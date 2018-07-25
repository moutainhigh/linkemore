package cn.linkmore.prefecture.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linkmore.lock.bean.LockBean;
import com.linkmore.lock.factory.LockFactory;
import com.linkmore.lock.response.ResponseMessage;

import cn.linkmore.bean.common.Constants.LockStatus;
import cn.linkmore.bean.common.Constants.RedisKey;
import cn.linkmore.bean.common.Constants.StallStatus;
import cn.linkmore.enterprise.response.ResBrandPreStall;
import cn.linkmore.enterprise.response.ResBrandStall;
import cn.linkmore.prefecture.client.EntBrandPreClient;
import cn.linkmore.prefecture.dao.cluster.PrefectureClusterMapper;
import cn.linkmore.prefecture.dao.cluster.StallClusterMapper;
import cn.linkmore.prefecture.entity.Stall;
import cn.linkmore.prefecture.response.ResPreGateway;
import cn.linkmore.redis.RedisService;
import cn.linkmore.util.JsonUtil;

@Component
public class FreeStallInit {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private StallClusterMapper stallClusterMapper;

	@Autowired
	private RedisService redisService;

	@Autowired
	private PrefectureClusterMapper prefectureClusterMapper;

	@Autowired
	private LockFactory lockFactory;

	@Autowired
	private EntBrandPreClient entBrandPreClient;

	@Scheduled(cron = "0 0/3 * * * ?")
	public void run() {
		log.info("sync stall lock thread...");
		init();
	}

	/**
	 * 品牌车区空闲车位处理
	 * 
	 * @param lbm
	 * @param list
	 */
	private void brand(Map<String, LockBean> lbm, List<Stall> list) {
		log.info("brand prefecture free stall init...");
		Map<Long, Stall> snmap = new HashMap<Long, Stall>();
		for (Stall s : list) {
			snmap.put(s.getId(), s);
		}
		List<ResBrandPreStall> bps = entBrandPreClient.preStallList();
		log.info("brand pre stall list {}", JSON.toJSON(bps));
		Map<Long, Set<Object>> bmap = new HashMap<Long, Set<Object>>();
		Set<Object> ls = null;
		Stall st = null;
		List<Long> bpids = new ArrayList<Long>();
		for (ResBrandPreStall bp : bps) {
			bpids.add(bp.getId());
			ls = new HashSet<Object>();
			for (ResBrandStall bs : bp.getStallList()) {
				st = snmap.get(bs.getStallId());
				if (lbm.containsKey(st.getLockSn())) {
					if (!this.redisService.exists(RedisKey.PREFECTURE_BUSY_STALL.key + st.getLockSn())) {
						ls.add(st.getLockSn());
					}
					lbm.remove(st.getLockSn());
				}
				list.remove(st);
			}
			bmap.put(bp.getId(), ls);
		}
		log.info("brand free stall map " + bmap);
		Set<Long> keys = bmap.keySet();
		for (Long key : keys) {
			bpids.remove(key);
			redisService.remove(RedisKey.PREFECTURE_BRAND_FREE_STALL.key + key);
			redisService.addAll(RedisKey.PREFECTURE_BRAND_FREE_STALL.key + key, bmap.get(key));
		}
		for (Long id : bpids) {
			log.info("brand redis remove key " + id);
			redisService.remove(RedisKey.PREFECTURE_BRAND_FREE_STALL.key + id);
		}
	}

	/**
	 * 普通自营空闲车位处理
	 * 
	 * @param lbm
	 * @param list
	 */
	private void common(Map<String, LockBean> lbm, List<Stall> list) {
		log.info("common prefecture free stall init...");
		List<Long> preIds = this.prefectureClusterMapper.findPreIdList();
		Map<Long, Set<Object>> map = new HashMap<Long, Set<Object>>();
		Set<Object> sns = null;
		for (Stall stall : list) {
			if (lbm.containsKey(stall.getLockSn())
					&& !this.redisService.exists(RedisKey.PREFECTURE_BUSY_STALL.key + stall.getLockSn())) {
				sns = map.get(stall.getPreId());
				if (sns == null) {
					sns = new HashSet<>();
					map.put(stall.getPreId(), sns);
				}
				sns.add(stall.getLockSn());
			}
		}
		log.info("free stall map " + map);
		Set<Long> keys = map.keySet();
		for (Long key : keys) {
			preIds.remove(key);
			redisService.remove(RedisKey.PREFECTURE_FREE_STALL.key + key);
			redisService.addAll(RedisKey.PREFECTURE_FREE_STALL.key + key, map.get(key));
		}
		for (Long id : preIds) {
			log.info("redis remove key " + id);
			redisService.remove(RedisKey.PREFECTURE_FREE_STALL.key + id);
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

	public void init() {
		log.info("free stall init... ");
		List<ResPreGateway> rpgs = this.prefectureClusterMapper.findPreGateList();
		ResponseMessage<LockBean> rm = null;
		List<LockBean> lbs = null;
		Map<String, LockBean> lbm = new HashMap<String, LockBean>();
		for (ResPreGateway rpg : rpgs) {
			rm = this.lockFactory.findAvailableLock(rpg.getNumber());
			lbs = rm.getDataList();
			log.info("rm = {}",JsonUtil.toJson(rm));
			if (rm.getMsgCode() != null && rm.getMsgCode() == 200 && rm.getDataList() != null) {
				for (LockBean lb : lbs) {
					if (lb.getLockState().intValue() == LockStatus.UP.status) {
						lbm.put(lb.getLockCode(), lb);
					}
				}
			}
		}
		List<Stall> list = this.stallClusterMapper.findByStatus(StallStatus.FREE.status);
		try {
			this.brand(lbm, list);
		} catch (Exception e) {
		}
		try {
			this.common(lbm, list);
		} catch (Exception e) {
		}
	}

}