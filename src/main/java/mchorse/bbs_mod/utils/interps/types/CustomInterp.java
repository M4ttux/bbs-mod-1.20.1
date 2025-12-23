package mchorse.bbs_mod.utils.interps.types;

import mchorse.bbs_mod.utils.interps.InterpContext;
import mchorse.bbs_mod.utils.interps.Lerps;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom interpolation that allows users to define their own curve
 * using control points, similar to After Effects
 */
public class CustomInterp extends BaseInterp
{
    private List<ControlPoint> points;
    private String name;
    private String key;

    public CustomInterp(String key, String name)
    {
        super(key, 0);
        this.key = key;
        this.name = name;
        this.points = new ArrayList<>();
        
        // Default points (linear)
        this.points.add(new ControlPoint(0.0, 0.0));
        this.points.add(new ControlPoint(1.0, 1.0));
    }

    public CustomInterp(String key, String name, List<ControlPoint> points)
    {
        super(key, 0);
        this.key = key;
        this.name = name;
        this.points = new ArrayList<>(points);
        
        // Ensure at least 2 points
        if (this.points.size() < 2)
        {
            this.points.clear();
            this.points.add(new ControlPoint(0.0, 0.0));
            this.points.add(new ControlPoint(1.0, 1.0));
        }
        
        this.sortPoints();
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getKey()
    {
        return this.key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public List<ControlPoint> getPoints()
    {
        return new ArrayList<>(this.points);
    }

    public void setPoints(List<ControlPoint> points)
    {
        this.points = new ArrayList<>(points);
        this.sortPoints();
    }

    public void addPoint(double x, double y)
    {
        this.points.add(new ControlPoint(x, y));
        this.sortPoints();
    }

    public void removePoint(int index)
    {
        if (this.points.size() > 2 && index >= 0 && index < this.points.size())
        {
            this.points.remove(index);
        }
    }

    public void updatePoint(int index, double x, double y)
    {
        if (index >= 0 && index < this.points.size())
        {
            this.points.get(index).x = x;
            this.points.get(index).y = y;
            this.sortPoints();
        }
    }

    private void sortPoints()
    {
        this.points.sort((a, b) -> Double.compare(a.x, b.x));
    }

    @Override
    public double interpolate(InterpContext context)
    {
        double x = context.x;
        
        if (this.points.isEmpty())
        {
            return Lerps.lerp(context.a, context.b, x);
        }

        // Find the two points that surround x
        int leftIndex = -1;
        int rightIndex = -1;

        for (int i = 0; i < this.points.size(); i++)
        {
            ControlPoint point = this.points.get(i);
            
            if (point.x <= x)
            {
                leftIndex = i;
            }
            
            if (point.x >= x && rightIndex == -1)
            {
                rightIndex = i;
            }
        }

        double y;

        // Handle edge cases
        if (leftIndex == -1)
        {
            y = this.points.get(0).y;
        }
        else if (rightIndex == -1)
        {
            y = this.points.get(this.points.size() - 1).y;
        }
        else if (leftIndex == rightIndex)
        {
            y = this.points.get(leftIndex).y;
        }
        else
        {
            // Interpolate between the two points
            ControlPoint left = this.points.get(leftIndex);
            ControlPoint right = this.points.get(rightIndex);
            
            double localX = (x - left.x) / (right.x - left.x);
            
            // Check if both points are smooth (have handles)
            if (left.smooth && right.smooth)
            {
                // Use Cubic Bezier interpolation (like After Effects)
                // The handles represent control points in Bezier space
                
                // P0 = left point
                // P1 = left point + outHandle (control point 1)
                // P2 = right point + inHandle (control point 2)  
                // P3 = right point
                
                // Calculate absolute positions of control points
                double p0x = left.x;
                double p0y = left.y;
                
                double p1x = left.x + left.outTangentX;
                double p1y = left.y + left.outTangentY;
                
                double p2x = right.x + right.inTangentX;
                double p2y = right.y + right.inTangentY;
                
                double p3x = right.x;
                double p3y = right.y;
                
                // Find the t parameter that corresponds to our target x value
                // We need to solve: x = bezierX(t) for t, then use that t to get bezierY(t)
                // Use Newton-Raphson method for better accuracy
                
                double targetX = x; // The X we're looking for
                double t = localX; // Initial guess
                
                // Newton-Raphson iterations to find t
                for (int i = 0; i < 8; i++)
                {
                    double oneMinusT = 1.0 - t;
                    double oneMinusT2 = oneMinusT * oneMinusT;
                    double t2 = t * t;
                    
                    // Calculate current X for this t
                    double currentX = oneMinusT2 * oneMinusT * p0x +
                                     3.0 * oneMinusT2 * t * p1x +
                                     3.0 * oneMinusT * t2 * p2x +
                                     t2 * t * p3x;
                    
                    // Calculate derivative dX/dt
                    double dx = 3.0 * oneMinusT2 * (p1x - p0x) +
                               6.0 * oneMinusT * t * (p2x - p1x) +
                               3.0 * t2 * (p3x - p2x);
                    
                    if (Math.abs(dx) < 0.000001) break;
                    
                    // Newton-Raphson step
                    t = t - (currentX - targetX) / dx;
                    t = Math.max(0.0, Math.min(1.0, t));
                }
                
                // Now calculate Y using the found t
                double oneMinusT = 1.0 - t;
                double oneMinusT2 = oneMinusT * oneMinusT;
                double oneMinusT3 = oneMinusT2 * oneMinusT;
                double t2 = t * t;
                double t3 = t2 * t;
                
                y = oneMinusT3 * p0y + 
                    3.0 * oneMinusT2 * t * p1y + 
                    3.0 * oneMinusT * t2 * p2y + 
                    t3 * p3y;
                
                // Clamp result to valid range
                y = Math.max(0.0, Math.min(1.0, y));
            }
            else
            {
                // Linear interpolation if either point is not smooth
                y = Lerps.lerp(left.y, right.y, localX);
            }
        }

        // Apply the custom curve to the interpolation
        return Lerps.lerp(context.a, context.b, y);
    }

    public CustomInterp copy()
    {
        return new CustomInterp(this.key, this.name, this.points);
    }

    /**
     * Control point for custom interpolation with tangent handles
     */
    public static class ControlPoint
    {
        public double x;
        public double y;
        public double inTangentX;
        public double inTangentY;
        public double outTangentX;
        public double outTangentY;
        public boolean smooth; // true = smooth with handles, false = linear

        public ControlPoint(double x, double y)
        {
            this.x = Math.max(0.0, Math.min(1.0, x));
            this.y = Math.max(0.0, Math.min(1.0, y));
            // Default tangents (horizontal)
            this.inTangentX = -0.1;
            this.inTangentY = 0.0;
            this.outTangentX = 0.1;
            this.outTangentY = 0.0;
            this.smooth = true; // Default to smooth
        }

        public ControlPoint copy()
        {
            ControlPoint cp = new ControlPoint(this.x, this.y);
            cp.inTangentX = this.inTangentX;
            cp.inTangentY = this.inTangentY;
            cp.outTangentX = this.outTangentX;
            cp.outTangentY = this.outTangentY;
            return cp;
        }

        @Override
        public String toString()
        {
            return String.format("(%.3f, %.3f)", this.x, this.y);
        }
    }
}
