package cn.linkmore.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("手机绑定请求")
public class ReqMobileBind {
	
	@ApiModelProperty(value = "手机号", required = true)
	private String mobile;
	
	@ApiModelProperty(value = "短信验证码", required = true)
	private String code;
	 
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}