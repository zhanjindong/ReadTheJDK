package vjava.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * this class used to explain how to use delayqueue
 * 
 * @author Eric
 * 
 */
public class DelayQueueDemo {
	public static void main(String[] args) {
		try {
			ExecutorService es = Executors.newCachedThreadPool();
			final DelayQueue<DelayTask> queue = new DelayQueue<DelayTask>();
			Random rand = new Random();
			// add 20's DelayTask to DelayQueue
			// for (int i = 0; i < 20; i++) {
			// queue.add(new DelayTask(rand.nextInt(500), "task " + i));
			// }

			queue.add(new DelayTask(60000, "task " + 3));

			// 因为DelayQueue使用的是重入锁，所以当第一个任务还有到执行的时候(Condition.await)，其他的线程可以获取到锁并向队列中加入新的任务。
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(40);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					queue.add(new DelayTask(5000, "task " + 1));
					System.out.println("add success by other thread");
				}
			}).start();

			// EndTask endTaks = new EndTask(1000000, "endtask");
			// add endtask to DelayQueue, it's time is 50001 to ensure will be
			// execute at the end of queue
			// queue.add(endTaks);
			es.execute(new DelayConsumer(queue));
			TimeUnit.SECONDS.sleep(100000);
			es.shutdownNow();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class DelayConsumer implements Runnable {
	private DelayQueue<DelayTask> queue;

	public DelayConsumer(DelayQueue<DelayTask> queue) {
		this.queue = queue;
	}

	public void run() {
		try {
			System.out.println("############print order by DelayQueue###############");
			// from DelayQueue get DelayTask and execute it
			while (!Thread.interrupted()) {
				queue.take().run();
			}
		} catch (InterruptedException e) {
			System.out.println("Exit Consumer");
		}
	}
}

class EndTask extends DelayTask {

	public EndTask(long delay, String name) {
		super(delay, name);
	}

	@Override
	public void run() {
		try {
			TimeUnit.MILLISECONDS.sleep(1);
			// print all task from internal list
			System.out.println("#############print by list order###############");
			for (DelayTask delayTask : tasks) {
				System.out.println("end" + delayTask);
			}
		} catch (InterruptedException e) {
			System.out.println("Exit LIST");
		}
	}
}

class DelayTask implements Runnable, Delayed {
	protected static List<DelayTask> tasks = new ArrayList<DelayTask>();
	public long time = 0;
	public long delay;
	private static int count = 0;
	private int id = count++;
	private String name;

	public DelayTask(long delay, String name) {
		this.delay = delay;
		this.name = name;
		time = System.nanoTime() + TimeUnit.NANOSECONDS.convert(this.delay, TimeUnit.MILLISECONDS);
		tasks.add(this);
	}

	@Override
	public int compareTo(Delayed o) {
		long result = this.getTime() - ((DelayTask) o).getTime();
		if (result > 0) {
			return 1;
		}
		if (result < 0) {
			return -1;
		}
		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(time - System.nanoTime(), TimeUnit.NANOSECONDS);
	}

	@Override
	public void run() {
		System.out.println(this);
	}

	public String toString() {
		// use time to print for get real "delay time"
		return "Task " + id + ":" + delay + ",name:" + name;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
