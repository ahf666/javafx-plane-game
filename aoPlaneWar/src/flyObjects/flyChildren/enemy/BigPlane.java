package flyObjects.flyChildren.enemy;

import flyObjects.flyChildren.enemy.beat.Score;
import javafx.scene.image.Image;
import flyObjects.flyChildren.enemy.beat.Award;
import util.Tool;

import java.util.Random;

/**
 * 大敌机
 */
public class BigPlane extends EnemyPlane implements Award, Score {
    private static Image[] images;
    private int award = new Random().nextInt(2);
    private int score = 20;

    static {
        images = new Image[2];
        images[0] = Tool.readImg("planeBig.png");
        images[1] = Tool.readImg("bombBig.png");
    }

    public BigPlane(int life, int bulletCount) {
        super(images[0]);
        setDifficulty(life,bulletCount);
        ySpeed = 2;
    }

    public void setImage() {
        setImage(images);
    }

    @Override
    public int getAward() {
        return award;
    }

    @Override
    public void setDifficulty(int life, int bulletCount) {
        this.life = life;
        this.bulletCount = bulletCount;
    }


    @Override
    public int getScore() {
        return score;
    }
}
