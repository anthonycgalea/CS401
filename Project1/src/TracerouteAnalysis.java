import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class TracerouteAnalysis {

	public static void main(String[] args) {
		// TODO: Change scanner to command line arguments
		Scanner sc = new Scanner(System.in); 
		System.out.println("Hello! What filename would you like to analyze the connection?");
		String filePath = null;
		boolean first = false;
		int[] hist = new int[30];
		for (int i = 0; i < 30; i++) {
			hist[i] = 0;
		}
		ArrayList<Traceroute> traceroutes = new ArrayList<>(); //Create traceroute object storage
		File f;
		do {
		if (first) {
			System.out.println("Invalid filename. Please try again. Enter a filename:");
		}
		filePath = sc.nextLine();
		f = new File(filePath);
		first = true;
		} while(!f.canRead());
		sc.close();
		System.out.println("Valid filename.");
		try {
			Scanner scanner = new Scanner(f); //Scanner to read file
			while (scanner.hasNext()) { //Make sure end of file is not reached
				String s = scanner.nextLine(); //Check next line
				if (s.split(" ")[0].equals("traceroute")) {  //traceroute indicates new traceroute
					traceroutes.add(new Traceroute(s)); //create new traceroute object
				} else {
					traceroutes.get(traceroutes.size()-1).addLine(s); //if not new one add line to traceroute
				}
			}
			scanner.close(); //close scanner
		} catch (Exception e) {
			System.out.println("File error."); //exception handling
		}
		float sumDelay = 0; //for adding up the total delays
		float totHops = 0; //for adding up the total hops of reachable websites
		int count = 0; //counter for division
		for (int j = 0; j < traceroutes.size(); j++) {
			if (traceroutes.get(j).reachable()) { //check if the current traceroute was reachable
				count++; //add one to counter
				sumDelay += traceroutes.get(j).getAverageDelay(); //add average delay to sum
				int hop = traceroutes.get(j).hops(); //get hops
				totHops += hop; //add hops
				hist[hop+1]++; //for histogram
			}
		}
		float aveDelay = sumDelay/count; //calculate average delay
		float aveHops = totHops/count; //calculate average hops
		float aveLinkDelay = 0; //calculate average link delay
		System.out.printf("Average link delay:\t%.14f ms\n", aveLinkDelay);
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

	public Traceroute(String firstLine) { //constructor
		this.ip = findIp(firstLine); //find ip
		this.lines = new ArrayList<>(); //create new arrayList object for holding lines
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
