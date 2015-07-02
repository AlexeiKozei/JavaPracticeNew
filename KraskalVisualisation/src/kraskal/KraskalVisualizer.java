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
    
	private Color RandomColor()
    {
        Random rnd = new Random();
        float[] hsb = Color.RGBtoHSB(rnd.nextInt(255), rnd.nextInt(255), 
                rnd.nextInt(255), null);
        
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
    private Color InvertColor(Color c)
    {
        int r = 255 - c.getRed();
        int g = 255 - c.getGreen();
        int b = 255 - c.getBlue();
        
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
    
    private void GetColors()
    {
        colors = new Color[source.GetVertexCount()];
        for(int i = 0; i < source.GetVertexCount(); i++)
            colors[i] = RandomColor();
    }
    
    private void RenderToPanel(BufferedImage img)
    {
        render_box.getGraphics().drawImage(img, 0, 0, null);
    }
    
    
    private void DrawVertices(Graphics2D pobj)
    {
        int x, y, x1, y1;
        double alpha = 0.0;
        
        Font myFont = new Font("TimesNewRoman", Font.CENTER_BASELINE, 15);
        pobj.setFont(myFont);
        
        pobj.setPaintMode(); 
        for(Integer i = 0; i < source.GetVertexCount(); i++)
        {
            pobj.setColor(colors[i]);
            
            x = (int) (xc + rc * Math.cos(alpha));
            y = (int) (yc + rc * Math.sin(alpha));
            
            pobj.fillOval(x - rv, y - rv, 2 * rv, 2 * rv);
            
            pobj.setColor(InvertColor(pobj.getColor()));
            pobj.drawString(i.toString(), x - rt, y + rt);
            
            alpha += da;
        }
    }
    
    private void DrawEdges(Graph g, Graphics2D pobj, boolean is_render)
    {
        int x, y, x1, y1;
        double alpha;
        
        
        pobj.setColor(Color.BLACK);
        pobj.setStroke(new BasicStroke(10.0f));
        for(int i = 0; i < g.GetEdgeCount(); i++)
        {
            Graph.GraphEdge tmp_edge = g.GetEdge(i);
            
            int src = tmp_edge.GetSrc();
            int dst = tmp_edge.GetDst();
            
            alpha = da * src;
            x = (int)(xc + rc * Math.cos(alpha));
            y = (int)(yc + rc * Math.sin(alpha));
            
            alpha = da * dst;
            x1 = (int)(xc + rc * Math.cos(alpha));
            y1 = (int)(yc + rc * Math.sin(alpha));
            
            if(is_render)
               pobj.setColor(colors[src]);
            
            pobj.drawLine(x, y, x1, y1);
        }
    }
    
    private BufferedImage DrawGraph()
    {
        BufferedImage bg = new BufferedImage(render_box.getWidth(), 
                render_box.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        
        Graphics2D g = bg.createGraphics();
        
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, render_box.getWidth(), render_box.getHeight());
       
        DrawEdges(source, g, false);
        DrawVertices(g);
             
        return bg;
    }
    
    private BufferedImage Render(ArrayList<Graph.GraphEdge> edges)
    {
        BufferedImage bg = new BufferedImage(render_box.getWidth(), 
                render_box.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        
        Graphics2D g = bg.createGraphics();
        
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, render_box.getWidth(), render_box.getHeight());
        
        DrawEdges(new Graph(edges, source.GetVertexCount()), g, true);
        DrawVertices(g);
             
        return bg;
    }
    
}
