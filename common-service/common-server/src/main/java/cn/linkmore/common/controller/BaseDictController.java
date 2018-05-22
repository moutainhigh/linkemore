package cn.linkmore.common.controller;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import cn.linkmore.common.request.ReqBaseDict;
import cn.linkmore.common.response.ResBaseDict;
import cn.linkmore.common.service.BaseDictService;

/**
 * 数据词典
 * @author   GFF
 * @Date     2018年5月18日
 * @Version  v2.0
 */
@RestController
@RequestMapping(value="/dict")
public class BaseDictController {
	
	@Resource
	private BaseDictService baseDictService;
	
	/**
	 * @Description  通过code查询数据
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value="/{code}",method=RequestMethod.GET)
	public List<ResBaseDict> selectList(@PathVariable("code") String code ) {
		List<ResBaseDict> res = this.baseDictService.selectList(code);
		return res;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public void save(@RequestBody ReqBaseDict baseDict) {
		this.baseDictService.save(baseDict);
	}

	@RequestMapping(method=RequestMethod.PUT)
	public void update(@RequestBody ReqBaseDict baseDict) {
		this.baseDictService.updateByIdSelective(baseDict);
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.DELETE)
	public void update(@PathVariable("id") Long id) {
		this.baseDictService.deleteById(id);
	}
	

}