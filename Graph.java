package kraskal;

import java.io.*;
import java.util.*;

public class Graph
{
    public class GraphEdge
    {
        private int src, dst, length;
        
        public GraphEdge(int x, int y, int len)
        {
            if(x < 0 || y < 0 || len < 0)
                throw new IllegalArgumentException("x, y, len must be > 0");
            else
            {
                src = x;
                dst = y;
                length = len;
            }
            
        }
        
        public int GetSrc() { return src; }
        public int GetDst() { return dst; }
        public int GetLength() { return length; }
    }
    
    private ArrayList<GraphEdge> data;
    private int vertex_count;
    
     public Graph(String file_name) throws FileNotFoundException
    {
        Scanner fin = new Scanner(new File(file_name));
        
        int edges_count = fin.nextInt();
        vertex_count = fin.nextInt();
        data = new ArrayList<>();
        
        int x, y, l;
        for(int i = 0; i < edges_count; i++)
        {
            x = fin.nextInt();
            y = fin.nextInt();
            l = fin.nextInt();
            
            data.add(new GraphEdge(x, y, l));
        }
    }
    
    
}
