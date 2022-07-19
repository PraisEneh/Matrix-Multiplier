public class Buffer {
     final int MaxBuffSize;
     static int MaxBuff;
     WorkItem [] store;
     int BufferStart, BufferEnd, BufferSize;
     static int fullCounter;
     static int emptyCounter;
     boolean isFull = false;
   
    public Buffer(int size) {
        MaxBuffSize = size;
        MaxBuff = MaxBuffSize;
        BufferEnd = -1;
        BufferStart = 0;
        BufferSize = 0;
        store = new WorkItem[MaxBuffSize];
}
    public synchronized void insert(WorkItem work) {
    	if(work==null) {return;}
        try {
            while (BufferSize == MaxBuffSize) {
            	fullCounter++;
                wait();
            }
            BufferEnd = (BufferEnd + 1) % MaxBuffSize;
            store[BufferEnd] = work;
            BufferSize++;
            notifyAll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
} }
    public synchronized WorkItem get() {
        try {
            while (BufferSize == 0) {
            	emptyCounter++;
                wait();
            }
            WorkItem work = store[BufferStart];
            BufferStart = (BufferStart + 1) % MaxBuffSize;
            BufferSize--;
            notifyAll();
            return work;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

}
        System.out.println("Nothing removed");
		return null;
		
    }
    
    public synchronized static int getSize() {
    	return MaxBuff;
    }
    
   /* public synchronized WorkItem find(String input) {
    	for(int i=0; i<store.length; i++) {
    		WorkItem work = store[i];
    		String name = work.getName();
    		if(name.contains(input)) {
    			return work;
    		}
    	}
    	System.out.println("Nothing Found");
    	return null;
    }*/
    
    public synchronized static int getFullCount() {
    	return fullCounter;
    }
    public synchronized static int getEmptyCount() {
    	return emptyCounter;
    }
    

    
    
    
}