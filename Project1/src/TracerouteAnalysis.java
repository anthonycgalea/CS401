import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class TracerouteAnalysis {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
	}

}

class Traceroute {

	ArrayList<String> lines;
	boolean foundEnd;
	private String ip;
	private double averageDelay;

	public Traceroute(String firstLine) {
		ip = findIp(firstLine);
		this.lines = new ArrayList<>();
		foundEnd = false;
	}

	private String findIp(String firstLine) {
		String[] sections = firstLine.split("(|)");
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
		String[] sections = s.split("(|)");
		for (int i = 1; i < sections.length; i = i + 2) {
			if (sections[i].equals(this.ip)) {
				this.foundEnd = true;
				System.out.println("Traceroute Complete!");
				calcAverageDelay();
				System.out.println("Average delay:\t" + this.averageDelay);
			}
		}
	}

	public void calcAverageDelay() {
		String lastLine = this.lines.get(this.lines.size() - 1);
		ArrayList<Double> delays = new ArrayList<>();
		String[] split = lastLine.split(" ");
		for (int i = 1; i < split.length; i++) {
			if (split[i].equals("ms")) {
				delays.add(Double.parseDouble(split[i - 1]));
			}
		}
		int i, sum = 0;
		for (i = 0; i < delays.size(); i++) {
			sum += delays.get(i);
		}
		this.averageDelay = sum / i;
	}

	public double getAverageDelay() {
		return this.averageDelay;
	}
}
