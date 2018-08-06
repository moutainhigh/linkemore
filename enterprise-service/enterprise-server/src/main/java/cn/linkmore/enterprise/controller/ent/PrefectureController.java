package cn.linkmore.enterprise.controller.ent;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.linkmore.bean.common.ResponseEntity;
import cn.linkmore.enterprise.controller.ent.response.ResCharge;
import cn.linkmore.enterprise.controller.ent.response.ResChargeDetail;
import cn.linkmore.enterprise.controller.ent.response.ResDayIncome;
import cn.linkmore.enterprise.controller.ent.response.ResDayTrafficFlow;
import cn.linkmore.enterprise.controller.ent.response.ResIncomeList;
import cn.linkmore.enterprise.controller.ent.response.ResPreOrderCount;
import cn.linkmore.enterprise.service.PrefectureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/ent/prefecture")
@Api(tags = "prefecture",description="车区运营【物业版】", produces = "application/json")
public class PrefectureController {

	@Resource
	private PrefectureService prefectureService;
	
	@RequestMapping(value="/pre-order-count" ,method=RequestMethod.GET)
	@ApiOperation(value = "查询每日车场订单收入", notes = "查询每日车场订单收入", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<List<ResPreOrderCount>> findPreList(HttpServletRequest request){
		return ResponseEntity.success(this.prefectureService.findPreList(request), request);
	}
	
	@RequestMapping(value="/pre-income" ,method=RequestMethod.GET)
	@ApiOperation(value = "查询车场近七日实收入", notes = "查询车场近七日实收入", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<BigDecimal> findPreDayIncome(
			@ApiParam(value="类型 0 7天 1 15天 2 30天",required=true,name="type") @NotNull(message="类型不能为空") @RequestParam("type") Short type ,
			@RequestParam("preId") @ApiParam("车区id") @NotNull(message="车区") Long preId,HttpServletRequest request){
		BigDecimal income = this.prefectureService.findPreDayIncome(type,preId,request);
		return ResponseEntity.success(income,request);
	}
	
	@RequestMapping(value="/proceeds-amount" ,method=RequestMethod.GET)
	@ApiOperation(value = "根据条件查询车场实收入金额[7-15-30]天", notes = "根据条件查询车场实收入[7-15-30]天", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<BigDecimal> findProceedsAmount(@ApiParam(value="类型 0 7天 1 15天 2 30天",required=true,name="type") @NotNull(message="类型不能为空") @RequestParam("type") Short type ,@ApiParam(value="车区id",required=true,name="preId") @NotNull(message="车区不能为null") @RequestParam("preId") Long preId,HttpServletRequest request){
		BigDecimal income = this.prefectureService.findProceedsAmount(type,preId,request);
		return ResponseEntity.success(income,request);
	}
	
	@RequestMapping(value="/proceeds" ,method=RequestMethod.GET)
	@ApiOperation(value = "根据条件查询车场实收入明细列表[7-15-30]天", notes = "根据条件查询车场实收入[7-15-30]天", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<ResIncomeList> findProceeds(@ApiParam(value="类型 0 7天 1 15天 2 30天",required=true,name="type") @NotNull(message="类型不能为空") @RequestParam("type") Short type ,@ApiParam(value="车区id",required=true,name="preId") @NotNull(message="车区不能为null") @RequestParam("preId") Long preId,HttpServletRequest request){
		ResIncomeList list = this.prefectureService.findProceeds(type,preId,request);
		return ResponseEntity.success(list,request);
	}
	
	@RequestMapping(value="/traffic-flow" ,method=RequestMethod.GET)
	@ApiOperation(value = "查询车场车流量统计[7-15-30]天明细列表", notes = "查询车场车流量统计[7-15-30]天", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<cn.linkmore.enterprise.controller.ent.response.ResTrafficFlow> findTrafficFlow(@ApiParam(value="类型 0 7天 1 15天 2 30天",required=true,name="type") @NotNull(message="类型不能为空")  @RequestParam("type") Short type ,@ApiParam(value="车区id",required=true,name="preId") @NotNull(message="车区不能为null")  @RequestParam("preId") Long preId,HttpServletRequest request){
		cn.linkmore.enterprise.controller.ent.response.ResTrafficFlow flow = this.prefectureService.findTrafficFlow(type,preId,request);
		return ResponseEntity.success(flow,request);
	}
	@RequestMapping(value="/traffic-flow-count" ,method=RequestMethod.GET)
	@ApiOperation(value = "查询车场车流量统计[7-15-30]天总流量", notes = "查询车场车流量统计[7-15-30]天", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<Integer> findTrafficFlowCount(@ApiParam(value="类型 0 7天 1 15天 2 30天",required=true,name="type") @NotNull(message="类型不能为空")  @RequestParam("type") Short type ,@ApiParam(value="车区id",required=true,name="preId") @NotNull(message="车区不能为null")  @RequestParam("preId") Long preId,HttpServletRequest request){
		Integer count = this.prefectureService.findTrafficFlowCount(type,preId,request);
		return ResponseEntity.success(count,request);
	}
	
	@RequestMapping(value="/charge-detail" ,method=RequestMethod.GET)
	@ApiOperation(value = "查询车场实时收费明细", notes = "查询车场实时收费明细", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<List<ResChargeDetail>> findChargeDetail( @RequestParam("preId") @NotNull(message="车区id") @ApiParam(value="车区id",required=true,name="preId") Long preId,HttpServletRequest request){
		List<ResChargeDetail> list = this.prefectureService.findChargeDetail(preId,request);
		return ResponseEntity.success(list, request);
	}
	
	/*@RequestMapping(value="/charge-detail-new" ,method=RequestMethod.GET)
	@ApiOperation(value = "查询车场实时收费明细-新", notes = "查询车场实时收费明细-新", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<List<cn.linkmore.enterprise.controller.ent.response.ResCharge>> findChargeDetailNew(@ApiParam(value="类型 0 7天 1 15天 2 30天",required=true,name="type") @NotNull(message="类型不能为空") @RequestParam("type") Short type , @ApiParam(value="车区id",required=true,name="preId") @NotNull(message="车区不能为空") @RequestParam("preId") Long preId,HttpServletRequest request){
		List<ResCharge> list = this.prefectureService.findChargeDetailNew(type,preId,request);
		return ResponseEntity.success(list, request);
	}*/
	
	@RequestMapping(value="/traffic-flow-list" ,method=RequestMethod.GET)
	@ApiOperation(value = "查询车场每日车流量列表", notes = "查询车场每日车流量列表", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<ResDayTrafficFlow> findTrafficFlowList(@ApiParam(value="类型 0 7天 1 15天 2 30天",required=true,name="type") @RequestParam("type") Short type , 
																	   @ApiParam(value="车区id",required=true,name="preId") @RequestParam("preId") Long preId,
																	   @ApiParam(value="当前月时间 如：2018年7月",required=true,name="date") @RequestParam("date") String date,
																	   HttpServletRequest request){
		ResDayTrafficFlow list = this.prefectureService.findTrafficFlowList(type,preId,date,request);
		return ResponseEntity.success(list, request);
	}
	
	@RequestMapping(value="/income-list" ,method=RequestMethod.GET)
	@ApiOperation(value = "查询车场每日收入列表", notes = "查询车场每日收入列表", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<ResDayIncome> findIncomeList(@ApiParam(value="类型 0 7天 1 15天 2 30天",required=true,name="type") @RequestParam("type") Short type , 
			 												 @ApiParam(value="车区id",required=true,name="preId")	@RequestParam("preId") Long preId,
															 @ApiParam(value="当前月时间 如：2018年7月",required=true,name="date") @RequestParam("date") String date,
															HttpServletRequest request){
		ResDayIncome list = this.prefectureService.findIncomeList(type,preId,date,request);
		return ResponseEntity.success(list, request);
	}
	
}
