package Robot;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.lang.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.io.*;
import java.util.*;
import java.net.*;

import static java.lang.Math.abs;


// This class draws the probability map and value iteration map that you create to the window
// You need only call updateProbs() and updateValues() from your theRobot class to update these maps
class mySmartMap extends JComponent implements KeyListener {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    int currentKey;

    int winWidth, winHeight;
    double sqrWdth, sqrHght;
    Color gris = new Color(170,170,170);
    Color myWhite = new Color(220, 220, 220);
    World mundo;

    int gameStatus;

    double[][] probs;
    double[][] vals;

    public mySmartMap(int w, int h, World wld) {
        mundo = wld;
        probs = new double[mundo.width][mundo.height];
        vals = new double[mundo.width][mundo.height];
        winWidth = w;
        winHeight = h;

        sqrWdth = (double)w / mundo.width;
        sqrHght = (double)h / mundo.height;
        currentKey = -1;

        addKeyListener(this);

        gameStatus = 0;
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public void setWin() {
        gameStatus = 1;
        repaint();
    }

    public void setLoss() {
        gameStatus = 2;
        repaint();
    }

    public void updateProbs(double[][] _probs) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = _probs[x][y];
            }
        }

        repaint();
    }

    public void updateValues(double[][] _vals) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                vals[x][y] = _vals[x][y];
            }
        }

        repaint();
    }

    public void paint(Graphics g) {
        paintProbs(g);
        //paintValues(g);
    }

    public void paintProbs(Graphics g) {
        double maxProbs = 0.0;
        int mx = 0, my = 0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProbs) {
                    maxProbs = probs[x][y];
                    mx = x;
                    my = y;
                }
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);

                    int col = (int)(255 * Math.sqrt(probs[x][y]));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }

            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(0, (int)(y * sqrHght), winWidth, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth), 0, (int)(x * sqrWdth), (int)winHeight);
        }

        //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);

        g.setColor(Color.green);
        g.drawOval((int)(mx * sqrWdth)+1, (int)(my * sqrHght)+1, (int)(sqrWdth-1.4), (int)(sqrHght-1.4));

        if (gameStatus == 1) {
            g.setColor(Color.green);
            g.drawString("You Won!", 8, 25);
        }
        else if (gameStatus == 2) {
            g.setColor(Color.red);
            g.drawString("You're a Loser!", 8, 25);
        }
    }

    public void paintValues(Graphics g) {
        double maxVal = -99999, minVal = 99999;
        int mx = 0, my = 0;

        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 0)
                    continue;

                if (vals[x][y] > maxVal)
                    maxVal = vals[x][y];
                if (vals[x][y] < minVal)
                    minVal = vals[x][y];
            }
        }
        if (minVal == maxVal) {
            maxVal = minVal+1;
        }

        int offset = winWidth+20;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);

                    //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
                    int col = (int)(255 * (vals[x][y]-minVal)/(maxVal-minVal));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }

            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(offset, (int)(y * sqrHght), (int)winWidth+offset, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth)+offset, 0, (int)(x * sqrWdth)+offset, (int)winHeight);
        }
    }


    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        //System.out.println(key);

        switch (key) {
            case 'i':
                currentKey = NORTH;
                break;
            case ',':
                currentKey = SOUTH;
                break;
            case 'j':
                currentKey = WEST;
                break;
            case 'l':
                currentKey = EAST;
                break;
            case 'k':
                currentKey = STAY;
                break;
        }
    }
}


// This is the main class that you will add to in order to complete the lab
public class theRobot extends JFrame {
    // Mapping of actions to integers
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    Color bkgroundColor = new Color(230,230,230);

    static mySmartMap myMaps; // instance of the class that draw everything to the GUI
    String mundoName;

    World mundo; // mundo contains all the information about the world.  See World.java
    double moveProb, sensorAccuracy;  // stores probabilies that the robot moves in the intended direction
                                      // and the probability that a sonar reading is correct, respectively

    double wrongMoveProb;

    // variables to communicate with the Server via sockets
    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;

