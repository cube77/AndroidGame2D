package de.fetim.androidgame2d;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ChibiCharacter extends GameObject {

    private static final int ROW_TOP_TO_BOTTOM = 0;
    private static final int ROW_RIGHT_TO_LEFT = 1;
    private static final int ROW_LEFT_TO_RIGHT = 2;
    private static final int ROW_BOTTOM_TO_TOP = 3;

    // Row index of Image are being used.
    private int rowUsing = ROW_LEFT_TO_RIGHT;

    private int colUsing;

    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;

    // Velocity of game character (pixel/millisecond)
    public static final float VELOCITY = 0.3f;

    private int movingVectorX = 10;
    private int movingVectorY = 5;

    // Joystick parameters
    private int CircleX;
    private int CircleY;

    private int XTouchVectorLength;
    private int YTouchVectorLength;
    private float LengthOfTouchVector;

    private int JoystickCircleRadius = 250; // radius of the JoystickCirce in Pixel

    private int TouchX;
    private int TouchY;

    private float JoystickX;
    private float JoystickY;



    private long lastDrawNanoTime =-1;

    private GameSurface gameSurface;

    public ChibiCharacter(GameSurface gameSurface, Bitmap image, int x, int y) {
        super(image, 4, 3, x, y);

        this.gameSurface= gameSurface;

        this.topToBottoms = new Bitmap[colCount]; // 3
        this.rightToLefts = new Bitmap[colCount]; // 3
        this.leftToRights = new Bitmap[colCount]; // 3
        this.bottomToTops = new Bitmap[colCount]; // 3

        for(int col = 0; col< this.colCount; col++ ) {
            this.topToBottoms[col] = this.createSubImageAt(ROW_TOP_TO_BOTTOM, col);
            this.rightToLefts[col]  = this.createSubImageAt(ROW_RIGHT_TO_LEFT, col);
            this.leftToRights[col] = this.createSubImageAt(ROW_LEFT_TO_RIGHT, col);
            this.bottomToTops[col]  = this.createSubImageAt(ROW_BOTTOM_TO_TOP, col);
        }

        //initialize Joystick parameters
        TouchX = 0;
        TouchY = 0;
        JoystickX = 0;
        JoystickY = 0;
        this.CircleX = 2300;//this.gameSurface.getWidth()/2;
        this.CircleY = 1150;//this.gameSurface.getHeight()/2;
        this.XTouchVectorLength = 0;
        this.YTouchVectorLength = 0;
        this.LengthOfTouchVector = 10;

    }

    public Bitmap[] getMoveBitmaps()  {
        switch (rowUsing)  {
            case ROW_BOTTOM_TO_TOP:
                return  this.bottomToTops;
            case ROW_LEFT_TO_RIGHT:
                return this.leftToRights;
            case ROW_RIGHT_TO_LEFT:
                return this.rightToLefts;
            case ROW_TOP_TO_BOTTOM:
                return this.topToBottoms;
            default:
                return null;
        }
    }

    public Bitmap getCurrentMoveBitmap()  {
        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[this.colUsing];
    }


    public void update()  {
        this.colUsing++;
        if(colUsing >= this.colCount)  {
            this.colUsing =0;
        }
        // Current time in nanoseconds
        long now = System.nanoTime();

        // Never once did draw.
        if(lastDrawNanoTime==-1) {
            lastDrawNanoTime= now;
        }
        // Change nanoseconds to milliseconds (1 nanosecond = 1000000 milliseconds).
        int deltaTime = (int) ((now - lastDrawNanoTime)/ 1000000 );

        // Distance moves
        float distance = VELOCITY * deltaTime;

        double movingVectorLength = Math.sqrt(movingVectorX* movingVectorX + movingVectorY*movingVectorY);

        // Calculate the new position of the game character.
        this.x = x +  (int)(distance* movingVectorX / movingVectorLength);
        this.y = y +  (int)(distance* movingVectorY / movingVectorLength);

        // When the game's character touches the edge of the screen, then change direction

        if(this.x < 0 )  {
            this.x = 0;
            this.movingVectorX = - this.movingVectorX;
        } else if(this.x > this.gameSurface.getWidth() -width)  {
            this.x= this.gameSurface.getWidth()-width;
            this.movingVectorX = - this.movingVectorX;
        }

        if(this.y < 0 )  {
            this.y = 0;
            this.movingVectorY = - this.movingVectorY;
        } else if(this.y > this.gameSurface.getHeight()- height)  {
            this.y= this.gameSurface.getHeight()- height;
            this.movingVectorY = - this.movingVectorY ;
        }

        // rowUsing
        if( movingVectorX > 0 )  {
            if(movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            }else if(movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            }else  {
                this.rowUsing = ROW_LEFT_TO_RIGHT;
            }
        } else {
            if(movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            }else if(movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            }else  {
                this.rowUsing = ROW_RIGHT_TO_LEFT;
            }
        }
    }

    public void draw(Canvas canvas)  {
        Bitmap bitmap = this.getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap,x, y, null);


        // draw a circle
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.RED);
        canvas.drawCircle(CircleX,
                          CircleY,
                          JoystickCircleRadius,
                          paint);

        paint.setColor(Color.WHITE);
        canvas.drawCircle(CircleX,
                          CircleY,
                          20,
                          paint);

        // draw the small Joystick circle
        paint.setColor(Color.WHITE);
        canvas.drawCircle(this.JoystickX,
                          this.JoystickY,
                          20,
                          paint);
        // draw debug text
        String txt = "X-Koord: "+ this.gameSurface.getWidth() + " Y-Koord: " + this.gameSurface.getHeight();
        paint.setColor(Color.RED);
        paint.setTextSize(40);

        canvas.drawText(
                txt,
                0,
                (this.gameSurface.getHeight()/10)*7,
                paint // Paint
        );

        txt = "Touch Coordinates X=" + this.TouchX + " Y=" + this.TouchY + " Version 9";
        canvas.drawText(
                txt,
                0,
                (this.gameSurface.getHeight()/10)*8,
                paint // Paint
                );

        txt = "XTouchVectorLength=" + this.XTouchVectorLength + " YTouchVectorLength= " + this.YTouchVectorLength + "VectorLength = " + this.LengthOfTouchVector;
        canvas.drawText(
                txt,
                0,
                (this.gameSurface.getHeight()/10)*9,
                paint // Paint
        );

        txt = "JoystickX=" + this.JoystickX + " JoystickY=" + this.JoystickY ;
        canvas.drawText(
                txt,
                0,
                (this.gameSurface.getHeight()/10)*10,
                paint //
        );
        // Last draw time.
        this.lastDrawNanoTime= System.nanoTime();
    }

    public void setMovingVector(int movingVectorX, int movingVectorY)  {
        this.movingVectorX= movingVectorX;
        this.movingVectorY = movingVectorY;
    }

    public void setTouchCoordinates(int X, int Y)  {
        this.TouchX= X;
        this.TouchY= Y;

        // X TouchVector length
        this.XTouchVectorLength = CircleX - X;
        // Y Touch Vector length
        this.YTouchVectorLength = CircleY - Y;

        setMovingVector(-this.XTouchVectorLength, -this.YTouchVectorLength);

        this.LengthOfTouchVector = (int)Math.sqrt(XTouchVectorLength * XTouchVectorLength + YTouchVectorLength * YTouchVectorLength); // length of touchvector relative to joystickCentre

        float test = this.JoystickCircleRadius;

        this.JoystickX = (float)((-1)*((test/this.LengthOfTouchVector)*this.XTouchVectorLength))+CircleX;
        this.JoystickY = (float)((-1)*((test/this.LengthOfTouchVector)*this.YTouchVectorLength))+CircleY;


    }
}