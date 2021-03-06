package cn.linkmore.account.controller.staff.response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
@Api("管理版用户信息")
public class ResAdmin {
	@ApiModelProperty(value = "唯一标识")
	private Long id;
	@ApiModelProperty(value = "手机号") 
	private String mobile;
	@ApiModelProperty(value = "令牌")
	private String token;
	@ApiModelProperty(value = "名称")
	private String realname;
	@ApiModelProperty(value = "状态,0禁用，1启用")
	private Short status;
	@ApiModelProperty(value = "角色类型 1  超级管理员 （具有维护模块）2物业版管理员(不具有)  ")
	private String type = "2";
	@ApiModelProperty(value = "运营是否可以查看 true 展开  false 不展开")
	private Boolean isOperation = false;
	
	@ApiModelProperty(value = "是否有删除权限 true是false否")
	private Short gatewayDelete;
	@ApiModelProperty(value = "账户名称")
	private String accountName;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public Short getStatus() {
		return status;
	}
	public void setStatus(Short status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getIsOperation() {
		return isOperation;
	}
	public void setIsOperation(Boolean isOperation) {
		this.isOperation = isOperation;
	}
	public Short getGatewayDelete() {
		return gatewayDelete;
	}
	public void setGatewayDelete(Short gatewayDelete) {
		this.gatewayDelete = gatewayDelete;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
}
