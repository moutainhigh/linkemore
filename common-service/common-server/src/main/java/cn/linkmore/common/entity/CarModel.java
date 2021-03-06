package cn.linkmore.common.entity;

/**
 * 三级具体车
 * 	迈腾
 * 	速腾
 * 
 * @author zhaoyufei
 */
public class CarModel {
	
	private String id;
	private String fullname;
	private String parentid;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	@Override
	public String toString() {
		return "CarModel [id=" + id + ", fullname=" + fullname + ", parentid=" + parentid + "]";
	}
	
}
