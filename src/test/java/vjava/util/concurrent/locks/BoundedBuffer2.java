package vjava.util.concurrent.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer2 {
	final Lock lock = new ReentrantLock();
	final Condition notFull = lock.newCondition();
	final Condition notEmpty = lock.newCondition();

	final Object[] items = new Object[100];
	int putptr, takeptr, count;

	public void put(Object x) throws InterruptedException {
		lock.lock();
		try {
			while (count == items.length)
				notFull.await();
			items[putptr] = x;
			if (++putptr == items.length)
				putptr = 0;
			++count;
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}

	public Object take() throws InterruptedException {
		lock.lock();
		try {
			while (count == 0)
				notEmpty.await();
			Object x = items[takeptr];
			if (++takeptr == items.length)
				takeptr = 0;
			--count;
			notFull.signal();
			return x;
		} finally {
			lock.unlock();
		}
	}

	public static class Producer implements Runnable {

		private BoundedBuffer2 q;

		Producer(BoundedBuffer2 q) {
			this.q = q;
			new Thread(this, "Producer").start();
		}

		int i = 0;

		public void run() {
			int i = 0;
			while (true) {
				try {
					q.put(i++);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static class Consumer implements Runnable {

		private BoundedBuffer2 q;

		Consumer(BoundedBuffer2 q) {
			this.q = q;
			new Thread(this, "Consumer").start();
		}

		public void run() {
			while (true) {
				try {
					System.out.println(q.take());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		final BoundedBuffer2 buffer = new BoundedBuffer2();
		new Thread(new Producer(buffer)).start();
		new Thread(new Consumer(buffer)).start();
	}
}
