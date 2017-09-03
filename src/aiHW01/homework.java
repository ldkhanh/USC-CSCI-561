package aiHW01;

import java.util.*;
import java.io.*;

class homework {

	public static void main(String[] args) {

		Graph g = new Graph();
		g.buildData();
		Algorithm search = new Algorithm();
		String algo = g.getAlgo();
		switch (algo) {
		case "BFS":
			search.BFS(g);
			break;
		case "DFS":
			search.DFS(g);
			break;
		case "UCS":
			search.UCS(g);
			break;
		case "A*":
			search.Astar(g);
			break;
		default:
			System.out.println("Input file is not compatible with given format");
			break;
		}
	}

}

class Algorithm {

	private Queue<Node> frontierB;
	private Deque<Node> frontierD;
	private Queue<Node> frontierU;
	private Queue<Node> exploredSet;

	public Algorithm() {
		this.frontierB = new LinkedList<Node>();
		this.frontierD = new LinkedList<Node>();
		this.exploredSet = new LinkedList<Node>();

	}

	public void BFS(Graph problem) {

		frontierB.offer(problem.getStartS());
		Node node, new_node;
		ArrayList<Node> childNode = new ArrayList<Node>();

		while (true) {
			if (frontierB.isEmpty())
				return;

			node = this.frontierB.poll();

			if (problem.getGoalS().getState().equals(node.getState())) {
				solutionOutput(node, problem);
				return;
			}
			this.exploredSet.add(node);
			childNode = expandNode(node, problem);

			for (int i = 0; i < childNode.size(); i++) {
				new_node = childNode.get(i);
				if (!frontierB.contains(new_node) && !exploredSet.contains(new_node)) {
					insertBFS(node, new_node, problem);
				}
			}
		}

	}

	public void DFS(Graph problem) {

		frontierD.push(problem.getStartS());
		Node node, new_node;
		ArrayList<Node> childNode = new ArrayList<Node>();

		while (true) {
			if (frontierD.isEmpty())
				return;

			node = frontierD.pop();
			if (problem.getGoalS().getState().equals(node.getState())) {
				solutionOutput(node, problem);
				return;
			}
			this.exploredSet.add(node);
			childNode = expandNode(node, problem);
			Collections.reverse(childNode);

			for (int i = 0; i < childNode.size(); i++) {
				new_node = childNode.get(i);
				if (!frontierD.contains(new_node) && !exploredSet.contains(new_node)) {
					insertDFS(node, new_node, problem);
				}
			}
		}
	}

	public void UCS(Graph problem) {
		this.frontierU = new PriorityQueue<Node>(problem.getNumNodes(), compUCS);
		frontierU.offer(problem.getStartS());
		Node node = problem.getStartS();
		Node new_node = null, temp = null;
		ArrayList<Node> childNode = new ArrayList<Node>();

		while (true) {
			if (frontierU.isEmpty())
				return;
			node = this.frontierU.poll();
			if (problem.getGoalS().getState().equals(node.getState())) {
				solutionOutput(node, problem);
				return;
			}
//			this.exploredSet.add(node);
			childNode = expandNode(node, problem);

			for (int i = 0; i < childNode.size(); i++) {
				new_node = childNode.get(i);
				if (!frontierU.contains(new_node) && !exploredSet.contains(new_node)) {
					insertNodeU(node, new_node, problem);
				} else if (frontierU.contains(new_node)) {
					temp = findNode(frontierU, new_node);
					if (temp.getGCost() > new_node.getGCost()) {
						frontierU.remove(temp);
						updateNodeU(node, new_node, problem);
					}
				} else if (exploredSet.contains(new_node)) {
					temp = findNode(exploredSet, new_node);
					if (temp.getGCost() > new_node.getGCost()) {
						exploredSet.remove(temp);
						updateNodeU(node, new_node, problem);
					}
				}
			}
			this.exploredSet.add(node);		//Khanh modify on exam review

			SortedSet<Node> sorter = new TreeSet<Node>(new Comparator<Node>() {
				@Override
				public int compare(Node a, Node b) {
					if (a.getGCost() == b.getGCost()) {
						return (a.getPriority() - b.getPriority());
					}
					return ((a.getGCost()) - (b.getGCost()));
				}
			});

			sorter.addAll(frontierU);
			frontierU.removeAll(sorter);
			frontierU.addAll(sorter);
		}
	}

