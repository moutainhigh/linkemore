/**
 * 
 */
package cn.linkmore.enterprise.controller.ent.request;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author luzhishen
 * @Date 2018年7月21日
 * @Version v1.0
 */
@ApiModel("车场车位")
public class ReqPreStall {
	
	@ApiModelProperty("车区ID")
	@NotNull(message="车区不能为空")
	private Long preId;
	@ApiModelProperty("车位类型 0临停 2 固定 3 vip")
	@NotNull(message="类型不能为空")
	private Short type;
	@ApiModelProperty("车位名称 (非必填默认查询所有)")
	private String stallName;
	public Long getPreId() {
		return preId;
	}

	public void setPreId(Long preId) {
		this.preId = preId;
	}

	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	public String getStallName() {
		return stallName;
	}

	public void setStallName(String stallName) {
		this.stallName = stallName;
	}
	
	
}
