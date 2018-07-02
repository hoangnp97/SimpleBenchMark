package vc.benchmark;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class BenLoad {
	public static void main(String[] args) {
		try {
			int cout = loadService(4, 60, "http://localhost:8080/prime?n=");
			System.out.println("number of request: " + cout);
			System.out.println("number of request/s : " + cout / 30.0);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static int loadService(int nThread, int time, String webService) throws InterruptedException, ExecutionException {
		ExecutorService exe = Executors.newFixedThreadPool(nThread);
		LocalDateTime startPoint = LocalDateTime.now();
		List<Callable<Integer>> listTask = new LinkedList<>();
		for(int i = 0; i < nThread; i++) {
			listTask.add(callable(exe, time, webService, startPoint));
		}
		List<Future<Integer>> result = exe.invokeAll(listTask);
		
		int sum = 0;
		for(int i = 0; i < result.size(); i++) {
			sum = sum + result.get(i).get();
		}
		
		return sum;
	}
	
	public static long getDuration(LocalDateTime start) {
		return Duration.between(start, LocalDateTime.now()).getSeconds();
	}
	
	public static Callable<Integer> callable(ExecutorService exe, int time, String webService, LocalDateTime startPoint) {
		return () -> {
			int count = 0;
			while(getDuration(startPoint) <= time) {
				Random rand = new Random();
				int input = rand.nextInt(10000);
				String query = webService + Integer.toString(input);
				HttpURLConnection connection = (HttpURLConnection) new URL(query).openConnection();
				connection.connect();
				if(connection.getResponseCode() == 200) {
					count ++;
				}
			}
			return count;
			
		};
	}
}