package cn.linkmore.enterprise.controller.ops;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.linkmore.bean.view.Tree;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.enterprise.request.ReqEntBrandStall;
import cn.linkmore.enterprise.service.EntBrandPreService;
import cn.linkmore.enterprise.service.EntBrandStallService;

/**
 * @Description - 品牌车位
 * @Author jiaohanbin
 * @Version v2.0
 */
@Controller
@RequestMapping("/ops/ent-brand-stall")
public class EntBrandStallController {
	
	@Resource
	private EntBrandStallService entBrandStallService;
	
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
		return this.entBrandStallService.findPage(pageable);
	}
	
	/**
	 * 新增
	 */
	@RequestMapping(value = "/v2.0/save", method = RequestMethod.POST)
	@ResponseBody
	public int save(@RequestBody ReqEntBrandStall reqEntBrandStall) {
		return	this.entBrandStallService.save(reqEntBrandStall);
	}
	
	/**
	 * 更新
	 */
	@RequestMapping(value = "/v2.0/update", method = RequestMethod.POST)
	@ResponseBody
	public int update(@RequestBody ReqEntBrandStall reqEntBrandStall) {
		return this.entBrandStallService.update(reqEntBrandStall);
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/v2.0/delete", method = RequestMethod.POST)
	@ResponseBody
	public int delete(@RequestBody List<Long> ids) {
		return this.entBrandStallService.delete(ids);
	}
	
	@RequestMapping(value = "/v2.0/tree", method = RequestMethod.POST)
	@ResponseBody
	public Tree tree() {
		return entBrandPreService.findTree();
	}

}
