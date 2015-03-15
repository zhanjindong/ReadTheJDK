package vjava.util.concurrent.locks;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 
 * 基于CLH lock queue实现的自旋锁。
 * <p>
 * CLH lock queue:
 * 
 * <pre>
 *      +------+  prev +-----+       +-----+
 * head |      | <---- |     | <---- |     |  tail
 *      +------+       +-----+       +-----+
 * </pre>
 * 
 */
class ClhSpinLock {
	private final ThreadLocal<Node> prev;
	private final ThreadLocal<Node> node;
	private final AtomicReference<Node> tail = new AtomicReference<Node>(new Node());

	public ClhSpinLock() {
		this.node = new ThreadLocal<Node>() {
			protected Node initialValue() {
				return new Node();
			}
		};

		this.prev = new ThreadLocal<Node>() {
			protected Node initialValue() {
				return null;
			}
		};
	}

	public void lock() {
		final Node node = this.node.get();
		node.locked = true;
		// 一个CAS操作即可将当前线程对应的节点加入到队列中，
		// 并且同时获得了predecessor节点的引用，然后就是等待predecessor释放锁
		Node pred = this.tail.getAndSet(node);
		this.prev.set(pred);
		while (pred.locked) {// 进入自旋
		}
	}

	public void unlock() {
		final Node node = this.node.get();
		node.locked = false;
		this.node.set(this.prev.get());
	}

	private static class Node {
		private volatile boolean locked;
	}
}

public class ClhSpinLockDemo {
	public static void main(String[] args) throws InterruptedException {
		final ClhSpinLock lock = new ClhSpinLock();
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
		lock.unlock();
	}
}
