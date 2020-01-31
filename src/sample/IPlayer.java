package sample;

import javafx.scene.canvas.Canvas;

public interface IPlayer {
    Canvas getCanvas();
    void setPlayerPosition(int x, int y);
    void movePlayer();
    int[] getPlayerPosition();
    void DropBomb();
    boolean getStatus();
}
