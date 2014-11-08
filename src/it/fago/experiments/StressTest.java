package it.fago.experiments;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class StressTest {

	public static void main(String[] args) throws Exception {
		System.out.println("WARM UP");

		atomicMultiThreadSimpleTest(64, 1);
		modifiedAtomicMultiThreadSimpleTest(64, 1);
		System.out.println("STARTING TEST\n\n");

		System.out
				.println("############################# Thread 2 ###################");

		atomicMultiThreadSimpleTest(2, 1);
		modifiedAtomicMultiThreadSimpleTest(2, 1);

		System.out
				.println("############################# Thread 4 ###################");

		atomicMultiThreadSimpleTest(4, 1);
		modifiedAtomicMultiThreadSimpleTest(4, 1);

		System.out
				.println("############################# Thread 8 ###################");

		atomicMultiThreadSimpleTest(8, 1);
		modifiedAtomicMultiThreadSimpleTest(8, 1);

		System.out
				.println("############################# Thread 16 ###################");

		atomicMultiThreadSimpleTest(16, 1);
		modifiedAtomicMultiThreadSimpleTest(16, 1);

		System.out
				.println("############################# Thread 32 ###################");

		atomicMultiThreadSimpleTest(32, 1);
		modifiedAtomicMultiThreadSimpleTest(32, 1);

		System.out
				.println("############################# Thread 64 ###################");

		atomicMultiThreadSimpleTest(64, 1);
		modifiedAtomicMultiThreadSimpleTest(64, 1);

		System.out
				.println("############################# Thread 128 ###################");

		atomicMultiThreadSimpleTest(128, 1);
		modifiedAtomicMultiThreadSimpleTest(128, 1);
	}

	// ============================================================
	//
	//
	//
	// ============================================================

	private static void atomicMultiThreadSimpleTest(int numOfWorker, int loop)
			throws Exception {
		ExecutorService workers = Executors.newFixedThreadPool(numOfWorker);
		final AtomicLong adder = new AtomicLong();

		for (int x = 0; x < loop; x++) {
			final int ITERATION = 1000000;
			final CountDownLatch latch = new CountDownLatch(ITERATION);

			long t1 = System.nanoTime();
			for (int i = 0; i < ITERATION; i++) {
				workers.submit(new Runnable() {
					@Override
					public void run() {
						adder.getAndIncrement();
						latch.countDown();
					}
				});
			}
			latch.await();
			long elapsed = System.nanoTime() - t1;
			long media = elapsed / ITERATION;
			System.out.println("[ATOMIC] done! [" + ITERATION + "] elapsed: "
					+ (elapsed / 1000000) + "ms media: " + media + "ns");
		}
		workers.shutdownNow();
	}

	private static void modifiedAtomicMultiThreadSimpleTest(int numOfWorker,
			int loop) throws Exception {
		ExecutorService workers = Executors.newFixedThreadPool(numOfWorker);
		final ModifiedAtomicLong adder = new ModifiedAtomicLong();

		for (int x = 0; x < loop; x++) {
			final int ITERATION = 1000000;
			final CountDownLatch latch = new CountDownLatch(ITERATION);

			long t1 = System.nanoTime();
			for (int i = 0; i < ITERATION; i++) {
				workers.submit(new Runnable() {
					@Override
					public void run() {
						adder.getAndIncrement();
						latch.countDown();
					}
				});
			}
			latch.await();
			long elapsed = System.nanoTime() - t1;
			long media = elapsed / ITERATION;
			System.out.println("[MODIFIED] done! [" + ITERATION + "] elapsed: "
					+ (elapsed / 1000000) + "ms media: " + media + "ns");
		}
		workers.shutdownNow();
	}

}// END