package mchorse.bbs_mod.utils.interps;

import mchorse.bbs_mod.utils.interps.types.CustomInterp;
import mchorse.bbs_mod.utils.interps.types.CustomInterp.ControlPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Examples of custom interpolation curves
 * These can be used as presets for common animation patterns
 */
public class CustomCurvePresets
{
    /**
     * Creates a smooth S-curve (ease in and out)
     */
    public static CustomInterp createSmoothS()
    {
        List<ControlPoint> points = new ArrayList<>();
        points.add(new ControlPoint(0.0, 0.0));
        points.add(new ControlPoint(0.25, 0.1));
        points.add(new ControlPoint(0.5, 0.5));
        points.add(new ControlPoint(0.75, 0.9));
        points.add(new ControlPoint(1.0, 1.0));
        
        return new CustomInterp("smooth_s", "Smooth S-Curve", points);
    }

    /**
     * Creates a bounce effect
     */
    public static CustomInterp createBounce()
    {
        List<ControlPoint> points = new ArrayList<>();
        points.add(new ControlPoint(0.0, 0.0));
        points.add(new ControlPoint(0.4, 0.8));
        points.add(new ControlPoint(0.5, 0.9));
        points.add(new ControlPoint(0.6, 0.85));
        points.add(new ControlPoint(0.75, 0.95));
        points.add(new ControlPoint(0.85, 0.98));
        points.add(new ControlPoint(1.0, 1.0));
        
        return new CustomInterp("bounce", "Bounce", points);
    }

    /**
     * Creates a sharp acceleration at the start
     */
    public static CustomInterp createQuickStart()
    {
        List<ControlPoint> points = new ArrayList<>();
        points.add(new ControlPoint(0.0, 0.0));
        points.add(new ControlPoint(0.2, 0.7));
        points.add(new ControlPoint(0.5, 0.85));
        points.add(new ControlPoint(1.0, 1.0));
        
        return new CustomInterp("quick_start", "Quick Start", points);
    }

    /**
     * Creates a slow start with sharp end
     */
    public static CustomInterp createSlowStart()
    {
        List<ControlPoint> points = new ArrayList<>();
        points.add(new ControlPoint(0.0, 0.0));
        points.add(new ControlPoint(0.5, 0.15));
        points.add(new ControlPoint(0.8, 0.3));
        points.add(new ControlPoint(1.0, 1.0));
        
        return new CustomInterp("slow_start", "Slow Start", points);
    }

    /**
     * Creates a wave pattern
     */
    public static CustomInterp createWave()
    {
        List<ControlPoint> points = new ArrayList<>();
        points.add(new ControlPoint(0.0, 0.0));
        points.add(new ControlPoint(0.2, 0.4));
        points.add(new ControlPoint(0.4, 0.3));
        points.add(new ControlPoint(0.6, 0.7));
        points.add(new ControlPoint(0.8, 0.6));
        points.add(new ControlPoint(1.0, 1.0));
        
        return new CustomInterp("wave", "Wave", points);
    }

    /**
     * Creates a step pattern with smooth transitions
     */
    public static CustomInterp createSteps()
    {
        List<ControlPoint> points = new ArrayList<>();
        points.add(new ControlPoint(0.0, 0.0));
        points.add(new ControlPoint(0.2, 0.25));
        points.add(new ControlPoint(0.3, 0.25));
        points.add(new ControlPoint(0.5, 0.5));
        points.add(new ControlPoint(0.6, 0.5));
        points.add(new ControlPoint(0.8, 0.75));
        points.add(new ControlPoint(0.9, 0.75));
        points.add(new ControlPoint(1.0, 1.0));
        
        return new CustomInterp("steps", "Steps", points);
    }

    /**
     * Creates an overshoot effect (goes beyond 1.0 then settles)
     * Note: Values are clamped to [0, 1] in ControlPoint constructor
     * This creates an approximation of overshoot within bounds
     */
    public static CustomInterp createOvershoot()
    {
        List<ControlPoint> points = new ArrayList<>();
        points.add(new ControlPoint(0.0, 0.0));
        points.add(new ControlPoint(0.6, 0.85));
        points.add(new ControlPoint(0.8, 0.95));
        points.add(new ControlPoint(0.9, 0.98));
        points.add(new ControlPoint(1.0, 1.0));
        
        return new CustomInterp("overshoot", "Overshoot", points);
    }

    /**
     * Register all presets (optional - for easy access)
     */
    public static void registerPresets()
    {
        CustomCurveManager manager = CustomCurveManager.getInstance();
        
        // Only register if they don't exist
        if (!manager.getCustomCurves().containsKey("smooth_s"))
        {
            manager.saveCurve(createSmoothS());
        }
        if (!manager.getCustomCurves().containsKey("bounce"))
        {
            manager.saveCurve(createBounce());
        }
        if (!manager.getCustomCurves().containsKey("quick_start"))
        {
            manager.saveCurve(createQuickStart());
        }
        if (!manager.getCustomCurves().containsKey("slow_start"))
        {
            manager.saveCurve(createSlowStart());
        }
        if (!manager.getCustomCurves().containsKey("wave"))
        {
            manager.saveCurve(createWave());
        }
        if (!manager.getCustomCurves().containsKey("steps"))
        {
            manager.saveCurve(createSteps());
        }
        if (!manager.getCustomCurves().containsKey("overshoot"))
        {
            manager.saveCurve(createOvershoot());
        }
    }
}
