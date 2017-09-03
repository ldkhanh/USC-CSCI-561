package aiHW03;

public class FOLResolutionInfer {
	public static void main(String[] args) {
		Resolution();
	}
	
	private static void Resolution() {
		
		Domain domain = new Domain();
		domain.setConstant("Liz");
		domain.setConstant("Billy");
		domain.setConstant("Bob");
		domain.setConstant("Charley");
		domain.setPredicate("Ancestor");
		domain.setPredicate("Father");
		domain.setPredicate("Mother");
		domain.setPredicate("Parent");
		
		KB kb = new KB(domain);
		kb.tell("Mother(Liz,Charley)");			// (A(x) => H(x))
		kb.tell("Father(Charley,Billy)");
		kb.tell("~Mother(x,y) | Parent(x,y)");
		kb.tell("(~Father(x,y)) | Parent(x,y)");
		kb.tell("(~Parent(x,y)) | Ancestor(x,y)");
		kb.tell("(~(Parent(x,y)) & Ancestor(y,z)) | Ancestor(x,z) ");
		
		String kbStr = kb.toString();
		ResolutionResult answer = kb.ask("Ancestor(Liz,Bob)");
				
		System.out.println("Knowledge Base:");
		System.out.println(kbStr);
		if (answer.isTrue()) {
			System.out.println("This query is True");
		} else {
			System.out.println("This query is False");
		}
	}

}
