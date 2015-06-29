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
    
   
}
