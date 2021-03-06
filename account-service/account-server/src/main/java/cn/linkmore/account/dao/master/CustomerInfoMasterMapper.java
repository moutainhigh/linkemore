package cn.linkmore.account.dao.master;

import org.apache.ibatis.annotations.Mapper;

import cn.linkmore.account.entity.CustomerInfo;

/**
 * 用户数据录入mapper（写）
 * @author   GFF
 * @Date     2018年5月30日
 * @Version  v2.0
 */
@Mapper
public interface CustomerInfoMasterMapper {
    /**
     * @Description	根据id删除  
     * @Author   GFF 
     * @Version  v2.0
     */
    int deleteById(Long id);

    /**
     * @Description  新增
     * @Author   GFF 
     * @Version  v2.0
     */
    int save(CustomerInfo record);

    /**
     * @Description  
     * @Author   GFF 新增null处理
     * @Version  v2.0
     */
    int saveSelective(CustomerInfo record);

    /**
     * @Description  更新null处理
     * @Author   GFF 
     * @Version  v2.0
     */
    int updateByIdSelective(CustomerInfo record);

    /**
     * @Description  更新
     * @Author   GFF 
     * @Version  v2.0
     */
    int updateById(CustomerInfo record);
}