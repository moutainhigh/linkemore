package cn.linkmore.enterprise.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.linkmore.bean.common.Constants.PushType;
import cn.linkmore.bean.common.Constants.RedisKey;
import cn.linkmore.bean.common.Constants.SmsTemplate;
import cn.linkmore.bean.common.security.CacheUser;
import cn.linkmore.bean.common.security.Token;
import cn.linkmore.bean.exception.BusinessException;
import cn.linkmore.bean.exception.StatusEnum;
import cn.linkmore.enterprise.controller.staff.request.OrderOperateRequestBean;
import cn.linkmore.enterprise.controller.staff.request.SraffReqConStall;
import cn.linkmore.enterprise.controller.staff.request.SraffReqConStallSn;
import cn.linkmore.enterprise.controller.staff.request.StallOnLineRequest;
import cn.linkmore.enterprise.controller.staff.request.StallOperateRequestBean;
import cn.linkmore.enterprise.dao.cluster.BaseDictMapper;
import cn.linkmore.enterprise.dao.cluster.EntRentedRecordClusterMapper;
import cn.linkmore.enterprise.dao.master.EntRentedRecordMasterMapper;
import cn.linkmore.enterprise.entity.BaseDict;
import cn.linkmore.enterprise.entity.EntRentedRecord;
import cn.linkmore.enterprise.entity.StallOperateLog;
import cn.linkmore.enterprise.service.StaffPrefectureService;
import cn.linkmore.order.client.OrderClient;
import cn.linkmore.order.response.ResOrderOperateLog;
import cn.linkmore.order.response.ResRedisOrders;
import cn.linkmore.order.response.ResUserOrder;
import cn.linkmore.prefecture.client.PrefectureClient;
import cn.linkmore.prefecture.client.StallBatteryLogClient;
import cn.linkmore.prefecture.client.StallClient;
import cn.linkmore.prefecture.client.StallOperateLogClient;
import cn.linkmore.prefecture.client.StrategyFeeClient;
import cn.linkmore.prefecture.request.ReqControlLock;
import cn.linkmore.prefecture.request.ReqStall;
import cn.linkmore.prefecture.request.ReqStallOperateLog;
import cn.linkmore.prefecture.response.ResPrefectureDetail;
import cn.linkmore.prefecture.response.ResStallBatteryLog;
import cn.linkmore.prefecture.response.ResStallEntity;
import cn.linkmore.redis.RedisLock;
import cn.linkmore.redis.RedisService;
import cn.linkmore.task.TaskPool;
import cn.linkmore.third.client.PushClient;
import cn.linkmore.third.client.SmsClient;
import cn.linkmore.third.request.ReqPush;
import cn.linkmore.third.request.ReqSms;
import cn.linkmore.user.factory.StaffUserFactory;
import cn.linkmore.user.factory.UserFactory;
import cn.linkmore.util.HttpUtil;
import cn.linkmore.util.JsonUtil;
import cn.linkmore.util.ObjectUtils;
import cn.linkmore.util.TokenUtil;

