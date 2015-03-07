package vjava.util.concurrent.locks;

import java.util.concurrent.locks.LockSupport;

public class LockSupportTest {
	public static void main(String[] args) {
		// 1次unpark给线程1个许可
		LockSupport.unpark(Thread.currentThread());
		// 如果线程非阻塞重复调用没有任何效果
		LockSupport.unpark(Thread.currentThread());
		// 消耗1个许可
		LockSupport.park(Thread.currentThread());
		// 阻塞
		LockSupport.park(Thread.currentThread());
	}
}
