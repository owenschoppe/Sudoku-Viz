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
 
  color getCellFill(int i, int j, int val)
  {
    int only = highlight(i,j);
    
    return getCellFill(i,j,val,only);
  }
  
  //-----------
   color getCellFill(int i, int j, int val, int only)
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
  
  color getTextFill(int only)
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
  
  int mapValue(int v)
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
  
  int highlight(int i, int j)
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
  
  int getCard(int k){
    return board.probCard(board.prob(i,j,k));
  }
  
  
  ////////////////////////////////////
  //navigation
  ///////////////////////////////////
  void draw()
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
  
  void mouseIsReleased(){
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
