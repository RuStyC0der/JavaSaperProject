package lab_5.saper.model;

import java.io.Serializable;

public class Cell implements Serializable {

    protected boolean Flag;
    protected boolean Bomb;
    protected boolean Open;


    public Cell (boolean hasFlag, boolean hasBomb, boolean isOpen){
        this.Flag = hasFlag;
        this.Bomb = hasBomb;
        this.Open = isOpen;
    }

    public Cell (){
        this.Flag = false;
        this.Bomb = false;
        this.Open = false;
    }

    public Cell (Cell cell){
        this.Flag = cell.Flag;
        this.Bomb = cell.Bomb;
        this.Open = cell.Open;
    }

    public void open(){this.Open = true;}
    public boolean isOpen() {return this.Open;}

    public boolean hasFlag() {return this.Flag;}

    public void setFlag(){this.Flag = true;}
    public void unsetFlag(){this.Flag = false;}

    public boolean hasBomb() {return this.Bomb;}
    public void setBomb() {this.Bomb = true;}

    public boolean isDefused(){return this.hasBomb() && this.hasFlag();}

}
