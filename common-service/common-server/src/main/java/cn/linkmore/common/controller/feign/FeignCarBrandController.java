package cn.linkmore.common.controller.feign;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkmore.bean.common.Constants.RedisKey;
import cn.linkmore.common.response.ResCarBrandBean;
import cn.linkmore.common.response.ResCarFirmBean;
import cn.linkmore.redis.RedisService;
import cn.linkmore.util.HttpUtils;



/**
 * 车辆品牌数据
 * @Version 2.0
 * @author  GFF
 * @Date     2018年5月11日
 */
@Controller
@RestController
@RequestMapping("/feign/car_brand")
public class FeignCarBrandController {

	@Resource
	private RedisService redisService;
	//API域名
    private final static String HOST = "http://jisucxdq.market.alicloudapi.com";
	//阿里云市场里，购买服务后的code
    private final static String APP_CODE = "fac3d2da15bc41b4a2a16ed02a2a7aef";
    //一级品牌url
    private final static String CarBrandPath = "/car/brand";
//    private final static String CarBrandPath = "/car/detail";
    //二级三级品牌url
    private final static String CarListPath = "/car/carlist";
    
    private final static String Method = "GET";
    
    private  Logger log = LoggerFactory.getLogger(getClass());
    
	/**
	 * 查询list
	 * @Description  
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(method = RequestMethod.GET)
	public Object list(HttpServletRequest request) {
		return redisService.get(RedisKey.COMMON_CAR_BRAND_LIST.key);
	}
	
	/**
	 * @Description  加载车辆品牌数据接口
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	@RequestMapping(value = "/load", method = RequestMethod.GET)
	public Map<String,Object> load(){
		log.info("请求车辆品牌接口,请耐心等待......");
		//请求状态  0成功，1请求中，2失败, 方便后台页面控制 请求按钮
		redisService.set("car_brand_status", 1);
		Map<String,Object> msg = new HashMap<String,Object>();
 	    Map<String, String> headers = new HashMap<String, String>();
 	    headers.put("Authorization", "APPCODE " + APP_CODE);
 	    Map<String, String> querys = new HashMap<String, String>();
 	    try {
 	    	HttpResponse response = HttpUtils.doGet(HOST, CarBrandPath, Method, headers, querys);
 	    	String jsonString = EntityUtils.toString(response.getEntity());
 	    	Map maps = (Map)JSON.parse(jsonString);
 	    	JSONArray arr = (JSONArray)maps.get("result");
 	    	List<ResCarBrandBean> brandList = JSONObject.parseArray(arr.toJSONString(),ResCarBrandBean.class);
 	    	
 	    	//获取车牌品牌的id
 	    	Map<String, String> querys2 = new HashMap<String, String>();
 	    	//结果集list
 	    	List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
 	    	for (ResCarBrandBean carBrand : brandList) {
 	    		//根据品牌id获取具体的 厂商品牌（一汽、上汽）
				querys2.put("parentid", carBrand.getId());
				HttpResponse response2 = HttpUtils.doGet(HOST, CarListPath, Method, headers, querys2);
				//必须休眠，容易丢数据[休眠后3秒后， 写出文件需要好几分钟]
				Thread.sleep(3000);
				String jsonString2 = EntityUtils.toString(response2.getEntity());
				Map maps2 = (Map)JSON.parse(jsonString2);
				
				//有的汽车品牌无数据，网络原因 如请求数据丢失将出现空指针异常
				Object obj = maps2.get("result");
				if(!obj.equals("")){
					JSONArray arry = (JSONArray) obj;
					List<ResCarFirmBean> carFirmList = JSONObject.parseArray(arry.toJSONString(),ResCarFirmBean.class);
					if(carFirmList.size() != 0){
						/*for (Map<String,Object> map : resultList) {
							if(map.get("initial").equals(carBrand.getInitial())) {
								List<ResCarFirmBean> carFirmMap = (List<ResCarFirmBean>) map.get("childlist");
								carFirmMap.addAll(carFirmList);
								map.put("childlist", carFirmMap);
								resultList.add(map);
								continue;
							}
						}*/
						//一级品牌拼装
						Map<String,Object> m = new HashMap<String,Object>();
						m.put("id", carBrand.getId());
		 	    		m.put("name",carBrand.getName());
		 	    		m.put("initial", carBrand.getInitial());
		 	    		m.put("parentid", carBrand.getParentid());
		 	    		m.put("childlist", carFirmList);
		 	    		resultList.add(m);
					}
				}
			}
 	    	resultList.sort(new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return o1.get("initial").toString().compareTo(o2.get("initial").toString());
				}
			});
 	    	redisService.set(RedisKey.COMMON_CAR_BRAND_LIST.key, JSON.toJSON(resultList));
 	    	//将成功状态存入redis
 	    	redisService.set("car_brand_status", 0);
	    	msg.put("message", true);
	    	log.info("车辆品牌数据请求成功......");
 	    } catch (NullPointerException n) {
 	    	redisService.set("car_brand_status", 2);
			msg.put("message", "请求数据丢失，请稍后重试");
			log.info("数据丢失，车辆品牌数据请求失败......");
		} catch (Exception e) {
			redisService.set("car_brand_status", 2);
			msg.put("message", "连接失败，请稍后重试");
			log.info("连接失败，车辆品牌数据请求失败......");
		}
 	    return msg;
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object list(){
		Object obj = redisService.get(RedisKey.COMMON_CAR_BRAND_LIST.key);
		return obj;
	}
	
}
