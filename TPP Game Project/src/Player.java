import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Controls player sprites and stats.
 */
public class Player extends Character{
    MouseInput mouseIn;
    int slowSpeed;
    int currentSpeed;

    KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    int hasKey = 0;
    public boolean attackCanceled = false;
    public Player(GamePanel gp, KeyHandler keyH, MouseInput mouseIn){
        super(gp);
        this.mouseIn = mouseIn;
        this.keyH=keyH;
        screenX = gp.screenWidth/2 - gp.tileSize/2;
        screenY = gp.screenHeight/2 - gp.tileSize/2;  //sets camera size

        solidArea = new Rectangle(16,16,32,36);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        attackArea = new Rectangle(0,0,36,36);

        setDefaultValues();

        getplayerImage();

        direction="down";

    }

    /**
     *  Sets Default values for player.
     */
    public void setDefaultValues(){
        worldX = gp.tileSize * 125;
        worldY = gp.tileSize * 125;
        speed = 10;
        slowSpeed = speed-6;
        currentSpeed = speed;
        //PLAYER STATUS
        maxLife = 10;
        life = maxLife;
        strength =1;
        dexterity =1;
        exp = 0;
        nextLevelExp= 5;
        coin= 0;
        currentWeapon = new OBJ_Sword_Normal(gp);
        currentShield = new OBJ_Shield_Wood(gp);
        projectile = new OBJ_Fireball(gp);
        attack= getAttack();
        defense = getDefense();
    }
    public void setDefaultPositions() {

        worldX = gp.tileSize * 125;
        worldY = gp.tileSize * 125;
        //direction?
    }
    public void resetoreLifeAndMan() {
        life = maxLife;
        mana = maxMana;
        invincible = false;
    }
    public int getAttack(){
        return attack;
    }
    public int getDefense(){
        return defense;
    }


