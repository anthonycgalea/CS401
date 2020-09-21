import java.io.File;
import java.util.Scanner;

public class TracerouteAnalysis {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		System.out.println("Hello! What filename would you like to analyze the connection?");
		String filePath = null;
		boolean first = false;
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
	}

}
