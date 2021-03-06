package cn.linkmore.cloud.zuul.filter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import cn.linkmore.bean.common.Constants.RedisKey;
import cn.linkmore.bean.common.ResponseEntity;
import cn.linkmore.bean.common.security.CacheUser;
import cn.linkmore.bean.exception.StatusEnum;
import cn.linkmore.redis.RedisService;
import cn.linkmore.user.factory.AppUserFactory;
import cn.linkmore.user.factory.UserFactory;
import cn.linkmore.util.JsonUtil;
import cn.linkmore.util.TokenUtil;

public class TokenFilter extends ZuulFilter {
	private UserFactory appUserFactory = AppUserFactory.getInstance();
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final String API_APP_PATH = "/app/";
	private static final String API_ENT_PATH = "/ent/";
	private static final String API_STAFF_PATH = "/staff/";
	private static final String API_OPS_PATH = "/admin/";
	private static final String API_FEIGN_PATH = "/feign/";
	private static final String SWAGGER_PATH = "/webjars/";

	private static final String CONTENTTYPE = "text/json;charset=UTF-8";
	private static final List<String> openResources = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("/swagger-ui.html");
			add("/configuration");
			add("/swagger-resources");
			add("/swagger-resources/configuration/ui");
			add("/swagger-ui.html/swagger-resources/configuration/ui");
			add("/swagger-resources/configuration/security");
			add("/api-docs");
			add("/v2/api-docs");

			add("/app/versions/v2.0/current");
			add("/app/mini/v2.0/login");
			add("/app/auth/v2.0/login");
			add("/app/exception-logs/v2.0/upload");
			add("/app/auth/v2.0/wechat");
			add("/app/auth/v2.0/send");
			add("/app/auth/v2.0/send-pw");
			add("/app/auth/v2.0/auth-is-new");
			add("/app/auth/v2.0/login-pw");
			add("/app/auth/v2.0/register");
			add("/app/auth/v2.0/auth-code");
			add("/app/auth/v2.0/edit-pw");

			add("/enterprise/ent/version/current");
			add("/enterprise/ent/auth/login");
			add("/enterprise/ent/auth/send");
			add("/feign/stall/exc/stall-exc-cause");
			
			add("/open/auth/access-token");
			add("/open/auth/token");
			add("/open/auth/t");
			
			add("/app/prefectures/v2.0/map/near-list");
			add("/app/prefectures/open-list");
			add("/app/prefectures/v2.0/map/list");
			add("/app/prefectures/v2.0/free/list");
			add("/app/prefectures/v2.0/strategy");
			add("/app/prefectures/v2.0/bluetooth");
			add("/app/brand-pre/v2.0/map/list"); 
			add("/app/brand-pre/v2.0/free/list"); 
			add("/app/brand-pre/v2.0/strategy");
			add("/app/brands/v2.0/brand-ad");
			add("/app/brands/v2.0/brand-apply");
			add("/app/brands/v2.0/brand-pre-ad");
			add("/app/citys/v2.0/list");
			add("/app/callback/v2.0/wechat-mini/order");
			add("/app/callback/v2.0/wechat/order");
			add("/app/callback/v2.0/alipay/order");
			add("/app/callback/v2.0/apple/order");

			add("/ent/mini/v2.0/login");
			add("/ent/auth/v2.0/login");
			add("/ent/auth/v2.0/send");

			add("/staff/auth/send");
			add("/staff/auth/login");
			add("/staff/vehicle-brands");
			add("/staff/auth/check-mobile");
			add("/ent/auth/send");

			add("/attach/image_upload");
			add("/ent/auth/v2.0/send");  
			add("/ent/auth/v2.0/send");  
			
			add("/staff/auth/send");  
			add("/staff/auth/login");  
			add("/staff/vehicle-brands");  
			add("/staff/auth/check-mobile");  
			add("/staff/auth/login-pw");  
			add("/staff/auth/send-reset");  
			add("/staff/auth/v2.0/edit-pw-auth");  
			add("/staff/auth/auth-code");  
			add("/staff/auth/check-account");  
			add("/staff/auth/reset");  
			
			add("/open/auth/token");
			add("/open/auth/t");
			
			add("/h5/");
			add("/h5/a");
			add("/h5/d");
			add("/h5/t");
			add("/h5/r");
			add("/h5/o");
			add("/h5/g");
			add("/h5/b");
			add("/h5/c");
			add("/h5/f");
			add("/h5/h");
			add("/h5/m");
			add("/h5/n");
			add("/h5/notify");
			add("/h5/notify.do");
	
