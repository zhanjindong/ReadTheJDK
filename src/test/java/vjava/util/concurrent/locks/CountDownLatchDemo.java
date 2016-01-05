package vjava.util.concurrent.locks;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {
	
	public static void main(String[] args) throws InterruptedException {
		final int N = 10;
		CountDownLatch startSignal = new CountDownLatch(1);
		CountDownLatch doneSignal = new CountDownLatch(N);

		for (int i = 0; i < N; ++i)
			// create and start threads
			new Thread(new Worker(startSignal, doneSignal)).start();

		// doSomethingElse(); // don't let run yet
		System.out.println("doSomethingElse1");
		startSignal.countDown(); // let all threads proceed
		// doSomethingElse();
		System.out.println("doSomethingElse2");
		doneSignal.await(); // wait for all to finish
		System.out.println("doSomethingElse after finish.");
	}
}

class Worker implements Runnable {
	private final CountDownLatch startSignal;
	private final CountDownLatch doneSignal;

	Worker(CountDownLatch startSignal, CountDownLatch doneSignal) {
		this.startSignal = startSignal;
		this.doneSignal = doneSignal;
	}

	public void run() {
		try {
			System.out.println(Thread.currentThread().getName());
			startSignal.await();
			doWork();
			doneSignal.countDown();
		} catch (InterruptedException ex) {
		} // return;
	}

	void doWork() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
