//Student ID: 500 339 625
import java.awt.*; 
import java.util.*;
import java.nio.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.imageio.*;

public class BoulderDash extends JPanel
{
    //*************************************************************************************
    //final fields
    //*************************************************************************************
    //the pixel width of the image of the tile
    private final int IMGSQUARESIZE = 50;
    //the actual pixel width that will be displayed in the frame
    private final int SQUARESIZE = 50;
    //the dimension of the level
    private final int WIDTH = 40, HEIGHT = 22;
    //the maximum number of butterflies/fireflies
    private final int MAXFLYCOUNT = 50;
    //the amound of time given per level in seconds
    private final int MAXTIMEPERLEVEL = 240;

    //*************************************************************************************
    //arrays
    //*************************************************************************************
    //stores the butterflies/fireflies
    private ButterFireFly[] butterFireFly;
    //stores the level data
    private BDTile[][] level;    
    //a boolean array that checks 
    //if something just slid out of place
    private boolean[][] justSlid;    
    //a boolean array that checks
    //if the position was a new amoeba
    private boolean[][] newAmoeba;
    //*************************************************************************************
    //timers
    //*************************************************************************************
    
    private javax.swing.Timer smoothScrollTimer;
    //a timer that ticks once per second
    private javax.swing.Timer oncePerSec; 
    //the timer for when the rocks/diamonds fall
    private javax.swing.Timer fallingRockTimer; 
    
    //an int that keeps track of the number 
    //seconds that the player is playing a level
    private int seconds = 0;
    //*************************************************************************************
    //labels
    //*************************************************************************************
    private JLabel secondsLeftLabel, moveCounterLabel, diamondCollectedLabel;
    //*************************************************************************************
    //booleans
    //*************************************************************************************
    private boolean isNewLevel;
    //checks if the constructor has finished
    private boolean isReady = false;
    //checks if the game is over
    private boolean isGameOver;    
    
    //*************************************************************************************
    //others
    //*************************************************************************************
    //the location of the camera (peephole)
    //and the ideal location of the camera
    private double topX, topY, idealX, idealY;
    private BDLevelReader lvlReader;
    //currentLevel is the current the player is on
    private int currentLevel = 1, numberOfLevels;
    //a boolean that checks if the levelReader needs to be used    
    private Graphics2D graphics2;

    //is the location of ther player 
    //x coord then y coord
    private int[] playerLocation = new int[2];
    //the count of how many diamonds have been collected
    private int diamondCollectedCount = 0;
    //the number of steps the player has made
    private int steps = 0;

