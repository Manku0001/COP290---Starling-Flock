/*! \file */

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class starlings extends PApplet {

Flock flock;
PFont f;
int count,pp=0,ll=0;
float forc,ener,ang_mom;
PVector aa;
int BOIDS = 200;
/*! The setup() function is run once, when the program starts.
 *  It adds an initial set of boids into the system. */
public void setup() {
  
  
  flock = new Flock();
  for (int i = 0; i < BOIDS; i++) {
    flock.addBoid(new Boid(random(width),random(height)));
  }
  f = createFont("Abyssinica SIL",16,true);
  textFont(f,18);
  count = BOIDS;
  forc = 0;

}


/*! It is called directly after setup() and it continuously 
 executes the lines of code contained inside its block until the program is stopped.
  * It is called automatically.
   and
  all programs update the screen at the end of draw(). */
public void draw() {
  background(255,255,255);
  fill(0, 200);
  text("Count = ",42,60);
  text(count,110,60);
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
}

/*! This function adds a new boid into the System */
public void mousePressed() {
  println("mouseX: "+mouseX);
  println("mouseY: "+mouseY);
  for (int i = 0; i < 10; i++) {
    flock.addBoid(new Boid(mouseX,mouseY));
  }
}
/*! \class Input
    \brief Input class.

 This class contains a list of all the boids/birds in the system. */
class Flock {
  ArrayList<Boid> boids; /*! List containing all the boids all the boids. */

  Flock() {
    boids = new ArrayList<Boid>(); /*! This constructor intitializes the list of boids.*/
  }

  public void run() {
    for (Boid b : boids) {
      b.run(boids);  // Passing the entire list of boids to each boid individually
    }
  }

  public void addBoid(Boid b) {
    boids.add(b);
    count += 1; 
  }

}




// The Boid class

class Boid {

  PVector position;
  PVector velocity;
  PVector acceleration;
  float r;
  float maxforce;    
  float maxspeed;    

    Boid(float x, float y) {
    acceleration = new PVector(0, 0); // PVector -> two or three dimensional vector
    float angle  = random(TWO_PI);
    velocity     = new PVector(cos(angle), sin(angle));
    position     = new PVector(x, y);
    r            = 3.0f;
    maxspeed     = 2;
    maxforce     = 0.03f;
  }

  public void run(ArrayList<Boid> boids) {
    flock(boids);
    update();
    borders();
    render();
  }

  /*void applyForce(PVector force) {
    // We could add mass here if we want A = F / M
    acceleration.add(force);
  }*/

  //  We accumulate a new acceleration each time based on three rules
  //  This just updates the acceleration and that is applied in the run method 
  public void flock(ArrayList<Boid> boids) {
    PVector sep = separate(boids);   // Separation
    PVector ali = align(boids);      // Alignment
    PVector coh = cohesion(boids);   // Cohesion
    
    // Arbitrarily weight these forces
    sep.mult(1.5f);
    ali.mult(1.0f);
    coh.mult(1.0f);
    
    // Add the force vectors to acceleration
    acceleration.add(sep);
    acceleration.add(ali);
    acceleration.add(coh);
    if(pp==0){
      forc = force(boids);
      ener = energ(boids);
      ang_mom = lvec(boids);
      pp++;
    }
    else{
      pp += 1;
      pp %= count*7.5f;
    }
    //println(acceleration.mag());
    // if(pp==0) {
    //   /*fill(0, 200);
    //   text(acceleration.mag(),80,84);*/
    //   forc = acceleration;
    //   pp=1;
    // }
    // else{
    //   /*fill(0, 200);
    //   text(aa.mag(),80,84);*/
    //   pp += 1;
    //   pp %= 250;

    // }
    //println(forc.mag());
    /*applyForce(sep);
    applyForce(ali);
    applyForce(coh);*/
  }

  // Method to update position
  public void update() {
    // Update velocity
    velocity.add(acceleration);
    // Limit speed
    velocity.limit(maxspeed);
    position.add(velocity);
    // Reset acceleration to 0 each cycle
    acceleration.mult(0);
  }

  // A method that calculates and applies a steering force towards a target
  // STEER = DESIRED MINUS VELOCITY
  public PVector seek(PVector target) {
    PVector desired = PVector.sub(target, position);  // A vector pointing from the position to the target
    // Scale to maximum speed
    desired.normalize();
    desired.mult(maxspeed);

    // Above two lines of code below could be condensed with new PVector setMag() method
    // Not using this method until Processing.js catches up
    // desired.setMag(maxspeed);

    // Steering = Desired minus Velocity
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(maxforce);  // Limit to maximum steering force
    return steer;
  }

  public void render() {
    // Draw a triangle rotated in the direction of velocity
    float theta = velocity.heading2D() + radians(90);
    
    fill(200, 100);
    stroke(0);
    /*ellipse(position.x, position.y, r, r);*/
    pushMatrix();
    translate(position.x, position.y);
    rotate(theta);
    beginShape(TRIANGLES);
    vertex(-r, r*1.5f);
    vertex(0, -r*1.5f);
    vertex(r, r*1.5f);
    endShape();
    popMatrix();
  }

  // Wraparound
  public void borders() {
    if (position.x < -r) position.x = width+r;
    if (position.y < -r) position.y = height+r;
    if (position.x > width+r) position.x = -r;
    if (position.y > height+r) position.y = -r;
  }

  // Separation
  // Method checks for nearby boids and steers away
  public PVector separate (ArrayList<Boid> boids) {
    float desiredseparation = 25.0f;
    PVector steer = new PVector(0, 0, 0);
    int count = 0;
    // For every boid in the system, check if it's too close
    for (Boid other : boids) {
      float d = PVector.dist(position, other.position);
      // If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
      if ((d > 0) && (d < desiredseparation)) {
        // Calculate vector pointing away from neighbor
        PVector diff = PVector.sub(position, other.position);
        diff.normalize();
        diff.div(d);        // Weight by distance
        steer.add(diff);
        count++;            // Keep track of how many
      }
    }
    // Average -- divide by how many
    if (count > 0) {
      steer.div((float)count);
    }

    // As long as the vector is greater than 0
    if (steer.mag() > 0) {
      // Implement Reynolds: Steering = Desired - Velocity
      steer.normalize();
      steer.mult(maxspeed);
      steer.sub(velocity);
      steer.limit(maxforce);
    }
    return steer;
  }

  // Alignment
  // For every nearby boid in the system, calculate the average velocity
  public PVector align (ArrayList<Boid> boids) {
    float neighbordist = 50;
    PVector sum = new PVector(0, 0);
    int count = 0;
    for (Boid other : boids) {
      float d = PVector.dist(position, other.position);
      if ((d > 0) && (d < neighbordist)) {
        sum.add(other.velocity);
        count++;
      }
    }
    if (count > 0) {
      sum.div((float)count);
      // Implement Reynolds: Steering = Desired - Velocity
      sum.normalize();
      sum.mult(maxspeed);
      PVector steer = PVector.sub(sum, velocity);
      steer.limit(maxforce);
      return steer;
    } 
    else {
      return new PVector(0, 0);
    }
  }

  // Cohesion
  // For the average position (i.e. center) of all nearby boids, calculate steering vector towards that position
  public PVector cohesion (ArrayList<Boid> boids) {
    float neighbordist = 50;
    PVector sum = new PVector(0, 0);   // Start with empty vector to accumulate all positions
    int count = 0;
    for (Boid other : boids) {
      float d = PVector.dist(position, other.position);
      if ((d > 0) && (d < neighbordist)) {
        sum.add(other.position); // Add position
        count++;
      }
    }
    if (count > 0) {
      sum.div(count);
      return seek(sum);  // Steer towards the position
    } 
    else {
      return new PVector(0, 0);
    }
  }

  public float force (ArrayList<Boid> boids){
    int c = 0;
    float f = 0;
    for(Boid other : boids) {
        f += other.acceleration.mag();
        c++;
    }
    return f;
  }

  public float energ (ArrayList<Boid> boids){
    int c = 0;
    float f = 0;
    for(Boid other : boids) {
        f += (other.velocity.mag() * other.velocity.mag());
        c++;
    }
    return f/(2*c);
  }

  public float lvec (ArrayList<Boid> boids){
    int c = 0;
    float f = 0;
    for(Boid other : boids) {
      PVector temp = other.position.copy();
      f += (other.velocity.cross(temp.sub(width/2,height/2))).mag();
      c++;
    }
    return f/c;
  }
  
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "starlings" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
