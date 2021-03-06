package cn.linkmore.enterprise.controller.ops;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.enterprise.request.ReqEntBrandUser;
import cn.linkmore.enterprise.service.EntBrandUserService;

/**
 * @Description - 品牌车位
 * @Author jiaohanbin
 * @Version v2.0
 */
@Controller
@RequestMapping("/ops/ent-brand-user")
public class EntBrandUserController {
	
	@Resource
	private EntBrandUserService entBrandUserService;

	/**
	 * 车区列表分页
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "/v2.0/list", method = RequestMethod.POST)
	@ResponseBody
	public ViewPage list(@RequestBody ViewPageable pageable) {
		return this.entBrandUserService.findPage(pageable);
	}
	
	/**
	 * 新增
	 */
	@RequestMapping(value = "/v2.0/save", method = RequestMethod.POST)
	@ResponseBody
	public int save(@RequestBody ReqEntBrandUser reqEntBrandUser) {
		return	this.entBrandUserService.save(reqEntBrandUser);
	}
	
	/**
	 * 更新
	 */
	@RequestMapping(value = "/v2.0/update", method = RequestMethod.POST)
	@ResponseBody
	public int update(@RequestBody ReqEntBrandUser reqEntBrandUser) {
		return this.entBrandUserService.update(reqEntBrandUser);
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/v2.0/delete", method = RequestMethod.POST)
	@ResponseBody
	public int delete(@RequestBody List<Long> ids) {
		return this.entBrandUserService.delete(ids);
	}
	
	/**
	 * 校验
	 */
	@RequestMapping(value = "/v2.0/check", method = RequestMethod.POST)
	@ResponseBody
	public int check(@RequestBody ReqEntBrandUser record) {
		return this.entBrandUserService.check(record);
	}
	
	/**
	 * 批量插入
	 */
	@RequestMapping(value = "/v2.0/insertBatch", method = RequestMethod.POST)
	@ResponseBody
	public int insertBatch(@RequestBody List<ReqEntBrandUser> reqUserList) {
		return this.entBrandUserService.insertBatch(reqUserList);
	}

}
