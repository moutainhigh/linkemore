package cn.linkmore.prefecture.service;

import java.util.List;
import java.util.Map;

import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.prefecture.entity.StrategyBase;
import cn.linkmore.prefecture.request.ReqCheck;
import cn.linkmore.prefecture.request.ReqStrategyBase;
import cn.linkmore.prefecture.response.ResFeeStrategy;
import cn.linkmore.prefecture.response.ResStrategyBase;

/**
 * Service接口 - 计费策略信息
 * @author jiaohanbin
 * @version 2.0
 *
 */
public interface StrategyBaseService {
	/**
	 * 根据订单详情中计费策略id查询
	 * @param id
	 * @return
	 */
	StrategyBase findById(Long id);
	/**
	 * 新增
	 * @param reqStrategyBase
	 * @return
	 */
	int save(ReqStrategyBase reqStrategyBase);
	/**
	 * 更新
	 * @param reqStrategyBase
	 * @return
	 */
	int update(ReqStrategyBase reqStrategyBase);
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	int delete(List<Long> ids);
	/**
	 * 校验
	 * @param reqCheck
	 * @return
	 */
	Integer check(ReqCheck reqCheck);
	/**
	 * 列表
	 * @param pageable
	 * @return
	 */
	ViewPage findPage(ViewPageable pageable);
	
	List<ResStrategyBase> findList(Map<String, Object> param);
	/**
	 * 计费策略下拉列表
	 * @param param
	 * @return
	 */
	List<ResFeeStrategy> findSelectList(Map<String, Object> param);
}
