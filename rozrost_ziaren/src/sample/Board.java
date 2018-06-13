package sample;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Board {

    private Cell[][] cells;
    private int width;
    private int height;
    private boolean period;
    private String neighbourhoodType;
    Random rand;
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new Cell[width][height];
        rand = new Random();
        resetAll();
    }

    // reset calej planszy
    private void resetAll() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new Cell(false, 0);
            }
        }
    }

    // ustawienie wartosci komorki
    public void setCellValue(int x, int y, boolean isAlive) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            cells[x][y].setAlive(isAlive);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    //ustawianie typu ziarna
    public void setCellGrainType(int x, int y, int grainType) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            cells[x][y].setGrainType(grainType);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public void setCellColor(int x, int y, Color typeColor) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            cells[x][y].setTypeCOlor(typeColor);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    // pobranie wartosci komorki
    public boolean getCellValue(int x, int y) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            return cells[x][y].isAlive();
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    // pobranie typu ziarna
    public int getCellGrainType(int x, int y) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            return cells[x][y].getGrainType();
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public Color getCellColor(int x, int y) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            return cells[x][y].getTypeCOlor();
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public boolean isPeriod() {
        return period;
    }

    public void setNeighbourhoodType(String neighbourhoodType) {
        this.neighbourhoodType = neighbourhoodType;
    }

    public void setPeriod(boolean period) {
        this.period = period;
    }

    // wykonanie aktualnej tury
    public void nextCycle() {
        int[] cellInfo;//= new int[2];
        // kopiowanie aktualnego stanu
        Cell[][] newBoard = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newBoard[i][j] = cells[i][j].clone();
            }
        }

        // wykonanie akcji dla danej komorki
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cellInfo = getGrainsGrowthType(i, j);
                newBoard[i][j].changeState(cellInfo[0],cellInfo[1]);
            }
        }

        cells = newBoard;
    }

    // wybranie typu rozrostu
    public int[] getGrainsGrowthType(int i, int j) {
        int [] info = new int[2];
        Map<Integer,Integer> neighbours = new HashMap<>();

        int type=0;
        if(neighbourhoodType=="Moore'a") {
            if (!period) {
                int startX = Math.max(i - 1, 0);
                int startY = Math.max(j - 1, 0);
                int endX = Math.min(i + 1, width - 1);
                int endY = Math.min(j + 1, height - 1);

                for (int x = startX; x <= endX; x++) {
                    for (int y = startY; y <= endY; y++) {

                        type = cells[x][y].getGrainType();
                        getNeighboursInfo(neighbours,type);
                    }
                }
            } else {
                //periodycznie
                int startX = i - 1;
                int startY = j - 1;
                int endX = i + 1;
                int endY = j + 1;

                int tmpX, tmpY;
                for (int x = startX; x <= endX; x++) {
                    for (int y = startY; y <= endY; y++) {
                        tmpX = x;
                        tmpY = y;
                        if (x == -1) tmpX = width - 1;
                        if (x == width) tmpX = 0;
                        if (y == -1) tmpY = height - 1;
                        if (y == height) tmpY = 0;

                        type = cells[tmpX][tmpY].getGrainType();
                        getNeighboursInfo(neighbours,type);
                    }
                }
            }
        }else if(neighbourhoodType=="von Neumann'a"){
            if (!period) {
                int left = Math.max(i - 1, 0);
                int up = Math.max(j - 1, 0);
                int right = Math.min(i + 1, width - 1);
                int down = Math.min(j + 1, height - 1);
                int[] x= {left,i,right,i};
                int[] y= {j, up, j, down};
                for (int k = 0; k < 4; k++) {

                        type = cells[x[k]][y[k]].getGrainType();
                        getNeighboursInfo(neighbours,type);
                }
            } else {
                //periodycznie
                int left = i - 1;
                int up = j - 1;
                int right = i + 1;
                int down = j + 1;
                int[] x= {left,i,right,i};
                int[] y= {j, up, j, down};
                int tmpX, tmpY;
                for (int k = 0; k < 4; k++) {
                    tmpX = x[k];
                    tmpY = y[k];
                    if (x[k] == -1) tmpX = width - 1;
                    if (x[k] == width) tmpX = 0;
                    if (y[k] == -1) tmpY = height - 1;
                    if (y[k] == height) tmpY = 0;

                    type = cells[tmpX][tmpY].getGrainType();
                    getNeighboursInfo(neighbours,type);
                }
            }
        }else if(neighbourhoodType.startsWith("Heksagonalne")){
            int choice=0;
            if(neighbourhoodType=="Heksagonalne lewe") choice = 0;
            if(neighbourhoodType=="Heksagonalne prawe") choice = 1;
            if(neighbourhoodType=="Heksagonalne losowe") choice = rand.nextInt(2);
            switch (choice) {
                case 0:
                if (!period) {
                    int startX = Math.max(i - 1, 0);
                    int startY = Math.max(j - 1, 0);
                    int endX = Math.min(i + 1, width - 1);
                    int endY = Math.min(j + 1, height - 1);

                    for (int x = startX; x <= endX; x++) {
                        for (int y = startY; y <= endY; y++) {
                            if (!((x == i - 1 && y == j + 1) || ((x == i + 1) && (y == j - 1)))) {
                                type = cells[x][y].getGrainType();

                                getNeighboursInfo(neighbours, type);
                            }

                        }
                    }
                } else {
                    //periodycznie
                    int startX = i - 1;
                    int startY = j - 1;
                    int endX = i + 1;
                    int endY = j + 1;

                    int tmpX, tmpY;
                    for (int x = startX; x <= endX; x++) {
                        for (int y = startY; y <= endY; y++) {
                            tmpX = x;
                            tmpY = y;
                            if (x == -1) tmpX = width - 1;
                            if (x == width) tmpX = 0;
                            if (y == -1) tmpY = height - 1;
                            if (y == height) tmpY = 0;
                            if (!((x == i - 1 && y == j + 1) || ((x == i + 1) && (y == j - 1)))) {
                                type = cells[tmpX][tmpY].getGrainType();

                                getNeighboursInfo(neighbours, type);
                            }
                        }
                    }
                }
                break;
                //prawe
                case 1:
                if (!period) {
                    int startX = Math.max(i - 1, 0);
                    int startY = Math.max(j - 1, 0);
                    int endX = Math.min(i + 1, width - 1);
                    int endY = Math.min(j + 1, height - 1);

                    for (int x = startX; x <= endX; x++) {
                        for (int y = startY; y <= endY; y++) {
                            if (!((x == i - 1 && y == j - 1) || ((x == i + 1) && (y == j + 1)))) {
                                type = cells[x][y].getGrainType();

                                getNeighboursInfo(neighbours, type);
                            }

                        }
                    }
                } else {
                    //periodycznie
                    int startX = i - 1;
                    int startY = j - 1;
                    int endX = i + 1;
                    int endY = j + 1;

                    int tmpX, tmpY;
                    for (int x = startX; x <= endX; x++) {
                        for (int y = startY; y <= endY; y++) {
                            tmpX = x;
                            tmpY = y;
                            if (x == -1) tmpX = width - 1;
                            if (x == width) tmpX = 0;
                            if (y == -1) tmpY = height - 1;
                            if (y == height) tmpY = 0;
                            if (!((x == i - 1 && y == j - 1) || ((x == i + 1) && (y == j + 1)))) {
                                type = cells[tmpX][tmpY].getGrainType();

                                getNeighboursInfo(neighbours, type);
                            }
                        }
                    }
                }
                break;
            }

        }else if(neighbourhoodType.startsWith("Pentagonalne")){
            int choice=0;
            if(neighbourhoodType=="Pentagonalne losowe") choice =rand.nextInt(4);
            if(neighbourhoodType=="Pentagonalne prawe") choice = 0;
            if(neighbourhoodType=="Pentagonalne lewe") choice = 1;
            if(neighbourhoodType=="Pentagonalne dolne") choice = 2;
            if(neighbourhoodType=="Pentagonalne górne") choice = 3;


            switch (choice) {
                case 0: //prawe
                    if (period) {
                        pentagonalPeriodNeighbourhood(i,j,i +1 ,true,neighbours,type);
                    } else {
                        //nieperiodycznie
                        pentagonalNotPeriodNeighbourhood(i,j,i +1 ,true,neighbours,type);
                    }
                    break;
                case 1: //lewe
                    if (period) {
                        pentagonalPeriodNeighbourhood(i,j,i -1 ,true,neighbours,type);
                    } else {
                        //nieperiodycznie
                        pentagonalNotPeriodNeighbourhood(i,j,i -1 ,true,neighbours,type);
                    }
                    break;
                case 2: //dolne
                    if (period) {
                        pentagonalPeriodNeighbourhood(i,j,j +1 ,false,neighbours,type);

                    } else {
                        //nieperiodycznie
                        pentagonalNotPeriodNeighbourhood(i,j,j +1 ,false,neighbours,type);
                    }
                    break;
                case 3: //górne
                    if (period) {
                        pentagonalPeriodNeighbourhood(i,j,j-1 ,false,neighbours,type);

                    } else {
                        //nieperiodycznie
                        pentagonalNotPeriodNeighbourhood(i, j,j-1, false, neighbours,type);
                    }
                    break;
            }

        }
        //max
        if(neighbours.isEmpty()) {
            info[1] = 0;
            info[0] = 0;
        }else {
            info[1] = neighbours.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
            info[0] = neighbours.get(info[1]);
        }

        return info;
    }

    public void pentagonalNotPeriodNeighbourhood(int i, int j, int cond, boolean X, Map<Integer, Integer> neighbours, int type){
        int val;
        int startX = Math.max(i - 1, 0);
        int startY = Math.max(j - 1, 0);
        int endX = Math.min(i + 1, width - 1);
        int endY = Math.min(j + 1, height - 1);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if(X) val=x; else val=y;
                if (!(val == cond)) {
                    type = cells[x][y].getGrainType();

                    getNeighboursInfo(neighbours,type);
                }

            }
        }
    }

    public void pentagonalPeriodNeighbourhood(int i, int j, int cond, boolean X, Map<Integer, Integer> neighbours, int type){
        int val;
        int startX = i - 1;
        int startY = j - 1;
        int endX = i + 1;
        int endY = j + 1;

        int tmpX, tmpY;
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                tmpX = x;
                tmpY = y;
                if (x == -1) tmpX = width - 1;
                if (x == width) tmpX = 0;
                if (y == -1) tmpY = height - 1;
                if (y == height) tmpY = 0;
                if(X) val=x; else val=y;
                if (!(val==cond)) {
                    type = cells[tmpX][tmpY].getGrainType();

                    getNeighboursInfo(neighbours,type);
                }
            }
        }



    }

    public void getNeighboursInfo(Map<Integer, Integer> neighbours, int type){
        if (neighbours.containsKey(type)) {
            int count = neighbours.get(type);
            neighbours.put(type, count + 1);
        } else if (type != 0) {
            neighbours.put(type, 1);
        }
    }

    public void printBoard(){
        for(int i=0; i <height;i++){
            for (int j=0;j<width;j++){
                System.out.print(getCellValue(j,i)+ "\t");
            }
            System.out.println("");
        }
    }



}
