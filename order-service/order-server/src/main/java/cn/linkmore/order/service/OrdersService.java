package cn.linkmore.order.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.order.controller.app.request.ReqBooking;
import cn.linkmore.order.controller.app.request.ReqBrandBooking;
import cn.linkmore.order.controller.app.request.ReqOrderStall;
import cn.linkmore.order.controller.app.request.ReqSwitch;
import cn.linkmore.order.controller.app.response.ResCheckedOrder;
import cn.linkmore.order.controller.app.response.ResOrder;
import cn.linkmore.order.controller.app.response.ResOrderDetail;
import cn.linkmore.order.request.ReqOrderExcel;
import cn.linkmore.order.response.ResOrderExcel;
import cn.linkmore.order.response.ResUserOrder;

/**
 * Service - 订单
 * @author liwenlong
 * @version 2.0
 *
 */
public interface OrdersService {

	/**
	 * 预约
	 * @param roc
	 * @param request
	 */
	void create(ReqBooking rb,HttpServletRequest request);
	
	/**
	 * 最新订单
	 * @param userId
	 * @return
	 */
	ResUserOrder latest(Long userId);
	
	/**
	 * 订单详情
	 * @param id
	 * @return
	 */
	ResOrderDetail detail(Long id,HttpServletRequest request);

	/**
	 * 降下地锁
	 * @param rod
	 */
	void down(ReqOrderStall ros,HttpServletRequest request);

	/**
	 * 切换车位
	 * @param ros
	 */
	void switchStall(ReqSwitch rs,HttpServletRequest request);

	/**
	 * 订单列表
	 * @param start 
	 * @param request
	 * @return
	 */
	List<ResCheckedOrder> list(Long start, HttpServletRequest request);

	/**
	 * 当前订单
	 * @param request
	 * @return
	 */
	ResOrder current(HttpServletRequest request);

	/**
	 * 落锁结果查询
	 * @param ros
	 * @param request
	 */
	Integer downResult(HttpServletRequest request);

	/**
	 * @Description  降锁消息推送
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	void downMsgPush(Long orderId, Long stallId);

	/**
	 * @Description  分页查询
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	ViewPage findPage(ViewPageable pageable);

	/**
	 * @Description  文件导出
	 * @Author   GFF 
	 * @Version  v2.0
	 */
	List<ResOrderExcel> exportList(ReqOrderExcel bean);
	/**
	 * 品牌预约车位
	 * @param rbb
	 * @param request
	 */
	void brandCreate(ReqBrandBooking rbb, HttpServletRequest request);
	/**
	 * 切换品牌车位
	 * @param rs
	 * @param request
	 */
	void switchBrandStall(ReqSwitch rs, HttpServletRequest request); 
}
