
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
  
  void mouseIsReleased(){
    parent.removePoint();
    
    super.mouseIsReleased();
  }
  
  void draw()
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
  
  void mouseIsReleased(){
    parent.addLine();
    super.mouseIsReleased();
  }
  
  void draw()
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
  
  void mouseIsReleased(){
    active = !active;
    super.mouseIsReleased();
  }
  
  void draw()
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
  
  void appendLines(){
    myLines = (Lines[]) append(myLines, new Lines(color(255), btn.x, btn.y, btn.cW, btn.cH, myLines.length+1,this ) );
    //updateActive();
    toggleLines(myLines[myLines.length-1]);
  }
  
  void addPoint(DisplayCell d, color c)
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
  
  void addLine(){
    //if there is a line and it has points or there isn't a line
    if(myLines.length < 1 || myLines.length > 0 && myLines[a].pLine.length > 0){
      appendLines();
    }
  }
  
  void removePoint(){
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
  
  void removeLine(){
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
  
  void toggle(int num, boolean state){
    myLines[num].btn.active = state;
  }
  
  void toggleLines(Lines l) {
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
  
  void mouseIsReleased()
  {
    tParent.toggleLines(parent);
    active = true;
    super.mouseIsReleased();
  }
  
  void draw()
  {
    println("Line: "+val+" Active: "+active+" Display: "+parent.display);
    if(parent.display && tParent.btn.active){
      parent.cCurrent = active ? parent.cActive : parent.c;
    
      colorMode(RGB,255);
      stroke(parent.cCurrent);
      int myStroke = active ? 2 : 1;
      strokeWeight(myStroke);
      
      color myFill = active ? color(255,255,255,255) : color(255,255,255,10);
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
  color cActive;
  color c;
  color cCurrent;
  boolean display;
  
  int val;
  String value;
    
  Tool parent;
  
  LineBtn btn;
  
  Lines(color c, float x, float y, int cW, int cH, int val, Tool p){
    parent = p;
    btn = new LineBtn(x, (int(val)*30 + 60 + y), cW, cH, val, this, p);
    println("pLine: "+btn.val+" y: "+this.btn.y);
    
    pLine = new Cell[0];
    this.cActive = c;
    btn.active = true;
    display = true;
  }
  
  Lines(color c){
    this.cActive = c;
    pLine = new Cell[0];
  }
  
  void addPoint(Cell d)
  {
    pLine = (Cell[]) append(pLine, d);
  }
  
  void removePoint()
  {
    pLine = (Cell[]) shorten(pLine);
    println("remove point pLine Length: "+pLine.length);
  }
  
  void update(int i){
    val = i;
    value = str(i);
    if(btn != null){
      btn.val = i;
      btn.value = str(i);
      btn.y = parent.btn.y + int(i)*30 + 60;
    }
  }
  
}

//***********************************************************************************************************