	public void Astar(Graph problem) {
		this.frontierU = new PriorityQueue<Node>(problem.getNumNodes(), compA2);
		frontierU.offer(problem.getStartS());
		Node node = problem.getStartS();
		Node new_node, temp;
		ArrayList<Node> childNode = new ArrayList<Node>();

		while (true) {
			if (frontierU.isEmpty())
				return;

			node = this.frontierU.poll();

			if (problem.getGoalS().getState().equals(node.getState())) {
				solutionOutput(node, problem);
				return;
			}
			this.exploredSet.add(node);
			childNode = expandNode(node, problem);

			for (int i = 0; i < childNode.size(); i++) {
				new_node = childNode.get(i);
				if (!frontierU.contains(new_node) && !exploredSet.contains(new_node)) {
					insertNodeU(node, new_node, problem);
				} else if (frontierU.contains(new_node)) {
					temp = findNode(frontierU, new_node);
					if ((temp.getGCost() + temp.getHCost()) > (new_node.getGCost() + new_node.getHCost())) {
						frontierU.remove(temp);
						updateNodeU(node, new_node, problem);
					}
				} else if (exploredSet.contains(new_node)) {
					temp = findNode(exploredSet, new_node);
					if ((temp.getGCost() + temp.getHCost()) > (new_node.getGCost() + new_node.getHCost())) {
						exploredSet.remove(temp);
						updateNodeU(node, new_node, problem);
					}
				}

			}

			SortedSet<Node> sorter = new TreeSet<Node>(new Comparator<Node>() {
				@Override

				public int compare(Node a, Node b) {
					if ((a.getGCost() + a.getHCost()) == (b.getGCost() + b.getHCost())) {
						return (a.getPriority() - b.getPriority());
					}
					return ((a.getGCost() + a.getHCost()) - (b.getGCost() + b.getHCost()));
				}
			});

			sorter.addAll(frontierU);
			frontierU.clear();
			frontierU.addAll(sorter);
		}
	}

	private void insertBFS(Node node, Node new_node, Graph g) {

		Node temp = new Node(new_node.getId(), new_node.getPriority(), new_node.getState(), new_node.getGCost(),
				new_node.getHCost());
		temp.setParent(node);
		temp.setDepth(node.getDepth() + 1);

		frontierB.offer(temp);
		Node n = g.getListNodes().get(new_node.getId());
		n.setParent(node);
		n.setDepth(node.getDepth() + 1);
	}

	private void insertDFS(Node node, Node new_node, Graph g) {

		Node temp = new Node(new_node.getId(), new_node.getPriority(), new_node.getState(), new_node.getGCost(),
				new_node.getHCost());
		temp.setParent(node);
		temp.setDepth(node.getDepth() + 1);

		frontierD.push(temp);
		Node n = g.getListNodes().get(new_node.getId());
		n.setParent(node);
		n.setDepth(node.getDepth() + 1);
	}

	private void insertNodeU(Node node, Node new_node, Graph g) {

		Node temp = new Node(new_node.getId(), new_node.getPriority(), new_node.getState(), new_node.getGCost(),
				new_node.getHCost());
		temp.setParent(node);
		temp.setDepth(node.getDepth() + 1);
		temp.setGCost(node.getGCost() + g.getTTime(node.getId(), new_node.getId()));

		frontierU.offer(temp);

		Node n = g.getListNodes().get(new_node.getId());
		n.setParent(node);
		n.setDepth(node.getDepth() + 1);
		n.setGCost(node.getGCost() + g.getTTime(node.getId(), new_node.getId()));
	}

