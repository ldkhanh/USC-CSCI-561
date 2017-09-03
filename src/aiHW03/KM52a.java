package aiHW03;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class KM52a {
	private  List<String> querys;
	private  List<String> clauses;
	private  List<String> allSt;
	
	public KM52a(){
		querys = new ArrayList<String>();
		clauses = new ArrayList<String>();
		allSt = new ArrayList<String>();
		
	}
	
	public  void Resolution() {
		StringBuilder sb = new StringBuilder();
		Domain domain = new Domain();
		Lexer lexer = new Lexer();
		for (int i = 0; i < allSt.size(); i++) {
			System.out.println(allSt.get(i));
			lexer.setIn(allSt.get(i));
			Token t = lexer.extractDomain();
			while (!t.getText().equals("EOI")) {
				t = lexer.extractDomain();
				if (t.getType() == 8) {
					System.out.println("Constant: " + t.getText());
					domain.setConstant(t.getText());
				} else if (t.getType() == 6) {
					System.out.println("PREDICATE: " + t.getText());
					domain.setPredicate(t.getText());
				} 
			}
		}

		KB kb = new KB(domain);
		for (String c : clauses) {
			kb.tell(c);
		}
		System.out.println("Knowledge Base:");
		System.out.println(kb.toString());
		for (int i = 0; i < querys.size(); i++) {
			ResolutionResult answer = kb.ask(querys.get(i));
			System.out.print(querys.get(i));
			if (answer.isTrue()) {
				System.out.println("   This query is True");
				if (i != querys.size()-1)
					sb.append("TRUE"+"\n");
				else 
					sb.append("TRUE");
			} else {
				System.out.println("   This query is False");
				if (i != querys.size()-1)
					sb.append("FALSE"+"\n");
				else 
					sb.append("FALSE");
			}
		}
		output(sb.toString());
		
	}

	public void input(String fileName) {
		BufferedReader br = null;
		try {
			int nQuery, nClause;
			String line;
			br = new BufferedReader(new FileReader(fileName));
			// Get Query
			if ((line = br.readLine()) != null)
				line = line.replaceAll("\\s", "");
			else {
				System.out.println("Invalid input");
				return;
			}
			if (line.matches("[0-9]+") && line.length() > 0)
				nQuery = Integer.parseInt(line);
			else
				return;
			for (int i = 0; i < nQuery; i++) {
				line = br.readLine();
				querys.add(line);
				if (line.charAt(0) != '(')
					allSt.add("("+line+")");
				else 
					allSt.add(line);
			}
			// Get KB
			if ((line = br.readLine()) != null)
				line = line.replaceAll("\\s", "");
			else {
				System.out.println("Invalid input");
				return;
			}
			if (line.matches("[0-9]+") && line.length() > 0)
				nClause = Integer.parseInt(line);
			else
				return;

			for (int i = 0; i < nClause; i++) {
				line = br.readLine();
				clauses.add(line);
				if (line.charAt(0) != '(')
					allSt.add("("+line+")");
				else 
					allSt.add(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void output(String out) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt"), "utf-8"))) {
			writer.write(out);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
