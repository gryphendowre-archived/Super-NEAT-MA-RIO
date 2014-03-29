package com.mojang.mario;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import com.mojang.mario.sprites.*;
import com.mojang.sonar.FixedSoundSource;
import com.mojang.mario.level.*;


public class LevelScene extends Scene implements SpriteContext
{
    private List<Sprite> sprites = new ArrayList<Sprite>();
    private List<Sprite> spritesToAdd = new ArrayList<Sprite>();
    private List<Sprite> spritesToRemove = new ArrayList<Sprite>();

    public Level level;
    public Mario mario;
    public float xCam, yCam, xCamO, yCamO;
    public static Image tmpImage;
    private int tick;

    private LevelRenderer layer;
    private BgRenderer[] bgLayer = new BgRenderer[2];

    private GraphicsConfiguration graphicsConfiguration;

    public boolean paused = false;
    public int startTime = 0;
    private int timeLeft;

    //    private Recorder recorder = new Recorder();
    //    private Replayer replayer = null;
    
    private long levelSeed;
    private MarioComponent renderer;
    private int levelType;
    private int levelDifficulty;
    
    
    public List<BigDecimal> distToRedKoopa;
    
    public LevelScene(GraphicsConfiguration graphicsConfiguration, MarioComponent renderer, long seed, int levelDifficulty, int type)
    {
        this.graphicsConfiguration = graphicsConfiguration;
        this.levelSeed = seed;
        this.renderer = renderer;
        this.levelDifficulty = levelDifficulty;
        this.levelType = type;
    }

