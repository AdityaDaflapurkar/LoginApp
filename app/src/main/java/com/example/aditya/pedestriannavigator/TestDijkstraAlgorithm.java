package com.example.aditya.pedestriannavigator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class TestDijkstraAlgorithm {

  private static List<Vertex> nodes;
  private static List<Edge> edges;

  
  public static void main(String[] args){
    nodes = new ArrayList<Vertex>();
    edges = new ArrayList<Edge>();
    for (int i = 0; i < 7; i++) {
      Vertex location = new Vertex("Node_" + i, "Node_" + i);
      nodes.add(location);
    }

    addLane("Edge_0", 0, 1, 1);
    addLane("Edge_1", 0, 6, 8);
    addLane("Edge_2", 1, 2, 2);
    addLane("Edge_3", 1, 3, 4);
    addLane("Edge_4", 2, 4, 3);
    addLane("Edge_5", 3, 5, 2);
    addLane("Edge_6", 4, 5, 5);
    addLane("Edge_7", 4, 6, 1);
    addLane("Edge_8", 5, 6, 2);

    // Lets check from location Loc_1 to Loc_10
    Graph graph = new Graph(nodes, edges);
    DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
    dijkstra.execute(nodes.get(0));
    LinkedList<Vertex> path = dijkstra.getPath(nodes.get(4));
    
    
    
    for (Vertex vertex : path) {
      System.out.println(vertex);
    }
////
   
    Gson gson = new Gson();
    Type type = new TypeToken<LinkedList<Vertex>>() {}.getType();
    String json = gson.toJson(path, type);
    System.out.println(json);
    LinkedList<Vertex> fromJson = gson.fromJson(json, type);
    for (Vertex vertex : fromJson) {
        System.out.println(vertex);
    }
    
    /////
    
    Gson g1 = new Gson();
    Type t1 = new TypeToken<Graph>() {}.getType();
    String j1 = g1.toJson(graph, t1);
    System.out.println(j1);
    Graph navagraph = g1.fromJson(j1, t1);
    
    
  }

  private static void addLane(String laneId, int sourceLocNo, int destLocNo,
      int duration) {
    Edge lane = new Edge(laneId,nodes.get(sourceLocNo), nodes.get(destLocNo), duration);
    edges.add(lane);
  }
} 
