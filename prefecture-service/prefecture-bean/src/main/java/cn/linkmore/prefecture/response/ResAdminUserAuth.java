package cn.linkmore.prefecture.response;
/**
 * 响应-管理用户授权
 * @author jiaohanbin
 * @version 2.0
 *
 */
public class ResAdminUserAuth {
    private Long id;

    private Long userId;

    private Long authId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }
}