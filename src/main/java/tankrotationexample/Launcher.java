
package main.java.tankrotationexample;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;


public class Launcher extends JPanel {

    //to get the screen, we cut the large WORLD image based on SCREEN_WIDTH and SCREEN_HEIGHT
    private static final int SCREEN_HEIGHT = 672;
    private static final int SCREEN_WIDTH = 960;
    static final int WORLD_HEIGHT = 1920; // 1920/32 = 60 walls high
    static final int WORLD_WIDTH = 1536;  // 1536/32 = 48 walls wide
    private final int Tank1_spawn_x = 150;
    private final int Tank1_spawn_y = 95;
    private final int Tank1_spawn_angle = 0;
    private final int Tank2_spawn_x = 1350;
    private final int Tank2_spawn_y = 1800;
    private final int Tank2_spawn_angle = 180;
    private BufferedImage world;
    private Graphics2D buffer;
    private JFrame jf;
    private Tank t1;
    private Tank t2;
    private static BufferedImage tank_img;
    private static BufferedImage player1_win_img;
    private static BufferedImage player2_win_img;
    private static BufferedImage menu_img;
    private static BufferedImage help_img;
    private boolean player1_won = false;
    private boolean player2_won = false;

    private CollisionHandler CH;
    static private Menu m; //static because the menu doesn't change for different instances of GameWorld(at least in my implementation)

    public void setGame_objects(ArrayList<GameObjects> game_objects) {
        this.game_objects = game_objects;
    }

    void addGame_object(GameObjects obj) { //package private
        this.game_objects.add(obj);
    }

    private ArrayList<GameObjects> game_objects = new ArrayList<>();
    private int Player1_num_lives = 2;
    private int Player2_num_lives = 2;

    enum Game_State {
        menu, game, help, exit,
    }

    static Game_State state = Game_State.menu; //default state is the menu state

    public static void main(String[] args) {
        Thread x;
        main.java.tankrotationexample.Launcher trex = new main.java.tankrotationexample.Launcher();
        trex.CH = new CollisionHandler();
        trex.init();
        try {


            while (true) {

                trex.repaint();

                if (main.java.tankrotationexample.Launcher.state == Game_State.game) {

                    for (int i = 0; i < trex.game_objects.size(); i++) {
                        if (trex.game_objects.get(i) instanceof Bullet) {
                            if (((Bullet) trex.game_objects.get(i)).getIsInactive()) { // is inactive
                                trex.game_objects.remove(i);

                                i--;
                            } else {
                                trex.game_objects.get(i).update();
                            }
                        }
                        if (trex.game_objects.get(i) instanceof Tank) {
                            if (((Tank) trex.game_objects.get(i)).getHealth() == 0) { //tank is destroyed
                                if ((((Tank) trex.game_objects.get(i)).getTag()).equals("Tank1")) { //player 1 tank is destroyed
                                    if (trex.Player1_num_lives > 1) { //game continues (player 1 has remaining lives)
                                        trex.Player1_num_lives--;

                                        //respawning
                                        ((Tank) trex.game_objects.get(i)).setHealth(100);  //replenishing health to full health
                                        trex.game_objects.get(i).setX(trex.Tank1_spawn_x);
                                        trex.game_objects.get(i).setY(trex.Tank1_spawn_y);
                                        trex.game_objects.get(i).setAngle(trex.Tank1_spawn_angle);


                                    } else { // player 2 has won
                                        trex.Player1_num_lives = 0;
                                        trex.player2_won = true;
                                        break;
                                    }
                                }
                                if ((((Tank) trex.game_objects.get(i)).getTag()).equals("Tank2")) { //player 2 tank is destroyed
                                    if (trex.Player2_num_lives > 1) { //game continues (player 2 has remaining lives)
                                        trex.Player2_num_lives--;


                                        ((Tank) trex.game_objects.get(i)).setHealth(100);
                                        trex.game_objects.get(i).setX(trex.Tank2_spawn_x);
                                        trex.game_objects.get(i).setY(trex.Tank2_spawn_y);
                                        trex.game_objects.get(i).setAngle(trex.Tank2_spawn_angle);


                                    } else { //player 1 has won
                                        trex.Player2_num_lives = 0;
                                        trex.player1_won = true;
                                        break;
                                    }
                                }

                            }
                        }
                        if (((trex.game_objects.get(i) instanceof main.java.tankrotationexample.BreakableWall) && ((main.java.tankrotationexample.BreakableWall) trex.game_objects.get(i)).getHealth() == 0)) {
                            trex.game_objects.remove(i);
                        }
                    }

                    trex.game_objects = trex.CH.HandleCollisions(trex.game_objects);  //handling collisions


                    trex.t1.update();
                    trex.t2.update();

                    Thread.sleep(1000 / 144);
                } else if (state == Game_State.exit) { //exiting
                    trex.jf.dispose();
                    System.exit(0);
                }
            }
        } catch (InterruptedException ignored) {

        }

    }


