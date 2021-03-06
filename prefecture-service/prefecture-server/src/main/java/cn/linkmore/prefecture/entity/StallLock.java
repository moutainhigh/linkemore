package cn.linkmore.prefecture.entity;

import java.util.Date;
/**
 * 车位锁
 * @author jiaohanbin
 * @version 2.0
 *
 */
public class StallLock {
    private Long id;

    private String sn;

    private Long prefectureId;

    private Long stallId;

    private Date createTime;

    private Date bindTime;
    
    private String model;
    
    private String version;
    
    private int battery;
    /**
     * 创建用户id
     */
    private Long createUserId;
    /**
     * 创建用户名称
     */
    private String createUserName;
    
    /**
     * 创建企业id
     */
    private Long createEntId;
    /**
     * 创建企业名称
     */
    private String createEntName;

	public Long getCreateEntId() {
		return createEntId;
	}

	public void setCreateEntId(Long createEntId) {
		this.createEntId = createEntId;
	}

	public String getCreateEntName() {
		return createEntName;
	}

	public void setCreateEntName(String createEntName) {
		this.createEntName = createEntName;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn == null ? null : sn.trim();
    }

    public Long getPrefectureId() {
        return prefectureId;
    }

    public void setPrefectureId(Long prefectureId) {
        this.prefectureId = prefectureId;
    }

    public Long getStallId() {
        return stallId;
    }

    public void setStallId(Long stallId) {
        this.stallId = stallId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getBindTime() {
        return bindTime;
    }

    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }
    
    public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getBattery() {
		return battery;
	}

	public void setBattery(int battery) {
		this.battery = battery;
	}
	
	
}