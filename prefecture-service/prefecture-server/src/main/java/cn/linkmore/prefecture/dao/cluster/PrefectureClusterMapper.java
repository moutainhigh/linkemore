package cn.linkmore.prefecture.dao.cluster;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import cn.linkmore.prefecture.controller.app.response.ResOpenPres;
import cn.linkmore.prefecture.controller.app.response.ResPrefecture;
import cn.linkmore.prefecture.entity.PrefectureView;
import cn.linkmore.prefecture.response.ResPre;
import cn.linkmore.prefecture.response.ResPreExcel;
import cn.linkmore.prefecture.response.ResPreGateway;
import cn.linkmore.prefecture.response.ResPreList;
import cn.linkmore.prefecture.response.ResPrefectureDetail;
/**
 * dao 车区
 * @author jiaohanbin
 * @version 2.0
 *
 */
@Mapper
public interface PrefectureClusterMapper {

	ResPrefectureDetail findById(Long id);

	List<ResPrefecture> findPreByStatusAndGPS(Map<String, Object> paramMap);

	List<ResPrefecture> findPreByStatusAndGPS1(Map<String, Object> paramMap);

	List<ResPre> findByIds(List<Long> list);
	
	List<Long> findPreIdList();

	List<ResPreExcel> findExportList();

	List<ResPreList> findSelectList();
	
	List<ResPre> findTreeList(Map<String, Object> param);

	List<ResPreExcel> findPage(Map<String, Object> param);

	Integer count(Map<String, Object> param);

	List<ResPre> findListByCityId(Long cityId);

	Integer check(Map<String, Object> param);

	List<ResPrefectureDetail> findAll();
	/**
	 * 校验车区是否存在
	 * @param param
	 * @return
	 */
	ResPrefectureDetail checkName(Map<String, Object> param);

	/**
	 * @Description  
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	List<cn.linkmore.prefecture.response.ResPrefecture> findPreList();

	List<ResPrefectureDetail> findList(Map<String, Object> param);
	
	/**
	 * 查询所有车区
	 * @return
	 */
	List<ResPreGateway> findPreGateList();

	/**
	 * @Description  根据条件查询车区
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	List<ResPre> findPreByIds(Map<String,Object> map);
	
	List<ResPreList> findSelectListByUser(Map<String,Object> map);

	/**
	 * @Description  根据分组查询
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	ResPrefectureDetail findByGateway(String groupCode);
	
	
	
	List<ResOpenPres> findByAppid(String appId);
	/**
	 * 根据车区id获取虚拟空闲车位数据
	 * @param id
	 * @return
	 */
	List<PrefectureView> findVmList(Long id);
	
}