    private void init() {
        this.jf = new JFrame("WORLD OF TANKS");
        this.world = new BufferedImage(main.java.tankrotationexample.Launcher.WORLD_WIDTH, Launcher.WORLD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        BufferedImage t1img = null, bullet_image, background_image, unbreakable_wall_img, breakable_wall_img, exp_img, large_explosion_img;
        BufferedImage t2img = null, bullet_image1, background_image1, unbreakable_wall_img1, breakable_wall_img1, exp_img1, large_explosion_img1; 

        try {
            t1img = ImageIO.read(getClass().getResource("/resources/tank1.png"));
            t2img = ImageIO.read(getClass().getResource("/resources/tank2.png"));

            unbreakable_wall_img = ImageIO.read(getClass().getResource("/resources/Wall2.gif"));
            SolidWall.set_unbreakable_wall_img(unbreakable_wall_img);
            
            unbreakable_wall_img1 = ImageIO.read(getClass().getResource("/resources/Wall2.gif"));
            SolidWall.set_unbreakable_wall_img(unbreakable_wall_img1); //done

            background_image = ImageIO.read(getClass().getResource("/resources/Background.bmp"));
            SolidWall.setBackground_img(background_image);
            
            background_image1 = ImageIO.read(getClass().getResource("/resources/Background.bmp"));
            SolidWall.setBackground_img(background_image1); //done

            breakable_wall_img = ImageIO.read(getClass().getResource("/resources/Wall1.gif"));
            main.java.tankrotationexample.BreakableWall.set_breakable_wall_img(breakable_wall_img);
            
            breakable_wall_img1 = ImageIO.read(getClass().getResource("/resources/Wall1.gif"));
            main.java.tankrotationexample.BreakableWall.set_breakable_wall_img(breakable_wall_img1); //done

            bullet_image = ImageIO.read(getClass().getResource("/resources/Weapon.gif"));
            Bullet.setBufferedImage(bullet_image); //setting the bullet image
            
            bullet_image1 = ImageIO.read(getClass().getResource("/resources/Weapon.gif"));
            Bullet.setBufferedImage(bullet_image1); //setting the bullet image done --

            exp_img = ImageIO.read(getClass().getResource("/resources/Explosion_small.gif"));
            Bullet.setExplosionImage(exp_img);
            
            exp_img1 = ImageIO.read(getClass().getResource("/resources/Explosion_small.gif"));
            Bullet.setExplosionImage(exp_img1); //done

            large_explosion_img = ImageIO.read(getClass().getResource("/resources/Explosion_large.gif"));
            Bullet.setBig_explosion_img(large_explosion_img);

            large_explosion_img1 = ImageIO.read(getClass().getResource("/resources/Explosion_large.gif"));
            Bullet.setBig_explosion_img(large_explosion_img1); //done

            main.java.tankrotationexample.Launcher.menu_img = ImageIO.read(getClass().getResource("/resources/Menu_img.PNG"));
            main.java.tankrotationexample.Launcher.help_img = ImageIO.read(getClass().getResource("/resources/help.png"));
            // GameWorld.menu_img = ImageIO.read(getClass().getResource("/resources/Menu_img.png"));

            main.java.tankrotationexample.Launcher.player1_win_img = ImageIO.read(getClass().getResource("/resources/player1_wins.png"));
            main.java.tankrotationexample.Launcher.player2_win_img = ImageIO.read(getClass().getResource("/resources/player2_wins.png"));

            PowerUp.setHealth_img(ImageIO.read(getClass().getResource("/health-shroom.png")));
            PowerUp.setSpeed_img(ImageIO.read(getClass().getResource("/resources/run.png")));


        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        //creating tanks
        t1 = new Tank(Tank1_spawn_x, Tank1_spawn_y, 0, 0, Tank1_spawn_angle, t1img);
        t1.setTag("Tank1");
        t2 = new Tank(Tank2_spawn_x, Tank2_spawn_y, 0, 0, Tank2_spawn_angle, t2img);
        t2.setTag("Tank2");

        m = new Menu(); //instantiating the menu

        //adding background
        for (int i = 0; i < WORLD_WIDTH; i = i + 320) {
            for (int j = 0; j < WORLD_HEIGHT; j = j + 240) {
                game_objects.add(new SolidWall(i, j, true)); // the true is denote it's a background(allows for linking proper images)
            }
        }

        int[] new_map_array = { //width is 48, height is 60
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 1,
                1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 2, 2, 2, 2, 1,
                1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 2, 2, 2, 1,
                1, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 0, 0, 2, 2, 1,
                1, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 2, 2, 2, 0, 0, 2, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 2, 2, 2, 2, 2, 0, 2, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 2, 2, 2, 2, 2, 0, 2, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 2, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 2, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 2, 1, 1, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 0, 1, 0, 0, 0, 2, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 2, 2, 0, 0, 0, 1, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 2, 0, 2, 2, 2, 2, 2, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 2, 0, 2, 2, 2, 2, 2, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 2, 0, 0, 2, 2, 2, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 1,
                1, 2, 2, 0, 0, 2, 2, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 1,
                1, 2, 2, 2, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1,
                1, 2, 2, 2, 2, 0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1,
                1, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 2, 2, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};


        int column = 0; //left to right(0->24)
        int entire_index = 0;

        for (int i = 0; i < 60; i++) { //loops up and down(entire horizontal row)

            for (int j = 0; j < 48; j++) {
                if (column == 60) { //resetting column
                    column = 0;
                }
                int temp_val = new_map_array[entire_index]; //holds value in array
                if (temp_val != 0) {
                    if (temp_val == 2) { // breakable wall
                        game_objects.add(new main.java.tankrotationexample.BreakableWall(j * 32, i * 32));
                    } else {
                        game_objects.add(new SolidWall(j * 32, i * 32, false));
                    }
                }
                column++;
                entire_index++;
            }
        }


        game_objects.add(t1); //adding tank 1
        t1.setGW(this);
        game_objects.add(t2); //adding tank 2
        t2.setGW(this);


        PowerUp power1 = new PowerUp(1320, 110, true, false); //making a health PowerUp
        PowerUp power2 = new PowerUp(180, 1800, true, false); //making a health PowerUp
        game_objects.add(power1);
        game_objects.add(power2);
        PowerUp power3 = new PowerUp(730, 852, false, true);  //making a speed PowerUp
        game_objects.add(power3);

        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_Q); //adding control
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);

        this.jf.setLayout(new BorderLayout());
        this.jf.add(this); //adding the GameWorld to the Jframe


        this.jf.addKeyListener(tc1);
        this.jf.addKeyListener(tc2);
        this.addMouseListener(new MouseReader());

        this.jf.setSize(main.java.tankrotationexample.Launcher.SCREEN_WIDTH + 20, main.java.tankrotationexample.Launcher.SCREEN_HEIGHT + 40);
        this.jf.setResizable(false);
        jf.setLocationRelativeTo(null);

        this.jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jf.setVisible(true);


    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        buffer = world.createGraphics();
        super.paintComponent(g2);


        if (main.java.tankrotationexample.Launcher.state == Game_State.menu) {       //menu state
            (g).drawImage(menu_img, 0, 0, SCREEN_WIDTH + 2, SCREEN_HEIGHT, null);
            m.drawImage(g);
        } else if (main.java.tankrotationexample.Launcher.state == Game_State.help) {  //help state
            (g).drawImage(help_img, 0, 0, SCREEN_WIDTH + 2, SCREEN_HEIGHT, null);
        } else if (main.java.tankrotationexample.Launcher.state == Game_State.game) {  //game state


            for (int i = 0; i < game_objects.size(); i++) {

                game_objects.get(i).drawImage(buffer);

            }

            //these 4 variables are not required per say, but make reading the code sections below easier
            int player1_x_Coord = t1.getX();
            int player2_x_Coord = t2.getX();
            int player1_y_Coord = t1.getY();
            int player2_y_Coord = t2.getY();


            if (player1_x_Coord < SCREEN_WIDTH / 4) {
                player1_x_Coord = SCREEN_WIDTH / 4;
            }
            if (player2_x_Coord < SCREEN_WIDTH / 4) {
                player2_x_Coord = SCREEN_WIDTH / 4;
            }
            if (player1_x_Coord > WORLD_WIDTH - SCREEN_WIDTH / 4) {
                player1_x_Coord = WORLD_WIDTH - SCREEN_WIDTH / 4;
            }
            if (player2_x_Coord > WORLD_WIDTH - SCREEN_WIDTH / 4) {
                player2_x_Coord = WORLD_WIDTH - SCREEN_WIDTH / 4;
            }
            if (player1_y_Coord < SCREEN_HEIGHT / 2) {
                player1_y_Coord = SCREEN_HEIGHT / 2;
            }
            if (player2_y_Coord < SCREEN_HEIGHT / 2) {
                player2_y_Coord = SCREEN_HEIGHT / 2;
            }
            if (player1_y_Coord > WORLD_HEIGHT - SCREEN_HEIGHT / 2) {
                player1_y_Coord = WORLD_HEIGHT - SCREEN_HEIGHT / 2;
            }
            if (player2_y_Coord > WORLD_HEIGHT - SCREEN_HEIGHT / 2) {
                player2_y_Coord = WORLD_HEIGHT - SCREEN_HEIGHT / 2;
            }


            BufferedImage left_split_screen = world.getSubimage(player1_x_Coord - SCREEN_WIDTH / 4, player1_y_Coord - SCREEN_HEIGHT / 2, SCREEN_WIDTH / 2, SCREEN_HEIGHT);
            BufferedImage right_split_screen = world.getSubimage(player2_x_Coord - SCREEN_WIDTH / 4, player2_y_Coord - SCREEN_HEIGHT / 2, SCREEN_WIDTH / 2, SCREEN_HEIGHT);

            g2.drawImage(left_split_screen, 0, 0, null);
            g2.drawImage(right_split_screen, SCREEN_WIDTH / 2 + 5, 0, null); //the +5 is to have a gap between the split screens

            g2.drawImage(world, SCREEN_WIDTH / 2 - main.java.tankrotationexample.Launcher.WORLD_WIDTH / 6 / 2, SCREEN_HEIGHT - main.java.tankrotationexample.Launcher.WORLD_HEIGHT / 6, main.java.tankrotationexample.Launcher.WORLD_WIDTH / 6, WORLD_HEIGHT / 6, null);
            g2.setFont(new Font("Verdana", Font.BOLD, 30));
            g2.setColor(Color.yellow);
            g2.drawString("LIVES: " + this.Player1_num_lives, 15, 28);
            g2.drawString("LIVES: " + this.Player2_num_lives, SCREEN_WIDTH / 2 + 10, 28);
            g2.setColor(Color.red);
            g2.fillRect(180, 8, 2 * t1.getHealth(), 20);
            g2.fillRect(SCREEN_WIDTH / 2 + 180, 8, 2 * t2.getHealth(), 20);


            if (player1_won) {
                g2.drawImage(player1_win_img, 0, 0, SCREEN_WIDTH + 10, SCREEN_HEIGHT, null);
            }
            if (player2_won) {
                g2.drawImage(player2_win_img, 0, 0, SCREEN_WIDTH + 10, SCREEN_HEIGHT, null);
            }

        }
    }

}
