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
  
  void keyEvent(KeyEvent event){
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
          num = char(event.getKeyCode());
          if(debug){println("Key: "+num);}
          if(num >= '0' && num <= '9'){ 
            println("Integer");  
            cells[cID].update(int(str(num)));
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

void setTarget(int i, int j)
{
  newI = i;
  newJ = j;
  println("New Target: "+i+","+j);
}

void switchFocus(int i, int j)
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

void updateFocus(boolean f)
{
  cells[cID].setFocus(f);
}

//////////////////////////////
//Math
//////////////////////////////

int cellID(int i, int j)
{
  return (i*9)+j;
}

void draw()
{
  switchFocus(newI, newJ);
}
  
}
