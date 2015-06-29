package kraskal;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.*;

public class KraskalVisualizer extends Thread
{
    JPanel render_box;
    JProgressBar progress;
    Graph source;
    MainForm form;
    
    Color[] colors; //colors of vertices
    
    int rc, rv, rt;
    int xc;
    int yc;
        
    double da;
    
    public KraskalVisualizer(MainForm mform, Graph g)
    {
        render_box = mform.RenderBox;
        progress = mform.VisProgress;
        source = g;
        form = mform;
        
        rc = 200; rv = 20; rt = 3;
        xc = render_box.getWidth() / 2;
        yc = render_box.getHeight() / 2;
        
        da = 2 * Math.PI / source.GetVertexCount();
        
        GetColors();
    }
    
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
    
    @Override
    public void run()
    {
        if (source == null)
        {
            throw new NullPointerException("src mustn't be null");
        } 
        else
        {           
            RenderToPanel(DrawGraph());
            progress.setValue(1);
            this.suspend();
            
            
            ArrayList<Graph.GraphEdge> data = source.GetData();
            ArrayList<Graph.GraphEdge> mst = new ArrayList<>();
            Integer cost = 0;

            SortEdges(data, 0, data.size() - 1);
            int[] tree_id = new int[source.GetVertexCount()];

            for (int i = 0; i < source.GetVertexCount(); i++)
            {
                tree_id[i] = i;
            }

            int dp = 100 / source.GetEdgeCount();
            for (int i = 0; i < source.GetEdgeCount(); i++)
            {
                int a = data.get(i).GetSrc();
                int b = data.get(i).GetDst();
                int l = data.get(i).GetLength();

                if (tree_id[a] != tree_id[b])
                {
                    cost += l;
                    mst.add(data.get(i));

                    int old_id = tree_id[b], new_id = tree_id[a];
                    Color old_color = colors[b], new_color = colors[a];
                    for (int j = 0; j < source.GetVertexCount(); j++)
                    {
                        if (tree_id[j] == old_id)
                        {
                            tree_id[j] = new_id;
                            colors[j] = new_color;
                        }
                    }
                    RenderToPanel(Render(mst));
                    progress.setValue(progress.getValue() + dp);
                    this.suspend();
                }
                else
                    progress.setValue(progress.getValue() + dp);
            }
            
            form.VisProgress.setValue(0);
            form.NextButton.setEnabled(false);
            form.VisualizeButton.setEnabled(true);
            form.LoadButton.setEnabled(true);
            form.MSTLen.setText(cost.toString());
            
            JOptionPane.showMessageDialog(null, "Визуализация завершена!",
                                              "Информация", JOptionPane.INFORMATION_MESSAGE);
            RenderToPanel(Render(mst));
            
            this.stop();
        }
    }
    
    
}
