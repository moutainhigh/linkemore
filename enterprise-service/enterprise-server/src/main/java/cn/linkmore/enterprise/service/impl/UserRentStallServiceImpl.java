package cn.linkmore.enterprise.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.linkmore.bean.common.Constants.ExpiredTime;
import cn.linkmore.bean.common.Constants.RedisKey;
import cn.linkmore.bean.common.security.CacheUser;
import cn.linkmore.bean.exception.BusinessException;
import cn.linkmore.bean.exception.StatusEnum;
import cn.linkmore.enterprise.controller.app.request.ReqLocation;
import cn.linkmore.enterprise.controller.app.request.ReqUserRentStall;
import cn.linkmore.enterprise.controller.app.response.OwnerPre;
import cn.linkmore.enterprise.controller.app.response.OwnerRes;
import cn.linkmore.enterprise.controller.app.response.OwnerStall;
import cn.linkmore.enterprise.controller.app.response.ResAuthRentStall;
import cn.linkmore.enterprise.controller.app.response.ResCurrentOwner;
import cn.linkmore.enterprise.controller.app.response.ResHaveRentList;
import cn.linkmore.enterprise.controller.app.response.ResHaveRentPre;
import cn.linkmore.enterprise.controller.app.response.ResHaveRentPreStall;
import cn.linkmore.enterprise.controller.app.response.ResParkingRecord;
import cn.linkmore.enterprise.controller.app.response.ResRentStallFlag;
import cn.linkmore.enterprise.controller.app.response.ResRentUser;
import cn.linkmore.enterprise.controller.app.response.ResRentUserStall;
import cn.linkmore.enterprise.dao.cluster.EntRentedRecordClusterMapper;
import cn.linkmore.enterprise.dao.cluster.OwnerStallClusterMapper;
import cn.linkmore.enterprise.dao.master.EntRentedRecordMasterMapper;
import cn.linkmore.enterprise.entity.AuthRecord;
import cn.linkmore.enterprise.entity.EntOwnerPre;
import cn.linkmore.enterprise.entity.EntOwnerStall;
import cn.linkmore.enterprise.entity.EntRentedRecord;
import cn.linkmore.enterprise.service.AuthRecordService;
import cn.linkmore.enterprise.service.OwnerStallService;
import cn.linkmore.enterprise.service.RentedRecordService;
import cn.linkmore.enterprise.service.UserRentStallService;
import cn.linkmore.prefecture.client.FeignLockClient;
import cn.linkmore.prefecture.client.StallClient;
import cn.linkmore.prefecture.request.ReqControlLock;
import cn.linkmore.prefecture.response.ResLockInfo;
import cn.linkmore.prefecture.response.ResLockInfos;
import cn.linkmore.prefecture.response.ResStall;
import cn.linkmore.prefecture.response.ResStallEntity;
import cn.linkmore.redis.RedisLock;
import cn.linkmore.redis.RedisService;
import cn.linkmore.user.factory.AppUserFactory;
import cn.linkmore.user.factory.UserFactory;
import cn.linkmore.util.DateUtils;
import cn.linkmore.util.JsonUtil;
import cn.linkmore.util.MapUtil;

