package cn.linkmore.prefecture.dao.cluster;

import java.util.List;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import cn.linkmore.prefecture.entity.AdminAuthPre;
import cn.linkmore.prefecture.response.ResAdminAuthPre;
import cn.linkmore.prefecture.response.ResStaffPres;

@Mapper
public interface AdminAuthPreClusterMapper {

	/**
	 * @Description  查询权限车区
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	List<AdminAuthPre> findList(Map<String, Object> map);

	/**
	 * @Description 根据车区id查询  
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	AdminAuthPre findById(Long preId);
	
	/**
	 * @Description 根据res车区id查询  
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	List<ResAdminAuthPre> findListRes(Map<String, Object> map);

	/**
	 * @Description  
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	List<ResStaffPres> findUserPres(Long id);

}