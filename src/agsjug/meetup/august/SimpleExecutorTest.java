package agsjug.meetup.august;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SimpleExecutorTest {
	
	private ExecutorService threadPool;

	public static void main(String[] args) {
		SimpleExecutorTest test = new SimpleExecutorTest();
		test.threadPool = Executors.newSingleThreadExecutor();
		//test.threadPool = Executors.newFixedThreadPool(2);
		test.runTest();
	}
	
	private void runTest() {		
		Future<String> result = threadPool.submit(() -> {
			System.out.println("Doing task on thread: " + Thread.currentThread().getName());
			TimeUnit.SECONDS.sleep(3);
			return "some value";
		});
		
		System.out.println("Doing something on thread: " + Thread.currentThread().getName());
		
		Future<String> result2 = threadPool.submit(() -> {
			System.out.println("Doing another task on thread: " + Thread.currentThread().getName());
			TimeUnit.SECONDS.sleep(3);
			return "another value";
		});
		
		System.out.println("Doing something else on thread: " + Thread.currentThread().getName());
		
		while (!result.isDone() || !result2.isDone()) {
			try {
				System.out.println("Still working on tasks...");
				TimeUnit.MILLISECONDS.sleep(400);
			} catch (InterruptedException e) {
				System.err.println("Interrupted: " + e.getMessage());
			}
		}
		
		try {
			System.out.println("Result of task 1: " + result.get());
			System.out.println("Result of task 2: " + result2.get());
		} catch (InterruptedException e) {
			System.err.println("Interrupted: " + e.getMessage());
		} catch (ExecutionException e) {
			System.err.println("Error while completing task: " + e.getMessage());
		}

		threadPool.shutdown();
	}

}
