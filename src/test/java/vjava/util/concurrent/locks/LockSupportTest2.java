package vjava.util.concurrent.locks;

import java.util.concurrent.locks.LockSupport;

//LockSupport可以响应中断
//所以AQS中的acquire分为中断和非中断
public class LockSupportTest2 {
	public static void main(String[] args) throws InterruptedException {

		final Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				LockSupport.park();
				System.out.println("thread " + Thread.currentThread().getId() + " awake!");
			}
		});
		// 对于没有启动的线程没有任何影响。
		LockSupport.unpark(t);
		t.start();

		Thread.sleep(3000);
		// 1. unpark
		// LockSupport.unpark(t);

		// 2. 中断
		t.interrupt();
	}
}
