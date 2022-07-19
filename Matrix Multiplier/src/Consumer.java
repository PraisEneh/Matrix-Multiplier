import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

class Consumer extends Thread {
   static AtomicBoolean isRunning = new AtomicBoolean(true);
   private final Buffer buffer;
   private WorkItem work;
   HashMap<String, Integer> configMap;
   int [][] matrixC;
   long sleepTotal = 0;
   long sleepCounter = 0;
   long MaxConsumerSleepTime;
   static int consumeCount = 0;
   int iterationTimer = 0;
   int iterationTimerLimit;
   int A, B;
   static int lowLimit, highLimit;

   
   public Consumer(Buffer b, HashMap<String, Integer> config, int [][] matrixc) {
      buffer = b;
      configMap = config;
      matrixC = matrixc;
      MaxConsumerSleepTime = configMap.get("MaxConsumerSleepTime");
	  setDaemon(true);
	  iterationTimerLimit = (configMap.get("M")*configMap.get("P"));

}
   
   public synchronized void goToSleep() {
		long lowerLimit = 0L;
		long upperLimit = (long) MaxConsumerSleepTime;
		long sleepTime = lowerLimit + (long) (Math.random() * (upperLimit - lowerLimit));
		try {
			System.out.println("Consumer sleeping for " + sleepTime + " time");
			Driver.consumerSleepTimes.add(sleepTime);
			Thread.sleep(sleepTime);
			sleepTotal += sleepTotal;
			sleepCounter++;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
   
   public synchronized static void requestStop() {
	   isRunning.set(false);
	   try {
		   //Driver.end();
		   System.out.println("Consumer Stopped");
		   Consumer.currentThread().notifyAll();
		   
	   }catch(Exception e) {
		   System.out.println("Consumer Ended");
	   }
   }
   
   public synchronized long getAvgSleep() {
		long avgSleep = sleepTotal / sleepCounter;

		return avgSleep;
	}
   
   
   //Multiplies any two given 2D array subrows
   //the sum of the subrows will be the index value in the C matrix
   public synchronized int multiply(int[]a, int[]b) {
	   int sum = 0;
	   for(int i=0; i<a.length; i++) {
		   sum = sum+(a[i]*b[i]);
	   }
	return sum;
   }
   
   public synchronized void consume() {
 	  if(iterationTimer>=iterationTimerLimit) {return;}
       work = buffer.get();
       A = work.lowA;
       B = work.lowB;
       System.out.println("TO WORK: "+work.getID());
       //for each index in each split multiply
       for(int i=0; i< configMap.get("SplitSize"); i++) {
    	   for(int j=0; j<configMap.get("SplitSize"); j++) {
    		   //if the index is larger than split max continue
    		   if(A>configMap.get("M")-1||B>configMap.get("P")-1) {continue;}
 		  		int check = multiply(work.subA[i], work.subB[j]);
 		  		if(check==0) {continue;}
 		  		work.subC[i][j] = check;
 		  		System.out.println("now in subC "+ work.subC[i][j]);
	   	  	  System.out.println("SUM OF MULTIPLY METHOD:  "+ check+" ON INDEX: ("+A+", "+B+")");
	   	  	  matrixC[A][B] = check;
	   	  	  iterationTimer++;
	   	  	  B++;
    	   }
    	   B = work.lowB;
    	   A++;
 	  }
       A = 0;
       B = 0;
   }
   
   
public void run() {
      while (isRunning.get()) {
    	  consume();
    	  consumeCount++;
          goToSleep();
    	  if(iterationTimer>=iterationTimerLimit) {requestStop(); break;}

      }
	   //Producer.requestStop();	   
	} 
	


}
