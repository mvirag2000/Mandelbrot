/*
**  Representation for points in the complex plane, and typical complex math routines.
*/
public class ComplexPoint
{   
    
    /*
    **  Constructs a new ComplexPoint with the given coordinates.
    */
    public ComplexPoint(double x, double i)
    {   this.x = x;
        this.i = i;
    }
    
    /*
    **  Copies a ComplexPoint into a new one.
    */
    public ComplexPoint(ComplexPoint c)
    {   this.x = c.x;
        this.i = c.i;
    }    
    
    /*
    **  Constructs a default ComplexPoint, the origin.
    */    
    public ComplexPoint()
    {   x = 0;
        i = 0;
    }
    
    /*
    **  Sets the location of an existing ComplexPoint.
    **  You can also set the coordinates directly, they're public.
    */
    public void Set(double x, double i)
    {   this.x = x;
        this.i = i;
    } 
    
    /*
    **  Squares the ComplexPoint.
    */
    public void Square()
    {   double a = x;
        double b = i;
        x = a * a - b * b;
        i = 2 * a * b;
    }
    
    /*
    **  Adds a ComplexPoint to this one.
    */
    public void AddPoint(ComplexPoint c)
    {   x = x + c.x;
        i = i + c.i;
    } 
    
    /*
    **  Multiplies this ComplexPoint by another one.
    */
    public void MultiplyPoint(ComplexPoint c)
    {   double newx = c.x * x - c.i * i;
        double newi = c.i * x + c.x * i;
        x = newx;
        i = newi;
    }
    
    /*
    **  Subtracts a ComplexPoint from this one.
    */
    public void SubtractPoint(ComplexPoint c)
    {   x = x - c.x;
        i = i - c.i;
    } 
    
    /*
    **  Computes square of distance from the origin.
    */
    public double RadSqr()
    {   double r = x * x + i * i;
        return r;
    } 
    
    /*
    **  Typical Mandelbrot-set routine, returns an integer from 0 to MAX.
    **  Value set to MAX for points in the set.
    **  Value represents the "dwell" for points not in the set.
    */
    public int MandelBrot(int max)
    {   ComplexPoint old = new ComplexPoint(x, i);
        int ctr = 0;    
        while ((this.RadSqr() < 4) && (++ctr < max))
        {   this.Square(); 
            this.AddPoint(old); 
        }    
        return ctr;    
    }    
    
    /*
    **  Binary Mandelbrot-set routine, iterates to MAX.
    **  Returns 'true' for points in the set.
    */
    public boolean MandelBoolean(int max)
    {   ComplexPoint old = new ComplexPoint(x, i);
        int ctr = 0;    
        while ((this.RadSqr() < 4) && (ctr++ < max))
        {   this.Square(); 
            this.AddPoint(old); 
        }    
        if (ctr > max)
            return true;
        else
            return false;
    } 
    
    /*
    **  Real coordinate of ComplexPoint.
    */
    public double x;
    
    /*
    **  Imaginary coordinate of ComplexPoint.
    */
    public double i;

} //Class ComplexPoint
