package mchorse.bbs_mod.ui.framework.elements.overlay;

import mchorse.bbs_mod.graphics.Draw;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs_mod.ui.utils.Area;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.utils.colors.Colors;
import mchorse.bbs_mod.utils.interps.CustomCurveManager;
import mchorse.bbs_mod.utils.interps.types.CustomInterp;
import mchorse.bbs_mod.utils.interps.types.CustomInterp.ControlPoint;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Custom curve editor UI
 * Allows users to create and edit custom interpolation curves visually
 */
public class UICustomCurveEditor extends UIOverlayPanel
{
    private static final int PADDING = 10;
    private static final int BUTTON_HEIGHT = 20;
    private static final int GRAPH_SIZE = 300;
    private static final int POINT_SIZE = 6;
    private static final int HANDLE_SIZE = 4;
    
    private CustomInterp interpolation;
    private List<ControlPoint> points;
    private int selectedPoint = -1;
    private int selectedHandle = -1; // -1 = none, 0 = in tangent, 1 = out tangent
    private boolean dragging = false;
    private boolean draggingHandle = false;
    
    private UITextbox nameField;
    private UIButton saveButton;
    private UIButton cancelButton;
    private UIButton addPointButton;
    private UIButton removePointButton;
    private UIButton resetButton;
    
    private Area graphArea = new Area();
    private Consumer<CustomInterp> callback;
    
    // Animation preview
    private float previewTime = 0;

    public UICustomCurveEditor(CustomInterp interpolation, Consumer<CustomInterp> callback)
    {
        super(UIKeys.CUSTOM_CURVE_EDITOR_TITLE);
        
        this.interpolation = interpolation == null ? new CustomInterp("custom", "Custom Curve") : interpolation.copy();
        this.points = new ArrayList<>(this.interpolation.getPoints());
        this.callback = callback;
        
        this.nameField = new UITextbox(100, (text) -> {});
        this.nameField.setText(this.interpolation.getName());
        
        this.saveButton = new UIButton(UIKeys.GENERAL_SAVE, (b) -> this.save());
        this.cancelButton = new UIButton(UIKeys.GENERAL_CLOSE, (b) -> this.close());
        this.addPointButton = new UIButton(UIKeys.CUSTOM_CURVE_ADD_POINT, (b) -> this.addPoint());
        this.removePointButton = new UIButton(UIKeys.CUSTOM_CURVE_REMOVE_POINT, (b) -> this.removePoint());
        this.resetButton = new UIButton(UIKeys.CUSTOM_CURVE_RESET, (b) -> this.reset());
        
        this.removePointButton.setEnabled(this.points.size() > 2);
        
        this.content.add(this.nameField);
        this.content.add(this.addPointButton, this.removePointButton, this.resetButton);
        this.content.add(this.saveButton, this.cancelButton);
    }
    
    @Override
    protected void onAdd(UIElement parent)
    {
        super.onAdd(parent);
        
        // Force initial resize to properly initialize button positions
        this.resize();
    }

    private void save()
    {
        String name = this.nameField.getText().trim();
        if (name.isEmpty())
        {
            name = "Custom Curve";
        }
        
        this.interpolation.setName(name);
        this.interpolation.setPoints(this.points);
        
        // Generate unique key if needed, or use existing key to overwrite
        if (this.interpolation.getKey() == null || this.interpolation.getKey().isEmpty())
        {
            String generatedKey = CustomCurveManager.getInstance().generateKey(name);
            this.interpolation.setKey(generatedKey);
        }
        
        if (this.callback != null)
        {
            this.callback.accept(this.interpolation);
        }
        
        this.close();
    }

    private void addPoint()
    {
        // Add point in the middle of the curve
        double targetX = 0.5;
        double targetY = this.interpolation.interpolate(0, 1, targetX);
        
        ControlPoint newPoint = new ControlPoint(targetX, targetY);
        
        // Use short horizontal tangents for predictable, smooth continuity
        // This prevents curve breaking even with extreme existing handles
        double horizontalLength = 0.08;
        newPoint.inTangentX = -horizontalLength;
        newPoint.inTangentY = 0.0;
        newPoint.outTangentX = horizontalLength;
        newPoint.outTangentY = 0.0;
        
        this.points.add(newPoint);
        this.points.sort((a, b) -> Double.compare(a.x, b.x));
        this.removePointButton.setEnabled(this.points.size() > 2);
    }