    private JFrame frame;
    private JPanel panel;
    //the image that is used for paintComponent
    private BufferedImage img;
    //*******************************************************************************************
    //accessor/mutator methods
    //*******************************************************************************************
    public boolean getIsGameOver()
    {
        return isGameOver;
    }
    //sets the ideal location the screen should be
    public void setIdealXY(double x, double y)
    {
        idealX = x;
        idealY = y;
    }
    //returns the ideal location X
    public double getIdealX()
    {
        return idealX;
    }
    //returns ideal location Y
    public double getIdealY()
    {
        return idealY;
    }
    //sets actual location of view
    public void setTopXY(double x, double y)
    {
        topX = x;
        topY = y;
    }
    //returns actual location of view X coord
    public double getTopX()
    {
        return topX;
    }
    //returns actual location of view Y coord
    public double getTopY()
    {
        return topY;
    }
    //method that sets the level tile at the index i, j to tile
    public void setTile(int i, int j, BDTile tile)
    {
        level[i][j] = tile;
    }
    //method that returns the level tile at the index i, j
    public BDTile getTile(int i, int j)
    {
        return level[i][j];
    }
    //gets the length of the level
    public int getTileArrayLength()
    {
        return level.length;
    }
    //gets the width of the level
    public int getTileArrayWidth()
    {
        return level[0].length;
    }
    //sets the graphics 
    public void setGraphics2D(Graphics2D g)
    {
        graphics2 = g;
    }
    //returns the graphivs 2d
    public Graphics2D getGraphics2D()
    {
        return graphics2;
    }
    //sets the JFrame to f
    public void setFrame (JFrame f)
    {
        frame = f;
    }
    //returns the JFrame frame
    public JFrame getFrame()
    {
        return frame;
    }
    //sets the JPanel to p
    public void setPanel(JPanel p)
    {
        panel = p;
    }
    //returns the JPanel panel
    public JPanel getPanel()
    {
        return panel;
    }
    //sets the field playerLocation
    public void setPlayerLocation(int i, int j)
    {
        playerLocation[0] = i;
        playerLocation[1] = j;
    }
    //sets only the x coordinate of the PlayerLocation
    public void setPlayerLocationX(int i)
    {
        playerLocation[0] = i;
    }
    //sets only the y coordinate of the PlayerLocation
    public void setPlayerLocationY(int j)
    {
        playerLocation[1] = j;
    }
    //returns the x coordinate of the PlayerLocation
    public int getPlayerLocationX()
    {
        return playerLocation[0];
    }
    //returns the y coordinate of the PlayerLocation
    public int getPlayerLocationY()
    {
        return playerLocation[1];
    }
    //sets the current level to level
    public void setLevel(int level)
    {
        currentLevel = level;
    }
    //returns the value of currentLevel
    public int getLevel()
    {
        return currentLevel;
    }
    //resets the timer to 0
    public void resetTimer()
    {
        seconds = 0;
    }
    //sets the timer to time s which is in seconds
    public void setTimer(int s)
    {
        seconds = s;
    }
    //returns timer in seconds
    public int getTimer()
    {
        return seconds;
    }
    //*******************************************************************************************
    //helper methods
    //*******************************************************************************************
    public void setJustSlid(int x, int y, boolean bool)
    {
        //check for out of bounds
        if ( x >= WIDTH || x < 0 || y >= WIDTH || y < 0)
        {
            return;
        }
        synchronized( justSlid )
        {
            justSlid[x][y] = bool;
        }
    }
    //checks if the block just slid
    public boolean getJustSlid(int x, int y)
    {
        //check for out of bounds
        if ( x >= WIDTH || x < 0 || y >= WIDTH || y < 0)
        {
            return false;
        }
        if ( justSlid[x][y] )
        {
            synchronized( justSlid )
            {
                justSlid[x][y] = false;
            }
            return true;
        }
        return false;
    }
    //sets the array position x,y to bool
    public void setNewAmoeba(int x, int y, boolean bool)
    {
        //check for out of bounds
        if ( x >= WIDTH || x < 0 || y >= WIDTH || y < 0)
        {
            return;
        }
        synchronized( newAmoeba )
        {
            newAmoeba[x][y] = bool;
        }
    }
    //checks if the amoeba is new
    public boolean isNewAmoeba(int x, int y)
    {
        //check for out of bounds
        if ( x >= WIDTH || x < 0 || y >= WIDTH || y < 0)
        {
            return false;
        }
        if ( newAmoeba[x][y] )
        {
            return true;
        }
        return false;
    }
    //checks if there is a fly nearby the player
    public void isFlyNearby()
    {
        int x = getPlayerLocationX();
        int y = getPlayerLocationY();
        //check for fireflies
        if ( compareTile(x + 1, y, BDTile.FIREFLY) || compareTile(x - 1, y, BDTile.FIREFLY) 
            || compareTile(x, y + 1, BDTile.FIREFLY) || compareTile(x, y - 1, BDTile.FIREFLY) )
        {
            explode(x, y, BDTile.EMPTY);
        }
        else if ( compareTile(x + 1, y, BDTile.BUTTERFLY) || compareTile(x - 1, y, BDTile.BUTTERFLY) 
            || compareTile(x, y + 1, BDTile.BUTTERFLY) || compareTile(x, y - 1, BDTile.BUTTERFLY) )
        {
            explode(x, y, BDTile.DIAMOND);
        }
    }
    //explodes location x, y and fills the tiles with filler
    public void explode (int x, int y, BDTile filler)
    {
        //checks if there are any butter/fire flies in the explosion
        for (int count = 0; count < butterFireFly.length; count++)
        {
            if ( butterFireFly[count] == null)
            {
                continue;
            }
            int bffX = butterFireFly[count].getPosX();
            int bffY = butterFireFly[count].getPosY();
            if ( bffX >= x - 1 && bffX <= x + 1 && bffY >= y - 1 && bffY <= y + 1)
            {
                butterFireFly[count] = null;
            }
        }
        for( int i = x - 1; i <= x + 1; i++ )
        {
            for( int j = y - 1; j <= y + 1; j++ )
            {
                //checks for array out of bounds
                if ( i >= 0 && i < WIDTH && j >= 0 && j < HEIGHT)
                {
                    //checks if player is inside the explosion
                    //if player is, the game ends
                    if (getTile(i, j) == BDTile.PLAYER)
                    {
                        isGameOver = true;
                    }

                    //if it isn't a wall explode it
                    if ( getTile(i, j) != BDTile.WALL)
                    {
                        synchronized(level[i][j])
                        {
                            setTile(i, j, filler);
                        }
                    }
                }
            }
        }
    }
    //*************************************************************************************************
    //helper methods to check if things are supposd to fall/slide
    //*************************************************************************************************
    //checks if the location i, j has an object that can fall
    public boolean isFallable(int i, int j)
    {
        if ( compareTile(i, j, BDTile.ROCK) || compareTile(i, j, BDTile.FALLINGROCK) || compareTile(i, j, BDTile.DIAMOND) || compareTile(i, j, BDTile.FALLINGDIAMOND) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    //checks if the tile is falling
    //may apply to only certain tiles
    public boolean isTileFalling(int x, int y)
    {
        //first check if the rock is on the bottom
        if (y + 1 >= HEIGHT)
        {
            return false;
        }
        //then check if there below is empty
        if ( getTile(x, y + 1) == BDTile.EMPTY )
        {
            return true;
        }
        return false;
    }
    //compares the tile in position x, y to BDTile tile
    //if they are the same, return true, else return false
    //if x and y are not possible values, return false
    public boolean compareTile(int x, int y, BDTile tile)
    {
        //checks if it array out of bounds
        if ( x >= WIDTH || y >= HEIGHT || x < 0 || y < 0)
        {
            return false;
        }
        //checks if the tiles are the same
        else if( getTile(x, y) == tile)
        {
            return true;
        }
        //returns false if not the same
        else 
        {
            return false;
        }
    }
    //checks if the tile will slide and handles it if it does
    //returns true if the tile slid to the right, else returns false
    //this is needed because the checking sweeps from left to right
    //NOTE: because the array is looked from bottom to top, left to right,
    //Before something slips, it must check if something above can slip first
    
    //NOTE: this method returns true when it slides to the right.
    //this again is because the array looks from left to right.
    //it is to prevent things falling immediately after sliding
    public boolean slideTile(int i, int j, BDTile tile, BDTile fallingTile)
     {
        //check if rock is falling
        if ( isTileFalling(i, j) )
        {
            //synchronize lock
            synchronized(level[i][j])
            {
                setTile(i, j, fallingTile);
            }
            return false;
        }
        //check if can slip, do not need to check
        //for array out of bounds, already handled
        else if( compareTile(i, j + 1, BDTile.ROCK) || compareTile(i, j + 1, BDTile.WALL) || compareTile(i, j + 1, BDTile.DIAMOND) )
        {
            //checks if there is something above it that can fall first 
            
            if ( isFallable(i, j -1)  && ( compareTile(i + 1, j - 1, BDTile.EMPTY) || compareTile(i - 1, j - 1, BDTile.EMPTY) )  )
            {
                return false;
            }
            if ( isFallable(i + 1, j - 1) && compareTile (i + 1, j, BDTile.EMPTY) )
            {
                return false;
            }
            //checks if there is something that can slide first
            if ( isFallable(i - 1, j - 1 ) && compareTile (i - 1, j, BDTile.EMPTY) )
            {
                return false;
            }
            //checks if there is something that can slide first
            if ( ( compareTile (i + 1, j - 1, BDTile.FALLINGDIAMOND) || compareTile (i + 1, j - 1, BDTile.FALLINGROCK) ) && compareTile (i + 1, j, BDTile.EMPTY) )
            {
                return false;
            }
            if ( compareTile(i - 1, j, BDTile.EMPTY) && compareTile(i - 1, j + 1, BDTile.EMPTY) )
            {
                setJustSlid(i, j, true);
                synchronized(level[i][j])
                {
                    synchronized(level[i - 1][j])
                    {
                        setTile(i, j, BDTile.EMPTY);
                        setTile(i - 1, j, fallingTile);
                    }
                }
            }
            //check the right side
            else if ( compareTile(i + 1, j, BDTile.EMPTY) && compareTile(i + 1, j + 1, BDTile.EMPTY) )
            {
                setJustSlid(i, j, true);
                synchronized(level[i][j])
                {
                    synchronized(level[i + 1][j])
                    {
                        setTile(i, j, BDTile.EMPTY);
                        setTile(i + 1, j, fallingTile);
                    }
                }
                return true;
            }
        }
        return false;
    }
    //checks if the tile is still falling and will handle if it does/doesn't
    public void fallTile(int i, int j, BDTile tile, BDTile fallingTile)
    {
        //checks if it already slid. If it did, return
        //and wait until next cycle to see if it will fall
        //getJustSlid method already sets the position (i, j+1)
        //to false if getJustSlid returns true
        if ( getJustSlid(i, j + 1) )
        {
            return;
        }
        //check if falling rock is still falling
        if ( isTileFalling(i, j) )
        {
            synchronized(level[i][j])
            {
                synchronized(level[i][j + 1])
                {
                    setTile(i, j, BDTile.EMPTY);
                    setTile(i, j + 1, fallingTile);
                }
            }
        }
        else
        {
            synchronized(level[i][j])
            {
                setTile(i, j, tile);
            }
        }
    }
    //method that restores the level to the original state
    public void restore()
    {
        //resets the labels
        secondsLeftLabel.setText("Time Remaining: " + convertTime(MAXTIMEPERLEVEL));
        moveCounterLabel.setText("Steps Taken: 0");
        diamondCollectedLabel.setText("Diamonds Mined: 0");
        //resets the value of isGameOver
        isGameOver = false;
        //resets the count of diamondsCollectedCount
        diamondCollectedCount = 0;
        //resets the count of how many steps the player took
        steps = 0;
        //empties the butterFireFly array which stores
        //the butterflies/fireflies
        for (int i = 0; i < butterFireFly.length; i++)
        {
            butterFireFly[i] = null;
        }
        //checks if the level changed
        if (isNewLevel)
        {
            try
            {
                lvlReader.setCurrentLevel(currentLevel);
            }
            catch (Exception e)
            {
                System.out.println("Could not set the level.");
            }
            isNewLevel = false;
        }
        //creates a new level array
        level = new BDTile[WIDTH][HEIGHT];
        //resets the timer
        seconds = 0;
        //initializes the level array
        int count = 0;
        for(int i = 0; i < WIDTH; i++)
        {
            for (int j = 0; j < HEIGHT; j++)
            {
                //sets the tile in the array
                setTile( i, j, lvlReader.getTile(i, j) );
                //if we find the player, set the ideal camera location
                if(lvlReader.getTile(i, j) == BDTile.PLAYER)
                {
                    setIdealXY( i - 7, j - 5 );
                }
                //if it is a firefly/butterfly, create a new butterFireFly
                //and put it into the array
                if( getTile(i, j) == BDTile.FIREFLY)
                {
                    butterFireFly[count++] = new ButterFireFly(i, j, BDTile.FIREFLY, this);
                }
                else if( getTile(i, j) == BDTile.BUTTERFLY)
                {
                    butterFireFly[count++] = new ButterFireFly(i, j, BDTile.BUTTERFLY, this);
                }
            }
        }
    }
    //so that we can print the time nicely, convert seconds into time format
    public String convertTime(int seconds)
    {
        int sec, min, hour;
        String secS = "", minS = "", hourS = "";
        String returnString = "";
        
        sec = seconds % 60;
        seconds /= 60;
        min = seconds % 60;
        hour = seconds / 60;

        if (sec < 10)
        {
            secS = "0";
        }
        secS = secS + sec;
        if (min < 10)
        {
            minS = "0";
        }
        minS = minS + min;
        if (hour < 10)
        {
            hourS = "0";
        }
        hourS = hourS + hour;
        return minS + ":" + secS;
    }
    //the method that grows the amoeba
    //it is only called when the amoeba should grow
    public void growAmoeba()
    {
        int growCount = 0;
        int amoebaCount = 0;
        //find the amoeba
        for (int i = 0; i < WIDTH; i++)
        {
            for (int j = 0; j < HEIGHT; j++)
            {
                //check if it is an amoeba
                if ( compareTile(i, j, BDTile.AMOEBA) )
                {
                    //increment amoebaCount
                    amoebaCount++;
                    //check if the amoeba is new
                    if ( isNewAmoeba(i, j) )
                    {
                        continue;
                    }
                    //if it is not new, check if it can grow
                    else
                    {
                        //checks if the amoeba can grow
                        if ( canAmoebaGrow(i, j) )
                        {
                            growCount++;
                            Random ran = new Random();
                            int randomInt, newX, newY;
                            do  
                            {
                                randomInt = ran.nextInt();
                                if (randomInt < 0)
                                {
                                    randomInt = -randomInt;
                                }
                                randomInt = randomInt%4;
                                switch (randomInt)
                                {
                                    case 0:
                                        newX = 0;
                                        newY = 1;
                                        break;
                                    case 1:
                                        newX = 1;
                                        newY = 0;
                                        break;
                                    case 2:
                                        newX = 0;
                                        newY = -1;
                                        break;
                                    case 3:
                                        newX = -1;
                                        newY = 0;
                                        break;
                                    default:
                                        newX = 0;
                                        newY = 1;
                                        break;
                                }
                                newX = newX + i;
                                newY = newY + j;
                                if (compareTile(newX, newY, BDTile.EMPTY) || compareTile(newX, newY, BDTile.DIRT))
                                {
                                    break;
                                }
                                //if the array is out of bounds, try again
                            }
                            //or if it is not dirt or empty, keep trying
                            while( newX < 0 || newX >= WIDTH || newY < 0 || newY >= HEIGHT || 
                                getTile(newX, newY) != BDTile.DIRT || getTile(newX, newY) != BDTile.EMPTY);
                            //once an appropriate tile is found
                            //make the amoeba bigger
                            synchronized(level[newX][newY])
                            {
                                setTile(newX, newY, BDTile.AMOEBA);
                            }
                            //set the position to be true so that
                            //the amoeba doesn't grow twice
                            setNewAmoeba(newX, newY, true);
                        }
                    }
                }
            }
        }
        //reset the values of the newAmoeba
        for (int i = 0; i < WIDTH; i++)
        {
            for (int j = 0; j < HEIGHT; j++)
            {
                setNewAmoeba(i, j, false);
            }
        }
        //if amoebaCount > 0 and none of them grow, turn them all into diamonds
        
        if (amoebaCount > 0 && growCount == 0)
        {
            for (int i = 0; i < WIDTH; i++)
            {
                for (int j = 0; j < HEIGHT; j++)
                {
                    if (compareTile(i, j, BDTile.AMOEBA))
                    {
                        synchronized(level[i][j])
                        {
                            setTile(i, j, BDTile.DIAMOND);
                        }
                    }
                }
            }
        }
    }
    public boolean canAmoebaGrow(int i, int j)
    {
        //checks if any neighbouring tiles are dirt or empty
        if ( compareTile(i + 1, j, BDTile.EMPTY) || compareTile(i - 1, j, BDTile.EMPTY) || compareTile(i, j + 1, BDTile.EMPTY) 
            || compareTile(i, j - 1, BDTile.EMPTY) || compareTile(i + 1, j, BDTile.DIRT) || compareTile(i - 1, j, BDTile.DIRT) 
            || compareTile(i, j + 1, BDTile.DIRT) || compareTile(i, j - 1, BDTile.DIRT) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    //*******************************************************************************************
    //listeners
    //*******************************************************************************************
    public class MyKeyListener implements KeyListener
    {
        public void keyPressed(KeyEvent ke)
        {
            char key = ke.getKeyChar();
            int arrow = ke.getKeyCode();
            int dirX = 0, dirY = 0;
            //r simply restores the level
            if (key == 'r')
            {
                restore();
                repaint();
                return;
            }
            //n increments the level and then restores
            if (key == 'n')
            {
                isNewLevel = true;
                if (currentLevel < numberOfLevels)
                {
                    currentLevel++;
                    restore();
                    repaint();
                }
                return;
            }
            //p decrements the level and then restores
            if (key == 'p')
            {
                isNewLevel = true;
                if (currentLevel > 1)
                {
                    currentLevel--;
                    restore();
                    repaint();
                }
                return;
            }
            //checks if the game is over 
            //just return and ignore the rest of the keys
            if (isGameOver)
            {
                return;
            }
            //code reaches here if the game is not over
            //listens the the arrow keys
            if (arrow == KeyEvent.VK_UP)
            {
                dirY = -1;
            }
            else if(arrow == KeyEvent.VK_DOWN)
            {
                dirY = 1;
            }   
            else if(arrow == KeyEvent.VK_LEFT)
            {
                dirX = -1;
            }
            else if(arrow == KeyEvent.VK_RIGHT)   
            {
                dirX = 1;
            }
            else 
            {
                return;
            }
            //move
            //the method move will handle all cases
            move(dirX, dirY);
        }
        public void keyReleased(KeyEvent ke)
        {
        }
        public void keyTyped(KeyEvent ke)
        {
        }
        //try to move the player to the direction dirX,dirY
        //method move handles all cases
        private void move (int dirX, int dirY)
        {
            //checks if we can move in the given direction
            if ( canMove(dirX, dirY) )
            {
                //increment the number of moves
                moveCounterLabel.setText("Steps Taken: " + ++steps);
                //oldX/oldY is the old location of the player
                //newX/newY is the new location of the player
                int oldX = getPlayerLocationX(), oldY = getPlayerLocationY();
                int newX = oldX + dirX, newY = oldY + dirY;
                
                if (compareTile(newX, newY, BDTile.DIAMOND) || compareTile(newX, newY, BDTile.FALLINGDIAMOND))
                {
                    diamondCollectedLabel.setText( "Diamonds Mined: " + (++diamondCollectedCount) );
                }
                
                //actually move the tiles
                synchronized(level[newX][newY])
                {
                    synchronized(level[oldX][oldY])
                    {
                        setTile(newX, newY, BDTile.PLAYER);
                        setTile(oldX, oldY, BDTile.EMPTY);
                    }
                }
                //this updates the location of the player that is stored
                //in the field
                setPlayerLocation(newX, newY);
                //sets the ideal camera location
                setIdealXY( getPlayerLocationX() - 7, getPlayerLocationY() - 5);
                repaint();
            }
        }
        //checks if the player can move to the location dirX, dirY 
        private boolean canMove(int dirX, int dirY)
        {
            //oldX/oldY is the location the player is currently in
            int oldX = getPlayerLocationX(), oldY = getPlayerLocationY();
            //newX/newY is the location that the player wants to move
            int newX = oldX + dirX, newY = oldY + dirY;
            //checks if is blocked by a wall, butterfly or firefly
            if ( compareTile(newX, newY, BDTile.WALL) || compareTile(newX, newY, BDTile.BUTTERFLY) || compareTile(newX, newY, BDTile.FIREFLY))
            {
                return false;
            }
            //checks if it is being blocked by a fallingrock
            else if ( compareTile(newX, newY, BDTile.FALLINGROCK) || compareTile(newX, newY, BDTile.AMOEBA) )
            {
                return false;
            }
            else if (getTile(newX, newY) == BDTile.ROCK)
            {
                //first check if array out of bounds
                if ( newX + dirX >=  getTileArrayLength() || newY + dirY >=  getTileArrayWidth() )
                {
                    return false;
                }
                //check if the rock can be pushed
                else if (getTile(newX + dirX, newY + dirY) == BDTile.EMPTY) 
                {
                    //if it can, move the rock
                    synchronized(level[newX + dirX][ newY + dirY])
                    {
                        setTile(newX + dirX, newY + dirY, BDTile.ROCK);
                    }
                    return true;
                }
                //if the code reaches here, it cannot be pushed
                return false;
            }
            return true;
        }
    }
    //*******************************************************************************************
    //constructor
    //*******************************************************************************************
    BoulderDash(JFrame f)
    {
        img = null;
        try 
        {
            img = ImageIO.read(new File("bd.png"));
        } 
        catch (IOException e) 
        {
        }
        
        panel = this;
        frame = f;
        
        //set it focusable so that keyboardlistener works
        panel.setFocusable(true);
        panel.requestFocus();
        panel.setPreferredSize( new Dimension(14*SQUARESIZE,10*SQUARESIZE) );
        panel.addKeyListener(new MyKeyListener());
        //initialize all the labels;
        diamondCollectedLabel = new JLabel("0");
        secondsLeftLabel = new JLabel("00:04:00");
        moveCounterLabel = new JLabel("0"); 
        //add the labels
        panel.add(diamondCollectedLabel);
        panel.add(secondsLeftLabel);
        panel.add(moveCounterLabel);

        //initialize all the arrays that are going to be used
        //an array that stores whether a rock has just slid or not
        justSlid = new boolean[WIDTH][HEIGHT];
        //an array that stores whether an amoeba has just grew or not
        newAmoeba = new boolean[WIDTH][HEIGHT];
        lvlReader = new BDLevelReader();
        butterFireFly = new ButterFireFly[MAXFLYCOUNT];
        try
        {
            numberOfLevels = lvlReader.readLevels("levels.xml");
        }
        catch (Exception e)
        {
            System.out.println("Could not read levels.");
        }
        //timer that counts how many seconds the level has been played
        oncePerSec = new javax.swing.Timer(1000, new ActionListener()
            {
                public void actionPerformed(ActionEvent ae)
                {
                    if (seconds == MAXTIMEPERLEVEL)
                    {
                        //end the game if there is no time left
                        isGameOver = true;
                        return;
                    }
                    seconds++;
                    secondsLeftLabel.setText( "Time Remaining: " + convertTime(240-seconds) );
                }
                
        });
        oncePerSec.start();
        //timer that checks if tiles can fall
        //if they can, they are handled here
        fallingRockTimer = new javax.swing.Timer(500, new ActionListener()
            {
                public void actionPerformed(ActionEvent ae)
                {
                    if (!isReady)
                    {
                        return;
                    }
                    //move the butter/fire flies
                    for (ButterFireFly BFFly: butterFireFly)
                    {
                        if (BFFly != null && !isGameOver)
                        {
                            BFFly.move();
                        }
                    }
                    //loop through the level to see if things can fall or slide
                    for (int j = HEIGHT - 1; j > 0; j--)
                    {
                        for (int i = 0; i < WIDTH; i++)
                        {
                            BDTile tile = getTile(i, j);
                            //check if it is a rock
                            if ( tile == BDTile.ROCK )
                            {
                                //this method checks if it slides, and handles if it does
                                //this method returns true if the tile slides to the right
                                if (slideTile(i, j, BDTile.ROCK, BDTile.FALLINGROCK) )
                                {
                                    //because the loop sweeps from left to right, skip on tile check
                                    i++;
                                }
                            }
                            //if it is a falling rock
                            else if ( tile == BDTile.FALLINGROCK )
                            {
                                //first checks if it is going to crush something
                                if ( compareTile(i, j + 1, BDTile.PLAYER) )
                                {
                                    explode(i, j + 1, BDTile.EMPTY);
                                    return;
                                }
                                else if ( compareTile(i, j + 1, BDTile.FIREFLY) )
                                {
                                    explode(i, j + 1, BDTile.EMPTY);
                                    return;
                                }
                                else if ( compareTile(i, j + 1, BDTile.BUTTERFLY) )
                                {
                                    explode(i, j + 1, BDTile.DIAMOND);
                                    return;
                                }
                                //this method checks if it continues to fall, and handles
                                //it if it does/doesn't
                                fallTile(i, j, BDTile.ROCK, BDTile.FALLINGROCK);
                            }
                            //check if it is a diamond
                            else if( tile == BDTile.DIAMOND)
                            {
                                //this method checks if it slides, and handles if it does
                                if (slideTile(i, j, BDTile.DIAMOND, BDTile.FALLINGDIAMOND) )
                                {
                                    //because the loop sweeps from left to right, skip on tile check
                                    i++;
                                }
                            }
                            else if ( tile == BDTile.FALLINGDIAMOND)
                            {
                                //this method checks if it continues to fall, and handles
                                //it if it does/doesn't
                                fallTile(i, j, BDTile.DIAMOND, BDTile.FALLINGDIAMOND);
                            }
                        }
                    }
                    repaint();
                }
        });
        fallingRockTimer.start();
        //this timer controls the smooth scrolling
        smoothScrollTimer = new javax.swing.Timer(40, new ActionListener()
            {
                public void actionPerformed(ActionEvent ae)
                {
                    //checks if the constructor is ready
                    if (isReady)
                    {
                        double idealX = getIdealX(), idealY = getIdealY(), topX = getTopX(), topY = getTopY();
                        
                        if ( idealX != topX || idealY != topY )
                        {
                            topX = (idealX-topX)/8 + topX;
                            topY = (idealY-topY)/8 + topY;
                            setTopXY(topX, topY);
                            repaint();
                        }
                        //here is where the amoeba has the chance to grow
                        Random ran = new Random();
                        int randomInt = ran.nextInt();
                        if ( randomInt%300 == 0 )
                        {
                            growAmoeba();
                        }
                    }
                }
        });
        smoothScrollTimer.start();
        isNewLevel = true;
        //set the diamondCollectedCount to 0
        diamondCollectedCount = 0;
        //initializes the level
        restore();
        
        //sets the position for the panel
        
        Insets insets = panel.getInsets();        
        //set the layout to null
        panel.setLayout(null);
        //handles the layout manually
        panel.setBounds(insets.left, insets.top, 14*SQUARESIZE, 10*SQUARESIZE);
        
        frame.setPreferredSize( new Dimension(14*SQUARESIZE,10*SQUARESIZE) );
        frame.pack();
        
        isReady = true;
        repaint();
    }
    //method that is called when the application is close
    public void finalize()
    {
        //kills all the timers
        smoothScrollTimer.stop();
        oncePerSec.stop(); 
        fallingRockTimer.stop(); 
    }
    //paint component method
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        setGraphics2D( (Graphics2D) g );
        Graphics2D g2 = getGraphics2D();
        int exit = 0;
        int translateI = (int) (topX*SQUARESIZE), translateJ = (int) (topY*SQUARESIZE);
        g2.translate(- translateI, -translateJ);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isReady)
        {
            //modify the fill part so that only the things inside the view can be painted
            double topX = getTopX(), topY = getTopY();
            for (int i = 0; i < WIDTH; i++)
            {
                for (int j = 0; j < HEIGHT; j++)
                {
                    //sets the value of srcx1 & srcy1
                    //so that they are at the appropriate part of the image
                    int srcx1 = 0, srcy1 = 0 , srcx2 = 0 , srcy2 = 0 ;
                    BDTile tileSet = getTile(i, j);
                    if (tileSet == BDTile.WALL)
                    {
                        srcx1 = IMGSQUARESIZE * 2;
                        srcy1 = IMGSQUARESIZE;
                    }
                    else if (tileSet == BDTile.ROCK)
                    {
                    }
                    else if (tileSet == BDTile.FALLINGROCK)
                    {
                    }
                    else if (tileSet == BDTile.DIAMOND)
                    {
                        srcx1 = IMGSQUARESIZE;
                    }
                    else if (tileSet == BDTile.FALLINGDIAMOND)
                    {
                        srcx1 = IMGSQUARESIZE;
                    }
                    else if (tileSet == BDTile.AMOEBA)
                    {
                        g2.setPaint(Color.green);
                    }
                    else if (tileSet == BDTile.DIRT)
                    {
                        srcx1 = IMGSQUARESIZE * 3;
                        srcy1 = IMGSQUARESIZE;
                    }
                    else if (tileSet == BDTile.EMPTY)
                    {
                        srcx1 = IMGSQUARESIZE;
                        srcy1 = IMGSQUARESIZE;
                    }
                    else if (tileSet == BDTile.FIREFLY)
                    {
                        srcx1 = IMGSQUARESIZE * 2;
                    }
                    else if (tileSet == BDTile.BUTTERFLY)
                    {
                        srcx1 = IMGSQUARESIZE * 3;
                    }
                    else if (tileSet == BDTile.EXIT)
                    {
                        srcx1 = IMGSQUARESIZE * 4;
                        srcy1 = IMGSQUARESIZE;
                        exit++;
                    }
                    else if (tileSet == BDTile.PLAYER)
                    {
                        srcy1 = IMGSQUARESIZE;
                        //sets the location of the player
                        setPlayerLocation(i, j);
                        isFlyNearby();
                    }
                    //sourceX2 is sourceX1 + the  size of the image
                    srcx2 = srcx1 + IMGSQUARESIZE;
                    srcy2 = srcy1 + IMGSQUARESIZE;
                    double i2 = (i )*SQUARESIZE, j2 = (j )*SQUARESIZE;
                    int i3 = (int) i2, j3 = (int) j2;
                    //img is a field that was initialized in the constructor
                    //IMGSQUARESIZE is the pixel width/length of the image
                    //SQUARESIZE is the pixel width/length of how big we want to draw
                    //the image
                    g2.drawImage( img, i3, j3, i3 + SQUARESIZE, j3 + SQUARESIZE, srcx1, srcy1, srcx2, srcy2, null);
                    if (tileSet == BDTile.AMOEBA)
                    {
                        g2.fill( new Rectangle2D.Double( i*SQUARESIZE, j*SQUARESIZE, SQUARESIZE + 1, SQUARESIZE + 1) );
                    }
                    
                }
            }

            g2.setPaint(new Color(238,238,238));
            
            //this paints the top section the same colour as the background
            //so that the JLabels can be read
            for (int i = 0; i < WIDTH; i++)
            {
                for (int j = 0; j < 1; j++)
                {
                    {
                        g2.fill( new Rectangle2D.Double( (i + topX)*SQUARESIZE -1, (j + topY)*SQUARESIZE -1 , SQUARESIZE + 2, SQUARESIZE + 2) );
                    }
                }
            }
            
           
            //set the bounds of all the labels
            //because the frame of reference changes, the timers must 
            //move with the frame of reference
            Insets insets = panel.getInsets();
            //set the position for the labels
            translateI = insets.left + (int) (topX*SQUARESIZE) ;
            translateJ = insets.top + (int) (topY*SQUARESIZE);
            
            Dimension dim = secondsLeftLabel.getPreferredSize();
            secondsLeftLabel.setBounds( translateI + 5, translateJ, dim.width, dim.height);
            
            dim = moveCounterLabel.getPreferredSize();
            moveCounterLabel.setBounds( translateI + 5 , translateJ + 25, dim.width, dim.height);
            
            dim = diamondCollectedLabel.getPreferredSize();
            diamondCollectedLabel.setBounds(translateI + SQUARESIZE * 3 , translateJ, dim.width, dim.height);
            
            //checks if there is still an exit, 
            //if exit does not exist, increment the level and restore

            if (exit == 0)
            {
                if (getLevel() <= numberOfLevels)
                {
                    setLevel(getLevel() + 1);
                    isNewLevel = true;
                    restore();
                    repaint();
                }
            }
        }
    }
    public static void main (String[] args)
    {
        //create frame called Boulder DAsh
        final JFrame f = new JFrame("Boulder Dash");
        final BoulderDash bd = new BoulderDash(f);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.add( bd );
        //add a window listener to kill the timers when the app closes
        f.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we) {
                bd.finalize(); // first kill the timer of the GameOfLife component
                f.dispose(); // and now we can dispose of the frame
            }
        });
        f.setVisible(true);
    }
}