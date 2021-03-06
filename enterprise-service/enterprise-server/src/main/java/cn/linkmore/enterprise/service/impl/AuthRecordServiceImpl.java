package cn.linkmore.enterprise.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.linkmore.account.client.UserClient;
import cn.linkmore.account.response.ResUser;
import cn.linkmore.bean.common.Constants;
import cn.linkmore.bean.common.Constants.RedisKey;
import cn.linkmore.bean.common.security.CacheUser;
import cn.linkmore.bean.exception.BusinessException;
import cn.linkmore.bean.exception.StatusEnum;
import cn.linkmore.bean.view.ViewFilter;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.enterprise.controller.app.request.ReqAuthRecord;
import cn.linkmore.enterprise.controller.app.request.ReqAuthRecordUpdate;
import cn.linkmore.enterprise.controller.app.response.AuthRecordDetail;
import cn.linkmore.enterprise.controller.app.response.AuthRecordPre;
import cn.linkmore.enterprise.dao.cluster.AuthRecordClusterMapper;
import cn.linkmore.enterprise.dao.cluster.EntRentedRecordClusterMapper;
import cn.linkmore.enterprise.dao.cluster.OwnerStallClusterMapper;
import cn.linkmore.enterprise.dao.master.AuthRecordMasterMapper;
import cn.linkmore.enterprise.entity.AuthRecord;
import cn.linkmore.enterprise.entity.EntOwnerStall;
import cn.linkmore.enterprise.entity.EntRentedRecord;
import cn.linkmore.enterprise.service.AuthRecordService;
import cn.linkmore.redis.RedisService;
import cn.linkmore.task.TaskPool;
import cn.linkmore.third.client.SmsClient;
import cn.linkmore.third.request.ReqSms;
import cn.linkmore.user.factory.AppUserFactory;
import cn.linkmore.user.factory.UserFactory;
import cn.linkmore.util.DateUtils;
import cn.linkmore.util.DomainUtil;

/**
 * 授权记录service
 * @author jhb
 * @Date 2019年3月27日
 * @Version v1.0
 */
@Service
public class AuthRecordServiceImpl implements AuthRecordService {
	
	@Resource
	private UserClient userClient;
	@Resource
	private AuthRecordMasterMapper authRecordMasterMapper;
	
	@Resource
	private AuthRecordClusterMapper authRecordClusterMapper;
	
	@Autowired
	private OwnerStallClusterMapper ownerStallClusterMapper;
	
	@Resource
	private EntRentedRecordClusterMapper entRentedRecordClusterMapper;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private UserFactory appUserFactory = AppUserFactory.getInstance();
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private SmsClient smsClient;

	@Override
	public ViewPage findPage(ViewPageable pageable) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Map<String,Object> param = new HashMap<String,Object>(); 
		List<ViewFilter> filters = pageable.getFilters();
		if(StringUtils.isNotBlank(pageable.getSearchProperty())) {
			param.put(pageable.getSearchProperty(), pageable.getSearchValue());
		}
		if(filters!=null&&filters.size()>0) {
			for(ViewFilter filter:filters) {
				param.put(filter.getProperty(), filter.getValue());
			}
		}
		if(StringUtils.isNotBlank(pageable.getOrderProperty())) {
			param.put("property", DomainUtil.camelToUnderline(pageable.getOrderProperty()));
			param.put("direction", pageable.getOrderDirection());
		}
		Integer count = this.authRecordClusterMapper.count(param);
		param.put("start", pageable.getStart());
		param.put("pageSize", pageable.getPageSize());
		List<AuthRecord> list = this.authRecordClusterMapper.findPage(param);
		Map<String,Long> rentRecordParam = null;
		if(CollectionUtils.isNotEmpty(list)) {
			for(AuthRecord authRecord: list) {
				rentRecordParam = new HashMap<String,Long>();
				authRecord.setAuthPeriod(sdf.format(authRecord.getStartTime()) + "-" + sdf.format(authRecord.getEndTime()));
				if(authRecord.getEndTime().before(new Date())) {
					authRecord.setAuthFlag((short)2);
				} 
				rentRecordParam.put("stallId", authRecord.getStallId());
				rentRecordParam.put("userId", authRecord.getUserId());
				EntRentedRecord rentRecord = entRentedRecordClusterMapper.findLastByUserIdAndStallId(rentRecordParam);
				if(rentRecord!=null) {
					if(rentRecord.getLeaveTime()!=null) {
						authRecord.setLastUseTime(sdf.format(rentRecord.getLeaveTime()));
					}else {
						authRecord.setLastUseTime(sdf.format(rentRecord.getDownTime()));
					}
				}
				Integer useCount = entRentedRecordClusterMapper.findUseCount(rentRecordParam);
				authRecord.setUseCount(useCount);
				
				ResUser resUser = userClient.findById(authRecord.getAuthUserId());
				if(resUser != null) {
					authRecord.setAuthUserName(resUser.getUsername());
				}
			}
		}
		
