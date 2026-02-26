import java.awt.*;
import java.applet.*;
import symantec.itools.awt.shape.*;
public class Fractal3 extends Applet
{    public void init()
    {	super.init();
        b = new BiffCanvas();
		b.topLeft = new ComplexPoint(-2,2);
		b.bottomRight = new ComplexPoint(2,-2);
		st = new Stack(b.topLeft, b.bottomRight);
		c = new DwellCanvas();
		c.max = b.max = 100;
   		//{{INIT_CONTROLS
		setLayout(null);
		addNotify();
		resize(515,415);
		setBackground(new Color(12632256));
		button6 = new java.awt.Button("<<");
		button6.reshape(12,372,24,24);
		button6.setFont(new Font("Dialog", Font.PLAIN, 12));
		add(button6);
		button5 = new java.awt.Button(">>");
		button5.reshape(480,372,24,24);
		button5.setFont(new Font("Dialog", Font.PLAIN, 12));
		add(button5);
		c.reshape(36,372,444,23);
		b.reshape(12,36,492,336);
		add(b);
		add(c);
		cmdBack = new java.awt.Button("&Back");
		cmdBack.disable();
		cmdBack.reshape(0,0,50,21);
		cmdBack.setFont(new Font("Dialog", Font.PLAIN, 12));
		add(cmdBack);
		spnMax = new symantec.itools.awt.NumericSpinner();
		spnMax.reshape(420,0,84,36);
		spnMax.setFont(new Font("Dialog", Font.PLAIN, 12));
		add(spnMax);
		spnMax.setMin(50);
		spnMax.setMax(950);
		spnMax.setCurrent(50);
		spnMax.setIncrement(50);
		label1 = new java.awt.Label("Iterations",Label.RIGHT);
		label1.reshape(348,12,72,12);
		label1.setFont(new Font("Dialog", Font.PLAIN, 12));
		add(label1);
		label2 = new java.awt.Label("");
		label2.reshape(60,0,162,24);
		label2.setFont(new Font("Dialog", Font.PLAIN, 12));
		add(label2);
		//}}
		p = b.location();
		g = b.getGraphics();
	}
	
	/*
	**  EVENT HANDLER
	*/
	public boolean handleEvent(Event e)
    {   if (e.id == Event.WINDOW_DESTROY) System.exit(0);
		if (e.target == b && e.id == Event.MOUSE_DOWN)
		{   downX = e.x - p.x;  //With top, left of BiffCanvas as (0,0)
		    downY = e.y - p.y;
		    dragX = downX; dragY = downY;
		    drawBox();
		    return true;
        }
        if (e.target == b && e.id == Event.MOUSE_DRAG)
        {   drawBox();
            dragX = e.x - p.x;
            dragY = e.y - p.y;
            drawBox();
            return true;
        }
        if (e.target == b && e.id == Event.MOUSE_UP)
        {   drawBox();
            b.setScreen(downX, downY, dragX, dragY);
            b.repaint();
    		label2.setText("Scale: " + b.getScale());
            st.push(b.topLeft, b.bottomRight);
            cmdBack.enable();
            return true;
        }
		if (e.target == spnMax && e.id == Event.MOUSE_DOWN)
		{   c.max = b.max = spnMax.getCurrent();
			return true;
		}
        return super.handleEvent(e);
    } 

    /*
    **  ACTION PROCEDURE
    */
	public boolean action(Event e, Object arg)
	{   if (e.target == cmdBack)
	    {   st.pop();
	        b.topLeft = st.topLeft();
    	    b.bottomRight = st.bottomRight();
    	    if (st.Francis() == true) cmdBack.disable();
	    }
	    if (arg == ">>")
	    {   c.skew = b.skew = b.skew - 10;
	        c.repaint();
	    }
	    if (arg == "<<")
	    {   c.skew = b.skew = b.skew + 10;
	        c.repaint();
	    }
        b.repaint();
    	label2.setText("Scale: " + b.getScale());
	    return true;
	} 

	/*
	**  DRAW BOX
	*/	
	public void drawBox()
	{   int leftX, rightX, topY, bottomY;
	    if (downX > dragX)
	    {   rightX = downX; leftX = dragX; }
	    else
	    {   rightX = dragX; leftX = downX;
	    }
	    if (downY > dragY)
	    {   bottomY = downY; topY = dragY; }
	    else
	    {   bottomY = dragY; topY = downY;
	    }
	    g.setColor(Color.white);
	    g.setXORMode(Color.black);
	    g.drawRect(leftX, topY, rightX - leftX, bottomY - topY);
	}

