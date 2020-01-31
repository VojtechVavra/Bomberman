package sample;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Player implements IPlayer{
    private final Canvas playerCanvas;
    private Group groupScene;
    private Stage primaryStage;
    private Scene mainScene;
    private Scene scene2;
    //private BorderPane bp;

    private final int MAX_BOMBS = 10;    // 50
    private int[] availableBombs = new int[] {5};   // used array to be able to pass integer by reference
    private int bombRange = 4;  // 3 default
    private int velocity = 2;
    private int posX = 0;
    private int posY = 0;
    private boolean isDead = false;

    private boolean isMovingUp = false;
    private boolean isMovingDown = false;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private PlayerDirection lastDirection;

    private Text bombText;
    private Text boomSizeText;

    public enum PlayerDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    /*public Player(Group gs)
    {
        this(gs, false);
    }

    public Player(Group gs, boolean visible)
    {
        this.groupScene = gs;
        playerCanvas = new Canvas(30,30);
        GraphicsContext gc = playerCanvas.getGraphicsContext2D();
        Rectangle rect1 = new Rectangle(0, 0, 200, 200);
        rect1.setFill(Color.BLUE);

        gc.setFill(Color.BLACK);
        gc.rect(0, 0, 30, 30);

        //gc.drawImage(Constants.poulpi, 0, 0, 30, 30);
        setPlayerPosition(51, 51);
        this.playerCanvas.setVisible(visible);

        CreateStats();
        RunMove();
        CatchKeyPressing();
    }*/

    public Player(Group gs, Scene main, Scene scene, Stage primaryStage, boolean visible)
    {
        this.groupScene = gs;
        this.mainScene = main;
        this.scene2 = scene;
        this.primaryStage = primaryStage;
        playerCanvas = new Canvas(30,30);
        GraphicsContext gc = playerCanvas.getGraphicsContext2D();

        // Blue cube instead of player texture
        //gc.setFill(Color.BLUE);
        //gc.fillRect(0,0,30,31);

        gc.drawImage(Constants.poulpi, 0, 0, 30, 30);
        setPlayerPosition(51, 51);
        this.playerCanvas.setVisible(visible);

        CreateStats();
        RunMove();
        CatchKeyPressing();
    }

    private void RunMove()
    {
        final long startNanoTime = System.nanoTime();
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                double t = (currentNanoTime - startNanoTime) / 100000000000.0;
                bombText.setText("Bombs: " + getAvailAbleBombs()[0]);
                boomSizeText.setText("Boom size: " + getBombRange());
                movePlayer();
            }
        }.start();
    }

    private void CatchKeyPressing()
    {
        scene2.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(Constants.isPlay)
                {
                    if(isDead){
                        if (event.getCode() == KeyCode.ESCAPE) {
                            Constants.isPlay = false;
                            //text1.setText("Main menu");
                            System.out.println("You are dead");
                            primaryStage.setScene(mainScene);
                            //scene = scene1;
                            primaryStage.show();
                        }
                    }
                    if(!isDead)
                    {
                        if (event.getCode() == KeyCode.ESCAPE) {
                            Constants.isPlay = false;
                            //text1.setText("Main menu");
                            System.out.println("Entering Main menu");

                            // set the scene
                            primaryStage.setScene(mainScene);
                            //scene = scene1;
                            primaryStage.show();
                        }
                        if (event.getCode() == KeyCode.SPACE) {
                            System.out.println("Bomb dropped");
                            DropBomb();
                        }
                        if (event.getCode() == KeyCode.LEFT) {
                            isMovingLeft = false;
                        }
                        else if (event.getCode() == KeyCode.RIGHT) {
                            isMovingRight = false;
                        }
                        else if (event.getCode() == KeyCode.UP) {
                            isMovingUp = false;
                        }
                        else if (event.getCode() == KeyCode.DOWN) {
                            isMovingDown = false;
                        }
                    }
                }
            }
        });

        scene2.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(!playerCanvas.isVisible())
                    isDead = true;
                if(Constants.isPlay && !isDead)
                {
                    if (event.getCode() == KeyCode.LEFT) {
                        //System.out.println("Move left");
                        isMovingUp = false;
                        isMovingDown = false;
                        isMovingLeft = true;
                        isMovingRight = false;
                        lastDirection = Player.PlayerDirection.LEFT;
                        //player1.moveLeft();
                    }
                    else if (event.getCode() == KeyCode.RIGHT) {
                        //System.out.println("Move right");
                        //player1.moveRight();
                        isMovingUp = false;
                        isMovingDown = false;
                        isMovingLeft = false;
                        isMovingRight = true;
                        lastDirection = Player.PlayerDirection.RIGHT;
                    }
                    else if (event.getCode() == KeyCode.UP) {
                        //System.out.println("Move up");
                        //player1.moveUp();
                        isMovingUp = true;
                        isMovingDown = false;
                        isMovingLeft = false;
                        isMovingRight = false;
                        lastDirection = Player.PlayerDirection.UP;
                    }
                    else if (event.getCode() == KeyCode.DOWN) {
                        //System.out.println("Move down");
                        //player1.moveDown();
                        isMovingUp = false;
                        isMovingDown = true;
                        isMovingLeft = false;
                        isMovingRight = false;
                        lastDirection = Player.PlayerDirection.DOWN;
                    }
                }

            }
        });
    }

    public Canvas getCanvas()
    {
        return this.playerCanvas;
    }

    public void setVisible(boolean visible)
    {
        this.playerCanvas.setVisible(visible);
    }

    public void setPlayerPosition(int x, int y)
    {
        this.posX = x;
        this.posY = y;
        this.playerCanvas.setLayoutX(this.posX);
        this.playerCanvas.setLayoutY(this.posY);
    }

    /*public Rectangle getBounds() {
        return new Rectangle(posX, posY, 30, 30);
    }*/

    private boolean canMove(PlayerDirection pd)
    {
        Rectangle r3 = new Rectangle(posX, posY - this.velocity, 30, 30);
        if(pd == PlayerDirection.UP){
            r3 = new Rectangle(posX, posY - this.velocity, 30, 30);
        } else if(pd == PlayerDirection.DOWN){
            r3 = new Rectangle(posX, posY + this.velocity, 30, 30);
        } else if(pd == PlayerDirection.RIGHT){
            r3 = new Rectangle(posX + this.velocity, posY, 30, 30);
        } else if(pd == PlayerDirection.LEFT){
            r3 = new Rectangle(posX - this.velocity, posY, 30, 30);
        }

        boolean canmove = true;
        for (int i = 0; i < Constants.objectCubes.size(); i++) {
            Rectangle r2 = Constants.objectCubes.get(i);

            if (r3.intersects(r2.getLayoutBounds())) {
                canmove = false;
            }
        }
        return canmove;
    }

    public void movePlayer()
    {
        if(isMovingRight && canMove(PlayerDirection.RIGHT))
        {
            this.posX += this.velocity;
            this.playerCanvas.setLayoutX(this.posX);
        }
        else if (isMovingLeft && canMove(PlayerDirection.LEFT))
        {
            this.posX -= this.velocity;
            this.playerCanvas.setLayoutX(this.posX);
        }
        if(isMovingUp && canMove(PlayerDirection.UP))
        {
            this.posY -= this.velocity;
            this.playerCanvas.setLayoutY(this.posY);
        }
        else if (isMovingDown && canMove(PlayerDirection.DOWN))
        {
            this.posY += this.velocity;
            this.playerCanvas.setLayoutY(this.posY);
        }
    }

    public int[] getPlayerPosition()
    {
        return new int[]{ this.posX, this.posY};
    }

    public int getBombRange()
    {
        return this.bombRange;
    }

    private void DecreaseAvailableBombs()
    {
        this.availableBombs[0]--;
    }

    private void IncreaseAvailableBombs()
    {
        if(this.availableBombs[0] < this.MAX_BOMBS)
            this.availableBombs[0]++;
    }

    public int[] getAvailAbleBombs()
    {
        return this.availableBombs;
    }

    public void setAvailAbleBombs(int bombs)
    {
        this.availableBombs[0] = bombs;
    }

    public void DropBomb()
    {
        if(this.availableBombs[0] > 0)
        {
            DecreaseAvailableBombs();

            int bombPosX = this.posX;
            int bombPosY = this.posY;
            if(isMovingUp || lastDirection == PlayerDirection.UP)
            {
                bombPosX = this.posX / 50 * 50;
                bombPosY = (int)((this.posY + 20) / 50) * 50;
            }
            else if(isMovingDown || lastDirection == PlayerDirection.DOWN)
            {
                bombPosX = this.posX / 50 * 50;
                bombPosY = (int)((this.posY - 0) / 50) * 50;
            }
            else if(isMovingLeft || lastDirection == PlayerDirection.LEFT)
            {
                bombPosX = (int)((this.posX + 20) / 50) * 50;
                bombPosY = this.posY / 50 * 50;
            }
            else if (isMovingRight || lastDirection == PlayerDirection.RIGHT)
            {
                bombPosX = (int)((this.posX + 5) / 50) * 50;    // bombPosX = (int)((this.posX - 0) / 50) * 50;
                bombPosY = this.posY / 50 * 50;
            }

            Bomb bomb = new Bomb(bombPosX, bombPosY, this.bombRange, this.availableBombs, this.MAX_BOMBS, this.groupScene, this.playerCanvas);

            this.groupScene.getChildren().add(bomb.getCanvas());
        }
    }

    public boolean getStatus()
    {
        return this.isDead;
    }

    private void CreateStats()
    {
        this.bombText = new Text(Constants.WIDTH / 2 - 200, 30, "Bombs: " + getAvailAbleBombs()[0]);
        this.bombText.setFill(Color.ORANGE);
        this.bombText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        this.boomSizeText = new Text(Constants.WIDTH / 2 + 50, 30, "Boom size: " + getBombRange());
        this.boomSizeText.setFill(Color.ORANGE);
        this.boomSizeText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        this.groupScene.getChildren().add(this.bombText);
        this.groupScene.getChildren().add(this.boomSizeText);
    }

}
