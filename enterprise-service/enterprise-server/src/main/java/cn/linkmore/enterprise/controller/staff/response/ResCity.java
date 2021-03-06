package cn.linkmore.enterprise.controller.staff.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("城市的Bean")
public class ResCity {
	
	@ApiModelProperty("城市编号（主键）")
    private Long id;

	@ApiModelProperty("城市名称")
    private String cityName;
	
	@ApiModelProperty("车区总数")
	private Integer preNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

	public Integer getPreNumber() {
		return preNumber;
	}

	public void setPreNumber(Integer preNumber) {
		this.preNumber = preNumber;
	}
}
