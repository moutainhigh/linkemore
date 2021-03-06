package cn.linkmore.enterprise.dao.cluster;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import cn.linkmore.enterprise.entity.EntBrandStall;
import cn.linkmore.enterprise.response.ResBrandAd;
import cn.linkmore.enterprise.response.ResBrandStall;

/**
 * 品牌车位
 * @author jiaohanbin
 * @version 2.0
 *
 */
@Mapper
public interface EntBrandStallClusterMapper {

    ResBrandStall findById(Long id);

	Integer check(Map<String, Object> param);

	List<ResBrandStall> findByBrandPreId(Long id);

	Integer count(Map<String, Object> param);

	List<ResBrandStall> findPage(Map<String, Object> param);
	
	List<ResBrandStall> findByIds(List<Long> ids);

}