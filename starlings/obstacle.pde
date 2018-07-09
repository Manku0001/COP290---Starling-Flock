class Obstacle {
   PVector position;
   
   Obstacle (float x, float y) {
     position = new PVector(x, y);
   }
      
   void draw () {
     fill(255, 90, 50);
     ellipse(position.x, position.y, 15, 15);
   }
}
