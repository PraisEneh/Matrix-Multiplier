import java.util.HashMap;

public class WorkItem {
	String id;
	int[][] subA;
	int [][] subB;
	int [][] subC;
	int lowA, highA;
	int lowB, highB;
	boolean done;
	
	
	int rowSize;
	int columnSize;
	
	static HashMap<String, Integer> configMap;
	
	public WorkItem( HashMap<String, Integer> config, int lA, int hA, int lB, int hB, int[][]sub_A, int[][]sub_B) {
		configMap = config;
		rowSize = (configMap.get("N")/configMap.get("SplitSize"))+(configMap.get("N")%configMap.get("SplitSize"));
		columnSize = (configMap.get("N")/configMap.get("SplitSize"))+(configMap.get("N")%configMap.get("SplitSize"));
		//System.out.println("Col size: "+ columnSize);
		subA = new int[configMap.get("SplitSize")][configMap.get("N")];
		subB = new int[configMap.get("SplitSize")][configMap.get("N")];
		subC = new int[configMap.get("SplitSize")][configMap.get("SplitSize")];
		done = false;
		lowA = lA;
		highA = hA;
		lowB = lB;
		highB = hB;
		subA = sub_A;
		subB = sub_B;
		
		setID();
	}
	
	public String getID() {
		return id;
	}
	public void setID() {
		id = "(Low A: "+lowA+ " High A: " +highA+")	(Low B: "+lowB+" High B: "+highB+")";
	}
	
	public void setStateTrue() {
		done = true;
		
	}
	
	public void setStateFalse() {
		done = false;
	}
	
}
