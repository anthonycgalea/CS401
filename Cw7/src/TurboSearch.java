import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class TurboSearch {
    public static void main(String[] args){
    	ArrayList<Thread> threads = new ArrayList<>();
    	ArrayList<FileRead> frs = new ArrayList<>();
        int tally=0; //stores the total number of lines across all files
          if (args.length < 2){
             System.out.println("Usage: java TurboSearch keyword sourcefile1 sourcefile2 . . .");
             return;
       }
       String keyword = args[0];
       for (int i = 1; i < args.length; i++){
          String filename = args[i]; //this will contain each filename
          // your code goes here
          //any additional classes that you add
          //should be added to the same file.
          FileRead fr = new FileRead(filename,keyword);
          Thread t = new Thread(fr);
          threads.add(t);
          frs.add(fr);
          t.start(); 
       }
       while(true) {
    	   int count = 0;
    	   for (int i = 0; i < threads.size(); i++) {
    		   if (frs.get(i).isDone()) {
    			   count++;
    		   }
    	   }
    	   if (count == threads.size()-1) {
    		   break;
    	   }
       }
       for (int i = 0; i < frs.size(); i++) {
    	   tally+=frs.get(i).getNumLines();
       }
       System.out.println("--------------------------------------------");
       if (tally == 0) {
    	   System.out.println("No results found.");
       } else {
    	   System.out.println("Found " + tally + " results.");
       }
    }
}
class FileRead implements Runnable {
	private String fileName;
	private String word;
	private int numLines;
	private boolean isDone;
    public FileRead(String fileName, String word) {
    	this.fileName = fileName;
    	this.word = word.toLowerCase();
    	this.numLines = 0;
    	this.isDone = false;
    }
    
    public int getNumLines() {
    	return this.numLines;
    }
    
    public boolean isDone() {
    	return this.isDone;
    }
    	
    public void run() {
    	try {
    		File f = new File(fileName);
    		if (f.canRead()) {
    			@SuppressWarnings("resource")
				Scanner sc = new Scanner(f);
    			while(sc.hasNext()) {
    				String s = sc.nextLine();
    				String sLower = s.toLowerCase();
    				if (sLower.contains(this.word)) {
    					this.numLines++;
    					if (s.length() > 40) {
    						int index = sLower.indexOf(this.word);
    						if (index <=70-this.word.length()) {
    							s = s.substring(0,70) + " [..output truncated]";
    						} else {
    							s = s.substring(index-35,index+35) + " [..output truncated]";
    						}
    					}
    					System.out.println(fileName + " | " + s);
    				}
    			}
    			
    		} else {
    			throw new Exception("Invalid file name:" + fileName);
    		}
    	} catch(Exception e) {
    		System.out.println("Invalid file name:" + fileName);
    	}
    	this.isDone = true;
    }
    
}