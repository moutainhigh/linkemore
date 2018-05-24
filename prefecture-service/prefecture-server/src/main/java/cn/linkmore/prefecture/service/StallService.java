package cn.linkmore.prefecture.service;

import java.util.List;

import cn.linkmore.prefecture.response.ResStall;
import cn.linkmore.prefecture.response.ResStallEntity;

/**
 * Service接口 - 车位信息
 * @author jiaohanbin
 * @version 2.0
 *
 */
public interface StallService {
	/**
	 * 预约订单时，根据车位锁序列号查询车位
	 * @param id
	 */
	void order(Long id);
	/**
	 * 取消订单释放车位
	 * @param stallId
	 */
	boolean cancel(Long stallId);
	/**
	 * 结账立场释放车位
	 * @param stallId
	 * @return true 车位锁升起成功 false 车位锁升起失败
	 */
	boolean checkout(Long stallId);
	/**
	 * 升锁操作
	 * @param stallId
	 * @return true 车位锁升起成功 false 车位锁升起失败
	 */
	boolean uplock(Long stallId);
	/**
	 * 降锁操作
	 * @param stallId
	 * @return true 车位锁降下成功 false 车位锁降下失败
	 */
	boolean downlock(Long stallId);
	/**
	 * 根据车位id查询正常状态下车位信息
	 * @param preId
	 * @return
	 */
	List<ResStall> findStallsByPreId(Long preId);
	/**
	 * 根据车位id查询车位信息
	 * @param stallId
	 * @return
	 */
	ResStallEntity findById(Long stallId);
	/**
	 * 根据锁编号查询车位
	 * @param sn 编号
	 * @return
	 */
	ResStallEntity findByLock(String sn);
	
}
