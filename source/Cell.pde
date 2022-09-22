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
  
  float cellX(int i)
  {
  i = (i+9) % 9; //when using arrows to navigate, wraps around the square.
  return parent.x+(i*(cW+1))+(floor(i/3)*parent.gS)-(parent.bW/2)+(cW/2); //-(parent.bW/2)+(cW/2) <------for CENTER
  }

  float cellY(int j)
  {
    j = (j+9) % 9;
    return parent.y+(j*(cW+1))+floor(j/3)*parent.gS-(parent.bW/2)+(cW/2);
  }
  
  int cellWidth()
  {
    return ((parent.bW-(2*parent.gS))/9)-2;
  }
  
  void mouseEvent(MouseEvent event)
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
  color cellFill;
  color textFill;
  
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
  boolean isMouseover(float mx, float my) {
   mouseOver = mx > x-cW/2 && mx < x+cW/2 && my > y-cH/2 && my < y+cH/2;
   return mouseOver; 
  }
  
  // Override as required...
  void mouseIsPressed()
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

  void mouseIsReleased() 
  {
      println("Cell "+i+","+j+" is being released");
      pressed = false;
      redraw();
  }
  
  void draw()
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
  
  void displayDefault()
  {
    //println("Display Default: "+i+","+j+","+val);
    rectMode(CENTER);
      fill(cellFill);
      rect(x,y,cW,cH);
    
  }
  
  void displayClicked()
  {
    rectMode(CENTER);
    stroke(90);
    strokeWeight(2);
    //noStroke();
    fill(cellFill);
    rect(x,y,cW,cH);
  }
  
  void displayOver()
  {
    rectMode(CENTER);
    stroke(90,60);
    strokeWeight(1);
    //noStroke();
    fill(cellFill);
    rect(x,y,cW,cH);
  }
  
  void displayText()
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
