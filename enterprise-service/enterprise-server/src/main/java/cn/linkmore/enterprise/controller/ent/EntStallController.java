/**
 * 
 */
package cn.linkmore.enterprise.controller.ent;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import cn.linkmore.bean.common.ResponseEntity;
import cn.linkmore.bean.exception.StatusEnum;
import cn.linkmore.common.response.ResBaseDict;
import cn.linkmore.enterprise.controller.ent.request.ReqOperatStall;
import cn.linkmore.enterprise.controller.ent.request.ReqPreStall;
import cn.linkmore.enterprise.controller.ent.request.ReqStallUpDown;
import cn.linkmore.enterprise.controller.ent.response.ResDetailStall;
import cn.linkmore.enterprise.controller.ent.response.ResEntStalls;
import cn.linkmore.enterprise.controller.ent.response.ResStall;
import cn.linkmore.enterprise.controller.ent.response.ResStallName;
import cn.linkmore.enterprise.service.EntStallService;
import cn.linkmore.prefecture.response.ResStallBatteryLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author luzhishen
 * @Date 2018年7月20日
 * @Version v1.0
 */

@Api(tags = "stall",description="企业车位【物业版】", produces = "application/json")
@RestController
@RequestMapping("/ent/stall")
@Validated
public class EntStallController {
	
	@Autowired
	private EntStallService entStallService;
	
