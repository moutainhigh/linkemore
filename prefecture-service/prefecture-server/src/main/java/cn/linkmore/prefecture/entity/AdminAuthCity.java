package cn.linkmore.prefecture.entity;
/**
 * entity 授权城市
 * @author jiaohanbin
 * @version 2.0
 *
 */
public class AdminAuthCity {
    private Long id;

    private Long authId;

    private Long cityId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
}