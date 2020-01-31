package sample;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class Main extends Application {

    private Scene scene;
    private Scene scene1;
    private Scene scene2;
    private GameMap map;
    public Group playScene;
    private Player player1;
    private Button continueBtn;
    private Text text1;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        BorderPane mainMenu = new BorderPane();
        primaryStage.setTitle("Bomberman");
        scene1 = new Scene(mainMenu, Constants.WIDTH, Constants.HEIGHT);
        //primaryStage.setResizable(false);

        MakeMainMenuUI(mainMenu);
        CheckifSaveFileExist();
        Pane buttonsPane = CreateButtons(primaryStage);
        mainMenu.getChildren().add(buttonsPane);

        primaryStage.setScene(scene1);
        scene = scene1;
        primaryStage.show();

        scene1.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(!Constants.isPlay)
                {
                    if (event.getCode() == KeyCode.ENTER) {
                        try {
                            CreateMap(primaryStage, false);
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        }

                        Constants.isPlay = true;
                        text1.setText("Exiting game");
                        System.out.println("Starting new game!");
                        continueBtn.setVisible(true);
                        // set the scene
                        primaryStage.setScene(scene2);
                        scene = scene2;
                        primaryStage.show();
                    }
                    else if (event.getCode() == KeyCode.ESCAPE) {
                        System.out.println("Exiting program");
                        Constants.isPlay = false;
                        Platform.exit();
                    }
                }
            }
        });
    }

    private void SaveGame()
    {
        // savegame
        Save newSave = new Save(Constants.saveName, player1);
        try {
            newSave.saveGame();
            System.out.println("Game saved!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in saving game!");
        }
    }

    public void CreateMap(Stage primaryStage, boolean loadFromSave) throws FileNotFoundException {
        if(!loadFromSave)
        {
            Group playScene = new Group();
            Constants.objectCubes.clear();

            // create structs
            map = new GameMap(playScene);
            playScene.getChildren().add(map.getGroup());

            // create new scene
            scene2 = new Scene(playScene, Constants.WIDTH, Constants.HEIGHT);
            scene2.setFill(Color.SADDLEBROWN);

            // create player
            player1 = new Player(playScene, scene1, scene2, primaryStage, true);
            playScene.getChildren().add(player1.getCanvas());
        }

        else if(loadFromSave) {
            int[][] arr = new int[15][21];
            int actualBombsAvailable = 5;
            int playerPosition[] = {51, 51};

            try {
                File fileDir = new File(Constants.saveName);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(fileDir), "UTF8"));

                String str;
                int line = 0;
                int linecount = 0;
                while ((str = in.readLine()) != null) {
                    linecount++;
                    String decryptedStr = AES.decrypt(str, Constants.secretKey);
                    if(linecount == 1)
                        continue;
                    else if(linecount == 2)
                        actualBombsAvailable = Integer.parseInt(decryptedStr);
                    else if(linecount == 3)
                    {
                        String b[] = decryptedStr.split(" ");
                        playerPosition[0] = Integer.parseInt(b[0]);
                        playerPosition[1] = Integer.parseInt(b[1]);
                    }
                    else {
                        String a[] = decryptedStr.substring(0, decryptedStr.length() - 1).split(" ");
                        for (int d = 0; d < a.length; d++) {
                            System.out.print(a[d] + " ");
                            arr[line][d] = Integer.parseInt(a[d]);
                        }
                        line++;
                        System.out.println();
                    }
                }
                //System.out.println(Arrays.toString(arr));
                System.out.println(playerPosition[0] + " " + playerPosition[1]);
                System.out.println("Game loaded.\n");
                in.close();
            }
            catch (UnsupportedEncodingException e)
            {
                System.out.println(e.getMessage());
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

            Group playScene = new Group();
            Constants.objectCubes.clear();

            // create structs
            map = new GameMap(playScene, arr);
            playScene.getChildren().add(map.getGroup());

            // create new scene
            scene2 = new Scene(playScene, Constants.WIDTH, Constants.HEIGHT);
            scene2.setFill(Color.SADDLEBROWN);

            // create player
            player1 = new Player(playScene, scene1, scene2, primaryStage, true);
            player1.setPlayerPosition(playerPosition[0], playerPosition[1]);
            player1.setAvailAbleBombs(actualBombsAvailable);
            playScene.getChildren().add(player1.getCanvas());
        }
    }

    public void SetButtonActive(String buttonName, boolean visibility)
    {
        if(buttonName.contains("continueBtn"))
            continueBtn.setVisible(visibility);
    }

    private void CheckifSaveFileExist()
    {
        try {
            File fileDir = new File(Constants.saveName);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));
            String str;

            while ((str = in.readLine()) != null) {
                String decryptedStr = AES.decrypt(str, Constants.secretKey);
                System.out.println(decryptedStr);
                if(decryptedStr.equals("Hello from Bomberman save file!"))
                {
                    Constants.saveFound = true;
                    break;
                }
            }

            in.close();
        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void MakeMainMenuUI(BorderPane mainMenu)
    {
        text1 = new Text(Constants.WIDTH / 2 - 60, 100, "Bomberman");
        text1.setFill(Color.CHOCOLATE);
        //Font font1 = new Font("Verdana", Font.BOLD, 50);
        //text1.setFont(Font.font(java.awt.Font.SERIF, 50));
        text1.setFont(Font.font("Verdana", FontWeight.BOLD, 50));
        mainMenu.getChildren().add(text1);

        Rectangle rectangle = new Rectangle();
        rectangle.setSize(Constants.WIDTH, Constants.HEIGHT);
        mainMenu.setBackground(new Background(new BackgroundFill(Color.DARKGREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        Canvas floor_background = new Canvas(Constants.WIDTH,200);
        GraphicsContext floor_bg = floor_background.getGraphicsContext2D();
        floor_bg.setFill(Color.SADDLEBROWN);
        floor_bg.fillRect(0, 100, Constants.WIDTH + 50, 200);
        floor_background.setLayoutY(Constants.HEIGHT - 200);
        mainMenu.getChildren().add(floor_background);

        Canvas bomberman_background = new Canvas(1024,683);
        GraphicsContext bomberman_bg = bomberman_background.getGraphicsContext2D();
        bomberman_bg.drawImage(Constants.bomberman_bg, -250, 0, 1024, 683);
        bomberman_background.setLayoutY(40);
        mainMenu.getChildren().add(bomberman_background);

        Canvas box_background = new Canvas(1024,683);
        GraphicsContext box_bg = bomberman_background.getGraphicsContext2D();
        //box_bg.drawImage(Constants.metal_cube, 714, 272, 128, 128);
        //box_bg.drawImage(Constants.metal_cube, 714, 144, 128, 128);
        //box_bg.drawImage(Constants.metal_cube, 778, 272, 128, 128);
        //box_bg.drawImage(Constants.metal_cube, 650, 272, 128, 128);

        box_bg.drawImage(Constants.metal_cube, 714, 272, 128, 128);
        //box_bg.drawImage(Constants.metal_cube, 630, 400, 128, 128);
        //box_bg.drawImage(Constants.metal_cube, 798, 400, 128, 128);

        box_bg.drawImage(Constants.metal_cube, 650, 400, 128, 128);
        box_bg.drawImage(Constants.metal_cube, 778, 400, 128, 128);
        box_bg.drawImage(Constants.metal_cube, 842, 528, 128, 128);
        box_bg.drawImage(Constants.metal_cube, 714, 528, 128, 128);
        box_bg.drawImage(Constants.metal_cube, 586, 528, 128, 128);
        mainMenu.getChildren().add(box_background);
    }

    private Pane CreateButtons(Stage primaryStage)
    {
        // create a Pane
        Pane pane = new Pane();

        // create continue button
        continueBtn = new Button("Continue  ");
        continueBtn.setOnAction( e -> {
            Constants.isPlay = true;
            // set the scene
            primaryStage.setScene(scene2);
            scene = scene2;
            primaryStage.show();
        });
        // relocate button
        continueBtn.relocate(500, 160);
        continueBtn.setVisible(false);
        // add button
        pane.getChildren().add(continueBtn);

        // create new game button
        Button startBtn = new Button("New Game  ");
        startBtn.setOnAction( e -> {
            try {
                CreateMap(primaryStage, false);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            Constants.isPlay = true;
            text1.setText("Exiting game");
            System.out.println("Starting new game!");
            continueBtn.setVisible(true);
            // set the scene
            primaryStage.setScene(scene2);
            scene = scene2;
            primaryStage.show();
        });
        // relocate button
        startBtn.relocate(500, 200);
        // add button
        pane.getChildren().add(startBtn);

        // create button load
        Button loadBtn = new Button("Load  ");
        loadBtn.setOnAction(e ->
        {
            Constants.isPlay = true;
            text1.setText("Exiting game");
            System.out.println("Starting loaded game!");
            try {
                CreateMap(primaryStage, true);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            continueBtn.setVisible(true);
            // set the scene
            primaryStage.setScene(scene2);
            scene = scene2;
            primaryStage.show();
        });
        // relocate button
        loadBtn.relocate(570, 200 + 40);
        loadBtn.setVisible(Constants.saveFound);
        // add button
        pane.getChildren().add(loadBtn);

        // create button save game
        Button saveBtn = new Button("Save  ");
        saveBtn.setOnAction(e ->
        {
            if(player1 != null && !player1.getStatus())
            {
                SaveGame();
                // savegame
                try {
                    File fileDir = new File("sav1.dat");
                    Constants.saveFound = true;
                }
                catch (Exception d)
                {
                    System.out.println(d.getMessage());
                    Constants.saveFound = false;
                }
                loadBtn.setVisible(Constants.saveFound);
            }
            else
                System.out.println("Can't save when you are dead or nonborn!");
        });
        // relocate button
        saveBtn.relocate(500, 200 + 40);
        //saveBtn.setVisible(Constants.saveFound);
        // add button
        pane.getChildren().add(saveBtn);

        // create button
        Button exitBtn = new Button("Exit      ");
        exitBtn.setOnAction(e -> Platform.exit());
        // relocate button
        exitBtn.relocate(500, 200 + 80);
        // add button
        pane.getChildren().add(exitBtn);

        return pane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
