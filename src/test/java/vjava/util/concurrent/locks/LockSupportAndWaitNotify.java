package vjava.util.concurrent.locks;

//阻塞当前线程
public class LockSupportAndWaitNotify {

	private static Object obj = new Object();

	public static void main(String[] args) throws InterruptedException {

		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					synchronized (obj) {
						try {
							System.out.println("Thread " + Thread.currentThread().getId() + " block on obj!");
							obj.wait();
							System.out.println("Thread " + Thread.currentThread().getId() + " unblocked!");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}).start();
			Thread.sleep(100);
		}

		synchronized (obj) {
			obj.notifyAll();
		}
	}
}
