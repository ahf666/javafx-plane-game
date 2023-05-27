package flyObjects.flyChildren.enemy;

import javafx.scene.image.Image;
import flyObjects.flyChildren.enemy.beat.Award;
import flyObjects.flyChildren.enemy.beat.Score;
import util.Tool;

import java.util.Random;

/**
 * 轰炸机
 */
public class BossPlane extends EnemyPlane implements Award, Score {
    private static Image[] images;
    private int score = 30;//分数
    private int award = new Random().nextInt(2);
    private int xSpeed = (int) Math.pow(-1, new Random().nextInt(2)) * 1;

    static {
        images = new Image[2];
        images[0] = Tool.readImg("planeBoss.png");
        images[1] = Tool.readImg("bombBoss.png");
    }

    public BossPlane(int life, int bulletCount) {
        super(images[0]);
        ySpeed = 2;
        setDifficulty(life,bulletCount);
    }

    public void setImage() {
        setImage(images);
    }

    @Override
    public void move() {
        if (this.y < this.image.getHeight() / 2) {
            y += ySpeed;
        } else {
            x += xSpeed;
        }
    }

    @Override
    public int getAward() {
        return award;
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
