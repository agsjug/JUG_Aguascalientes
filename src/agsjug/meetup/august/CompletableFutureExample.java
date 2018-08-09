package agsjug.meetup.august;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureExample {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		
		CompletableFuture<String> futureName = CompletableFuture.supplyAsync(() -> {
			System.out.println("Getting name from remote service running on thread: " + 
					Thread.currentThread().getName());
			return "AGS JUG";
		}, exec).thenApply((result) -> {
			System.out.println("Executing callback on thread: " + 
					Thread.currentThread().getName());
			
			return "Hello " + result;
		}).thenApplyAsync((result) -> {
			System.out.println("Executing callback on thread: " + 
					Thread.currentThread().getName());
			
			return result + " from Softtek";
		});
		
		System.out.println(futureName.get());
		
		exec.shutdown();
	}

}
