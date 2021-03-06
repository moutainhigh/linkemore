package cn.linkmore.account.dao.master;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import cn.linkmore.account.entity.User;
import cn.linkmore.account.response.ResUser;
/**
 * 用户(写)
 * @author   GFF
 * @Date     2018年5月23日
 * @Version  v2.0
 */
@Mapper
public interface UserMasterMapper {
    /**
     * @Description  
     * @Author   GFF 
     * @Version  v2.0
     */
    int deleteById(Long id);
    /**
     * deletebyids
     * @param ids
     * @return
     */
    int deleteByIds(List<Long> ids);
    
    /**
     * @Description  
     * @Author   GFF 
     * @Version  v2.0
     */
    int insert(User record);

    /**
     * @Description  
     * @Author   GFF 
     * @Version  v2.0
     */
    int insertSelective(User record);

    /**
     * @Description  
     * @Author   GFF 
     * @Version  v2.0
     */
    int updateByIdSelective(User record);

    /**
     * @Description  
     * @Author   GFF 
     * @Version  v2.0
     */
    int updateById(User record);

	/**
	 * @Description  
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	void updateByColumn(Map<String, Object> param);

	/**
	 * @Description  
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	void updateLoginTime(Map<String, Object> param);

	/**
	 * @Description  
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	void updateMobile(Map<String, Object> param);

	void insert(ResUser user);

	/**
	 * 更新订单数
	 * @param param
	 */
	void orderUpdate(Map<String, Object> param);

	/**
	 * 更新订单数
	 * @param param
	 */
	void checkoutUpdate(Map<String, Object> param);

	/**
	 * @Description  
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	void save(ResUser user);

	void updateFansStatus(Map<String, Object> param);
	
	
	/**
	 * @Description  更新密码
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	void updatePassword(Map<String, Object> map);
	
	void updateIds(List<ResUser> users);
	


}