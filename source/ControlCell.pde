public class ControlCell extends Cell{

  boolean debug_box = false;
  boolean isFocus;
  int k = 0;
  Boards board;
  ControlGrid parent;
  color HIGH, NORMAL_FILL;
  
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
  
  void update(int k)
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
  
  void updateBoard() throws Exception
  {
      if(debug_box){ println("Try update "+i+","+j+" : "+val); }
      board.update(i,j,val); //attempt the move made by the player
  }
  
  void setValue(int k)
  {  
    val = k;
  }
  
  void clear()
  {
    val = 0;
  }
  
  void setFocus(boolean f)
  {
    isFocus = f;
  }
  
  void mouseIsReleased()
  {
    parent.switchFocus(i,j);
    super.mouseIsReleased();
  }
  
  void draw()
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
  
  void displayOver()
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
  
  void displayClicked()
  {
    //println("Display Default: "+i+","+j+","+val);
    rectMode(CENTER);
      fill(HIGH);
      rect(x,y,cW,cH);
    
  }
}