    private void removePoint()
    {
        if (this.selectedPoint >= 0 && this.selectedPoint < this.points.size() && this.points.size() > 2)
        {
            this.points.remove(this.selectedPoint);
            this.selectedPoint = -1;
            this.removePointButton.setEnabled(this.points.size() > 2);
        }
    }

    private void reset()
    {
        this.points.clear();
        this.points.add(new ControlPoint(0.0, 0.0));
        this.points.add(new ControlPoint(1.0, 1.0));
        this.selectedPoint = -1;
        this.removePointButton.setEnabled(false);
    }

    @Override
    public void close()
    {
        super.close();
        this.dragging = false;
    }

    @Override
    public void resize()
    {
        super.resize();
        
        int y = PADDING;
        
        this.nameField.relative(this.content).set(PADDING, y, GRAPH_SIZE, BUTTON_HEIGHT);
        y += BUTTON_HEIGHT + 5;
        
        // Update graph area position relative to content
        this.graphArea.x = this.content.area.x + PADDING;
        this.graphArea.y = this.content.area.y + y;
        this.graphArea.w = GRAPH_SIZE;
        this.graphArea.h = GRAPH_SIZE;
        y += GRAPH_SIZE + PADDING;
        
        int buttonW = (GRAPH_SIZE - PADDING * 2) / 3;
        this.addPointButton.relative(this.content).set(PADDING, y, buttonW, BUTTON_HEIGHT);
        this.removePointButton.relative(this.content).set(PADDING + buttonW + 5, y, buttonW, BUTTON_HEIGHT);
        this.resetButton.relative(this.content).set(PADDING + buttonW * 2 + 10, y, buttonW, BUTTON_HEIGHT);
        y += BUTTON_HEIGHT + 5;
        
        int saveButtonW = (GRAPH_SIZE - 5) / 2;
        this.saveButton.relative(this.content).set(PADDING, y, saveButtonW, BUTTON_HEIGHT);
        this.cancelButton.relative(this.content).set(PADDING + saveButtonW + 5, y, saveButtonW, BUTTON_HEIGHT);
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (super.subMouseClicked(context))
        {
            return true;
        }
        
        if (this.graphArea.isInside(context.mouseX, context.mouseY))
        {
            this.handleGraphClick(context);
            return true;
        }
        
        return false;
    }

