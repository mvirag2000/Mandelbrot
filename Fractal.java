import java.awt.*;
import java.applet.*;
// Old Fractal program using Layout manager and B/W Mandelbrot generator
public class Fractal extends Applet 
{	public void init() 
    {	super.init();
    	addNotify();
		resize(300,300);
    	setLayout(new BorderLayout());
		Panel p = new Panel();
    		p.setLayout(new FlowLayout());
        	p.add(new Button("Right"));
    	    p.add(new Button("Left"));
    	    p.add(new Button("Up"));
    	    p.add(new Button("Down"));
    	    p.add(new Button("In"));
    	    p.add(new Button("Out"));
    	add("North", p);
		b = new BiffCanvas();
		b.topLeft = new ComplexPoint(-2,2);
		b.bottomRight = new ComplexPoint(2,-2);
		add("Center", b);
    }
    public boolean handleEvent(Event e)
    {   if (e.id == Event.WINDOW_DESTROY) System.exit(0);
        return super.handleEvent(e);
    }    
	public boolean action(Event e, Object arg) 
	{   double tlx = b.topLeft.x;
	    double tli = b.topLeft.i;
	    double brx = b.bottomRight.x;
	    double bri = b.bottomRight.i;
	    double xdist = brx - tlx;
	    double idist = tli - bri;
	    if (arg == "In")
	    {   b.topLeft.Set(tlx + xdist / 6, tli - idist / 6);
	        b.bottomRight.Set(brx - xdist / 6, bri + idist / 6);
	    }    
	    if (arg == "Out")
	    {   b.topLeft.Set(tlx - xdist / 6, tli + idist / 6);
	        b.bottomRight.Set(brx + xdist / 6, bri - idist / 6);
	    }    
	    if (arg == "Right")
	    {   b.topLeft.Set(tlx + xdist / 4, tli);
	        b.bottomRight.Set(brx + xdist / 4, bri);
	    }    
	    if (arg == "Left")
	    {   b.topLeft.Set(tlx - xdist / 4, tli);
	        b.bottomRight.Set(brx - xdist / 4, bri);
	    }
	    if (arg == "Up")
	    {   b.topLeft.Set(tlx, tli + idist / 4);
	        b.bottomRight.Set(brx, bri + idist / 4);
	    }
	    if (arg == "Down")
	    {   b.topLeft.Set(tlx, tli - idist / 4);
	        b.bottomRight.Set(brx, bri - idist / 4);
	    }    
	    b.repaint();
	    return true;
	}
	private BiffCanvas b;
} //Applet Fractal

class BiffCanvas extends Canvas 
{   public void paint(Graphics g) 
	{   double newi = 0;
	    double newx = 0;
	    ComplexPoint blah = new ComplexPoint(newx, newi);
	    Dimension d = size();
        int w = d.width;
        int h = d.height;
        double iScaleUnit = (topLeft.i - bottomRight.i) / h;
        double xScaleUnit = (bottomRight.x - topLeft.x) / w;
        //Try forcing the aspect ratio to 1.0:
        if (xScaleUnit > iScaleUnit)
            xScaleUnit = iScaleUnit;
        else
            iScaleUnit = xScaleUnit;
	    for (int xpos = 0; xpos < w; xpos++)
	        for (int ypos = 0; ypos < h ; ypos++)
	        {   newi = topLeft.i - ypos * iScaleUnit;
	            newx = topLeft.x + xpos * xScaleUnit;
	            blah.Set(newx, newi);
	            if (blah.MandelBW(100))
	                g.setColor(Color.black);
	            else
	                g.setColor(Color.white);
           	    g.fillRect(xpos, ypos, 1, 1);
    	    }
	} 
	public ComplexPoint topLeft;
	public ComplexPoint bottomRight;    
} //Class BiffCanvas
