package settings;

public class Settings {
    private static Palette activePalette = Palette.GREEN_BLACK;

    public static Palette getActivePalette(){
        return activePalette;
    }

    public static void setActivePalette(Palette p){
        activePalette = p;
    }
}
