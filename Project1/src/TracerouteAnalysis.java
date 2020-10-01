import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class TracerouteAnalysis {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in); 
		System.out.println("Hello! What filename would you like to analyze the connection?");
		String filePath = null;
		boolean first = false; //so code does not run first time
		int[] hist = new int[30]; //create histogram arraylist
		for (int i = 0; i < 30; i++) { 
			hist[i] = 0; //fill histogram arraylist
		}
		ArrayList<Traceroute> traceroutes = new ArrayList<>(); //Create traceroute object storage
		File f;
		do {
		if (first) {
			System.out.println("Invalid filename. Please try again. Enter a filename:");
		}
		filePath = sc.nextLine(); //get filename from user
		f = new File(filePath); //create file object
		first = true; //for second+ time through do/while loop
		} while(!f.canRead()); //continue until a valid filename is entered
		sc.close();
		try {
			Scanner scanner = new Scanner(f); //Scanner to read file
			while (scanner.hasNext()) { //Make sure end of file is not reached
				String s = scanner.nextLine(); //Check next line
				if (s.split(" ")[0].equals("traceroute")) {  //traceroute indicates new traceroute
					if (traceroutes.size() > 0) {
						traceroutes.get(traceroutes.size()-1).checkEndFound();
					}
					traceroutes.add(new Traceroute(s)); //create new traceroute object
				} else {
					traceroutes.get(traceroutes.size()-1).addLine(s); //if not new one add line to traceroute
				}
			}
			scanner.close(); //close scanner
		} catch (Exception e) {
			System.out.println("File error."); //exception handling
			e.printStackTrace();
		}
		float sumDelay = 0; //for adding up the total delays
		float totHops = 0; //for adding up the total hops of reachable websites
		int count = 0; //counter for division
		int linkCounter = 0; //counter for adding each valid hop
		float sumLinkDelay = 0; //counter for adding each valid link delay
		for (int j = 0; j < traceroutes.size(); j++) {
			linkCounter += traceroutes.get(j).linkDelays.size();
			for (int k = 0; k < traceroutes.get(j).linkDelays.size(); k++) {
				sumLinkDelay += traceroutes.get(j).linkDelays.get(k);
			}
			
			if (traceroutes.get(j).reachable()) { //check if the current traceroute was reachable
				count++; //add one to counter
				sumDelay += traceroutes.get(j).getAverageDelay(); //add average delay to sum
				int hop = traceroutes.get(j).hops(); //get hops
				totHops += hop; //add hops
				//hist[hop+1]++; //for histogram
			}
		}
		
		float aveDelay = sumDelay/count; //calculate average delay
		float aveHops = totHops/count; //calculate average hops
		float aveLinkDelay = sumLinkDelay / linkCounter; //calculate average link delay
		try {
			File avgOutputFile = new File("avg.txt"); //create file object
			avgOutputFile.delete(); //delete any existing file under this name
			avgOutputFile.createNewFile(); //create new file under this name
			FileWriter fw = new FileWriter("avg.txt"); //create filewriter 
			fw.flush(); //flush filewriter
			//write to file
			fw.write(String.format("Average number of hops:\t%.14f hops\n", aveHops));
			fw.write(String.format("Average delay:\t%.14f ms\n", aveDelay));
			fw.write(String.format("Average link delay:\t%.14f ms\n", aveLinkDelay));
			fw.flush(); //flush filewriter
			fw.close(); //close fw
			File histOutputFile = new File("hist.txt"); //create file object
			histOutputFile.delete(); //delete any existing file under this name
			histOutputFile.createNewFile(); //create new file under this name
			FileWriter fw2 = new FileWriter("hist.txt"); //create fw
			fw2.flush(); //flush fw
			for (int i = 0; i < 30; i++) {
				fw2.write(String.format("%d:\t%d\n", i+1, hist[i])); //write histogram
			}
			fw2.flush();
			fw2.close();
		} catch (Exception e) {
			System.out.println("File error.");
		}
	}

}