    public void init()
    {
        try
        {
            Level.loadBehaviors(new DataInputStream(LevelScene.class.getResourceAsStream("/tiles.dat")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        /*        if (replayer!=null)
         {
         level = LevelGenerator.createLevel(2048, 15, replayer.nextLong());
         }
         else
         {*/
//        level = LevelGenerator.createLevel(320, 15, levelSeed);
        
        level = LevelGenerator.createLevel(320, 15, levelSeed, levelDifficulty, levelType);
        //        }

        /*        if (recorder != null)
         {
         recorder.addLong(LevelGenerator.lastSeed);
         }*/

        if (levelType==LevelGenerator.TYPE_OVERGROUND)
            Art.startMusic(1);
        else if (levelType==LevelGenerator.TYPE_UNDERGROUND)
            Art.startMusic(2);
        else if (levelType==LevelGenerator.TYPE_CASTLE)
            Art.startMusic(3);
        
        distToRedKoopa = new ArrayList<BigDecimal>(); 
        paused = false;
        Sprite.spriteContext = this;
        sprites.clear();
        layer = new LevelRenderer(level, graphicsConfiguration, 320, 240);
        for (int i = 0; i < 2; i++)
        {
            int scrollSpeed = 4 >> i;
            int w = ((level.width * 16) - 320) / scrollSpeed + 320;
            int h = ((level.height * 16) - 240) / scrollSpeed + 240;
            Level bgLevel = BgLevelGenerator.createLevel(w / 32 + 1, h / 32 + 1, i == 0, levelType);
            bgLayer[i] = new BgRenderer(bgLevel, graphicsConfiguration, 320, 240, scrollSpeed);
        }
        mario = new Mario(this);
        sprites.add(mario);
        startTime = 1;
        
        timeLeft = 200*15;

        tick = 0;
    }

    public int fireballsOnScreen = 0;

    List<Shell> shellsToCheck = new ArrayList<Shell>();

    public void checkShellCollide(Shell shell)
    {
        shellsToCheck.add(shell);
    }

    List<Fireball> fireballsToCheck = new ArrayList<Fireball>();

    public void checkFireballCollide(Fireball fireball)
    {
        fireballsToCheck.add(fireball);
    }

    public void tick()
    {
        timeLeft--;
        if (timeLeft==0)
        {
            mario.die();
        }
        xCamO = xCam;
        yCamO = yCam;

        if (startTime > 0)
        {
            startTime++;
        }

        float targetXCam = mario.x - 160;

        xCam = targetXCam;

        if (xCam < 0) xCam = 0;
        if (xCam > level.width * 16 - 320) xCam = level.width * 16 - 320;
        
        /*      if (recorder != null)
         {
         recorder.addTick(mario.getKeyMask());
         }
         
         if (replayer!=null)
         {
         mario.setKeys(replayer.nextTick());
         }*/
        
        fireballsOnScreen = 0;

        for (Sprite sprite : sprites)
        {
            if (sprite != mario)
            {
                float xd = sprite.x - xCam;
                float yd = sprite.y - yCam;
                if (xd < -64 || xd > 320 + 64 || yd < -64 || yd > 240 + 64)
                {
                    removeSprite(sprite);
                }
                else
                {
                    if (sprite instanceof Fireball)
                    {
                        fireballsOnScreen++;
                    }
                }
            }
        }

        if (paused)
        {
            for (Sprite sprite : sprites)
            {
                if (sprite == mario)
                {
                    sprite.tick();
                }
                else
                {
                    sprite.tickNoMove();
                }
            }
        }
        else
        {
            tick++;
            level.tick();

            boolean hasShotCannon = false;
            int xCannon = 0;

            for (int x = (int) xCam / 16 - 1; x <= (int) (xCam + layer.width) / 16 + 1; x++)
                for (int y = (int) yCam / 16 - 1; y <= (int) (yCam + layer.height) / 16 + 1; y++)
                {
                    int dir = 0;

                    if (x * 16 + 8 > mario.x + 16) dir = -1;
                    if (x * 16 + 8 < mario.x - 16) dir = 1;

                    SpriteTemplate st = level.getSpriteTemplate(x, y);

                    if (st != null)
                    {
                        if (st.lastVisibleTick != tick - 1)
                        {
                            if (st.sprite == null || !sprites.contains(st.sprite))
                            {
                                st.spawn(this, x, y, dir);
                            }
                        }

                        st.lastVisibleTick = tick;
                    }

                    if (dir != 0)
                    {
                        byte b = level.getBlock(x, y);
                        if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_ANIMATED) > 0)
                        {
                            if ((b % 16) / 4 == 3 && b / 16 == 0)
                            {
                                if ((tick - x * 2) % 100 == 0)
                                {
                                    xCannon = x;
                                    for (int i = 0; i < 8; i++)
                                    {
                                        addSprite(new Sparkle(x * 16 + 8, y * 16 + (int) (Math.random() * 16), (float) Math.random() * dir, 0, 0, 1, 5));
                                    }
                                    addSprite(new BulletBill(this, x * 16 + 8 + dir * 8, y * 16 + 15, dir));
                                    hasShotCannon = true;
                                }
                            }
                        }
                    }
                }

            if (hasShotCannon)
            {
                sound.play(Art.samples[Art.SAMPLE_CANNON_FIRE], new FixedSoundSource(xCannon * 16, yCam + 120), 1, 1, 1);
            }

            for (Sprite sprite : sprites)
            {
                sprite.tick();
            }

            for (Sprite sprite : sprites)
            {
                sprite.collideCheck();
            }

            for (Shell shell : shellsToCheck)
            {
                for (Sprite sprite : sprites)
                {
                    if (sprite != shell && !shell.dead)
                    {
                        if (sprite.shellCollideCheck(shell))
                        {
                            if (mario.carried == shell && !shell.dead)
                            {
                                mario.carried = null;
                                shell.die();
                            }
                        }
                    }
                }
            }
            shellsToCheck.clear();

            for (Fireball fireball : fireballsToCheck)
            {
                for (Sprite sprite : sprites)
                {
                    if (sprite != fireball && !fireball.dead)
                    {
                        if (sprite.fireballCollideCheck(fireball))
                        {
                            fireball.die();
                        }
                    }
                }
            }
            fireballsToCheck.clear();
        }

        sprites.addAll(0, spritesToAdd);
        sprites.removeAll(spritesToRemove);
        spritesToAdd.clear();
        spritesToRemove.clear();
    }
    
    private DecimalFormat df = new DecimalFormat("00");
    private DecimalFormat df2 = new DecimalFormat("000");

    public void render(Graphics g, float alpha)
    {
    	//clear out all stimuli 
    	distToRedKoopa.clear();
    	
        int xCam = (int) (mario.xOld + (mario.x - mario.xOld) * alpha) - 160;
        int yCam = (int) (mario.yOld + (mario.y - mario.yOld) * alpha) - 120;
        //int xCam = (int) (xCamO + (this.xCam - xCamO) * alpha);
        //        int yCam = (int) (yCamO + (this.yCam - yCamO) * alpha);
        if (xCam < 0) xCam = 0;
        if (yCam < 0) yCam = 0;
        if (xCam > level.width * 16 - 320) xCam = level.width * 16 - 320;
        if (yCam > level.height * 16 - 240) yCam = level.height * 16 - 240;

        //      g.drawImage(Art.background, 0, 0, null);

        for (int i = 0; i < 2; i++)
        {
            bgLayer[i].setCam(xCam, yCam);
            bgLayer[i].render(g, tick, alpha);
        }

        g.translate(-xCam, -yCam);
        for (Sprite sprite : sprites)
        {
            if (sprite.layer == 0)
            { 
            	sprite.render(g, alpha);   
            }
            if(sprite instanceof Enemy || sprite instanceof BulletBill || sprite instanceof Shell || sprite instanceof FlowerEnemy)
            {
            	g.setColor(Color.RED);
            	g.drawLine((int)mario.x, (int)mario.y, (int)sprite.x, (int)sprite.y);
            	
            	
            	
            	if(sprite instanceof Enemy)
            	{
            		Enemy s = (Enemy)sprite; 
            		if(s.getType() == Enemy.ENEMY_GREEN_KOOPA || s.getType() == Enemy.ENEMY_RED_KOOPA)
            		{
            			if(s.getType() == Enemy.ENEMY_RED_KOOPA)
            			{
            				double dist = Math.pow(Math.pow((mario.x-sprite.x),2) + Math.pow(mario.y - sprite.y, 2), 0.5); 
                        	distToRedKoopa.add(new BigDecimal(dist));
            			}
            			
            			//top
                    	g.fillRect((int)sprite.x -(sprite.wPic/2), (int)sprite.y- (sprite.hPic), sprite.wPic, 2);
                    	//bottom
                    	g.fillRect((int)sprite.x -(sprite.wPic/2), (int)sprite.y, sprite.wPic, 2);
            			//left
                    	g.fillRect((int)sprite.x -(sprite.wPic/2), (int)sprite.y- (sprite.hPic), 2, sprite.hPic);
                    	//right
                    	g.fillRect((int)sprite.x +(sprite.wPic/2), (int)sprite.y- (sprite.hPic), 2, sprite.hPic);
            		}
            		else
            		{
            			//top
                    	g.fillRect((int)sprite.x -(sprite.wPic/2), (int)sprite.y- (sprite.hPic/2), sprite.wPic, 2);
                    	//bottom
                    	g.fillRect((int)sprite.x -(sprite.wPic/2), (int)sprite.y, sprite.wPic, 2);
            			//left side
                    	g.fillRect((int)sprite.x-(sprite.wPic/2), (int)sprite.y- (sprite.hPic/2), 2, sprite.hPic/2);
                    	//right
                    	g.fillRect((int)sprite.x+(sprite.wPic/2), (int)sprite.y- (sprite.hPic/2), 2, sprite.hPic/2);
            		}
            		
            	}
            	
            	else if(sprite instanceof FlowerEnemy)
            	{
            		FlowerEnemy s = (FlowerEnemy)sprite; 
            		
            		//top
                	g.fillRect((int)sprite.x -(sprite.wPic/2), (int)sprite.y- (sprite.hPic), sprite.wPic, 2);
                	//bottom
                	g.fillRect((int)sprite.x -(sprite.wPic/2), (int)sprite.y, sprite.wPic, 2);
        			//left
                	g.fillRect((int)sprite.x -(sprite.wPic/2), (int)sprite.y- (sprite.hPic), 2, sprite.hPic);
                	//right
                	g.fillRect((int)sprite.x +(sprite.wPic/2), (int)sprite.y- (sprite.hPic), 2, sprite.hPic);
            	}
            	else
            	{
            		//top
                	g.fillRect((int)sprite.x -(sprite.wPic/2), (int)sprite.y- (sprite.hPic/2), sprite.wPic, 2);
                	//bottom
                	g.fillRect((int)sprite.x -(sprite.wPic/2), (int)sprite.y, sprite.wPic, 2);
        			//left side
                	g.fillRect((int)sprite.x-(sprite.wPic/2), (int)sprite.y- (sprite.hPic/2), 2, sprite.hPic/2);
                	//right
                	g.fillRect((int)sprite.x+(sprite.wPic/2), (int)sprite.y- (sprite.hPic/2), 2, sprite.hPic/2);
            	}
            	
            }
            else if(sprite instanceof Mushroom || sprite instanceof FireFlower)
            {
            	g.setColor(Color.GREEN);
            	g.drawLine((int)mario.x, (int)mario.y, (int)sprite.x, (int)sprite.y);
            	//top
            	g.fillRect((int)sprite.x -(sprite.wPic/2), (int)sprite.y- (sprite.hPic), sprite.wPic, 2);
            	//bottom
            	g.fillRect((int)sprite.x -(sprite.wPic/2), (int)sprite.y, sprite.wPic, 2);
    			//left side
            	g.fillRect((int)sprite.x-(sprite.wPic/2), (int)sprite.y- (sprite.hPic/2), 2, sprite.hPic/2);
            	//right
            	g.fillRect((int)sprite.x+(sprite.wPic/2), (int)sprite.y- (sprite.hPic/2), 2, sprite.hPic/2);
            }
            else if (sprite instanceof Fireball)
            {
            	g.setColor(Color.ORANGE);
            	g.drawLine((int)mario.x, (int)mario.y, (int)sprite.x, (int)sprite.y);
            }
            
        }

        
        
        g.translate(xCam, yCam);

        layer.setCam(xCam, yCam);
        layer.render(g, tick, paused?0:alpha);
        
        boolean colYBoxExist; 
        //Renders lines between mario and items of note
        for (int x = xCam / 16; x <= (xCam + layer.width) / 16; x++)
        {
        	colYBoxExist = false; 
            for (int y = yCam / 16; y <= (yCam + layer.height) / 16; y++)
            {
                byte b = level.getBlock(x, y);

                if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_ANIMATED) > 0)
                {
                    int animTime = (tick / 3) % 4;

                    if ((b % 16) / 4 == 0 && b / 16 == 1)
                    {
                        animTime = (tick / 2 + (x + y) / 8) % 20;
                        if (animTime > 3) animTime = 0;
                    }
                    if ((b % 16) / 4 == 3 && b / 16 == 0)
                    {
                        animTime = 2;
                    }
                    int yo = 0;
                    if (x >= 0 && y >= 0 && x < level.width && y < level.height) yo = level.data[x][y];
                    if (yo > 0) yo = (int) (Math.sin((yo - alpha) / 4.0f * Math.PI) * 8);
                }
                
                    if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_SPECIAL) > 0)
                    {
                        g.setColor(Color.PINK);
                        //g.fillRect((x << 4) - xCam + 2 + 4, (y << 4) - yCam + 2 + 4, 4, 4);
                        g.drawLine((int)mario.x -xCam, (int)mario.y - yCam, (x << 4) - xCam + 2 + 4, (y << 4) - yCam + 2 + 4);
                    }
                    if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_BUMPABLE) > 0)
                    {
                        g.setColor(Color.BLUE);
                        //g.fillRect((x << 4) - xCam + 2, (y << 4) - yCam + 2, 4, 4);
                        g.drawLine((int)mario.x -xCam, (int)mario.y - yCam, (x << 4) - xCam + 2, (y << 4) - yCam + 2);
                    }
                    if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_BREAKABLE) > 0)
                    {
                        //g.setColor(Color.GREEN);
                        //g.fillRect((x << 4) - xCam + 2 + 4, (y << 4) - yCam + 2, 4, 4);
                    }
                    if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_PICKUPABLE) > 0)
                    {
                        g.setColor(Color.YELLOW);
                    	g.drawLine((int)mario.x -xCam, (int)mario.y - yCam, ((x << 4) - xCam + 2), ((y << 4) - yCam + 2 + 4));
                    }
                    if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_ANIMATED) > 0)
                    {
                    }
                    if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_BLOCK_ALL) > 0)
                	{
                        colYBoxExist = true; 
                	}
                    if( y > (yCam +layer.height/1.1) / 16)
                    {                    	
                    	if(!colYBoxExist)
                    	{
                    		g.setColor(Color.BLACK);
	                        g.fillRect((x << 4) - xCam, (y << 4) - yCam, 16, 2);
	                        g.fillRect((x << 4) - xCam, (y << 4) - yCam + 14, 16, 2);
	                        g.fillRect((x << 4) - xCam, (y << 4) - yCam, 2, 16);
	                        g.fillRect((x << 4) - xCam + 14, (y << 4) - yCam, 2, 16);  
	                        
                    	}
                    }
                }
        }
        
        
        layer.renderExit0(g, tick, paused?0:alpha, mario.winTime==0);
        
        g.translate(-xCam, -yCam);
        for (Sprite sprite : sprites)
        {
            if (sprite.layer == 1) sprite.render(g, alpha);
        }
        g.translate(xCam, yCam);
        g.setColor(Color.BLACK);
        layer.renderExit1(g, tick, paused?0:alpha);
        
        drawStringDropShadow(g, "MARIO " + df.format(Mario.lives), 0, 0, 7);
        drawStringDropShadow(g, "00000000", 0, 1, 7);
        
        drawStringDropShadow(g, "COIN", 14, 0, 7);
        drawStringDropShadow(g, " "+df.format(Mario.coins), 14, 1, 7);

        drawStringDropShadow(g, "WORLD", 24, 0, 7);
        drawStringDropShadow(g, " "+Mario.levelString, 24, 1, 7);

        drawStringDropShadow(g, "TIME", 35, 0, 7);
        int time = (timeLeft+15-1)/15;
        if (time<0) time = 0;
        drawStringDropShadow(g, " "+df2.format(time), 35, 1, 7);


        if (startTime > 0)
        {
            float t = startTime + alpha - 2;
            t = t * t * 0.6f;
            renderBlackout(g, 160, 120, (int) (t));
        }
