package data_processing;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class RuntimeMeasure {
	static RuntimeMeasure me = null;
	String fileName = "runtime.csv";
	ArrayList<String> columnNames = new ArrayList<String>();
	ArrayList<String> data = new ArrayList<String>();
	PrintWriter writer;
	int count = 0;
	int printed = 0;
	boolean header = false;
	char[] buchstaben = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	
	private RuntimeMeasure(){
		try {
			writer = new PrintWriter(fileName, "UTF-8");
		}catch(Exception e){
			
		}
	}
	
	static public RuntimeMeasure getRuntimeMeasure(){
		if(me == null)
			me = new RuntimeMeasure();
		return me;
	}
	
	public void printAll(){
		if(writer == null)return;
		String row="x;";
		if(header == false){
			header = true;
			for(int j=0;j<columnNames.size();j++){
				row = row + columnNames.get(j) + ";" ;
			}
			writer.println(row);
			System.out.println(row);
			row = "x;";
			for(int j=0;j<columnNames.size();j++){
				row = row + "=MAX("+buchstaben[j+1]+"5:"+buchstaben[j+1]+"100000);";
			}
			writer.println(row);
			System.out.println(row);
			row = "x;";
			for(int j=0;j<columnNames.size();j++){
				row = row + "=MIN("+buchstaben[j+1]+"5:"+buchstaben[j+1]+"100000);";
			}
			writer.println(row);
			System.out.println(row);
			row = "x;";
			for(int j=0;j<columnNames.size();j++){
				row = row + "=MITTELWERT("+buchstaben[j+1]+"5:"+buchstaben[j+1]+"100000);";
			}
			writer.println(row);
			System.out.println(row);
		}
		int size = data.size()-5;
		int nameSize = columnNames.size();
		row = "";
		for(int i=printed;i<size;i++){
			row = row + data.get(i) + ";";
			if(i%nameSize == nameSize-1){
				
				if(row.startsWith("x"))
				writer.println(row);
				System.out.println(row);
				row =  "x;";
			}
		}	
	}
	
	public void add(String cur){
		data.add(cur);
		if(data.size()%columnNames.size()==columnNames.size()-1){
			count ++;
		}
		if(count == 2){
			printAll();
			count = 0;
		}
	}
	
	public void addNames(String name){
		for(int i=0;i<columnNames.size();i++){
			if(columnNames.get(i).equals(name))return;
		}
		columnNames.add(name);
	}
}

class NanoSecondsTimestampProvider {

    private long nanoSecondsOffset, nanoSecondsError;

    public NanoSecondsTimestampProvider() {
        long curMilliSecs0, curMilliSecs1, 
               curNanoSecs, startNanoSecs, endNanoSecs;
        do {    
            startNanoSecs = System.nanoTime();
            curMilliSecs0 = System.currentTimeMillis(); 
            curNanoSecs = System.nanoTime();
            curMilliSecs1 = System.currentTimeMillis(); 
            endNanoSecs = System.nanoTime();
        } while ( curMilliSecs0 == curMilliSecs1 );
         
        nanoSecondsOffset = 1000000L*curMilliSecs1 - curNanoSecs;
        nanoSecondsError = endNanoSecs - startNanoSecs;
    }   

    public long getNanoSecondsDeviation() {
        return nanoSecondsError;
    }   

    public long currentNanoSecondsTimestamp() {
       // return System.nanoTime() + nanoSecondsOffset;
        return System.nanoTime() ;
    }  
    
    static public String getCurrentTimeAsString(){
      DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
      formatter.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
      return formatter.format(new Date());
    }
}