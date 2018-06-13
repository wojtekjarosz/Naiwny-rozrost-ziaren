package sample;

import javafx.scene.paint.Color;

public class Cell implements Cloneable {

    private boolean alive;
    private int grainType;
    private Color typeCOlor;

    public Cell(boolean alive, int grainType) {
        this.setAlive(alive);
        this.setGrainType(grainType);
    }


    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getGrainType() {
        return grainType;
    }

    public void setGrainType(int grainType) {
        this.grainType = grainType;
    }

    public Color getTypeCOlor() {
        return typeCOlor;
    }

    public void setTypeCOlor(Color typeCOlor) {
        this.typeCOlor = typeCOlor;
    }

    @Override
    public Cell clone() {
        return new Cell(alive, grainType);
    }

    public void changeState(int neighboursCount, int type) {

        if (!alive) {
            if(neighboursCount>0){
                alive=true;
                grainType =type;
            }

        }

    }

}