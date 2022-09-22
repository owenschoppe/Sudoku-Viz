//-----------------------------------------------------------------------------------------------------------------------------------------------------------------

public class Label{
  
  String value;
  float x, y, parentX, parentY;
  int fontSize;
  color labelFill;
  
  Label(String v, float parentX, float parentY, int s){
    this.value = v;
    colorMode(RGB,255);
    labelFill = color(255);
    fontSize = s;
    update(parentX,parentY);    
    registerDraw(this);
  }
  
  void update(){
    textFont(fontA, fontSize);
    x = parentX + textWidth(value)/2;
    y = parentY - (fontSize/2) - 5;
  }
  
  void update(float x, float y){
    parentX = x;
    parentY = y;
    update();
  }
  
  void draw(){
    fill(labelFill);
    noStroke();
    textFont(fontA, fontSize);
    textAlign(CENTER,CENTER);
    text(value, x, y);
  }
  
}
