package lab_5.saper.view;


import lab_5.saper.model.Board;
import lab_5.saper.model.Cell;
import lab_5.saper.model.GameOverException;
import lab_5.saper.model.YouWinException;

public class console_viev {

    private static String cellToString(Cell cell, int countOfBomb){

        if (cell.hasBomb()){
            return "[B]";
        }
//        if (cell.isOpen() && cell.hasBomb()){
//            return "[B]";
//        }

        if (cell.hasFlag() && !cell.isOpen()){
            return "[F]";
        }

        if (!cell.isOpen()){
            return "[*]";
        }
        if (countOfBomb > 0){
            return "[" + countOfBomb + "]";
        }
        return "[ ]";

    }




    private static String boardToString(Board board){
        StringBuilder b = new StringBuilder();
        Cell[][] bo = board.getBoardClone();
        for (int i = 0; i < bo.length; i++) {
            for (int j = 0; j < bo[i].length; j++) {
                b.append(cellToString(bo[i][j], board.howManyBombsAround(i, j)));
            }
            b.append("\n");
        }
        return b.toString();
    }


    public static void main(String[] args) throws GameOverException, YouWinException {
        Board b = new Board(15, 30);
        System.out.println();
        System.out.println(boardToString(b));
        System.out.println(b.howManyBombsAround(0,0));
        b.toggleFlag(1,1);
        b.openCellAction(0,0);
        System.out.println(boardToString(b));

    }

}
