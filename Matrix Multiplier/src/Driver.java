import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;



public class Driver extends Thread {
	static AtomicBoolean isRunning = new AtomicBoolean(true);
	static Random ran = new Random();
	
	static HashMap<String, Integer> configMap = new HashMap<String, Integer>();
	static LinkedList<Long> consumerSleepTimes = new LinkedList<Long>();
	static LinkedList<Long> producerSleepTimes = new LinkedList<Long>();
	static int [][] matrixA;
	static int [][] matrixB;
	static int [][] matrixC;
	static int [][] matrixC2;
	static long startTime;
	static long endTime;
	 /*Test Variables
	  * static int[][]MatrixA = {
				{1,2,3},
				{1,2,3},
				
		};		static int[][]MatrixB = {
				{1,2},
				{1,2},
				{1,2}
				
		};*/
	static double avgThreadSleepTime;
	int numOfProThread;
	int numOfConThread;
	
	
	static Buffer buffer;
	static Consumer c;
	static Consumer c2;
	static Producer p;
	


	public synchronized static void initProgram(String s) {
		File file = new File(s);
		try {
			Scanner sc = new Scanner(file);
			boolean flag = false;
			while(sc.hasNext()&& !flag) {
				String input = sc.nextLine();
				if(input.contains("#")) {flag = true; break;}
				String[] parts =input.split(" ");
				int value = Integer.parseInt(parts[1]);
				
				configMap.put(parts[0], value);
			}
			System.out.println("Configurations: "+configMap);
			matrixA = new int[configMap.get("M")][configMap.get("N")];
			matrixB = new int[configMap.get("N")][configMap.get("P")];
			matrixC = new int[configMap.get("M")][configMap.get("P")];
			matrixC2 = new int[configMap.get("M")][configMap.get("P")];
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Matrix A Length: "+ matrixA.length);
		//Fill Matrix A
		for(int i=0; i<matrixA.length; i++) {
			for(int j=0; j<matrixA[i].length; j++) {
				matrixA[i][j] = ran.nextInt(10); 
			}
		}

		System.out.println("Size: "+matrixA.length);
		//Fill Matrix B
		for(int i=0; i<matrixB.length; i++) {
			for(int j=0; j<matrixB[i].length; j++) {
				matrixB[i][j] = ran.nextInt(10);
			}
		}
		
		System.out.println();
		System.out.println();
		System.out.println("Matrix A");
		System.out.println();
		for(int i=0; i<matrixA.length; i++) {
			for(int j=0; j<matrixA[i].length; j++) {
				System.out.print(matrixA[i][j]+", ");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
		System.out.println("Matrix B");
		System.out.println();
	
		for(int i=0; i<matrixB.length; i++) {
			for(int j=0; j<matrixB[i].length; j++) {
				System.out.print(matrixB[i][j]+", ");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
		
		
	
	}
	
	public synchronized static void end() {
		Producer.isRunning.set(false);
		Consumer.isRunning.set(false);
		System.out.println("PRODUCER/CONUSMER SIMULATION RESULT");
		System.out.println("Default 3 Loops Matrix");
		int sum = 0;
		for(int i=0; i<matrixC2.length; i++) {
			for(int j=0; j<matrixC2[i].length; j++) {
				for(int k=0; k<configMap.get("N"); k++) {
					sum = sum+(matrixA[i][k] * matrixB[k][j]);
				}
				matrixC2[i][j] = sum;
				sum = 0;
				System.out.print(matrixC2[i][j]+", ");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("Resultant Bounded Buffer Matrix");
		for(int i=0; i<matrixC.length; i++) {
			for(int j=0; j<matrixC[i].length; j++) {
				System.out.print(matrixC[i][j]+", ");
			}
			System.out.println();
		}
		System.out.println();
		long duration = 0;
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("PRODUCER/CONUSMER SIMULATION STATISTICS");
		System.out.println("Simulation Time: "+ duration+"ms");
		printSleepTimes();
		System.out.println("Number of Producer Threads: "+ configMap.get("NumProducer"));
		System.out.println("Number of Consumer Threads: "+ configMap.get("NumConsumer"));
		System.out.println("Size Of Buffer: "+ Buffer.getSize());
		System.out.println("Number Of Workers Produced: "+ Producer.count);
		System.out.println("Number Of Workers Consumed: "+ Consumer.consumeCount);
		System.out.println("Number Of Times Buffer Was Empty: "+Buffer.getEmptyCount());
		System.out.println("Number Of Times Buffer Was Full: "+Buffer.getFullCount());
		 
		System.exit(0);
	}
	
	
	

	
	public synchronized static void begin() {
		ExecutorService executor = Executors.newFixedThreadPool(configMap.get("NumConsumer")+(configMap.get("NumProducer")));
		buffer = new Buffer(configMap.get("MaxBuffSize"));
		//p = new Producer(matrixA, matrixB, buffer, configMap.get("SplitSize"), configMap.get("MaxProducerSleepTime"), configMap);
		executor.execute(new Producer(matrixA, matrixB, buffer, configMap.get("SplitSize"), configMap.get("MaxProducerSleepTime"), configMap));
		executor.execute(new Producer(matrixA, matrixB, buffer, configMap.get("SplitSize"), configMap.get("MaxProducerSleepTime"), configMap));
		executor.execute(new Consumer(buffer, configMap, matrixC));
		executor.execute(new Consumer(buffer, configMap, matrixC));
		executor.execute(new Consumer(buffer, configMap, matrixC));
		//c = new Consumer(buffer, configMap, matrixC, 0,(configMap.get("SplitSize")/2));
		//c2 = new Consumer(buffer, configMap, matrixC,(configMap.get("SplitSize")/2), upper);
		/*p.start();
		c.start();
		c2.start();*/
		try {
			long sleepTime = 1000;
			isRunning.set(false);
			Thread.sleep(sleepTime);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			executor.wait();
			//p.requestStop();
			/*p.join();
			c.join();
			c2.join();*/

		}catch(Exception e) {
			System.out.println("");
		}

	}
	
	public synchronized static void printSleepTimes() {
		long proSum = 0;
		for(int i=0; i<producerSleepTimes.size(); i++) {
			proSum = proSum + producerSleepTimes.get(i);
		}
		long newProSum = proSum / producerSleepTimes.size();
		System.out.println("Average Producer Sleep Time: "+ newProSum+"ms");
		
		long conSum = 0;
		for(int i=0; i<consumerSleepTimes.size(); i++) {
			conSum = conSum + consumerSleepTimes.get(i);
		}
		long newConSum = conSum / consumerSleepTimes.size();
		System.out.println("Average Consumer Sleep Time: "+ newConSum+"ms");
	}
	

	public static void main(String[] args) {
		startTime = System.currentTimeMillis();
		initProgram("config.dat");
		begin();
		end();
	}

}
