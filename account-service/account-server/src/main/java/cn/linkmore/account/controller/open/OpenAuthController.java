package cn.linkmore.account.controller.open;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.linkmore.account.controller.open.requset.ReqOpenAuth;
import cn.linkmore.account.controller.open.requset.ReqOpenUser;
import cn.linkmore.account.controller.open.response.ResOpenAuth;
import cn.linkmore.account.service.OpenAuthService;
import cn.linkmore.bean.common.ResponseEntity;
import cn.linkmore.bean.common.security.CacheUser;
import cn.linkmore.bean.exception.BusinessException;
import cn.linkmore.bean.exception.StatusEnum;
import cn.linkmore.redis.RedisService;
import cn.linkmore.user.factory.AppUserFactory;
import cn.linkmore.user.factory.UserFactory;
import cn.linkmore.util.HttpUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;

/**
 * 第三方开放授权
 * 
 * @author changlei
 * @version 1.0
 */

@Api(tags = "Auth", description = "第三方开放授权")
@RestController
@RequestMapping("/open/auth")
public class OpenAuthController {

	@Resource
	private RedisService redisService;

	private UserFactory appUserFactory = AppUserFactory.getInstance();

	@Autowired
	private OpenAuthService openAuthService;

	@ApiOperation(value = "开放授权", notes = "token", consumes = "application/json")
	@RequestMapping(value = "/token", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResOpenAuth> token(@Validated @RequestBody ReqOpenAuth reqOpenAuth,
			HttpServletRequest request) {
		ResponseEntity<ResOpenAuth> response = null;
		try {
			ResOpenAuth resOpenAuth = openAuthService.getToken(reqOpenAuth);
			response = ResponseEntity.success(resOpenAuth, request);
		} catch (BusinessException e) {
			response = ResponseEntity.fail(e.getStatusEnum(), request);
		} catch (Exception e) {
			response = ResponseEntity.fail(StatusEnum.SERVER_EXCEPTION, request);
		}
		return response;
	}

	@RequestMapping(value = "/t", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<CacheUser> t(@RequestBody ReqOpenAuth reqOpenAuth, HttpServletRequest request) {
		String key = appUserFactory.createTokenRedisKey(request);
		CacheUser user = (CacheUser) this.redisService.get(key);
		return ResponseEntity.success(user, request);
	}

	public  final Base64.Encoder encoder = Base64.getEncoder();
	public  final Base64.Decoder decoder = Base64.getDecoder();
	
	//测试环境
    /*
	public  final String baseUri="http://beta.zuolin.com";
	public  final String appKey = "b403af14-ed87-455f-979b-4648b139fec8";
	public  final String secretKey = "ujNuYJb2v+4PWsqnwzOMhkzwqDrJ83XolC+IjKJV+VJBi1P4OWANjkQcfDK9Qc34xupKk2lg4769Po3AtnfL8A==";	
	public  final String redirectUri="http://192.168.1.205:8003/open/auth/redirect";
	public  final String backUri="https://web-blue.linkmoreparking.cn/#/";
*/
	//正式环境
	
	//public  final String baseUri="http://guomaofuwu.zuolin.com";
	/*
	public  final String baseUri="https://core.zuolin.com";
	public  final String appKey = "4793a6be-ff28-44e1-8ad9-1455bc34667a";
	public  final String secretKey = "JTWEznLfDb1zrAeJO9OK/WmQoAQ+8hshR1E86WVP9mpsYNnKoMs2kq6Fayq+N9R5d6IiU8SeFIH+sxtR+SL5qg==";
	public  final String redirectUri="https://api.linkmoreparking.com/api/account/open/auth/redirect";
	//public  final String backUri="https://web-blue.linkmoretech.cn/#/";
	
	
	public  final String codePath="/evh/oauth2/authorize";
	public  final String tokenPath="/evh/oauth2/token";
	public  final String userInfoPath="/evh/oauth2api/trd/userInfo";
	public  final String authInfoPath="/evh/oauth2api/trd/authenticationInfo";
	*/
	
	@Value("${guomaofuwu.baseUri}")
	public  String baseUri;
	@Value("${guomaofuwu.appKey}")
	public  String appKey;
	@Value("${guomaofuwu.secretKey}")
	public  String secretKey;	
	@Value("${guomaofuwu.redirectUri}")
	public  String redirectUri;		
	@Value("${guomaofuwu.backUri}")
	public  String backUri;		
	@Value("${guomaofuwu.codePath}")
	public  String codePath;
	@Value("${guomaofuwu.tokenPath}")
	public  String tokenPath;	
	@Value("${guomaofuwu.userInfoPath}")
	public  String userInfoPath;	
	@Value("${guomaofuwu.authInfoPath}")
	public  String authInfoPath;		
	
	public  String getToken(String code) {
		Map<String,String> headers=new HashMap<String,String>();
		Map<String,String> bodys=new HashMap<String,String>();
		String res=null;
		try {
			headers.put("Authorization", "Basic "+encoder.encodeToString( (appKey + ":" + secretKey).getBytes("UTF-8")));
			bodys.put("grant_type", "authorization_code");
			bodys.put("code", code);
			bodys.put("redirect_uri", redirectUri);
			bodys.put("client_id", appKey);
			HttpResponse doPost = HttpUtils.doPost(baseUri, tokenPath, "", headers, null, bodys);
			res=EntityUtils.toString(doPost.getEntity(),"UTF-8");
			if(StringUtils.isNotEmpty(res) ) {
				JSONObject obj = JSONObject.fromObject(res);
				if(obj !=null) {
					if(obj.has("access_token")) {
						res = encoder.encodeToString(obj.getString("access_token").getBytes("UTF-8"));
						//System.out.println("resToken="+res);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public  String getUserInfo(String token) {
		Map<String,String> headers=new HashMap<String,String>();
		headers.put("Authorization", "Bearer "+token);
		String res=null;
		try {
			HttpResponse doGet = HttpUtils.doGet(baseUri, userInfoPath, "", headers, null);
			res=EntityUtils.toString(doGet.getEntity(),"UTF-8");
			if(StringUtils.isNotEmpty(res) ) {
				JSONObject obj = JSONObject.fromObject(res);
				if(obj !=null) {
					if(obj.has("response")) {
						res=obj.getString("response");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	public  String getAuthInfo(String token) {
		Map<String,String> headers=new HashMap<String,String>();
		headers.put("Authorization", "Bearer "+token);
		String res=null;
		try {
			HttpResponse doGet = HttpUtils.doGet(baseUri, authInfoPath, "", headers, null);
			res=EntityUtils.toString(doGet.getEntity(),"UTF-8");
			if(StringUtils.isNotEmpty(res) ) {
				JSONObject obj = JSONObject.fromObject(res);
				if(obj !=null) {
					if(obj.has("response")) {
						res=obj.getString("response");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * 第三方app主入口
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/index")
	public void main(HttpServletRequest request,HttpServletResponse response) {
		String url=baseUri+codePath+"?client_id="+appKey
				+"&response_type=code&scope=basic&redirect_uri="+redirectUri
				+"&state="+UUID.randomUUID().toString().replaceAll("-", "")
				+"#oauth2_redirect";
		String html="<script language=\"javascript\" type=\"text/javascript\">window.location.href=\"" + url + "\"</script>";
		
		try {
			response.setHeader("content-type", "text/html;charset=UTF-8");
			OutputStream out = response.getOutputStream();  
			out.write(html.getBytes("UTF-8")); 

			//response.sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 回调，地址
	 * @param code
	 * @param state
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/redirect")
	public void redirect(@RequestParam String code,String state, HttpServletRequest request,HttpServletResponse response) {
		try {
			String token=getToken(code);
			if(StringUtils.isNotBlank(token)) {
				String userInfo=getUserInfo(token);
				//String authInfo=getAuthInfo(token);
				if(StringUtils.isNotEmpty(userInfo) ) {
					JSONObject obj = JSONObject.fromObject(userInfo);
					if(obj !=null) {
						if(obj.has("phone")) {
							ReqOpenUser reqOpenUser=new ReqOpenUser();
							reqOpenUser.setId(obj.getLong("id"));
							reqOpenUser.setAccountName(obj.getString("accountName"));
							reqOpenUser.setPhone(obj.getString("phone"));
							reqOpenUser.setNickName(obj.getString("nickName"));
							reqOpenUser.setSex(obj.getInt("gender"));
							String mytoken = openAuthService.getToken(reqOpenUser);
							response.sendRedirect(backUri +"?token="+ mytoken);
						}
					}
				}
			}else {
				response.setContentType("text/html;charset=utf-8");
				response.getWriter().print("获取token失败，请联系管理员");
			}
		} catch (Exception e) {
			try {
				response.setContentType("text/html;charset=utf-8");
				response.getWriter().print("系统错误，请联系管理员");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/backUri")
	public String backUri(@RequestParam HashMap<String,String> map) {
		//return new String(decoder.decode(map.get("userInfo")))+"<br/><br/>"+new String(decoder.decode(map.get("authInfo")));
		return "token="+map.get("token");
	}

}
