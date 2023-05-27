package flyObjects.flyChildren.enemy;

import javafx.scene.image.Image;
import flyObjects.flyChildren.enemy.beat.Score;
import util.Tool;


/**
 * 小敌机
 */
public class SmallPlane extends EnemyPlane implements Score {
    private static Image[] images;
    private int score = 5;//分数

    static {
        images = new Image[2];
        images[0]=Tool.readImg("planeSmall.png");
        images[1]=Tool.readImg("bombSmall.png");
    }

    public SmallPlane(int life, int bulletCount) {
        super(images[0]);
        setDifficulty(life,bulletCount);
        ySpeed = 1;
    }
    public void setImage() {
        setImage(images);
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setDifficulty(int life, int bulletCount) {
        this.life = life;
        this.bulletCount = bulletCount;
    }
}
