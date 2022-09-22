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
  
  void draw(){
    //update();
  }
  
  void update(){
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
  
  void calculateRow(int i, int j){
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
  
  void calculateCol(int i, int j){
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
  
  void calculateGrp(int i, int j){
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
  
  void resetDist(){
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
  
  void printLines(){
    for(int i=0; i<9; i++){
      Rows[i].printLines();
      Cols[i].printLines();
      Grps[i].printLines();
    }
  }
  
  void printRows(){
    for(int i=0; i<9; i++){
      println("ROW "+i+" ********************************");
      Rows[i].printLines();
    }
    println();
  }
  
  void printCols(){
    for(int i=0; i<9; i++){
      println("Col "+i+" ********************************");
      Cols[i].printLines();
    }
    println();
  }
  
  void printGrps(){
    for(int i=0; i<9; i++){
      println("Grp "+i+" ********************************");
      Grps[i].printLines();
    }
    println();
  }
    
  //---------------
    
  Cell getCell(int i, int j){
    return (Cell) controller.cells[(i*9)+j];
  }       
   
  Cell getCell(int i, int j, DistanceGrid g){
    return (Cell) g.cells[(i*9)+j];
  }  
  
  int grp(int i, int j)
  {
    return (floor(i/3)*3)+floor(j/3);
  }
  
  int grpInt(int i, int j)
  {
    return (i%3)*3+(j%3);
  }
  
  int value(Cell d)
  {
    return board.cell[d.i][d.j][0];
  }
  
  LineObj getLine(int i, int j, LineObj[] myLines){
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
  
  void Intersect(Cell d){
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
  
  void Reduce(int c){ //the current cell
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
  
  void Collapse(){
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
  
  void comparison(LineObj child, LineObj parent, boolean full){
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
  
  LineObj[] arrange(LineObj[] myLines, int n, int l){
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
  
  void fixFlags(LineObj child, LineObj parent, boolean full){
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
  
  void addLine(Cell d){
    //if there is a line and it has points or there isn't a line
    //if(myLines.length < 1 || myLines.length > 0 && myLines[a].length > 0){
      appendLines(d);
    //}
  }
  
  void appendLines(Cell d){
    //println("Append Line");
    myLines = (LineObj[]) append(myLines, new LineObj(d) );
    //updateActive();
    //toggleLines(myLines[myLines.length-1]);
  }
  
  void duplicateLine(int l){
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
  
  void removeLine(int l){
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
  
  void colorCells(int r){
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
  
  void printLines(){
    for(int i=0; i<myLines.length; i++){
      print("Line "+int(i+1)+":: ");
      myLines[i].printCells();
      println();
    }
  }
  
  void printLine(int l){
    print("Line "+int(l+1)+":: ");
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
  
  void init(Cell d){
    for(int k=0; k<9; k++){
      flags[k] = bin(d.i, d.j, k+1);
      flagsDown[k] = bin(d.i, d.j, k+1);
    }
  }
  
  void update(){
    id = id();
    sum = sum();
    length = length();
  }
  
  int bin(int i, int j, int k){
    return board.binaryCard(board.probCard(board.prob(i,j,k)));
  }
  
  int value(Cell d){
    return controller.board.cell[d.i][d.j][0];
  }
  
  void addPoint(Cell d){
    this.cells = (Cell[]) append(this.cells, d);
    update();
    //printCells();
  }
  
  void removePoint(int p){
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
  
  void intersect(Cell d)
  {
    //print("Start  ");
    //printFlags();
    for(int k=0; k<9; k++){
      this.flags[k] = (this.flags[k] == 1 || bin(d.i, d.j, k+1) == 1) ? 1 : 0;
      flagsDown[k] = (this.flags[k] == 0 || bin(d.i, d.j, k+1) == 0) ? 0 : 1;
    }
  }
  
  int sum(){
    int sum = 0;
    for(int k=0; k<9; k++){
      sum += flags[k];
    }
    return sum;
  }
  
  int length(){
    return cells.length;
  }
  
  int id(){
    int id = 1;
    for(int k=0; k<9; k++){
      id = flags[k] == 1 ? id*primes[k] : id;
    }
    return id;
  }
  
  void printCells(){
    print("\t Sum: "+sum+"\t ID: "+id+"\t Line Length: "+length+"\t");
    printFlags();
    print("\t");
    for(int c=0; c<this.length; c++){
      print(" | "+this.cells[c].i+","+this.cells[c].j);
    }
    
    
  }
  
  void printFlags(){
    for(int c=0; c<9; c++){
      print(this.flags[c]+",");
    }
    printFlagsDown();
  }
  
  void printFlagsDown(){
    print(" :: ");
    for(int c=0; c<9; c++){
      print(this.flagsDown[c]+",");
    }
  }
  
  int[] possibilities(){
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
  color cellColor;
  color strokeColor;
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
  
  color getColor(){
    /*colorMode(HSB, 90);
    float h = hue(cellColor);
    float s = saturation(cellColor);
    float b = brightness(cellColor);;
    float a = alpha(cellColor);

    //a += (i == controller.ci && j == controller.cj) ? 30 : 0; //just update the alpha for the current cell
    
    return color(h,s,b,a);*/
    return cellColor;
  }
  
  void mouseIsReleased()
  {
    controller.switchFocus(i,j);
    highlightLine();
    super.mouseIsReleased();
  }
  
  void draw(){
    cellFill = getColor();
    if(this.i == controller.ci && this.j == controller.cj){
      stroke(strokeColor);
      strokeWeight(1);
    }
    super.draw();
  }
  
  void highlightLine(){
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
  
  void clear(){
    for(int i=0; i<81; i++){
      colorMode(HSB, 90);
      cells[i].cellColor = color(0,0,45,10); //initialize to a grey value
      cells[i].strokeColor = color(30);
    }
  }
  
  void register(){
    for(int c=0; c<81; c++){
      registerDraw(cells[c]);
    }
    registerDraw(myLabel);
  }
  
  void unregister(){
    for(int c=0; c<81; c++){
      unregisterDraw(cells[c]);
    }
    unregisterDraw(myLabel);
  }

}
//---------------------------------------------------------------------------------------------------------

