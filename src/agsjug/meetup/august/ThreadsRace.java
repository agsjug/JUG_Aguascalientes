package agsjug.meetup.august;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ThreadsRace {
	private static final String[] poolOfNames = new String[] {"Fulanito", "Sutanito", "Menganito", 
			"Perenganito"};
	private static int competitorsGenerated = 0;
		
	public static void main(String[] args) {
		Race agsJugRace = new ThreadsRace().new Race(poolOfNames.length);
		List<Future<String>> resultPositions = null;
		
		try {
			resultPositions = agsJugRace.startRace();
		} catch (InterruptedException e) {
			System.err.println("Interruptions during race: " + e.getMessage());
		}
		
		resultPositions.stream().map(future -> {
			String result = "Competitor failed to complete race";
			try {
				result = future.get();
			} catch (InterruptedException e) {
				System.err.println("Interruptions during race: " + e.getMessage());
			} catch (ExecutionException e) {
				System.err.println("Errors during race: " + e.getMessage());
			}
			
			return result;
		}).sorted((result1, result2) -> {
			String position1 = result1.substring(result1.lastIndexOf(" ") + 1);
			String position2 = result2.substring(result2.lastIndexOf(" ") + 1);
			return position1.compareTo(position2);
		}).forEach(System.out::println);
		
		agsJugRace.stopRace();
	}
	
	private class Race {
		int finishPosition = 1;
		int numberOfCompetitors;
		ExecutorService executor;
		
		Race(int numberOfCompetitors) {
			this.numberOfCompetitors = numberOfCompetitors;
			executor = Executors.newFixedThreadPool(numberOfCompetitors);
		}
		
		synchronized int getPosition() {
			return this.finishPosition++;
		}
		
		List<Future<String>> startRace() throws InterruptedException {
			List<Future<String>> results = null;
			results = this.executor.invokeAll(this.createCompetitors());
			
			return results;
		}
		
		void stopRace() {
			this.executor.shutdown();
			try {
				this.executor.awaitTermination(3, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				System.err.println("Interruptions while stoping race: " + e.getMessage());
			} finally {
			    if (!executor.isTerminated()) {
			        System.err.println("Cancel non-finished racers");
			    }
			    executor.shutdownNow();
			    System.out.println("Race finished");
			}
		}
		
		List<Callable<String>> createCompetitors() {
			List<Callable<String>> competitors = new ArrayList<>(this.numberOfCompetitors);
			
			for (int i = 1; i <= this.numberOfCompetitors; i++) {
				competitors.add(() -> {
					Thread.currentThread().setName("MeetupThread - " + generateName());
					System.out.println("Competitor " + Thread.currentThread().getName() + 
							" started...");
					TimeUnit.SECONDS.sleep(3);
					
					return Thread.currentThread().getName() + " finished in position: " 
						+ this.getPosition();
				});
			}
			
			return competitors;
		}
		
		synchronized String generateName() {
			return poolOfNames[competitorsGenerated++];
		}
	}
}