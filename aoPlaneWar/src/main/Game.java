package main;

import flyObjects.flyFather.FlyFather;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import flyObjects.flyChildren.enemy.beat.Award;
import flyObjects.flyChildren.enemy.beat.Score;
import flyObjects.flyChildren.enemy.*;
import flyObjects.flyChildren.hero.HeroBullet;
import flyObjects.flyChildren.hero.HeroPlane;
import util.Tool;

import java.util.*;

//flyObjects与javafx结合
public class Game extends Pane {
    public static final int WIDTH = 700;
    public static final int HEIGHT = 900;
    protected static Canvas canvas = new Canvas(Game.WIDTH, Game.HEIGHT);
    private static GraphicsContext gc = canvas.getGraphicsContext2D();
    static Image[] bgs = new Image[5];
    static Image[] bgsTurn = new Image[5];

    static {
        for (int i = 1; i <= 5; i++) {
            bgs[i - 1] = Tool.readImg("bg" + i + ".jpg");
            bgsTurn[i - 1] = Tool.readImg("bg" + i + "Turn.jpg");
        }
    }
    //预先创建图像对象
    private static Image background;
    private static Image backgroundTurn;
    private static Image start = Tool.readImg("startBg.jpg");
    private static Image pause = Tool.readImg("pause.png");
    private static Image startButton = Tool.readImg("startButton.png");
    private static Image rules = Tool.readImg("help.png");
    //预先创建声音对象
    static MediaPlayer shot_P = Tool.readMp3("heroShoot.mp3");
    static MediaPlayer smallShoot = Tool.readMp3("smallShoot.mp3");
    static MediaPlayer bigShoot = Tool.readMp3("bigShoot.mp3");
    static MediaPlayer bossShoot = Tool.readMp3("bossShoot.mp3");
    static MediaPlayer over_g = Tool.readMp3("gameOver.mp3");
    static MediaPlayer smallBom = Tool.readMp3("smallBomb.mp3");
    static MediaPlayer bigBom = Tool.readMp3("bigBomb.mp3");
    static MediaPlayer bossBom = Tool.readMp3("bossBomb.mp3");
    static MediaPlayer back = Tool.readMp3("background.mp3");
    static MediaPlayer hurt = Tool.readMp3("heroHurt.mp3"); // 用static定义只加载一次，某个音频在线程1播放时就不能同时在线程2播放
    protected static final int START = 0;//启动
    protected static final int RUNNING = 1;//运行
    protected static final int PAUSE = 2;//暂停
    protected static final int OVER = 3;//结束
    protected int state = START;//游戏的状态
    private List<Enemy> enemys = new ArrayList<>(); // 当前敌人列表
    private List<HeroBullet> hbs = new ArrayList<>(); // 当前英雄的子弹
    private HeroPlane heroPlane = new HeroPlane();
    int y1;
    int y2;
    int count; // 当前已运行毫秒数
    static Timer timer;
    int score;
    int temp; // 用于调整难度(每100分难度+1，temp存储上一关分数)
    int size = 30; // 当前信息的字体大小
    int leve; // 生成敌人的频率
    int difficulty = 1; // 当前难度等级
    boolean isHitHero;
    boolean introduce = false;
    protected PauseUi pauseUi = new PauseUi();
    protected boolean shutBgmusic = false;
    protected boolean shutBgSound = false;

    public Game() {
        this.getChildren().add(canvas);
        this.listener();
    }

    /**
     * 启动
     */
    public void start() {
        paintStart();
        init();
    }

    /**
     * 初始化资源
     */
    private void init() {
        heroPlane = new HeroPlane();
        hbs = new ArrayList<>();
        enemys = new ArrayList<>();
        score = 0;
        count = 0;
        temp = 0;
        leve = 180;
        difficulty=1;
        background = bgs[0];
        backgroundTurn = bgsTurn[0];
        y1 = 0;
        y2 = (int) -background.getHeight();
    }


