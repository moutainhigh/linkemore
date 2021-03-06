package cn.linkmore.ops.ent.service;

import java.util.List;
import java.util.Map;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.enterprise.request.ReqAddEntPreture;
import cn.linkmore.enterprise.response.ResEntPrefecture;
import cn.linkmore.prefecture.response.ResPreList;

/**
 * 企业车区
 * @author   GFF
 * @Date     2018年7月27日
 * @Version  v2.0
 */
public interface PrefectrueService {

	/**
	 * @Description  分页查询
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	ViewPage findPage(ViewPageable pageable);

	/**
	 * @Description  新增 
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	void save(ReqAddEntPreture auth);

	/**
	 * @Description  更新
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	void update(ReqAddEntPreture auth);

	/**
	 * @Description  删除
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	void delete(List<Long> ids);

	/**
	 * @Description  查询企业下所有车区
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	List<ResEntPrefecture> findAll(Map<String, Object> map);

	/**
	 * @Description  查询未被创建的企业车区
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	List<ResPreList> findNotCreateEntPre();

}