	private DwellCanvas c;
	private BiffCanvas b;
	private int downX, downY, dragX, dragY;
	private Point p;
	private Graphics g;
	private Stack st;
	//{{DECLARE_CONTROLS
	java.awt.Button button6;
	java.awt.Button button5;
	java.awt.Button cmdBack;
	symantec.itools.awt.NumericSpinner spnMax;
	java.awt.Label label1;
	java.awt.Label label2;
	//}}
} //Applet Fractal2


/*
**  BIFFCANVAS CLASS
*/
class BiffCanvas extends Canvas
{   public void paint(Graphics g)
	{   Dimension d = size();
        double w = d.width;
        double h = d.height;
        double idist = topLeft.i - bottomRight.i;
        double xdist = bottomRight.x - topLeft.x;
        double PlaneAspect = idist / xdist;
        double ScreenAspect = h / w;
        if (ScreenAspect > PlaneAspect)
            ScaleUnit = xdist / w;
        else
            ScaleUnit = idist / h;
        topLeft.i = topLeft.i + (h * ScaleUnit - idist) / 2;
        bottomRight.i = bottomRight.i - (h * ScaleUnit - idist) / 2;
        topLeft.x = topLeft.x - (w * ScaleUnit - xdist) / 2;
        bottomRight.x = bottomRight.x + (w * ScaleUnit - xdist) / 2;
	    for (int xpos = 0; xpos < w; xpos++)
   	        for (int ypos = 0; ypos < h ; ypos++)
	        {   blah.Set(topLeft.x + xpos * ScaleUnit, topLeft.i - ypos * ScaleUnit);
	            g.setColor(ChromaScale(blah.MandelBrot(max)));
           	    g.fillRect(xpos, ypos, 1, 1);
    	    }
	} //Paint

	public void setScreen(int X1, int Y1, int X2, int Y2)
	{   int rightX, topY, leftX, bottomY;
	    if (X1 > X2)
	    {   rightX = X1; leftX = X2; }
	    else
	    {   rightX = X2; leftX = X1;
	    }
	    if (Y1 > Y2)
	    {   bottomY = Y1; topY = Y2; }
	    else
	    {   bottomY = Y2; topY = Y1;
	    }
	    double saveTopX = topLeft.x;
	    double saveTopI = topLeft.i;
	    topLeft.x = topLeft.x + ScaleUnit * leftX;
	    topLeft.i = topLeft.i - ScaleUnit * topY;
	    bottomRight.x = saveTopX + ScaleUnit * rightX;
	    bottomRight.i = saveTopI - ScaleUnit * bottomY;
	} //setScreen

	public Color ChromaScale(int k)
	{   Color f;
	    float min = (float) 0.00001;
	    if (k == max)
	        f = Color.black;
	    else
	    {   float fk = k + skew;
	        if (fk >= max) fk = max;
	        if (fk < min) fk = min;
    	    f = new Color(Color.HSBtoRGB((float)(max - fk) / max, (float)0.85, (float)0.85));
    	}
	    return f;
	} //ChromaScale
	
	public String getScale()
	{   return dubble.toString(ScaleUnit);
	}

	public int max;
	public int skew = 20;
	private double ScaleUnit;  // Points / Pixels
	public ComplexPoint topLeft;
	public ComplexPoint bottomRight;
	private ComplexPoint blah = new ComplexPoint();
	private Double dubble = new Double(0);
} //Class BiffCanvas

class DwellCanvas extends BiffCanvas
{   public void paint(Graphics g)
    {   Dimension d = size();
        int w = d.width;
        int h = d.height;
	    for (int xpos = 0; xpos < w; xpos++)
	    {   g.setColor(ChromaScale(xpos * max / w));
      	    g.fillRect(xpos, 0, 1, h);
       	}
    }
} //Class DwellCanvas

class Item
{   public Item(ComplexPoint tl, ComplexPoint br, Item n)
    {   topLeft = new ComplexPoint(tl); //need to make a copy
        bottomRight = new ComplexPoint(br);
        next = n;
    }
    public ComplexPoint topLeft;
    public ComplexPoint bottomRight;
    public Item next;
} //Class Item


/*
**  PUSHDOWN STACK
*/
class Stack
{   public Stack(ComplexPoint tl, ComplexPoint br)
    {   current = new Item(tl, br, null);
    }
    public ComplexPoint topLeft()
    {   if (current != null) return current.topLeft;
        else return null;
    }
    public ComplexPoint bottomRight()
    {   if (current != null) return current.bottomRight;
        else return null;
    }    
    public void push(ComplexPoint tl, ComplexPoint br)
    {   Item f = new Item(tl, br, current);
        current = f;
    }
    public void pop()
    {   if (current.next != null) current = current.next;
    } 
    public boolean Francis()
    {   if (current.next == null) return true;
        else return false;
    }    
    private Item current;
}
