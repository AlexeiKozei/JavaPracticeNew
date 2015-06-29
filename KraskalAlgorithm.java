package kraskal;

import java.util.*;

public class KraskalAlgorithm
{
    public KraskalAlgorithm() {}
    
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
