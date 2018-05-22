package cn.linkmore.prefecture.fee;

import com.linkmore.lock.bean.AbuttingBean;
import com.linkmore.lock.factory.LockFactory;
/**
 * 初始化锁工厂
 * @author jiaohanbin
 * @version 2.0
 *
 */
public class InitLockFactory {
	private static LockFactory lockFactory;

	private InitLockFactory() {
	}

	public static LockFactory getInstance() {
		if (lockFactory == null) {
			lockFactory = LockFactory.getInstance();
			AbuttingBean abuttingBean = new AbuttingBean();
			abuttingBean.setHuaQingUrl("http://plockopen.huaching.com/api");
			abuttingBean.setLinkmoreUrl("http://192.168.1.81:8081");
			lockFactory.setAbuttingBean(abuttingBean);
		}
		return lockFactory;
	}
}