@Service
public class UserRentStallServiceImpl implements UserRentStallService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private UserFactory appUserFactory = AppUserFactory.getInstance();
	@Autowired
	private RentedRecordService recordService;
	@Autowired
	private AuthRecordService authRecordService;
	@Autowired
	private RedisService redisService;
	@Autowired
	private OwnerStallService ownerStallService;
	@Autowired
	private RedisLock redisLock;
	@Autowired
	private OwnerStallClusterMapper ownerStallClusterMapper;
	@Autowired
	private EntRentedRecordMasterMapper entRentedRecordMasterMapper;
	@Autowired
	private EntRentedRecordClusterMapper entRentedRecordClusterMapper;
	@Autowired
	private FeignLockClient feignLockClient;
	@Autowired
	private StallClient stallClient;

	@Override
	public OwnerRes findStall(HttpServletRequest request, ReqLocation location) {
		CacheUser user = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
		OwnerRes res = new OwnerRes();
		Boolean isHave = false;
		int num = 0;
		try {
			Long userId = user.getId();
			// 查询最新的未完成进程
			EntRentedRecord record = entRentedRecordClusterMapper.findByUser(userId);
			List<EntRentedRecord> recordList = this.entRentedRecordClusterMapper.findAllByUser(user.getId());
			List<EntOwnerPre> prelist = null;
			List<EntOwnerStall> stalllist = ownerStallClusterMapper.findStall(userId);
			log.info(JsonUtil.toJson(stalllist));
			if (stalllist == null || stalllist.size() == 0) {
				return res;
			}
			Set<Long> ids = new HashSet<Long>();
			if (CollectionUtils.isNotEmpty(stalllist) && stalllist.size() > 0) {
				for (EntOwnerStall entOwnerStall : stalllist) {
					ids.add(entOwnerStall.getPreId());
				}
				prelist = ownerStallClusterMapper.findPreByIds(ids);
				log.info(JsonUtil.toJson(prelist));
			}
			if (prelist == null || prelist.size() == 0) {
				return res;
			}
			List<String> gateways = prelist.stream().map(pre -> pre.getGateway()).collect(Collectors.toList());
			List<ResLockInfos> lockInfos = this.feignLockClient.lockLists(gateways);
			Map<Long, List<ResLockInfo>> tempMap = new HashMap<>();
			for (ResLockInfos info : lockInfos) {
				for (EntOwnerPre resPre : prelist) {
					if (resPre.getGateway().equals(info.getGroupId())) {
						tempMap.put(resPre.getPreId(), info.getInfos());
						break;
					}
				}

			}
			log.info("车位>>>" + stalllist.size() + "车区>>>" + prelist.size() + "用户>>>" + JSON.toJSONString(user));
			List<OwnerPre> list = new ArrayList<>();
			Set<Long> stallIdList = new HashSet<>();
			if ("0".equals(location.getSwitchFlag()) && record != null) { // 未完成进程
				for (EntOwnerPre pre : prelist) {
					if (pre.getPreId().equals(record.getPreId())) {
						OwnerPre ownerpre = new OwnerPre();
						ownerpre.setPreId(pre.getPreId());
						ownerpre.setPreName(pre.getPreName());
						ownerpre.setAddress(pre.getAddress());
						ownerpre.setLatitude(pre.getLatitude());
						ownerpre.setLongitude(pre.getLongitude());
						List<OwnerStall> ownerstalllist = new ArrayList<>();
						for (EntOwnerStall enttall : stalllist) {
							if (enttall.getStallId().equals(record.getStallId())) {
								OwnerStall OwnerStall = new OwnerStall();
								if (tempMap != null && !tempMap.isEmpty()) {
									for (Entry<Long, List<ResLockInfo>> info : tempMap.entrySet()) {
										if (info.getKey() == pre.getPreId()) {
											for (ResLockInfo inf : info.getValue()) {
												if (inf.getLockCode().equals(enttall.getLockSn())) {
													OwnerStall.setBattery(inf.getElectricity());
													OwnerStall.setGatewayStatus(inf.getOnlineState());
													break;
												}
											}
										}
									}
								}
								OwnerStall.setStallType(enttall.getStallType());
								OwnerStall.setRentMoType(enttall.getRentMoType());
								OwnerStall.setRentOmType(enttall.getRentOmType());
								OwnerStall.setStallId(enttall.getStallId());
								OwnerStall.setMobile(enttall.getMobile());
								OwnerStall.setPlate(enttall.getPlate());
								OwnerStall.setStallName(enttall.getStallName());
								OwnerStall.setImageUrl(enttall.getImageUrl());
								OwnerStall.setRouteGuidance(enttall.getRouteGuidance());
								OwnerStall.setStallLocal(enttall.getStallLocal());
								OwnerStall.setLockSn(enttall.getLockSn());
								OwnerStall.setStallEndTime(DateUtils.convert(enttall.getEndTime(), null));
								OwnerStall.setLockStatus(enttall.getLockStatus());
								OwnerStall.setStatus(enttall.getStatus() == 1l ? 1 : 2l);
								ownerstalllist.add(OwnerStall);
								num++;
								isHave = true;
								break;
							}
						}
						ownerpre.setStalls(ownerstalllist);
						list.add(ownerpre);
						log.info("..........findStall isHave = {} list = {}", isHave, list);
						break;
					}
				}
			} else {
				for (EntOwnerPre pre : prelist) {
					OwnerPre ownerpre = new OwnerPre();
					ownerpre.setPreId(pre.getPreId());
					ownerpre.setPreName(pre.getPreName());
					ownerpre.setAddress(pre.getAddress());
					ownerpre.setLatitude(pre.getLatitude());
					ownerpre.setLongitude(pre.getLongitude());
					ownerpre.setDistance(MapUtil.getDistance(location.getLatitude(), location.getLongitude(),
							new Double(pre.getLatitude()), new Double(pre.getLongitude())));
					List<OwnerStall> ownerstalllist = new ArrayList<>();
					for (EntOwnerStall enttall : stalllist) {
						if (pre.getPreId().equals(enttall.getPreId())) {
							if (stallIdList.contains(enttall.getStallId())) {
								continue;
							}
							stallIdList.add(enttall.getStallId());
							OwnerStall OwnerStall = new OwnerStall();
							if (tempMap != null && !tempMap.isEmpty()) {
								for (Entry<Long, List<ResLockInfo>> info : tempMap.entrySet()) {
									if (info.getKey() == pre.getPreId()) {
										for (ResLockInfo inf : info.getValue()) {
											if (inf.getLockCode().equals(enttall.getLockSn())) {
												OwnerStall.setBattery(inf.getElectricity());
												OwnerStall.setGatewayStatus(inf.getOnlineState());
												break;
											}
										}
									}
								}
							}
							OwnerStall.setStallType(enttall.getStallType());
							OwnerStall.setRentMoType(enttall.getRentMoType());
							OwnerStall.setRentOmType(enttall.getRentOmType());
							OwnerStall.setStallId(enttall.getStallId());
							OwnerStall.setMobile(enttall.getMobile());
							OwnerStall.setPlate(enttall.getPlate());
							OwnerStall.setStallName(enttall.getStallName());
							// OwnerStall.setStartTime(handleTime(enttall.getStartTime()));
							// OwnerStall.setEndTime(handleTime(enttall.getEndTime()));
							OwnerStall.setImageUrl(enttall.getImageUrl());
							OwnerStall.setStallEndTime(DateUtils.convert(enttall.getEndTime(), null));
							OwnerStall.setRouteGuidance(enttall.getRouteGuidance());
							OwnerStall.setStallLocal(enttall.getStallLocal());
							OwnerStall.setLockSn(enttall.getLockSn());
							OwnerStall.setLockStatus(enttall.getLockStatus());
							OwnerStall.setStatus(enttall.getStatus() == 1l ? 1 : 2l);

							// 当前车位被该用户占用的情况下设置一对多标识为1
							if (enttall.getStatus().intValue() == 2) {
								if (CollectionUtils.isNotEmpty(recordList)) {
									for (EntRentedRecord rentRecord : recordList) {
										if (OwnerStall.getStallId().equals(rentRecord.getStallId())) {
											OwnerStall.setRentOmType((short) 1);
										}
									}
								}
							}

							num++;
							ownerstalllist.add(OwnerStall);
							log.info(JsonUtil.toJson(OwnerStall));
						}
					}
					ownerpre.setStalls(ownerstalllist);
					list.add(ownerpre);
				}
				// 排序
				Collections.sort(list, new Comparator<OwnerPre>() {
					public int compare(OwnerPre pre1, OwnerPre pre2) {
						return Double.valueOf(pre1.getDistance()).compareTo(Double.valueOf(pre2.getDistance()));
					}
				});
			}
			res.setRes(list);
			res.setIsHave(isHave);
			res.setNum(num);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString());
			throw new BusinessException(StatusEnum.SERVER_EXCEPTION);
		}
	}

	@Override
	public Boolean control(ReqUserRentStall reqOperatStall, HttpServletRequest request) {
		CacheUser user = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
		if (user == null) {
			throw new BusinessException(StatusEnum.USER_APP_NO_LOGIN);
		}
		Boolean isAllow = false;
		List<EntOwnerStall> stalllist = ownerStallClusterMapper.findStall(user.getId());
		EntRentedRecord newrecord = new EntRentedRecord();
		for (EntOwnerStall entOwnerStall : stalllist) {
			if (reqOperatStall.getStallId().equals(entOwnerStall.getStallId())) {
				newrecord.setStallId(entOwnerStall.getStallId());
				newrecord.setUserId(user.getId());
				newrecord.setStatus(0L);
				newrecord.setDownTime(new Date());
				newrecord.setPreId(entOwnerStall.getPreId());
				newrecord.setStallName(entOwnerStall.getStallName());
				// newrecord.setEntId(entOwnerStall.getEntId());
				newrecord.setPreName(entOwnerStall.getPreName());
				// newrecord.setEntPreId(entOwnerStall.getEntPreId());
				newrecord.setPlateNo(entOwnerStall.getPlate());
				isAllow = true;
				break;
			}
		}
		if (!isAllow) {
			if(reqOperatStall.getState().intValue() == 2) {
				List<EntRentedRecord> re = entRentedRecordClusterMapper.findLastByStallIds(Arrays.asList(reqOperatStall.getStallId()));
				if(re != null) {
					List<EntRentedRecord> list = re.stream().filter( r -> r.getUserId().equals(user.getId())).collect(Collectors.toList());
					if(list != null && list.size() != 0) {
						isAllow =true;
					}
				}
			}
			if(!isAllow) {
				log.info(user.getId() + ">>>STAFF_STALL_EXISTS");
				throw new BusinessException(StatusEnum.STAFF_STALL_EXISTS);
			}
		}

		// 争抢
		String robkey = RedisKey.ROB_STALL_ISHAVE.key + reqOperatStall.getStallId();
		Integer using = 0;
		Boolean have = true;
		try {
			have = this.redisLock.getLock(robkey, user.getId());
			log.info("用户=======>" + user.getId() + (have == true ? "已抢到" : "未抢到") + "锁" + robkey);
		} catch (Exception e) {

		}
		if (!have) {
			throw new BusinessException(StatusEnum.STALL_HIVING_DO);
		}
		Map<String, Object> pam = new HashMap<>();
		pam.put("stallId", reqOperatStall.getStallId());
		pam.put("userId", user.getId());
		// 判断当前车位是否被其他人使用
		using = entRentedRecordClusterMapper.findUsingRecord(pam);
		ResStallEntity stallEntity = stallClient.findById(reqOperatStall.getStallId());
		log.info("operate the stallName = {},stall={}", stallEntity.getStallName(), JSON.toJSON(stallEntity));
		Long userId = null;
		// 原有逻辑支持一对多操作
		if (using > 0) {
			//被授权用户使用时 授权用户可以操作
			List<AuthRecord> records = this.authRecordService.findAuthUserIdAndStallId(user.getId(),reqOperatStall.getStallId());
			if(records == null || records.size() == 0) {
				this.redisService.remove(robkey);
				throw new BusinessException(StatusEnum.STALL_AlREADY_CONTROL);
			}
			EntRentedRecord record = this.entRentedRecordClusterMapper.findByStallId(reqOperatStall.getStallId());
			
			if(record != null && record.getStatus() == 0 ) {
			userId = record.getUserId();
			}
		}

		// 新逻辑
		/*
		 * if(stallEntity!= null) { //多对一操作允许控制同一个车位
		 * if(stallEntity.getRentMoType().intValue() == 0) { if (using>0) {
		 * this.redisService.remove(robkey); throw new
		 * BusinessException(StatusEnum.STALL_AlREADY_CONTROL); } } }
		 */

		// 未完成记录同一用户只有一单
		// EntRentedRecord record =
		// entRentedRecordClusterMapper.findByUser(user.getId());
		Map<String, Long> param = new HashMap<String, Long>();
		param.put("userId", user.getId());
		param.put("stallId", reqOperatStall.getStallId());
		EntRentedRecord record = entRentedRecordClusterMapper.findByUserIdAndStallId(param);
		log.info("record = {}", JSON.toJSON(record));
		if (reqOperatStall.getState() == 2) {
			log.info("用户>>>" + user.getId() + "升锁>>>" + reqOperatStall.getStallId());
		} else if (reqOperatStall.getState() == 1) {
			log.info("用户>>>" + user.getId() + "降锁>>>" + reqOperatStall.getStallId());

			// 正常车位，若无使用记录则插入数据 && stallEntity.getRentOmType().intValue() == 0 &&
			// stallEntity.getRentMoType().intValue() == 0
			if (!Objects.nonNull(record)) {
				entRentedRecordMasterMapper.saveSelective(newrecord);
			}
			// 当长租车位为1对多标识时，若无记录或者有使用记录但使用记录车位Id和当前操作车位Id不一致时可添加使用记录
			/*
			 * if(stallEntity.getRentOmType().intValue() == 1 && (record == null ||
			 * (record!=null && record.getStallId()!=stallEntity.getId()))) {
			 * entRentedRecordMasterMapper.saveSelective(newrecord); }
			 */

			// 当长租车位为多对1标识时，若当前用户无使用记录，且该车位没有被占用则可以新增记录
			/*
			 * if(stallEntity.getRentMoType().intValue() == 1 && record == null && using
			 * ==0) { entRentedRecordMasterMapper.saveSelective(newrecord); }
			 */

			// 原流程
			// Objects.nonNull 如果参数不为空则返回true
			/*
			 * if (!Objects.nonNull(record)) { try {
			 * entRentedRecordMasterMapper.saveSelective(newrecord); } catch (Exception e) {
			 * e.printStackTrace(); } log.info("用户>>>" + user.getId() + "record>>>" +
			 * reqOperatStall.getStallId()); }
			 */
		}
		// 放入缓存
		String rediskey = RedisKey.ACTION_STALL_DOING.key + reqOperatStall.getStallId();
		this.redisService.set(rediskey, user.getId(), ExpiredTime.STALL_LOCK_BOOKING_EXP_TIME.time);
		log.info("用户>>>" + user.getId() + "缓存>>>" + rediskey);
		// 调用
		ReqControlLock reqc = new ReqControlLock();
		reqc.setKey(rediskey);
		reqc.setStallId(reqOperatStall.getStallId());
		reqc.setStatus(reqOperatStall.getState());
		reqc.setUserId(userId != null ? userId : user.getId());
		Boolean control = stallClient.appControl(reqc);
		if (control == null || !control) {
			if (this.redisService.exists(RedisKey.OWNER_CONTROL_LOCK.key + reqc.getStallId())) {
				Object object = this.redisService.get(RedisKey.OWNER_CONTROL_LOCK.key + reqc.getStallId());
				this.redisService.remove(RedisKey.OWNER_CONTROL_LOCK.key + reqc.getStallId());
				if (control == null) {
					control = false;
				}
				throw new BusinessException(StatusEnum.get((int) object));
			} else {
				control = true;
			}
		}
		log.info("用户>>>" + user.getId() + "调用>>>" + reqOperatStall.getStallId());
		return control;
	}

	@Override
	public Boolean owner(HttpServletRequest request) {
		Boolean owner = this.ownerStallService.owner(request);
		return owner;
	}

	@Override
	public ResCurrentOwner current(HttpServletRequest request) {
		CacheUser user = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
		int count = 0;
		int singleCount = 1;
		List<EntRentedRecord> recordList = this.entRentedRecordClusterMapper.findAllByUser(user.getId());
		EntRentedRecord record = this.entRentedRecordClusterMapper.findByUser(user.getId());
		ResCurrentOwner owner = new ResCurrentOwner();
		if (record == null) {
			owner.setStatus(false);
			owner.setSwitchFlag(true);
		} else {
			List<EntOwnerStall> stalllist = ownerStallClusterMapper.findStall(user.getId());
			if (CollectionUtils.isNotEmpty(stalllist)) {
				for (EntOwnerStall ownerStall : stalllist) {
					log.info("current user have the record size ={}", recordList.size());
					// 使用记录>1条则判断当前用户是否可点击切换车位
					if (recordList.size() > 1) {
						for (EntRentedRecord rentRecord : recordList) {
							if (ownerStall.getRentOmType().intValue() == 1) {
								if (rentRecord.getStallId().equals(ownerStall.getStallId())) {
									count++;
								}
							}
						}
					} else {
						// 使用记录为仅有1条记录时，根据当前记录的车位id与开通了1对多操作的id进行对比
						if (ownerStall.getRentOmType().intValue() == 1) {
							if (!record.getStallId().equals(ownerStall.getStallId())) {
								singleCount++;
							}
						}
					}
				}

				if (count > 1 || singleCount > 1) {
					owner.setSwitchFlag(true);
				}
				owner.setStatus(true);
				owner.setStallNumber(stalllist.size());
				owner.setPreId(record.getPreId());
				owner.setPreName(record.getPreName());
				owner.setStallId(record.getStallId());
				owner.setStallName(record.getStallName());
			} else {
				owner.setStatus(false);
			}
		}
		return owner;
	}

	@Override
	public ResAuthRentStall findStallList(HttpServletRequest request, ReqLocation location) {
		CacheUser user = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
		ResAuthRentStall authRentStall = new ResAuthRentStall();
		List<ResRentUser> rentUserList = new ArrayList<>();
		List<ResRentUserStall> rentUserStallList = null;
		ResRentUserStall rentUserStall = null;
		ResRentUser rentUser = null;
		Map<String, Object> param = new HashMap<>();
		param.put("userId", user.getId());
		param.put("endTime", 1);
		param.put("flag", 0);
		List<AuthRecord> findRecordList = this.authRecordService.findRecordList(param);
		List<EntOwnerStall> stalllist = ownerStallClusterMapper.findStall(user.getId());
		log.info("被授权记录={} 自有可用车位数={}",JSON.toJSON(findRecordList), stalllist.size());
		if((stalllist == null || stalllist.size() == 0) && (findRecordList == null || findRecordList.size() == 0)) {
			return authRentStall;
		}
		List<Long> preIdAuthList = null;
		List<Long> stallIdAuthList = null;
		List<ResStall> resStallList = null;
		Set<Long> preIds = new HashSet<>();
		List<Long> stalls = new ArrayList<>();
		if(findRecordList != null && findRecordList.size() != 0) {
			preIdAuthList = findRecordList.stream().map(f -> f.getPreId()).collect(Collectors.toList());
			stallIdAuthList = findRecordList.stream().map(f -> f.getStallId()).collect(Collectors.toList());
			if(preIdAuthList != null) {
				preIds.addAll(preIdAuthList);
			}
			if(stallIdAuthList != null) {
				stalls.addAll(stallIdAuthList);
			}
			if(stallIdAuthList != null && stallIdAuthList.size() != 0) {
				Map<String, Object> map = new HashMap<>();
				map.put("list", stallIdAuthList);
				resStallList = this.stallClient.findPreStallList(map);
				log.info(JsonUtil.toJson(resStallList));
			}
		}
		stalllist = stalllist.stream().filter(s -> s.getStallType().equals("1")).collect(Collectors.toList());
		List<Long> preIdOwnerList = null;
		List<Long> stallIdOwnerList = null;
		if(stalllist != null && stalllist.size() != 0) {
			preIdOwnerList = stalllist.stream().map(s -> s.getPreId()).collect(Collectors.toList());
			if(preIdOwnerList != null) {
				preIds.addAll(preIdOwnerList);
			}
			stallIdOwnerList = stalllist.stream().map(s -> s.getStallId()).collect(Collectors.toList());
			if(stallIdOwnerList != null) {
				stalls.addAll(stallIdOwnerList);
			}
		}
		if(preIds.size() == 0) {
			return authRentStall;
		}
		List<EntOwnerPre> preList = ownerStallClusterMapper.findPreByIds(preIds);
		List<String> gateways = preList.stream().map(pre -> pre.getGateway()).collect(Collectors.toList());
		List<ResLockInfos> lockInfos = this.feignLockClient.lockLists(gateways);
		Map<Long, List<ResLockInfo>> tempMap = new HashMap<>();
		if(stalls.size() == 0) {
			return authRentStall;
		}
		Map<String,Object> map = new HashMap<>();
		map.put("list", stalls);
//		List<ResStall> stallList = this.stallClient.findPreStallList(map);
		List<EntRentedRecord> records = this.recordService.findLastByStallIds(stalls);
//		log.info(JsonUtil.toJson(stallList));
		log.info(JsonUtil.toJson(stalllist));
		Boolean isHave = false;
		if ("0".equals(location.getSwitchFlag())) { 
			EntRentedRecord record = entRentedRecordClusterMapper.findByUser(user.getId());
			if(record != null) {
				isHave = true;
				rentUser = new ResRentUser();
				ResStallEntity resStallEntity = this.stallClient.findById(record.getStallId());
				rentUserStallList = new ArrayList<>();
				rentUserStall = new ResRentUserStall();
				rentUserStall.setDownLockTime(record.getDownTime());
				rentUserStall.setStallStatus(resStallEntity.getStatus());
				rentUserStall.setStallId(record.getStallId());
				rentUserStall.setUseUpLockTime(record.getLeaveTime());
				rentUserStall.setStallName(record.getStallName());
				rentUserStall.setLockSn(resStallEntity.getLockSn());
				if(stallIdOwnerList!=null && stallIdOwnerList.contains(rentUserStall.getStallId())) {
					rentUserStall.setIsUserRecord(1);
					rentUserStall.setUserStatus(1);
					if(stalllist != null && stalllist.size() != 0) {
						for (EntOwnerStall ownerStall : stalllist) {
							if(ownerStall.getStallId().equals(record.getStallId())) {
								rentUserStall.setValidity(DateUtils.convert(ownerStall.getEndTime(), DateUtils.DARW_FORMAT_TIME));
								rentUserStall.setRentMoType(ownerStall.getRentMoType());
								rentUserStall.setRentOmType(ownerStall.getRentOmType());
								rentUserStall.setLockSn(ownerStall.getLockSn());
								break;
							}
						}
					}
				}else if(stallIdAuthList != null && stallIdAuthList.contains(record.getStallId())) {
					//此处若授权记录超过当前时间则过期，会出现空指针异常
					AuthRecord authRecord = this.authRecordService.findByUserId(user.getId(), record.getStallId());
					rentUserStall.setValidity(authRecord.getEndTime());
				}else {
					AuthRecord authRecord = this.authRecordService.findByUserId(user.getId(), record.getStallId());
					if(authRecord != null) {
						rentUserStall.setValidity(authRecord.getEndTime());
					}else {
						rentUserStall.setValidity(new Date());
					}
					
				}
				rentUserStall.setPreId(record.getPreId());
				rentUserStall.setPreName(record.getPreName());
		
				ResStallEntity entity = this.stallClient.findById(record.getStallId());
				rentUserStall.setStallStatus(entity.getStatus().intValue());
				ResLockInfo lockInfo = this.feignLockClient.lockInfo(entity.getLockSn());
				if(lockInfo != null) {
					rentUserStall.setBattery(lockInfo.getElectricity());
					rentUserStall.setParkingState(lockInfo.getParkingState());
					rentUserStall.setGatewayStatus(lockInfo.getOnlineState());
					if (lockInfo.getLockState() == 1) {
						rentUserStall.setLockStatus(lockInfo.getLockState());
					} else {
						rentUserStall.setLockStatus(2);
					}
					// rentUserStall.setGatewayStatus(inf.getOnlineState());
				}
				// AuthRecord record = findRecordList.stream().filter(f -> f.getStallId() ==
				// enttall.getStallId()).findFirst().get();
				// rentUserStall.setValidity(record.getEndTime());
				for (EntOwnerPre pre : preList) {
					if(pre.getPreId() == rentUserStall.getPreId()) {
						rentUser = new ResRentUser();
						rentUser.setPreId(pre.getPreId());
						rentUser.setPreName(pre.getPreName());
						rentUser.setAddress(pre.getAddress());
						rentUser.setLatitude(pre.getLatitude());
						rentUser.setLongitude(pre.getLongitude());
						rentUserStall.setUnderLayer(pre.getUnderLayer());
						rentUser.setDistance(MapUtil.getDistance(location.getLatitude(), location.getLongitude(),
								new Double(pre.getLatitude()), new Double(pre.getLongitude())));
						rentUserList.add(rentUser);
					}
				}
				rentUserStallList.add(rentUserStall);
				rentUser.setRentUserStalls(rentUserStallList);
				authRentStall.setIsHave(isHave);
				authRentStall.setRentUsers(rentUserList);
				return authRentStall;
			}
		}
		authRentStall.setIsHave(isHave);
		for (ResLockInfos info : lockInfos) {
			for (EntOwnerPre resPre : preList) {
				if (resPre.getGateway().equals(info.getGroupId())) {
					tempMap.put(resPre.getPreId(), info.getInfos());
					break;
				}
			}

		}
		for (EntOwnerPre pre : preList) {
			rentUser = new ResRentUser();
			rentUser.setPreId(pre.getPreId());
			rentUser.setPreName(pre.getPreName());
			rentUser.setAddress(pre.getAddress());
			rentUser.setLatitude(pre.getLatitude());
			rentUser.setLongitude(pre.getLongitude());
			rentUser.setDistance(MapUtil.getDistance(location.getLatitude(), location.getLongitude(),
					new Double(pre.getLatitude()), new Double(pre.getLongitude())));
			rentUserStallList = new ArrayList<>();
			//	长租用户车位
			if(stalllist != null && stalllist.size() != 0) {
			for (EntOwnerStall enttall : stalllist) {
				if (pre.getPreId().equals(enttall.getPreId())) {
					rentUserStall = new ResRentUserStall();
					rentUserStall.setStallStatus(enttall.getStatus().intValue());
					rentUserStall.setRentMoType(enttall.getRentMoType());
					rentUserStall.setRentOmType(enttall.getRentOmType());
					rentUserStall.setUnderLayer(pre.getUnderLayer());
					if (tempMap != null && !tempMap.isEmpty()) {
						for (Entry<Long, List<ResLockInfo>> info : tempMap.entrySet()) {
							if (info.getKey() == pre.getPreId()) {
								for (ResLockInfo inf : info.getValue()) {
									if (inf.getLockCode().equals(enttall.getLockSn())) {
										rentUserStall.setBattery(inf.getElectricity());
										rentUserStall.setParkingState(inf.getParkingState());
										rentUserStall.setGatewayStatus(inf.getOnlineState());
										if (inf.getLockState() == 1) {
											log.info(inf.getLockCode() + "===" + inf.getLockState());
											rentUserStall.setLockStatus(inf.getLockState());
										} else {
											rentUserStall.setLockStatus(2);
										}
										// rentUserStall.setGatewayStatus(inf.getOnlineState());
										break;
									}
								}
							}
						}
					}
//					if (enttall.getStatus() == 2) {
//						rentUserStall.setIsUserUse(1);
//					}
					
					for (EntRentedRecord resRentedRecord : records) {
						if (resRentedRecord.getStallId().equals(enttall.getStallId())) {
							if (enttall.getStatus().intValue() == 2) {
								rentUserStall.setRentOmType((short) 1);
							}
							switch (rentUserStall.getLockStatus()) {
							case 1:
								rentUserStall.setUseUpLockTime(resRentedRecord.getLeaveTime());
								break;
							case 2:
								rentUserStall.setDownLockTime(resRentedRecord.getDownTime());
								break;
							}
							if (resRentedRecord.getUserId() != user.getId() && resRentedRecord.getType().intValue() == 2 &&enttall.getStatus().intValue() == 2) {
								AuthRecord re = this.authRecordService.findByUserId(resRentedRecord.getUserId(),
										resRentedRecord.getStallId());
								rentUserStall.setIsAuthUser(1);
								rentUserStall.setIsUserRecord(1);
								
								rentUserStall.setUseUserMobile(re.getMobile());
								rentUserStall.setUseUserName(re.getUsername());
								break;
							}
						}
					}
//					for (ResStall s : stallList) {
//						if(s.getId() == enttall.getStallId()) {
							rentUserStall.setStallStatus(enttall.getStatus().intValue());
							rentUserStall.setLockSn(enttall.getLockSn());
//							break;
//						}
//					}
					rentUserStall.setUserStatus(1);
					rentUserStall.setPreId(pre.getPreId());
					rentUserStall.setPreName(pre.getPreName());
					rentUserStall.setStallId(enttall.getStallId());
					rentUserStall.setStallName(enttall.getStallName());
					rentUserStall.setStallStatus(enttall.getStatus().intValue());
					// AuthRecord record = findRecordList.stream().filter(f -> f.getStallId() ==
					// enttall.getStallId()).findFirst().get();
					rentUserStall.setValidity(DateUtils.convert(enttall.getEndTime(), DateUtils.DARW_FORMAT_TIME));
					// rentUserStall.setValidity(record.getEndTime());
					rentUserStallList.add(rentUserStall);
				}
			}
			}
			if(findRecordList != null && findRecordList.size() != 0) {
			//	被授权用户车位
			for (AuthRecord authRecord : findRecordList) {
				if (pre.getPreId().equals(authRecord.getPreId())) {
					for (ResStall resStall : resStallList) {
						if (resStall.getId().equals(authRecord.getStallId())) {
							rentUserStall = new ResRentUserStall();
							if (tempMap != null && !tempMap.isEmpty()) {
								for (Entry<Long, List<ResLockInfo>> info : tempMap.entrySet()) {
									if (info.getKey() == pre.getPreId()) {
										for (ResLockInfo inf : info.getValue()) {
											if (inf.getLockCode().equals(resStall.getLockSn())) {
												rentUserStall.setParkingState(inf.getParkingState());
												rentUserStall.setBattery(inf.getElectricity());
												rentUserStall.setGatewayStatus(inf.getOnlineState());
												if (inf.getLockState() == 1) {
													log.info(inf.getLockCode() + "===" + inf.getLockState());
													rentUserStall.setLockStatus(inf.getLockState());
												} else {
													rentUserStall.setLockStatus(2);
												}
												break;
											}

										}
									}
								}
							}
//							if (resStall.getStatus() == 2) {
//								rentUserStall.setIsUserUse(1);
//							}
							for (EntRentedRecord resRentedRecord : records) {
								if (resRentedRecord.getStallId().equals(resStall.getId())) {
									/*if (resRentedRecord.getUserId() != user.getId()
											&& resRentedRecord.getType().equals("2")) {
										AuthRecord re = this.authRecordService.findByUserId(resRentedRecord.getUserId(),
												resRentedRecord.getStallId());
										rentUserStall.setUseUserMobile(re.getMobile());
										rentUserStall.setUseUserName(re.getUsername());
										break;
									}*/
									switch (rentUserStall.getLockStatus()) {
									case 1:
										rentUserStall.setUseUpLockTime(resRentedRecord.getLeaveTime());
										break;
									case 2:
										rentUserStall.setDownLockTime(resRentedRecord.getDownTime());
										break;
									}
									break;
								}
							}
//							for (ResStall s : stallList) {
//								if(s.getId() == resStall.getId()) {
							rentUserStall.setStallStatus(resStall.getStatus());
//									break;
//								}
//							}
							rentUserStall.setPreId(pre.getPreId());
							rentUserStall.setUnderLayer(pre.getUnderLayer());
							rentUserStall.setPreName(pre.getPreName());
							rentUserStall.setStallId(resStall.getId());
							rentUserStall.setStallName(resStall.getStallName());
							rentUserStall.setStallStatus(resStall.getStatus().intValue());
							rentUserStall.setLockSn(resStall.getLockSn());
							// AuthRecord record = findRecordList.stream().filter(f -> f.getStallId() ==
							// enttall.getStallId()).findFirst().get();
							rentUserStall
									.setValidity(DateUtils.convert(authRecord.getEndTime(), DateUtils.DARW_FORMAT_TIME));
							// rentUserStall.setValidity(record.getEndTime());
							rentUserStallList.add(rentUserStall);
						}
					}
				}
				
			}
			}
			rentUser.setRentUserStalls(rentUserStallList);
		}
		rentUserList.add(rentUser);
		// }else if(stallIdOwnerList.contains(enttall.getStallId())) {
		// if(tempMap != null && !tempMap.isEmpty()) {
		// for (Entry<Long, List<ResLockInfo>> info : tempMap.entrySet()) {
		// if(info.getKey() == pre.getPreId()) {
		// for (ResLockInfo inf : info.getValue()) {
		// if(inf.getLockCode().equals(enttall.getLockSn())) {
		// rentUserStall.setBattery(inf.getElectricity());
		// if (inf.getLockState() == 1) {
		// log.info(inf.getLockCode() + "===" + inf.getLockState());
		// rentUserStall.setLockStatus(inf.getLockState());
		// } else {
		// rentUserStall.setLockStatus(2);
		// }
		//// rentUserStall.setGatewayStatus(inf.getOnlineState());
		// break;
		// }
		// }
		// }
		// }
		// }
		// switch (rentUserStall.getLockStatus()) {
		// case 1:
		//
		// break;
		// case 2:
		// break;
		// }
		// if(enttall.getStatus() == 2) {
		// rentUserStall.setIsUserUse(1);
		// }
		// rentUserStall.setPreId(pre.getPreId());
		// rentUserStall.setPreName(pre.getPreName());
		// rentUserStall.setStallId(enttall.getStallId());
		// rentUserStall.setStallName(enttall.getStallName());
		// rentUserStall.setStallStatus(enttall.getStatus().intValue());
		// EntOwnerStall ownerStall = stalllist.stream().filter(s -> s.getStallId() ==
		// enttall.getStallId() && DateUtils.convert(s.getEndTime(),
		// DateUtils.DARW_FORMAT_TIME).getTime() > new
		// Date().getTime()).findFirst().get();
		// if(ownerStall != null) {
		// rentUserStall.setValidity(DateUtils.convert(ownerStall.getEndTime(),
		// DateUtils.DARW_FORMAT_TIME));
		// }
		// }
		// rentUserStallList.add(rentUserStall);
		// 排序
		// Collections.sort(rentUserList, new Comparator<OwnerPre>() {
		// public int compare(OwnerPre pre1, OwnerPre pre2) {
		// return
		// Double.valueOf(pre1.getDistance()).compareTo(Double.valueOf(pre2.getDistance()));
		// }
		// });
		authRentStall.setRentUsers(rentUserList);
		return authRentStall;
	}

	@Override
	public Boolean controlAuth(ReqUserRentStall reqConStall, HttpServletRequest request) {
		Boolean control = this.control(reqConStall, request);
		if(control && reqConStall.getState().intValue() == 1) {
			CacheUser user = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
			AuthRecord authRecord = authRecordService.findByUserId(user.getId(), reqConStall.getStallId());
			List<EntRentedRecord> list = this.recordService.findLastByStallIds(Arrays.asList(reqConStall.getStallId()));
			if(authRecord != null) {
				if(list != null && list.size() == 1) {
					this.entRentedRecordMasterMapper.updateType(list.get(0).getId(),(short)2);
				}
			}else {
				this.entRentedRecordMasterMapper.updateType(list.get(0).getId(),(short)1);
			}
		}
		return control;
	}

	@Override
	public List<ResParkingRecord> parkingRecord(HttpServletRequest request,Integer pageNo,Long stallId) {
		/*CacheUser user = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
		List<AuthRecord> authRecordList = authRecordService.findAuthRecordByAuthUserId(user.getId());
		List<Long> collect = authRecordList.stream().map(auth -> auth.getUserId()).collect(Collectors.toList());
		if(collect != null && collect.size() != 0) {
			collect.add(user.getId());
		}else {
			collect = new ArrayList<>();
			collect.add(user.getId());
		}*/
		List<EntRentedRecord> list = this.recordService.findParkingRecord(pageNo,stallId);
		List<ResParkingRecord> records = new ArrayList<>();
		ResParkingRecord record = null;
		for (EntRentedRecord entRentedRecord : list) {
			record = new ResParkingRecord();
			record.setMobile(entRentedRecord.getMobile());
			record.setUsername(entRentedRecord.getUsername());
			record.setEndTime(entRentedRecord.getLeaveTime());
			record.setStartTime(entRentedRecord.getDownTime());
			record.setPreId(entRentedRecord.getPreId());
			record.setPreName(entRentedRecord.getPreName());
			record.setStallAuthType(entRentedRecord.getType());
			record.setStallName(entRentedRecord.getStallName());
			record.setStallId(entRentedRecord.getStallId());
			if(entRentedRecord.getLeaveTime() == null) {
				entRentedRecord.setLeaveTime(new Date());
			}
			record.setServiceTime(DateUtils.getDurationDetail(entRentedRecord.getLeaveTime(), entRentedRecord.getDownTime()));
			records.add(record);
		}
		return records;
	}

	
	public ResRentStallFlag authFlag(HttpServletRequest request) {
		CacheUser user = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
		Boolean is = false;
		if (user != null) {
			List<EntOwnerStall> stalllist = ownerStallClusterMapper.findAuthStall(user.getId());
			if (stalllist.size() > 0) {
				is = true;
			}
		}
		ResRentStallFlag flag = new ResRentStallFlag();
		flag.setAuthFlag(is);
	
		Set<Object> members = this.redisService.members(RedisKey.USER_APP_SHARE_STALL.key+user.getId());
		if(members != null && members.size() != 0) {
			flag.setShareFlag(true);
			this.redisService.remove(RedisKey.USER_APP_SHARE_STALL.key+user.getId());
		}
		return flag;
	}

	@Override
	public List<OwnerPre> authStall(HttpServletRequest request) {
		List<OwnerPre> list = new ArrayList<OwnerPre>();
		CacheUser user = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
		List<EntOwnerPre> prelist = null;
		Set<Long> stallIdList = new HashSet<>();
		if (user != null) {
			List<EntOwnerStall> stalllist = ownerStallClusterMapper.findAuthStall(user.getId());
			if (stalllist == null || stalllist.size() == 0) {
				return list;
			}
			Set<Long> ids = new HashSet<Long>();
			if (CollectionUtils.isNotEmpty(stalllist) && stalllist.size() > 0) {
				for (EntOwnerStall entOwnerStall : stalllist) {
					ids.add(entOwnerStall.getPreId());
				}
				prelist = ownerStallClusterMapper.findPreByIds(ids);
			}
			if (prelist == null || prelist.size() == 0) {
				return list;
			}
			for (EntOwnerPre pre : prelist) {
				OwnerPre ownerpre = new OwnerPre();
				ownerpre.setPreId(pre.getPreId());
				ownerpre.setPreName(pre.getPreName());
				ownerpre.setAddress(pre.getAddress());
				ownerpre.setLatitude(pre.getLatitude());
				ownerpre.setLongitude(pre.getLongitude());
				List<OwnerStall> ownerstalllist = new ArrayList<>();
				for (EntOwnerStall enttall : stalllist) {
					if (pre.getPreId().equals(enttall.getPreId())) {
						if (stallIdList.contains(enttall.getStallId())) {
							continue;
						}
						stallIdList.add(enttall.getStallId());
						OwnerStall OwnerStall = new OwnerStall();
						OwnerStall.setStallType(enttall.getStallType());
						OwnerStall.setRentMoType(enttall.getRentMoType());
						OwnerStall.setRentOmType(enttall.getRentOmType());
						OwnerStall.setStallId(enttall.getStallId());
						OwnerStall.setMobile(enttall.getMobile());
						OwnerStall.setPlate(enttall.getPlate());
						OwnerStall.setStallName(enttall.getStallName());
						OwnerStall.setImageUrl(enttall.getImageUrl());
						OwnerStall.setStallEndTime(DateUtils.convert(enttall.getEndTime(), null));
						OwnerStall.setRouteGuidance(enttall.getRouteGuidance());
						OwnerStall.setStallLocal(enttall.getStallLocal());
						OwnerStall.setLockSn(enttall.getLockSn());
						OwnerStall.setLockStatus(enttall.getLockStatus());
						OwnerStall.setStatus(enttall.getStatus() == 1l ? 1 : 2l);
						ownerstalllist.add(OwnerStall);
					}
				}
				ownerpre.setStalls(ownerstalllist);
				list.add(ownerpre);
			}
		}
		return list;
	}

	@Override
	public ResHaveRentList findRentStallList(HttpServletRequest request, ReqLocation location) {
		CacheUser user = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
		ResHaveRentList authRentStall = new ResHaveRentList();
		List<ResHaveRentPre> rentUserList = new ArrayList<ResHaveRentPre>();
		List<ResHaveRentPreStall> rentUserStallList = null;
		ResHaveRentPreStall rentUserStall = null;
		ResHaveRentPre rentUser = null;
		List<EntOwnerStall> authStallList = new ArrayList<EntOwnerStall>();
		List<EntOwnerStall> ownerStallList = new ArrayList<EntOwnerStall>();
		List<EntOwnerStall> stalllist = ownerStallClusterMapper.findStall(user.getId());
		log.info("v2.0.0.2 userId = {} stalllist={}", user.getId() , stalllist.size());
		if (CollectionUtils.isEmpty(stalllist)) {
			return authRentStall;
		}
		authStallList = stalllist.stream().filter(s -> s.getStallType().equals("2")).collect(Collectors.toList());
		ownerStallList = stalllist.stream().filter(s -> s.getStallType().equals("1")).collect(Collectors.toList());

		List<Long> preIdAuthList = null;
		List<Long> stallIdAuthList = null;
		List<ResStall> resStallList = null;
		Set<Long> preIds = new HashSet<>();
		List<Long> stallIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(authStallList)) {
			preIdAuthList = authStallList.stream().map(f -> f.getPreId()).collect(Collectors.toList());
			stallIdAuthList = authStallList.stream().map(f -> f.getStallId()).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(preIdAuthList)) {
				preIds.addAll(preIdAuthList);
			}
			if (CollectionUtils.isNotEmpty(stallIdAuthList)) {
				stallIds.addAll(stallIdAuthList);
			}
			if (CollectionUtils.isNotEmpty(stallIdAuthList)) {
				Map<String, Object> map = new HashMap<>();
				map.put("list", stallIdAuthList);
				resStallList = this.stallClient.findPreStallList(map);
				log.info("被授权车位列表 ={}", JsonUtil.toJson(resStallList));
			}
		}
		List<Long> preIdOwnerList = null;
		List<Long> stallIdOwnerList = null;
		if (CollectionUtils.isNotEmpty(ownerStallList)) {
			preIdOwnerList = ownerStallList.stream().map(s -> s.getPreId()).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(preIdOwnerList)) {
				preIds.addAll(preIdOwnerList);
			}
			stallIdOwnerList = ownerStallList.stream().map(s -> s.getStallId()).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(stallIdOwnerList)) {
				stallIds.addAll(stallIdOwnerList);
			}
		}
		
		//所拥有的所有车区信息
		List<EntOwnerPre> preList = ownerStallClusterMapper.findPreByIds(preIds);
		List<String> gateways = preList.stream().map(pre -> pre.getGateway()).collect(Collectors.toList());
		List<ResLockInfos> lockInfos = this.feignLockClient.lockLists(gateways);
		Map<Long, List<ResLockInfo>> tempMap = new HashMap<>();
		for (ResLockInfos info : lockInfos) {
			for (EntOwnerPre resPre : preList) {
				if (resPre.getGateway().equals(info.getGroupId())) {
					tempMap.put(resPre.getPreId(), info.getInfos());
					break;
				}
			}
		}
		
		//当前所有车位长租使用记录列表
		Map<String, Object> map = new HashMap<>();
		map.put("list", stallIds);
		List<EntRentedRecord> records = this.recordService.findLastByStallIds(stallIds);
		log.info("rent record list = {}",JSON.toJSON(records));
		
		/*if ("0".equals(location.getSwitchFlag())) {
			EntRentedRecord record = entRentedRecordClusterMapper.findByUser(user.getId());
			if (record != null) {
				rentUser = new ResHaveRentPre();
				ResStallEntity resStallEntity = this.stallClient.findById(record.getStallId());
				rentUserStallList = new ArrayList<>();
				rentUserStall = new ResHaveRentPreStall();
				rentUserStall.setDownLockTime(record.getDownTime());
				rentUserStall.setStallStatus(resStallEntity.getStatus());
				rentUserStall.setStallId(record.getStallId());
				rentUserStall.setUseUpLockTime(record.getLeaveTime());
				rentUserStall.setStallName(record.getStallName());
				rentUserStall.setPreId(record.getPreId());
				rentUserStall.setPreName(record.getPreName());
				rentUserStall.setLockSn(resStallEntity.getLockSn());
				rentUserStall.setStallStatus(resStallEntity.getStatus().intValue());
				if (stallIdOwnerList.contains(rentUserStall.getStallId())) {
					rentUserStall.setIsUserRecord(1);
					rentUserStall.setUserStatus(1);
					if (CollectionUtils.isEmpty(stalllist)) {
						for (EntOwnerStall ownerStall : stalllist) {
							if (ownerStall.getStallId().equals(record.getStallId())) {
								rentUserStall.setValidity(
										DateUtils.convert(ownerStall.getEndTime(), DateUtils.DARW_FORMAT_TIME));
								rentUserStall.setLockSn(ownerStall.getLockSn());
								break;
							}
						}
					}
				} else if (stallIdAuthList != null && stallIdAuthList.contains(record.getStallId())) {
					// 此处若授权记录超过当前时间则过期，会出现空指针异常
					AuthRecord authRecord = this.authRecordService.findByUserId(user.getId(), record.getStallId());
					rentUserStall.setValidity(authRecord.getEndTime());
				} else {
					AuthRecord authRecord = this.authRecordService.findByUserId(user.getId(), record.getStallId());
					if (authRecord != null) {
						rentUserStall.setValidity(authRecord.getEndTime());
					} else {
						rentUserStall.setValidity(new Date());
					}
				}
				ResLockInfo lockInfo = this.feignLockClient.lockInfo(resStallEntity.getLockSn());
				if (lockInfo != null) {
					rentUserStall.setBattery(lockInfo.getElectricity());
					rentUserStall.setParkingState(lockInfo.getParkingState());
					rentUserStall.setGatewayStatus(lockInfo.getOnlineState());
					if (lockInfo.getLockState() == 1) {
						rentUserStall.setLockStatus(lockInfo.getLockState());
					} else {
						rentUserStall.setLockStatus(2);
					}
				}
				for (EntOwnerPre pre : preList) {
					if (pre.getPreId() == rentUserStall.getPreId()) {
						rentUser = new ResHaveRentPre();
						rentUser.setPreId(pre.getPreId());
						rentUser.setPreName(pre.getPreName());
						rentUser.setAddress(pre.getAddress());
						rentUser.setLatitude(pre.getLatitude());
						rentUser.setLongitude(pre.getLongitude());
						rentUserStall.setUnderLayer(pre.getUnderLayer());
						rentUser.setDistance(MapUtil.getDistance(location.getLatitude(), location.getLongitude(),
								new Double(pre.getLatitude()), new Double(pre.getLongitude())));
						rentUserList.add(rentUser);
					}
				}
				rentUserStallList.add(rentUserStall);
				rentUser.setRentPreStalls(rentUserStallList);
				authRentStall.setRentPres(rentUserList);
				return authRentStall;
			}
		}*/
		
		for (EntOwnerPre pre : preList) {
			rentUser = new ResHaveRentPre();
			rentUser.setPreId(pre.getPreId());
			rentUser.setPreName(pre.getPreName());
			rentUser.setAddress(pre.getAddress());
			rentUser.setLatitude(pre.getLatitude());
			rentUser.setLongitude(pre.getLongitude());
			rentUser.setDistance(MapUtil.getDistance(location.getLatitude(), location.getLongitude(),
					new Double(pre.getLatitude()), new Double(pre.getLongitude())));
			rentUserStallList = new ArrayList<>();
			// 自有长租用户车位
			if (CollectionUtils.isNotEmpty(ownerStallList)) {
				for (EntOwnerStall enttall : ownerStallList) {
					if (pre.getPreId().equals(enttall.getPreId())) {
						rentUserStall = new ResHaveRentPreStall();
						rentUserStall.setStallStatus(enttall.getStatus().intValue());
						rentUserStall.setUnderLayer(pre.getUnderLayer());
						rentUserStall.setStallStatus(enttall.getStatus().intValue());
						rentUserStall.setLockSn(enttall.getLockSn());
						if (tempMap != null && !tempMap.isEmpty()) {
							for (Entry<Long, List<ResLockInfo>> info : tempMap.entrySet()) {
								if (info.getKey() == pre.getPreId()) {
									for (ResLockInfo inf : info.getValue()) {
										if (inf.getLockCode().equals(enttall.getLockSn())) {
											rentUserStall.setBattery(inf.getElectricity());
											rentUserStall.setParkingState(inf.getParkingState());
											rentUserStall.setGatewayStatus(inf.getOnlineState());
											if (inf.getLockState() == 1) {
												log.info(inf.getLockCode() + "===" + inf.getLockState());
												rentUserStall.setLockStatus(inf.getLockState());
											} else {
												rentUserStall.setLockStatus(2);
											}
											break;
										}
									}
								}
							}
						}

						for (EntRentedRecord resRentedRecord : records) {
							if (resRentedRecord.getStallId().equals(enttall.getStallId())) {
								switch (rentUserStall.getLockStatus()) {
								case 1:
									rentUserStall.setUseUpLockTime(resRentedRecord.getLeaveTime());
									break;
								case 2:
									rentUserStall.setDownLockTime(resRentedRecord.getDownTime());
									break;
								}
								if (resRentedRecord.getUserId() != user.getId()
										&& resRentedRecord.getType().intValue() == 2
										&& enttall.getStatus().intValue() == 2) {
									AuthRecord re = this.authRecordService.findByUserId(resRentedRecord.getUserId(),
											resRentedRecord.getStallId());
									rentUserStall.setIsAuthUser(1);
									rentUserStall.setIsUserRecord(1);
									rentUserStall.setUseUserMobile(re.getMobile());
									rentUserStall.setUseUserName(re.getUsername());
									break;
								}
							}
						}
						rentUserStall.setUserStatus(1);
						rentUserStall.setPreId(pre.getPreId());
						rentUserStall.setPreName(pre.getPreName());
						rentUserStall.setStallId(enttall.getStallId());
						rentUserStall.setStallName(enttall.getStallName());
						rentUserStall.setStallStatus(enttall.getStatus().intValue());
						rentUserStall.setValidity(DateUtils.convert(enttall.getEndTime(), DateUtils.DARW_FORMAT_TIME));
						rentUserStallList.add(rentUserStall);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(authStallList)) {
				// 被授权用户车位
				for (EntOwnerStall authRecord : authStallList) {
					if (pre.getPreId().equals(authRecord.getPreId())) {
						for (ResStall resStall : resStallList) {
							if (resStall.getId().equals(authRecord.getStallId())) {
								rentUserStall = new ResHaveRentPreStall();
								if (tempMap != null && !tempMap.isEmpty()) {
									for (Entry<Long, List<ResLockInfo>> info : tempMap.entrySet()) {
										if (info.getKey() == pre.getPreId()) {
											for (ResLockInfo inf : info.getValue()) {
												if (inf.getLockCode().equals(resStall.getLockSn())) {
													rentUserStall.setParkingState(inf.getParkingState());
													rentUserStall.setBattery(inf.getElectricity());
													rentUserStall.setGatewayStatus(inf.getOnlineState());
													if (inf.getLockState() == 1) {
														log.info(inf.getLockCode() + "===" + inf.getLockState());
														rentUserStall.setLockStatus(inf.getLockState());
													} else {
														rentUserStall.setLockStatus(2);
													}
													break;
												}
											}
										}
									}
								}
								for (EntRentedRecord resRentedRecord : records) {
									if (resRentedRecord.getStallId().equals(resStall.getId())) {
										switch (rentUserStall.getLockStatus()) {
										case 1:
											rentUserStall.setUseUpLockTime(resRentedRecord.getLeaveTime());
											break;
										case 2:
											rentUserStall.setDownLockTime(resRentedRecord.getDownTime());
											break;
										}
										break;
									}
								}
								rentUserStall.setStallStatus(resStall.getStatus());
								rentUserStall.setPreId(pre.getPreId());
								rentUserStall.setUnderLayer(pre.getUnderLayer());
								rentUserStall.setPreName(pre.getPreName());
								rentUserStall.setStallId(resStall.getId());
								rentUserStall.setStallName(resStall.getStallName());
								rentUserStall.setStallStatus(resStall.getStatus().intValue());
								rentUserStall.setLockSn(resStall.getLockSn());
								rentUserStall.setValidity(
										DateUtils.convert(authRecord.getEndTime(), DateUtils.DARW_FORMAT_TIME));
								rentUserStallList.add(rentUserStall);
							}
						}
					}
				}
			}
			rentUser.setRentPreStalls(rentUserStallList);
		}
		rentUserList.add(rentUser);
		authRentStall.setRentPres(rentUserList);
		return authRentStall;
	}
	
}
