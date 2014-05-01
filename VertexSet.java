import java.util.ArrayList;

//vertexList consists of all vertices which are not in our desired hamiltonian cycle
//Initially, the vertexList has all vertices
public class VertexSet {
    private ArrayList<Vertex> vertexList;
    Angles A = new Angles();
    
    public VertexSet() {
        vertexList = new ArrayList<Vertex>();
    }
    public void addVertex(Vertex v){
        vertexList.add(v);
    }
    public Vertex getVertex(int i) {
        return vertexList.get(i);
    }
    public void removeVertex(Vertex v) {
        vertexList.remove(v);
    }
    public int getNumberOfVertices(){
        return vertexList.size();
    }
    //returns ArrayList of all possible edges given the vertexSet
    public ArrayList<Edge> getAllPossibleEdges() {
        ArrayList<Edge> returnEdgeList = new ArrayList<Edge>();
        
        for (int i = 0; i < vertexList.size(); i++) {
            for (int j = i+1; j < vertexList.size(); j++){
                    double distance = A.calculate(vertexList.get(i).getX(), vertexList.get(i).getY(), vertexList.get(j).getX(), vertexList.get(j).getY());
                    returnEdgeList.add(new Edge(vertexList.get(i), vertexList.get(j), distance));
            }
        }
        
        return returnEdgeList; 
    }
    
}
