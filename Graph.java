import java.util.*;
import java.io.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

//Creates the minimum tour for the traveling salesman
public class Graph {
    private ArrayList<Vertex> vertexListFinal;
    private ArrayList<Edge> edgeListFinal;
    private double lengthOfTour; 
        
    ArrayList<String> cities = new ArrayList<String>();
    ArrayList<String> entries = new ArrayList<String>();
    ArrayList<Integer> nums = new ArrayList<Integer>();
    ArrayList<String> lines = new ArrayList<String>();
    
    public Graph() {
        vertexListFinal = new ArrayList<Vertex>();
        edgeListFinal = new ArrayList<Edge>();
        lengthOfTour = 0; //variable that stores the length of tour at each step
    }
    
    //starts by finding the smallest edge and then constructing the smallest possible 3-cycle
    //then, uses the update function to build upon the cycle
    public void constructTour(VertexSet v, EdgeSet e) {
        
        //find the shortest edge, remove it from the edge set, add it to our tour
        double minDistance = e.getEdge(0).getDistance();
        Edge smallestEdge = e.getEdge(0);
        for (int i = 0; i < e.getNumberOfEdges(); i++) {
            if (e.getEdge(i).getDistance() < minDistance) {
                minDistance = e.getEdge(i).getDistance();
                smallestEdge = e.getEdge(i);
            }
        }
        
        lengthOfTour += minDistance;
        e.removeEdge(smallestEdge);
        edgeListFinal.add(smallestEdge);
        
        for (int i = 0; i < smallestEdge.getEndpoints().size(); i++){
            vertexListFinal.add(smallestEdge.getEndpoints().get(i));
            v.removeVertex(smallestEdge.getEndpoints().get(i));
        }
        
        //forms the smallest triangle
        double sum = e.getDistanceGivenEndpoints(v.getVertex(0), vertexListFinal.get(0)) + e.getDistanceGivenEndpoints(v.getVertex(0), vertexListFinal.get(1));
        Vertex vertexToAdd = v.getVertex(0);
        Edge edgeOneToAdd = e.getEdgeGivenEndpoints(vertexToAdd, vertexListFinal.get(0));
        Edge edgeTwoToAdd = e.getEdgeGivenEndpoints(vertexToAdd, vertexListFinal.get(1));
        for (int i = 0; i < v.getNumberOfVertices(); i++) {
            if (e.getDistanceGivenEndpoints(v.getVertex(i), vertexListFinal.get(0)) + e.getDistanceGivenEndpoints(v.getVertex(i), vertexListFinal.get(1)) < sum) {
                sum = e.getDistanceGivenEndpoints(v.getVertex(i), vertexListFinal.get(0)) + e.getDistanceGivenEndpoints(v.getVertex(i), vertexListFinal.get(1));
                vertexToAdd = v.getVertex(i);
                edgeOneToAdd = e.getEdgeGivenEndpoints(vertexToAdd, vertexListFinal.get(0));
                edgeTwoToAdd = e.getEdgeGivenEndpoints(vertexToAdd, vertexListFinal.get(1));
            }
        }
        edgeListFinal.add(edgeOneToAdd);
        e.removeEdge(edgeOneToAdd);
        edgeListFinal.add(edgeTwoToAdd);
        e.removeEdge(edgeTwoToAdd);
        vertexListFinal.add(vertexToAdd);
        v.removeVertex(vertexToAdd);
        lengthOfTour += edgeOneToAdd.getDistance() + edgeTwoToAdd.getDistance();
        
        //continues updating
        while (v.getNumberOfVertices() != 0) {
            ArrayList<Edge> edgesToRemoveFromEdgeSet = update(v,e);
            for (int i = 0; i < edgesToRemoveFromEdgeSet.size(); i++) {
                e.removeEdge(edgesToRemoveFromEdgeSet.get(i));
                ArrayList<Vertex>  vertexToRemove = edgesToRemoveFromEdgeSet.get(i).getEndpoints();
                for (int j = 0; j < vertexToRemove.size(); j++) {
                    v.removeVertex(vertexToRemove.get(j));
                }
            }
        }
    }
    
