package cn.linkmore.account.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.linkmore.account.dao.cluster.VehicleMarkManageClusterMapper;
import cn.linkmore.account.dao.master.VehicleMarkManageMasterMapper;
import cn.linkmore.account.entity.VehicleMarkManage;
import cn.linkmore.account.request.ReqVehicleMark;
import cn.linkmore.account.response.ResVechicleMark;
import cn.linkmore.account.service.UserService;
import cn.linkmore.account.service.VehicleMarkManageService;
import cn.linkmore.bean.exception.BusinessException;
import cn.linkmore.bean.exception.StatusEnum;
import cn.linkmore.util.ObjectUtils;
/**
 * @author   GFF
 * @Date     2018年5月21日
 * @Version  v2.0
 */
@Service
public class VehicleMarkManageServiceImpl implements VehicleMarkManageService {

	@Resource
	private VehicleMarkManageClusterMapper vehicleMarkManageClusterMapper;
	@Resource
	private VehicleMarkManageMasterMapper vehicleMarkManageMasterMapper;
	@Resource
	private UserService userService;
	
	@Override
	public List<VehicleMarkManage> findByUserId(Long userId) {
		return this.vehicleMarkManageClusterMapper.findByUserId(userId);
	}

	@Override
	@Transactional
	public void save(ReqVehicleMark bean) {
		List<VehicleMarkManage> list = this.findByUserId(bean.getUserId());
		if(list.size() < 4){
			//检查车牌号是否已经存在
			List<String> fieldVlaue = ObjectUtils.findFieldVlaue(list, "vehMark", new String[]{"vehMark"}, new String[] {bean.getVehMark()});
			if(fieldVlaue.size() > 0){
				throw new BusinessException(StatusEnum.ACCOUNT_PLATE_EXISTS);
			}else{
				VehicleMarkManage manage = new VehicleMarkManage();
				manage.setVehUserId(bean.getUserId().toString());
				manage.setVehMark(bean.getVehMark());
				manage.setCreateTime(new Date());
				manage.setUpdateTime(new Date());
				vehicleMarkManageMasterMapper.insertSelective(manage);
				return;
			}
		}
		throw new BusinessException(StatusEnum.ACCOUNT_PLATE_LIMIT);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		this.vehicleMarkManageMasterMapper.deleteById(id);
	}

	@Override
	public List<ResVechicleMark> findResList(Long userId) {
		return this.vehicleMarkManageClusterMapper.findResList(userId);
	}

	@Override
	public ResVechicleMark findById(Long id) {
		ResVechicleMark manage = this.vehicleMarkManageClusterMapper.findById(id);
		return manage;
	}

	
}
