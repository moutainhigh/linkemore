package cn.linkmore.enterprise.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.enterprise.controller.app.request.ReqBrandPre;
import cn.linkmore.enterprise.controller.app.response.ResEntBrandPreCity;
import cn.linkmore.enterprise.controller.app.response.ResEntBrandPreLeisure;
import cn.linkmore.enterprise.controller.app.response.ResEntBrandPreStrategy;
import cn.linkmore.enterprise.entity.EntBrandPre;
import cn.linkmore.enterprise.request.ReqCheck;

public interface EntBrandPreService {

	/**
	 * 分页查询
	 * 
	 * @param pageable
	 * @return
	 */
	ViewPage findPage(ViewPageable pageable);

	/**
	 * 根据条件查询
	 * 
	 * @param param
	 * @return
	 */
	List<EntBrandPre> findList(Map<String, Object> param);

	/**
	 * 保存
	 * 
	 * @param record
	 */
	int save(EntBrandPre record);

	/**
	 * 更新
	 * 
	 * @param record
	 * @return
	 */
	int update(EntBrandPre record);

	/**
	 * 删除
	 * 
	 * @param ids
	 * @return
	 */
	int delete(Long id);

	/**
	 * 检验属性存在
	 * @param reqCheck
	 * @return
	 */
	Integer check(ReqCheck reqCheck);

	List<ResEntBrandPreCity> list(ReqBrandPre rp, HttpServletRequest request);

	ResEntBrandPreStrategy findPreStrategy(Long brandPreId);

	List<ResEntBrandPreLeisure> getStallCount(HttpServletRequest request);

}