    private void handleGraphClick(UIContext context)
    {
        int mx = context.mouseX - this.graphArea.x;
        int my = context.mouseY - this.graphArea.y;
        
        // First check if clicking on tangent handles (only if point is smooth)
        if (this.selectedPoint >= 0 && this.selectedPoint < this.points.size())
        {
            ControlPoint point = this.points.get(this.selectedPoint);
            boolean isFirstPoint = (this.selectedPoint == 0);
            boolean isLastPoint = (this.selectedPoint == this.points.size() - 1);
            
            // Only check handles if the point is smooth
            if (point.smooth)
            {
                int px = (int) (point.x * this.graphArea.w);
                int py = (int) ((1 - point.y) * this.graphArea.h);
                
                // Check in tangent handle (yellow) - skip for first point
                if (!isFirstPoint)
                {
                    int inHandleX = (int) ((point.x + point.inTangentX) * this.graphArea.w);
                    int inHandleY = (int) ((1 - (point.y + point.inTangentY)) * this.graphArea.h);
                
                    if (Math.abs(mx - inHandleX) <= HANDLE_SIZE && Math.abs(my - inHandleY) <= HANDLE_SIZE)
                    {
                        this.selectedHandle = 0;
                        this.draggingHandle = true;
                        return;
                    }
                }
                
                // Check out tangent handle (cyan) - skip for last point
                if (!isLastPoint)
                {
                    int outHandleX = (int) ((point.x + point.outTangentX) * this.graphArea.w);
                    int outHandleY = (int) ((1 - (point.y + point.outTangentY)) * this.graphArea.h);
                
                    if (Math.abs(mx - outHandleX) <= HANDLE_SIZE && Math.abs(my - outHandleY) <= HANDLE_SIZE)
                    {
                        this.selectedHandle = 1;
                        this.draggingHandle = true;
                        return;
                    }
                }
            }
        }
        
        // Check if clicking on a point
        for (int i = 0; i < this.points.size(); i++)
        {
            ControlPoint point = this.points.get(i);
            int px = (int) (point.x * this.graphArea.w);
            int py = (int) ((1 - point.y) * this.graphArea.h);
            
            if (Math.abs(mx - px) <= POINT_SIZE && Math.abs(my - py) <= POINT_SIZE)
            {
                // Check if ALT is pressed - toggle smooth/linear mode
                if (Screen.hasAltDown())
                {
                    point.smooth = !point.smooth;
                    return;
                }
                
                this.selectedPoint = i;
                this.selectedHandle = -1;
                this.dragging = true;
                return;
            }
        }
        
        this.selectedPoint = -1;
        this.selectedHandle = -1;
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        this.dragging = false;
        this.draggingHandle = false;
        
        return super.subMouseReleased(context);
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);
        
