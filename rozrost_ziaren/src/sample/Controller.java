package sample;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.*;
import static java.lang.Thread.sleep;

public class Controller {
    int width = 400, height = 300;
    int cellSize, cellSizeY;
    //int cellSize=(1200)/((width>height)? width : height);
    Board board;
    @FXML
    CheckBox checkbox;
    @FXML
    Canvas canvas;
    @FXML
    ChoiceBox choiceBox, randChoiceBox;
    @FXML
    Button startButton, mcButton;
    @FXML
    TextField textField, xTextField, yTextField, rTextField;

    GraphicsContext gc;
    Random rand;
    Thread thread, mcThread;
    Color cellColor = Color.SANDYBROWN;
    Color backgroundColor = Color.LIGHTYELLOW;
    private volatile boolean running = true;
    private volatile boolean mcRunning = true;
    //Color[] colors;
    List<Color> colors;

    @FXML
    public void initialize() {
        rand = new Random();
        xTextField.setText("100");
        yTextField.setText("100");
        rTextField.setVisible(true);
        rTextField.setText("5");
        generate();
    }
    @FXML
    public void generate(){
        canvas.setWidth(1600);
        canvas.setHeight(1000);
        /*canvas.setWidth(width*cellSize);
        canvas.setHeight(height*cellSize);*/

        width=Integer.parseInt(xTextField.getText());
        height=Integer.parseInt(yTextField.getText());

        cellSize=(int)canvas.getWidth()/width;
        cellSizeY = (int) canvas.getHeight()/height;
        colors = new ArrayList<>();
        board =new Board(width, height);
        gc = canvas.getGraphicsContext2D();
        //drawLines();
        gc.setFill(cellColor);
        canvas.setOnMouseClicked(event -> {
            try {
                double x=event.getSceneX();
                double y=event.getSceneY();
                System.out.println("Clicked on: "+ x + ", " + y);

                int cell_x =(int) x*width/(width*cellSize);
                int cell_y =(int) y*height/(height*cellSizeY);
                System.out.println("Cell nr: "+ cell_x + ", " + cell_y);
                if(!board.getCellValue(cell_x,cell_y)) {
                    board.setCellValue(cell_x, cell_y, true);
                    board.setCellGrainType(cell_x,cell_y,colors.size()+1);

                    float r = rand.nextFloat();
                    float g = rand.nextFloat();
                    float b = rand.nextFloat();
                    board.setCellColor(cell_x,cell_y,Color.color(r,g,b));
                    colors.add(board.getCellColor(cell_x,cell_y));
                    gc.setFill(board.getCellColor(cell_x,cell_y));
                    gc.fillRect(x + 1 - (x % cellSize), y + 1 - (y % cellSize), cellSize, cellSizeY);
                }else{
                    //board.setCellValue(cell_x, cell_y, false);
                    //gc.setFill(backgroundColor);
                    //gc.fillRect(x + 1 - (x % cellSize), y + 1 - (y % cellSize), cellSize-1, cellSize-1);


                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        checkbox.setSelected(true);
        textField.setText(3+"");
        randChoiceBox.setOnAction(event -> {
            if(randChoiceBox.getValue().toString()=="z promieniem")
                rTextField.setVisible(true);
            else rTextField.setVisible(false);
        });
        choiceBox.getItems().addAll("Moore'a", "von Neumann'a","Pentagonalne losowe","Pentagonalne lewe","Pentagonalne prawe" ,
                "Pentagonalne górne","Pentagonalne dolne","Heksagonalne losowe", "Heksagonalne lewe","Heksagonalne prawe");
        choiceBox.setValue("Moore'a");
        randChoiceBox.getItems().addAll("losowe","równomierne","z promieniem");
        randChoiceBox.setValue("losowe");
        startButton.setText("START!");

        /*anchorPaneLeft.setMinWidth(width*cellSize);
        anchorPaneLeft.setMinHeight(height*cellSize);
        anchorPaneLeft.setMaxWidth(width*cellSize);
        anchorPaneLeft.setMaxHeight(height*cellSize);*/
        //anchorPaneLeft.prefWidth(width*cellSize);
    }

    @FXML
    public void handleStart() {

        if(startButton.getText() == "START!") {
            running = true;
            thread = new Thread(() -> {
                while (running) {
                    Platform.runLater(() -> startFunction());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            startButton.setText("STOP!");
        }else if(startButton.getText() == "STOP!"){
            running = false;
            thread.interrupt();
            startButton.setText("START!");
        }
    }

    public void startFunction(){

        board.setPeriod(checkbox.isSelected());
        board.setNeighbourhoodType((String) choiceBox.getValue());
        if(randChoiceBox.getValue().toString()=="z promieniem")
            rTextField.setVisible(true);
         else rTextField.setVisible(false);
        board.nextCycle();
        drawBoard();
        //board.printBoard();

        /*try {
               sleep(100);
        } catch (InterruptedException e) {
              e.printStackTrace();
        }*/

    }

    @FXML
    public void handleClear(){
        for(int i=0; i<width; i++){
            for(int j=0;j<height;j++){
                board.setCellValue(i,j,false);
                board.setCellGrainType(i,j,0);
                gc.setFill(backgroundColor);
                gc.fillRect(i*cellSize+1,j*cellSizeY+1,cellSize,cellSizeY);
            }
        }
        colors.clear();
    }

    @FXML
    public void handleRand(){

        int numberOfCells = Integer.parseInt(textField.getText());
        if (numberOfCells > (width * height)) {
            numberOfCells = width * height;
            textField.setText(numberOfCells + "");
        }
        String choice = randChoiceBox.getValue().toString();
        switch (choice) {
            case "losowe":
                randFunc(numberOfCells);
            break;
            case "równomierne":
                //handleClear();
                int product= width*height;
                int distance = product/numberOfCells;
                int k=0;int i=0;
                for (int x=0; x< width;x++){
                    for (int y=0; y<height;y++){
                        if(k%distance==0 && k < product) {
                            float r = rand.nextFloat();
                            float g = rand.nextFloat();
                            float b = rand.nextFloat();
                            board.setCellValue(x, y, true);
                            board.setCellGrainType(x, y, i + 1);

                            board.setCellColor(x, y, Color.color(r, g, b));
                            gc.setFill(board.getCellColor(x, y));
                            colors.add(board.getCellColor(x, y));
                            gc.fillRect(x * cellSize + 1, y * cellSizeY + 1, cellSize , cellSizeY );
                            i++;
                        }
                        k++;
                    }
                }
                break;
            case "z promieniem":
                handleClear();
                int size = colors.size();
                int noChange = 0;
                int counter=0;
                for (int j = size; j < numberOfCells + size; j++) {

                    int x = rand.nextInt(width);
                    int y = rand.nextInt(height);
                    float r = rand.nextFloat();
                    float g = rand.nextFloat();
                    float b = rand.nextFloat();
                    int R=Integer.parseInt(rTextField.getText());
                    System.out.println("");
                    if (!board.getCellValue(x, y) && countDistance(x,y)) {

                        board.setCellValue(x, y, true);
                        board.setCellGrainType(x, y, j + 1);
                        System.out.println("");
                        board.setCellColor(x, y, Color.color(r, g, b));
                        gc.setFill(board.getCellColor(x, y));
                        //colors[i]=board.getCellColor(x,y);
                        colors.add(board.getCellColor(x, y));
                        gc.fillRect(x * cellSize + 1, y * cellSizeY + 1, cellSize , cellSizeY );
                        gc.strokeOval(x* cellSize  + cellSize/2 -cellSize*R ,y * cellSizeY  + cellSizeY/2 -cellSizeY*R, cellSize*R*2,cellSizeY*R*2);
                        noChange=0;
                        counter++;
                    } else {
                        j--;
                        noChange++;
                        if (noChange==10000){
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("WARNING Dialog");
                            alert.setHeaderText("Zbyt dużo zarodków");
                            alert.setContentText("Wylosowano maxymalną ilość równą "+counter);
                            alert.showAndWait();
                            break;
                        }
                    }


                }
                break;
        }
    }

    public void randFunc(int numberOfCells){
        try {

            int size = colors.size();
            //colors = new Color[numberOfCells];
            for (int i = size; i < numberOfCells + size; i++) {

                int x = rand.nextInt(width);
                int y = rand.nextInt(height);
                float r = rand.nextFloat();
                float g = rand.nextFloat();
                float b = rand.nextFloat();

                //Color randomColor = new Color.(r, g, b);
                if (!board.getCellValue(x, y)) {
                    board.setCellValue(x, y, true);
                    board.setCellGrainType(x, y, i + 1);

                    board.setCellColor(x, y, Color.color(r, g, b));
                    gc.setFill(board.getCellColor(x, y));
                    //colors[i]=board.getCellColor(x,y);
                    colors.add(board.getCellColor(x, y));
                    gc.fillRect(x * cellSize + 1, y * cellSizeY + 1, cellSize , cellSizeY );
                } else i--;

            }

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR Dialog");
            alert.setHeaderText("Error");
            alert.setContentText("Not a number");
            alert.showAndWait();
        }
    }

    public boolean countDistance(int x, int y){
        boolean cond=true;
        double distance=0;
        for(int i=0; i<width; i++){
            for(int j=0; j<height;j++){
                if(board.getCellValue(i,j)){
                    distance= sqrt(pow((i-x),2) + pow((j-y),2));
                    if((distance)<(Integer.parseInt(rTextField.getText())+1))
                        cond=false;
                }
            }
        }
        return cond;
    }

    public void drawBoard(){
        for(int i=0;i<width;i++){
            for (int j=0;j<height;j++){
                if(board.getCellValue(i,j)){
                    //System.out.println(i + " "+ j);
                    //gc.setFill(board.getCellColor(i,j));
                    //gc.setFill(cellColor);
                    gc.setFill(colors.get(board.getCellGrainType(i,j)-1));
                    gc.fillRect(i*cellSize+1,j*cellSizeY+1,cellSize,cellSizeY);
                }else{
                    gc.setFill(backgroundColor);
                    gc.fillRect(i*cellSize+1,j*cellSizeY+1,cellSize,cellSizeY);
                }
            }
        }
    }

    @FXML
    public void monteCarloHandle(){
        //startButton.setDisable(false);
        System.out.println(mcButton.getText());
        if(mcButton.getText().startsWith("MCSTART")) {
            mcRunning = true;
            mcThread = new Thread(() -> {
                while (mcRunning) {
                    Platform.runLater(() -> monteCarlo());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            mcThread.start();
            mcButton.setText("MCSTOP");
        }else if(mcButton.getText().startsWith("MCSTOP")){
            mcRunning = false;
            mcThread.interrupt();
            mcButton.setText("MCSTART");
        }
    }

    public void monteCarlo(){

        Map<Integer,Integer> energies = new HashMap<>();
        Map<Integer,Color> colours = new HashMap<>();
        int i = rand.nextInt(width);
        int j = rand.nextInt(height);
        //System.out.println("Komórka: "+i+","+j);
        //System.out.println("Typ: "+board.getCellGrainType(i,j));
        Color color;
        int type=0;
        if (!board.isPeriod()) {
            int startX = Math.max(i - 1, 0);
            int startY = Math.max(j - 1, 0);
            int endX = Math.min(i + 1, width - 1);
            int endY = Math.min(j + 1, height - 1);

            //liczymy energię komórki
            for (int x = startX; x <= endX; x++) {
                for (int y = startY; y <= endY; y++) {
                    type=board.getCellGrainType(x,y);
                    color = board.getCellColor(x,y);
                    if (energies.containsKey(type)) {
                        int count = energies.get(type);
                        energies.put(type, count + 1);

                    } else if (type != 0) {
                        energies.put(type, 1);
                    }
                    colours.put(type,color);
                }
            }
            int k = energies.get(board.getCellGrainType(i,j));
            energies.put(board.getCellGrainType(i,j), k-1);
            type= energies.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
            int energy=energies.get(type);

            board.setCellGrainType(i,j,type);
            board.setCellColor(i,j,colours.get(type));



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

                    type=board.getCellGrainType(tmpX,tmpY);
                    color = board.getCellColor(tmpX,tmpY);
                    if (energies.containsKey(type)) {
                        int count = energies.get(type);
                        energies.put(type, count + 1);

                    } else if (type != 0) {
                        energies.put(type, 1);
                        System.out.println("");
                    }
                    colours.put(type,color);
                }

            }
            int k = energies.get(board.getCellGrainType(i,j));
            energies.put(board.getCellGrainType(i,j), k-1);
            type= energies.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
            System.out.println(type);
            int energy=energies.get(type);

            board.setCellGrainType(i,j,type);
            board.setCellColor(i,j,colours.get(type));


        }
        drawBoard();
    }

    @FXML
    public void handleFillRandomly(){
        handleClear();
        randFunc(width*height);
    }

    public void drawLines(){
        gc.setFill(Color.BLACK);
        for(int i=0; i<(width*cellSize); i++){
            for(int j=0; j<(height*cellSizeY); j++){
                if((i%cellSize==0 )||(j%cellSizeY==0))
                    gc.fillRect(i,j,1,1);
            }
        }
        drawBoard();
    }

}
