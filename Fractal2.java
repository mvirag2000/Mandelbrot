import java.awt.*;
import java.applet.*;
import symantec.itools.awt.shape.*;
// Rev2 Fractal program with color Mandelbrot generator and no LayoutMgr.
/* To Do: 
        First region (2.0,2.0) doesn't seem to be in the linked list.
   Rev 3:  Rip out all the niceties and put code in-line for efficiency.
*/    
public class Fractal2 extends Applet
{    public void init()
    {	super.init();
        b = new BiffCanvas();
		b.topLeft = new ComplexPoint(-2,2);
		b.bottomRight = new ComplexPoint(2,-2);
		l = new LinkedList(b.topLeft, b.bottomRight);
		c = new DwellCanvas();
		c.max = b.max = 100;
   		//{{INIT_CONTROLS
		setLayout(null);
		addNotify();
		resize(512,394);
		b.reshape(96,84,336,264);
		add(b);
		In = new java.awt.Button("In");
		In.reshape(12,204,60,24);
		add(In);
		Out = new java.awt.Button("Out");
		Out.reshape(12,228,60,24);
		add(Out);
		button1 = new java.awt.Button("Left");
		button1.reshape(12,108,60,24);
		add(button1);
		button2 = new java.awt.Button("Right");
		button2.reshape(12,132,60,24);
		add(button2);
		button3 = new java.awt.Button("Down");
		button3.reshape(12,180,60,24);
		add(button3);
		button4 = new java.awt.Button("Up");
		button4.reshape(12,156,60,24);
		add(button4);
		c.reshape(96,360,336,23);
		add(c);
		button5 = new java.awt.Button(">>");
		button5.reshape(432,360,24,24);
		add(button5);
		button6 = new java.awt.Button("<<");
		button6.reshape(72,360,24,24);
		add(button6);
		topI = new symantec.itools.awt.FormattedTextField();
		topI.setEditable(false);
		topI.reshape(12,72,75,22);
		topI.setFont(new Font("Helvetica", Font.PLAIN, 9));
		add(topI);
		topI.setMask("0.0000000000");
		botI = new symantec.itools.awt.FormattedTextField();
		botI.setEditable(false);
		botI.reshape(12,324,75,22);
		botI.setFont(new Font("Helvetica", Font.PLAIN, 9));
		add(botI);
		botI.setMask("0.0000000000");
		topX = new symantec.itools.awt.FormattedTextField();
		topX.setEditable(false);
		topX.reshape(96,36,75,22);
		topX.setFont(new Font("Helvetica", Font.PLAIN, 9));
		add(topX);
		topX.setMask("0.0000000000");
		botX = new symantec.itools.awt.FormattedTextField();
		botX.setEditable(false);
		botX.reshape(372,36,75,22);
		botX.setFont(new Font("Helvetica", Font.PLAIN, 9));
		add(botX);
		botX.setMask("0.0000000000");
		horizontalLine1 = new symantec.itools.awt.shape.HorizontalLine();
		horizontalLine1.reshape(144,132,45,2);
		add(horizontalLine1);
		button7 = new java.awt.Button("Reset");
		button7.reshape(24,36,60,24);
		add(button7);
		spnMax = new symantec.itools.awt.NumericSpinner();
		spnMax.reshape(0,276,84,36);
		add(spnMax);
		spnMax.setMin(100);
		spnMax.setMax(950);
		spnMax.setCurrent(100);
		spnMax.setIncrement(50);
		cmdBack = new java.awt.Button("Back");
		cmdBack.reshape(192,36,60,24);
		add(cmdBack);
		cmdForward = new java.awt.Button("Forward");
		cmdForward.reshape(300,36,60,24);
		add(cmdForward);
		//}}
		p = b.location();
		g = b.getGraphics();
        topX.setText(dubble.toString(b.topLeft.x));
        topI.setText(dubble.toString(b.topLeft.i));
        botX.setText(dubble.toString(b.bottomRight.x));
        botI.setText(dubble.toString(b.bottomRight.i));
	}
    public boolean handleEvent(Event e)
    {   if (e.id == Event.WINDOW_DESTROY)
            System.exit(0);
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
            topX.setText(dubble.toString(b.topLeft.x)); //Repaint may change the coordinates
            topI.setText(dubble.toString(b.topLeft.i));
            botX.setText(dubble.toString(b.bottomRight.x));
            botI.setText(dubble.toString(b.bottomRight.i));
            l.addList(b.topLeft, b.bottomRight);
            return true;
        }
		if (e.target == spnMax && e.id == Event.MOUSE_DOWN) {
			c.max = b.max = spnMax.getCurrent();
			return true;
		}
        return super.handleEvent(e);
    }  //Event Handler
    
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
	    if (arg == "Back")
	    {   l.back();
	        b.topLeft = l.topLeft();
	        b.bottomRight = l.bottomRight();
	    }    
	    if (arg == "Forward")
	    {   l.forward();
	        b.topLeft = l.topLeft();
	        b.bottomRight = l.bottomRight();
	    }    
	    if (arg == "Reset")
	    {   b.topLeft.Set(-2.0, 2.0);
	        b.bottomRight.Set(2.0, -2.0);
	        spnMax.setCurrent(100);
	        c.max = b.max = 100;
	        c.skew = b.skew = 20;
	        c.repaint();
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
        topX.setText(dubble.toString(b.topLeft.x)); //Repaint may change the coordinates
        topI.setText(dubble.toString(b.topLeft.i));
        botX.setText(dubble.toString(b.bottomRight.x));
        botI.setText(dubble.toString(b.bottomRight.i));
//        l.addList(b.topLeft, b.bottomRight);
	    return true;
	}  //Action
	
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
	private Double dubble = new Double(0);
	private int downX, downY, dragX, dragY;
	private Point p;
	private Graphics g;
	private LinkedList l;
	//{{DECLARE_CONTROLS
	java.awt.Button In;
	java.awt.Button Out;
	java.awt.Button button1;
	java.awt.Button button2;
	java.awt.Button button3;
	java.awt.Button button4;
	java.awt.Button button5;
	java.awt.Button button6;
	symantec.itools.awt.FormattedTextField topI;
	symantec.itools.awt.FormattedTextField botI;
	symantec.itools.awt.FormattedTextField topX;
	symantec.itools.awt.FormattedTextField botX;
	symantec.itools.awt.shape.HorizontalLine horizontalLine1;
	java.awt.Button button7;
	symantec.itools.awt.NumericSpinner spnMax;
	java.awt.Button cmdBack;
	java.awt.Button cmdForward;
	//}}
} //Applet Fractal2

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
	
	public int max;
	public int skew = 20;
	private double ScaleUnit;  // Points/Pixels
	public ComplexPoint topLeft;
	public ComplexPoint bottomRight;    
	private ComplexPoint blah = new ComplexPoint(0, 0);
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
{   public Item(ComplexPoint tl, ComplexPoint br, Item n, Item p)
    {   topLeft = tl;
        bottomRight = br;
        next = n;
        prev = p;
    }
    public ComplexPoint topLeft;
    public ComplexPoint bottomRight;
    public Item next;
    public Item prev;
} //Class Item

class LinkedList
{   public LinkedList(ComplexPoint tl, ComplexPoint br)
    {   current = new Item(tl, br, null, null);
    }
    public void addList(ComplexPoint tl, ComplexPoint br)
    {   Item f = new Item(tl, br, current.next, current);
        if (current.next != null) current.next.prev = f;
        current.next = f;
        current = f;
    }    
    public void forward()
    {   if (current.next != null) current = current.next;
    }
    public void back()
    {   if (current.prev != null) current = current.prev;
    }
    public ComplexPoint topLeft()
    {   return current.topLeft;
    }
    public ComplexPoint bottomRight()
    {   return current.bottomRight;
    }
    private Item current;
} //Class LinkedList