    // variables to store information entered through the command-line about the current scenario
    boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
    boolean knownPosition = false;
    int startX = -1, startY = -1;
    int decisionDelay = 250;

    // store your probability map (for position of the robot in this array
    double[][] probs;


    // store your computed value of being in each state (x, y)
    double[][] Vs;

    public theRobot(String _manual, int _decisionDelay) {
        // initialize variables as specified from the command-line
        if (_manual.equals("automatic"))
            isManual = false;
        else
            isManual = true;
        decisionDelay = _decisionDelay;

        // get a connection to the server and get initial information about the world
        initClient();

        // Read in the world
        mundo = new World(mundoName);

        // set up the GUI that displays the information you compute
        int width = 500;
        int height = 500;
        int bar = 20;
        setSize(width,height+bar);
        getContentPane().setBackground(bkgroundColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height+bar);
        myMaps = new mySmartMap(width, height, mundo);
        getContentPane().add(myMaps);

        setVisible(true);
        setTitle("Probability and Value Maps");

        doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
    }

    // this function establishes a connection with the server and learns
    //   1 -- which world it is in
    //   2 -- it's transition model (specified by moveProb)
    //   3 -- it's sensor model (specified by sensorAccuracy)
    //   4 -- whether it's initial position is known.  if known, its position is stored in (startX, startY)
    public void initClient() {
        int portNumber = 3333;
        String host = "localhost";

        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));

            mundoName = sin.readLine();
            moveProb = Double.parseDouble(sin.readLine());
            wrongMoveProb = 1 - moveProb;
            sensorAccuracy = Double.parseDouble(sin.readLine());
            System.out.println("Need to open the mundo: " + mundoName);
            System.out.println("moveProb: " + moveProb);
            System.out.println("sensorAccuracy: " + sensorAccuracy);

            // find out of the robots position is know
            String _known = sin.readLine();
            if (_known.equals("known")) {
                knownPosition = true;
                startX = Integer.parseInt(sin.readLine());
                startY = Integer.parseInt(sin.readLine());
                System.out.println("Robot's initial position is known: " + startX + ", " + startY);
            }
            else {
                System.out.println("Robot's initial position is unknown");
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    // function that gets human-specified actions
    // 'i' specifies the movement up
    // ',' specifies the movement down
    // 'l' specifies the movement right
    // 'j' specifies the movement left
    // 'k' specifies the movement stay
    int getHumanAction() {
        System.out.println("Reading the action selected by the user");
        while (myMaps.currentKey < 0) {
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        int a = myMaps.currentKey;
        myMaps.currentKey = -1;

        System.out.println("Action: " + a);

        return a;
    }

    // initializes the probabilities of where the AI is
    void initializeProbabilities() {
        probs = new double[mundo.width][mundo.height];
        // if the robot's initial position is known, reflect that in the probability map
        if (knownPosition) {
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if ((x == startX) && (y == startY))
                        probs[x][y] = 1.0;
                    else
                        probs[x][y] = 0.0;
                }
            }
        }
        else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
            int count = 0;

            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        count++;
                }
            }

            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        probs[x][y] = 1.0 / count;
                    else
                        probs[x][y] = 0;
                }
            }
        }

        myMaps.updateProbs(probs);
    }

    // isPit(gridX, gridY), isWall(gridX, gridY), isGoal(gridX, gridY), transitionFunction(), sensorFunction(sensorInput, gridX, gridY)

    boolean isWall(int gridX, int gridY) {
        // 0: empty square; 1: wall; 2: stairwell; 3: goal
        return mundo.grid[gridX][gridY] == 1 ? true : false;
    }

    boolean isPitOrGoal(int gridX, int gridY) {
        // 0: empty square; 1: wall; 2: stairwell; 3: goal
        return mundo.grid[gridX][gridY] == 2 || mundo.grid[gridX][gridY] == 3 ? true : false;
    }

    boolean isValidState(int gridX, int gridY) {
        // 0: empty square; 1: wall; 2: stairwell; 3: goal
        return mundo.grid[gridX][gridY] == 1 ||mundo.grid[gridX][gridY] == 2 || mundo.grid[gridX][gridY] == 3 ? true : false;
    }

    double[][] normalizeProbs(double[][] newProbs, double total) {
        for (int i = 0; i < newProbs.length; i++) {
            for (int j = 0; j < newProbs[0].length; j++) {
                newProbs[i][j] = newProbs[i][j] / total;
            }
        }

        return newProbs;
    }

    double sensorFunction(String sensor, int gridX, int gridY){
        // north
        int sensorReading = Character.getNumericValue(sensor.charAt(0));
        int mundoVal = mundo.grid[gridX][gridY-1];
        double northProb = sensorMatches(sensorReading, mundoVal) ? sensorAccuracy : 1-sensorAccuracy;

        // south
        sensorReading = Character.getNumericValue(sensor.charAt(1));
        mundoVal = mundo.grid[gridX][gridY+1];
        double southProb = sensorMatches(sensorReading, mundoVal) ? sensorAccuracy : 1-sensorAccuracy;

        // east
        sensorReading = Character.getNumericValue(sensor.charAt(2));
        mundoVal = mundo.grid[gridX+1][gridY];
        double eastProb = sensorMatches(sensorReading, mundoVal) ? sensorAccuracy : 1-sensorAccuracy;

        // west
        sensorReading = Character.getNumericValue(sensor.charAt(3));
        mundoVal = mundo.grid[gridX-1][gridY];
        double westProb = sensorMatches(sensorReading, mundoVal) ? sensorAccuracy : 1-sensorAccuracy;

        return (northProb * southProb * eastProb * westProb);
    }

    boolean sensorMatches(int sensorReading, int mundoVal){
        return (sensorReading == 0 && mundoVal != 1) || (sensorReading == 1 && mundoVal == 1);
    }

    double max(double num1,double num2,double num3,double num4) {
        if (num1 >= num2 && num1 >= num3 && num1 >= num4) {
            return num1;
        }
        if (num2 >= num1 && num2 >= num3 && num2 >= num4) {
            return num2;
        }
        if (num3 >= num1 && num3 >= num2 && num3 >= num4) {
            return num3;
        }
        if (num4 >= num2 && num4 >= num3 && num4 >= num1) {
            return num4;
        }
        else return num1;
    }

    double transitionFunction(int action, int gridX, int gridY) {
        // add all the possible probabilites from directions that we could have come from
        double north = 0.0;
        double south = 0.0;
        double east = 0.0;
        double west = 0.0;
        double stay = 0.0;

        // check if we came from north
        if (gridY - 1 >= 0 && !isWall(gridX, gridY - 1) && !isPitOrGoal(gridX, gridY - 1)) {
            if (action == 1) {
                // this would be a correct move to get here
                north = probs[gridX][gridY - 1] * moveProb;
            }
            else {
                north = probs[gridX][gridY - 1] * wrongMoveProb;
            }
        }

        // check if we came from south
        if (gridY + 1 < mundo.height && !isWall(gridX, gridY + 1) && !isPitOrGoal(gridX, gridY + 1)) {
            if (action == 0) {
                // this would be a correct move to get here
                south = probs[gridX][gridY + 1] * moveProb;
            }
            else {
                south = probs[gridX][gridY + 1] * wrongMoveProb;
            }
        }

        // check if we came from the west
        if (gridX - 1 >= 0 && !isWall(gridX - 1, gridY) && !isPitOrGoal(gridX - 1, gridY)) {
            if (action == 2) {
                // this would be a correct move to get here
                east = probs[gridX - 1][gridY] * moveProb;
            }
            else {
                east = probs[gridX - 1][gridY] * wrongMoveProb;
            }
        }

        // check if we came from the east
        if (gridX + 1 < mundo.width && !isWall(gridX + 1, gridY) && !isPitOrGoal(gridX + 1, gridY)) {
            if (action == 3) {
                // this would be a correct move to get here
                west = probs[gridX + 1][gridY] * moveProb;
            }
            else {
                west = probs[gridX + 1][gridY] * wrongMoveProb;
            }
        }

        // check all cases for if we stayed
        // check if the north is wall and we moved against it
        if (isWall(gridX, gridY - 1)) {
            if (action == 0) {
                stay += probs[gridX][gridY] * moveProb;
            }
            else {
                stay += probs[gridX][gridY] * wrongMoveProb;
            }
        }

        // check if the south is wall and we moved against it
        if (isWall(gridX, gridY + 1)) {
            if (action == 1) {
                stay += probs[gridX][gridY] * moveProb;
            }
            else {
                stay += probs[gridX][gridY] * wrongMoveProb;
            }
        }

        // check if east is a wall and we moved against it
        if (isWall(gridX + 1, gridY)) {
            if (action == 2) {
                stay += probs[gridX][gridY] * moveProb;
            }
            else {
                stay += probs[gridX][gridY] * wrongMoveProb;
            }
        }
        // check if west is a wall and we moved against it
        if (isWall(gridX - 1, gridY)) {
            if (action == 3) {
                stay += probs[gridX][gridY] * moveProb;
            }
            else {
                stay += probs[gridX][gridY] * wrongMoveProb;
            }
        }

        // check probability of us just staying
        if (action == 4) {
            stay += probs[gridX][gridY] * moveProb;
        }
        else {
            stay += probs[gridX][gridY] * wrongMoveProb;
        }

        return north + south + east + west + stay;
    }

    // TODO: update the probabilities of where the AI thinks it is based on the action selected and the new sonar readings
    //       To do this, you should update the 2D-array "probs"
    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    void updateProbabilities(int action, String sonars) {
        // your code
        double[][] newProbs = new double[mundo.width][mundo.height];
        double totalFromRound = 0.0;
        for (int i = 0; i < mundo.height; i++) {
            for (int j = 0; j < mundo.width; j++) {
                // this is for each space in the world
                if (isPitOrGoal(i,j) || isWall(i,j)) {
                    // the game would be over if we are at one of these spots, so the probability of being there is 0
                    newProbs[i][j] = 0;
                    continue;
                }

                // we now know the spot is not a special space
                // call the transitionFunction first
                double transitionProb = transitionFunction(action, i, j);
                // now call the sensor function
                double sensorProb = sensorFunction(sonars, i, j);
                // now multiply sensor and transition function probabilities
                double totalProbability = sensorProb * transitionProb;
                // add the current new probability to the newProbs array
                newProbs[i][j] = totalProbability;
                // add to the sum of the total
                totalFromRound += totalProbability;
            }
        }

        // now normalize the probabilites based on the total
        newProbs = normalizeProbs(newProbs, totalFromRound);

        probs = newProbs;
        myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
        //  new probabilities will show up in the probability map on the GUI
    }


