package cn.linkmore.prefecture.controller.staff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.linkmore.bean.common.Constants;
import cn.linkmore.bean.common.ResponseEntity;
import cn.linkmore.bean.exception.BusinessException;
import cn.linkmore.bean.exception.StatusEnum;
import cn.linkmore.prefecture.controller.staff.request.ReqAssignStall;
import cn.linkmore.prefecture.controller.staff.request.ReqLockIntall;
import cn.linkmore.prefecture.controller.staff.request.ReqStaffStallList;
import cn.linkmore.prefecture.controller.staff.response.ResGateway;
import cn.linkmore.prefecture.controller.staff.response.ResLockGatewayList;
import cn.linkmore.prefecture.controller.staff.response.ResSignalHistory;
import cn.linkmore.prefecture.controller.staff.response.ResStaffNewAuth;
import cn.linkmore.prefecture.controller.staff.response.ResStaffPreList;
import cn.linkmore.prefecture.controller.staff.response.ResStaffStallDetail;
import cn.linkmore.prefecture.controller.staff.response.ResStaffStallList;
import cn.linkmore.prefecture.controller.staff.response.ResStaffStallSn;
import cn.linkmore.prefecture.service.PrefectureService;
import cn.linkmore.prefecture.service.StallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "Stall-Staff", description = "车位管理【管理版】")
@RestController
@RequestMapping("/staff/stall")
@Validated
public class StaffStallController {

	@Resource
	private PrefectureService prefectureService;
	@Resource
	private StallService stallService;
	
	@RequestMapping(value="/pre-list",method=RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "车区列表", notes = "根查询车区列表", consumes = "application/json")
	public ResponseEntity<List<ResStaffPreList>> findPreList(HttpServletRequest request, @ApiParam("城市id") @NotNull(message="城市id不能为空") @RequestParam("cityId") Long cityId) {
		List<ResStaffPreList> list = this.stallService.findPreList(request,cityId);
		return ResponseEntity.success(list, request);
	}
	@RequestMapping(value="/stall-list",method=RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "车位列表", notes = "根查询车位列表", consumes = "application/json")
	public ResponseEntity<List<ResStaffStallList>> findStallList(HttpServletRequest request, @RequestBody @Validated ReqStaffStallList staffList) {
		List<ResStaffStallList> list = this.stallService.findStallList(request,staffList);
		return ResponseEntity.success(list, request);
	}
	
	@RequestMapping(value="/stall-detail",method=RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "车位详情", notes = "车位详情", consumes = "application/json")
	public ResponseEntity<ResStaffStallDetail> findStaffStallDetails(HttpServletRequest request,  @ApiParam("车位id") @NotNull(message="车位id不能为空") @RequestParam("stallId") Long stallId) {
		ResStaffStallDetail detail = this.stallService.findStaffStallDetails(request,stallId);
		return ResponseEntity.success(detail, request);
	}
	
	@RequestMapping(value="/stall-detail-sn",method=RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "根据车位锁编号查询车位编号", notes = "根据车位锁编号查询车位编号", consumes = "application/json")
	public ResponseEntity<ResStaffStallSn> findStaffStallSn(HttpServletRequest request,  @ApiParam("车位锁编号") @NotNull(message="sn") @RequestParam("sn") String sn,
			@ApiParam(value="车区",required=false) @RequestParam(value="preId",required=false) Long preId
			) {
		ResStaffStallSn detail = this.stallService.findStaffStallSn(request,sn,preId);
		return ResponseEntity.success(detail, request);
	}
	
	@RequestMapping(value="/lock­signal­history",method=RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "查询车位锁在一定时间内的信号强度", notes = "查询车位锁在一定时间内的信号强度", consumes = "application/json")
	public ResponseEntity<ResSignalHistory> lockSignalHistory(HttpServletRequest request,  @ApiParam("车位锁编号") @NotNull(message="sn") @RequestParam("sn") String sn) {
		ResSignalHistory history = this.stallService.lockSignalHistory(request,sn);
		return ResponseEntity.success(history, request);
	}
	
