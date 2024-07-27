package com.mudra;

import java.time.Duration;

// Continuation related classes have been moved inside this
// internal package but we are making this visible by exposing
// the package --add-exports java.base/jdk.internal.vm=ALL-UNNAMED
import jdk.internal.vm.*;

public class ContinuationPlay {
	
	private static final ContinuationScope SCOPE1 = new ContinuationScope("scope1");

	public static void main(String[] args) throws Exception {
		
		System.out.println("main : enter");
		
		Continuation c = new Continuation(SCOPE1, new IOProcessor());
		while (!c.isDone()) {
			c.run();
			
			System.out.println(">> main : scope1 loop");

			Thread.sleep(Duration.ofSeconds(3));
		}
		
		
		System.out.println("main : exit");

	}

}

class IOProcessor implements Runnable {
	
	private static final ContinuationScope SCOPE2 = new ContinuationScope("scope2");
	
	private void method1() {
		System.out.println("method1 : enter");
		
		int part = 0; 
		
		part++;
		System.out.println("method1 : execute part " + part);
		Continuation.yield(SCOPE2);
		
		part++;
		System.out.println("method1 : execute part " + part);
		Continuation.yield(SCOPE2);

		part++;
		System.out.println("method1 : execute part " + part);
		Continuation.yield(SCOPE2);

		System.out.println("method1 : exit");
	}
	
	private void method() {
		System.out.println("method : enter");
		
		Continuation c = new Continuation(SCOPE2, this::method1);
		while (!c.isDone()) {
			c.run();
			
			System.out.println(">> method : scope2 loop");
		}
		
		System.out.println("method : exit");
		
		
	}

	@Override
	public void run() {
		method();
	}
	
}
