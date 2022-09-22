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
  void mouseEvent(MouseEvent event){
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
  boolean isMouseover(float mx, float my) {
   mouseOver = mx > x-bW/2 && mx < x+bW/2 && my > y-bH/2 && my < y+bH/2;
   return mouseOver; 
  }
  
  // Override as required...
  void mouseIsPressed() {
      println("Grid "+i+","+j+" is being clicked");
      //x = mouseX; //would center the box on the mouse if used with rectMode(CENTER);
      //y = mouseY;
      pressed = true;
      controller.dragEvent = false;
  }
  
  void mouseIsDragged() {
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

  void mouseIsReleased() {
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
  
  DisplayGrid[] detectCollision() //it might be better just to map all cells to a fixed grid. more aesthetic.
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
