package cn.linkmore.prefecture.request;
/**
 * 请求车区
 * @author jiaohanbin
 * @version 2.0
 *
 */
public class ReqPrefecture {

	/**
	 * 经度
	 */
    private Double latitude;

    /**
     * 纬度
     */
    private Double longitude;
    /**
     * 比例尺
     */
    private Double scale;
    /**
     * 用户id
     */
    private Long userId;

    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

	public Double getScale() {
		return scale;
	}

	public void setScale(Double scale) {
		this.scale = scale;
	}
}
