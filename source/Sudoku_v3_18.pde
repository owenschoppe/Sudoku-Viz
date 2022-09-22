//TODO:
//collision detection on the boards - with snapping. or lock to set positions? //overlapping numbers

//add an update flags function when loading board
//enable importing any csv spreadsheet of values
//add a button to show and hide the distance calcs? resize the window. 

import java.awt.*;
import java.awt.event.*;
import java.io.*;

boolean debug = false;

PFont fontA;

Boards board;

PApplet app = this;

//Global Variables
DisplayGrid selected = null;
ControlGrid controller;
DisplayGrid[] intGrids;
Label displayLabel;
//Tool tool; //drawing tool
//Row_Out row_o; //file output for cluster analysis
Distances distance; //distance analysis matricies
Description desc;
DistanceBtn distanceBtn;

File_Out saveFile;
File_In loadFile;
ClearBoard clearBoard;

void setup() {
  size(1125, 565);
  fill(255);
  noStroke();
  background(0);
  smooth();
  frameRate(15);
  fontA = loadFont("Avenir-Medium-20.vlw");

  board = new Boards();

  int offsetX = 35;
  int offsetY = 70;
  int area = height - (offsetY + offsetX);
  int controlWidth = (area)/3*2;

  int displayMargin = 10;
  int displayWidth = area/3;
  println("Control Width: "+displayWidth);

  //create Controll grid
  controller = new ControlGrid(328, 7, 10, offsetX, offsetY, board);
  controller.cells[0].setFocus(true);  

  desc = new Description(board);

  //create possibility displays
  intGrids = new DisplayGrid[0];
  //int displayOffset = width/2 + 20;
  //int displayOffset = width - offsetX - 3*displayWidth - 2*displayMargin + 15;
  int displayOffset = int(controller.x)+(controller.bW/2)+displayMargin*2+35;
  //create 9 displaygrids and pass in the board and a k (1->9)
  for (int i=0; i<3; i++) {
    for (int j=0; j<3; j++) {
      intGrids = (DisplayGrid[]) append(intGrids, new DisplayGrid(i, j, displayWidth, 2, displayMargin, board, (i*3+j+1), displayOffset, offsetY ) ); //(int i, int j, int bW, int gS, int margin, Boards b, int k, int offsetX, int offsetY)
    }
  }

  displayLabel = new Label("Possible Values", intGrids[0].x-intGrids[0].bW/2, intGrids[0].y-intGrids[0].bH/2, 10);


  //create distance displays
  distance = new Distances(displayWidth, 2, displayMargin, board); //(width-20,15,50,20,board);

  saveFile = new File_Out(180, 10, 50, 20, "Save", board);
  loadFile = new File_In(120, 10, 50, 20, "Load");
  clearBoard = new ClearBoard(60, 10, 50, 20, "New");
  
                                //float x, float y, int cW, int cH, String val
  distanceBtn = new DistanceBtn(width-155, 10, 100, 20, "Show Chains", 1);
  distanceBtn.toggleDisplay();

  noLoop();
  redraw();
}

void draw() {
  background(0);
  colorMode(HSB, 90);
  stroke(50);
  strokeWeight(1);
  int x1 = int(distance.grids[0].x-distance.grids[0].bW/2-35);
  int x2 = controller.bW+53;
  line(x1, 50, x1, height-15);
  line(x2, 50, x2, height-15);
}

//use this method to clear the formatting of the lines from all display cells
  void clearLine(){
    for(int g=0; g<9; g++){
        for(int c=0; c<81; c++){
          intGrids[g].cells[c].inLine = false;
        }
      }
  }

//---------------------------------------------------------------------------------------------------------

public class ClearBoard extends ControlButton
{

  ClearBoard(float x, float y, int cW, int cH, String val)
  {
    super(x, y, cW, cH, val);
  }

  void mouseIsReleased() {    
    clear();
    clearFlags();
    distance.update();
    super.mouseIsReleased();
  }

  void clear() {
    for (int i = 0; i < 81; i++) {
      updateCell( i, 0);
    }
  }

  void updateCell(int cID, int v) {
    controller.cells[cID].update(v);
  }
  
  void clearFlags(){
    for(int c = 0; c < 81; c++){
      for(int k=1; k<10; k++){
        int i = controller.cells[c].i;
        int j = controller.cells[c].j;
        //int boardFlag = board.binaryCard(board.probCard(board.prob(i,j,k)));
        //if(boardFlag < 1){ //we only need to scan for flags that were toggled off.
          //board.toggleProb(i,j,k);
        //}
        
        board.cell[i][j][k]=0;
      }
    }
  }
}

//---------------------------------------------------------------------------------------------------------
public class DistanceBtn extends ControlButton
{
  boolean show;
  float oX, oY;
  
  DistanceBtn(float x, float y, int cW, int cH, String val, int l)
  {
    super(x,y,cW,cH,val,l);
    oX = x;
    oY = y;
    show = true;
  }
  
  void mouseIsReleased(){
    toggleDisplay();
  }
  
  void toggleDisplay(){
      show = !show;
    if(show){
      value = "Hide Chains";
      size(1125, height);
      frame.setSize(1125, height+21);
      x = oX;
      for(int i=0; i<3; i++){
        distance.grids[i].register();
      }
    }
    else{
      value = "Show Chains";
      size(915, height);
      frame.setSize(915, height+21);
      x = x-138;
      for(int i=0; i<3; i++){
        distance.grids[i].unregister();
      }
    }
  }
  
}
//---------------------------------------------------------------------------------------------------------

//---------------------------------------------------------------------------------------------------------

//---------------------------------------------------------------------------------------------------------

