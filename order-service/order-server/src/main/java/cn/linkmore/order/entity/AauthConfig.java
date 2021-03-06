package cn.linkmore.order.entity;

import org.springframework.stereotype.Component;

@Component
public class AauthConfig {


	private static final String WX_OAUTH_CODE = "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s&connect_redirect=1#wechat_redirect";

	private static final String ZFB_OAUTH_CODE = "redirect:https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=%s&scope=auth_base&redirect_uri=%s&state=%s";

	private static final String NO_OAUTH_CODE = "redirect:%s?code=no&type=other";
	//https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=2017070507648078&scope=auth_base&redirect_uri=https://api.pabeitech.cn/pay/alipayAsyncCallBack
	/*"redirect:http://order.linkmoreparking.cn/?&preid=%s&openid=%s&type=%s";*/
	/*"redirect:http://deal.linkmoreparking.cn/?&preid=%s&openid=%s&type=%s";*/
	//private static final String H5_INDEX ="redirect:http://order.linkmoreparking.cn?preid=%s&openid=%s&type=%s";
	//private static final String H5_INDEX ="redirect:http://qrcode.linkmoreparking.com?preid=%s&openid=%s&type=%s";
	private static final String H5_INDEX ="redirect:http://qrcode.linkmoretech.cn?preid=%s&openid=%s&type=%s";
	
	public String getWxCode(String appId, String redirect_uri,Long preId) {
		return String.format(WX_OAUTH_CODE, appId, redirect_uri,preId);
	}
	
	public String getZfbCode(String appId, String redirect_uri,Long preId) {
		return String.format(ZFB_OAUTH_CODE, appId, redirect_uri,preId);
	}
	
	public String getNoCode(String redirect_uri) {
		return String.format(NO_OAUTH_CODE, redirect_uri);
	}
	
	public String h5Index(Long preid,String openid, String type) {
		return String.format(H5_INDEX, preid,openid, type);
	}

}
