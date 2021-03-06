package cn.linkmore.prefecture.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.linkmore.feign.FeignConfiguration;
import cn.linkmore.prefecture.client.hystrix.PrefectureClientHystrix;
import cn.linkmore.prefecture.request.ReqPrefecture;
import cn.linkmore.prefecture.response.ResOpenPres;
import cn.linkmore.prefecture.response.ResPre;
import cn.linkmore.prefecture.response.ResPrefecture;
import cn.linkmore.prefecture.response.ResPrefectureDetail;
import cn.linkmore.prefecture.response.ResPrefectureList;
/**
 * 远程调用 - 车区信息
 * @author jiaohanbin
 * @version 2.0
 *
 */ 
@FeignClient(value = "prefecture-server", path = "/feign/pres", fallback=PrefectureClientHystrix.class,configuration = FeignConfiguration.class)
public interface PrefectureClient {
	
	/**
	 * 根据主键查询详情
	 * @param id 主键ID
	 * @return 车区信息
	 */
	@RequestMapping(value="/v2.0/{id}",method=RequestMethod.GET)
	@ResponseBody 
	public ResPrefectureDetail findById(@PathVariable("id") Long id);
	/**
	 * 根据主键查询批量车区名称
	 * 
	 * @param id Long
	 * @return
	 */
	@RequestMapping(value = "/v2.0/prename", method = RequestMethod.POST)
	@ResponseBody
	public List<ResPre> prenames(@RequestBody List<Long> ids);
	
	
	/**
	 * 根据位置查询车区地图
	 * 
	 * @param reqPrefecture ReqPrefecture
	 * @return
	 */
	@RequestMapping(value = "/v2.0/loc", method = RequestMethod.POST)
	@ResponseBody
	public List<ResPrefecture> findPreListByLoc(@RequestBody ReqPrefecture reqPrefecture);
	
	/**
	 * 根据车区id查询车区空闲车位
	 * 
	 * @param preId Long
	 * @return
	 */
	@RequestMapping(value = "/v2.0/free_count", method = RequestMethod.GET)
	@ResponseBody
	public List<ResPrefectureList> refreshFreeStall();
	
	@RequestMapping(value = "/v2.0/by-pre-ids", method = RequestMethod.POST)
	@ResponseBody
	public List<ResPre> findPreByIds(@RequestBody Map<String, Object> map);
	
	@RequestMapping(value = "/v2.0/free-stall-num", method = RequestMethod.GET)
	@ResponseBody
	public Long findByGroupId(@RequestParam("stallId") Long stallId, @RequestParam("preId") Long preId);
	
	@RequestMapping(value = "/v2.0/near-free-stall", method = RequestMethod.GET)
	@ResponseBody
	public String nearFreeStallLockSn(@RequestParam("stallId") Long stallId, @RequestParam("preId") Long preId);
			
	@RequestMapping(value = "/v2.0/get-floor", method = RequestMethod.GET)
	@ResponseBody
	public List<String> getFloor(@RequestParam(value="preId", required=true) Long preId);
	
	@RequestMapping(value = "/v2.0/open-list", method = RequestMethod.GET)
	@ResponseBody
	public List<ResOpenPres> openPres(@RequestParam("appId") String appId);
}