    //adds a vertex with the cheapest increased cost to the finalVertexList, increasing the cycle length by 1
    //returns the vertex to be removed from the vertex set
    public ArrayList<Edge> update(VertexSet verticesNotInCycle, EdgeSet edgesNotInCycle) {
        
        ArrayList<Double> minimumAddedDistances = new ArrayList<Double>(); //stores all the minimum added distances to the graph for each choice of current edge
        ArrayList<Edge> edgesToRemove = new ArrayList<Edge>(); //stores the edges you need to remove in order to add the new vertex
        ArrayList<Vertex> verticesToAdd = new ArrayList<Vertex>(); //stores the vertex that you add to the cylce to get the minimum added distance for the choice of the current edge
        
        //looks at each edge IN our cycle/tour so far
        for (int i = 0; i < edgeListFinal.size(); i++){
            
            //variables for vertex to be added and edge to be removed from the current tour
            Vertex vertex = verticesNotInCycle.getVertex(0);
            
            Edge e = edgeListFinal.get(i);
            
            //stores the minimum added difference accumulated by subtracting out an edge from the tour and adding in two new edges 
            double minimumAddedDifference = - e.getDistance() + edgesNotInCycle.getDistanceGivenEndpoints(verticesNotInCycle.getVertex(0), e.getEndpoints().get(0)) + edgesNotInCycle.getDistanceGivenEndpoints(verticesNotInCycle.getVertex(0), e.getEndpoints().get(1));;
            
            //looks at each vertex not in our cycle
            for (int j = 0; j < verticesNotInCycle.getNumberOfVertices(); j++) {
                Vertex newVertex = verticesNotInCycle.getVertex(j);
                
                //looks at the edges that contain the new vertex and endpoints of the edge we're looking at 
                
                //sees the difference of subtracting out our old edge and adding the two new edges in
                double difference = - e.getDistance() + edgesNotInCycle.getDistanceGivenEndpoints(newVertex, e.getEndpoints().get(0)) + edgesNotInCycle.getDistanceGivenEndpoints(newVertex, e.getEndpoints().get(1));
                
                if (difference < minimumAddedDifference) {
                    minimumAddedDifference = difference;
                    vertex = newVertex;
                }               
            }
            minimumAddedDistances.add(minimumAddedDifference); 
            verticesToAdd.add(vertex);
            edgesToRemove.add(e);
        }
        
        //find the minimum "minimumAddedDifference" in the ArrayList
        double trueMin = minimumAddedDistances.get(0);
        for (int i = 0; i < minimumAddedDistances.size(); i++) {
            if (minimumAddedDistances.get(i) < trueMin) {
                trueMin = minimumAddedDistances.get(i);
            }
        }
        
        //updates the cycle - changes tour length, removes edge from cycle, adds new vertex to cycle, adds new edges to cycle
        lengthOfTour += trueMin;
        int index = minimumAddedDistances.indexOf(trueMin);
        Vertex vertexToAddToCycle = verticesToAdd.get(index);
        Edge edgeToRemoveFromCycle = edgesToRemove.get(index);
        edgeListFinal.remove(edgeToRemoveFromCycle);
        vertexListFinal.add(vertexToAddToCycle);
        
        Vertex endpoint1 = edgeToRemoveFromCycle.getEndpoints().get(0);
        Vertex endpoint2 = edgeToRemoveFromCycle.getEndpoints().get(1);
        double distance1 = edgesNotInCycle.getDistanceGivenEndpoints(endpoint1, vertexToAddToCycle);
        double distance2 = edgesNotInCycle.getDistanceGivenEndpoints(endpoint2, vertexToAddToCycle);
        
        edgeListFinal.add(new Edge (endpoint1, vertexToAddToCycle, distance1));
        edgeListFinal.add(new Edge (endpoint2, vertexToAddToCycle, distance2));
        
        ArrayList<Edge> edgesToRemoveFromEdgeSet = new ArrayList<Edge>();
        edgesToRemoveFromEdgeSet.add(new Edge (endpoint1, vertexToAddToCycle, distance1));
        edgesToRemoveFromEdgeSet.add(new Edge (endpoint2, vertexToAddToCycle, distance2));
        
        return edgesToRemoveFromEdgeSet;
    }
    