        if (this.dragging && this.selectedPoint >= 0 && this.selectedPoint < this.points.size())
        {
            int mx = context.mouseX - this.graphArea.x;
            int my = context.mouseY - this.graphArea.y;
            
            double x = Math.max(0.0, Math.min(1.0, mx / (double) this.graphArea.w));
            double y = Math.max(0.0, Math.min(1.0, 1.0 - my / (double) this.graphArea.h));
            
            // Prevent moving first and last point on X axis
            if (this.selectedPoint == 0)
            {
                x = 0.0;
            }
            else if (this.selectedPoint == this.points.size() - 1)
            {
                x = 1.0;
            }
            
            this.points.get(this.selectedPoint).x = x;
            this.points.get(this.selectedPoint).y = y;
            
            if (this.selectedPoint > 0 && this.selectedPoint < this.points.size() - 1)
            {
                this.points.sort((a, b) -> Double.compare(a.x, b.x));
            }
        }
        else if (this.draggingHandle && this.selectedPoint >= 0 && this.selectedPoint < this.points.size())
        {
            ControlPoint point = this.points.get(this.selectedPoint);
            int mx = context.mouseX - this.graphArea.x;
            int my = context.mouseY - this.graphArea.y;
            
            // Calculate handle position relative to point (in normalized 0-1 space)
            double mouseXNorm = mx / (double) this.graphArea.w;
            double mouseYNorm = 1.0 - (my / (double) this.graphArea.h);  // Invert Y
            
            // Calculate handle offset from point
            double handleX = mouseXNorm - point.x;
            double handleY = mouseYNorm - point.y;
            
            // 180-degree limit: prevent handles from flipping to the opposite side
            // Get the original handle direction to maintain hemisphere
            double originalHandleX = (this.selectedHandle == 0) ? point.inTangentX : point.outTangentX;
            double originalHandleY = (this.selectedHandle == 0) ? point.inTangentY : point.outTangentY;
            
            // Calculate dot product to check if new handle is in opposite hemisphere (> 180 degrees)
            double dotProduct = handleX * originalHandleX + handleY * originalHandleY;
            
            // If dot product is negative and we're not near zero, clamp to 90 degrees from original
            if (dotProduct < 0 && (Math.abs(originalHandleX) > 0.01 || Math.abs(originalHandleY) > 0.01))
            {
                // Project onto the perpendicular plane (limit to 90 degrees from original)
                double perpX = -originalHandleY;
                double perpY = originalHandleX;
                
                // Determine which perpendicular direction is closer
                double dotPerp1 = handleX * perpX + handleY * perpY;
                
                if (dotPerp1 > 0)
                {
                    handleX = perpX;
                    handleY = perpY;
                }
                else
                {
                    handleX = -perpX;
                    handleY = -perpY;
                }
                
                // Normalize and scale to original magnitude
                double currentMag = Math.sqrt(handleX * handleX + handleY * handleY);
                double targetMag = Math.sqrt((mouseXNorm - point.x) * (mouseXNorm - point.x) + 
                                            (mouseYNorm - point.y) * (mouseYNorm - point.y));
                if (currentMag > 0.001)
                {
                    handleX = (handleX / currentMag) * targetMag;
                    handleY = (handleY / currentMag) * targetMag;
                }
            }
            
            // CTRL key: lock to axis (X or Y based on which has more movement)
            boolean ctrlPressed = Screen.hasControlDown();
            if (ctrlPressed)
            {
                if (Math.abs(handleX) > Math.abs(handleY))
                {
                    // Lock to X axis
                    handleY = 0.0;
                }
                else
                {
                    // Lock to Y axis
                    handleX = 0.0;
                }
            }
            
            // Corner points can stretch more (1.5 vs 0.7)
            boolean isCornerPoint = (this.selectedPoint == 0 || this.selectedPoint == this.points.size() - 1);
            double maxLimit = isCornerPoint ? 1.5 : 0.7;
            
            // For corner points, ensure handles don't go outside the graph (0-1 range)
            if (isCornerPoint)
            {
                // Calculate absolute position of handle
                double handleAbsX = point.x + handleX;
                double handleAbsY = point.y + handleY;
                
                // Clamp to graph bounds
                handleAbsX = Math.max(0.0, Math.min(1.0, handleAbsX));
                handleAbsY = Math.max(0.0, Math.min(1.0, handleAbsY));
                
                // Recalculate relative offset
                handleX = handleAbsX - point.x;
                handleY = handleAbsY - point.y;
            }
            else
            {
                // Limit handle length for non-corner points
                handleX = Math.max(-maxLimit, Math.min(maxLimit, handleX));
                handleY = Math.max(-maxLimit, Math.min(maxLimit, handleY));
            }
            
            // Check if SHIFT is pressed for independent handle movement
            boolean shiftPressed = Screen.hasShiftDown();
            
            if (this.selectedHandle == 0)
            {
                // In tangent
                point.inTangentX = handleX;
                point.inTangentY = handleY;
                
                // Mirror to out tangent if SHIFT is not pressed (linked mode)
                if (!shiftPressed)
                {
                    point.outTangentX = -handleX;
                    point.outTangentY = -handleY;
                }
            }
            else if (this.selectedHandle == 1)
            {
                // Out tangent
                point.outTangentX = handleX;
                point.outTangentY = handleY;
                
                // Mirror to in tangent if SHIFT is not pressed (linked mode)
                if (!shiftPressed)
                {
                    point.inTangentX = -handleX;
                    point.inTangentY = -handleY;
                }
            }
        }
        
        // Update animation preview
        this.previewTime += 0.01F;
        if (this.previewTime > 1.0F)
        {
            this.previewTime = 0.0F;
        }
        
