package aiHW02;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class TestCases {
	
	public static void RunTest() {
		
		try {
			List<File> file = Files.walk(Paths.get("/Users/khanh/Documents/workspace/CSCI561AIK/TestCases"))
			        .filter(Files::isRegularFile)
			        .map(Path::toFile)
			        .collect(Collectors.toList());
			String temp = "";
			for(int i = 0; i < file.size()-1; i++ ){
				temp = file.get(i).getName();
				if (temp.endsWith(".txt"))
					if (temp.equals("input.txt")) {
						System.out.println(file.get(i).toString() + " Index : " +i);
						System.out.println(file.get(i+1).toString() + " Index : " +(i+1));
						homework hw = new homework();
						hw.Run(file.get(i).toString());
						if (compareOutput(file.get(i+1).toString())){
							System.out.println("Test Case Passed");
						} else {
							System.out.println("Test Not Passed");
						}
					}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		RunTest();
		
	}
	
	public static boolean compareOutput(String testOuput){
		try {
			String gameOutput = readStream(new FileInputStream("output.txt"));
			String testcaseOutput = readStream(new FileInputStream(testOuput));
			if (gameOutput.equals(testcaseOutput))
				return true;
			else 
				return false;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		}
	}
	
	public static String readStream(InputStream is) {
	    StringBuilder sb = new StringBuilder(512);
	    try {
	        Reader r = new InputStreamReader(is, "utf-8");
	        int c = 0;
	        while ((c = r.read()) != -1) {
	            sb.append((char) c);
	        }
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	    return sb.toString();
	}

}
