package flyObjects.flyChildren.enemy;

import javafx.scene.image.Image;
import main.Game;
import flyObjects.flyFather.FlyFather;
import flyObjects.flyChildren.hero.HeroBullet;
import flyObjects.flyChildren.hero.HeroPlane;

/**
 * 敌人类
 */
public abstract class Enemy extends FlyFather {
    int ySpeed;//y方向移动

    //敌机子弹
    public Enemy(Image image, int x, int y) {
        super(image, x, y);
    }

    //敌机
    public Enemy(Image image) {
        super(image);
    }

    public void move() {
        y += ySpeed;
    }

    public boolean overFlow() {
        if (this.y >= Game.HEIGHT + this.image.getHeight() / 2
                || this.x <= this.image.getWidth() / 2 ||
                this.x >= Game.WIDTH + this.image.getWidth() / 2) {
            return true;
        }
        return false;
    }

    public boolean touchPlane(HeroPlane heroPlane) {
        int x = this.x;
        int y = this.y;
        int m1 = (int) (heroPlane.getX() - heroPlane.getImage().getWidth() / 2 - this.image.getWidth() / 2);
        int m2 = (int) (heroPlane.getX() + heroPlane.getImage().getWidth() / 2 + this.image.getWidth() / 2);
        int n1 = (int) (heroPlane.getY() - heroPlane.getImage().getHeight() / 2 - this.image.getHeight() / 2);
        int n2 = (int) (heroPlane.getY() + heroPlane.getImage().getHeight() / 2 + this.image.getHeight() / 2);
        return x >= m1 && x <= m2 && y >= n1 && y <= n2;
    }

    public boolean touchBullet(HeroBullet heroBullet) {
        int x = this.x;
        int y = this.y;
        int m1 = (int) (heroBullet.getX() - heroBullet.getImage().getWidth() / 2 - this.image.getWidth() / 2);
        int m2 = (int) (heroBullet.getX() + heroBullet.getImage().getWidth() / 2 + this.image.getWidth() / 2);
        int n1 = (int) (heroBullet.getY() - heroBullet.getImage().getHeight() / 2 - this.image.getHeight() / 2);
        int n2 = (int) (heroBullet.getY() + heroBullet.getImage().getHeight() / 2 + this.image.getHeight() / 2);
        return x >= m1 && x <= m2 && y >= n1 && y <= n2;
    }
}
