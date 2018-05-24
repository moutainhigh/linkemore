package cn.linkmore.security.controller;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import cn.linkmore.bean.view.Tree;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.security.entity.PageElement;
import cn.linkmore.security.request.ReqCheck;
import cn.linkmore.security.request.ReqPageElement;
import cn.linkmore.security.service.PageElementService;
import cn.linkmore.util.ObjectUtils;

/**
 * Controller - 页面元素操作
 * 
 * @author jiaohanbin
 * @version 2.0
 *
 */
@RestController
@RequestMapping("/page_element")
public class PageElementController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PageElementService pageElementService;
	
	@RequestMapping(value = "/v2.0/save", method = RequestMethod.POST)
	@ResponseBody
	public void save(@RequestBody ReqPageElement reqPageElement){
		PageElement pageElement = new PageElement();
		pageElement = ObjectUtils.copyObject(reqPageElement, pageElement);
		this.pageElementService.save(pageElement);
		 
	}
	
	@RequestMapping(value = "/v2.0/update", method = RequestMethod.POST)
	@ResponseBody
	public void update(@RequestBody ReqPageElement reqPageElement){
		PageElement pageElement = new PageElement();
		pageElement = ObjectUtils.copyObject(reqPageElement, pageElement);
		this.pageElementService.update(pageElement);
	}
	
	@RequestMapping(value = "/v2.0/delete", method = RequestMethod.POST)
	@ResponseBody
	public void delete(@RequestBody List<Long> ids){ 
		this.pageElementService.delete(ids);
	}
	
	@RequestMapping(value = "/v2.0/check", method = RequestMethod.POST)
	@ResponseBody
	public Boolean check(@RequestBody ReqCheck reqCheck){
		Boolean flag = true ;
		Integer count = this.pageElementService.check(reqCheck); 
		if(count>0){
            flag = false;
        }
        return flag;
	}
	
	@RequestMapping(value = "/v2.0/list", method = RequestMethod.POST)
	@ResponseBody
	public ViewPage list(@RequestBody ViewPageable pageable){ 
		return this.pageElementService.findPage(pageable); 
	} 
	
	
	@RequestMapping(value = "/v2.0/tree", method = RequestMethod.GET)
	@ResponseBody
	public Tree tree(){ 
		return this.pageElementService.findTree();
	}
	
	@RequestMapping(value = "/v2.0/map", method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> map(){
		return this.pageElementService.map();
	}
}