	private void updateNodeU(Node node, Node new_node, Graph g) {

		Node temp = new Node(new_node.getId(), g.getNumNodes(), new_node.getState(), new_node.getGCost(),
				new_node.getHCost());
		temp.setParent(node);
		temp.setDepth(node.getDepth() + 1);
		temp.setGCost(node.getGCost() + g.getTTime(node.getId(), new_node.getId()));

		frontierU.offer(temp);

		Node n = g.getListNodes().get(new_node.getId());
		n.setParent(node);
		n.setDepth(node.getDepth() + 1);
		n.setGCost(node.getGCost() + g.getTTime(node.getId(), new_node.getId()));

	}

	private ArrayList<Node> expandNode(Node node, Graph g) {
		ArrayList<Node> sucessors = new ArrayList<Node>();
		Node s;
		int adj[][] = g.getAdjG();
		ArrayList<Node> listNodes = g.getListNodes();

		int k = node.getId();

		for (int i = 0; i < g.getNumNodes(); i++) {
			if (adj[k][i] >= 0) {
				s = listNodes.get(i);
				Node n = new Node(s.getId(), s.getPriority(), s.getState(), node.getGCost() + adj[k][i], s.getHCost());
				sucessors.add(n);
			}
		}
		return sucessors;
	}

	private void solutionOutput(Node node, Graph g) {

		ArrayList<Node> list = new ArrayList<Node>();

		Node pNode = g.getGoalS();
		while (pNode != null) {
			list.add(pNode);
			pNode = pNode.getParent();
		}

		Collections.reverse(list);

		System.out.println("Output: " + g.getAlgo() + ":");
		if (g.getAlgo().equals("BFS") || g.getAlgo().equals("DFS")) {

			for (int i = 0; i < list.size(); i++)
				System.out.println(list.get(i).getState() + " " + list.get(i).getDepth());
			output1(list);
		} else if (g.getAlgo().equals("UCS")) {
			for (int i = 0; i < list.size(); i++)
				System.out.println(list.get(i).getState() + " " + list.get(i).getGCost());
			output2(list);
		} else if (g.getAlgo().equals("A*")) {
			for (int i = 0; i < list.size(); i++)
				System.out.println(list.get(i).getState() + " " + list.get(i).getGCost());
			output2(list);
		}
	}

