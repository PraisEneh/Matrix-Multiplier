import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Producer extends Thread {

	static AtomicBoolean isRunning = new AtomicBoolean(true);
	private final Buffer buffer;
	static HashMap<String, Integer> configMap;
	private int[][] matrixA;
	private int[][] matrixB;
	int splitSize;
	long MaxProducerSleepTime;
	static int count;
	int pointerAY;
	int pointerAX;
	int pointerBY;
	int pointerBX;
	int globalPointerX, globalPointerY;
	int globalLowA, globalHighA;
	int globalLowB, globalHighB;
	int[][] globalSubA;
	int[][] globalSubB;
	int iterationTimer, iterationTimerLimit;


	long sleepTotal = 0;
	long sleepCounter = 0;
	

	public Producer(int[][] A, int[][] B, Buffer b, int split, int sleeptime, HashMap<String, Integer> config) {

		buffer = b;
		matrixA = A;
		matrixB = B;
		splitSize = split;
		MaxProducerSleepTime = sleeptime;
		count = 0;
		pointerAY = 0;
		pointerAX = 0;
		pointerBY = 0;
		pointerBX = 0;
		configMap = config;
	    iterationTimerLimit = (configMap.get("M")*configMap.get("P"));
	    iterationTimer=0;
	    setDaemon(true);
	}

	public synchronized void splitWork() {

		if(iterationTimer>iterationTimerLimit) {return;}
		for(globalPointerY=0; globalPointerY < ((configMap.get("M")/configMap.get("SplitSize"))+(configMap.get("M")%configMap.get("SplitSize"))); globalPointerY++) {
			globalSubA = doA();
			//printSUBA();
			for(globalPointerX=0; globalPointerX < ((configMap.get("P")/configMap.get("SplitSize"))+(configMap.get("P")%configMap.get("SplitSize"))); globalPointerX++) {
				globalSubB = doB();
				//printSUBB();
				//System.out.println("LOW INDEX A TO WORK ITEM "+globalLowA+", HIGH INDEX A "+globalHighA+" LOW INDEX B TO WORK ITEM "+globalLowB+", HIGH INDEX B "+globalHighB);
				WorkItem workItem = new WorkItem(configMap, globalLowA, globalHighA, globalLowB, globalHighB, globalSubA, globalSubB);
				buffer.insert(workItem);
				count++;
				iterationTimer++;
				globalSubB = null;
			}
			globalSubA = null;
		}
		

	}
	
	public synchronized int[][] doA() {
		int counter = 0;
		int lowA = pointerAY;
		int highA = ((splitSize + pointerAY) - 1);
		int [][]subA = new int[configMap.get("SplitSize")][configMap.get("N")];
		if(highA > configMap.get("M")-1) {highA = configMap.get("M")-1;}
		//System.out.println("(Low A: "+lowA+ " High A: " +highA+")");
		for (int i = 0; i < subA.length; i++) {
			for (int j = 0; j < subA[i].length; j++) {
				subA[i][j] = matrixA[pointerAY][pointerAX];
				pointerAX++;

			}
			pointerAX = 0;
			pointerAY++;
			if(pointerAY==configMap.get("M")) {pointerAY=0; break;}
			counter++;
		}
		pointerAX = 0;
		if (configMap.get("SplitSize") == counter) {
			globalLowA = lowA;
			globalHighA = highA;
			return subA;
			}
		globalLowA = lowA;
		globalHighA = highA;
		return subA;
	}
	//Tried with different dimensions. Trying to get the indexs correct
	public synchronized int[][] doB() {
		int counter = 0;
		int lowB = pointerBX;
		int highB = ((splitSize + pointerBX) - 1);
		int [][]subB = new int[configMap.get("SplitSize")][configMap.get("N")];
		if(highB > configMap.get("P")-1) {highB = (configMap.get("P")-1);}
		//System.out.println("(Low B: "+lowB+ " High B: " +highB+")");
		for (int i = 0; i < subB.length; i++) {
			for (int j = 0; j < subB[i].length; j++) {
				subB[i][j] = matrixB[pointerBY][pointerBX];
				pointerBY++;
			}
			pointerBY = 0;
			pointerBX++;
			counter++;
			if (pointerBX == configMap.get("P")) {highB = pointerBX-1; pointerBX = 0;break;}

		}
		pointerBY = 0;
		if(configMap.get("SplitSize")==counter) {
			globalLowB = lowB;
			globalHighB = highB;
			return subB;
			}
		globalLowB = lowB;
		globalHighB = highB;
		return subB;
	}

	public synchronized int getWorkItemCount() {
		return count;
	}

	public synchronized double getAvgSleep() {
		long avgSleep = sleepTotal / sleepCounter;

		return avgSleep;
	}

	/**
	 * Sleep for random amount of time within config range
	 */
	public synchronized void goToSleep() {
		long lowerLimit = 0L;
		long upperLimit = (long) MaxProducerSleepTime;
		long sleepTime = lowerLimit + (long) (Math.random() * (upperLimit - lowerLimit));
		try {
			System.out.println("Producer sleeping for " + sleepTime + " time");
			Driver.producerSleepTimes.add(sleepTime);
			Thread.sleep(sleepTime);
			sleepTotal += sleepTotal;
			sleepCounter++;
			getAvgSleep();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void printSUBA() {
		System.out.println("SUB_A GOING TO WORKITEM");
		for (int i = 0; i < globalSubA.length; i++) {
			for (int j = 0; j < globalSubA[i].length; j++) {
					System.out.print(globalSubA[i][j]+", ");
			}
			System.out.println();
		}
	}
	public synchronized void printSUBB() {
		System.out.println("SUB_B GOING TO WORKITEM");
		for (int i = 0; i < globalSubB.length; i++) {
			for (int j = 0; j < globalSubB[i].length; j++) {
				System.out.print(globalSubB[i][j]+", ");
			}
			System.out.println();
		}
	}
	public synchronized static void requestStop() {
		   isRunning.set(false);
		   try {
			  // Driver.notifyMe();
			//Thread.currentThread().interrupt();;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   System.out.println("Producer Stopped");
	   }

	/*
	 * Check if all produced workitems are finished. For each completed work item,
	 * copy the subC matrix of such a work item to the final result matrix C
	 */


	public void run() {
		while (isRunning.get()) {
			splitWork();
			goToSleep();
			if(iterationTimer>iterationTimerLimit) {
					requestStop();
					break;
			}
	
			//System.out.println("Buffer size: " + buffer.getSize());
		}
	}

}