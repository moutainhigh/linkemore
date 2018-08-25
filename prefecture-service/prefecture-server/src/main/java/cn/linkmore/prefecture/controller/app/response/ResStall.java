package cn.linkmore.prefecture.controller.app.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("车位列表")
public class ResStall {
	
	@ApiModelProperty(value = "车位ID")
	private Long stallId;
	@ApiModelProperty(value = "车位名称")
	private String stallName;
	@ApiModelProperty(value = "车位锁编号")
	private String lockSn;
	public Long getStallId() {
		return stallId;
	}
	public void setStallId(Long stallId) {
		this.stallId = stallId;
	}
	public String getStallName() {
		return stallName;
	}
	public void setStallName(String stallName) {
		this.stallName = stallName;
	}
	public String getLockSn() {
		return lockSn;
	}
	public void setLockSn(String lockSn) {
		this.lockSn = lockSn;
	}
}
