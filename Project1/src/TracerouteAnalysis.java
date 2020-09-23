import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class TracerouteAnalysis {

	public static void main(String[] args) {
		// DONE Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		System.out.println("Hello! What filename would you like to analyze the connection?");
		String filePath = null;
		boolean first = false;
		ArrayList<Traceroute> traceroutes = new ArrayList<>();
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
			Scanner scanner = new Scanner(f);
			while (scanner.hasNext()) {
				String s = scanner.nextLine();
				if (s.split(" ")[0].equals("traceroute")) {
					if (traceroutes.size() != 0) {
						if (!traceroutes.get(traceroutes.size()-1).reachable()) {
							System.out.println("Fail."); 
						}
					}
					traceroutes.add(new Traceroute(s));
				} else {
					traceroutes.get(traceroutes.size()-1).addLine(s);
				}
			}
			scanner.close();
		} catch (Exception e) {
			System.out.println("File error.");
		}
		float sumDelay = 0;
		float totHops = 0;
		int count = 0;
		for (int j = 0; j < traceroutes.size(); j++) {
			if (traceroutes.get(j).reachable()) {
				count++;
				sumDelay += traceroutes.get(j).getAverageDelay();
				totHops += traceroutes.get(j).hops();
			}
		}
		float aveDelay = sumDelay/count;
		float aveHops = totHops/count;
		float aveLinkDelay = 0;
		System.out.printf("Average number of hops:\t%.14f hops\n", aveHops);
		System.out.printf("Average delay:\t%.14f ms\n", aveDelay);
		System.out.printf("Average link delay:\t%.14f ms\n", aveLinkDelay);
		try {
			File avgOutputFile = new File("avg.txt");
			avgOutputFile.delete();
			avgOutputFile.createNewFile();
			FileWriter fw = new FileWriter("avg.txt");
			fw.flush();
			fw.write(String.format("Average number of hops:\t%.14f hops\n", aveHops));
			fw.write(String.format("Average delay:\t%.14f ms\n", aveDelay));
			fw.write(String.format("Average link delay:\t%.14f ms\n", aveLinkDelay));
			fw.flush();
			fw.close();
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

	public Traceroute(String firstLine) {
		ip = findIp(firstLine);
		this.lines = new ArrayList<>();
		foundEnd = false;
		this.delays = new ArrayList<>();
	}

	private String findIp(String firstLine) {
		String[] sections = firstLine.split("\\(|\\)");
		return sections[1];
	}

	public boolean reachable() {
		return foundEnd;
	}

	public int hops() {
		if (reachable()) {
			return lines.size();
		} else {
			return 0;
		}
	}

	public void addLine(String s) {
		this.lines.add(s);
		String[] sections = s.split("\\(|\\)");
		for (int i = 1; i < sections.length; i = i + 2) {
			if (sections[i].equals(this.ip)) {
				this.foundEnd = true;
				System.out.println("Traceroute Complete!");
				calcAverageDelay();
				System.out.printf("Average delay:\t%.3f\n", this.averageDelay);
			}
		}
	}

	public void calcAverageDelay() {
		String lastLine = this.lines.get(this.lines.size() - 1);
		ArrayList<Float> delays = new ArrayList<>();
		String[] split = lastLine.split(" ");
		for (int i = 1; i < split.length; i++) {
			if (split[i].equals("ms")) {
				delays.add(Float.parseFloat(split[i - 1]));
				this.delays.add(Float.parseFloat(split[i - 1]));
			}
		}
		int i;
		float sum = 0;
		for (i = 0; i < delays.size(); i++) {
			sum += delays.get(i);
		}
		this.averageDelay = sum / i;
	}

	public float getAverageDelay() {
		return this.averageDelay;
	}
}