//        mario.x>level.xExit*16
        if (mario.winTime > 0)
        {
            float t = mario.winTime + alpha;
            t = t * t * 0.2f;

            if (t > 900)
            {
                renderer.levelWon();
                //              replayer = new Replayer(recorder.getBytes());
//                init();
            }

            renderBlackout(g, (int) (mario.xDeathPos - xCam), (int) (mario.yDeathPos - yCam), (int) (320 - t));
        }

        if (mario.deathTime > 0)
        {
            float t = mario.deathTime + alpha;
            t = t * t * 0.4f;

            if (t > 1800)
            {
                renderer.levelFailed();
                //              replayer = new Replayer(recorder.getBytes());
//                init();
            }

            renderBlackout(g, (int) (mario.xDeathPos - xCam), (int) (mario.yDeathPos - yCam), (int) (320 - t));
        }
    }

    private void drawStringDropShadow(Graphics g, String text, int x, int y, int c)
    {
        drawString(g, text, x*8+5, y*8+5, 0);
        drawString(g, text, x*8+4, y*8+4, c);
    }
    
    private void drawString(Graphics g, String text, int x, int y, int c)
    {
        char[] ch = text.toCharArray();
        for (int i = 0; i < ch.length; i++)
        {
            g.drawImage(Art.font[ch[i] - 32][c], x + i * 8, y, null);
        }
    }
    
    private void renderBlackout(Graphics g, int x, int y, int radius)
    {
        if (radius > 320) return;

        int[] xp = new int[20];
        int[] yp = new int[20];
        for (int i = 0; i < 16; i++)
        {
            xp[i] = x + (int) (Math.cos(i * Math.PI / 15) * radius);
            yp[i] = y + (int) (Math.sin(i * Math.PI / 15) * radius);
        }
        xp[16] = 320;
        yp[16] = y;
        xp[17] = 320;
        yp[17] = 240;
        xp[18] = 0;
        yp[18] = 240;
        xp[19] = 0;
        yp[19] = y;
        g.fillPolygon(xp, yp, xp.length);

        for (int i = 0; i < 16; i++)
        {
            xp[i] = x - (int) (Math.cos(i * Math.PI / 15) * radius);
            yp[i] = y - (int) (Math.sin(i * Math.PI / 15) * radius);
        }
        xp[16] = 320;
        yp[16] = y;
        xp[17] = 320;
        yp[17] = 0;
        xp[18] = 0;
        yp[18] = 0;
        xp[19] = 0;
        yp[19] = y;

        g.fillPolygon(xp, yp, xp.length);
    }


    public void addSprite(Sprite sprite)
    {
        spritesToAdd.add(sprite);
        sprite.tick();
    }

    public void removeSprite(Sprite sprite)
    {
        spritesToRemove.add(sprite);
    }

    public float getX(float alpha)
    {
        int xCam = (int) (mario.xOld + (mario.x - mario.xOld) * alpha) - 160;
        //        int yCam = (int) (mario.yOld + (mario.y - mario.yOld) * alpha) - 120;
        //int xCam = (int) (xCamO + (this.xCam - xCamO) * alpha);
        //        int yCam = (int) (yCamO + (this.yCam - yCamO) * alpha);
        if (xCam < 0) xCam = 0;
        //        if (yCam < 0) yCam = 0;
        //        if (yCam > 0) yCam = 0;
        return xCam + 160;
    }

    public float getY(float alpha)
    {
        return 0;
    }

    public void bump(int x, int y, boolean canBreakBricks)
    {
        byte block = level.getBlock(x, y);

        if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BUMPABLE) > 0)
        {
            bumpInto(x, y - 1);
            level.setBlock(x, y, (byte) 4);
            level.setBlockData(x, y, (byte) 4);

            if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_SPECIAL) > 0)
            {
                sound.play(Art.samples[Art.SAMPLE_ITEM_SPROUT], new FixedSoundSource(x * 16 + 8, y * 16 + 8), 1, 1, 1);
                if (!Mario.large)
                {
                    addSprite(new Mushroom(this, x * 16 + 8, y * 16 + 8));
                }
                else
                {
                    addSprite(new FireFlower(this, x * 16 + 8, y * 16 + 8));
                }
            }
            else
            {
            	System.out.println("Tile bumpable at " + x + "  " + y); 
                Mario.getCoin();
                sound.play(Art.samples[Art.SAMPLE_GET_COIN], new FixedSoundSource(x * 16 + 8, y * 16 + 8), 1, 1, 1);
                addSprite(new CoinAnim(x, y));
            }
        }

        if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BREAKABLE) > 0)
        {
            bumpInto(x, y - 1);
            if (canBreakBricks)
            {
                sound.play(Art.samples[Art.SAMPLE_BREAK_BLOCK], new FixedSoundSource(x * 16 + 8, y * 16 + 8), 1, 1, 1);
                level.setBlock(x, y, (byte) 0);
                for (int xx = 0; xx < 2; xx++)
                    for (int yy = 0; yy < 2; yy++)
                        addSprite(new Particle(x * 16 + xx * 8 + 4, y * 16 + yy * 8 + 4, (xx * 2 - 1) * 4, (yy * 2 - 1) * 4 - 8));
            }
            else
            {
                level.setBlockData(x, y, (byte) 4);
            }
        }
    }

    public void bumpInto(int x, int y)
    {
        byte block = level.getBlock(x, y);
        if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_PICKUPABLE) > 0)
        {
        	System.out.println("Pickup at " + x + "  " + y); 
            Mario.getCoin();
            sound.play(Art.samples[Art.SAMPLE_GET_COIN], new FixedSoundSource(x * 16 + 8, y * 16 + 8), 1, 1, 1);
            level.setBlock(x, y, (byte) 0);
            addSprite(new CoinAnim(x, y + 1));
        }

        for (Sprite sprite : sprites)
        {
            sprite.bumpCheck(x, y);
        }
    }
}