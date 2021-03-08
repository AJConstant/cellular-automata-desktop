package domain.automata_model;

public enum UniverseSize {

    TINY(10, 10, "Tiny (10 x 10)"),
    SMALL(50, 50, "Small (50 x 50"),
    MEDIUM(100, 100, "Medium (100 x 100)"),
    STANDARD_GENERATE(500, 500, "Generation Standard (500 x 500)"),
    STANDARD_VISUALIZE(500, 250, "Visualization Standard (500 x 250)"),
    LARGE(1000, 1000, "Large (1000 x 1000)"),
    HUGE(2500, 2500, "Huge (2500 x 2500)"),
    LARGEST(5000, 5000, "Largest (5000 x 5000)");

    private int width;
    private int height;
    private String displayName;

    UniverseSize(int width, int height, String displayName){
        this.displayName = displayName;
        this.width = width;
        this.height = height;
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public String getDisplayName(){
        return this.displayName;
    }

}
