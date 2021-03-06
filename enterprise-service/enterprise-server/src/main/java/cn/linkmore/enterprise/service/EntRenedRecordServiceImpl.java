package cn.linkmore.enterprise.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.linkmore.enterprise.dao.cluster.EntRentedRecordClusterMapper;
import cn.linkmore.enterprise.dao.master.EntRentedRecordMasterMapper;
import cn.linkmore.enterprise.entity.EntRentedRecord;
import cn.linkmore.enterprise.request.ReqPreDetails;
import cn.linkmore.enterprise.request.ReqStaffPreOwnerStall;
import cn.linkmore.enterprise.response.ResEntRentedRecord;
import cn.linkmore.enterprise.response.ResOwnerStallDetails;
import cn.linkmore.enterprise.response.ResOwnerStallReportForms;
import cn.linkmore.enterprise.response.ResStaffOwnerUseStall;

@Service
public class EntRenedRecordServiceImpl implements EntRenedRecordService {

	@Resource
	private EntRentedRecordMasterMapper entRentedRecordMasterMapper;
	@Resource
	private EntRentedRecordClusterMapper entRentedRecordClusterMapper;
	@Override
	public void updateDownTime(Long stallId) {
		EntRentedRecord id = entRentedRecordClusterMapper.findByStallId(stallId);
		if(id != null) {
			Map<String,Object> map = new HashMap<>();
			map.put("id", id.getId());
			map.put("date", new Date());
			entRentedRecordMasterMapper.updateDownTime(map);
		}
	}
	@Override
	public EntRentedRecord findLastPlateNumber(Long stallId) {
		EntRentedRecord record = this.entRentedRecordClusterMapper.findByStallId(stallId);
		return record != null && record.getStatus() == 0 ? record : null;
	}
	@Override
	public List<ResEntRentedRecord> findLastPlateNumberByPreId(Long preId) {
		return this.entRentedRecordClusterMapper.findLastPlateNumberByPreId(preId);
	}
	
	@Override
	public EntRentedRecord findByUserId(Long userId) {
		EntRentedRecord record = this.entRentedRecordClusterMapper.findByUser(userId);
		return record;
	}
	@Override
	public List<ResStaffOwnerUseStall> findPreUseNumber(ReqStaffPreOwnerStall reqStaffPreOwnerStall) {
		return this.entRentedRecordClusterMapper.findPreUseNumber(reqStaffPreOwnerStall);
	}
	@Override
	public ResOwnerStallDetails findPreDetails(ReqPreDetails reqPreDetails) {
		return this.entRentedRecordClusterMapper.findPreDetails(reqPreDetails);
	}
	@Override
	public ResOwnerStallReportForms findOwnerStallReportForms(ReqPreDetails details) {
		return this.entRentedRecordClusterMapper.findOwnerStallReportForms(details);
	}
	
	
	
}
