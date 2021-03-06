package cn.linkmore.ops.biz.controller;

import java.io.BufferedInputStream;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;

import cn.linkmore.account.response.ResPageUser;
import cn.linkmore.bean.common.ResponseEntity;
import cn.linkmore.bean.exception.DataException;
import cn.linkmore.bean.view.ViewMsg;
import cn.linkmore.bean.view.ViewPage;
import cn.linkmore.bean.view.ViewPageable;
import cn.linkmore.ops.biz.service.UserService;
import cn.linkmore.ops.request.ReqUserResetPW;
import cn.linkmore.ops.utils.ExcelUtil;
import cn.linkmore.util.JsonUtil;
import io.swagger.annotations.Api;

/**
 * 用户信息
 * @author   GFF
 * @Date     2018年5月26日
 * @Version  v2.0
 */
@Controller
@RequestMapping("/admin/biz/user")
@Api(tags = "user", description = "用户信息")
public class UserController {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public ViewPage list(HttpServletRequest request, ViewPageable pageable) {
		return this.userService.findPage(pageable);
	}
	
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public ViewMsg delete(@RequestBody List<Long> ids) {
		ViewMsg msg = null;
		try {
			this.userService.delete(ids);
			msg = new ViewMsg("删除成功", true);
		} catch (DataException e) {
			msg = new ViewMsg(e.getMessage(), false);
		} catch (Exception e) {
			e.printStackTrace();
			msg = new ViewMsg("删除失败", false);
		}
		return msg;
	}
	
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	@ResponseBody
	public ViewMsg reset(@RequestBody ReqUserResetPW reset) {
		ViewMsg msg = null;
		try {
			log.info(JsonUtil.toJson(reset));
			this.userService.reset(reset);
			msg = new ViewMsg("重置成功", true);
		} catch (DataException e) {
			msg = new ViewMsg(e.getMessage(), false);
		} catch (Exception e) {
			e.printStackTrace();
			msg = new ViewMsg("重置失败", false);
		}
		return msg;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/export", method = RequestMethod.POST)
	public void export(ViewPageable pageable, HttpServletResponse response) {
		List<cn.linkmore.account.response.ResPageUser> list = userService.export(pageable);
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		ServletOutputStream sos = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			JSONArray ja = new JSONArray();
			Map<String, Object> s = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			for (ResPageUser rrb : list) {
				s = new HashMap<String, Object>();
				s.put("nickName", rrb.getNickName());
				s.put("mobile", rrb.getMobile());
				s.put("cityName", rrb.getCityName());
				s.put("orderCount", rrb.getOrderCount());
				s.put("loginTime", rrb.getLoginTime() != null ? sdf.format(rrb.getLoginTime()) : "未激活");
				s.put("ordersTime", rrb.getOrdersTime() != null ? sdf.format(rrb.getOrdersTime()) : "未激活");
				s.put("userStatus", this.parseSource(rrb.getUserStatus()));
				s.put("plateNo", rrb.getPlateNo());
				ja.add(s);
			}
			Map<String, String> headMap = new LinkedHashMap<String, String>();
			headMap.put("nickName", "用户名");
			headMap.put("mobile", "手机号码");
			headMap.put("cityName", "常规使用地");
			headMap.put("orderCount", "交易次数");
			headMap.put("loginTime", "最近登录时间");
			headMap.put("ordersTime", "最近下单时间");
			headMap.put("userStatus", "用户状态");
			headMap.put("plateNo", "车牌号");
			baos = new ByteArrayOutputStream();
			String title = "用户信息";
			ExcelUtil.exportExcel(title, headMap, ja, null, 0, baos);
			byte[] content = baos.toByteArray();
			is = new ByteArrayInputStream(content);
			response.reset();
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String((title + ".xlsx").getBytes(), "iso-8859-1"));
			response.setContentLength(content.length);
			sos = response.getOutputStream();
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(sos);
			byte[] buff = new byte[8192];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
			bos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
				}
			}
			if (sos != null) {
				try {
					sos.close();
				} catch (Exception e) {
				}
			}
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e) {
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private String parseAppStatus(SimpleDateFormat sdf, Integer appStatus, Date appTime) {
		String html = "未登录";
		if (appStatus != null && appStatus == 1) {
			html = sdf.format(appTime);
		}
		return html;
	}

	private String parseWechatStatus(SimpleDateFormat sdf, Integer wechatStatus, Date wechatTime) {
		String html = "未绑定";
		if (wechatStatus != null && wechatStatus == 1) {
			html = sdf.format(wechatTime);
		}
		return html;
	}

	private String parseSource(Integer source) {
		if (source == null) {
			return "";
		}
		String text = null;
		switch (source) {
		case 0:
			text = "后台发卷";
			break;
		case 1:
			text = "APP注册";
			break;
		case 2:
			text = "公众号";
			break;
		case 3:
			text = "三方微信";
			break;
		case 4:
			text = "扫码领券";
			break;
		case 5:
			text = "分享领券";
			break;
		}
		return text;
	}
}
