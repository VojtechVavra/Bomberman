package sample;

import java.io.*;
import java.nio.charset.Charset;

public class Save {
    private String fileName;
    private Player player;

    Save(String saveName, Player player){
        this.fileName = saveName;
        this.player = player;
    };

    // Writing String To File when Using File Output Stream
    public void saveGame() throws IOException {

        FileOutputStream fos = new FileOutputStream(Constants.saveName);
        OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
        BufferedWriter out = new BufferedWriter(osw);
        String text = "Hello from Bomberman save file!";
        int availAbleBombs = this.player.getAvailAbleBombs()[0];
        String strAvailAbleBombs = String.valueOf(availAbleBombs);
        int[] playerPosition = this.player.getPlayerPosition();

        String encryptedStringText = AES.encrypt(text, Constants.secretKey);
        String encryptedAvailAbleBombs = AES.encrypt(strAvailAbleBombs, Constants.secretKey);

        out.write (encryptedStringText + "\n");
        out.write(encryptedAvailAbleBombs + "\n");

        String plPos = playerPosition[0] + " " + playerPosition[1];
        String encryptedPlPos = AES.encrypt(plPos, Constants.secretKey);
        out.write(encryptedPlPos + "\n");


        String lineToWrite = "";
        for(int y = 0; y < GameMap.generatedMap.length; y++)
        {
            for(int x = 0; x < GameMap.generatedMap[0].length; x++)
            {
                lineToWrite += GameMap.generatedMap[y][x] + " ";
            }
            out.write(AES.encrypt(lineToWrite, Constants.secretKey) + "\n");
            lineToWrite = "";
        }

        out.flush();
        fos.close();
        //System.out.println("File saved.");
    }
}
