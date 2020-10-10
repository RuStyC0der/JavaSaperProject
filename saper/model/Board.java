package lab_5.saper.model;

// TODO: need singleton

import java.io.*;
import java.util.ArrayList;


public class Board implements Serializable {

    private Cell[][] _board;
    private int size;
    private int bombCount;
    private int flagsCount;
    private int bombsDefused = 0;


    private int[][] aroundPosition = {
        {1, -1},
        {1, 0},
        {1, 1},
        {-1, 1},
        {-1, 0},
        {-1, -1},
        {0, -1},
        {0, 1}
    };

    public Board (int size, int bombCount) {
        this.size = size;
        this.flagsCount = bombCount;
        this.bombCount = bombCount;
        this._board = new Cell[size][size];
        for (int i = 0; i < this._board.length; i++) {
            for (int j = 0; j < this._board[i].length; j++) {
                _board[i][j] = new Cell();
            }
        }
        this.fillBoardWithBombs(bombCount);
    }

    //       сделано так потому что алгоритм с проверкой занята ли ячейка(когда берут две рандомные координаты и
    //       проверяют занята ли уже ячейка по данным координатам, если да то ищем следующую ячейку) работает неоправдано долго
    //       в случае если доступных ячеек почти не осталось, данный алгоритм решает данную проблему хоть и занимает много памяти.
    private void fillBoardWithBombs(int bombCount) {
        ArrayList<Integer[]> pairList = new ArrayList<>(this.size * this.size);
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                Integer[] pair = new Integer[2];
                pair[0] = i;
                pair[1] = j;
                pairList.add(pair);
            }
        }
        //работаем пока остались бомбы или место на доске, не уверен насчёт второго, возможно лучше бросать исключение
        while (bombCount-- > 0 && pairList.size() > 0) {
            Integer[] pair = pairList.remove(getRandomInt(pairList.size()));
            this._board[pair[0]][pair[1]].setBomb();
        }

    }


    private int getRandomInt(int to){
        return (int)((Math.random() * (to)) + 0);}


    public void openCellAction(int x, int y) throws GameOverException {
        if (this._board[x][y].hasBomb() && !this._board[x][y].hasFlag()){
            GameOverAction();
            throw new GameOverException();
        }
        this.cascadeCellOpen(x,y);
    }

    // сделано так в угоду производительности, проще вести счетчик чем каждый раз перебирать доску
    public void toggleFlag(int x, int y) throws YouWinException {
        if (this._board[x][y].hasFlag()) {
            if (this._board[x][y].isDefused()) {
                this.bombsDefused--;
            }
            this._board[x][y].unsetFlag();
            this.flagsCount++;
        } else {
            if (this.flagsCount > 0) {
                this._board[x][y].setFlag();
                this.flagsCount--;
                if (this._board[x][y].isDefused()) {
                    this.bombsDefused++;
                }
            }
        }
        if (this.bombsDefused == this.bombCount){
            throw new YouWinException();
        }
    }



    public int howManyBombsAround(int x, int y) {
        int bombCounter = 0;
        for (int[] pos : aroundPosition) {
            try{
                bombCounter += (this._board[x+pos[0]][y+pos[1]].hasBomb())?1:0;
            }catch(Exception ignored) {}
        }
        return bombCounter;
    }


    private void cascadeCellOpen(int x, int y) {
        if (!this._board[x][y].isOpen() && !this._board[x][y].hasFlag() && !this._board[x][y].hasBomb()) {
            this._board[x][y].open();
            if (this.howManyBombsAround(x, y) == 0){
                for (int[] pos : this.aroundPosition) {
                    if (x + pos[0] < 0 || y + pos[1] < 0 || x + pos[0] >= this.size || y + pos[1] >= this.size){continue;}
                    cascadeCellOpen(x + pos[0], y + pos[1]);
                }
            }
        }
    }



    private void openAllBombs() {
        for (Cell[] cells : this._board) {
            for (Cell cell : cells) {
                if (cell.hasBomb()){
                    cell.open();
                }
            }
        }
    }


    private void GameOverAction(){
        openAllBombs();
    }

    public Cell[][] getBoardClone(){return this._board.clone();}

    public Cell getCellClone(int x , int y){return new Cell(this._board[x][y]);}

    public int getSize(){return this.size;}

    public int getBombCount(){return this.bombCount;}

    public int getFlagsCount(){return this.flagsCount;}

    public void saveBoardToFile(String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.flush();
        oos.close();
    }

    public static Board loadBoardFromFile(String filePath) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
        return (Board)ois.readObject();
    }




    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Board b = new Board(10, 5);
        b.saveBoardToFile("./brd");
        Board b2 = Board.loadBoardFromFile("./brd");
        System.out.println(b2.bombCount);
    }

}
