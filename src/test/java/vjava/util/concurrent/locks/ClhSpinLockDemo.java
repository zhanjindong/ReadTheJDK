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
	private final ThreadLocal<Node> pred;
	private final ThreadLocal<Node> node;
	private final AtomicReference<Node> tail = new AtomicReference<Node>(new Node());

	public ClhSpinLock() {
		this.node = new ThreadLocal<Node>() {
			protected Node initialValue() {
				return new Node();
			}
		};

		this.pred = new ThreadLocal<Node>() {
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
		this.pred.set(pred);
		while (pred.locked) {// 进入自旋
		}
	}

	public void unlock() {
		final Node node = this.node.get();
		node.locked = false;
		this.node.set(this.pred.get());
	}

	private static class Node {
		private volatile boolean locked;
	}
}

public class ClhSpinLockDemo {
	public static void main(String[] args) {
		ClhSpinLock lock = new ClhSpinLock();
		lock.lock();
		//lock.unlock();
		lock.lock();
	}
}