        this.renderGraph(context);
    }

    private void renderGraph(UIContext context)
    {
        int x = this.graphArea.x;
        int y = this.graphArea.y;
        int w = this.graphArea.w;
        int h = this.graphArea.h;
        
        // Skip rendering if dimensions are invalid
        if (w <= 0 || h <= 0)
        {
            return;
        }
        
        // Background
        context.batcher.box(x, y, x + w, y + h, Colors.A50);
        
        // Grid lines (4x4 grid like After Effects)
        int gridColor = 0x33FFFFFF; // Semi-transparent white
        int gridDivisions = 4;
        
        // Vertical grid lines
        for (int i = 1; i < gridDivisions; i++)
        {
            int gridX = x + (i * w / gridDivisions);
            context.batcher.box(gridX, y, gridX + 1, y + h, gridColor);
        }
        
        // Horizontal grid lines
        for (int i = 1; i < gridDivisions; i++)
        {
            int gridY = y + (i * h / gridDivisions);
            context.batcher.box(x, gridY, x + w, gridY + 1, gridColor);
        }
        
        // Curve - draw as continuous line
        if (this.points.size() >= 2)
        {
            CustomInterp tempInterp = new CustomInterp("temp", "temp", this.points);
            int steps = w; // One pixel per step for smooth line
            
            int prevX = x;
            int prevY = y + (int) ((1 - tempInterp.interpolate(0, 1, 0)) * h);
            
            for (int i = 1; i <= steps; i++)
            {
                double t = i / (double) steps;
                double yValue = tempInterp.interpolate(0, 1, t);
                
                int currentX = x + i;
                int currentY = y + (int) ((1 - yValue) * h);
                
                // Draw line from previous point to current point
                this.drawLine(context, prevX, prevY, currentX, currentY, Colors.WHITE);
                
                prevX = currentX;
                prevY = currentY;
            }
        }
        
        // Control points and tangent handles
        for (int i = 0; i < this.points.size(); i++)
        {
            ControlPoint point = this.points.get(i);
            int px = x + (int) (point.x * w);
            int py = y + (int) ((1 - point.y) * h);
            
            boolean isSelected = this.selectedPoint == i;
            boolean isFirstPoint = (i == 0);
            boolean isLastPoint = (i == this.points.size() - 1);
            
            // Draw tangent handles if this point is selected AND smooth
            if (isSelected && point.smooth)
            {
                // First point (bottom-left) only shows OUT handle (cyan)
                // Last point (top-right) only shows IN handle (yellow)
                // Middle points show both handles
                
                // In tangent handle (yellow) - skip for first point
                if (!isFirstPoint)
                {
                    int inHandleX = x + (int) ((point.x + point.inTangentX) * w);
                    int inHandleY = y + (int) ((1 - (point.y + point.inTangentY)) * h);
                
                // Draw line from point to in handle (thicker line)
                for (int offset = -1; offset <= 1; offset++)
                {
                    this.drawLine(context, px, py + offset, inHandleX, inHandleY + offset, 0xFF888800);
                }
                
                // Draw in handle (yellow circle/square)
                int handleSize = HANDLE_SIZE + 1;
                context.batcher.box(inHandleX - handleSize, inHandleY - handleSize, 
                                   inHandleX + handleSize, inHandleY + handleSize, 0xFFFFFF00);
                // Handle border
                context.batcher.box(inHandleX - handleSize - 1, inHandleY - handleSize - 1, 
                                   inHandleX + handleSize + 1, inHandleY - handleSize, Colors.A50);
                context.batcher.box(inHandleX - handleSize - 1, inHandleY + handleSize, 
                                   inHandleX + handleSize + 1, inHandleY + handleSize + 1, Colors.A50);
                context.batcher.box(inHandleX - handleSize - 1, inHandleY - handleSize, 
                                   inHandleX - handleSize, inHandleY + handleSize, Colors.A50);
                    context.batcher.box(inHandleX + handleSize, inHandleY - handleSize, 
                                       inHandleX + handleSize + 1, inHandleY + handleSize, Colors.A50);
                }
                
                // Out tangent handle (cyan) - skip for last point
                if (!isLastPoint)
                {
                    int outHandleX = x + (int) ((point.x + point.outTangentX) * w);
                    int outHandleY = y + (int) ((1 - (point.y + point.outTangentY)) * h);
                
                    // Draw line from point to out handle (thicker line)
                    for (int offset = -1; offset <= 1; offset++)
                    {
                        this.drawLine(context, px, py + offset, outHandleX, outHandleY + offset, 0xFF008888);
                    }
                    
                    // Draw out handle (cyan circle/square)
                    int handleSize = HANDLE_SIZE + 1;
                    context.batcher.box(outHandleX - handleSize, outHandleY - handleSize, 
                                       outHandleX + handleSize, outHandleY + handleSize, 0xFF00FFFF);
                    // Handle border
                    context.batcher.box(outHandleX - handleSize - 1, outHandleY - handleSize - 1, 
                                       outHandleX + handleSize + 1, outHandleY - handleSize, Colors.A50);
                    context.batcher.box(outHandleX - handleSize - 1, outHandleY + handleSize, 
                                       outHandleX + handleSize + 1, outHandleY + handleSize + 1, Colors.A50);
                    context.batcher.box(outHandleX - handleSize - 1, outHandleY - handleSize, 
                                       outHandleX - handleSize, outHandleY + handleSize, Colors.A50);
                    context.batcher.box(outHandleX + handleSize, outHandleY - handleSize, 
                                       outHandleX + handleSize + 1, outHandleY + handleSize, Colors.A50);
                }
            }
            
            // Draw control point
            int pointSize = isSelected ? POINT_SIZE + 3 : POINT_SIZE;
            int color = isSelected ? 0xFFFF0000 : 0xFFFFFFFF; // Bright red for selected, white for normal
            context.batcher.box(px - pointSize, py - pointSize, px + pointSize, py + pointSize, color);
            
            // Point border (black outline)
            int borderThickness = 1;
            context.batcher.box(px - pointSize - borderThickness, py - pointSize - borderThickness, 
                               px + pointSize + borderThickness, py - pointSize, 0xFF000000);
            context.batcher.box(px - pointSize - borderThickness, py + pointSize, 
                               px + pointSize + borderThickness, py + pointSize + borderThickness, 0xFF000000);
            context.batcher.box(px - pointSize - borderThickness, py - pointSize, 
                               px - pointSize, py + pointSize, 0xFF000000);
            context.batcher.box(px + pointSize, py - pointSize, 
                               px + pointSize + borderThickness, py + pointSize, 0xFF000000);
        }
        
        // Draw animation preview
        if (this.points.size() >= 2)
        {
            CustomInterp tempInterp = new CustomInterp("temp", "temp", this.points);
            double previewY = tempInterp.interpolate(0, 1, this.previewTime);
            
            int previewX = x + (int) (this.previewTime * w);
            int previewYPos = y + (int) ((1 - previewY) * h);
            
            // Draw preview ball
            int ballSize = 8;
            context.batcher.box(previewX - ballSize, previewYPos - ballSize, 
                               previewX + ballSize, previewYPos + ballSize, Colors.GREEN);
            // Ball border
            context.batcher.box(previewX - ballSize - 1, previewYPos - ballSize - 1, 
                               previewX + ballSize + 1, previewYPos - ballSize, Colors.A75);
            context.batcher.box(previewX - ballSize - 1, previewYPos + ballSize, 
                               previewX + ballSize + 1, previewYPos + ballSize + 1, Colors.A75);
            context.batcher.box(previewX - ballSize - 1, previewYPos - ballSize, 
                               previewX - ballSize, previewYPos + ballSize, Colors.A75);
            context.batcher.box(previewX + ballSize, previewYPos - ballSize, 
                               previewX + ballSize + 1, previewYPos + ballSize, Colors.A75);
        }
        
        // Border
        context.batcher.box(x, y, x + w, y + 1, Colors.WHITE);
        context.batcher.box(x, y + h - 1, x + w, y + h, Colors.WHITE);
        context.batcher.box(x, y, x + 1, y + h, Colors.WHITE);
        context.batcher.box(x + w - 1, y, x + w, y + h, Colors.WHITE);
    }
    
    private void drawLine(UIContext context, int x1, int y1, int x2, int y2, int color)
    {
        // Bresenham's line algorithm for smooth lines
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        
        int x = x1;
        int y = y1;
        
        while (true)
        {
            context.batcher.box(x, y, x + 1, y + 1, color);
            
            if (x == x2 && y == y2)
            {
                break;
            }
            
            int e2 = 2 * err;
            
            if (e2 > -dy)
            {
                err -= dy;
                x += sx;
            }
            
            if (e2 < dx)
            {
                err += dx;
                y += sy;
            }
        }
    }
}
