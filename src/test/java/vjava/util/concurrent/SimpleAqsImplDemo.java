package vjava.util.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 
 * AQS的简单实现类。非重入的。
 * <p>
 * 其实这就是ThreadPoolExecutor$Worker的实现代码。
 * <p>
 * AQS的主要实现有：
 * <ul>
 * <li>ReentrantLock</li>
 * <li>Semaphore</li>
 * <li>CountDownLatch</li>
 * <li>FutureTask</li>
 * </ul>
 * 每个实现都各自有一个内部抽象类Sync实现AQS，具体实现又分为FairSync和NonfairSync，从而实现公平和非公平竞争。
 * 
 * @author jdzhan,2015-3-10
 * 
 */
class SimpleLock extends AbstractQueuedSynchronizer {
	private static final long serialVersionUID = -7316320116933187634L;

	public SimpleLock() {

	}

	// state只有0和1，互斥
	protected boolean tryAcquire(int unused) {
		if (compareAndSetState(0, 1)) {
			setExclusiveOwnerThread(Thread.currentThread());
			return true;// 成功获得锁
		}
		// 线程进入等待队列
		return false;
	}

	protected boolean tryRelease(int unused) {
		setExclusiveOwnerThread(null);
		setState(0);
		return true;
	}

	public void lock() {
		acquire(1);
	}

	public boolean tryLock() {
		return tryAcquire(1);
	}

	public void unlock() {
		release(1);
	}

	public boolean isLocked() {
		return isHeldExclusively();
	}
}

public class SimpleAqsImplDemo {
	public static void main(String[] args) throws InterruptedException {
		final SimpleLock lock = new SimpleLock();
		lock.lock();

		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					lock.lock();
					System.out.println(Thread.currentThread().getId() + " acquired the lock!");
					lock.unlock();
				}
			}).start();
			// 简单的让线程按照for循环的顺序阻塞在lock上
			Thread.sleep(100);
		}

		System.out.println("main thread unlock!");
		// 释放lock可以看到，阻塞的锁是按照顺序依次获取到锁的。
		// AQS的核心是CLH lock queue的一个变种
		lock.unlock();
	}
}