    /**
     * 注册页面鼠标监听
     */
    private void listener() {
        canvas.setOnMouseClicked(event -> {
            // 点击的范围
            if (event.getX() >= 0 && event.getX() <= 700 && event.getY() >= 450 && event.getY() <= 900) {
                if (!introduce) {
                    gc.drawImage(bgs[4], 0, 0);
                    gc.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 50));
                    gc.setFill(Color.RED);
                    gc.fillText("游戏规则", 250, 50);
                    gc.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 25));
                    gc.setFill(Color.WHITE);
                    gc.fillText("小敌机获得5分", 0, 120);
                    gc.fillText("大敌机获得20分，以及生命值或火力值", 0, 180);
                    gc.fillText("BOSS机获得30分,以及生命值或火力值",0,240);
                    gc.fillText("分数（score）每增加100分难度（difficulty）增加1！", 0, 300);
                    gc.fillText("难度改变地图会切换，敌机出现的频率也会变高", 0, 360);
                    gc.fillText("武器等级上限为 2+difficulty，初始为2，角色受伤会减2",0,420);
                    gc.fillText("小敌机的生命值为difficulty，子弹数为1", 0, 480);
                    gc.fillText("大敌机的生命值为3+difficulty*2，子弹数为2+difficulty/3", 0, 540);
                    gc.fillText("Boss机的生命值为7+difficulty*3，子弹数为2+difficulty", 0, 600);
                    gc.fillText("角色受伤，三种敌机爆炸，分别有不同的音效", 0, 660);
                    gc.fillText("角色，三种敌机发射子弹，分别有不同的音效", 0, 720);
                    gc.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 50));
                    gc.fillText("（作者：ao666）", 0, 780);
                    gc.drawImage(startButton, 250, 800);
                    introduce = true;
                } else {
                    state = RUNNING;
                    //timer 为空说明游戏未启动或终止
                    if (timer == null) {
                        init();
                        timer();
                    }
                }
            } else {
                switch (state) {
                    case RUNNING:
                        if (timer == null) {
                            timer();
                        }
                        break;
                    case START:
                        timer();
                        state = RUNNING;
                        break;
                    case OVER:
                        init();
                        state = RUNNING;
                        if (timer == null) {
                            timer();
                        }
                        break;
                }
            }
        });
        canvas.setOnMouseMoved(event -> {
            if (state == RUNNING) { // 飞机跟着鼠标移动
                heroPlane.move((int) event.getX(), (int) event.getY());
            }
        });
        canvas.setOnMouseEntered(event -> {
            if (state == PAUSE) { // 鼠标移动进来了
                pauseUi.getBtContinue().setOnAction(event1 -> {
                    pauseUi.getStage().close();
                    state = RUNNING;
                });
                pauseUi.getStage().setOnCloseRequest(event1 -> {
                    state = RUNNING;
                });
                pauseUi.getBtExit().setOnAction(event1 -> {
                    pauseUi.getStage().close();
                    state = OVER;
                });
            }
        });
        canvas.setOnMouseExited(event -> {
            if (state == RUNNING) { // 鼠标移动出去了
                state = PAUSE;
                pauseUi.getStage().setResizable(false);
                pauseUi.getStage().setAlwaysOnTop(true);
                pauseUi.getStage().show();
                pauseUi.getRbShutBgMusic().setOnAction(event1 -> {
                    if(pauseUi.getRbShutBgMusic().isSelected()){
                        shutBgmusic = true;
                    }else{
                        shutBgmusic = false;
                    }
                });
                pauseUi.getRbShutBgSound().setOnAction(event1 -> {
                    if(pauseUi.getRbShutBgSound().isSelected()){
                        shutBgSound = true;
                    }else{
                        shutBgSound = false;
                    }
                });
            }
        });
    }

    /**
     * 关闭游戏关闭定时器和背景音乐
     */
    public void close() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (back != null && !shutBgmusic)
            back.stop();
    }

    /**
     * 每10ms更新数据并刷新javafx界面
     */
    private void timer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (state == RUNNING) { // 如果当前是运行状态
                    count++; // 每10*50ms发射一次子弹
                    checkScore();    //检测分数设定难度
                    if(!shutBgmusic) {
                        back.setCycleCount(count);
                    }
                    if(shutBgmusic){
                        back.pause();
                    }
                    if (count % 50 == 0) {  //通过取模控制速度
                        if(!shutBgSound) {
                            shot_P.play();
                            shot_P.seek(Duration.ZERO);
                        }
                        HeroShoot();
                    }
                    if (count % leve == 0) {
                        addEnemy(); //生成敌人频率
                    }
                    if (count % 200 == 0) {
                        enemyShoot(); //敌人发射子弹
                    }
                    if (count % 2 == 0) {
                        backgroundMove();//背景移动
                    }
                    if(count == 200){ // 防止count太大，200因为count取余最大只有200
                        count=0;
                    }
                    HeroBulletMove();//英雄机子弹移动
                    enemyMove();// 敌人移动
                    enemyHitHeroPlane();//检查敌人撞机
                    enemyHitHeroBullet();//检查敌人是否被击中
                    removeEnemy(); //删除敌人(线程互斥)
                    removeHeroBullet(); //删除英雄机子弹(线程互斥)
                    gameOverAction();   //检测游戏结束(调用)
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //更新JavaFX的主线程的代码
                        repaint();
                    }
                });
            }
        }, 10, 10);
    }

    /**
     * 检查当前场景的分数
     */
    private void checkScore() {
        if(score/100 > temp){
            temp = score/100;
            background = bgs[temp%5]; // 每100分切换场景
            backgroundTurn = bgsTurn[temp%5];
            difficulty++;
            if(leve > 50){ // 调整速度
                leve -= 10;
            }
        }
    }

    /**
     * 先改变state再通过timer刷新状态
     */
    private void gameOverAction() {
        if (heroPlane.getState() == FlyFather.DELETABLE) {
            state = OVER;
        }
    }

    /**
     * 判断当前场景里的子弹是否需要删除，这里判断的时候不能有其它线程操作子弹对象的集合
     */
    private synchronized void removeHeroBullet() {
        Iterator<HeroBullet> it = hbs.iterator();
        while (it.hasNext()) {
            HeroBullet heroBullet = it.next();
            if (heroBullet.getState() == FlyFather.DELETABLE || heroBullet.overflow()) {//英雄机子弹的状态是可删除或英雄机子弹越界
                it.remove();
            }
        }
    }

    /**
     * 判断当前场景里的敌人是否可删除，不能有其它线程操作敌人对象的集合
     */
    private synchronized void removeEnemy() {
        Iterator<Enemy> it = enemys.iterator();
        while (it.hasNext()) {
            Enemy enemy = it.next();
            if (enemy.getState() == FlyFather.DELETABLE || enemy.overFlow()) {//敌人的状态是可删除的或敌人越界
                it.remove();
            }
        }
    }

    /**
     * 根据state判断当前状态选择背景音乐是开启关闭还是暂停
     */
    private void paintState() {
        switch (state) {
            case RUNNING:
                paintScore(); // 运行状态一直更新分数等信息
                if(!shutBgmusic){
                    back.play();
                }
                break;
            case PAUSE:
                if(!shutBgmusic) {
                    back.pause();
                }
                break;
            case OVER:
                paintOver(); // 停止画出结束界面以及音效
                if(!shutBgmusic) {
                    back.stop();
                }
        }
    }

    /**
     * 结束时设置音效和界面
     */
    private void paintOver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!shutBgSound) {
                    over_g.play();
                    over_g.seek(Duration.ZERO);
                }
            }
        }).start();
        gc.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 60));
        gc.setFill(Color.BLUE);
        gc.fillText("游戏结束", 230,300);
        gc.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 30));
        gc.fillText("最终得分：" + score, 260, 400);
        gc.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 50));
        gc.setFill(Color.RED);
        gc.fillText("点击重新开始", 200, 550);

        timer.cancel(); // timer类停止定时工作
        timer = null;
    }

    /**
     * 画出开始界面
     */
    private void paintStart() {
        gc.drawImage(start, 0, 0);
        gc.drawImage(startButton, 280, 400);
        gc.drawImage(rules, 250, 500);
    }

    /**
     * 画出左上角的分数等信息
     */
    private void paintScore() {
        gc.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 30));
        gc.setFill(Color.WHITE);
        gc.fillText("分数：" + score, 10, 25);
        gc.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, size));
        gc.setFill(Color.YELLOW);
        gc.fillText("血量：" + heroPlane.getLife(), 10, 55);
        gc.fillText("武器等级:" + heroPlane.getFire() + "/" + (2+difficulty),10, 85);
        gc.setFont(Font.font("黑体", FontWeight.BOLD, FontPosture.REGULAR, 30));
        gc.setFill(Color.RED);
        gc.fillText("当前难度等级：" + difficulty,10,115);
        gc.fillText("小飞机血量：" + difficulty,10,145);
        gc.fillText("大飞机血量：" + (3+difficulty*2),10,175);
        gc.fillText("BOSS血量：" + (7+difficulty*3),10,205);
        gc.fillText("敌人生成频率："+leve*10 + "ms",10,235);
    }

    /**
     * 控制背景移动
     */
    private void backgroundMove() {
        y1++;
        y2++;
        if (y1 == 0) {
            y2 = (int) -background.getHeight();
            //turn = false;
        }
        if (y2 == 0) {
            y1 = (int) -background.getHeight();
            //turn = true;
        }
    }
    /*
     if(!turn) {
        gc.drawImage(background, 0, y1);
        gc.drawImage(background, 0, y2);
    }else{
        gc.drawImage(backgroundTurn, 0, y1);
        gc.drawImage(backgroundTurn, 0, y2);
    }*/

    /**
     * 更新场上的所有信息
     */
    private void repaint() {
        paintBackground();
        paintHeroBillet();
        paintHeroPlane();
        paintEnemy();
        paintState();
    }

    /**
     * 根据难度生成敌人
     */
    private synchronized void addEnemy() {
        EnemyPlane enemyPlane;
        int type = new Random().nextInt(6);
        if (type < 3) {
            enemyPlane = new SmallPlane(difficulty,1);
        } else if (type < 5) {
            enemyPlane = new BigPlane(3+difficulty*2,2+difficulty/3);
        } else {
            enemyPlane = new BossPlane(7+difficulty*3,difficulty+2);
        }
        enemys.add(enemyPlane);
    }

    /**
     * 画背景
     */
    private void paintBackground() {
            gc.drawImage(background, 0, y1-2);
            gc.drawImage(backgroundTurn, 0, y2-2);
    }

    /**
     * 画敌人
     */
    private synchronized void paintEnemy() {
        for (Enemy enemy : enemys) {
            enemy.setImage();
            Image image = enemy.getImage();
            if (image != null) {
                gc.drawImage(image, enemy.getX() - image.getWidth() / 2, enemy.getY() - image.getHeight() / 2);
            }
        }
    }

    /**
     * 画敌人的子弹
     */
    private synchronized void enemyShoot() {
        List<EnemyBullet> ebt = new ArrayList<>();
        for (Enemy enemy : enemys) {
            if (enemy instanceof EnemyPlane && enemy.getState() == FlyFather.ALIVE) {
                EnemyBullet[] enemyBullets = ((EnemyPlane) enemy).shootBullet();
                ebt.addAll(Arrays.asList(enemyBullets));
            }
            // 根据敌人类型决定音效
            if(enemy instanceof SmallPlane){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(!shutBgSound) {
                            smallShoot.play();
                            smallShoot.seek(Duration.ZERO);
                        }
                    }
                }).start();
            }
            if(enemy instanceof BossPlane){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(!shutBgSound) {
                            bossShoot.play();
                            bossShoot.seek(Duration.ZERO);
                        }
                    }
                }).start();
            }
            if(enemy instanceof BigPlane){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(!shutBgSound) {
                            bigShoot.play();
                            bigShoot.seek(Duration.ZERO);
                        }
                    }
                }).start();
            }
        }
        enemys.addAll(ebt);
    }

    /**
     * 敌人移动
     */
    private synchronized void enemyMove() {
        for (Enemy enemy : enemys) {
            if (enemy.getState() == FlyFather.ALIVE) {
                enemy.move();
            }
        }
    }

    /**
     * 敌人子弹移动
     */
    private synchronized void HeroBulletMove() {
        for (HeroBullet heroBullet : hbs) {
            if (heroBullet.getState() == FlyFather.ALIVE) {
                heroBullet.move();
            }
        }
    }

    /**
     * 英雄机发射子弹
     */
    private synchronized void HeroShoot() {
        if (heroPlane.getState() == FlyFather.ALIVE) {
            HeroBullet[] heroBullets = heroPlane.shootBullet(difficulty); // 根据难度确定子弹数量
            synchronized (heroPlane) {
                hbs.addAll(Arrays.asList(heroBullets));
            }
        }
    }

    /**
     * 画英雄机子弹
     */
    private synchronized void paintHeroBillet() {
        for (HeroBullet heroBullet : hbs) {
            heroBullet.setImage();
            Image image = heroBullet.getImage();
            if (image != null) {
                gc.drawImage(image, heroBullet.getX() - image.getWidth() / 2, heroBullet.getY() - image.getHeight() / 2);
            }
        }
    }

    int num = 0;
    /**
     * 英雄机被击中切换图片
     */
    private void paintHeroPlane() {
        if (isHitHero) {
            if (num == 5) {
                isHitHero = false;
                num = 0;
            }
            heroPlane.changeImage();
            num++;
        } else {
            heroPlane.setImage();
        }
        Image image = heroPlane.getImage();
        if (image != null) {
            gc.drawImage(image, heroPlane.getX() - image.getWidth() / 2, heroPlane.getY() - image.getHeight() / 2);
        }
    }

    /**
     * 英雄机与子弹或敌机碰撞
     */
    private synchronized void enemyHitHeroPlane() {
        for (Enemy enemy : enemys) {
            if (enemy.getState() == FlyFather.ALIVE        //敌人是活着的
                    &&
                    heroPlane.getState() == FlyFather.ALIVE        //英雄机是活着的
                    &&
                    enemy.touchPlane(heroPlane)) {                //撞上了
                heroPlane.subLife();
                heroPlane.ClearFire();
                waringLife();
                if (heroPlane.getLife() <= 0) {
                    heroPlane.setState(FlyFather.DEAD);
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(!shutBgSound) {
                                hurt.play();
                                hurt.seek(Duration.ZERO);
                            }
                        }
                    }).start();
                }
                tackActionForEnemy(enemy);
            }
        }
    }

    /**
     * 血量减少时游戏机左上角信息闪烁
     */
    private void waringLife() {
        isHitHero = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    size = 40;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    size = 20;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                size = 30;
            }
        }).start();
    }

    /**
     * 敌机或敌方子弹被摧毁获得奖励并开线程播放相关音频
     */
    private void tackActionForEnemy(Enemy enemy){
        if (enemy instanceof EnemyPlane) { // 如果是敌机，否则为敌机子弹
            EnemyPlane enemyPlane = (EnemyPlane) enemy;
            enemyPlane.subLife();
            if (enemyPlane.getLife() <= 0) { // 当生命值小于0
                enemyPlane.setState(FlyFather.DEAD);
                heroGetScore(enemyPlane); // 获得分数
                // 如果是Boss敌机
                if (enemyPlane instanceof BossPlane) {
                    heroGetAward(enemyPlane); // 获得奖励
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(!shutBgSound) {
                                bossBom.play();
                                bossBom.seek(Duration.ZERO);
                            }
                        }
                    }).start();
                }
                //如果是big机
                if (enemyPlane instanceof BigPlane){ //&& heroPlane.getState() == FlyingObject.ALIVE) {
                    heroGetAward(enemyPlane); // 获得奖励
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(!shutBgSound) {
                                if(!shutBgSound) {
                                    bigBom.play();
                                    bigBom.seek(Duration.ZERO);
                                }
                            }
                        }
                    }).start();
                }
                // 如果是小飞机
                if (enemyPlane instanceof SmallPlane){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(!shutBgSound) {
                                smallBom.play();
                                smallBom.seek(Duration.ZERO);
                            }
                        }
                    }).start();
                }
            }
        } else { // 子弹
            enemy.setState(FlyFather.DEAD);
        }
    }


    /**
     * 攻击或生命奖励
     */
    private void heroGetAward(EnemyPlane enemyPlane){
        Award award = (Award) enemyPlane;
        int type = award.getAward();
        switch (type) {
            case Award.FIRE:
                heroPlane.addFire();
                break;
            case Award.LIFE:
                heroPlane.addLife();
        }
    }

    /**
     * 分数奖励
     */
    private void heroGetScore(EnemyPlane enemyPlane){
        Score score = (Score) enemyPlane;
        this.score += score.getScore();
        Button button = new Button();
    }

    /**
     * 敌人被子弹击中
     */
    public synchronized void enemyHitHeroBullet() {
        for (Enemy enemy : enemys) {
            for (HeroBullet heroBullet : hbs) {
                if (enemy.getState() == FlyFather.ALIVE        //敌人是活着的
                        &&
                        heroBullet.getState() == FlyFather.ALIVE        //英雄机子弹是活着的
                        &&
                        enemy.touchBullet(heroBullet)) {                //撞上了
                    heroBullet.setState(FlyFather.DEAD);
                    tackActionForEnemy(enemy);
                }
            }
        }
    }
}
