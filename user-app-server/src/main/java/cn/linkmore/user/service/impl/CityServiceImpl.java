package cn.linkmore.user.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.linkmore.common.client.CityClient;
import cn.linkmore.third.client.LocateClient;
import cn.linkmore.user.response.ResCity;
import cn.linkmore.user.service.CityService;

/**
 * Service实现 - 城市
 * 
 * @author liwenlong
 *
 */
@Service
public class CityServiceImpl implements CityService {

	@Autowired
	private CityClient cityClient;
	
	@Autowired
	private LocateClient locateClient;

	@Override
	public List<ResCity> list(String longitude,String latitude) {
		List<cn.linkmore.common.response.ResCity> list = this.cityClient.list(0, null);
		List<ResCity> res = null;
		ResCity rc = null;
		Map<String,ResCity> rcMap = new HashMap<String,ResCity>();
		if (CollectionUtils.isNotEmpty(list)) {
			res = new ArrayList<ResCity>();
			for (cn.linkmore.common.response.ResCity re : list) {
				rc = new ResCity();
				rc.setId(re.getId());
				rc.setAdcode(re.getCode());
				rc.setName(re.getName());
				res.add(rc);
				rcMap.put(re.getCode(), rc);
			}
		}
		if (!rcMap.isEmpty()) {
			cn.linkmore.third.request.ReqLocate req = new cn.linkmore.third.request.ReqLocate();
			req.setLongitude(longitude);
			req.setLatitude( latitude); 
			cn.linkmore.third.response.ResLocate info = this.locateClient.get(longitude,latitude);
			if(info!=null) {
				rc = rcMap.get(info.getAdcode());
				if(rc!=null) {
					rc.setStatus(ResCity.STATUS_CHECKED);
				}
			}
		} 
		return res;
	}

}