			add("/attach/image_upload");
			
			add("/open/auth/index");
			add("/open/auth/backUri");
			add("/open/auth/redirect");
			add("/open/guomao/payPage");
			add("/open/guomao/callBack");
			add("/open/guomao/refundOrder");
			add("/open/guomao/queryOrder");
			
			add("/app/web-site/v2.0/login-pw");
			add("/app/web-site/v2.0/register");
		}
	};

	@Autowired
	private RedisService redisService;

	@Override
	public String filterType() {
		return "pre"; // 可以在请求被路由之前调用
	}

	@Override
	public int filterOrder() {
		return 0; // filter执行顺序，通过数字指定 ,优先级为0，数字越大，优先级越低
	}

	@Override
	public boolean shouldFilter() {
		return true;// 是否执行该过滤器，此处为true，说明需要过滤
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		String ip = request.getRemoteAddr();
		String url = request.getRequestURI();
		String uri = url.substring(url.indexOf("/", 5));
		String key = TokenUtil.getKey(request);
		log.info("ip:{},token:{}", ip, key);
		log.info(uri);
		CacheUser cu = (CacheUser) this.redisService.get(appUserFactory.createTokenRedisKey(request));
		log.info("json:{}", JsonUtil.toJson(cu));
		if (url.contains(SWAGGER_PATH) || (openResources.contains(uri) || cu != null)) {
			ctx.setSendZuulResponse(true);
			ctx.setResponseStatusCode(200);
			ctx.set("isSuccess", true);
			return null;
		} else if (uri.contains(API_OPS_PATH)) {
			ctx.setSendZuulResponse(true);
			ctx.setResponseStatusCode(200);
			ctx.set("isSuccess", true);
			return null;
		} else if (uri.contains(API_STAFF_PATH)) {
			CacheUser staffCu = (CacheUser) this.redisService.get(RedisKey.STAFF_STAFF_AUTH_USER.key + key);
			if (staffCu == null) {
				ctx.setSendZuulResponse(false);
				ctx.setResponseStatusCode(200);
				ctx.setResponseBody(JsonUtil.toJson(ResponseEntity.fail(StatusEnum.USER_APP_NO_LOGIN, request)));
				ctx.getResponse().setContentType(CONTENTTYPE);
				ctx.set("isSuccess", false);
			}
			ctx.setSendZuulResponse(true);
			ctx.setResponseStatusCode(200);
			ctx.set("isSuccess", true);
			return null;
		} else if (uri.contains(API_APP_PATH)) {
			ctx.setSendZuulResponse(false);
			ctx.setResponseStatusCode(200);
			ctx.setResponseBody(JsonUtil.toJson(ResponseEntity.fail(StatusEnum.USER_APP_NO_LOGIN, request)));
			ctx.getResponse().setContentType(CONTENTTYPE);
			ctx.set("isSuccess", false);
			return null;
		} else if (uri.contains(API_OPS_PATH)) {
			ctx.setSendZuulResponse(false);
			ctx.setResponseStatusCode(200);
			ctx.setResponseBody(JsonUtil.toJson(ResponseEntity.fail(StatusEnum.USER_APP_NO_LOGIN, request)));
			ctx.getResponse().setContentType(CONTENTTYPE);
			ctx.set("isSuccess", false);
			return null;
		} else if (uri.contains(API_FEIGN_PATH)) {
			ctx.setSendZuulResponse(false);
			ctx.setResponseStatusCode(200);
			ctx.setResponseBody(JsonUtil.toJson(ResponseEntity.fail(StatusEnum.USER_APP_NO_LOGIN, request)));
			ctx.getResponse().setContentType(CONTENTTYPE);
			ctx.set("isSuccess", false);
			return null;
		} else if (uri.contains(API_ENT_PATH)) {
			ctx.setSendZuulResponse(true);
			ctx.setResponseStatusCode(200);
			ctx.set("isSuccess", true);
			return null;
		} else {
			ctx.setSendZuulResponse(false);
			ctx.setResponseStatusCode(200);
			ctx.setResponseBody(JsonUtil.toJson(ResponseEntity.fail(StatusEnum.USER_APP_NO_LOGIN, request)));
			ctx.getResponse().setContentType(CONTENTTYPE);
			ctx.set("isSuccess", false);
			return null;
		}
	}
}