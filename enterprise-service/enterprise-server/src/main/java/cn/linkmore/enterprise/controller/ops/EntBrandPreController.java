package cn.linkmore.enterprise.controller.ops;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.enterprise.request.ReqEntBrandPre;
import cn.linkmore.enterprise.service.EntBrandPreService;
import cn.linkmore.prefecture.request.ReqCheck;

/**
 * @Description - 品牌企业车区
 * @Author jiaohanbin
 * @Version v2.0
 */
@Controller
@RequestMapping("/ops/ent-brand-pre")
public class EntBrandPreController {
	
	@Resource
	private EntBrandPreService entBrandPreService;

	/**
	 * 车区列表分页
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/v2.0/list", method = RequestMethod.POST)
	@ResponseBody
	public ViewPage list(@RequestBody ViewPageable pageable) {
		return this.entBrandPreService.findPage(pageable);
	}
	
	/**
	 * 删除品牌车区
	 */
	@RequestMapping(value = "/v2.0/delete-ids", method = RequestMethod.POST)
	@ResponseBody
	public int delete(@RequestBody List<Long> ids) {
		return this.entBrandPreService.delete(ids);
	}
	
	/**
	 * 检查名称重复
	 */
	@RequestMapping(value = "/v2.0/check", method = RequestMethod.POST)
	@ResponseBody
	public Boolean check(@RequestBody ReqCheck reqCheck) {
		Boolean flag = true;
		/*Integer count = this.entBrandPreService.check(reqCheck);
		if (count > 0) {
			flag = false;
		}*/		
		return flag;
	}
	
	/**
	 * 新增
	 */
	@RequestMapping(value = "/v2.0/save", method = RequestMethod.POST)
	@ResponseBody
	public int save(@RequestBody ReqEntBrandPre reqEntBrandPre) {
		return	this.entBrandPreService.save(reqEntBrandPre);
	}
	
	/**
	 * 更新
	 */
	@RequestMapping(value = "/v2.0/update", method = RequestMethod.POST)
	@ResponseBody
	public int update(@RequestBody ReqEntBrandPre reqEntBrandPre) {
		return this.entBrandPreService.update(reqEntBrandPre);
	}
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/v2.0/delete", method = RequestMethod.POST)
	@ResponseBody
	public int delete(@RequestParam("id") Long id) {
		return this.entBrandPreService.delete(id);
	}

}