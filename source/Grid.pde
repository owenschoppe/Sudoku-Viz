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
  
  float gridX(int i){
  //i = (i+9) % 9; //when using arrows to navigate, wraps around the square.
  return i*(bW+margin)+offsetX+bW/2; //+bW/2 <--- to provide center x & y. this is better for collision detection
  }

  float gridY(int j){
    //j = (j+9) % 9;
    return j*(bH+margin)+offsetY+bH/2;
  }
  
  int cell_ID (int i, int j)
  {
    return (i*9)+j;
  }
  
}