		return new ViewPage(count,pageable.getPageSize(),list); 
	}

	@Override
	public AuthRecord findById(Long id) {
		return authRecordClusterMapper.findById(id);
	}

	@Override
	public List<AuthRecord> findRecordList(Map<String, Object> param) {
		return authRecordClusterMapper.findRecordList(param);
	}

	@Override
	public Boolean save(ReqAuthRecord record, HttpServletRequest request) {
		Boolean flag = false;
		CacheUser user = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
		if (user == null) {
			throw new BusinessException(StatusEnum.USER_APP_NO_LOGIN);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		AuthRecord authRecord = null;
		Map<String,Object> param = new HashMap<String,Object>();
		if(StringUtils.isNotBlank(record.getStallIds())) {
			String [] ids = record.getStallIds().split(",");
			String [] stallNames = record.getStallNames().split(",");
			int i = 0;
			for(String stallId: ids) {
				try {
					Long userId = this.userClient.getUserIdByMobile(record.getMobile(),record.getUsername());
					param.put("userId", userId);
					param.put("stallId", stallId);
					param.put("endTime", 1);
					param.put("flag", 0);
					List<AuthRecord> authRecordList = authRecordClusterMapper.findRecordList(param);
					if(CollectionUtils.isNotEmpty(authRecordList)) {
						throw new BusinessException(StatusEnum.EXIST_AUTH_RECORD_LIST);
					}
					authRecord = new AuthRecord();
					authRecord.setStallId(Long.valueOf(stallId));
					authRecord.setStallName(stallNames[i]);
					authRecord.setCreateTime(new Date());
					authRecord.setUpdateTime(new Date());
					authRecord.setMobile(record.getMobile());
					authRecord.setUsername(record.getUsername());
					authRecord.setPreId(record.getPreId());
					authRecord.setPreName(record.getPreName());
					authRecord.setUserId(userId);
					authRecord.setRelationId(record.getRelationId());
					authRecord.setRelationName(record.getRelationName());
					authRecord.setStartTime(sdf.parse(record.getStartTime()));
					authRecord.setEndTime(sdf.parse(record.getEndTime()));
					authRecord.setAuthFlag((short)0);
					//启用状态
					authRecord.setSwitchFlag((short)1);
					authRecord.setAuthUserId(user.getId());
					if(authRecord.getStartTime().after(authRecord.getEndTime())) {
						throw new BusinessException(StatusEnum.AUTH_RECORD_STARTAFTEREND);
					}
					
					authRecordMasterMapper.save(authRecord);
					flag = true;
					
					if(flag) {
						ReqSms sms = new ReqSms();
						Map<String, String> smsParam = new HashMap<String, String>();
						smsParam.put("tel", user.getMobile());
						sms.setParam(smsParam);
						sms.setMobile(record.getMobile());
						sms.setSt(Constants.SmsTemplate.AUTH_RENT_STALL_NOTICE);
						smsClient.send(sms);
					}
				} catch (ParseException e) {
					flag = false;
					e.printStackTrace();
					throw new BusinessException(StatusEnum.VALID_EXCEPTION);
				}
				i++;
			}
			TaskPool.getInstance().task(new Runnable() {
				@Override
				public void run() {
					shareStall(record.getStallIds(), record.getMobile(), request);
				}
			});
		}
		return flag;
	}

	@Override
	public Boolean update(ReqAuthRecordUpdate record, HttpServletRequest request) {
		Boolean flag = false;
		CacheUser user = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
		if (user == null) {
			throw new BusinessException(StatusEnum.USER_APP_NO_LOGIN);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		try {
			AuthRecord authRecord = new AuthRecord();
			authRecord.setId(record.getId());
			authRecord.setUpdateTime(new Date());
			authRecord.setUsername(record.getUsername());
			authRecord.setRelationId(record.getRelationId());
			authRecord.setRelationName(record.getRelationName());
			authRecord.setStartTime(sdf.parse(record.getStartTime()));
			authRecord.setEndTime(sdf.parse(record.getEndTime()));
			if(authRecord.getStartTime().after(authRecord.getEndTime())) {
				throw new BusinessException(StatusEnum.AUTH_RECORD_STARTAFTEREND);
			}
			authRecordMasterMapper.update(authRecord);
			flag = true;
		} catch (ParseException e) {
			e.printStackTrace();
			throw new BusinessException(StatusEnum.VALID_EXCEPTION);
		}
		return flag;
	}

	@Override
	public Boolean cancalAuth(Long id, HttpServletRequest request) {
		Boolean flag = false;
		CacheUser user = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
		if (user == null) {
			throw new BusinessException(StatusEnum.USER_APP_NO_LOGIN);
		}
		int num = authRecordMasterMapper.cancelAuth(id);
		
		if(num == 1) {
			TaskPool.getInstance().task(new Runnable() {
				@Override
				public void run() {
					cancelShare(id);
				}
			});
			flag = true;
			TaskPool.getInstance().task(new Runnable() {
				@Override
				public void run() {
					cancalShare(id);
				}

				
			});
		}
		return flag;
	}
	private void cancalShare(Long id) {
		AuthRecord authRecord = this.authRecordClusterMapper.findById(id);
		if(authRecord != null) {
			Set<Object> members = this.redisService.members(RedisKey.USER_APP_SHARE_STALL.key+authRecord.getUserId());
			if(members != null && members.contains(authRecord.getStallId())) {
				members.remove(authRecord.getStallId());
			}
			this.redisService.remove(RedisKey.USER_APP_SHARE_STALL.key+authRecord.getUserId());
			if(members != null && members.size() != 0) {
				this.redisService.addAll(RedisKey.USER_APP_SHARE_STALL.key+authRecord.getUserId(),members);
			}
		}
	}

	@Override
	public List<AuthRecordPre> findRecordList(HttpServletRequest request) {
		Map<String,Object> param = new HashMap<String,Object>();
		CacheUser user = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
		if (user == null) {
			throw new BusinessException(StatusEnum.USER_APP_NO_LOGIN);
		}
		List<EntOwnerStall> stalllist = ownerStallClusterMapper.findStall(user.getId());
		Set<Long> ids = new HashSet<Long>();
		Map<Long,EntOwnerStall> map = new HashMap<Long,EntOwnerStall>();
		if (CollectionUtils.isNotEmpty(stalllist) && stalllist.size() > 0) {
			for (EntOwnerStall entOwnerStall : stalllist) {
				ids.add(entOwnerStall.getStallId());
				if("1".equals(entOwnerStall.getStallType())) {
					map.put(entOwnerStall.getStallId(), entOwnerStall);
				}
			}
		}
		log.info("auth stall map = {}",JSON.toJSON(map));
		List<AuthRecordPre> authRecordPreList = null;
		AuthRecordPre authRecordPre = null;
		param.put("authUserId", user.getId());
		List<AuthRecord> authRecordList = authRecordClusterMapper.findRecordList(param);
		Set<Long> stallId = new HashSet<Long>();
		if(CollectionUtils.isNotEmpty(authRecordList)) {
			authRecordPreList = new ArrayList<AuthRecordPre>();
			for(AuthRecord authRecord:authRecordList) {
				if(stallId.contains(authRecord.getStallId())) {
					continue;
				}
				authRecordPre = new AuthRecordPre();
				authRecordPre.setStallId(authRecord.getStallId());
				authRecordPre.setPreId(authRecord.getPreId());
				authRecordPre.setPreName(authRecord.getPreName());
				authRecordPre.setStallName(authRecord.getStallName());
				authRecordPreList.add(authRecordPre);
				stallId.add(authRecord.getStallId());
			}
			List<AuthRecordDetail> detailList = null;
			AuthRecordDetail recordDetail = null;
			SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
			SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
			List<Long> authIds = new ArrayList<Long>();
			for(AuthRecordPre authPre: authRecordPreList) {
				detailList = new ArrayList<AuthRecordDetail>();
				for (AuthRecord authRecord : authRecordList) {
					if(authPre.getPreId().equals(authRecord.getPreId()) &&
							authPre.getStallId().equals(authRecord.getStallId())) {
						recordDetail = new AuthRecordDetail();
						recordDetail.setId(authRecord.getId());
						recordDetail.setMobile(authRecord.getMobile());
						recordDetail.setRelationId(authRecord.getRelationId());
						recordDetail.setRelationName(authRecord.getRelationName());
						recordDetail.setUsername(authRecord.getUsername());
						String startTimeStr = sdf.format(authRecord.getStartTime());
						String entTimeStr = sdf.format(authRecord.getEndTime());
						if(startTimeStr!= null && startTimeStr.startsWith("0")) {
							startTimeStr = startTimeStr.substring(1);
						}
						if(entTimeStr!= null && entTimeStr.startsWith("0")) {
							entTimeStr = entTimeStr.substring(1);
						}
						recordDetail.setStartTime(startTimeStr);
						recordDetail.setEndTime(entTimeStr);
						recordDetail.setAuthFlag(authRecord.getAuthFlag());
						recordDetail.setEndTimeAll(sdfAll.format(authRecord.getEndTime()));
						if(map.get(authRecord.getStallId())!=null) {
							recordDetail.setStallEndTime(DateUtils.convert(map.get(authRecord.getStallId()).getEndTime(), null));
						}
						//此处需要根据车位id查询当前授权人是否拥有车位的使用权限
						if(authRecord.getAuthFlag() == 0 && authRecord.getEndTime().before(new Date())) {
							recordDetail.setAuthFlag((short)2);
						} else {
							if(authRecord.getAuthFlag() == 0 && !ids.contains(authRecord.getStallId()) ) {
								recordDetail.setAuthFlag((short)3);
								authIds.add(authRecord.getId());
							}
						} 
						detailList.add(recordDetail);
					}
				}
				authPre.setDetailList(detailList);
			}
			
			if(CollectionUtils.isNotEmpty(authIds)) {
				log.info("update the auth_flag = 3 authIds = {}", authIds);
				int num = authRecordMasterMapper.batchUpdate(authIds);
			}
		}
		return authRecordPreList;
	}

	@Override
	public AuthRecord findByUserId(Long userId,Long stallId) {
		return 	this.authRecordClusterMapper.findByUserId(userId,stallId);
		
	}

	@Override
	public List<AuthRecord> findAuthUserIdAndStallId(Long userId, Long stallId) {
		return this.authRecordClusterMapper.findAuthUserIdAndStallId(userId, stallId);
	}

	@Override
	public Boolean shareStall(String stallIds,String mobile, HttpServletRequest request) {
		Long userId = this.userClient.getUserIdByMobile(mobile);
		String[] ids = stallIds.split(",");
		Set<Long> s = new HashSet<>();
		for (String string : ids) {
			if(StringUtils.isNotBlank(string)) {
				s.add(Long.decode(string));
			}
		}
		this.redisService.add(RedisKey.USER_APP_SHARE_STALL.key+userId, s);
		return true;
	}
	
	public Boolean cancelShare(Long id) {
		AuthRecord record = this.authRecordClusterMapper.findById(id);
		record.getMobile();
		Long mobile = this.userClient.getUserIdByMobile(record.getMobile());
		if(mobile == null) {
			return false;
		}
		Set<Object> members = this.redisService.members(RedisKey.USER_APP_SHARE_STALL.key+mobile);
		if(members.contains(record.getStallId())) {
			members.remove(record.getStallId());
			this.redisService.add(RedisKey.USER_APP_SHARE_STALL.key+members, members);
		}
		return true;
	}
	public int operateSwitch(Map<String, Object> param) {
		return this.authRecordMasterMapper.operateSwitch(param);
	}

	@Override
	public int updateOverdueStatus() {
		return this.authRecordMasterMapper.updateOverdueStatus();
	}

	@Override
	public List<AuthRecord> findAuthRecordByAuthUserId(Long id) {
		return this.authRecordClusterMapper.findAuthRecordByAuthUserId(id);
	}
	
	
}