	@ApiOperation(value = "查询企业下停车场信息", notes = "查询企业下停车场信息", consumes = "application/json")
	@RequestMapping(value = "/select-pre-stalls",method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<ResEntStalls>> selectEntStalls(HttpServletRequest request){
		List<ResEntStalls> list = null;
		list = entStallService.selectEntStalls(request);
		return ResponseEntity.success(list, request);
	}
	
	@ApiOperation(value = "查询企业下停车场信息（新版）", notes = "查询企业下停车场信息（新版）", consumes = "application/json")
	@RequestMapping(value = "/select-pre-stalls-new",method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<ResEntStalls>> selectEntStallsNew(HttpServletRequest request){
		List<ResEntStalls> list = null;
		list = entStallService.selectEntStallsNew(request);
		return ResponseEntity.success(list, request);
	}
	
	@ApiOperation(value = "查询停车场车位列表", notes = "查询停车场车位列表", consumes = "application/json")
	@RequestMapping(value = "/select-stalls",method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<List<ResStallName>> selectEntStalls(@RequestBody @Validated ReqPreStall reqPreStall ,HttpServletRequest request){
		List<ResStallName> list = null;
		if(reqPreStall == null){
			list = new ArrayList<>();
			return ResponseEntity.success(list, request);
		}
		if(reqPreStall.getType() == 1) {
			reqPreStall.setType((short)0);
		}
		List<ResStallName> stalls = entStallService.selectStalls(request,reqPreStall.getPreId(),reqPreStall.getType(),reqPreStall.getStallName());
		return ResponseEntity.success(stalls, request);
	}
	
	@ApiOperation(value = "查询停车场车位列表", notes = "查询停车场车位列表", consumes = "application/json")
	@RequestMapping(value = "/select-stalls-new",method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<List<ResStall>> selectEntStallsNew(@RequestBody @Validated ReqPreStall reqPreStall ,HttpServletRequest request){
		List<ResStall> list = null;
		if(reqPreStall == null){
			list = new ArrayList<>();
			return ResponseEntity.success(list, request);
		}
		list = entStallService.selectEntStallsNew(request,reqPreStall.getPreId(),reqPreStall.getType(),reqPreStall.getStallName());
		return ResponseEntity.success(list, request);
	}
	
	@ApiOperation(value = "查询车位详细信息", notes = "查询车位详细信息", consumes = "application/json")
	@RequestMapping(value = "/select-detail-stalls",method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ResDetailStall> selectEntDetailStalls(@RequestParam("stall_id") @ApiParam("车位id") @NotNull(message="车位不能为null") Long stall_id ,HttpServletRequest request){
		if(stall_id == null){
			return null;
		}
		ResDetailStall detailStall = this.entStallService.selectEntDetailStalls(stall_id,request);
		return ResponseEntity.success(detailStall, request);
	}
		
	@ApiOperation(value = "操作车位", notes = "操作车位", consumes = "application/json")
	@RequestMapping(value = "/operate-stall",method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> operatStalls(@RequestBody ReqOperatStall reqOperatStall,HttpServletRequest request){
		try {
			this.entStallService.operatStalls(request,reqOperatStall.getStallId(),reqOperatStall.getState());
		} catch (Exception e) {
			return ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION.code,e.getMessage(), request);
		}
		return ResponseEntity.success("操作成功", request);
	}
	
	@ApiOperation(value = "车位上线", notes = "车位上线", consumes = "application/json")
	@RequestMapping(value = "/change-up",method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResponseEntity<String> changeUp(@RequestParam("stall_id") @ApiParam("车位id") @NotNull(message="车位不能为null") Long stall_id,HttpServletRequest request){
		try {
			this.entStallService.change(request,stall_id,1,0L,"车位上线");
		} catch (Exception e) {
			return ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION.code,e.getMessage(), request);
		}
		return ResponseEntity.success("车位上线成功", request);
	}
	
	@ApiOperation(value = "车位下线", notes = "车位下线", consumes = "application/json")
	@RequestMapping(value = "/change-down",method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> changeDown(@RequestBody ReqStallUpDown stall,HttpServletRequest request){
		try {
			this.entStallService.change(request,stall.getStallId(),2,stall.getRemarkId(),stall.getRemark());
		} catch (Exception e) {
			return ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION.code,e.getMessage(), request);
		}
		return ResponseEntity.success("车位下线成功", request);
	}
	
	@ApiOperation(value = "复位", notes = "复位", consumes = "application/json")
	@RequestMapping(value = "/reset",method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> reset(@RequestParam("stallId") @ApiParam("车位id") @NotNull(message="车位不能为null") Long stallId,HttpServletRequest request){
		this.entStallService.reset(stallId,request);
		return ResponseEntity.success("复位成功", request);
	}
	
	@ApiOperation(value = "下线原因", notes = "下线原因", consumes = "application/json")
	@RequestMapping(value = "/down-cause",method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<ResBaseDict>> downCause(HttpServletRequest request){
		List<ResBaseDict> cause = this.entStallService.downCause();
		return ResponseEntity.success(cause, request);
	}
	
	@ApiOperation(value = "车位地锁电池更换记录数据", notes = "车位地锁电池更换记录数据", consumes = "application/json")
	@RequestMapping(value = "/lock-change-record", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<cn.linkmore.enterprise.controller.ent.response.ResStallBatteryLog>> findLockChangeRecord(@RequestParam("stallId") Long stallId,HttpServletRequest request){
		ResponseEntity<List<cn.linkmore.enterprise.controller.ent.response.ResStallBatteryLog>> response = null;
		List<cn.linkmore.enterprise.controller.ent.response.ResStallBatteryLog> list=this.entStallService.findLockChangeRecord(stallId);
		response = ResponseEntity.success(list, request);
    	return response;
	}
	
	@ApiOperation(value = "降锁回调", notes = "降锁回调校验结果", consumes = "application/json")
	@RequestMapping(value = "/v2.0/down/result", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> downResult(HttpServletRequest request) { 
		Integer count = this.entStallService.downResult(request);
		ResponseEntity<?> response = null;
		if(count==0) {
			response =  ResponseEntity.success(count, request);
		}else if(count==1){
			response =  ResponseEntity.fail(StatusEnum.ORDER_LOCKDOWN_FAIL, request);
		}else if(count>1) {
			response = ResponseEntity.fail(StatusEnum.ORDER_FAIL_SWITCHLOCK, request);
		}
		return response;
	}
}
