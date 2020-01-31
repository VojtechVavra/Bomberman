package sample;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import java.util.Random;

public class GameMap {
    //private Canvas mapCanvas;
    private final Canvas borderCubes;
    private final int blockSize = 50;
    public static int generatedMap[][] = new int[Constants.map1.length][Constants.map1[0].length];
    public static Canvas canvasBox[][] = new Canvas[Constants.map1.length][Constants.map1[0].length];
    private static Group groupSceneMap;
    private Random rand;

    public GameMap(Group gs)
    {
        //this.groupSceneMap = gs;
        groupSceneMap = new Group();
        rand = new Random();
        borderCubes = new Canvas(Constants.WIDTH, Constants.HEIGHT);
        GraphicsContext gc = borderCubes.getGraphicsContext2D();

        for(int y = 0; y < Constants.map1.length; y++)
        {
            for(int x = 0; x < Constants.map1[y].length; x++)
            {
                generatedMap[y][x] = Constants.map1[y][x];
                if(Constants.map1[y][x] == 0){
                    generatedMap[y][x] = 0;
                    // Generate random integers in range 0 to 2
                    int randNum = rand.nextInt(3);

                    if(randNum == 1)
                    {
                        generatedMap[y][x] = 3;

                        final Canvas boxCanvas = new Canvas(blockSize, blockSize);
                        GraphicsContext gcBox = boxCanvas.getGraphicsContext2D();
                        gcBox.drawImage(Constants.wooden_box2, 0, 0, blockSize, blockSize);
                        boxCanvas.setLayoutX(x * blockSize);
                        boxCanvas.setLayoutY(y * blockSize);
                        groupSceneMap.getChildren().add(boxCanvas);
                        canvasBox[y][x] = boxCanvas;
                        Constants.saveRectangleBounds(x * 50, y * 50, blockSize, blockSize);
                    }
                }
                else if(Constants.map1[y][x] == 1){
                    final Canvas boxCanvas = new Canvas(blockSize, blockSize);
                    GraphicsContext gcBox = boxCanvas.getGraphicsContext2D();
                    gcBox.drawImage(Constants.metal_cube, 0, 0, blockSize, blockSize);
                    boxCanvas.setLayoutX(x * blockSize);
                    boxCanvas.setLayoutY(y * blockSize);
                    GameMap.groupSceneMap.getChildren().add(boxCanvas);
                    Constants.saveRectangleBounds(x * 50, y * 50, blockSize, blockSize);
                }
                else if(Constants.map1[y][x] == 2) {
                    final Canvas boxCanvas = new Canvas(blockSize, blockSize);
                    GraphicsContext gcBox = boxCanvas.getGraphicsContext2D();
                    gcBox.drawImage(Constants.wooden_box, 0, 0, blockSize, blockSize);
                    boxCanvas.setLayoutX(x * blockSize);
                    boxCanvas.setLayoutY(y * blockSize);
                    groupSceneMap.getChildren().add(boxCanvas);
                }
            }
        }

        if(generatedMap[1][1] != 0)
        {
            generatedMap[1][1] = 0;
            groupSceneMap.getChildren().remove(canvasBox[1][1]);
            removeFromObjectCubes(1, 1);
        }
        if(generatedMap[1][2] != 0)
        {
            generatedMap[1][2] = 0;
            groupSceneMap.getChildren().remove(canvasBox[1][2]);
            removeFromObjectCubes(1, 2);
        }
        if(generatedMap[2][1] != 0)
        {
            generatedMap[2][1] = 0;
            groupSceneMap.getChildren().remove(canvasBox[2][1]);
            removeFromObjectCubes(2, 1);
        }
    }

    public GameMap(Group gs, int map[][])
    {
        groupSceneMap = new Group();
        rand = new Random();
        borderCubes = new Canvas(Constants.WIDTH, Constants.HEIGHT);
        GraphicsContext gc = borderCubes.getGraphicsContext2D();
        generatedMap = map;

        for(int y = 0; y < Constants.map1.length; y++)
        {
            for(int x = 0; x < Constants.map1[y].length; x++)
            {
                if(generatedMap[y][x] == 1){
                    final Canvas boxCanvas = new Canvas(blockSize, blockSize);
                    GraphicsContext gcBox = boxCanvas.getGraphicsContext2D();
                    gcBox.drawImage(Constants.metal_cube, 0, 0, blockSize, blockSize);
                    boxCanvas.setLayoutX(x * blockSize);
                    boxCanvas.setLayoutY(y * blockSize);
                    GameMap.groupSceneMap.getChildren().add(boxCanvas);
                    Constants.saveRectangleBounds(x * 50, y * 50, blockSize, blockSize);
                }
                else if(generatedMap[y][x] == 2) {
                    final Canvas boxCanvas = new Canvas(blockSize, blockSize);
                    GraphicsContext gcBox = boxCanvas.getGraphicsContext2D();
                    gcBox.drawImage(Constants.wooden_box, 0, 0, blockSize, blockSize);
                    boxCanvas.setLayoutX(x * blockSize);
                    boxCanvas.setLayoutY(y * blockSize);
                    groupSceneMap.getChildren().add(boxCanvas);
                }
                else if(generatedMap[y][x] == 3)
                {
                    final Canvas boxCanvas = new Canvas(blockSize, blockSize);
                    GraphicsContext gcBox = boxCanvas.getGraphicsContext2D();
                    gcBox.drawImage(Constants.wooden_box2, 0, 0, blockSize, blockSize);
                    boxCanvas.setLayoutX(x * blockSize);
                    boxCanvas.setLayoutY(y * blockSize);
                    groupSceneMap.getChildren().add(boxCanvas);
                    canvasBox[y][x] = boxCanvas;
                    Constants.saveRectangleBounds(x * 50, y * 50, blockSize, blockSize);
                }
            }
        }
    }

    private void removeFromObjectCubes(int y, int x)
    {
        for (int i = 0; i < Constants.objectCubes.size(); i++) {
            Rectangle rectangle = Constants.objectCubes.get(i);
            if(rectangle.getY() == y * blockSize && rectangle.getX() == x * blockSize)
                Constants.objectCubes.remove(i);
        }
    }

    public static void editFieldOfMap(int y, int x, int num)
    {
        generatedMap[y][x] = num;
        groupSceneMap.getChildren().remove(canvasBox[y][x]);
    }

    public Group getGroup()
    {
        return groupSceneMap;
    }

    public Canvas getCanvas()
    {
        return this.borderCubes;
    }
}
