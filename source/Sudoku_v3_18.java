import processing.core.*; 
import processing.xml.*; 

import java.awt.*; 
import java.awt.event.*; 
import java.io.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class Sudoku_v3_18 extends PApplet {

//TODO:
//collision detection on the boards - with snapping. or lock to set positions? //overlapping numbers

//add an update flags function when loading board
//enable importing any csv spreadsheet of values
//add a button to show and hide the distance calcs? resize the window. 





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

public void setup() {
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
  int displayOffset = PApplet.parseInt(controller.x)+(controller.bW/2)+displayMargin*2+35;
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

public void draw() {
  background(0);
  colorMode(HSB, 90);
  stroke(50);
  strokeWeight(1);
  int x1 = PApplet.parseInt(distance.grids[0].x-distance.grids[0].bW/2-35);
  int x2 = controller.bW+53;
  line(x1, 50, x1, height-15);
  line(x2, 50, x2, height-15);
}

//use this method to clear the formatting of the lines from all display cells
  public void clearLine(){
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

  public void mouseIsReleased() {    
    clear();
    clearFlags();
    distance.update();
    super.mouseIsReleased();
  }

  public void clear() {
    for (int i = 0; i < 81; i++) {
      updateCell( i, 0);
    }
  }

  public void updateCell(int cID, int v) {
    controller.cells[cID].update(v);
  }
  
  public void clearFlags(){
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
  
  public void mouseIsReleased(){
    toggleDisplay();
  }
  
  public void toggleDisplay(){
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

class Boards
{
  int[][][] cell;
  boolean debug_board = false;
  
  Boards() {
    
    cell = new int[9][9][10];
    for (int i=0; i<9; i++){
      for (int j=0; j<9; j++){
        //cell[i][j][0] = 0;
        //createNumberbox(i, j, 0);
        for (int k=0; k<10; k++){
          cell[i][j][k] = 0;
          if(debug_board){
            println("i: " + i);
            println("j: " + j);
            println("k: " + k);
            println();
          }
        }
      }
    }
  }
  
  /////////////////////////////////////////////////
  //Math
  /////////////////////////////////////////////////  

  public int row(int i)
  {
   return floor(i/3);
  }
  
  public int col(int i)
  {
   return i%3;
  }
  
  public int getGroupFloor_(int i) 
  {
  return floor(i / 3) * 3;
  }
  
  // takes a value in range [0-8] and returns the corresponding group floor {2,5,8}
  public int getGroupCeil_(int i) 
  {
    return getGroupFloor_(i) + 3; //shouldn't this be + 2? no because the logic is < not <=
  }
  
  public int prob(int i, int j, int k)
  {
    if(k > 0){
      return cell[i][j][k];
    }
    else{
      return 0;
    }
  }
  
  public int probCard(int p)
  {
    if(p != 0){
      //println("PROBABILITY CARDINATLITY: "+p/abs(p));
      return p/abs(p); //return -1 or 1
    }
    else{
      return 0;
    }  
  }

  public int binaryCard(int p) //return 0 or 1
  {
    switch(p){
      case -1:
        return 0;
      case 1:
        return 1;
      default:
        //error
        return 1;
    }
  }  
  
  public int[] possibilities(int i, int j){
    int[] p = new int[1];
    p[0] = 0; //sum position
    if(cell[i][j][0] == 0){
      for(int k=1; k<10; k++){
        if(cell[i][j][k] == 0){
          p[0]++; //increment the summ
          p = append(p, k); //add the current k to the array
        }
      }
    }
    return p;
  }
  /////////////////////////////////////////////////
  //Update
  /////////////////////////////////////////////////
  public void update(int i, int j, int k1) throws Exception
  {
    int k2 = cell[i][j][0];
    if(k1 == k2){
      throw new Exception("None i:"+i+" j:"+j+" k1:"+k1+" k2:"+k2+" prob k1:"+prob(i,j,k1)+" prob k2:"+prob(i,j,k2));
      //return;
    }
    if(prob(i,j,k1)+prob(i,j,k2) < 0){
      throw new Exception("Illegal Move i:"+i+" j:"+j+" k1:"+k1+" k2:"+k2+" prob k1:"+prob(i,j,k1)+" prob k2:"+prob(i,j,k2));
    }
    if(k2 > 0){
      println("Undo i:"+i+" j:"+j+" k1:"+k1+" k2:"+k2+" prob k1:"+prob(i,j,k1)+" prob k2:"+prob(i,j,k2));
      updateK(i,j,k2,1);
      cell[i][j][0] = 0;
    }
    if(k1 > 0){
      println("Update i:"+i+" j:"+j+" k1:"+k1+" k2:"+k2+" prob k1:"+prob(i,j,k1)+" prob k2:"+prob(i,j,k2));
      updateK(i,j,k1,-1);
      cell[i][j][0] = k1;
    }
      
  }
  
  public void updateK(int i,int j,int k, int delta)
  {
    updateCell(i,j,k,delta);
    updateNeighbors(i,j,k,delta);
  }
  
  public void updateCell(int i,int j,int k, int d)
  {
      for(int n = 1; n<10; n++){
        if(n == k){
          cell[i][j][n] += -d;
        }
        else{
          cell[i][j][n] += d;
        }
      }

    
  }
  
  public void updateNeighbors(int i, int j, int k, int d)
  {
    updateRow(i,j,k,d);
    updateCol(i,j,k,d);
    updateGrp(i,j,k,d);
  }
  
  public void updateRow(int x, int y, int k, int d)//row and col functions could be combined into compound
  {
    int startX = 0;
    for(int i = startX; i<9; i++){
      if(i!=x){
        //check for error before setting prob to 0. 
        //if there is an error, undo previous moves.
        if(cell[i][y][k]<=0){
          cell[i][y][k] += d;
        }
        else{
        }
        
      }
    }
  }
  
  public void updateCol(int x, int y, int k, int d)
  {
    int startY = 0;
    for(int j = startY; j<9; j++){
      if(j!=y){
        if(cell[x][j][k]<=0){
          cell[x][j][k] += d;
        }
        else{
        }
      }
    }
  }
  
  public void updateGrp(int x, int y, int k, int d) //throws Exception
  {
    int groupCeilX = getGroupCeil_(x);
    int groupCeilY = getGroupCeil_(y);
    for (int i = getGroupFloor_(x); i < groupCeilX; i++) {
      if (i != x) {
        for (int j = getGroupFloor_(y); j < groupCeilY; j++) {
          if (j != y) {
            if(cell[i][j][k]<=0){
              cell[i][j][k] += d;
            }
            else{
            }
          }
        }
      }
    }
  }
  
  public void toggleProb(int i, int j, int k)
  {
    //if the cell is an option, turn it off.
    if(prob(i,j,k) == 0){ 
      print("cell prob1: "+prob(i,j,k));
      cell[i][j][k] -= 1;
      println(" prob2: "+prob(i,j,k));
      //displayCell(this.cell[i][j][k],i,j,k,getColor(i,j,k));
      //redraw();
      return;
    }
    //if the cell isn't an option
    //at great expense we check the legality of toggling the probability.
    if(prob(i,j,k) < 0 && cell[i][j][0]==0){ 
      print("cell prob1: "+prob(i,j,k)+" check: ");
      boolean legal = true;
      
      for(int n=0; n<9; n++){
        legal = prob(i,n,k) > 0 ? false: legal; //check the row starting from the left
        print(","+prob(i,n,k));
      }
        
      for(int m =0; m<9; m++){
        legal = prob(m,j,k) > 0 ? false: legal; //check the column starting from the top
        print(","+prob(m,j,k));
      }
      
      int groupCeilX = getGroupCeil_(i);
      int groupCeilY = getGroupCeil_(j);
      for (int m = getGroupFloor_(i); m < groupCeilX; m++) {
        if(m != i){
          for (int n = getGroupFloor_(j); n < groupCeilY; n++) {
            if (n != j) {
              legal = prob(m,n,k) > 0 ? false: legal; //check the group
              print(","+prob(m,n,k));
            }
          }
        }
      }
        cell[i][j][k] += legal ? 1 : 0; //if legal add one
        
        intGrids[k-1].cells[i*9+j].getCellFill(i,j,k); //update the color.
        
        println(" legal: "+legal+" prob2: "+prob(i,j,k));
    }
  }
  
  
}
public class Cell extends Button
{
  Grid parent;
  
  Cell(int i, int j, int val, Grid parent)
  {
    super(0,0, val); //temporarily set the x,y to 0;
    this.parent = parent;
    this.i = i;
    this.j = j;

    cW = cH = cellWidth();
    x = cellX(i);
    y = cellY(j);
    
    registerMouseEvent(this);
  }
  
  public float cellX(int i)
  {
  i = (i+9) % 9; //when using arrows to navigate, wraps around the square.
  return parent.x+(i*(cW+1))+(floor(i/3)*parent.gS)-(parent.bW/2)+(cW/2); //-(parent.bW/2)+(cW/2) <------for CENTER
  }

  public float cellY(int j)
  {
    j = (j+9) % 9;
    return parent.y+(j*(cW+1))+floor(j/3)*parent.gS-(parent.bW/2)+(cW/2);
  }
  
  public int cellWidth()
  {
    return ((parent.bW-(2*parent.gS))/9)-2;
  }
  
  public void mouseEvent(MouseEvent event)
  {
      //---------need a parent class that instantiates all of 
      if(selected == null && isMouseover(mouseX, mouseY) || selected == parent && isMouseover(mouseX, mouseY) ){
        //println("Box "+i+","+j+"is being hovered");
        switch(event.getID()){
          
          case MouseEvent.MOUSE_PRESSED:
            mouseIsPressed();
            //selected = (DisplayGrid) parent;
          break;
            
          /*case MouseEvent.MOUSE_DRAGGED:   
            mouseIsDragged();
          break;*/
          
          case MouseEvent.MOUSE_RELEASED:
            mouseIsReleased();
            //selected = null;
          break;
        }
        redraw();
      }
      else{
        pressed = false;
      }
  }
    
}

public class Button
{
  boolean debug_board = false;
  float x;
  float y;
  int i;
  int j;
  int cW; //cell width
  int cH;
  int val; //the cells display value
  int cellFill;
  int textFill;
  
  //internal
  boolean pressed = false;
  boolean mouseOver = false;
    
  //may have to have two contructors, one for DisplayGrid and one for ControlGrid parents
  Button(float x, float y, int cW, int cH, int val)
  {
    this.x = x;
    this.y = y;
    this.val = val;
    this.cW = cW;
    this.cH = cH;
    
    //registerDraw(this);
  }
  
  Button(float x, float y, int val)
  {
    this.x = x;
    this.y = y;
    this.val = val;
    
    //registerDraw(this);
  }
  
  /////////////////////////////////////
  //Control
  /////////////////////////////////////
    
  //rectangle detection
  public boolean isMouseover(float mx, float my) {
   mouseOver = mx > x-cW/2 && mx < x+cW/2 && my > y-cH/2 && my < y+cH/2;
   return mouseOver; 
  }
  
  // Override as required...
  public void mouseIsPressed()
  {
      println("Cell "+i+","+j+" is being clicked");
      //x = mouseX; //would center the box on the mouse if used with rectMode(CENTER);
      //y = mouseY;
      pressed = true;
      redraw();
  }
  
  //cells are never draggable
  /*void mouseIsDragged() {
      println("Box "+i+","+j+" is being dragged");
      x += mouseX-pmouseX;
      y += mouseY-pmouseY;
  }*/

  public void mouseIsReleased() 
  {
      println("Cell "+i+","+j+" is being released");
      pressed = false;
      redraw();
  }
  
  public void draw()
  {    
    if(!mouseOver && !pressed) {
      displayDefault();
      displayText();
    }
    else if (pressed) {
      displayClicked();
      displayText();
    }
    else {
      displayOver();
      displayText();
    }
  }
  
  public void displayDefault()
  {
    //println("Display Default: "+i+","+j+","+val);
    rectMode(CENTER);
      fill(cellFill);
      rect(x,y,cW,cH);
    
  }
  
  public void displayClicked()
  {
    rectMode(CENTER);
    stroke(90);
    strokeWeight(2);
    //noStroke();
    fill(cellFill);
    rect(x,y,cW,cH);
  }
  
  public void displayOver()
  {
    rectMode(CENTER);
    stroke(90,60);
    strokeWeight(1);
    //noStroke();
    fill(cellFill);
    rect(x,y,cW,cH);
  }
  
  public void displayText()
  {
    fill(textFill);
    noStroke();
    textFont(fontA, cH-4);
    textAlign(CENTER,CENTER);
    String value = val > 0 ? str(val) : "";
    if((board.probCard(board.prob(i,j,val))+1)>0){ text(value, x, y); }
  }
 
  ////////////////////////////////////
  //Math
  ////////////////////////////////////
 
}
public class ControlButton extends Button
{ 
  String value;
  boolean active = false;
  int textLines = 1;
  
  int BUTTON_UP;
  int BUTTON_DOWN;
  int TEXT_UP;
  int TEXT_DOWN;
  
  ControlButton(float x, float y, int cW, int cH, String val){
    super(x,y,cW,cH,0);
    this.value = val; //overload the int val with string
    colorMode(RGB, 255);
    BUTTON_UP = color(128);
    BUTTON_DOWN = color(75);
    TEXT_UP = color(0);
    TEXT_DOWN = color(255);
  
    cellFill = BUTTON_UP;
    textFill =  TEXT_UP;
    
    registerMouseEvent(this);
    registerDraw(this);
  }
  
   ControlButton(float x, float y, int cW, int cH, String val, int l){
    super(x,y,cW,cH,0);
    this.value = val; //overload the int val with string
    textLines = l;
    colorMode(RGB, 255);
    BUTTON_UP = color(128);
    BUTTON_DOWN = color(75);
    TEXT_UP = color(0);
    TEXT_DOWN = color(255);
  
    cellFill = BUTTON_UP;
    textFill =  TEXT_UP;
    
    registerMouseEvent(this);
    registerDraw(this);
  }
  
  ControlButton(float x, float y, int cW, int cH, int val){
    super(x,y,cW,cH,val);
    this.value = str(val); //overload the int val with string
    colorMode(RGB, 255);
    cellFill = color(BUTTON_UP);
    textFill = color(TEXT_UP);
    
    registerMouseEvent(this);
    registerDraw(this);
  }
  
  public void mouseEvent(MouseEvent event)
  {
      //may actually want to wrap this class in a grid parent to prevent accidental pressing with mouse while dragging. Grid becomes Container.
      //if we do this, mvoe the mouseEvent method back into the Button class.
      
      //if(selected == null && isMouseover(mouseX, mouseY) || selected == parent && isMouseover(mouseX, mouseY) ){
      if(isMouseover(mouseX, mouseY)){  
        println("Box "+i+","+j+" is being hovered. MouseOver="+mouseOver);
        mouseOver = true;
        switch(event.getID()){
          
          case MouseEvent.MOUSE_PRESSED:
            mouseIsPressed();
            //selected = (DisplayGrid) parent;
          break;
            
          /*case MouseEvent.MOUSE_DRAGGED:   
            mouseIsDragged();
          break;*/
          
          case MouseEvent.MOUSE_RELEASED:
            mouseIsReleased();
            //selected = null;
          break;
        }
        //NECESSARY FOR HOVER EFFECTS ON CONTROL BUTTONS
        redraw();
      }
      else{
        pressed = false;
        mouseOver = false;
      }
  }
  
  public void draw(){
    //get color
    colorMode(HSB,90);
    if(active || pressed){
      cellFill = BUTTON_DOWN;
      textFill = TEXT_DOWN;
    }
    else{
      cellFill = BUTTON_UP;
      textFill = TEXT_UP;
    }
    //println("Draw Tool Button");
    super.draw();
  }
  
  public void displayText()
  {
    fill(textFill);
    noStroke();
    textFont(fontA, (cH/textLines)-6);
    textAlign(CENTER,CENTER);
    text(value, x-1, y-1); 
  }
  
  public void displayOver()
  {
    rectMode(CENTER);
    
    noStroke();
    fill(cellFill);
    rect(x,y,cW,cH);
    
    stroke(90);
    strokeWeight(1);
    float x1 = x-(cW/2);
    float y1 = y-(cH/2);
    float x2 = x+(cW/2);
    float y2 = y+(cH/2);
    
    line(x1,y1,x1,y2);
    line(x1,y2,x2,y2);
    line(x2,y2,x2,y1);
  }
  
  public void displayClicked()
  {
    rectMode(CENTER);
    
    noStroke();
    fill(cellFill);
    rect(x,y,cW,cH);
    
    stroke(90);
    strokeWeight(2);
    float x1 = x-(cW/2);
    float y1 = y-(cH/2);
    float x2 = x+(cW/2);
    float y2 = y+(cH/2);
    
    line(x1,y1,x1,y2);
    line(x1,y2,x2,y2);
    line(x2,y2,x2,y1);
  }

}
public class ControlCell extends Cell{

  boolean debug_box = false;
  boolean isFocus;
  int k = 0;
  Boards board;
  ControlGrid parent;
  int HIGH, NORMAL_FILL;
  
  ControlCell(Boards b, int i, int j, int k, ControlGrid parent)
  {
    super(i, j, 0, (Grid) parent);
    //println("New ControlCell "+i+","+j);
    this.parent = parent;
    this.board = b;
    if(debug_box){println("New Control:"+i+":"+x+" , "+j+":"+y);}
    //this.cW = this.cH = cW-1; //31;
    //x = cellX(i);
    //y = cellY(j);
    isFocus = false;
    colorMode(RGB, 255);
    HIGH = color(35,232,162); //hsb(40,73,47)
    NORMAL_FILL = color(255);
    textFill = color(0);
    
    registerDraw(this);
  }
  
  /*int cellX(int i)
  {
  i = (i+9) % 9; //when using arrows to navigate, wraps around the square.
  //println("parent x: "+parent.x+" parent gS: "+parent.gS+" i: "+i+" cW: "+cW);
  return parent.x+(i*cW+1)+floor(i/3)*parent.gS;
  }

  int cellY(int j)
  {
    j = (j+9) % 9;
    return parent.y+(j*cW+1)+floor(j/3)*parent.gS;
  }*/
  
  //////////////////////
  //Control
  //////////////////////  
  
  //create a keyEvent function and register it
  
  public void update(int k)
  {
    int prev = val; 
    try{
      setValue(k);
      updateBoard();
      
      //if you change the line, then remove the formatting because it isn't the same anymore.
      //remove this section if it is preferred to have some persistance of the chain.
      int id = (i*9)+j;
      if(intGrids[k-1].cells[id].inLine){
        clearLine();
      }
    }
    catch(Exception e){
      println(e.getMessage());
      setValue(prev);
    }
    //display();
    redraw();
  }
  
  public void updateBoard() throws Exception
  {
      if(debug_box){ println("Try update "+i+","+j+" : "+val); }
      board.update(i,j,val); //attempt the move made by the player
  }
  
  public void setValue(int k)
  {  
    val = k;
  }
  
  public void clear()
  {
    val = 0;
  }
  
  public void setFocus(boolean f)
  {
    isFocus = f;
  }
  
  public void mouseIsReleased()
  {
    parent.switchFocus(i,j);
    super.mouseIsReleased();
  }
  
  public void draw()
  { 
    colorMode(HSB, 90);
    if(isFocus){
      cellFill = HIGH;
    }
    else{
      cellFill = NORMAL_FILL;
    }
    noStroke();
    super.draw();
  }
  
  public void displayOver()
  {
    //println("Display Default: "+i+","+j+","+val);
    rectMode(CENTER);
      if(!isFocus){
        colorMode(HSB, 90);
        fill(40,20,90);
        
      }
      else{
        fill(HIGH);
      }
      rect(x,y,cW,cH);
      
    
  }
  
  public void displayClicked()
  {
    //println("Display Default: "+i+","+j+","+val);
    rectMode(CENTER);
      fill(HIGH);
      rect(x,y,cW,cH);
    
  }
}

public class ControlGrid extends Grid {
  boolean debug = false;
  boolean dragEvent = false;
  //track locked display grid
  int ci = 0;
  int cj = 0;
  int newI = 0;
  int newJ = 0;
  
  Label label;
  
  //instnatiate a board
  ControlCell[] cells;
  
  ControlGrid(int bW, int gS, int margin, int offsetX, int offsetY, Boards board) {
    //instantiate the abstract grid class
       //(int i, int j, int b, int gS, int margin, int offsetX, int offsetY, int ci, int cj)   
    super(0,     0,     bW,    gS,     margin,     offsetX,     offsetY);
    super.board = board;
    
    cells = new ControlCell[0];
    
    label = new Label("Sudoku Board",x-bW/2,y-bH/2,10);
    
    registerKeyEvent(this);
    registerDraw(this);
    
    //link 81 control cells to board[i][j][0]
    for (int i=0; i<9; i++){
      for (int j=0; j<9; j++){
        //int cellWidth = ((this.bW-(2*this.gS))/9)-2;
        cells = (ControlCell[]) append(cells, new ControlCell(board, i, j, 0, this) ); //(int x, int y, int cW, Boards b, int i, int j, Grid parent)
      }
    } 
    
  }
  
  public void keyEvent(KeyEvent event){
  if (event.getID() == KeyEvent.KEY_PRESSED) {
    if(debug){print("Key Pressed: "+event.getKeyCode()+" ");}
    //if(debug){print("Coded Key: "+keyCode+" ");}
      switch(event.getKeyCode()){
        case KeyEvent.VK_UP:
          println("UP");
          //navigate(0,-1);
          setTarget(newI,newJ-1);
          switchFocus(newI, newJ);
          break;
        case KeyEvent.VK_DOWN:
          println("DOWN");
          //navigate(0,1);
          setTarget(newI,newJ+1);
          switchFocus(newI, newJ);
          break;
        case KeyEvent.VK_LEFT:
          println("LEFT");
          //navigate(-1,0);
          setTarget(newI-1,newJ);
          switchFocus(newI, newJ);
          break;
        case KeyEvent.VK_RIGHT:
          println("RIGHT");
          //navigate(1,0);
          setTarget(newI+1,newJ);
          switchFocus(newI, newJ);
          break;
        case KeyEvent.VK_BACK_SPACE:
          println("DELETE");  
          cells[cID].update(0);
          distance.update();
          break;
        case KeyEvent.VK_DELETE:
          println("DELETE");  
          cells[cID].update(0);
          distance.update();
          break;
        default:
          char num;
          num = PApplet.parseChar(event.getKeyCode());
          if(debug){println("Key: "+num);}
          if(num >= '0' && num <= '9'){ 
            println("Integer");  
            cells[cID].update(PApplet.parseInt(str(num)));
            distance.update();
          } 
          else{ 
            println("NOT Integer"); 
          }
          break;
      }
  }
  if (event.getID() == KeyEvent.KEY_RELEASED) {
    println("Key Released");
    switchFocus(newI, newJ);
    redraw();
  }
}

public void setTarget(int i, int j)
{
  newI = i;
  newJ = j;
  println("New Target: "+i+","+j);
}

public void switchFocus(int i, int j)
{
  if(ci != i || cj != j){
    if(debug){ print("ControlCell "+cID+": "+ci+","+cj+"   ");}
    int prevI = ci;
    int prevJ = cj;
    updateFocus(false); //clear the old cell inFocus
    
    newI = ci = (i+9) % 9; //when using arrows to navigate, wraps around the square.
    newJ = cj = (j+9) % 9;
    cID = cellID(ci,cj);
    if(debug){ println("ControlCell "+cID+": "+ci+","+cj);}
    updateFocus(true); //set the new focus
    
    redraw();
  }
}

public void updateFocus(boolean f)
{
  cells[cID].setFocus(f);
}

//////////////////////////////
//Math
//////////////////////////////

public int cellID(int i, int j)
{
  return (i*9)+j;
}

public void draw()
{
  switchFocus(newI, newJ);
}
  
}
public class Description
{
  Boards board;
  float x, y;
  int fontSize;
  int boxWidth;
  int boxHeight;
  
  Description(Boards b){
    board = b;
    registerDraw(this);
    
    x = controller.offsetX;
    y = controller.offsetY + (controller.bH+2);
    
    boxWidth = controller.bW-15;
    boxHeight = height-PApplet.parseInt(y)-35;
  }
  
  public void draw(){
    int ci = controller.ci;
    int cj = controller.cj;
    String statement = "";
    int[] possible = board.possibilities(ci,cj);
    LineObj theLine;
    String place = null;
    
    float y1 = y;
    int bW = boxWidth;
    y1 += fontSize + 20;
    
    theLine = whatChain(ci, cj, distance.Rows[cj].myLines);
    if(theLine != null && theLine.length < possible[0]){ possible = theLine.possibilities(); place = "row"; }
    statement += chainStatement(theLine, "row"); 
    
    theLine = ( whatChain(ci, cj, distance.Cols[ci].myLines));
    if(theLine != null && theLine.length < possible[0]){ possible = theLine.possibilities(); place = "column"; }
    statement +=  chainStatement(theLine, "column");
    
    int grp = ((ci/3)*3)+(cj/3);
    theLine = ( whatChain(ci, cj, distance.Grps[grp].myLines));
    if(theLine != null && theLine.length < possible[0]){ possible = theLine.possibilities(); place = "group"; }
    statement +=  chainStatement(theLine, "group");
    
    drawText( whatPossible(ci,cj, possible, place), x, y, 14, bW);
    drawText(statement, x, y1, 10, bW);
  }
  
  public void drawText(String statement, float x, float y, int s, int bW){
    colorMode(HSB, 90);
    fill(90);
    fontSize = s;
    textFont(fontA, fontSize);
    textAlign(LEFT);
    rectMode(CORNER);
    text(statement, x, y, bW, boxHeight);
    //text(statement, x, y);
  }
  
  public String whatPossible(int ci, int cj, int[] possible, String place){
    String statement = "";
    if(possible[0] > 0){
      if(place != null){
        statement += "In the "+place+" this";
      }
      else{ statement += "This"; }
      
      statement += " cell ("+PApplet.parseInt(ci+1)+","+PApplet.parseInt(cj+1)+") ";
      
      if(possible[0] > 1){
      statement += "has "+possible[0]+" possibilities";
      }
      else{
        statement += "has one possibility";
      }
      
      for(int k=1; k<possible.length; k++){
       if(k==1){
         statement += ": ";
       }
       if(k>1){
         statement += ",";
       }
         statement += possible[k];
      }
      
    }
    else{
      statement += "This cell ("+PApplet.parseInt(ci+1)+","+PApplet.parseInt(cj+1)+") is set as "+board.cell[ci][cj][0];
    }
    statement += "\n\n";
    //print(statement);
    return statement;
  }
  
  public LineObj whatChain(int ci, int cj, LineObj[] myLines){
    LineObj theLine = null;
    //println("Found "+myLines.length+" lines in "+place);
    for(int l=0; l<myLines.length; l++){ //for line in row
      for(int c=0; c<myLines[l].length; c++){ //for cells in line
        if(myLines[l].cells[c].i == ci && myLines[l].cells[c].j == cj){
          if(theLine == null || (myLines[l].length < theLine.length)){
            theLine = myLines[l]; 
          }
        }
      }
    }
    return theLine;
  }
    
  public String chainStatement(LineObj theLine, String place){
    String statement = "";
    if(theLine != null){
      statement += "In this "+place+" this cell ";
      if(theLine.length > 1){
        statement += "forms a "+theLine.length+" cell chain with "+theLine.length+" possibilities: ";
      }
      else{
        statement += "has one possibility: ";
      }
      int[] chain = theLine.possibilities();
        for(int i=1; i < chain.length; i++){
          statement += chain[i];
          
          statement += i!=PApplet.parseInt(chain.length-1) ? "," : "";
        }
      statement += "\n\n";
    }
    //print(statement);
    return statement;
  }
  
}
public class DisplayCell extends Cell{
  //get board and k from parent
  Boards board;
  float dx;
  float dy;
  DisplayGrid parent;
  boolean debug_board = true;
  boolean inLine = false;

  //create 81 display cells linked to board[i][j][k]
  DisplayCell(Boards b, int i, int j, int k, DisplayGrid parent){
    super(i, j, k, (Grid) parent);
    //print("New DisplayCell "+i+","+j);
    this.parent = parent;
    board = b;
    //x = cellX(i);
    //y = cellY(j);
    dx = x - parent.x;
    dy = y - parent.y;
    //println("parent x: "+parent.x+" parent y: "+parent.y+" dX: "+dx+" dY: "+dy);
    
    registerDraw(this);
  }
  
 /////////////////////////////////
 //Display
 /////////////////////////////////
 
  public int getCellFill(int i, int j, int val)
  {
    int only = highlight(i,j);
    
    return getCellFill(i,j,val,only);
  }
  
  //-----------
   public int getCellFill(int i, int j, int val, int only)
  {
    
    colorMode(HSB, 90);
    int h;
    int s;
    int b;
    int a;
    h = 10*val; //k is hue: 10, 20, 30, 40, 50, 60, 70, 80, 90
    s = constrain((getCard(val)+1)*45,0,90); //probability is brightness: 10,45,90;
    //a = (i == controller.ci && j == controller.cj) ? constrain((getCard()+1)*60,15,90) : constrain((getCard()+1)*30,5,90);
    a = constrain((getCard(val)+1)*30,5,90);
    b = 90;
    
    if(val == only && distanceBtn.show) {
      s=0;
      a=90;
    }
    
    return color(h,s,b,a);
  }
  
  public int getTextFill(int only)
  {
    int a = (getCard(val)+1)*45;
    int h = 0;
    int s = 0;
    int b = 0;
    
    colorMode(HSB,90);
    
    if(this.i == controller.ci && this.j == controller.cj && val != only  && distanceBtn.show){
      h = 0;
      s = 0;
      b = 0;
      a = 90;
    }
    
    if(val == only && distanceBtn.show) {
      h = 10*val;
      s = 0;
      b = 0;
      a=90;
    }
    
    return color(h,s,b,a);
    
  }
  
  public int mapValue(int v)
  {
    if(v < 0){
      return 0;
    }
    if(v == 0){
      return 1;
    }
    else{
      return 2;
    }
  }
  
  public int highlight(int i, int j)
  {
    //if(debug_board){print("Highlight ");}
    int only = 0;
    int card;
    int option = 0;
    for(int k=1; k<10; k++){
      card = getCard(k);
      //if(i == 2 && j == 2) { println("k: "+k+" card: "+card); }
      only += card;
      option = card>-1 ? k : option;
    }
    //if(i == 2 && j == 2) { println("only: "+only+" option: "+option); }
    if(only < -7){
      //if(debug_board){print("/------------- ONLY OPTION "+option+" --------------/ ");}
      return option;
    }
    else{
      //if(debug_board){print("Option: "+0+" ");}
      return 0;
    }
  }
  
  public int getCard(int k){
    return board.probCard(board.prob(i,j,k));
  }
  
  
  ////////////////////////////////////
  //navigation
  ///////////////////////////////////
  public void draw()
  {
    colorMode(HSB, 90);
    x = parent.x + dx;
    y = parent.y + dy;
    int only = highlight(i,j);
    this.cellFill = getCellFill(i,j,val,only);
    this.textFill = getTextFill(only);
    //cellFill = color(255, 150);
    //println("Cell Width: "+cW);
    if(inLine && val != only && distanceBtn.show){
      textFill = color(90);
    }
    if(this.i == controller.ci && this.j == controller.cj){
      int strokeColor = (getCard(val)+1) > 0 ? 90 : 30;
      stroke(strokeColor);
      strokeWeight(1);
    }
    else{
      strokeWeight(0);
    }
    super.draw();
  }
  
  public void mouseIsReleased(){
    println("Released- drag: "+controller.dragEvent/*+" draw: "+tool.btn.active*/);
    if(controller.dragEvent == false /*&& tool.btn.active == false*/){
      println("Toggle: "+i+","+j+","+val);
      board.toggleProb(i,j,val);
      distance.update();
    }
    /*if(controller.dragEvent == false && tool.btn.active == true){
        println("Add Vector");
        colorMode(HSB,90);
        //tool.addPoint(this,color(hue(cellFill),saturation(cellFill),brightness(cellFill),20) ); //center each vertex on the cell
    }*/
    super.mouseIsReleased();
  }
}
public class DisplayGrid extends Grid {

  //variables for dragging
  int minX, maxX, minY, maxY;
  float dx, dy;
  
  int k;
  DisplayCell[] cells;
  
  boolean pressed = false;
  boolean mouseOver = false;
  boolean dragged = false;
  
  DisplayGrid(int i, int j, int bW, int gS, int margin, Boards b, int k, int offsetX, int offsetY){
       //(int i, int j, int b, int gS, int margin, int offsetX, int offsetY)
    super(i,     j,     bW,    gS,     margin,     offsetX,     offsetY); 
    minX = offsetX + bW/2;
    //maxX = width - offsetY - bW/2; //tweek these
    maxX = offsetX + bW*3 + margin*2 - bW/2;
    minY = offsetY + bW/2;
    maxY = offsetY + bW*3 + margin*2 - bW/2;
    this.k = k;
    //x = gridX(i);
    //y = gridY(j);
    //println("New DisplayGrid "+(i*3+j)+" x: "+x+" y: "+y+" bW: "+bW);
    
    board = b;
    
    //registerMouseEvent(this);
    
    cells = new DisplayCell[0];
    //initialize 81 display cells
    for (int m=0; m<9; m++){
      for (int n=0; n<9; n++){
        //int cellWidth = ((this.bW-(2*this.gS))/9)-2;
        cells = (DisplayCell[]) append(cells, new DisplayCell(board, m, n, k, this));
        //cells[cell_ID(m,n)].cW = cells[cell_ID(m,n)].cH = (bW-(2*gS))/9; //scale the cells to fit the grid.
      }
    }
    
  }
  
/*public void display(){
    textFont(fontA, 9);
    if(debug_board){print("Display ");}
    //the most efficient way would be to have a nested function that loops over the display function for each cell
     for (int i=0; i<9; i++){
      for (int j=0; j<9; j++){
        //for(int k=1; k<10; k++) {
            //displayCell(board.cell[i][j][k],i,j,k,getColor(i,j,k));
            cells[cell_ID(i,j)].display
          }
        //}
     }
 }*/
  
  /////////////////////////////////////
  //Navigation
  /////////////////////////////////////
  public void mouseEvent(MouseEvent event){
      //---------need a parent class that instantiates all of 
      if((selected == null && isMouseover(mouseX, mouseY)) || selected == this){
        //println("Box "+i+","+j+"is being hovered");
        switch(event.getID()){
          
          case MouseEvent.MOUSE_PRESSED:
            mouseIsPressed();
            selected = this;
            dragged = false;
            dx = x-mouseX;
            dy = y-mouseY;
          break;
            
          case MouseEvent.MOUSE_DRAGGED:   
            mouseIsDragged();
          break;
          
          case MouseEvent.MOUSE_RELEASED:
            mouseIsReleased();
            selected = null;
          break;
        }
      }
      else{
        pressed = false;
      }
  }
    
  //rectangle detection
  public boolean isMouseover(float mx, float my) {
   mouseOver = mx > x-bW/2 && mx < x+bW/2 && my > y-bH/2 && my < y+bH/2;
   return mouseOver; 
  }
  
  // Override as required...
  public void mouseIsPressed() {
      println("Grid "+i+","+j+" is being clicked");
      //x = mouseX; //would center the box on the mouse if used with rectMode(CENTER);
      //y = mouseY;
      pressed = true;
      controller.dragEvent = false;
  }
  
  public void mouseIsDragged() {
      println("Grid "+i+","+j+" is being dragged");
      //x = x+mouseX-pmouseX;
      //y = y+mouseY-pmouseY;
      //print("maxX: "+maxX+" minX: "+minX+" maxY: "+maxY+" minY: "+minY);
      x = constrain(mouseX+dx, minX, maxX);
      y = constrain(mouseY+dy, minY, maxY);
      //y = lock(y+mouseY-pmouseY, minY, maxY);
      dragged = true;
      controller.dragEvent = true;
      //println(" x: "+x+" y: "+y);
      //redraw();
      loop();
  }

  public void mouseIsReleased() {
      println("Grid "+i+","+j+" is being released");
      pressed = false;
      DisplayGrid[] pair = detectCollision();
      if(pair.length > 0){
        println("Collision: Grid1: "+pair[0].k+" Grid2: "+pair[1].k);
        //move together
        pair[0].x = pair[1].x;
        pair[0].y = pair[1].y;
        redraw();
      }
      noLoop();
      
  }
  
  public DisplayGrid[] detectCollision() //it might be better just to map all cells to a fixed grid. more aesthetic.
  {
    DisplayGrid[] pair;
    pair = new DisplayGrid[0];
    if(this.dragged){ //for the dragged grid, check all others.
      for(int m=0; m<9; m++){
        if(isMouseover(intGrids[m].x, intGrids[m].y) ){
          if(intGrids[m] != this) {
            pair = (DisplayGrid[]) append(pair, this);
            pair = (DisplayGrid[]) append(pair, intGrids[m]);
          }
        }
      }
    }
    return pair;
  }
  
  /*int lock(int val, int minv, int maxv)   //use constrain function
  {   
    //println("min: "+minv+" max: "+maxv+" val: "+val);
    return  min(max(val, minv), maxv);   
  } */ 
  
  /////////////////////////////////////
  //Math
  /////////////////////////////////////
  
}
  //*********************************************************************************************************************
public class Distances{
  
  //DistanceBtn btn; //now done on draw()
  
  DistObj[] Rows;
  DistObj[] Cols;
  DistObj[] Grps;
  
  DistanceGrid[] grids;
  
  //Label myLabel;
  
  Boards board;
    
  Distances(int bW, int gS, int margin, Boards b){
    board = b;
    
    Rows = new DistObj[0];
    Cols = new DistObj[0];
    Grps = new DistObj[0];
    
    grids = new DistanceGrid[0];
    
    //btn = new DistanceBtn(x,y,cW,cH,"Scan");
    
    //create an array of 9 row objects
    //and an array of col and grp objects
      //each row object contains an array of line objects
        //each line object contains an array of cells and the comparison values
    for(int i=0; i<9; i++){
      Rows = (DistObj[]) append(Rows, new DistObj() );
      Cols = (DistObj[]) append(Cols, new DistObj() );
      Grps = (DistObj[]) append(Grps, new DistObj() );
    }
    
    int offsetX = width-bW-20;
    //int offsetX = intGrids[0].offsetX+(3*(intGrids[0].bW+intGrids[0].margin));
    //int offsetX = controller.offsetX + controller.bW+2;
    int offsetY = controller.offsetY;
    String name = "";
    
    for(int i=0; i<3; i++){
      switch(i){
        case 0:
          //offsetX = controller.offsetX + controller.bW+2;
          //offsetY = controller.offsetY;
          name = "Cells Connected in Rows";
          break;
        case 1:
          //offsetX = controller.offsetX + controller.bW+2;
          //offsetY = controller.offsetY + (controller.bH+2)/2;
          name = "Cells Connected in Columns";
          break;
        case 2:
          //offsetX = controller.offsetX + controller.bW+2;
          //offsetY = controller.offsetY + controller.bH+2;
          name = "Cells Connected in Groups";
          break;
      }
                                                           //(int bW, int gS, int margin, int offsetX, int offsetY, int type, string name)
      grids = (DistanceGrid[]) append(grids, new DistanceGrid(bW,     gS,     margin,     offsetX,     offsetY,     i,        name) ); //row = 0, col = 1, grp = 2   (rowSpacing, colSpacing, rgSpacing, cgSpacing)
    }
    
    registerDraw(this);
    
    //myLabel = new Label("Chained Cells", grids[0].x-(grids[0].bW/2), grids[0].y-(grids[0].bH/2));
  }
  
  public void draw(){
    //update();
  }
  
  public void update(){
    resetDist(); //reset the Distance Objects
      ////////////////////////////////////////////////////////////////////////////////////////////////
    //for cells in row, col, and grp
    for(int i=0; i<9; i++){
      for(int j=0; j<9; j++){
        //int j = 8;
        calculateRow(i,j);
        calculateCol(i,j);
        calculateGrp(i,j);
      }
    }
    //final reduce
    for(int i=0; i<9; i++){
      Rows[i].Reduce(8);
      Cols[i].Reduce(8);
      Grps[i].Reduce(8);
      
      //since all cells can be members of at most one line per row, column, and group we can find the non-overlapping subsets
      Rows[i].Collapse();
      Cols[i].Collapse();
      Grps[i].Collapse();
    }

    //print results
     /*printRows();
     printCols();
     printGrps();*/
     
    //display the results
    for(int i=0; i<9; i++){
      Rows[i].colorCells(i);
      Cols[i].colorCells(i);
      Grps[i].colorCells(i);
    }
  }
  
  public void calculateRow(int i, int j){
    //println("Iteration:: "+i+" Row Lines:: "+Rows[j].myLines.length);
    Cell cell = getCell(i,j,grids[0]);
        if(value(cell) == 0){
          Rows[j].addLine(cell);
           //add line such that line[] == cell[]
          //line id += ... //done on construction
        //println("(After add one) Lines:: "+Rows[j].myLines.length);
          
          Rows[j].Intersect(cell);
            //for all previous lines, prev (int i=lines.length; i>=0; i--)
            //add line such that line[k] = prev[k] || cell[k] == 1 ? 1 : 0; for all k(1-9)
            //line id += line[k]*primes[k] for all k(1-9)
          //println("(After Intersect) Lines:: "+Rows[j].myLines.length);
          
          //this time saving heuristic is based on the premise that we can know whether it is possible to complete the line in question given the number of remaining cells to check.
          Rows[j].Reduce(i);
            //for all lines
            //if cell > line.sum()+line.length-1
            //else remove line
        }
        else{
          Rows[j].maxLine++;
        }
  }
  
  public void calculateCol(int i, int j){
    Cell cell = getCell(i,j,grids[1]);
        if(value(cell) == 0){
          Cols[i].addLine(cell);
          Cols[i].Intersect(cell);
          Cols[i].Reduce(j);
        }
        else{
          Cols[i].maxLine++;
        }
  }
  
  public void calculateGrp(int i, int j){
    Cell cell = getCell(i,j,grids[2]);
        if(value(cell) == 0){
          Grps[grp(i,j)].addLine(cell);
          Grps[grp(i,j)].Intersect(cell);
          Grps[grp(i,j)].Reduce(grpInt(i,j));
        }
        else{
          Grps[grp(i,j)].maxLine++;
        }
  }
  
  public void resetDist(){
    for(int i=0; i<9; i++){
      for(int j=Rows[i].myLines.length; j>0; j--){
        Rows[i].myLines = (LineObj[]) shorten(Rows[i].myLines); //clear all lines
      }
      Rows[i].maxLine = 0;
      /////////////////////////////////
      for(int j=Cols[i].myLines.length; j>0; j--){
        Cols[i].myLines = (LineObj[]) shorten(Cols[i].myLines);
      }
      Cols[i].maxLine = 0;
      /////////////////////////////////
      for(int j=Grps[i].myLines.length; j>0; j--){
        Grps[i].myLines = (LineObj[]) shorten(Grps[i].myLines);
      }
      Grps[i].maxLine = 0;
    }
    for(int i=0; i<3; i++){
      grids[i].clear();
    }
  }
  
  public void printLines(){
    for(int i=0; i<9; i++){
      Rows[i].printLines();
      Cols[i].printLines();
      Grps[i].printLines();
    }
  }
  
  public void printRows(){
    for(int i=0; i<9; i++){
      println("ROW "+i+" ********************************");
      Rows[i].printLines();
    }
    println();
  }
  
  public void printCols(){
    for(int i=0; i<9; i++){
      println("Col "+i+" ********************************");
      Cols[i].printLines();
    }
    println();
  }
  
  public void printGrps(){
    for(int i=0; i<9; i++){
      println("Grp "+i+" ********************************");
      Grps[i].printLines();
    }
    println();
  }
    
  //---------------
    
  public Cell getCell(int i, int j){
    return (Cell) controller.cells[(i*9)+j];
  }       
   
  public Cell getCell(int i, int j, DistanceGrid g){
    return (Cell) g.cells[(i*9)+j];
  }  
  
  public int grp(int i, int j)
  {
    return (floor(i/3)*3)+floor(j/3);
  }
  
  public int grpInt(int i, int j)
  {
    return (i%3)*3+(j%3);
  }
  
  public int value(Cell d)
  {
    return board.cell[d.i][d.j][0];
  }
  
  public LineObj getLine(int i, int j, LineObj[] myLines){
    LineObj theLine = null;
    //println("Found "+myLines.length+" lines in "+place);
    for(int l=0; l<myLines.length; l++){ //for line in row
      for(int c=0; c<myLines[l].length; c++){ //for cells in line
        if(myLines[l].cells[c].i == i && myLines[l].cells[c].j == j){
          if(theLine == null || (myLines[l].length < theLine.length)){
            theLine = myLines[l]; 
          }
        }
      }
    }
    return theLine;
  }
}

//*********************************************************************************************************************
public class DistObj{
  LineObj[] myLines; 
  
  int maxLine = 0;
  //int a = 0; //active line
  
  DistObj(){
    myLines = new LineObj[0];
  }
  
  ////////////////////////
  //Methods
  ////////////////////////
  
  public void Intersect(Cell d){
    //println("Intersect");
    for(int l=myLines.length-2; l>=0; l--){
      //printLine(l);
      duplicateLine(l);
      int newLine = myLines.length-1;
      //println("Intersect Line "+int(newLine+1)+" with Cell "+d.i+","+d.j);
      //try{
        myLines[newLine].intersect(d);
      /*}
      catch(Exception e){
        println(e.getMessage());
        //removeLine(newLine);
      }*/
      myLines[newLine].addPoint(d);
      //println();
      //printLine(l);
      //printLine(newLine);
      //println();
    }
  }
  
  public void Reduce(int c){ //the current cell
    //if cell > line.sum()+line.length-1
      //else remove line
    for(int l=myLines.length-1; l>=0; l--){
      LineObj thisLine = myLines[l];
      thisLine.update();
      int needed = thisLine.sum - thisLine.length; //what we need minus that which we have.
      int remaining = 8-c;
      boolean remove = remaining < needed ? true : false; //screens out lines could never become complete
      boolean fixed = thisLine.sum == 1 || thisLine.sum == 9 || thisLine.sum == 8 ? true : false; //screens out lines that prove a cell is a fixed number
    //println("Reduce Line "+int(l+1)+" : (remaining < needed) "+remaining+" < "+needed+"  Remove: "+remove+"  Fixed: "+fixed); //added one to l to enhance readability
      if(remove || fixed){
        //8 - c < sum - length 
        //if the number of cells remaining is less than the target number of cells minus the current number of cells remove the line.
        //a = l; //set the active line to the current line
      //printLine(l);
        removeLine(l); //remove the active line
      }
      else{
      //printLine(l);
      }
      
      if(c == 8 && thisLine.length == 9-maxLine && myLines.length > 0){ //9-maxLine is the longest possible chain. It has all the cells that are not explicitely defined. 
        //println("Remove Max: "+l+" :: "+thisLine.length+" = "+int(9-maxLine));
        removeLine(l);
      }
    }
  }
  
  public void Collapse(){
    LineObj child, parent;
    boolean full = false;
    
    //collapse lines
    if(myLines.length > 0){
    for(int l=0; l<myLines.length; l++){ //for all lines
      for(int n=0; n<myLines.length; n++){ //for all other lines
        if(l != n && myLines[n].cells.length > 0 && myLines[l].cells.length > 0){
          
          //arrange lines
          LineObj[] lines = arrange(myLines, n , l);
          
          //if the lines are of equal length set flag for a full intersect
          full = (myLines[n].length == myLines[l].length) ? true : false;
          
         //println("\nCompare line "+l+" with line "+n); 
          
          //do the comparison
         comparison(lines[0], lines[1], full);
         
         //fix flags
         fixFlags(lines[0], lines[1], full);
          
        }
      }
    }
    
    //update all lines
    for(int l=0; l<myLines.length; l++){
      myLines[l].update();
    }
    }
  }
  
  public void comparison(LineObj child, LineObj parent, boolean full){
     int pStart = 0; //begin at the start
     int cStart = 0; //not necessary
          for(int c=cStart; c<child.cells.length; c++){ //for all cells in smaller
            for(int p=pStart; p<parent.cells.length; p++){ //search in the larger
              //println("Child: "+c+" Length: "+child.cells.length+" Parent: "+p+" Length: "+parent.cells.length);
              if(child.cells[c] == parent.cells[p]){
                if(parent.cells.length > 1){
                  parent.removePoint(p);
                  pStart = p;
                }
                else{full = true;}
                if(full){
                  child.removePoint(c);
                  c--; //decriment c to adjust for the removed point
                }
                break;
              }
            } 
          }
  }
  
  public LineObj[] arrange(LineObj[] myLines, int n, int l){
    LineObj[] lines = new LineObj[2];
    if(myLines[n].length < myLines[l].length){ //subtract the smaller from the larger
      lines[0] = myLines[n];
      lines[1] = myLines[l];
    }
    else{ //the previous line is greater or equal to the former in length
      lines[0] = myLines[l];
      lines[1] = myLines[n];
    }
     return lines; 
  }
  
  public void fixFlags(LineObj child, LineObj parent, boolean full){
    boolean toggle;
    
    for(int i = 0; i<9; i++){
        //if both flags are one then we need to toggle one of them off.
        toggle = parent.flags[i] == 1 && child.flags[i] == 1 ? true : false;
        
        //if this isn't the last possibility of the parent
        if(parent.sum() > 1){
          //make the change
          //println("toggle parent flag "+i);
          parent.flags[i] = toggle ? 0 : parent.flags[i]; //else leave it alone.
        }
        //otherwise subtract it from the child
        else{ full = true; }
        
        if(full){
          //println("toggle child flag "+i);
          child.flags[i] = toggle ? 0 : child.flags[i];
        }
    }
  }
  
  public void addLine(Cell d){
    //if there is a line and it has points or there isn't a line
    //if(myLines.length < 1 || myLines.length > 0 && myLines[a].length > 0){
      appendLines(d);
    //}
  }
  
  public void appendLines(Cell d){
    //println("Append Line");
    myLines = (LineObj[]) append(myLines, new LineObj(d) );
    //updateActive();
    //toggleLines(myLines[myLines.length-1]);
  }
  
  public void duplicateLine(int l){
    //print("Duplicate Line "+int(l+1)+" : start "+myLines.length);
    myLines = (LineObj[]) append(myLines, new LineObj() ); //expand the array by one
    //println(" End "+myLines.length);
    int newLine = myLines.length-1;
    for(int i=0; i<9; i++){
      myLines[newLine].flags[i] = myLines[l].flags[i];
    }
    for(int i=0; i<myLines[l].cells.length; i++){
      myLines[newLine].cells = (Cell[]) append(myLines[newLine].cells, myLines[l].cells[i]);//fill the new array position with the object to be duplicated
    }
    myLines[newLine].update();
    //printLine(l);
    //printLine(newLine);
  }
  
  public void removeLine(int l){
  //print("  Remove Line "+int(l+1)+" of "+myLines.length); //added one to the comments to enhance readability
    if(l < myLines.length-1){ //if the line is not the last one
      for(int i=l; i<myLines.length-1; i++){
        //print(" ( line "+int(i+1)+" = line "+int(i+2)+" )");
        myLines[i] = myLines[i+1];
        myLines[i].update();
      }
    }
    else{ //otherwise the line is the last one
      
      myLines[l] = null;
    }
    if(myLines.length > 0){
      //shorten the array
      myLines = (LineObj[]) shorten(myLines);
    }
  //println(" ( Array Length: "+myLines.length+" )");
    //printLines();
  }

  ////////////////////////
  //Display Methods
  ////////////////////////
  
  public void colorCells(int r){
    colorMode(HSB, 90);
    float a = 0;
    float strokeColor = 30;
    for(int l=0; l<myLines.length; l++){
      LineObj thisline = myLines[l];
      //a += 30;
      //float cHue = hue(thisCell.cellColor);
      float rHue = (((r+l)*70)%90); //uses *70%90 to have the line colors be non-contiguous +l*9
      //float h = cHue == 0 ? rHue : cHue+20 ;
      float h = rHue;
      float s = 45;
      float b = 90;
      a = 50-(20*(l%2));
      strokeColor = 90;
        
      for(int c=0; c<thisline.length; c++){
        DistanceCell thisCell = (DistanceCell) thisline.cells[c];
        //println("Line: "+l+" hue: "+rHue+" Saturation: "+s+" hue: "+rHue);
        thisCell.cellColor = color(h, s, b, a);
        thisCell.strokeColor = color(strokeColor);
      }
    }
  }
  
  public void printLines(){
    for(int i=0; i<myLines.length; i++){
      print("Line "+PApplet.parseInt(i+1)+":: ");
      myLines[i].printCells();
      println();
    }
  }
  
  public void printLine(int l){
    print("Line "+PApplet.parseInt(l+1)+":: ");
      myLines[l].printCells();
      println();
  }
}
//*********************************************************************************************************************
public class LineObj{
  int[] flags = new int[9];
  int[] flagsDown = new int[9];
  int id = 0;
  int sum;
  int length;
  int[] primes = {2,3,5,7,11,13,17,19,23};
  Cell[] cells;
  
  LineObj(Cell d){
    cells = new Cell[0];
    init(d);
    addPoint(d);
  }
  LineObj(){
    cells = new Cell[0];
  }
  
  public void init(Cell d){
    for(int k=0; k<9; k++){
      flags[k] = bin(d.i, d.j, k+1);
      flagsDown[k] = bin(d.i, d.j, k+1);
    }
  }
  
  public void update(){
    id = id();
    sum = sum();
    length = length();
  }
  
  public int bin(int i, int j, int k){
    return board.binaryCard(board.probCard(board.prob(i,j,k)));
  }
  
  public int value(Cell d){
    return controller.board.cell[d.i][d.j][0];
  }
  
  public void addPoint(Cell d){
    this.cells = (Cell[]) append(this.cells, d);
    update();
    //printCells();
  }
  
  public void removePoint(int p){
    if(p < cells.length-1){
      for(int c=p; c<cells.length-1; c++){
        cells[c] = cells[c+1];
      }
    }
    if(cells.length>0){
      //println("Shorten");
      cells = (Cell[]) shorten(cells);
    }
  }
  
  public void intersect(Cell d)
  {
    //print("Start  ");
    //printFlags();
    for(int k=0; k<9; k++){
      this.flags[k] = (this.flags[k] == 1 || bin(d.i, d.j, k+1) == 1) ? 1 : 0;
      flagsDown[k] = (this.flags[k] == 0 || bin(d.i, d.j, k+1) == 0) ? 0 : 1;
    }
  }
  
  public int sum(){
    int sum = 0;
    for(int k=0; k<9; k++){
      sum += flags[k];
    }
    return sum;
  }
  
  public int length(){
    return cells.length;
  }
  
  public int id(){
    int id = 1;
    for(int k=0; k<9; k++){
      id = flags[k] == 1 ? id*primes[k] : id;
    }
    return id;
  }
  
  public void printCells(){
    print("\t Sum: "+sum+"\t ID: "+id+"\t Line Length: "+length+"\t");
    printFlags();
    print("\t");
    for(int c=0; c<this.length; c++){
      print(" | "+this.cells[c].i+","+this.cells[c].j);
    }
    
    
  }
  
  public void printFlags(){
    for(int c=0; c<9; c++){
      print(this.flags[c]+",");
    }
    printFlagsDown();
  }
  
  public void printFlagsDown(){
    print(" :: ");
    for(int c=0; c<9; c++){
      print(this.flagsDown[c]+",");
    }
  }
  
  public int[] possibilities(){
    int[] p = new int[1];
    p[0] = 0; //sum position
      for(int k=0; k<9; k++){
        if(flags[k] == 1){
          p[0]++; //increment the summ
          p = append(p, k+1); //add the current k to the array
        }
      }
    return p;
  }
  
}

//---------------------------------------------------------------------------------------------------------
//*********************************************************************************************************************
public class DistanceCell extends Cell
{
  int cellColor;
  int strokeColor;
  int type;
  
  DistanceCell(int i, int j, int k, int type, DistanceGrid parent){
    super(i, j, k, (Grid) parent);
    
    this.type = type;
    //y += (j*rS) + floor(j/3)*rgS + 16;
    //x += (i*cS) + floor(i/3)*cgS + 16;
    switch(type){
      case 0:
      //y = controller.cells[i*9+j].y; //-9 to top align
      break;
      case 1:
      //x = controller.cells[i*9+j].x;
      break;
      case 2:
      break;
    }
    registerDraw(this);
  }
  
  public int getColor(){
    /*colorMode(HSB, 90);
    float h = hue(cellColor);
    float s = saturation(cellColor);
    float b = brightness(cellColor);;
    float a = alpha(cellColor);

    //a += (i == controller.ci && j == controller.cj) ? 30 : 0; //just update the alpha for the current cell
    
    return color(h,s,b,a);*/
    return cellColor;
  }
  
  public void mouseIsReleased()
  {
    controller.switchFocus(i,j);
    highlightLine();
    super.mouseIsReleased();
  }
  
  public void draw(){
    cellFill = getColor();
    if(this.i == controller.ci && this.j == controller.cj){
      stroke(strokeColor);
      strokeWeight(1);
    }
    super.draw();
  }
  
  public void highlightLine(){
    //get the line of interest
    LineObj[] myLines = null;
    switch(type){
      case 0:
      myLines = distance.Rows[j].myLines;
      break;
      case 1:
      myLines = distance.Cols[i].myLines;
      break;
      case 2:
      int grp = ((i/3)*3)+(j/3);
      myLines = distance.Grps[grp].myLines;
      break;
    }
    //proceed only if there are lines to highlight
    if(myLines.length > 0){
      LineObj theLine = distance.getLine(i,j,myLines);
    
      //clear previous formatting
      clearLine();
    
      //get possibilities
      int[] possible = theLine.possibilities();
      
      //update new formatting
      for(int c=0; c<theLine.length; c++){
        int i = theLine.cells[c].i;
        int j = theLine.cells[c].j;
        //for(int g=0; g<9; g++){ //use this for the naive coloring
        for(int g=1; g<possible.length; g++){ //use this to color just the subset cells
          intGrids[possible[g]-1].cells[i*9+j].inLine = true;
        }
      }
    }
    else{
      clearLine();
    }
  }
  
  /*void displayOver(){
    rectMode(CENTER);
    fill(cellFill);
    rect(x,y,cW,cH);
  }*/
}
//---------------------------------------------------------------------------------------------------------
public class DistanceGrid extends Grid
{
  DistanceCell[] cells;
  
  Label myLabel;
  
  DistanceGrid(int bW, int gS, int margin, int offsetX, int offsetY, int type, String name){ //give it one number, pass to the super.j to display vertically (int rS, int cS, int rgS, int cgS)
       //(int i, int j, int b, int gS, int margin, int offsetX, int offsetY)   
    super(0,     type,     bW,    gS,     margin,         offsetX,     offsetY);
    //intitiallize 81 DistanceCells
    cells = new DistanceCell[0];
    for(int i=0; i<9; i++){
      for(int j=0; j<9; j++){
        int cellWidth = ((this.bW-(2*this.gS))/9)-2;
        cells = (DistanceCell[]) append(cells, new DistanceCell(i,j,0,type,this) );
        colorMode(HSB, 90);
        cells[(i*9)+j].cellColor = color(0,0,45,10); //initialize to a grey value
        cells[(i*9)+j].strokeColor = color(30);
      }
    }
    
     myLabel = new Label(name, x-(bW/2), y-(bH/2),10);
  }
  
  public void clear(){
    for(int i=0; i<81; i++){
      colorMode(HSB, 90);
      cells[i].cellColor = color(0,0,45,10); //initialize to a grey value
      cells[i].strokeColor = color(30);
    }
  }
  
  public void register(){
    for(int c=0; c<81; c++){
      registerDraw(cells[c]);
    }
    registerDraw(myLabel);
  }
  
  public void unregister(){
    for(int c=0; c<81; c++){
      unregisterDraw(cells[c]);
    }
    unregisterDraw(myLabel);
  }

}
//---------------------------------------------------------------------------------------------------------


//*************************************************************** CLEAR *****************************************************************//

public class Clear extends ControlButton
{
  Tool parent;
  Clear(float x, float y, int cW, int cH, String val, Tool p)
  {
    super(x,y,cW,cH,val);
    parent = p;
    registerDraw(this);
  }
  
  public void mouseIsReleased(){
    parent.removePoint();
    
    super.mouseIsReleased();
  }
  
  public void draw()
  {
    if(parent.btn.active){
      super.draw();
    }
  }
  
}

//*************************************************************** NEW *****************************************************************//

public class NewLine extends ControlButton
{
  Tool parent;
  NewLine(float x, float y, int cW, int cH, String val, Tool p)
  {
    super(x,y,cW,cH,val);
    parent = p;
    registerDraw(this);
  }
  
  public void mouseIsReleased(){
    parent.addLine();
    super.mouseIsReleased();
  }
  
  public void draw()
  {
    if(parent.btn.active){
      super.draw();
    }
  }
  
}
    
//*************************************************************** TOOL BUTTON *****************************************************************//

public class ToolButton extends ControlButton
{
  Tool parent;
  ToolButton(float x, float y, int cW, int cH, String val, Tool p)
  {
    super(x,y,cW,cH,val);
    parent = p;
    registerDraw(this);
  }
  
    /////////////////////////////////////
  //Registered Events
  ////////////////////////////////////
  
  public void mouseIsReleased(){
    active = !active;
    super.mouseIsReleased();
  }
  
  public void draw()
  {
    //println("Draw Object");
    //draw drawer background
    if(active){
      float bH = (parent.myLines.length+2)*30+20;
      float bY = y+bH/2;
      fill(200);
      rectMode(CENTER);
      rect(x,bY,cW,bH);
    }
    super.draw();
  }
  
}
    
//***************************************************************** TOOL ***************************************************************//

public class Tool
{
  Lines[] myLines;
  int a = 0; //active line
  
  ToolButton btn;
  Clear clear;
  NewLine newLine;
  
  Tool(float x, float y, int cW, int cH, String val)
  {
    myLines = new Lines[0];   
   
    btn = new ToolButton(x,y,50,20,"Draw",this);
    newLine = new NewLine(x,y+30,50,20,"New",this);
    clear = new Clear(x,y+60,50,20,"Undo",this);
  }
  
  public void appendLines(){
    myLines = (Lines[]) append(myLines, new Lines(color(255), btn.x, btn.y, btn.cW, btn.cH, myLines.length+1,this ) );
    //updateActive();
    toggleLines(myLines[myLines.length-1]);
  }
  
  public void addPoint(DisplayCell d, int c)
  {
    println("Length: "+myLines.length);
    println("A: "+a);
    //if there isn't a line, add one
    if(myLines.length < 1){
      addLine();
    }
    //add the Cell to the current line
    
    myLines[a].addPoint( (Cell) d);
    //if the Cell is in the first position use it's color for the line.
    if(myLines[a].pLine.length == 1) {
      myLines[a].c = c;
    }
  }
  
  public void addLine(){
    //if there is a line and it has points or there isn't a line
    if(myLines.length < 1 || myLines.length > 0 && myLines[a].pLine.length > 0){
      appendLines();
    }
  }
  
  public void removePoint(){
    println("Length: "+myLines.length);
    //if the line has points
    if(myLines.length > 0){
      if(myLines[a].pLine.length > 1){
        //remove the last point
        myLines[a].removePoint();
      }
      else { 
        //otherwise remove the line
        println("remove line");
        myLines[a].removePoint();
        removeLine();
      }
    }
  }
  
  public void removeLine(){
    //if there is a line
    if(myLines.length > 1){
      myLines[a].display=false;
      //shift all the entries above the active line down
      for(int i=a; i<myLines.length-1; i++){
        myLines[i] = myLines[i+1];
        myLines[i].update(i+1);
      }
    }
    else if(myLines.length == 1){
      myLines[a] = null;
    }
      //shorten the array
      myLines = (Lines[]) shorten(myLines);
      println("A: "+a+" Length: "+myLines.length);
    if(a >= myLines.length && myLines.length > 0){
      a = myLines.length-1;
      toggle(a,true);
    }
    else if(myLines.length > 0){
      toggleLines(myLines[0]); 
    } 
      
  }
  
  public void toggle(int num, boolean state){
    myLines[num].btn.active = state;
  }
  
  public void toggleLines(Lines l) {
    //set all lines other than the new line to inactive
    /*for(int i=0; i<myLines.length && i != l.val; i++){
      myLines[i].active = false;
    }*/
    if(myLines.length > 0 && a < myLines.length) {
      toggle(a,false);
    }
    a = l.btn.val-1;
    println("Set Line "+a+" true.");
    toggle(a,true);
  }
  
}

//************************************************************ lineButton ********************************************************************//
public class LineBtn extends ControlButton
{
  Lines parent;
  Tool tParent;
  LineBtn(float x, float y, int cW, int cH, int val, Lines p,Tool t)
  {
    super(x,y,cW,cH,val);
    parent = p;
    tParent = t;
    registerDraw(this);
  }
  
  public void mouseIsReleased()
  {
    tParent.toggleLines(parent);
    active = true;
    super.mouseIsReleased();
  }
  
  public void draw()
  {
    println("Line: "+val+" Active: "+active+" Display: "+parent.display);
    if(parent.display && tParent.btn.active){
      parent.cCurrent = active ? parent.cActive : parent.c;
    
      colorMode(RGB,255);
      stroke(parent.cCurrent);
      int myStroke = active ? 2 : 1;
      strokeWeight(myStroke);
      
      int myFill = active ? color(255,255,255,255) : color(255,255,255,10);
      fill(myFill);
      //if there is an ellipse to draw
      if(parent.pLine.length>0){
        //draw the first ellipse
        ellipse(parent.pLine[0].x, parent.pLine[0].y,10,10);
      }
      //draw the remaining line
      fill(0,0,0,0);
      for(int i=0; i < parent.pLine.length-1; i++){
        ellipse(parent.pLine[i+1].x, parent.pLine[i+1].y,10,10);
        //println("Draw line: "+myLines[j].pLine[i].x +","+ myLines[j].pLine[i].y +","+ myLines[j].pLine[i+1].x +","+ myLines[j].pLine[i+1].y);
        line(parent.pLine[i].x, parent.pLine[i].y, parent.pLine[i+1].x, parent.pLine[i+1].y);
      }
    
    super.draw();
    }
  }
  
}

//************************************************************ pLines ********************************************************************//
public class Lines
{
  Cell[] pLine;
  int cActive;
  int c;
  int cCurrent;
  boolean display;
  
  int val;
  String value;
    
  Tool parent;
  
  LineBtn btn;
  
  Lines(int c, float x, float y, int cW, int cH, int val, Tool p){
    parent = p;
    btn = new LineBtn(x, (PApplet.parseInt(val)*30 + 60 + y), cW, cH, val, this, p);
    println("pLine: "+btn.val+" y: "+this.btn.y);
    
    pLine = new Cell[0];
    this.cActive = c;
    btn.active = true;
    display = true;
  }
  
  Lines(int c){
    this.cActive = c;
    pLine = new Cell[0];
  }
  
  public void addPoint(Cell d)
  {
    pLine = (Cell[]) append(pLine, d);
  }
  
  public void removePoint()
  {
    pLine = (Cell[]) shorten(pLine);
    println("remove point pLine Length: "+pLine.length);
  }
  
  public void update(int i){
    val = i;
    value = str(i);
    if(btn != null){
      btn.val = i;
      btn.value = str(i);
      btn.y = parent.btn.y + PApplet.parseInt(i)*30 + 60;
    }
  }
  
}

//***********************************************************************************************************

public class File_In extends ControlButton
{
  XMLElement file;
  
  File_In(float x, float y, int cW, int cH, String val)
  {
    super(x,y,cW,cH,val);
  }
  
  public void mouseIsReleased(){  
    file = null;  
    String path = ( loadFile(new Frame(), "Open a sudoku XML file", "/Users/myName/Desktop/", ".xml") ); //feed this path into the readFile function;
    //parse the path for a file type. Direct file to appropriate input function. Only handles xml currently.
      openLoadFile(path);
      readFile();
      readFlags();
      distance.update();
    super.mouseIsReleased();
  }
  
  public void openLoadFile(String path){
   file = new XMLElement(app, path);
  }
  
  public void readFile(){
    for(int i = 0; i < file.getChildCount();i++){
      XMLElement cell;
      cell = file.getChild(i);
      
      updateCell( i,cell.getInt("value") );
      // import the other elements?
    }
  }
  
  public void updateCell(int cID, int v){
    controller.cells[cID].update(v);
  }
  
  public void readFlags(){
    for(int c = 0; c < file.getChildCount();c++){
      XMLElement cell;
      cell = file.getChild(c);
      XMLElement flags;
      flags = cell.getChild(0);
      for(int k=1; k<10; k++){
        int flag = flags.getInt(str(k));
        int i = controller.cells[c].i;
        int j = controller.cells[c].j;
        //int boardFlag = board.binaryCard(board.probCard(board.prob(i,j,k)));
        int boardFlag = board.prob(i,j,k);
        
        println("Flag: "+flag+" Board: "+boardFlag);
        if( flag != boardFlag ){ //we only need to scan for flags that were toggled off.
          println("Toggle Flag");
          //board.toggleProb(i,j,k);
          board.cell[i][j][k] = flag;
        }
      }
      
      //updateCell( int((i*9)+j),cell.getInt("value") );
      // import the other elements?
    }
  }
  
  public void clearBoard(){
    for(int i=0; i<81; i++){
      updateCell(i, 0);
    }
  }
  
  public String loadFile (Frame f, String title, String defDir, String fileType) {
    FileDialog fd = new FileDialog(f, title, FileDialog.LOAD);
    fd.setFile(fileType);
    fd.setDirectory(defDir);
    fd.setLocation(50, 50);
    fd.show();
    String path = fd.getDirectory()+fd.getFile();
    return path;
  }
  
}

//---------------------------------------------------------------------------------------------------------

public class File_Out extends ControlButton
{
  Boards board;
  XMLElement file;
  PrintWriter xmlfile;
  
  File_Out(float x, float y, int cW, int cH, String val, Boards b)
  {
    super(x,y,cW,cH,val);
    board = b;
  }
  
  public void mouseIsReleased(){
    String path = "";
    path = ( saveFile(new Frame(), "Save as", "/Users/myName/Desktop/", ".xml") ); //feed this path into the readFile function;
    if (path == null) {
    // If a file was not selected
    println("No output file was selected...");
  } else {
    // If a file was selected, print path to folder
    println(path);
    output(path);
  }
    super.mouseIsReleased();
  }
  
  public void output(String path){
    String[] words = splitTokens(path, "/.");
    String name = words[words.length-2];
    file = new XMLElement();
    file.setName("Sudoku_Board");
    xmlfile = createWriter(path);
    
    //try loading the saved file
    try
    {
      XMLWriter schreibXML = new XMLWriter(xmlfile) ;  
  
      for(int i=0; i<9; i++){
        for(int j=0; j<9; j++){
          OutputCell(i,j);
        }
      }
  
      schreibXML.write(file);

      xmlfile.flush();
      xmlfile.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    } 
  }

  
  public void OutputCell(int i, int j){
      //add new cell
      XMLElement cell = new XMLElement();
      cell.setName("cell");
      //add its i
      cell.setInt("i",i);
      //add its j
      cell.setInt("j",j);
      //add its value
      cell.setInt("value",board.cell[i][j][0]);
      //add its flags
      XMLElement flag = new XMLElement();
      flag.setName("flag");
      for(int k=1; k<10; k++){
        //flag.setInt(str(k),board.binaryCard(board.probCard(board.prob(i,j,k))) );
        flag.setInt(str(k),board.prob(i,j,k) );
      }
      cell.addChild(flag);
      file.insertChild(cell,cellID(i,j));
  }
  
  public int cellID(int i, int j){
    return (i*9)+j;
  }
  
  public String saveFile (Frame f, String title, String defDir, String fileType) {
    FileDialog fd = new FileDialog(f, title,    FileDialog.SAVE);
    fd.setFile(fileType);
    fd.setDirectory(defDir);
    fd.setLocation(50, 50);
    fd.show();
    String path = fd.getDirectory()+fd.getFile();
    return path;
  }

}
public class Grid{
  
  //positioning variables
  float x;
  float y;
  int i;
  int j;
  int bW; //board width
  int bH; //board height
  int gS; //spacing between groups
  int margin; //the offset 
  int offsetX; //the padding between grids
  int offsetY;
  //int ci; //current cell i
  //int cj;
  int cID = 0;
  
  Boards board;
  
  Grid(int i, int j, int b, int gS, int margin, int offsetX, int offsetY) {
    this.i = i;
    this.j = j;
    this.bW = this.bH = b;
    this.margin = margin;
    //this.ci = ci;
    //this.cj = cj;
    this.gS = gS;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    
    this.x = gridX(this.i);
    this.y = gridY(this.j);
  }
    
  /*void update(int i, int j){
    ci = i;
    cj = j;
  }*/
  
  public float gridX(int i){
  //i = (i+9) % 9; //when using arrows to navigate, wraps around the square.
  return i*(bW+margin)+offsetX+bW/2; //+bW/2 <--- to provide center x & y. this is better for collision detection
  }

  public float gridY(int j){
    //j = (j+9) % 9;
    return j*(bH+margin)+offsetY+bH/2;
  }
  
  public int cell_ID (int i, int j)
  {
    return (i*9)+j;
  }
  
}
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------

public class Label{
  
  String value;
  float x, y, parentX, parentY;
  int fontSize;
  int labelFill;
  
  Label(String v, float parentX, float parentY, int s){
    this.value = v;
    colorMode(RGB,255);
    labelFill = color(255);
    fontSize = s;
    update(parentX,parentY);    
    registerDraw(this);
  }
  
  public void update(){
    textFont(fontA, fontSize);
    x = parentX + textWidth(value)/2;
    y = parentY - (fontSize/2) - 5;
  }
  
  public void update(float x, float y){
    parentX = x;
    parentY = y;
    update();
  }
  
  public void draw(){
    fill(labelFill);
    noStroke();
    textFont(fontA, fontSize);
    textAlign(CENTER,CENTER);
    text(value, x, y);
  }
  
}
  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "Sudoku_v3_18" });
  }
}
