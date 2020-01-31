package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Bomb {
    private final Canvas bombCanvas;
    private GraphicsContext gc;
    private Group groupScene;
    private int bombRange;
    private int[] availableBombs;
    private int max_bombs;
    private Canvas player;

    Bomb(int bombPosX, int bombPosY, int bombRange, int[] availableBombs, int MAX_BOMBS, Group groupScene, Canvas player1)
    {
        this.groupScene = groupScene;
        this.bombRange = bombRange;
        this.availableBombs = availableBombs;
        this.max_bombs = MAX_BOMBS;
        this.bombCanvas = new Canvas(45,45);
        this.gc = bombCanvas.getGraphicsContext2D();
        this.gc.drawImage(Constants.bomb, 0,0, 50, 45);  // w: 50, h: 45
        this.bombCanvas.setLayoutX(bombPosX);
        this.bombCanvas.setLayoutY(bombPosY);
        this.player = player1;
        SetTimerToExplode(this.bombCanvas, this.gc);
    }

    public void SetTimerToExplode(Canvas bombCanvas, GraphicsContext gc) {
        // wait 3 secs
        // https://stackoverflow.com/questions/39235545/add-delay-after-platform-runlater-runnable
        final KeyFrame kf3 = new KeyFrame(Duration.seconds(1), e -> ChangeBombColor(bombCanvas, gc, 2));
        final Timeline timeline = new Timeline(kf3);
        Platform.runLater(timeline::play);
    }

    private void IncreaseAvailableBombs()
    {
        if(this.availableBombs[0] < this.max_bombs)
            this.availableBombs[0]++;
    }

    private synchronized void ChangeBombColor(Canvas bombCanvas, GraphicsContext gc, int sec)
    {
        if(sec == 2)
        {
            int newSec = ++sec;
            final KeyFrame kf3 = new KeyFrame(Duration.seconds(1), e -> ChangeBombColor(bombCanvas, gc, newSec));
            gc.clearRect(0, 0, 45, 45);
            gc.drawImage(Constants.bomb_fire, 0,0, 45, 45);
            final Timeline timeline = new Timeline(kf3);
            Platform.runLater(timeline::play);
        }
        else if(sec == 3)
        {
            final KeyFrame kf3 = new KeyFrame(Duration.seconds(1), e -> MakeExplosion(bombCanvas));
            gc.clearRect(0, 0, 45, 45);
            gc.drawImage(Constants.bomb_red, 0,0, 45, 45);
            final Timeline timeline = new Timeline(kf3);
            Platform.runLater(timeline::play);
        }
    }

    private void MakeExplosion(Canvas bombCanvas)
    {
        // delete bomb
        groupScene.getChildren().remove(bombCanvas);

        // create BOOM
        int bombPosX = (int)bombCanvas.getLayoutX() / 50;
        int bombPosY = (int)bombCanvas.getLayoutY() / 50;

        int bombRadiusReducedX1 = 0;
        int bombRadiusReducedX2 = 0;
        int bombRadiusReducedY1 = 0;
        int bombRadiusReducedY2 = 0;
        for (int x1 = bombPosX; x1 < bombPosX + this.bombRange; x1++)
        {
            if(Constants.map1[bombPosY][x1] == 1 || Constants.map1[bombPosY][x1] == 2) {
                //bombRadiusReducedX++;
                break;
            }
            else if(GameMap.generatedMap[bombPosY][x1] == 3) {
                bombRadiusReducedX1++;
                removeBox(bombPosY, x1);
                break;
            }
            else
                bombRadiusReducedX1++;
        }
        for (int x2 = bombPosX; x2 > bombPosX - this.bombRange; x2--)
        {
            if(Constants.map1[bombPosY][x2] == 1 || Constants.map1[bombPosY][x2] == 2)
                break;
            else if(GameMap.generatedMap[bombPosY][x2] == 3) {
                bombRadiusReducedX2++;
                removeBox(bombPosY, x2);
                break;
            }
            else
                bombRadiusReducedX2++;
        }
        for (int y1 = bombPosY; y1 < bombPosY + this.bombRange; y1++)
        {
            if(Constants.map1[y1][bombPosX] == 1 || Constants.map1[y1][bombPosX] == 2) {
                //bombRadiusReducedX++;
                break;
            }
            //else if(Constants.map1[y1][bombPosX] == 3)
            else if(GameMap.generatedMap[y1][bombPosX] == 3)
            {
                bombRadiusReducedY1++;
                removeBox(y1, bombPosX);
                break;
            }
            else
                bombRadiusReducedY1++;
        }
        for (int y2 = bombPosY; y2 > bombPosY - this.bombRange; y2--)
        {
            if(Constants.map1[y2][bombPosX] == 1 || Constants.map1[y2][bombPosX] == 2)
                break;
            else if(GameMap.generatedMap[y2][bombPosX] == 3)
            {
                bombRadiusReducedY2++;
                removeBox(y2, bombPosX);
                break;
            }
            else
                bombRadiusReducedY2++;
        }

        for(int bx = bombPosX - bombRadiusReducedX2; bx < bombPosX + bombRadiusReducedX1; bx++)
        {
            if((int)Math.round(this.player.getLayoutX() / 50) == bx && ((int)Math.round(this.player.getLayoutY()) / 50) == bombPosY)
            {
                System.out.println("Player died by top X axis boom");
                this.player.setVisible(false);
            }
        }

        for(int by = bombPosY - bombRadiusReducedY2; by < bombPosY + bombRadiusReducedY1; by++)
        {
            if((int)Math.round(this.player.getLayoutY() / 50) == by && (int)Math.round(this.player.getLayoutX() / 50) == bombPosX)
            {
                System.out.println("Player died by top Y axis boom");
                this.player.setVisible(false);
            }
        }

        if(!this.player.isVisible())
        {
            PopupDieText();
        }

        // make explosion
        //final Canvas fireX = new Canvas(50,50);
        Circle fireCircleX = new Circle(bombCanvas.getLayoutX() + 25, bombCanvas.getLayoutY() + 25, 50);
        //Ellipse fireEllipseX = new Ellipse(bombCanvas.getLayoutX() + 25, bombCanvas.getLayoutY() + 25, getBombRange() * 50, 20);
        Ellipse fireEllipseX1 = new Ellipse(bombCanvas.getLayoutX() + 25 + ((bombRadiusReducedX1 - 1) * 50) / 2, bombCanvas.getLayoutY() + 25, (bombRadiusReducedX1 - 1) * 50, 20);
        Ellipse fireEllipseX2 = new Ellipse(bombCanvas.getLayoutX() + 25 - ((bombRadiusReducedX2 - 1) * 50) / 2, bombCanvas.getLayoutY() + 25, (bombRadiusReducedX2 - 1) * 50, 20);
        Ellipse fireEllipseY1 = new Ellipse(bombCanvas.getLayoutX() + 25, bombCanvas.getLayoutY() + 25 + ((bombRadiusReducedY1 - 1) * 50) / 2, 20, (bombRadiusReducedY1 - 1) * 50);
        Ellipse fireEllipseY2 = new Ellipse(bombCanvas.getLayoutX() + 25, bombCanvas.getLayoutY() + 25 - ((bombRadiusReducedY2 - 1) * 50) / 2, 20, (bombRadiusReducedY2 - 1) * 50);

        fireCircleX.setFill(new ImagePattern(Constants.fire));
        fireEllipseX1.setFill(new ImagePattern(Constants.fire));
        fireEllipseX2.setFill(new ImagePattern(Constants.fire));
        fireEllipseY1.setFill(new ImagePattern(Constants.fire));
        fireEllipseY2.setFill(new ImagePattern(Constants.fire));

        this.groupScene.getChildren().add(fireEllipseX1);
        this.groupScene.getChildren().add(fireEllipseX2);
        this.groupScene.getChildren().add(fireEllipseY1);
        this.groupScene.getChildren().add(fireEllipseY2);

        Constants.soundClip.play(1.0);
        Constants.soundClip.setPriority(100);

        IncreaseAvailableBombs();

        final KeyFrame kf3 = new KeyFrame(Duration.seconds(0.5), e -> ClearFire(fireEllipseX1, fireEllipseX2, fireEllipseY1, fireEllipseY2));
        final Timeline timeline = new Timeline(kf3);
        Platform.runLater(timeline::play);
    }

    private void ClearFire(Ellipse fireCanvasX1, Ellipse fireCanvasX2, Ellipse fireCanvasY1, Ellipse fireCanvasY2) {
        groupScene.getChildren().remove(fireCanvasX1);
        groupScene.getChildren().remove(fireCanvasX2);
        groupScene.getChildren().remove(fireCanvasY1);
        groupScene.getChildren().remove(fireCanvasY2);
    }

    private void removeBox(int y, int x)
    {
        GameMap.editFieldOfMap(y, x, 0);
        removeFromObjectCubes(y, x);
    }

    private void removeFromObjectCubes(int y, int x)
    {
        for (int i = 0; i < Constants.objectCubes.size(); i++) {
            Rectangle rectangle = Constants.objectCubes.get(i);
            if(rectangle.getY() == y * Constants.BLOCK_SIZE && rectangle.getX() == x * Constants.BLOCK_SIZE)
                Constants.objectCubes.remove(i);
        }
    }

    public Canvas getCanvas()
    {
        return this.bombCanvas;
    }

    private void PopupDieText()
    {
        final Text text1_you_died = new Text(Constants.WIDTH / 2 - 260, Constants.HEIGHT / 2 -100, "You died!\n");
        text1_you_died.setFill(Color.ORANGE);
        text1_you_died.setFont(Font.font("Verdana", FontWeight.BOLD, 100));

        final Text text2_press_esc = new Text(Constants.WIDTH / 2 - 260, Constants.HEIGHT / 2, "Press [ESC] to exit");
        text2_press_esc.setFill(Color.ORANGE);
        text2_press_esc.setFont(Font.font("Verdana", FontWeight.BOLD, 50));

        this.groupScene.getChildren().add(text1_you_died);
        this.groupScene.getChildren().add(text2_press_esc);
    }


}
