public class Description
{
  Boards board;
  float x, y;
  int fontSize;
  int boxWidth;
  int boxHeight;
  
  Description(Boards b){
    board = b;
    registerDraw(this);
    
    x = controller.offsetX;
    y = controller.offsetY + (controller.bH+2);
    
    boxWidth = controller.bW-15;
    boxHeight = height-int(y)-35;
  }
  
  void draw(){
    int ci = controller.ci;
    int cj = controller.cj;
    String statement = "";
    int[] possible = board.possibilities(ci,cj);
    LineObj theLine;
    String place = null;
    
    float y1 = y;
    int bW = boxWidth;
    y1 += fontSize + 20;
    
    theLine = whatChain(ci, cj, distance.Rows[cj].myLines);
    if(theLine != null && theLine.length < possible[0]){ possible = theLine.possibilities(); place = "row"; }
    statement += chainStatement(theLine, "row"); 
    
    theLine = ( whatChain(ci, cj, distance.Cols[ci].myLines));
    if(theLine != null && theLine.length < possible[0]){ possible = theLine.possibilities(); place = "column"; }
    statement +=  chainStatement(theLine, "column");
    
    int grp = ((ci/3)*3)+(cj/3);
    theLine = ( whatChain(ci, cj, distance.Grps[grp].myLines));
    if(theLine != null && theLine.length < possible[0]){ possible = theLine.possibilities(); place = "group"; }
    statement +=  chainStatement(theLine, "group");
    
    drawText( whatPossible(ci,cj, possible, place), x, y, 14, bW);
    drawText(statement, x, y1, 10, bW);
  }
  
  void drawText(String statement, float x, float y, int s, int bW){
    colorMode(HSB, 90);
    fill(90);
    fontSize = s;
    textFont(fontA, fontSize);
    textAlign(LEFT);
    rectMode(CORNER);
    text(statement, x, y, bW, boxHeight);
    //text(statement, x, y);
  }
  
  String whatPossible(int ci, int cj, int[] possible, String place){
    String statement = "";
    if(possible[0] > 0){
      if(place != null){
        statement += "In the "+place+" this";
      }
      else{ statement += "This"; }
      
      statement += " cell ("+int(ci+1)+","+int(cj+1)+") ";
      
      if(possible[0] > 1){
      statement += "has "+possible[0]+" possibilities";
      }
      else{
        statement += "has one possibility";
      }
      
      for(int k=1; k<possible.length; k++){
       if(k==1){
         statement += ": ";
       }
       if(k>1){
         statement += ",";
       }
         statement += possible[k];
      }
      
    }
    else{
      statement += "This cell ("+int(ci+1)+","+int(cj+1)+") is set as "+board.cell[ci][cj][0];
    }
    statement += "\n\n";
    //print(statement);
    return statement;
  }
  
  LineObj whatChain(int ci, int cj, LineObj[] myLines){
    LineObj theLine = null;
    //println("Found "+myLines.length+" lines in "+place);
    for(int l=0; l<myLines.length; l++){ //for line in row
      for(int c=0; c<myLines[l].length; c++){ //for cells in line
        if(myLines[l].cells[c].i == ci && myLines[l].cells[c].j == cj){
          if(theLine == null || (myLines[l].length < theLine.length)){
            theLine = myLines[l]; 
          }
        }
      }
    }
    return theLine;
  }
    
  String chainStatement(LineObj theLine, String place){
    String statement = "";
    if(theLine != null){
      statement += "In this "+place+" this cell ";
      if(theLine.length > 1){
        statement += "forms a "+theLine.length+" cell chain with "+theLine.length+" possibilities: ";
      }
      else{
        statement += "has one possibility: ";
      }
      int[] chain = theLine.possibilities();
        for(int i=1; i < chain.length; i++){
          statement += chain[i];
          
          statement += i!=int(chain.length-1) ? "," : "";
        }
      statement += "\n\n";
    }
    //print(statement);
    return statement;
  }
  
}