	@ApiOperation(value = "指定车位操作", notes = "指定车位操作")
	@RequestMapping(value = "/staff-assign", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> staffAssign(HttpServletRequest request, @Validated @RequestBody ReqAssignStall bean) {
		ResponseEntity<String> response = null;
		String plate = this.stallService.staffAssign(bean,request);
		response = ResponseEntity.success(plate, request);
		return response;
	}
	
	@ApiOperation(value = "删除指定车位操作", notes = "删除指定车位操作")
	@RequestMapping(value = "/assign-del", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Void> staffAssignDel(HttpServletRequest request, @Validated @RequestBody ReqAssignStall bean) {
		this.stallService.staffAssignDel(bean);
		return ResponseEntity.success(null, request);
	}
	
	@ApiOperation(value = "复位", notes = "复位", consumes = "application/json")
	@RequestMapping(value = "/reset",method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> reset(@RequestParam("stallId") @ApiParam("车位id") @NotNull(message="车位不能为null") Long stallId,HttpServletRequest request){
		this.stallService.reset(stallId,request);
		return ResponseEntity.success("复位成功", request);
	}
	
	
	@ApiOperation(value = "地锁安装", notes = "地锁安装")
	@RequestMapping(value = "/installLock", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> installLock(HttpServletRequest request, @Validated @RequestBody ReqLockIntall reqLockIntall) {
		ResponseEntity<Boolean> response = null;
		try {
			this.stallService.install(reqLockIntall,request);
			response = ResponseEntity.success(true, request);
		} catch (BusinessException e) {
			response = ResponseEntity.fail(e.getStatusEnum(), request);
		} catch (Exception e) {
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		return response;
	}
	@ApiOperation(value = "地锁安装2.0.1", notes = "地锁安装")
	@RequestMapping(value = "/v2.0.1/installLock", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> installLockTwo(HttpServletRequest request, @Validated @RequestBody ReqLockIntall reqLockIntall) {
		ResponseEntity<Boolean> response = null;
		try {
			this.stallService.installLock(reqLockIntall,request);
			response = ResponseEntity.success(true, request);
		} catch (BusinessException e) {
			response = ResponseEntity.fail(e.getStatusEnum(), request);
		} catch (Exception e) {
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		return response;
	}
	@ApiOperation(value = "删除车位锁", notes = "删除车位锁")
	@RequestMapping(value = "/remove-stall-lock", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> removeStallLock(HttpServletRequest request, @ApiParam(value="车位id",required=true) @NotNull(message="车位id不能为空") @RequestParam(value = "stallId",required= true) Long stallId ) {
		ResponseEntity<Boolean> response = null;
		try {
			this.stallService.removeStallLock(stallId,request);
			response = ResponseEntity.success(true, request);
		} catch (BusinessException e) {
			response = ResponseEntity.fail(e.getStatusEnum(), request);
		} catch (Exception e) {
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		return response;
	}
	
	@ApiOperation(value = "获取最新人员的权限", notes = "获取最新人员的权限")
	@RequestMapping(value = "/find-new-auth", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<ResStaffNewAuth>> findNewAuth(HttpServletRequest request,  @ApiParam(value="城市id 非必填默认查询人员所有的权限",required=false) @RequestParam(value = "cityId",required= false) Long cityId) {
		try {
			return ResponseEntity.success(this.stallService.findNewAuth(cityId,request), request);
		} catch (BusinessException e) {
			return ResponseEntity.fail(e.getStatusEnum(), request);
		} catch (Exception e) {
			return ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
	}
	@ApiOperation(value = "绑定网关", notes = "绑定网关")
	@GetMapping(value = "/bind-group")
	@ResponseBody
	public ResponseEntity<Boolean> bindGroup(HttpServletRequest request, @ApiParam(value="车区id",required=true) @NotNull(message="车区id不能为空") @RequestParam(value = "preId",required= true) Long preId ,
			@ApiParam(value="网关编号",required=true) @NotBlank(message="网关编号不能为空") @RequestParam(value = "serialNumber",required= true) String serialNumber 
			){
		Boolean falg = this.prefectureService.bindGroup(preId,serialNumber,request);
		return ResponseEntity.success(falg, request);
	}
	
	@ApiOperation(value = "删除(解除)绑定网关", notes = "删除绑定网关")
	@GetMapping(value = "/unbind-group")
	@ResponseBody
	public ResponseEntity<Boolean> unBindGroup(HttpServletRequest request, @ApiParam(value="分组编号",required=true) @NotNull(message="分组编号不能为空") @RequestParam(value = "groupCode",required= true) String groupCode ,
			@ApiParam(value="网关编号",required=true) @NotBlank(message="网关编号不能为空") @RequestParam(value = "serialNumber",required= true) String serialNumber 
			){
		Boolean falg = this.prefectureService.unBindGroup(groupCode,serialNumber,request);
		return ResponseEntity.success(falg, request);
	}
	
	@ApiOperation(value = "删除(解除)网关绑定的锁", notes = "删除(解除)网关绑定的锁")
	@GetMapping(value = "/unbind-lock")
	@ResponseBody
	public ResponseEntity<Boolean> unBindLock(HttpServletRequest request ,
			@ApiParam(value="网关编号",required=true) @NotBlank(message="编号不能为空") @RequestParam(value = "serialNumber",required= true) String serialNumber ,
			@ApiParam(value="锁编号",required=true) @NotBlank(message="锁编号") @RequestParam(value = "lockSn",required= true) String lockSn 
			){
		Boolean falg = this.prefectureService.removeLock(lockSn,request);
		return ResponseEntity.success(falg, request);
	}
	
	@ApiOperation(value = "查询网关list", notes = "查询网关list")
	@GetMapping(value = "/find-gateway-group")
	@ResponseBody
	public ResponseEntity<List<ResGateway>> findGatewayGroup(HttpServletRequest request, @ApiParam(value="车区id",required=true) @NotNull(message="车区id不能为空") @RequestParam(value = "preId",required= true) Long preId
			){
		List<ResGateway> group = this.prefectureService.findGatewayGroup(preId,request);
		return ResponseEntity.success(group, request);
	}
	
	@ApiOperation(value = "查询网关(扫一扫)", notes = "查询网关(扫一扫")
	@GetMapping(value = "/find-gateway-serialNumber")
	@ResponseBody
	public ResponseEntity<cn.linkmore.prefecture.controller.staff.response.ResGatewayDetails> getGatewayDetails(HttpServletRequest request, @ApiParam(value="网关编号",required=true) @NotNull(message="网关编号不能为空") @RequestParam(value = "serialNumber",required= true) String serialNumber
			){
		cn.linkmore.prefecture.controller.staff.response.ResGatewayDetails details = prefectureService.getGatewayDetails(serialNumber,request);
		return ResponseEntity.success(details, request);
	}
	
	@ApiOperation(value = "加载锁", notes = "加载锁")
	@GetMapping(value = "/load-lock")
	@ResponseBody
	public ResponseEntity<Boolean> loadAllLock(HttpServletRequest request, @ApiParam(value="网关编号",required=true) @NotNull(message="网关不能为空") @RequestParam(value = "serialNumber",required= true) String serialNumber	){
		Boolean falg = prefectureService.loadLock(request,serialNumber);
		return ResponseEntity.success(falg, request);
	}
	
	@ApiOperation(value = "重启网关", notes = "重启网关")
	@GetMapping(value = "/restart-gateway")
	@ResponseBody
	public ResponseEntity<Boolean> restartGateway(HttpServletRequest request, @ApiParam(value="网关编号",required=true) @NotNull(message="网关不能为空") @RequestParam(value = "serialNumber",required= true) String serialNumber	){
		Boolean falg = prefectureService.restartGateway(request,serialNumber);
		return ResponseEntity.success(falg, request);
	}
	@ApiOperation(value = "查询锁绑定的网关/未绑定的网关(用于展示批量更新车位锁网关的列表)", notes = "重启网关")
	@GetMapping(value = "/find-lock-gateways")
	@ResponseBody
	public ResponseEntity<List<ResLockGatewayList>> findLockGateways(HttpServletRequest request, @ApiParam(value="锁编号编号",required=true) @NotNull(message="锁编号不能为空") @RequestParam(value = "lockSn",required= true) String lockSn	,
			@ApiParam(value="车区id",required=true) @NotNull(message="车区id不能为空") @RequestParam(value = "preId",required= true) Long preId	){
		List<ResLockGatewayList> gateways = stallService.findLockGateways(request,lockSn,preId);
		return ResponseEntity.success(gateways, request);
	}
	
	@ApiOperation(value = "更新车位锁绑定的网关(批量更新)", notes = "更新车位锁绑定的网关(批量更新)")
	@PutMapping(value = "/edit-lock-bind-gateway")
	@ResponseBody
	public ResponseEntity<Boolean> editLockBindGateway(HttpServletRequest request, @ApiParam(value="锁编号",required=true) @NotNull(message="锁编号不能为空") @RequestParam(value = "lockSn",required= true) String lockSn	
			,@ApiParam("网关编号 多个网关编号,分隔('网关编号1','网关编号2')") @NotNull(message="网关编号不能为空") @RequestParam(value = "serialNumbers",required= true) String serialNumbers
			){
		Boolean falg = stallService.editLockBindGateway(request,serialNumbers,lockSn);
		return ResponseEntity.success(falg, request);
	}
	@ApiOperation(value = "确认绑定", notes = "确认绑定")
	@GetMapping(value = "/confirm")
	@ResponseBody
	public ResponseEntity<Boolean> confirm(HttpServletRequest request, @ApiParam(value="网关编号",required=true) @NotNull(message="网关编号不能为空") @RequestParam(value = "serialNumber",required= true) String serialNumber
			){
		Boolean flag = prefectureService.confirm(serialNumber,request);
		return ResponseEntity.success(flag, request);
	}
	
	@ApiOperation(value = "根据车区id获取车位楼层", notes = "根据车区id获取车位楼层", consumes = "application/json")
	@RequestMapping(value = "/get-floor", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<String>> getFloor(@Validated @RequestParam(value="preId", required=true) Long preId, HttpServletRequest request) {
		cn.linkmore.prefecture.response.ResPrefectureDetail detail =  this.prefectureService.findById(preId);
		List<String> floorList = new ArrayList<String>();
		if(detail !=null && StringUtils.isNotBlank(detail.getUnderLayer())) {
			floorList = Arrays.asList(detail.getUnderLayer().split("、"));
		}else {
			floorList.add(Constants.FLOOR_ALL);
		}
		return ResponseEntity.success(floorList, request);
	}
	
}
