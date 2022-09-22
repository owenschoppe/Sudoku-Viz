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

  int row(int i)
  {
   return floor(i/3);
  }
  
  int col(int i)
  {
   return i%3;
  }
  
  int getGroupFloor_(int i) 
  {
  return floor(i / 3) * 3;
  }
  
  // takes a value in range [0-8] and returns the corresponding group floor {2,5,8}
  int getGroupCeil_(int i) 
  {
    return getGroupFloor_(i) + 3; //shouldn't this be + 2? no because the logic is < not <=
  }
  
  int prob(int i, int j, int k)
  {
    if(k > 0){
      return cell[i][j][k];
    }
    else{
      return 0;
    }
  }
  
  int probCard(int p)
  {
    if(p != 0){
      //println("PROBABILITY CARDINATLITY: "+p/abs(p));
      return p/abs(p); //return -1 or 1
    }
    else{
      return 0;
    }  
  }

  int binaryCard(int p) //return 0 or 1
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
  
  int[] possibilities(int i, int j){
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
  
  void updateK(int i,int j,int k, int delta)
  {
    updateCell(i,j,k,delta);
    updateNeighbors(i,j,k,delta);
  }
  
  void updateCell(int i,int j,int k, int d)
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
  
  void updateNeighbors(int i, int j, int k, int d)
  {
    updateRow(i,j,k,d);
    updateCol(i,j,k,d);
    updateGrp(i,j,k,d);
  }
  
  void updateRow(int x, int y, int k, int d)//row and col functions could be combined into compound
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
  
  void updateCol(int x, int y, int k, int d)
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
  
  void updateGrp(int x, int y, int k, int d) //throws Exception
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
  
  void toggleProb(int i, int j, int k)
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