    /**
     *  Gets player sprites from res directory.
     */
    public void getplayerImage(){
        StringBuilder sb = new StringBuilder();
        int i=2;
        String basePath = "TPP Game Project/res/player/super_mewtwo_";
        up1 = setup(sb.append(basePath).append("up_1").toString(),gp.tileSize,gp.tileSize*i);
        sb.setLength(0);
        up2 = setup(sb.append(basePath).append("up_2").toString(),gp.tileSize,gp.tileSize*i);
        sb.setLength(0);
        down1 = setup(sb.append(basePath).append("down_1").toString(),gp.tileSize,gp.tileSize*i);
        sb.setLength(0);
        down2 = setup(sb.append(basePath).append("down_2").toString(),gp.tileSize,gp.tileSize*i);
        sb.setLength(0);
        right1 = setup(sb.append(basePath).append("right_1").toString(),gp.tileSize,gp.tileSize*i);
        sb.setLength(0);
        right2 = setup(sb.append(basePath).append("right_2").toString(),gp.tileSize,gp.tileSize*i);
        sb.setLength(0);
        left1 = setup(sb.append(basePath).append("left_1").toString(),gp.tileSize,gp.tileSize*i);
        sb.setLength(0);
        left2 = setup(sb.append(basePath).append("left_2").toString(),gp.tileSize,gp.tileSize*i);
    }
    /**
         *  Updates the player data (60FPS).
     */
    public void update(){
        //System.out.println(attacking);
        boolean upPressed = keyH.upPressed;
        boolean leftPressed = keyH.leftPressed;
        boolean rightPressed = keyH.rightPressed;
        boolean downPressed = keyH.downPressed;
        boolean enterPressed = keyH.enterPressed;
        if (attacking == true){
            attacking();
            //System.out.println("attacking is true");
        } else if (upPressed||downPressed||
                leftPressed||rightPressed|| enterPressed) { //Checks all player input to see if moving. (so player animation doesn't move when player is still).
            //System.out.println("attacking is false");
            //Subtracts speed from x & y coordinates and sets direction for sprite.
            if (upPressed && leftPressed) {
                direction = "up-left";

            } else if (upPressed && rightPressed) {
                direction = "up-right";

            } else if (downPressed && leftPressed) {
                direction = "down-left";

            } else if (downPressed && rightPressed) {
                direction = "down-right";

            } else if (upPressed) {
                direction = "up";

            } else if (downPressed) {
                direction = "down";

            } else if (rightPressed) {
                direction = "right";

            } else if (leftPressed) {
                direction = "left";

            }
            // Check tile collision
            collisionOn = false;
            gp.cChecker.checkTile(this);

            // Check object collision
            int objIndex = gp.cChecker.checkObject(this,true);
            pickupObject(objIndex);

            // Check NPC collision
            int npcIndex = gp.cChecker.checkCharacter(this, gp.npc);
            interactNPC(npcIndex);

            // Check monster collision
            int monsterIndex = gp.cChecker.checkCharacter(this, gp.monster);
            contactMonster(monsterIndex);

            // Check event
            gp.eHandler.checkEvent();



            //if collision is false player can move
            if (collisionOn == false && keyH.enterPressed == false){

                switch (direction){
                    case "up": worldY -= speed;
                        break;
                    case "down": worldY += speed;
                        break;
                    case "left": worldX -= speed;
                        break;
                    case "right": worldX += speed;
                        break;
                    case "up-left": worldX -= speed;
                                    worldY -= speed;
                        break;
                    case "up-right": worldY -= speed;
                                     worldX += speed;
                        break;
                    case "down-left": worldY += speed;
                                      worldX -= speed;
                        break;
                    case "down-right": worldY += speed;
                                       worldX += speed;
                }
            }

//            if (keyH.enterPressed == true && attackCanceled== false){
//                gp.playSE(7);
//                attacking = true;
//                spriteCounter = 0;
//            }

            attackCanceled = false;
           gp.keyH.enterPressed = false;    //resets



            //Alternates sprite number so draw method can switch sprites for animation.

            spriteCounter++;    //every frame increases counter by 1
            if (spriteCounter > 10) {    //changes sprite every 10 frames
                if (spriteNum == 1) {
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        }

        if ( gp.mouseIn.mousePress == true && shotAvailableCounter == 10) {
            // SET DEFAULT COORDINATES, DIRECTION AND USER
            OBJ_Player_Projectile proj = new OBJ_Player_Projectile(gp,this);
            gp.player.speed = slowSpeed;
            proj.set(gp.player.worldX, gp.player.worldY, mouseIn.mx, mouseIn.my, "polar", true, this);

            // ADD IT TO THE LIST
            gp.projectileList.add(proj);
            shotAvailableCounter = 0;


            gp.playSE(8);
            mouseIn.mousePress=false;
        }else if (slowCounter == 15){
            speed = currentSpeed;
            slowCounter = 0;
        }

        if (invincible == true) {

            invincibleCounter++;
            if (invincibleCounter > 60) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
        if (shotAvailableCounter < 10) {
            shotAvailableCounter++;
        }
        if (slowCounter < 15) {
            slowCounter++;
        }
        if (keyH.godModeOn == false) {
            if (life <= 0) {
                gp.gameState = gp.gameOverState;
                gp.stopMusic();
                gp.playMusic(0);
                gp.stopMusic();
                gp.stopMusic();
                gp.playSE(9);

            }
        }

    }
    public void pickupObject(int i){
        if (i !=999){

            String objectName= gp.obj[gp.currentMap][i].name;
            switch(objectName){
                case"Key":
                    gp.playSE(1);//calls sound effect according to soundUrl array.
                    hasKey++;
                    gp.obj[gp.currentMap][i]=null;
                    gp.ui.showMessage("You got a key!");
                    break;
                case"Door":
                    if (hasKey>0){
                        gp.playSE(4);
                        gp.obj[gp.currentMap][i] = null;
                        hasKey--;
                        gp.ui.showMessage("You opened the door!");
                    }
                    else {
                        gp.ui.showMessage("You need a key!");
                    }
                    break;
                case "XSpeed":
                    gp.playSE(3);
                    currentSpeed += 5;        // if you make this 100 you weirdly can barely move.
                    gp.obj[gp.currentMap][i] = null;
                    gp.ui.showMessage("Speed up!");
                    break;
                case "Portal":
                    gp.ui.gameFinished = true;
                    gp.stopMusic();
                    gp.playSE(0);//needs work
                    break;
            }
        }
    }
    public void interactNPC(int i) {

        if (gp.keyH.enterPressed==true){
            if (i != 999) {
                attackCanceled = true;
                gp.gameState = gp.dialogueState;
                gp.npc[gp.currentMap][i].speak();

                }
            }
        }


    public void contactMonster(int i) {
        if (i != 999) {


            if (invincible == false) {
                gp.playSE(6);
                if (gp.monster[gp.currentMap][i].invincible == false) {
                    gp.playSE(5);

                    int damage = gp.monster[gp.currentMap][i].attack - defense;
                    if (damage < 0) {
                        damage = 0;
                    }
                    life -= damage;
                    invincible = true;
                }
            }
        }
    }

    public void damageMonster(int i, int attack){
        if (i !=999){

            if (gp.monster[gp.currentMap][i].invincible == false  || gp.monster[gp.currentMap][i].invincible == true){ //temp fix
                gp.playSE(5);

                int damage = attack - gp.monster[gp.currentMap][i].defense;
                if (damage < 0){
                    damage = 0;
                }

                gp.monster[gp.currentMap][i].life-=damage;
                gp.monster[gp.currentMap][i].invincible = true;
                gp.monster[gp.currentMap][i].damageReaction();
                if (gp.monster[gp.currentMap][i].life<=0 ){
                    gp.monster[gp.currentMap][i].dying = true;
                }
            }
        }
    }
    /**
     * Updates screen with player images (60FPS).
     */
    public void draw(Graphics2D graphics2) {
//        graphics2.setColor(Color.white);
//        graphics2.fillRect(x,y, /*width*/ gp.tileSize, /*height*/ gp.tileSize);   //Player Character

        //When direction switches: calls appropriate sprite & alternates between them for animation.

        BufferedImage image = null;
        int tempScreenX = screenX;
        int tempScreenY = screenY;
        switch (direction) {
            case "up":
                if (attacking == false) {
                    if (spriteNum == 1) {
                        image = up1;
                    }
                    if (spriteNum == 2) {
                        image = up2;
                    }
                }
                if (attacking == true) {
                    tempScreenY = screenY - gp.tileSize;//corrects displacement of wide sprite
                    if (spriteNum == 1) {

                        image = attackUp1;
                    }
                    if (spriteNum == 2) {
                        image = attackUp2;
                    }
                }
                break;
            case "down":
                if (attacking == false) {
                    if (spriteNum == 1) {
                        image = down1;
                    }
                    if (spriteNum == 2) {
                        image = down2;
                    }
                }
                if (attacking == true) {
                    if (spriteNum == 1) {
                        image = attackDown1;
                    }
                    if (spriteNum == 2) {
                        image = attackDown2;
                    }
                }
                break;
            case "right", "up-right", "down-right":
                if (attacking == false) {
                    if (spriteNum == 1) {
                        image = right1;
                    }
                    if (spriteNum == 2) {
                        image = right2;
                    }
                }
                if (attacking == true) {
                    if (spriteNum == 1) {
                        image = attackRight1;
                    }
                    if (spriteNum == 2) {
                        image = attackRight2;
                    }
                }

                break;
            case "left", "up-left", "down-left":
                if (attacking == false) {
                    if (spriteNum == 1) {
                        image = left1;
                    }
                    if (spriteNum == 2) {
                        image = left2;
                    }
                }
                if (attacking == true) {
                    tempScreenX = screenX - gp.tileSize;
                    if (spriteNum == 1) {
                        image = attackLeft1;
                    }
                    if (spriteNum == 2) {
                        image = attackLeft2;
                    }
                }
                break;
        }

        if (invincible == true) {
            graphics2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        }
        graphics2.drawImage(image, tempScreenX, tempScreenY, null);

        // Reset alpha
        graphics2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        //Changes image, puts it where it goes, changes how big it is.
       // graphics2.drawImage(image, screenX, screenY,null);
        //graphics2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
       // graphics2.drawRect(gp.Pathnode[col][row].col,node[col][row].row);


    }

}
