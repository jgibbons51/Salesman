import java.util.ArrayList;


//edgeList consists of all vertices which are not in our desired hamiltonian cycle
//Initially, the edgeList has all vertices
public class EdgeSet {
	private ArrayList<Edge> edgeList;
	
	public EdgeSet() {
		edgeList = new ArrayList<Edge>();
	}
	public void addEdge(Edge e){
		edgeList.add(e);
	}
	
	//removes an edge from the edge list
	public void removeEdge(Edge e) {
		edgeList.remove(e);
	}
		
	//returns all edges that contain a given vertex as an endpoint
	public ArrayList<Edge> getEdges(Vertex v) {
		ArrayList<Edge> returnEdgeList = new ArrayList<Edge>();
		
		for (int i = 0; i < edgeList.size(); i++){
			//If the Vertex v is in the VertexSet of the edge, add that edge to the returnEdgeList
			if (edgeList.get(i).getEndpoints().get(0) == v || edgeList.get(i).getEndpoints().get(1) == v) {
				returnEdgeList.add(edgeList.get(i)); //add the edge to returnList
			}
		}
		return returnEdgeList;
	}
	
	public int getNumberOfEdges(){
		return edgeList.size();
	}
	
	//returns an edge given its endpoints - this edge must be in the edgelist
	public Edge getEdgeGivenEndpoints(Vertex a, Vertex b) {
		Edge returnEdge = edgeList.get(0);
		for (int i = 0; i < edgeList.size(); i++) {
			Edge e = edgeList.get(i);
			if ((e.getEndpoints().get(0) == a && e.getEndpoints().get(1) == b) || (e.getEndpoints().get(0) == b && e.getEndpoints().get(1) == a)){
				returnEdge = e;
			}
		}
		return returnEdge;
	}
	
	//returns the edge occupying the ith index of edgeList
	public Edge getEdge(int i) {
		return edgeList.get(i);
	}
	
	
	//returns an edge's distance given its endpoints - this edge must be in the edgeList
	public double getDistanceGivenEndpoints(Vertex a, Vertex b) {
		double distance = 0;
		for (int i = 0; i < edgeList.size(); i++) {
			Edge e = edgeList.get(i);
			if ((e.getEndpoints().get(0) == a && e.getEndpoints().get(1) == b) || (e.getEndpoints().get(0) == b && e.getEndpoints().get(1) == a)){
				distance = e.getDistance();
			}
		}
		return distance;
	}
}
