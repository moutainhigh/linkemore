package cn.linkmore.user.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.linkmore.account.request.ReqVehicleMark;
import cn.linkmore.account.response.ResVechicleMark;
import cn.linkmore.bean.common.ResponseEntity;
import cn.linkmore.user.service.VehicleMarkManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
	
/**
 * 车牌号管理
 * @author   GFF 
 * @Date     2018年5月21日
 * @Version  v2.0
 */
@Api(tags="Vehicle_mark",description="车牌号管理")
@RestController
@RequestMapping("/vehicle_mark")
public class VehicleMarkController{

	@Resource
	private VehicleMarkManageService vehicleMarkManageService;
	
	
	/**
	 * @Description  新增
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@ApiOperation(value="新增",notes="车牌号必填,车牌号规则校验", consumes = "application/json")
	@RequestMapping(value = "/v2.0", method = RequestMethod.POST)
	public ResponseEntity<?> create( @RequestBody ReqVehicleMark bean,HttpServletRequest request) {
		this.vehicleMarkManageService.save(bean,request);
		return ResponseEntity.success(null, request);
	}
	
	/**
	 * @Description  删除
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@ApiOperation(value="删除",notes="根据id删除", consumes = "application/json")
	@RequestMapping(value = "/v2.0", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@RequestParam("id") Long id,HttpServletRequest request){
		this.vehicleMarkManageService.deleteById(id);
		return ResponseEntity.success(null, request);
	}
	
	/**
	 * @Description  列表
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@ApiOperation(value="列表list",notes="查询该用户所有车牌", consumes = "application/json")
	@RequestMapping(value = "/v2.0", method = RequestMethod.GET)
	public ResponseEntity<?> list(HttpServletRequest request){
		List<ResVechicleMark> list = vehicleMarkManageService.selectResList(request);
		return ResponseEntity.success(list, request);
	}
	
	
	/**
	 * @Description  根据id查询
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@ApiOperation(value="根据id查询",notes="根据id查询车牌号详情", consumes = "application/json")
	@RequestMapping(value = "/v2.0", method = RequestMethod.GET)
	public ResponseEntity<?> selectById(@RequestParam("id") Long id,HttpServletRequest request) {
		ResVechicleMark mark = this.vehicleMarkManageService.selectById(id);
		return ResponseEntity.success(mark, request);
	}
	
}