class Traceroute {

	ArrayList<String> lines;
	boolean foundEnd;
	private String ip;
	private float averageDelay;
	public ArrayList<Float> delays;
	public ArrayList<Float> linkDelays;
	private float mostRLDelay;
	private int newValidHop;

	public Traceroute(String firstLine) { //constructor
		this.ip = findIp(firstLine); //find ip
		this.lines = new ArrayList<>(); //create new arrayList object for holding lines
		this.linkDelays = new ArrayList<>();
		foundEnd = false; //indicator whether this traceroute found the destination
		this.delays = new ArrayList<>(); //new arraylist for delays
	}

	private String findIp(String firstLine) {
		String[] sections = firstLine.split("\\(|\\)"); //regex to find ip
		return sections[1]; //ip will be between the parentheses
	}

	public boolean reachable() {
		return foundEnd; //getter
	}

	public int hops() {
		if (reachable()) { //checks if reachable
			return lines.size(); //if reachable the number of hops is just the number of lines
		} else {
			return 0; //if not reachable, return 0;
		}
	}

	public void addLine(String s) {
		this.lines.add(s);
		String[] delaySplit = s.split(" "); //split line into sections for analysis for delays and hop numbers
		int count = 0;
		float delaySum = 0;
		for (int i = 1; i < delaySplit.length; i++) {
			if (delaySplit[i].equals("ms")) {
				count++;
				delaySum+= Float.parseFloat(delaySplit[i-1]);
			}
		}
		int hop;
		if (lines.size() < 10) { //hop numbers greater than 10 will end up in array index 0, otherwise 1
			hop = Integer.parseInt(delaySplit[1]);
		} else {
			hop = Integer.parseInt(delaySplit[0]);
		}
		if (linkDelays.size() > 0 && delaySum > 0 && hop-this.newValidHop == 1 && (delaySum/count) > this.mostRLDelay) { //checks if a hop is adjacent to the last recorded hop as well as making sure it is not a negative delay.
			this.linkDelays.add((delaySum/count)-this.mostRLDelay); //calculates the delay between the two hops and adds it for storage
			this.mostRLDelay = delaySum / count; //stores most recent reported delay from the traceroute
			this.newValidHop = hop; //records most recent valid hop for adjacency purposes
		} else if (delaySum > 0 && linkDelays.size() == 0) { //storage of the first hop
			this.linkDelays.add((delaySum/count));
			this.newValidHop = hop;
			this.mostRLDelay = delaySum / count;
		} else if (delaySum > 0) { //for storing most recent valid hop in case reachable websites pop up immediately after
			this.newValidHop = hop;
			this.mostRLDelay = delaySum / count;
		} 
		
	}
	
	public void checkEndFound() {
		String s = this.lines.get(this.lines.size()-1);
		String[] sections = s.split("\\(|\\)"); //regex to find ips in line
		for (int i = 1; i < sections.length; i = i + 2) { //ips will be between parentheses
			if (sections[i].equals(this.ip)) { //checks if ips are equal
				this.foundEnd = true;
				calcAverageDelay(); //calculate average delay
			}
		}
	}

	public void calcAverageDelay() {
		String lastLine = this.lines.get(this.lines.size() - 1); //find last line of the lines entered
		ArrayList<Float> delays = new ArrayList<>(); //create arraylist for storage
		String[] split = lastLine.split(" "); //split last line by spaces
		for (int i = 1; i < split.length; i++) {
			if (split[i].equals("ms")) { //ms will always succeed the delay, so use that to determine where to look 
				delays.add(Float.parseFloat(split[i - 1])); //parse string as float
				this.delays.add(Float.parseFloat(split[i - 1])); //parse string as float
			}
		}
		int i;
		float sum = 0;
		for (i = 0; i < delays.size(); i++) {
			sum += delays.get(i); //add up delays
		}
		this.averageDelay = sum / i; //divide them
	}

	public float getAverageDelay() {
		return this.averageDelay; //getter
	}
}
