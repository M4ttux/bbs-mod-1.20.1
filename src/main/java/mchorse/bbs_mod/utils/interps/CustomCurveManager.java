package mchorse.bbs_mod.utils.interps;

import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.data.types.BaseType;
import mchorse.bbs_mod.data.types.ListType;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.data.types.StringType;
import mchorse.bbs_mod.data.storage.DataStorage;
import mchorse.bbs_mod.utils.interps.types.CustomInterp;
import mchorse.bbs_mod.utils.interps.types.CustomInterp.ControlPoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager for custom interpolation curves
 * Handles saving, loading, and managing user-created curves
 */
public class CustomCurveManager
{
    private static final String CUSTOM_CURVES_FOLDER = "custom_curves";
    private static CustomCurveManager instance;
    
    private Map<String, CustomInterp> customCurves = new HashMap<>();
    private File curvesFolder;

    private CustomCurveManager()
    {
        this.curvesFolder = new File(BBSMod.getSettingsFolder(), CUSTOM_CURVES_FOLDER);
        
        if (!this.curvesFolder.exists())
        {
            this.curvesFolder.mkdirs();
        }
        
        this.loadAllCurves();
    }

    public static CustomCurveManager getInstance()
    {
        if (instance == null)
        {
            instance = new CustomCurveManager();
        }
        
        return instance;
    }

    public Map<String, CustomInterp> getCustomCurves()
    {
        return new HashMap<>(this.customCurves);
    }

    public CustomInterp getCurve(String key)
    {
        return this.customCurves.get(key);
    }

    public void saveCurve(CustomInterp curve)
    {
        String key = curve.getKey();
        String name = curve.getName();
        
        // Generate key from name if not set
        if (key == null || key.isEmpty())
        {
            key = this.generateKey(name);
            curve.setKey(key);
        }
        
        // Update existing curve or add new one
        this.customCurves.put(key, curve);
        this.saveToFile(curve);
        
        // Update in the main interpolation map
        Interpolations.MAP.put(key, curve);
    }
    
    public String generateKey(String name)
    {
        if (name == null || name.isEmpty())
        {
            name = "custom";
        }
        
        // Convert name to valid key (lowercase, replace spaces with underscores)
        String baseKey = name.toLowerCase().replaceAll("[^a-z0-9_]", "_");
        
        // Check if key already exists, add number suffix if needed
        String key = baseKey;
        int counter = 1;
        
        while (this.customCurves.containsKey(key))
        {
            key = baseKey + "_" + counter;
            counter++;
        }
        
        return key;
    }

    public void deleteCurve(String key)
    {
        this.customCurves.remove(key);
        Interpolations.MAP.remove(key);
        
        File file = new File(this.curvesFolder, key + ".dat");
        if (file.exists())
        {
            file.delete();
        }
    }

    public void renameCurve(String oldKey, String newKey)
    {
        CustomInterp curve = this.customCurves.remove(oldKey);
        
        if (curve != null)
        {
            this.customCurves.put(newKey, curve);
            Interpolations.MAP.remove(oldKey);
            Interpolations.MAP.put(newKey, curve);
            
            File oldFile = new File(this.curvesFolder, oldKey + ".dat");
            File newFile = new File(this.curvesFolder, newKey + ".dat");
            
            if (oldFile.exists())
            {
                oldFile.renameTo(newFile);
            }
        }
    }

    private void saveToFile(CustomInterp curve)
    {
        try
        {
            File file = new File(this.curvesFolder, curve.getKey() + ".dat");
            MapType data = this.serializeCurve(curve);
            
            DataStorage.writeToStream(new FileOutputStream(file), data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadAllCurves()
    {
        if (!this.curvesFolder.exists())
        {
            return;
        }
        
        File[] files = this.curvesFolder.listFiles((dir, name) -> name.endsWith(".dat"));
        
        if (files == null)
        {
            return;
        }
        
        for (File file : files)
        {
            try
            {
                BaseType type = DataStorage.readFromStream(new FileInputStream(file));
                
                if (type != null && type.isMap())
                {
                    CustomInterp curve = this.deserializeCurve(type.asMap());
                    
                    if (curve != null)
                    {
                        this.customCurves.put(curve.getKey(), curve);
                        Interpolations.MAP.put(curve.getKey(), curve);
                    }
                }
            }
            catch (Exception e)
            {
                System.err.println("Failed to load custom curve: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    private MapType serializeCurve(CustomInterp curve)
    {
        MapType data = new MapType();
        
        data.put("key", new StringType(curve.getKey()));
        data.put("name", new StringType(curve.getName()));
        
        ListType pointsList = new ListType();
        for (ControlPoint point : curve.getPoints())
        {
            MapType pointData = new MapType();
            pointData.putDouble("x", point.x);
            pointData.putDouble("y", point.y);
            pointsList.add(pointData);
        }
        
        data.put("points", pointsList);
        
        return data;
    }

    private CustomInterp deserializeCurve(MapType data)
    {
        try
        {
            String key = data.getString("key");
            String name = data.getString("name");
            ListType pointsList = data.getList("points");
            
            List<ControlPoint> points = new ArrayList<>();
            
            for (BaseType pointType : pointsList)
            {
                if (pointType.isMap())
                {
                    MapType pointData = pointType.asMap();
                    double x = pointData.getDouble("x");
                    double y = pointData.getDouble("y");
                    points.add(new ControlPoint(x, y));
                }
            }
            
            return new CustomInterp(key, name, points);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void reload()
    {
        this.customCurves.clear();
        
        // Remove custom curves from main map
        List<String> toRemove = new ArrayList<>();
        for (String key : Interpolations.MAP.keySet())
        {
            if (Interpolations.MAP.get(key) instanceof CustomInterp)
            {
                toRemove.add(key);
            }
        }
        
        for (String key : toRemove)
        {
            Interpolations.MAP.remove(key);
        }
        
        this.loadAllCurves();
    }
}
