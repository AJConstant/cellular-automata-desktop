package settings;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum Palette {
    GREEN_BLACK("root-green-dark", Color.web("#222629"), Color.web("#6b6e70"), "Green/Black"),
    MATTE_COLORFUL("root-deep-blue", Color.web("#0c0032"), Color.web("#240090"), "Deep Blue"),
    RED_VELVET("root-red-velvet", Color.web("#2f4545"), Color.web("#24305E"), "Red Velvet(Pingas)");

    private Color canvasBackground;
    private Color canvasForeground;
    private String cssName;
    private String displayName;

    Palette(String cssName, Color canvasBackground, Color canvasForeground, String displayName){
        this.canvasBackground = canvasBackground;
        this.canvasForeground = canvasForeground;
        this.cssName = cssName;
        this.displayName = displayName;
    }

    public Color getCanvasBackground() {
        return this.canvasBackground;
    }

    public Color getCanvasForeground() {
        return this.canvasForeground;
    }

    public String getCssName() {
        return this.cssName;
    }

    public String getDisplayName() { return this.displayName; }
}
