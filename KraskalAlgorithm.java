package kraskal;

import java.util.*;

public class KraskalAlgorithm
{
    public KraskalAlgorithm() {}
	
	private void SortEdges(ArrayList<Graph.GraphEdge> edges, int start, int end)
    {
        if (start >= end)
            return;
        
        int i = start, j = end;
        int cur = i - (i - j) / 2;
        while (i < j) 
        {
            while (i < cur && (edges.get(i).GetLength() <= edges.get(cur).GetLength()))
                i++;
            while (j > cur && (edges.get(cur).GetLength() <= edges.get(j).GetLength())) 
                j--;
            if (i < j) 
            {
                Graph.GraphEdge temp = edges.get(i);
                edges.set(i, edges.get(j));
                edges.set(j, temp);
                               
                if (i == cur)
                    cur = j;
                else if (j == cur)
                    cur = i;
            }
        }
        SortEdges(edges, start, cur);
        SortEdges(edges, cur + 1, end);
    }
    
    public Graph GetMinSpanTree(Graph src)
    {
        if(src == null)
            throw new NullPointerException("src mustn't be null");
        else
        {
            ArrayList<Graph.GraphEdge> data = src.GetData();
            ArrayList<Graph.GraphEdge> mst = new ArrayList<>();
            int cost = 0;
            
            SortEdges(data, 0, data.size() - 1);
            int[] tree_id = new int[src.GetVertexCount()];
            
            for(int i = 0; i < src.GetVertexCount(); i++)
                tree_id[i] = i;
            
            for(int i = 0; i < src.GetEdgeCount(); i++)
            {
                int a = data.get(i).GetSrc();
                int b = data.get(i).GetDst();
                int l = data.get(i).GetLength();
                
                if(tree_id[a] != tree_id[b])
                {
                    cost += l;
                    mst.add(data.get(i));
                    
                    int old_id = tree_id[b], new_id = tree_id[a];
                    for(int j = 0; j < src.GetVertexCount(); j++)
                    {
                        if(tree_id[j] == old_id)
                            tree_id[j] = new_id;
                    }
                }
                    
            }
            
            return new Graph(mst, src.GetVertexCount());
        }
    }
}
