package cn.linkmore.order.controller.staff.response;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel("异常订单")
public class ResUnusualOd {

	
	@ApiModelProperty(value = "车位ID")
    private Long stallId;//车位id

	@ApiModelProperty(value = "车为名称")
    private String stallName;//车位名称

	@ApiModelProperty(value = "车区id")
    private Long prefectureId;//车区id
	
	@ApiModelProperty(value = "订单id")
    private Long orderId;//订单id
	
	@ApiModelProperty(value = "订单编号")
    private String orderNo;//订单编号
	
	@ApiModelProperty(value = "订单开始时间")
    private Date orderStartTime;//订单开始时间
	
	@ApiModelProperty(value = "订单持续时间(单位/秒)")
    private String orderTime;//订单持续时间
	
	@ApiModelProperty(value = "订单结束时间")
    private Date orderEndTime;//订单结束时间
	
	@ApiModelProperty(value = "订单状态")
    private Short orderStatus;//订单状态
	
	@ApiModelProperty(value = "降锁状态")
    private Short lockDownStatus;//降锁状态、
	
	@ApiModelProperty(value = "降锁时间")
    private Date lockDownTime;//降锁时间
	
	@ApiModelProperty(value = "异常分类(0预约30分钟未降锁，1降锁超4个小时，2订单结束未释放车位，3关闭订单未释放车位，4挂起未释放车位)")
    private Short category;//异常分类(0预约30分钟未降锁，1降锁超4个小时，2订单结束未释放车位，3关闭订单未释放车位，4挂起未释放车位)
	
	@ApiModelProperty(value = "订单操作时间")
    private Date orderOperateTime;//订单操作时间
	
	@ApiModelProperty(value = "订单联系人")
    private String orderMobile;//订单联系人
	
	@ApiModelProperty(value = "创建时间")
    private Date createTime;//创建时间
	@ApiModelProperty(value = "挂起和关闭状态")
    private Short statusHistory;//挂起和关闭状态

	
	


	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public Short getStatusHistory() {
		return statusHistory;
	}

	public void setStatusHistory(Short statusHistory) {
		this.statusHistory = statusHistory;
	}

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
        this.stallName = stallName == null ? null : stallName.trim();
    }

    public Long getPrefectureId() {
        return prefectureId;
    }

    public void setPrefectureId(Long prefectureId) {
        this.prefectureId = prefectureId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Date getOrderStartTime() {
        return orderStartTime;
    }

    public void setOrderStartTime(Date orderStartTime) {
        this.orderStartTime = orderStartTime;
    }

    public Date getOrderEndTime() {
        return orderEndTime;
    }

    public void setOrderEndTime(Date orderEndTime) {
        this.orderEndTime = orderEndTime;
    }

    public Short getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Short orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Short getLockDownStatus() {
        return lockDownStatus;
    }

    public void setLockDownStatus(Short lockDownStatus) {
        this.lockDownStatus = lockDownStatus;
    }

    public Date getLockDownTime() {
        return lockDownTime;
    }

    public void setLockDownTime(Date lockDownTime) {
        this.lockDownTime = lockDownTime;
    }

    public Short getCategory() {
        return category;
    }

    public void setCategory(Short category) {
        this.category = category;
    }

    public Date getOrderOperateTime() {
        return orderOperateTime;
    }

    public void setOrderOperateTime(Date orderOperateTime) {
        this.orderOperateTime = orderOperateTime;
    }

    public String getOrderMobile() {
        return orderMobile;
    }

    public void setOrderMobile(String orderMobile) {
        this.orderMobile = orderMobile == null ? null : orderMobile.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
