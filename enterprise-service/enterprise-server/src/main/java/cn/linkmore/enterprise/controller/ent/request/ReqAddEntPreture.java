package cn.linkmore.enterprise.controller.ent.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("新增企业车区")
public class ReqAddEntPreture {
	

	@ApiModelProperty(value = "车区名称", required = true)
    private String preName;
	@ApiModelProperty(value = "企业id", required = true)
    private Long entId;
	@ApiModelProperty(value = "企业名称", required = true)
    private String entName;


	public String getPreName() {
		return preName;
	}

	public void setPreName(String preName) {
		this.preName = preName;
	}

	public Long getEntId() {
		return entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public String getEntName() {
		return entName;
	}

	public void setEntName(String entName) {
		this.entName = entName;
	}
    
}
