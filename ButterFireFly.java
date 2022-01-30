public class ButterFireFly
{
    private int directionX;
    private int directionY;
    private int posX;
    private int posY;
    private BDTile type;
    private BoulderDash BD;
    private void rotateCCW()
    {
        int x = directionX;
        int y = directionY;
        directionX = - y;
        directionY = x;
    }
    private void rotateCW()
    {
        int x = directionX;
        int y = directionY;
        directionX = y;
        directionY = -x;
    }
    public void move()
    {
        //moves the butterfly or firefly
        //if it is blocked, it will change directions automatically
        boolean moved = false;
        int oldX, oldY, newX, newY;
        oldX = getPosX();
        oldY = getPosY();
        newX = oldX + getXDir();
        newY = oldY + getYDir();
        if (BD.getIsGameOver())
        {
            return;
        }        
        //first check if you can move in the direction that it is facing.
        for(int counter = 0; !moved && counter < 4; counter++)
        {
            if ( BD.compareTile(newX, newY, BDTile.EMPTY) )
            {
                moved = true;
                //need to synchronize lock here
                BD.setTile(oldX, oldY, BDTile.EMPTY);
                BD.setTile(newX, newY, type);
                //change the values of posX and posY to reflect new positions
                setPosXY(newX, newY);
            }
            //else change direction and check if that direction is free
            else
            {
                //rotate CW or CCW depending on type of fly
                if (type == BDTile.FIREFLY)
                {
                    rotateCW();
                }
                else
                {
                    rotateCCW();
                }
                //reset the value of newX and newY
                newX = oldX + getXDir();
                newY = oldY + getYDir();
            }
        }
    }
    public int getXDir()
    {
        return directionX;
    }
    public int getYDir()
    {
        return directionY;
    }
    //sets the position of the fly
    public void setPosXY(int x, int y)
    {
        posY = y;
        posX = x;
    }
    public int getPosX()
    {
        return posX;
    }
    public int getPosY()
    {
        return posY;
    }
    public void setXYDirection(int x, int y)
    {
        directionX = x;
        directionY = y;
    }
    public ButterFireFly(int x, int y, BDTile t, BoulderDash dash)
    {
        setPosXY(x, y);
        setXYDirection(0, 1);
        type = t;
        BD = dash;
    }
    public void setInitialValues(int x, int y, BDTile t, BoulderDash dash)
    {
        setPosXY(x, y);
        setXYDirection(0, 1);
        type = t;
        BD = dash;
    }
}