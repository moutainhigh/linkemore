package cn.linkmore.user.request;

import javax.validation.constraints.Min;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("预约请求")
public class ReqBooking {
	@ApiModelProperty(value = "车区ID", required = true)
	@Min(value=0,message="车区ID为大于0的长整数")
	private Long prefectureId;
	
	@ApiModelProperty(value = "车牌ID", required = true)
	@Min(value=0,message="车牌ID为大于0的长整数")
	private Long plateId;
	
	public Long getPrefectureId() {
		return prefectureId;
	}
	public void setPrefectureId(Long prefectureId) {
		this.prefectureId = prefectureId;
	}
	public Long getPlateId() {
		return plateId;
	}
	public void setPlateId(Long plateId) {
		this.plateId = plateId;
	}
	
}