	private void output1(ArrayList<Node> list) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt"), "utf-8"))) {
			for (int i = 0; i < list.size(); i++) {
				writer.write(list.get(i).getState() + " " + list.get(i).getDepth() + "\n");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void output2(ArrayList<Node> list) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt"), "utf-8"))) {
			for (int i = 0; i < list.size(); i++) {
				writer.write(list.get(i).getState() + " " + list.get(i).getGCost() + "\n");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private Node findNode(Queue<Node> list, Node new_node) {
		Iterator<Node> cTemp = list.iterator();
		Node temp = null;
		while (cTemp.hasNext()) {
			temp = cTemp.next();
			if (temp.getState().equals(new_node.getState()) && (temp.getId() == new_node.getId())) {
				return temp;
			}
		}
		return null;
	}

	private static Comparator<Node> compA2 = new Comparator<Node>() {

		@Override
		public int compare(Node c1, Node c2) {
			if ((c1.getGCost() + c1.getHCost()) == (c2.getGCost() + c2.getHCost()))
				return (c1.getPriority() - c2.getPriority());
			return (int) ((c1.getGCost() + c1.getHCost()) - (c2.getGCost() + c2.getHCost()));
		}
	};

	private static Comparator<Node> compUCS = new Comparator<Node>() {

		@Override
		public int compare(Node c1, Node c2) {
			if (c1.getGCost() == c2.getGCost())
				return (c1.getPriority() - c2.getPriority());
			return (int) (c1.getGCost() - c2.getGCost());
		}
	};
}

class Graph {
	private ArrayList<Node> listNodes;
	private int[][] adjG;
	private int numNodes;
	private String Algo;
	private Node startS;
	private Node goalS;

	public Graph() {
		this.setListNodes(new ArrayList<Node>());
		this.setNumNodes(0);
		this.setGoalS(null);
		this.setGoalS(null);
	}

	public void buildData() {
		String numLT = null, numST = null, startState, goalState;
		int numLiveTraffic = 0;
		int numSundayTraffic = 0;
		BufferedReader br = null;
		String[] tokens;
		Map<String, Integer> nodesMap = new LinkedHashMap<String, Integer>();
		ArrayList<LiveTraffic> lStates = new ArrayList<LiveTraffic>();

		try {
			br = new BufferedReader(new FileReader("input.txt"));

			this.setAlgo(br.readLine().replaceAll("\\s", ""));
			startState = br.readLine().replaceAll("\\s", "");
			goalState = br.readLine().replaceAll("\\s", "");

			if ((numLT = br.readLine()) != null)
				numLT = numLT.replaceAll("\\s", "");
			else {
				System.out.println("Check input File");
				return;
			}
			if (numLT.matches("[0-9]+") && numLT.length() > 0)
				numLiveTraffic = Integer.parseInt(numLT);

			for (int i = 0; i < numLiveTraffic; i++) {
				lStates.add(new LiveTraffic(br.readLine()));
			}

			// ---------Prioritize Node -----
			Queue<String> qNodes = new LinkedList<String>();
			Set<String> visitedN = new LinkedHashSet<String>();
			String currS;
			String firstS;
			String secondS;
			if (numLiveTraffic > 0) {
				qNodes.offer(startState);
				while (!qNodes.isEmpty()) {
					currS = qNodes.poll();
					visitedN.add(currS);
					for (int i = 0; i < lStates.size(); i++) {
						firstS = lStates.get(i).getFirstS();
						if (firstS.equals(currS)) {
							secondS = lStates.get(i).getSecondS();
							if (!visitedN.contains(secondS) && !qNodes.contains(secondS)) {
								qNodes.offer(secondS);
							}
						}
					}
				}
			}
			
			//----Checked extra node
			for (int i = 0; i < lStates.size(); i++)
			{
				if (!visitedN.contains(lStates.get(i).getFirstS())){
					visitedN.add(lStates.get(i).getFirstS());
					System.out.println("Extra 1: "+lStates.get(i).getFirstS());
				}
				
				if (!visitedN.contains(lStates.get(i).getSecondS())){
					visitedN.add(lStates.get(i).getSecondS());
					System.out.println("Extra 2: "+lStates.get(i).getSecondS());
				}
			}
			//----end checked extra node
			
			this.setNumNodes(visitedN.size());
			// -------------------------------

			int j = 0;
			String temp;
			Iterator<String> it = visitedN.iterator();
			while (it.hasNext()) {
				temp = (String) it.next();
				nodesMap.put(temp, j++);
				listNodes.add(new Node(j - 1, j - 1, temp));
			}

			this.setStartS(listNodes.get((int) nodesMap.get(startState)));
			this.setGoalS(listNodes.get((int) nodesMap.get(goalState)));
			// ----Initialize edges -----
			adjG = new int[this.getNumNodes()][this.getNumNodes()];
			for (int i = 0; i < this.getNumNodes(); i++)
				for (int k = 0; k < this.getNumNodes(); k++)
					adjG[i][k] = -1;
			// ------
			for (int i = 0; i < numLiveTraffic; i++) {
				adjG[(int) nodesMap.get(lStates.get(i).getFirstS())][(int) nodesMap
						.get(lStates.get(i).getSecondS())] = lStates.get(i).getTravelTime();
			}

			// ----------Sunday Traffic Lines------------
			if ((numST = br.readLine()) != null)
				numST = numST.replaceAll("\\s", "");
			if (numST != null && numST.matches("[0-9]+") && numST.length() > 0)
				numSundayTraffic = Integer.parseInt(numST);

			String sLine;
			for (int i = 0; i < numSundayTraffic; i++) {
				sLine = br.readLine();
				tokens = sLine.split(" ");
				for (int k = 0; k < listNodes.size(); k++)
					if (listNodes.get(k).getState().equals(tokens[0].replaceAll("\\s", "")))
						listNodes.get(k).setHCost(Integer.parseInt(tokens[1].replaceAll("\\s", "")));
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

	public ArrayList<Node> getListNodes() {
		return listNodes;
	}

	public void setListNodes(ArrayList<Node> listNodes) {
		this.listNodes = listNodes;
	}

	public String getAlgo() {
		return Algo;
	}

	public void setAlgo(String algo) {
		Algo = algo;
	}

	public int getNumNodes() {
		return numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

	public int[][] getAdjG() {
		return adjG;
	}

	public void setAdjG(int[][] adjG) {
		this.adjG = adjG;
	}

	public Node getStartS() {
		return startS;
	}

	public void setStartS(Node startS) {
		this.startS = startS;
	}

	public Node getGoalS() {
		return goalS;
	}

	public void setGoalS(Node goalS) {
		this.goalS = goalS;
	}

	public int getTTime(int a, int b) {

		return this.adjG[a][b];
	}
}

class Node implements Comparator<Node> {

	private int id;
	private int priority;
	private String state = null;
	private Node parent = null;
	private int Depth = 0;
	private int gCost = 0;
	private int hCost = 0;

	public Node() {

	}

	public Node(int id) {
		this.setId(id);
	}

	public Node(String state) {
		this.setState(state);
	}

	public Node(int id, String state) {
		this.setState(state);
		this.setId(id);
	}

	public Node(int id, int priority, String state) {
		this.setState(state);
		this.setId(id);
		this.setPriority(priority);
	}

	public Node(int id, String state, int pcost) {
		this.setId(id);
		this.setState(state);
		this.setGCost(pcost);
	}

	public Node(int id, String state, int pcost, int hCost) {
		this.setId(id);
		this.setState(state);
		this.setGCost(pcost);
		this.setHCost(hCost);
	}

	public Node(int id, int priority, String state, int pcost, int hCost) {
		this.setId(id);
		this.setPriority(priority);
		this.setState(state);
		this.setGCost(pcost);
		this.setHCost(hCost);
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && (o instanceof Node) && ((Node) o).getState() == this.getState())
			return true;
		if (o != null && (o instanceof Node) && ((Node) o).getId() == this.getId())
			return true;

		return false;
	}

	@Override
	public int hashCode() {
		return this.getId();
	}

	@Override
	public int compare(Node node1, Node node2) {
		if (node1.getGCost() == node2.getGCost())
			return (node1.getId() - node2.getId());
		return (node1.getGCost() - node2.getGCost());
	}

	public int getGCost() {
		return gCost;
	}

	public void setGCost(int gCost) {
		this.gCost = gCost;
	}

	public int getHCost() {
		return hCost;
	}

	public void setHCost(int hCost) {
		this.hCost = hCost;
	}

	public int getDepth() {
		return Depth;
	}

	public void setDepth(int depth) {
		Depth = depth;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}

class LiveTraffic {

	private String lines;

	public LiveTraffic(String lines) {
		this.lines = lines;
	}

	public String getLine() {
		return this.lines;
	}

	public String getFirstS() {
		String[] temp = this.lines.split(" ");
		return temp[0];
	}

	public String getSecondS() {
		String[] temp = this.lines.split(" ");
		return temp[1];
	}

	public int getTravelTime() {
		String[] temp = this.lines.split(" ");
		try {
			return Integer.parseInt(temp[2].replaceAll("\\s", ""));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

}
