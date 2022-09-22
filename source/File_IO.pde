public class File_In extends ControlButton
{
  XMLElement file;
  
  File_In(float x, float y, int cW, int cH, String val)
  {
    super(x,y,cW,cH,val);
  }
  
  void mouseIsReleased(){  
    file = null;  
    String path = ( loadFile(new Frame(), "Open a sudoku XML file", "/Users/myName/Desktop/", ".xml") ); //feed this path into the readFile function;
    //parse the path for a file type. Direct file to appropriate input function. Only handles xml currently.
      openLoadFile(path);
      readFile();
      readFlags();
      distance.update();
    super.mouseIsReleased();
  }
  
  void openLoadFile(String path){
   file = new XMLElement(app, path);
  }
  
  void readFile(){
    for(int i = 0; i < file.getChildCount();i++){
      XMLElement cell;
      cell = file.getChild(i);
      
      updateCell( i,cell.getInt("value") );
      // import the other elements?
    }
  }
  
  void updateCell(int cID, int v){
    controller.cells[cID].update(v);
  }
  
  void readFlags(){
    for(int c = 0; c < file.getChildCount();c++){
      XMLElement cell;
      cell = file.getChild(c);
      XMLElement flags;
      flags = cell.getChild(0);
      for(int k=1; k<10; k++){
        int flag = flags.getInt(str(k));
        int i = controller.cells[c].i;
        int j = controller.cells[c].j;
        //int boardFlag = board.binaryCard(board.probCard(board.prob(i,j,k)));
        int boardFlag = board.prob(i,j,k);
        
        println("Flag: "+flag+" Board: "+boardFlag);
        if( flag != boardFlag ){ //we only need to scan for flags that were toggled off.
          println("Toggle Flag");
          //board.toggleProb(i,j,k);
          board.cell[i][j][k] = flag;
        }
      }
      
      //updateCell( int((i*9)+j),cell.getInt("value") );
      // import the other elements?
    }
  }
  
  void clearBoard(){
    for(int i=0; i<81; i++){
      updateCell(i, 0);
    }
  }
  
  String loadFile (Frame f, String title, String defDir, String fileType) {
    FileDialog fd = new FileDialog(f, title, FileDialog.LOAD);
    fd.setFile(fileType);
    fd.setDirectory(defDir);
    fd.setLocation(50, 50);
    fd.show();
    String path = fd.getDirectory()+fd.getFile();
    return path;
  }
  
}

//---------------------------------------------------------------------------------------------------------

public class File_Out extends ControlButton
{
  Boards board;
  XMLElement file;
  PrintWriter xmlfile;
  
  File_Out(float x, float y, int cW, int cH, String val, Boards b)
  {
    super(x,y,cW,cH,val);
    board = b;
  }
  
  void mouseIsReleased(){
    String path = "";
    path = ( saveFile(new Frame(), "Save as", "/Users/myName/Desktop/", ".xml") ); //feed this path into the readFile function;
    if (path == null) {
    // If a file was not selected
    println("No output file was selected...");
  } else {
    // If a file was selected, print path to folder
    println(path);
    output(path);
  }
    super.mouseIsReleased();
  }
  
  void output(String path){
    String[] words = splitTokens(path, "/.");
    String name = words[words.length-2];
    file = new XMLElement();
    file.setName("Sudoku_Board");
    xmlfile = createWriter(path);
    
    //try loading the saved file
    try
    {
      XMLWriter schreibXML = new XMLWriter(xmlfile) ;  
  
      for(int i=0; i<9; i++){
        for(int j=0; j<9; j++){
          OutputCell(i,j);
        }
      }
  
      schreibXML.write(file);

      xmlfile.flush();
      xmlfile.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    } 
  }

  
  void OutputCell(int i, int j){
      //add new cell
      XMLElement cell = new XMLElement();
      cell.setName("cell");
      //add its i
      cell.setInt("i",i);
      //add its j
      cell.setInt("j",j);
      //add its value
      cell.setInt("value",board.cell[i][j][0]);
      //add its flags
      XMLElement flag = new XMLElement();
      flag.setName("flag");
      for(int k=1; k<10; k++){
        //flag.setInt(str(k),board.binaryCard(board.probCard(board.prob(i,j,k))) );
        flag.setInt(str(k),board.prob(i,j,k) );
      }
      cell.addChild(flag);
      file.insertChild(cell,cellID(i,j));
  }
  
  int cellID(int i, int j){
    return (i*9)+j;
  }
  
  String saveFile (Frame f, String title, String defDir, String fileType) {
    FileDialog fd = new FileDialog(f, title,    FileDialog.SAVE);
    fd.setFile(fileType);
    fd.setDirectory(defDir);
    fd.setLocation(50, 50);
    fd.show();
    String path = fd.getDirectory()+fd.getFile();
    return path;
  }

}