    public void printTour() {
        int numOfEdgesInTour = edgeListFinal.size();
        
        System.out.println(1 + ": " + edgeListFinal.get(0).getEndpoints().get(0).getLabel() + " to " + edgeListFinal.get(0).getEndpoints().get(1).getLabel() + " - " + edgeListFinal.get(0).getDistance() + " miles");
        Vertex startingVertex = edgeListFinal.get(0).getEndpoints().get(0);
        Vertex nextVertex = edgeListFinal.get(0).getEndpoints().get(1);
        edgeListFinal.remove(0);
        
        while (edgeListFinal.size() != 0) {
            for (int j = 2; j <= numOfEdgesInTour; j++) {
                for (int i = 0; i < edgeListFinal.size(); i++) {
                    if (edgeListFinal.get(i).getEndpoints().get(0) == nextVertex){
                        System.out.println(j + ": " + edgeListFinal.get(i).getEndpoints().get(0).getLabel() + " to " + edgeListFinal.get(i).getEndpoints().get(1).getLabel() + " - " + edgeListFinal.get(i).getDistance() + " miles");
                        startingVertex = edgeListFinal.get(i).getEndpoints().get(0);
                        nextVertex = edgeListFinal.get(i).getEndpoints().get(1);
                        edgeListFinal.remove(i);
                        break;
                    }
                    else if (edgeListFinal.get(i).getEndpoints().get(1) == nextVertex) {
                        System.out.println(j + ": " + edgeListFinal.get(i).getEndpoints().get(1).getLabel() + " to " + edgeListFinal.get(i).getEndpoints().get(0).getLabel() + " - " + edgeListFinal.get(i).getDistance() + " miles");
                        startingVertex = edgeListFinal.get(i).getEndpoints().get(1);
                        nextVertex = edgeListFinal.get(i).getEndpoints().get(0);
                        edgeListFinal.remove(i);
                        break;
                    }
                }   
            }
        }
        
        System.out.println("The tour's length is " + (int) lengthOfTour + " miles.");
    }
    
    public static void main (String[] args) throws FileNotFoundException {
        Graph g = new Graph();
        g.enterText();
        
        Scanner s = new Scanner(new File("Test.txt")).useDelimiter("\\s*, \\s*");
        
        VertexSet v = new VertexSet();
        while (s.hasNext()) {
            v.addVertex(new Vertex (s.next(), Double.parseDouble(s.next()), Double.parseDouble(s.next())));
        }
        
        //v.addVertex(new Vertex("a", 0, 0));
        //v.addVertex(new Vertex("b", 0, 1));
        //v.addVertex(new Vertex("c", 1, 1));
        //v.addVertex(new Vertex("d", 1, 0));
        //v.addVertex(new Vertex("e", 2, 2));
        //v.addVertex(new Vertex("f", 3, 3));
        
        EdgeSet e = new EdgeSet();
        ArrayList<Edge> listOfEdges = v.getAllPossibleEdges();
        for (int i = 0; i < listOfEdges.size(); i++) {
            e.addEdge(listOfEdges.get(i));
        }
        
        g.constructTour(v, e);
        g.printTour();
      
    }
    
    public void enterText() throws FileNotFoundException 
    {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the number of cities you would like to visit");
        int number = scan.nextInt();
        scan.nextLine();
        
        for(int i=0; i<number; i++)
        {
            System.out.println("Enter the name of a city you will visit. Please do not repeat.");
            cities.add(scan.nextLine());
        }
           
        Scanner s = new Scanner(new File("Citylist.txt")).useDelimiter("\\s*,\\s*");
        
        int tracker = 0;
        while(s.hasNext())
        {
           String str = s.next();           
           //System.out.println(tracker+"\t"+str);
           entries.add(str);
           tracker++;
        }
        
        for(int j=0; j<cities.size(); j++)
        {
            for(int k=0; k<entries.size(); k++)
            {
                if(cities.get(j).equals(entries.get(k)))
                {
                  //System.out.println(j+"\t"+k);
                  nums.add(k);
                }
            }
        }
        
        for(int l=0; l<nums.size(); l++)
        {
            //System.out.println(nums.get(l));
            
            int n = nums.get(l);
            String st = entries.get(n).concat(",").concat(entries.get(n+1)).concat(", ").concat(entries.get(n+2)).concat(", ").concat(entries.get(n+3)).concat(", ");
            lines.add(st);           
        }
                
        PrintWriter out = new PrintWriter("Test.txt");
        
        for(int n=0; n<lines.size(); n++)
        {
            out.println(lines.get(n));
        }
        out.close();
    }    
}
