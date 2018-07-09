Flock flock;
PFont f;
int count,pp=0,ll=0;
float forc,ener,ang_mom;
PVector aa;
int BOIDS = 1500;
ArrayList<Obstacle> obstacles;
float avoidRadius = 60;
int mode;

/*! The setup() function is run once, when the program starts.
 *  It adds an initial set of boids into the system. */
void setup() {
  
  fullScreen();
  flock = new Flock();
  for (int i = 0; i < BOIDS; i++) {
    flock.addBoid(new Boid(random(width),random(height)));
  }
  f = createFont("Abyssinica SIL",16,true);
  textFont(f,18);
  count = BOIDS;
  forc = 0;
  obstacles = new ArrayList<Obstacle>();
  mode = 1;
}


/*! It is called directly after setup() and it continuously 
 executes the lines of code contained inside its block until the program is stopped.
  * It is called automatically.
   and
  all programs update the screen at the end of draw(). */
void draw() {
  background(255,255,255);
  fill(0, 200);
  text("Count = " + count + "\n" + "Frames Per Second = " + round(frameRate) +"\n"  ,42,60);
  // text(,110,60);
  text("Average force = ",1700,60);
  text(forc,1840,60);
  text("Total force = ",1700,80);
  text(forc*count,1840,80);
  text("Average energy = ",42,970);
  text(ener,190,970);
  text("Total energy = ",42,990);
  text(ener*count,190,990);
  text("Average ang_mom = ",1650,970);
  text(ang_mom,1820,970);
  flock.run();

  for (int i = 0; i < obstacles.size(); i++) {
    Obstacle current = obstacles.get(i);
    current.draw();
  }
}

/*! This function adds a new boid into the System */
void mousePressed() {
  // println("mouseX: "+mouseX);
  // println("mouseY: "+mouseY);
  if (mode == 1) {
    for (int i = 0; i < 10; i++) {
        flock.addBoid(new Boid(mouseX,mouseY));
    }
  }
  else {
        obstacles.add(new Obstacle(mouseX, mouseY));
  }
  
}

void keyPressed() {

  if (key == 'b') {
    mode = 1;
  } else if (key == 'o') {
    mode = 2;
    
  }
}


