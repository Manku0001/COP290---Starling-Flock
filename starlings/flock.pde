/*! \class Input
    \brief Input class.

 This class contains a list of all the boids/birds in the system. */
class Flock {
  ArrayList<Boid> boids; /*! List containing all the boids all the boids. */
 
  Flock() {
    boids = new ArrayList<Boid>(); /*! This constructor intitializes the list of boids.*/
  }

  class RunParallel extends Thread {
    int s;
    int e;
    ArrayList<Boid> boid;

    RunParallel(int i1, int i2, ArrayList<Boid> b) {
        s = i1;
        e = i2;
        boid = b;
    }

    public void run()
    {
        try {
            for (int i = s; i < e; ++i) {
                boids.get(i).run(boid);
            }
        } catch (Exception e) {
            e.printStackTrace();    
        }
    }

  }

  void run() {

    RunParallel p1 = new RunParallel(0, int(count/4), boids);
    RunParallel p2 = new RunParallel(int(count/4), int(2*count/4), boids);
    RunParallel p3 = new RunParallel(int(2*count/4), int(3*count/4), boids);
    RunParallel p4 = new RunParallel(int(3*count/4), count, boids);


    p1.start();
    p2.start();
    p3.start();
    p4.start();

    try {
        p1.join();
        p2.join();
        p3.join();
        p4.join();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    for (Boid b : boids) {
        b.render();
              // b.run(boids);  // Passing the entire list of boids to each boid individually

    }
  }

  void addBoid(Boid b) {
    boids.add(b);
    count += 1; 
  }

}