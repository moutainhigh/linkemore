package cn.linkmore.coupon.dao.cluster;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import cn.linkmore.coupon.response.ResSubject;
import cn.linkmore.coupon.response.ResSubjectBean;
@Mapper
public interface SubjectClusterMapper {

    ResSubject findById(Long id);

	Integer count(Map<String, Object> param);

	List<ResSubjectBean> findPage(Map<String, Object> param);

	Integer check(Map<String, Object> param);
	/**
	 * 查询切换车位失败专题列表
	 * @return
	 */
	List<ResSubject> findSubjectList();
	/**
	 * 查询品牌专题列表
	 * @param type 
	 * @return
	 */
	List<ResSubject> findBrandSubjectList(Integer type);
}