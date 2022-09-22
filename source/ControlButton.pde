public class ControlButton extends Button
{ 
  String value;
  boolean active = false;
  int textLines = 1;
  
  color BUTTON_UP;
  color BUTTON_DOWN;
  color TEXT_UP;
  color TEXT_DOWN;
  
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
  
  void mouseEvent(MouseEvent event)
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
  
  void draw(){
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
  
  void displayText()
  {
    fill(textFill);
    noStroke();
    textFont(fontA, (cH/textLines)-6);
    textAlign(CENTER,CENTER);
    text(value, x-1, y-1); 
  }
  
  void displayOver()
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
  
  void displayClicked()
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