@Service
public class StaffPrefectureServiceImpl implements StaffPrefectureService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private UserFactory staffUserFactory = StaffUserFactory.getInstance();
	@Autowired
	private RedisLock redisLock;
	@Autowired
	private EntRentedRecordClusterMapper entRentedRecordClusterMapper;
	@Autowired
	private EntRentedRecordMasterMapper rentedRecordMasterMapper;
	@Autowired
	private RedisService redisService;

	@Autowired
	private StallClient stallClient;

	@Autowired
	private SmsClient smsClient;

	@Autowired
	private PrefectureClient prefectureClient;

	@Autowired
	private OrderClient orderClient;

	@Autowired
	private BaseDictMapper baseDictMapper;

	@Autowired
	private StallOperateLogClient stallOperateLogClient;

	@Autowired
	private StallBatteryLogClient stallBatteryLogClient;

	@Autowired
	private StrategyFeeClient strategyFeeClient;
	
	@Autowired
	private PushClient pushClient;

	private ConcurrentHashMap<Long, String> mapOs = new ConcurrentHashMap<>();
	private static final int TIMEOUT = 30 * 1000;

	/**
	 * 升锁与降锁
	 */
	@Override
	public Boolean control(SraffReqConStall reqOperatStall, HttpServletRequest request) {
		CacheUser user = (CacheUser) this.redisService
				.get(RedisKey.STAFF_STAFF_AUTH_USER.key + TokenUtil.getKey(request));

		if (user == null) {
			throw new BusinessException(StatusEnum.USER_APP_NO_LOGIN);
		}
		
		ResStallEntity stall = stallClient.findById(reqOperatStall.getStallId());
		if(stall ==null   ) {
			throw new BusinessException(StatusEnum.FAIL_STALL_NUM);
		}
		
		String userid = user.getId().toString();
		long time = System.currentTimeMillis() + TIMEOUT;
		String robkey = RedisKey.MANAGER_STALL.key + reqOperatStall.getStallId();
		if (!redisLock.lock(String.valueOf(robkey), String.valueOf(time))) {
			log.info("no get it,wait a moment");
			throw new BusinessException(StatusEnum.STALL_HIVING_DO);
		}
		String reskey = (reqOperatStall.getState() == 1 ? RedisKey.MANAGER_STALL_DOWN.key
				: RedisKey.MANAGER_STALL_UP.key);
		redisService.set(reskey + userid, userid);
		ReqControlLock reqc = new ReqControlLock();
		reqc.setStallId(reqOperatStall.getStallId());
		reqc.setStatus(reqOperatStall.getState());
		reqc.setKey(reskey + userid);
		reqc.setRobkey(robkey);
		reqc.setType(stall.getType());
		Boolean flag = stallClient.managerlock(reqc);
		
		if(flag == null || !flag) {
			if (this.redisService.exists(RedisKey.OWNER_CONTROL_LOCK.key + reqc.getStallId())) {
				Object object = this.redisService.get(RedisKey.OWNER_CONTROL_LOCK.key + reqc.getStallId());
//				this.redisService.remove(RedisKey.OWNER_CONTROL_LOCK.key + reqc.getStallId());
				throw new BusinessException(StatusEnum.get((int) object));
			}
		}
		if(/*stall.getType() == 2 &&遗弃 新需求所有人员升锁关闭用户使用记录 */flag && reqOperatStall.getState().intValue() == 2) {
//			Map<String, Object> map = new HashMap<>();
//			map.put("stallId", reqc.getStallId());
//			rentedRecordMasterMapper.updateRentUserStatus(map );
			EntRentedRecord record = this.entRentedRecordClusterMapper.findByStallId(reqc.getStallId());
			if(record != null && record.getStatus().intValue() != 1) {
				record.setStatus(1L);
				record.setLeaveTime(new Date());
				this.rentedRecordMasterMapper.updateDownStatus(record);
			}
		}
		return flag;
	}

	@Override
	public Boolean controlSn(SraffReqConStallSn reqOperatStallSn, HttpServletRequest request) {
		CacheUser user = (CacheUser) this.redisService
				.get(RedisKey.STAFF_STAFF_AUTH_USER.key + TokenUtil.getKey(request));
		if (user == null) {
			throw new BusinessException(StatusEnum.USER_APP_NO_LOGIN);
		}
		String userid = user.getId().toString();
		String reskey = (reqOperatStallSn.getState() == 1 ? RedisKey.MANAGER_STALL_DOWN.key
				: RedisKey.MANAGER_STALL_UP.key);
		redisService.set(reskey + userid, userid);
		ReqControlLock reqc = new ReqControlLock();
		
		reqc.setStatus(reqOperatStallSn.getState());
		reqc.setKey(reskey + userid);
		reqc.setLockSn(reqOperatStallSn.getLockSn());
		Boolean flag = stallClient.managerlockSn(reqc);
		if(flag == null || !flag) {
			if (this.redisService.exists(RedisKey.OWNER_CONTROL_LOCK.key + reqc.getLockSn())) {
				Object object = this.redisService.get(RedisKey.OWNER_CONTROL_LOCK.key + reqc.getLockSn());
				this.redisService.remove(RedisKey.OWNER_CONTROL_LOCK.key + reqc.getLockSn());
				throw new BusinessException(StatusEnum.get((int) object));
			}
		}
		return flag;
	}
	
	/**
	 * 释放车位
	 */
	@Override
	public void releaseStall(Long stallId, HttpServletRequest request) {
		CacheUser user = (CacheUser) this.redisService
				.get(RedisKey.STAFF_STAFF_AUTH_USER.key + TokenUtil.getKey(request));
		if (user == null) {
			throw new BusinessException(StatusEnum.USER_APP_NO_LOGIN);
		}
		// 查询车位是否空闲
		ResStallEntity stall = stallClient.findById(stallId);
		if (stall == null) {
			throw new BusinessException(StatusEnum.STALL_UNKNOW_TYPE);
		}
		// 查询订单有无订单
		ResUserOrder orders = orderClient.findStallLatest(stallId);
		if (orders != null && orders.getStatus().intValue() == orders.ORDERS_STATUS_UNPAY) {
			throw new BusinessException(StatusEnum.STALL_OPERATE_ORDERING);
		}
		// 查询车位状态
		Map<String, Object> map = new HashMap<>();
		map = stallClient.watch(stallId);
		if (map == null || map.isEmpty()) {
			throw new BusinessException(StatusEnum.STALL_LOCK_OFFLINE);
		}
		if (!"200".equals(String.valueOf(map.get("code")))) {
			throw new BusinessException(StatusEnum.STALL_LOCK_OFFLINE);
		}
		/*
		 * if (String.valueOf(map.get("status")).equals("1")) { throw new
		 * BusinessException(StatusEnum.STALL_LOCK_NO_UP); }
		 */
		// 置为空闲
		stallClient.checkout(stallId);
	}

	/**
	 * 强制释放车位
	 */
	@Override
	public void forceReleaseStall(Long stallId, HttpServletRequest request) {
		CacheUser user = (CacheUser) this.redisService
				.get(RedisKey.STAFF_STAFF_AUTH_USER.key + TokenUtil.getKey(request));
		if (user == null) {
			throw new BusinessException(StatusEnum.USER_APP_NO_LOGIN);
		}
		// 查询车位是否空闲
		ResStallEntity stall = stallClient.findById(stallId);
		if (stall == null) {
			throw new BusinessException(StatusEnum.STALL_UNKNOW_TYPE);
		}
		// 查询订单有无订单
		ResUserOrder orders = orderClient.findStallLatest(stallId);
		if (orders != null && orders.getStatus().intValue() == orders.ORDERS_STATUS_UNPAY) {
			throw new BusinessException(StatusEnum.STALL_OPERATE_ORDERING);
		}
		// 置为空闲
		stallClient.checkout(stallId);
	}

	/**
	 * 车位下线
	 */
	@Override
	public void offline(StallOperateRequestBean bean, HttpServletRequest request) {
		CacheUser user = (CacheUser) this.redisService
				.get(RedisKey.STAFF_STAFF_AUTH_USER.key + TokenUtil.getKey(request));
		if (user == null) {
			throw new BusinessException(StatusEnum.USER_APP_NO_LOGIN);
		}
		ResStallEntity stall = stallClient.findById(bean.getStallId());
		if (stall.getStatus().intValue() > stall.STATUS_USED) {
			throw new BusinessException(StatusEnum.STALL_OPERATE_ORDERING);
		}
		// 查询订单
		ResUserOrder orders = orderClient.findStallLatest(bean.getStallId());
		if (orders != null && orders.getStatus().intValue() == orders.ORDERS_STATUS_UNPAY) {
			throw new BusinessException(StatusEnum.STALL_OPERATE_ORDERING);
		}
		// 更换电池
		if (bean.getRemarkId() == 1) {
			ResStallBatteryLog sbl = new ResStallBatteryLog();
			sbl.setAdminId(user.getId());
			sbl.setAdminName(user.getMobile());
			sbl.setCreateTime(new Date());
			sbl.setTotalNum(Integer.valueOf(0));
			sbl.setVoltage(0d);
			sbl.setStallId(stall.getId());
			stallBatteryLogClient.save(sbl);
		}

		// 更新车位状态
		ReqStall reqStall = new ReqStall();
		stall.setId(bean.getStallId());
		stall.setStatus(reqStall.STATUS_OUTLINE);
		reqStall = ObjectUtils.copyObject(stall, reqStall);
		stallClient.updateStatus(reqStall);

		// 插入记录
		ReqStallOperateLog sol = new ReqStallOperateLog();
		sol.setCreateTime(new Date());
		sol.setOperation(StallOperateLog.OPERATION_ONLINE);
		sol.setOperatorId(user.getId());
		sol.setSource(StallOperateLog.SOURCE_APP);
		sol.setStallId(bean.getStallId());
		sol.setRemarkId(bean.getRemarkId());
		sol.setRemark(bean.getRemark());
		sol.setStatus(StallOperateLog.STATUS_TRUE);
		stallOperateLogClient.save(sol);
		// 去除redis
		this.redisService.remove("freelock_key:" + stall.getPreId(), new Object[] { stall.getLockSn() });
	}

	/**
	 * 车位上线
	 * 
	 * @return
	 */
	@Override
	public void online(StallOnLineRequest bean, HttpServletRequest request) {
		CacheUser user = (CacheUser) this.redisService
				.get(RedisKey.STAFF_STAFF_AUTH_USER.key + TokenUtil.getKey(request));
		
		if (user == null) {
			throw new BusinessException(StatusEnum.USER_APP_NO_LOGIN);
		}
		ResStallEntity stall = stallClient.findById(bean.getStallId());

		if(stall ==null   ) {
			throw new BusinessException(StatusEnum.FAIL_STALL_NUM);
		}
		if (stall.getStatus().intValue() != stall.STATUS_OUTLINE && stall.getStatus() != stall.STATUS_FAULT) {
			throw new BusinessException(StatusEnum.STALL_OPERATE_UNOFFLINE);
		}
		ResUserOrder orders = orderClient.findStallLatest(bean.getStallId());
		if (orders != null && orders.getStatus().intValue() == orders.ORDERS_STATUS_UNPAY) {
			throw new BusinessException(StatusEnum.STALL_OPERATE_ORDERING);
		}

		Map<String, Object> map = new HashMap<>();
		map = stallClient.watch2(bean.getStallId());
		if (map == null || map.isEmpty()) {
			throw new BusinessException(StatusEnum.STALL_LOCK_OFFLINE);
		}
		if ("200".equals(String.valueOf(map.get("code")))) {
			if (Integer.valueOf(map.get("status").toString()) == 2) {
				throw new BusinessException(StatusEnum.STALL_LOCK_NO_UP);
			}
			ReqStallOperateLog sol = new ReqStallOperateLog();
			sol.setCreateTime(new Date());
			sol.setOperation(StallOperateLog.OPERATION_ONLINE);
			sol.setOperatorId(user.getId());
			sol.setSource(StallOperateLog.SOURCE_APP);
			sol.setStallId(bean.getStallId());
			sol.setStatus(StallOperateLog.STATUS_TRUE);
			stallOperateLogClient.save(sol);

			ReqStall reqStall = new ReqStall();
			stall.setId(bean.getStallId());
			stall.setStatus(reqStall.STATUS_FREE);
			reqStall = ObjectUtils.copyObject(stall, reqStall);
			stallClient.updateStatus(reqStall);
		} else {
			throw new BusinessException(StatusEnum.STALL_LOCK_NO_UP);
		}
	}

	/**
	 * 挂起订单
	 */
	@Override
	public void suspend(OrderOperateRequestBean oorb, HttpServletRequest request) {
		CacheUser user = (CacheUser) this.redisService
				.get(RedisKey.STAFF_STAFF_AUTH_USER.key + TokenUtil.getKey(request));

		if (user == null) {
			throw new BusinessException(StatusEnum.USER_APP_NO_LOGIN);
		}
		// 查订单
		ResUserOrder order = orderClient.findOrderById(oorb.getOrderId());
		if (order == null) {
			throw new BusinessException(StatusEnum.ORDER_OPERATE_NULLORDER); // 订单不存在
		}
		if (!order.getStallId().equals(oorb.getStallId())) {
			throw new BusinessException(StatusEnum.ORDER_OPERATE_NULLSTALL); // 车位不匹配
		}
		if (order.getStatus().intValue() != order.ORDERS_STATUS_UNPAY) {
			throw new BusinessException(StatusEnum.ORDER_OPERATE_NOUNPAID); // //非预约中订单
		}

		// 计算金额
		/*ReqStrategy reqStrategy = new ReqStrategy();
		reqStrategy.setBeginTime(order.getBeginTime().getTime());
		reqStrategy.setEndTime(order.getEndTime().getTime());
		reqStrategy.setStrategyId(order.getStrategyId());
		Map<String, Object> res = strategyBaseClient.fee(reqStrategy);*/
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> param = new HashMap<String,Object>();
		param.put("stallId", order.getId());
		param.put("plateNo", order.getPlateNo());
		param.put("startTime", sdf.format(order.getCreateTime()));
		param.put("endTime", sdf.format(new Date()));
		Map<String, Object> res = this.strategyFeeClient.amount(param);
		Double totalAmount = 0d;
		if(res != null) {
			String totalStr = res.get("chargePrice").toString();
			String totalAmountStr = new java.text.DecimalFormat("0.00").format(Double.valueOf(totalStr));
			totalAmount = Double.valueOf(totalAmountStr);
		}

		Date d = new Date();
		// 关闭订单
		Map<String, Object> map = new HashMap<>();
		map.put("endTime", sdf.format(d));
		map.put("updateTime", sdf.format(d));
		map.put("statusTime", sdf.format(d));
		map.put("statusHistory", 1);
		map.put("status", 6);
		map.put("id", oorb.getOrderId());
		map.put("actualAmount", BigDecimal.valueOf(totalAmount));
		orderClient.updateClose(map);

		// 更新详情
		map.clear();
		map.put("endTime", sdf.format(d));
		map.put("ordNo", order.getOrderNo());
		orderClient.updateDetail(map);

		// 插入订单记录
		ResOrderOperateLog ool = new ResOrderOperateLog();
		ool.setCreateTime(new Date());
		ool.setOrderId(oorb.getOrderId());
		ool.setOperatorId(user.getId());
		ool.setSource(ool.ADMIN);
		ool.setStallId(oorb.getStallId());
		ool.setOperation(ool.SUSPENDED);
		ool.setRemark(oorb.getRemark());
		ool.setRemarkId(oorb.getRemarkId());
		ool.setStatus(ool.SUCCESS);
		orderClient.savel(ool);
		// 查车位
		ResStallEntity stall = stallClient.findById(order.getStallId());
		// 更新车位
		ReqStall reqStall = new ReqStall();
		reqStall.setId(oorb.getStallId());
		reqStall.setStatus(reqStall.STATUS_USED);
		reqStall.setBindOrderStatus(reqStall.ORDER_SUSPENDED);
		stallClient.updateStatus(reqStall);

		// 更新redis
		ResRedisOrders resRedisOrders = (ResRedisOrders) this.redisService
				.get(RedisKey.LINKMORE_APP_ORDER_KEY.key + order.getUserId());
		if (resRedisOrders != null) {
			resRedisOrders.setStatus(resRedisOrders.ORDERS_STATUS_PENDING);
			resRedisOrders.setEndTime(new Date());
			this.redisService.set(RedisKey.LINKMORE_APP_ORDER_KEY.key + order.getUserId(), resRedisOrders);
		}

		// 通知闸机
		// 查询车区
		ResPrefectureDetail pre = prefectureClient.findById(stall.getPreId());
		BaseDict baseDict = baseDictMapper.selectByPrimaryKey(pre.getBaseDictId()); // 根据字典id 去查询字典值
		// 查订单
		ResUserOrder neworder = orderClient.findOrderById(oorb.getOrderId());
		ReqSms req = new ReqSms();
		req.setMobile(order.getUsername());
		req.setSt(SmsTemplate.ORDER_SUSPEND_NOTICE);
		tell(neworder, req,1);
	}

	public void tell(ResUserOrder orders, ReqSms req,int Type) {
		TaskPool.getInstance().task(new Runnable() {
			@Override
			public void run() {
				try {
					//推送给个人版
					String title = "订单操作";
					String content = (Type ==1?"订单已被管理员挂起":"订单已被管理员关闭");
					PushType type = (Type ==1?PushType.ORDER_STAFF_SUSPEND_NOTICE:PushType.ORDER_STAFF_CLOSED_NOTICE);
					String bool = "true";
					Token token = (Token) redisService.get(staffUserFactory.createUserIdRedisKey(orders.getUserId(), "1"));

					ReqPush rp = new ReqPush();
					rp.setAlias(orders.getUserId().toString());
					rp.setTitle(title);
					rp.setContent(content);
					rp.setClient(token.getClient());
					rp.setType(type);
					rp.setData(bool);
					pushClient.push(rp);
				} catch (Exception e) {
					log.info("push sms erro");
				}
				try {
					if (req != null) {
						smsClient.send(req);
					}
				} catch (Exception e) {
					log.info("send sms erro");
				}
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("orderNo", orders.getOrderNo());
					map.put("beginTime", orders.getBeginTime());
					map.put("endTime", orders.getEndTime());
					map.put("totalAmount", orders.getTotalAmount());
					map.put("actualAmount", orders.getActualAmount());
					map.put("plateNo", orders.getPlateNo());
					map.put("preId", orders.getPreId());
					map.put("dockId", orders.getDockId());
					map.put("status", orders.getStatus());
					String json = JsonUtil.toJson(map);
					HttpUtil.sendJson("http://192.168.1.172:8086/order/orderDeal", json);
				} catch (Exception e) {
					log.info("tell lock erro");
				}
			}
		});
	}

	/**
	 * 关闭订单
	 */
	@Override
	public void close(OrderOperateRequestBean oorb, HttpServletRequest request) {
		CacheUser user = (CacheUser) this.redisService
				.get(RedisKey.STAFF_STAFF_AUTH_USER.key + TokenUtil.getKey(request));
		
		if (user == null) {
			throw new BusinessException(StatusEnum.USER_APP_NO_LOGIN);
		}
		// 查订单
		ResUserOrder order = orderClient.findOrderById(oorb.getOrderId());
		if (order == null) {
			throw new BusinessException(StatusEnum.ORDER_OPERATE_NULLORDER); // 订单不存在
		}
		if (!order.getStallId().equals(oorb.getStallId())) {
			throw new BusinessException(StatusEnum.ORDER_OPERATE_NULLSTALL); // 车位不匹配
		}
		if (order.getStatus().intValue() != order.ORDERS_STATUS_UNPAY) {
			throw new BusinessException(StatusEnum.ORDER_OPERATE_NOUNPAID); // //非预约中订单
		}
		// 关闭订单
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> map = new HashMap<>();
		map.put("endTime", sdf.format(d));
		map.put("updateTime", sdf.format(d));
		map.put("statusTime", sdf.format(d));
		map.put("statusHistory", 2);
		map.put("status", 7);
		map.put("id", oorb.getOrderId());
		orderClient.updateClose(map);
		// 更新详情
		map.clear();
		map.put("endTime", sdf.format(d));
		map.put("ordNo", order.getOrderNo());
		orderClient.updateDetail(map);
		// 插入订单记录
		ResOrderOperateLog ool = new ResOrderOperateLog();
		ool.setCreateTime(new Date());
		ool.setOrderId(oorb.getOrderId());
		ool.setOperatorId(user.getId());
		ool.setSource(ool.ADMIN);
		ool.setStallId(oorb.getStallId());
		ool.setOperation(ool.ORDER_ClOSE);
		ool.setRemark(oorb.getRemark());
		ool.setRemarkId(oorb.getRemarkId());
		ool.setStatus(ool.SUCCESS);
		orderClient.savel(ool);
		// 查车位
		ResStallEntity stall = stallClient.findById(order.getStallId());
		// 更新车位
		ReqStall reqStall = new ReqStall();
		reqStall.setId(oorb.getStallId());
		reqStall.setStatus(reqStall.STATUS_USED);
		reqStall.setBindOrderStatus(reqStall.ORDER_ClOSE);
		stallClient.updateStatus(reqStall);
		// 更新redis
		this.redisService.remove("freelock_key:" + stall.getPreId(), new Object[] { stall.getLockSn() });

		// 通知闸机
		ResPrefectureDetail pre = prefectureClient.findById(stall.getPreId()); // 查询车区
		BaseDict baseDict = baseDictMapper.selectByPrimaryKey(pre.getBaseDictId()); // 根据字典id 去查询字典值
		ResUserOrder neworder = orderClient.findOrderById(oorb.getOrderId()); // 查订单
		tell(neworder, null,2);
	}

	

}
