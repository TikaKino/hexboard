package pcore_gstrat;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

//See http://www.redblobgames.com/grids/hexagons/

public class SlickHelloWorld extends BasicGame {
	
	public SlickHelloWorld(String gname)
	{
		super(gname);
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.drawString("Hello World", 10, 100);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
	}
	
	public static void main (String[] args) {
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new SlickHelloWorld("Hello World"));
			appgc.setDisplayMode(800, 600, false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			System.out.println("Failed! "+ex.getMessage());
		}
	}
}
