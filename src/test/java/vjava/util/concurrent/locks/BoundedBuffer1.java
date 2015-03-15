package vjava.util.concurrent.locks;


class BoundedBuffer1 {
	private int contents;

	final Object[] items = new Object[100];
	int putptr, takeptr, count;

	public synchronized void put(Object x) {
		while (count == items.length) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}

		items[putptr] = x;
		if (++putptr == items.length)
			putptr = 0;
		++count;
		notifyAll();
	}

	public synchronized Object take() {
		while (count == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		Object x = items[takeptr];
		if (++takeptr == items.length)
			takeptr = 0;
		--count;
		notifyAll();
		return x;
	}
	
	public static class Producer implements Runnable {

		private BoundedBuffer1 q;

		Producer(BoundedBuffer1 q) {
			this.q = q;
			new Thread(this, "Producer").start();
		}

		int i = 0;

		public void run() {
			int i = 0;
			while (true) {
				q.put(i++);
			}
		}
	}

	public static class Consumer implements Runnable {

		private BoundedBuffer1 q;

		Consumer(BoundedBuffer1 q) {
			this.q = q;
			new Thread(this, "Consumer").start();
		}

		public void run() {
			while (true) {
				System.out.println(q.take());
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		final BoundedBuffer1 buffer = new BoundedBuffer1();
		new Thread(new Producer(buffer)).start();
		new Thread(new Consumer(buffer)).start();
	}
}