//    // initializes the probabilities of where the AI is
//    void initializeProbabilities() {
//        probs = new double[mundo.width][mundo.height];
//        Vs = new double[mundo.width][mundo.height];
//        // if the robot's initial position is known, reflect that in the probability map
//        if (knownPosition) {
//            for (int y = 0; y < mundo.height; y++) {
//                for (int x = 0; x < mundo.width; x++) {
//                    if ((x == startX) && (y == startY))
//                        probs[x][y] = 1.0;
//                    else
//                        probs[x][y] = 0.0;
//                }
//            }
//        }
//        else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
//            int count = 0;
//
//            for (int y = 0; y < mundo.height; y++) {
//                for (int x = 0; x < mundo.width; x++) {
//                    if (mundo.grid[x][y] == 0)
//                        count++;
//                }
//            }
//
//            for (int y = 0; y < mundo.height; y++) {
//                for (int x = 0; x < mundo.width; x++) {
//                    if (mundo.grid[x][y] == 0)
//                        probs[x][y] = 1.0 / count;
//                    else
//                        probs[x][y] = 0;
//                }
//            }
//        }
//
//        myMaps.updateProbs(probs);
//    }
//
//      void updateProbabilities(int action, String sonars) {
//          // your code
//          double[][] norm = new double[mundo.width][mundo.height];
//
//          ArrayList<Double> temp = new ArrayList<>();
//
//          for (int y = 0; y < mundo.height; y++) {
//              for (int x = 0; x < mundo.width; x++) {
//                  if (mundo.grid[x][y] != 1 && mundo.grid[x][y] != 2) {
//                      int[] currState = {x, y};
//                      double transition = transitionModel(currState, action);
//                      double sensor = sensorModel(currState, sonars);
//                      norm[x][y] = transition * sensor;
//                      temp.add(transition * sensor);
//                  }
//              }
//          }
//
//          double sum = 0;
//          for (Double d : temp) {
//              sum += d;
//          }
//
//          for (int y = 0; y < mundo.height; y++) {
//              for (int x = 0; x < mundo.width; x++) {
//                  if (sum != 0) {
//                      probs[x][y] = norm[x][y] / sum;
//                  } else {
//                      probs[x][y] = norm[x][y];
//                  }
//              }
//          }
//
//          myMaps.updateProbs(probs);
//      }
//
//    double transitionModel(int[] currState, int action) {
//        double prob = 0;
//        double wrongProb = (1 - moveProb) / 4;
//        int[] prevState = {0, 0};
//        double option = 0;
//        for (int i = 0; i < 5; i++) {
//            //check all states around current, including self
//            int prev = 0;
//            switch (i) {
//                case 0:
//                    prevState[0] = currState[0]-1; //left
//                    prevState[1] = currState[1];
//                    option = mundo.grid[prevState[0]+1][prevState[1]] == 0 || mundo.grid[prevState[0]+1][prevState[1]] == 3 ? 1 : 0;
//                    prev = 2;
//                    break;
//                case 1:
//                    prevState[0] = currState[0]; //up
//                    prevState[1] = currState[1]-1;
//                    option = mundo.grid[prevState[0]][prevState[1]+1] == 0  ||  mundo.grid[prevState[0]][prevState[1]+1] == 3 ? 1 : 0;
//                    prev = 1;
//                    break;
//                case 2:
//                    prevState[0] = currState[0]+1; //right
//                    prevState[1] = currState[1];
//                    option = mundo.grid[prevState[0]-1][prevState[1]] == 0 || mundo.grid[prevState[0]-1][prevState[1]] == 3 ? 1 : 0;
//                    prev = 3;
//                    break;
//                case 3:
//                    prevState[0] = currState[0]; //down
//                    prevState[1] = currState[1]+1;
//                    option = mundo.grid[prevState[0]][prevState[1]-1] == 0  ||  mundo.grid[prevState[0]][prevState[1]-1] == 3 ? 1 : 0;
//                    prev = 0;
//                    break;
//                case 4:
//                    prevState[0] = currState[0]; //stay
//                    prevState[1] = currState[1];
//                    double left = mundo.grid[prevState[0]-1][prevState[1]] == 1 ? 1 : 0;
//                    double up = mundo.grid[prevState[0]][prevState[1]-1] == 1 ? 1 : 0;
//                    double right = mundo.grid[prevState[0]+1][prevState[1]] == 1 ? 1 : 0;
//                    double down = mundo.grid[prevState[0]][prevState[1]+1] == 1 ? 1 : 0;
//                    double stay = 1;
//                    switch (action) {
//                        case 0:
//                            prob += moveProb * up  + ((wrongProb * (left + right + down + stay))) * probs[prevState[0]][prevState[1]];
//                            break;
//                        case 1:
//                            prob += moveProb * down + ((wrongProb * (left + right + up + stay))) * probs[prevState[0]][prevState[1]];
//                            break;
//                        case 2:
//                            prob += moveProb * right + ((wrongProb * (left + up + down + stay))) * probs[prevState[0]][prevState[1]];
//                            break;
//                        case 3:
//                            prob += moveProb * left + ((wrongProb * (up + right + down + stay))) * probs[prevState[0]][prevState[1]];
//                            break;
//                        case 4:
//                            prob += moveProb * stay + ((wrongProb * (left + right + down + up))) * probs[prevState[0]][prevState[1]];
//                            break;
//                    }
//                    prev = 4;
//                    break;
//            }
//
//
//            if (prev == 4) { //stay special case
//                continue;
//            }
//            else if (prev == action) { //move correctly
//                prob += (moveProb * probs[prevState[0]][prevState[1]]);
//                continue;
//            }
//            else { //move incorrectly
//                prob += option * wrongProb * probs[prevState[0]][prevState[1]];
//            }
//        }
//
//        return prob;
//    }
//
//    double sensorModel(int[] currState, String sonars) {
//        double result = 1;
//        result *= mundo.grid[currState[0]][currState[1]-1] == Character.getNumericValue(sonars.charAt(0)) ? sensorAccuracy : 1 - sensorAccuracy;
//        result *= mundo.grid[currState[0]][currState[1]+1] == Character.getNumericValue(sonars.charAt(1)) ? sensorAccuracy : 1 - sensorAccuracy;
//        result *= mundo.grid[currState[0]+1][currState[1]] == Character.getNumericValue(sonars.charAt(2)) ? sensorAccuracy : 1 - sensorAccuracy;
//        result *= mundo.grid[currState[0]-1][currState[1]] == Character.getNumericValue(sonars.charAt(3)) ? sensorAccuracy : 1 - sensorAccuracy;
//        return result;
//    }



    // This is the function you'd need to write to make the robot move using your AI;
    // You do NOT need to write this function for this lab; it can remain as is
    int automaticAction() {
        double upSum =0;
        double downSum =0;
        double rightSum =0;
        double leftSum =0;
        double staySum =0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 1 && mundo.grid[x][y] != 2 && mundo.grid[x][y] != 3) {
                    for (int i = 0; i < 5; i++) {
                        switch (i) {
                            case 0:
                                upSum += probs[x][y] * Vs[x][y-1];
                                break;
                            case 1:
                                downSum += probs[x][y] * Vs[x][y+1];
                                break;
                            case 2:
                                rightSum += probs[x][y] * Vs[x+1][y];
                                break;
                            case 3:
                                leftSum += probs[x][y] * Vs[x-1][y];
                                break;
                            case 4:
                                staySum += probs[x][y] * Vs[x][y];
                                break;
                        }
                    }
                }
            }
        }
        ArrayList<Double> sums = new ArrayList<>();
        sums.add(upSum);
        sums.add(downSum);
        sums.add(rightSum);
        sums.add(leftSum);
        sums.add(staySum - 1);
        double max = Collections.max(sums);
        int act = sums.indexOf(max);
        return act;
    }

    void valueIteration() {
        double gamma = 1.0;
        double[][] rewards = new double[mundo.width][mundo.height];
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                switch (mundo.grid[x][y]) {
                    case 0:
                        rewards[x][y] = -1;
                        break;
                    case 1:
                        rewards[x][y] = 0;
                        break;
                    case 2:
                        rewards[x][y] = -200;
                        break;
                    case 3:
                        rewards[x][y] = 500;
                        break;
                }
            }
        }
        // Initialize utility estimates
        double[][] utilities = new double[mundo.width][mundo.height];
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                switch (mundo.grid[x][y]) {
                    case 0:
                        utilities[x][y] = 0;
                        break;
                    case 1:
                        utilities[x][y] = 0;
                        break;
                    case 2:
                        utilities[x][y] = -200;
                        break;
                    case 3:
                        utilities[x][y] = 50;
                        break;
                }
            }
        }
        double[][] temp = new double[mundo.width][mundo.height];
        boolean change;
        do {
            change = false;
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    temp[x][y] = utilities[x][y];
                }
            }
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] != 1 && mundo.grid[x][y] != 2 && mundo.grid[x][y] != 3) {
                        ArrayList<Double> sums = new ArrayList<>();
                        double upSum = transitionFunction(x, y, 0, temp);
                        sums.add(upSum);
                        double downSum = transitionFunction(x, y, 1, temp);
                        sums.add(downSum);
                        double rightSum = transitionFunction(x, y, 2, temp);
                        sums.add(rightSum);
                        double leftSum = transitionFunction(x, y, 3, temp);
                        sums.add(leftSum);
                        double staySum = transitionFunction(x, y, 4, temp);
                        sums.add(staySum);
                        double max = Collections.max(sums);
                        double maxUtil = rewards[x][y] + gamma * max;
                        if (abs(abs(utilities[x][y]) - abs(maxUtil)) > .5)  {
                            change = true;
                            utilities[x][y] = maxUtil;
                        }
                    }
                }
            }
        } while (change);
        myMaps.updateValues(utilities);
        Vs = utilities;

    }

    double transitionFunction(int x, int y, int action, double[][] oldUtil) {
        double wrongProb = ((1 - moveProb) / 4);
        int wall = 0;
        wall += mundo.grid[x + 1][y] == 1 ? 1 : 0;
        wall += mundo.grid[x - 1][y] == 1 ? 1 : 0;
        wall += mundo.grid[x][y + 1] == 1 ? 1 : 0;
        wall += mundo.grid[x][y - 1] == 1 ? 1 : 0;
        switch (action) {
            case 0:
                double total = 0;
                total += ((wrongProb + (wall * wrongProb)) * oldUtil[x][y]);
                total += (moveProb * oldUtil[x][y-1]);
                total += (wrongProb * oldUtil[x][y+1]);
                total += (wrongProb * oldUtil[x-1][y]);
                total += (wrongProb * oldUtil[x+1][y]);
                return total;
            case 1:
                total = 0;
                total += ((wrongProb + (wall * wrongProb)) * oldUtil[x][y]);
                total += (wrongProb * oldUtil[x][y-1]);
                total += (moveProb * oldUtil[x][y+1]);
                total += (wrongProb * oldUtil[x-1][y]);
                total += (wrongProb * oldUtil[x+1][y]);
                return total;
            case 2:
                total = 0;
                total += ((wrongProb + (wall * wrongProb)) * oldUtil[x][y]);
                total += (wrongProb * oldUtil[x][y-1]);
                total += (wrongProb * oldUtil[x][y+1]);
                total += (wrongProb * oldUtil[x-1][y]);
                total += (moveProb * oldUtil[x+1][y]);
                return total;
            case 3:
                total = 0;
                total += ((wrongProb + (wall * wrongProb)) * oldUtil[x][y]);
                total += (wrongProb * oldUtil[x][y-1]);
                total += (wrongProb * oldUtil[x][y+1]);
                total += (moveProb * oldUtil[x-1][y]);
                total += (wrongProb * oldUtil[x+1][y]);
                return total;
            case 4:
                total = 0;
                total += ((moveProb + (wall * wrongProb)) * oldUtil[x][y]);
                total += (wrongProb * oldUtil[x][y-1]);
                total += (wrongProb * oldUtil[x][y+1]);
                total += (wrongProb * oldUtil[x-1][y]);
                total += (wrongProb * oldUtil[x+1][y]);
                return total;
        }
        return 0.0;
    }

    void doStuff() {
        int action;

        initializeProbabilities();  // Initializes the location (probability) map
        valueIteration();  // TODO: function you will write in Part II of the lab

        while (true) {
            try {
                if (isManual)
                    action = getHumanAction();
                else
                    action = automaticAction();

                sout.println(action); // send the action to the Server

                // get sonar readings after the robot moves
                String sonars = sin.readLine();
                //System.out.println("Sonars: " + sonars);

                updateProbabilities(action, sonars);

                if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
                    if (sonars.charAt(4) == 'w') {
                        System.out.println("I won!");
                        myMaps.setWin();
                        break;
                    }
                    else if (sonars.charAt(4) == 'l') {
                        System.out.println("I lost!");
                        myMaps.setLoss();
                        break;
                    }
                }
                else {
                    // here, you'll want to update the position probabilities
                    // since you know that the result of the move as that the robot
                    // was not at the goal or in a stairwell
                }
                Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
                                              // decisionDelay is specified by the send command-line argument, which is given in milliseconds
            }
            catch (IOException e) {
                System.out.println(e);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // java theRobot [manual/automatic] [delay]
    public static void main(String[] args) {
        theRobot robot = new theRobot(args[0], Integer.parseInt(args[1]));  // starts up the robot
    }
}