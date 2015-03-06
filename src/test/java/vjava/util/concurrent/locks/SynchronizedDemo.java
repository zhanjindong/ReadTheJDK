package vjava.util.concurrent.locks;

public class SynchronizedDemo {

	private static Object lock = new Object();

	public static void main(String[] args) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				test();
			}
		}).start();

		test();
		System.out.println("ok");
	}

	static void test() {
		synchronized (lock) {
			for (int i = 0; i < 1000; i++) {
				if (i==500) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println(Thread.currentThread().getId() + ":" + i);
			}
		}
	}
}
