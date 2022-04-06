import java.lang.reflect.Array;
import java.util.*;
import java.io.File;

public class FordFulkerson {

    //Finds connected nodes
    public static ArrayList<Integer> getNeighbors(int v, WGraph graph){

        ArrayList<Integer> neighbors = new ArrayList<>();
        for(int i = 0; i < graph.getEdges().size(); i++){
            if(graph.getEdges().get(i).nodes[0] == v){
                if(graph.getEdges().get(i).weight > 0) {
                    neighbors.add(graph.getEdges().get(i).nodes[1]);
                }
            }
        }
        return neighbors;
    }

    //Performs a DFS to find a path from source to destination
    public static ArrayList<Integer> pathDFS(Integer source, Integer destination, WGraph graph){

        Boolean[] visited = new Boolean[graph.getNbNodes()];
        for(int i=0; i<graph.getNbNodes(); i++){
            visited[i] = false;
        }

        ArrayList<Integer> pathList = new ArrayList<Integer>();
        pathList.add(source);

        ArrayList<ArrayList<Integer>> allPaths = new ArrayList<ArrayList<Integer>>();
        pathDFSHelper(source, destination, graph, visited, pathList, allPaths);

        if(allPaths.size() == 0){
            return new ArrayList<Integer>();
        }
        else{
            return allPaths.get(0);
        }
    }

    //DFS helper
    public static void pathDFSHelper(Integer source, Integer destination, WGraph graph, Boolean[] visited, ArrayList<Integer> path, ArrayList<ArrayList<Integer>> totalPath ) {

        if (Objects.equals(source, destination)) {
            totalPath.add(new ArrayList<Integer>(path));
        }

        visited[source] = true;

        ArrayList<Integer> neighbors = getNeighbors(source, graph);

        for (int i = 0; i < neighbors.size(); i++) {
            if (!visited[neighbors.get(i)]) {
                visited[neighbors.get(i)] = true;
                path.add(neighbors.get(i));
                pathDFSHelper(neighbors.get(i), destination, graph, visited, path, totalPath);
                path.remove(neighbors.get(i));
            }
        }
        visited[source] = false;
    }


    public static String fordfulkerson( WGraph graph){

        String answer="";
        int maxFlow = 0;
        int totalNodes = graph.getNbNodes();

        //Creates and initializes a residual graph and final graph. The residual is a copy of the original graph.
        //The final graph is a copy of the original except all edges have a weight of 0.
        //The final graph is used to later return and illustrate the final max flow.
        WGraph risidual = new WGraph();
        WGraph finalGraph = new WGraph();
        for (int i = 0; i < graph.getEdges().size(); i++) {
            risidual.addEdge(new Edge(graph.getEdges().get(i).nodes[0], graph.getEdges().get(i).nodes[1], graph.getEdges().get(i).weight));
            finalGraph.addEdge(new Edge(graph.getEdges().get(i).nodes[0], graph.getEdges().get(i).nodes[1], 0));
        }
        finalGraph.setSource(graph.getSource());
        finalGraph.setDestination(graph.getDestination());


        ArrayList<Integer> path = pathDFS(graph.getSource(), graph.getDestination(), risidual);

        //While there is a path in the residual graph.......
        while( path.size() > 0 ) {

            //The bottleneck of the path is found
            int bottleNeck = Integer.MAX_VALUE;
            for (int i = 0; i < path.size(); i++) {

                if(i == 0){ continue;}
                int node1 = path.get(i-1);
                int node2 = path.get(i);

                if (risidual.getEdge(node1,node2).weight < bottleNeck) {
                    bottleNeck = risidual.getEdge(node1,node2).weight;
                }
            }

            //The edges in the path of the residual graph are updated according to the bottleneck
            for (int i = 0; i < path.size(); i++) {
                if(i == 0){ continue;}
                int node1 = path.get(i-1);
                int node2 = path.get(i);

                //The bottleneck is subtracted from the weight of the edge in the residual graph
                risidual.setEdge(node1,node2,risidual.getEdge(node1,node2).weight - bottleNeck);

                //A reverse edge is added (or updated) in the residual graph
                if(risidual.getEdge(node2,node1) == null) {
                    risidual.addEdge(new Edge(node2, node1, bottleNeck));
                }
                else{
                    risidual.setEdge(node2,node1, risidual.getEdge(node2,node1).weight + bottleNeck);
                }

                //The final graph is also updated to illustrate final max flow
                if(finalGraph.getEdge(node1,node2) == null) {
                    finalGraph.addEdge(new Edge(node1,node2,bottleNeck));
                }
                else{
                    finalGraph.setEdge(node1,node2, finalGraph.getEdge(node1,node2).weight + bottleNeck);
                }

            }
            //For each path the bottleneck is added to the max flow
            maxFlow += bottleNeck;

            //A new path is found (or not)
            path = pathDFS(graph.getSource(), graph.getDestination(), risidual);
        }
        answer += "Max Flow: " + maxFlow + "\n" + finalGraph.toString();
        return answer;
    }


    public static void main(String[] args){

        WGraph g = new WGraph();
        g.setSource(0);
        g.setDestination(9);
        Edge[] edges = new Edge[] {
                new Edge(0, 1, 10),
                new Edge(0, 2, 5),
                new Edge(2, 3, 5),
                new Edge(1, 3, 10),
                new Edge(3, 4, 5),
                new Edge(4, 5, 10),
                new Edge(4, 6, 5),
                new Edge(6, 7, 5),
                new Edge(6, 8, 10),
                new Edge(8, 9, 10),
        };
        Arrays.stream(edges).forEach(e->g.addEdge(e));
        String result = FordFulkerson.fordfulkerson(g);
        System.out.println(result);

    }
}

