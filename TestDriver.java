import java.util.Random;

public class TestDriver {
	private static class ProducerThread extends Thread {

		private final Queue<Integer> q;
		private final int count;
		private final Random rand = new Random();

		public ProducerThread(Queue<Integer> q, int count) {
			this.q = q;
			this.count = count;
		}

		@Override
		public void run() {
			for (int i = 0; i < count; i++) {
				q.enqueue(i);
				System.out.println("Enqueued "+ i);
				try {
					Thread.sleep(Math.abs(rand.nextInt() % 10));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
			q.enqueue(q.getExitMessage());
		}
	}

	private static class ConsumerThread<V> extends Thread {

		private final Queue<V> q;
		private final Random rand = new Random();

		public ConsumerThread(Queue<V> q) {
			this.q = q;
		}

		@Override
		public void run() {
			while (true) {
				V val = q.dequeue();
				try {
					Thread.sleep(Math.abs(rand.nextInt() % 15));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				if (val != q.getExitMessage()) {
					System.out.println("Dequeued " + val);
				} else {
					System.out.println("Exit messsage received; queue closed");
					break;
				}
			}
		}
	}

	public static void main(String[] args) {
		int capacity = 100;
		Integer exitMessage = new Integer(-1);
		Queue<Integer> q = new Queue<Integer>(capacity, exitMessage);
		ProducerThread pt1 = new ProducerThread(q, capacity * 10);
		ConsumerThread<Integer> ct1 = new ConsumerThread<Integer>(q);
		ProducerThread pt2 = new ProducerThread(q, capacity * 10);
		ConsumerThread<Integer> ct2 = new ConsumerThread<Integer>(q);
		pt1.start();
		ct1.start();
		pt2.start();
		ct2.start();
		try {
			pt1.join();
			ct1.join();
			pt2.join();
			ct2.join();
		} catch (InterruptedException e) {
			System.out.println("Caught unexpected InterruptedException");
		}
	}
}
