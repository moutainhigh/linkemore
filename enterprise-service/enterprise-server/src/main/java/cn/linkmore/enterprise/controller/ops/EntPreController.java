/**
 * 
 */
package cn.linkmore.enterprise.controller.ops;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import cn.linkmore.bean.common.ResponseEntity;
import cn.linkmore.bean.exception.StatusEnum;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.enterprise.controller.ent.request.ReqUpdateEntPreture;
import cn.linkmore.enterprise.request.ReqAddEntPreture;
import cn.linkmore.enterprise.response.ResEntPrefecture;
import cn.linkmore.enterprise.service.EntPreService;
import cn.linkmore.prefecture.response.ResPreList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 企业车区
 * @author luzhishen
 * @Date 2018年7月20日
 * @Version v1.0
 */
@Api(tags = "preture",description="企业车区", produces = "application/json")
@RestController
@RequestMapping("/ops/enterprise")
public class EntPreController {
	
	@Autowired
	private EntPreService entPreService;
	
	@ApiOperation(value = "保存企业分区", notes = "保存企业分区", consumes = "application/json")
	@RequestMapping(value = "/save-ent-pre",method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> saveEntPre(@RequestBody ReqAddEntPreture reqAddEntPreture,HttpServletRequest request){
		if(reqAddEntPreture ==  null){
			return ResponseEntity.fail(StatusEnum.VALID_EXCEPTION, request);
		}
		int result = entPreService.saveEntPre(reqAddEntPreture.getPreId(),reqAddEntPreture.getEntId(),reqAddEntPreture.getPreName(),null,null);
		if(result == 0){
			return ResponseEntity.fail(StatusEnum.VALID_EXCEPTION, request);
		}
		return ResponseEntity.success("保存成功", request);
	}
	
    @ApiOperation(value = "删除企业分区", notes = "删除企业分区", consumes = "application/json")
	@RequestMapping(value = "/delete-ent-pre",method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> deleteEntPre(Long id ,HttpServletRequest request ){
		if(id ==  null){
			return ResponseEntity.fail(StatusEnum.VALID_EXCEPTION, request);
		}
		int result = entPreService.deleteEntPre(id);
		if(result == 0){
			return ResponseEntity.fail(StatusEnum.VALID_EXCEPTION, request);
		}
		return ResponseEntity.success("删除成功", request);
	}
    
    @ApiOperation(value = "修改企业分区", notes = "修改企业分区", consumes = "application/json")
	@RequestMapping(value = "/update-ent-pre",method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> updateEntPre(@RequestBody ReqUpdateEntPreture  reqUpdateEntPreture,HttpServletRequest request ){
		if(reqUpdateEntPreture ==  null){
			return ResponseEntity.fail(StatusEnum.VALID_EXCEPTION, request);
		}
		int result = entPreService.updateEntPre(reqUpdateEntPreture.getId(),reqUpdateEntPreture.getEntId(),reqUpdateEntPreture.getPreId(),reqUpdateEntPreture.getPreName());
		if(result == 0){
			return ResponseEntity.fail(StatusEnum.VALID_EXCEPTION, request);
		}
		return ResponseEntity.success("修改成功", request);
	}
    
    @RequestMapping(value = "/page",method = RequestMethod.POST)
	@ResponseBody
	public ViewPage findPage(@RequestBody ViewPageable pageable) {
		return this.entPreService.findPage(pageable);
	}

    @RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public void save(@RequestBody ReqAddEntPreture auth) {
    	this.entPreService.saveEntPre(auth.getPreId(), auth.getEntId(), auth.getPreName(),auth.getOperatorId(),auth.getOperatorName());
    }

	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@ResponseBody
	public void update(@RequestBody ReqAddEntPreture auth) {
		this.entPreService.updateEntPre(auth.getId(),auth.getEntId(), auth.getPreId(), auth.getPreName());
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@ResponseBody
	public void delete(@RequestBody List<Long> ids) {
		for (Long long1 : ids) {
			this.entPreService.deleteEntPre(long1);
		}
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.POST)
	@ResponseBody
	public List<ResEntPrefecture> findAll(@RequestBody Map<String,Object> map){
		return this.entPreService.findList(map);
	}
	 
	@RequestMapping(value = "/not-create-pre", method = RequestMethod.GET)
	@ResponseBody
	public List<ResPreList> findNotCreateEntPre(){
		return this.entPreService.findNotCreateEntPre();
	}
}
