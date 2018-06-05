package cn.linkmore.order.response;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 用户订单
 * @author liwenlong
 * @version 2.0
 *
 */
public class ResUserOrder {
	/**
	 * 主键
	 */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 车区id
     */
    private Long preId;
    
    private String preName;
    /**
     * 车位id
     */
    private Long stallId;
    
    private String stallName;
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 支付类型:1免费2优惠券3账户
     */
    private Integer payType;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    /**
     * 实际金额
     */
    private BigDecimal actualAmount;
    /**
     * 状态：1，未支付；2，已支付；3，已完成；4，已取消 5 退款，6挂起订单，7关闭订单
     */
    private Integer status;
    /**
     * 开始时间
     */
    private Date beginTime;
    /**
     * 用户名
     */
    private String username;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 车牌号
     */
    private String plateNo; 
     
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 对接计费系统id
     */
    private String dockId;
    /**
     * 订单历史操作状态（1.挂起2关闭）
     */
    private Short statusHistory;
    /**
     * 订单用户类型(0普通，1奥迪内部用户)
     */
    private Short userType;
    /**
     * 订单操作时间[挂起关闭]
     */
    private Date statusTime;
    /**
     * 降锁时间
     */
    private Date lockDownTime;
    /**
     * 降锁状态:null未操作，0失败，1成功
     */
    private Short lockDownStatus;
    
    /**
     * 计费ID
     */
    private Long strategyId;
    
    private  Date switchTime;
    
    
    private Short switchStatus;

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

    public Long getPreId() {
        return preId;
    }

    public void setPreId(Long preId) {
        this.preId = preId;
    }

    public Long getStallId() {
        return stallId;
    }

    public void setStallId(Long stallId) {
        this.stallId = stallId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo == null ? null : plateNo.trim();
    } 

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDockId() {
        return dockId;
    }

    public void setDockId(String dockId) {
        this.dockId = dockId == null ? null : dockId.trim();
    }

    public Short getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(Short statusHistory) {
        this.statusHistory = statusHistory;
    }

    public Short getUserType() {
        return userType;
    }

    public void setUserType(Short userType) {
        this.userType = userType;
    }

    public Date getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Date statusTime) {
        this.statusTime = statusTime;
    }

    public Date getLockDownTime() {
        return lockDownTime;
    }

    public void setLockDownTime(Date lockDownTime) {
        this.lockDownTime = lockDownTime;
    }

    public Short getLockDownStatus() {
        return lockDownStatus;
    }

    public void setLockDownStatus(Short lockDownStatus) {
        this.lockDownStatus = lockDownStatus;
    }

	public Long getStrategyId() {
		return strategyId;
	}
	
	public void setStrategyId(Long strategyId) {
		this.strategyId = strategyId;
	}

	public String getPreName() {
		return preName;
	}

	public void setPreName(String preName) {
		this.preName = preName;
	}

	public String getStallName() {
		return stallName;
	}

	public void setStallName(String stallName) {
		this.stallName = stallName;
	}

	public Date getSwitchTime() {
		return switchTime;
	}

	public void setSwitchTime(Date switchTime) {
		this.switchTime = switchTime;
	}

	public Short getSwitchStatus() {
		return switchStatus;
	}

	public void setSwitchStatus(Short switchStatus) {
		this.switchStatus = switchStatus;
	} 
	
	
}
