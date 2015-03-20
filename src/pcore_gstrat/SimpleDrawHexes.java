package pcore_gstrat;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.MouseButtonControl;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

import hexgrid.*;
import hexgrid.coords.*;

public class SimpleDrawHexes extends BasicGame implements InputProviderListener {
	
	protected int hexSizePixels;
	protected int gridsize = 8;
	protected HexGrid hexgrid;
	
	protected Hex selected;
	protected ArrayList<Hex> pathToSelected;
	protected int lastMouseX;
	protected int lastMouseY;
	
	protected Input input;
	protected InputProvider inputProvider;
	protected int input_state;
	
	public static final int INPUT_STATE_BASIC = 0;
	public static final int INPUT_STATE_MCLICKDRAG = 1;
	
	private int viewportTLx;
	private int viewportTLy;
	private int viewportBRx;
	private int viewportBRy;
	
	private int viewoffsetx;
	private int viewoffsety;
	
	protected TrueTypeFont fontMainText;
	protected TrueTypeFont fontCoords;
	
	public SimpleDrawHexes()
	{
		super("Simple Draw Hexes");
		
		this.hexgrid = new HexGrid(gridsize,gridsize);
		for(int x = 0; x < gridsize; x++)
		{
			for(int y = 0; y < gridsize; y++)
			{
				AxialHexCoord ac = (new OddQOffsetHexCoord(x,y)).getAxial();
				Hex h = new Hex(ac);
				this.hexgrid.put(ac, h);
			}
		}
		
		this.selected = null;
		this.pathToSelected = null;
		this.lastMouseX = 0;
		this.lastMouseY = 0;
		
		this.viewportBRx = 1079;
		this.viewportBRy = 767;
		this.viewportTLx = 200;
		this.viewportTLy = 0;
	}
	
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		
		gc.setShowFPS(false);
		gc.setTargetFrameRate(60);
		
		this.input_state = INPUT_STATE_BASIC;
		this.input = gc.getInput();
		this.inputProvider = new InputProvider(this.input);
		this.inputProvider.addListener(this);
		
		this.hexSizePixels = 48;
		this.viewoffsetx = this.viewportTLx + 70;
		this.viewoffsety = 60;
		
		Command c;
		c = new BasicCommand("click");
		this.inputProvider.bindCommand(new MouseButtonControl(0), c);
		c = new BasicCommand("mclick");
		this.inputProvider.bindCommand(new MouseButtonControl(2), c);
		
		Font f;
		f = new Font("Verdana",Font.BOLD,16);
		this.fontMainText = new TrueTypeFont(f,false);
		f = new Font("Courier New",Font.PLAIN,12);
		this.fontCoords = new TrueTypeFont(f,false);
	}
	
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		
		//Rendering Grid here; clip to viewport.
		Rectangle clip = new Rectangle(this.viewportTLx,this.viewportTLy,this.viewportBRx - this.viewportTLx,this.viewportBRy - this.viewportTLy);
		g.setClip(clip);
		
		Set<AxialHexCoord> coords = this.hexgrid.keySet();
		Iterator<AxialHexCoord> it = coords.iterator();
		float oldw = g.getLineWidth();
		g.setLineWidth(2.0f);
		while(it.hasNext())
		{
			AxialHexCoord ac = it.next();
			//ArrayList<Integer> pixels = HexCoordUtils.hexToPixel(ac, this.hexSizePixels);
			Point fractionalHexCenter = HexCoordUtils.hexToFractional2D(ac);
			int px = (int)(fractionalHexCenter.getX() * (float)this.hexSizePixels);
			int py = (int)(fractionalHexCenter.getY() * (float)this.hexSizePixels);
			int hexh = (int)((float)Math.sqrt(3)/2 * (float)this.hexSizePixels);

			g.drawOval(px+this.viewoffsetx-2, py+this.viewoffsety-2, 4, 4);
			
			OddQOffsetHexCoord oq = ac.getOddQOffset();
			String out = "("+oq.x+","+oq.y+")";
			int width = this.fontCoords.getWidth(out) / 2;
			int height = this.fontCoords.getHeight(out);
			this.fontCoords.drawString((float)(px+this.viewoffsetx-width), (float)(py+this.viewoffsety+hexh-height), out, Color.gray);
			
			ArrayList<Point> cornerFracs = HexCoordUtils.hexCornersFractional2D(ac);
			for(int i = 0; i < 6; i++)
			{
				int j = i + 1;
				if(i == 5)
					j = 0;
				
				int sx = (int)(cornerFracs.get(i).getX() * this.hexSizePixels);
				int sy = (int)(cornerFracs.get(i).getY() * this.hexSizePixels);
				
				int ex = (int)(cornerFracs.get(j).getX() * this.hexSizePixels);
				int ey = (int)(cornerFracs.get(j).getY() * this.hexSizePixels);
				
				g.drawLine(sx + this.viewoffsetx,sy + this.viewoffsety,ex + this.viewoffsetx,ey + this.viewoffsety);
			}
		}
		g.setLineWidth(oldw);
		
		//Highlight selected hex, surrounds and path to start corner
		if(this.selected != null)
		{
			ArrayList<Hex> surrounds = this.hexgrid.getAdjacentHexes(this.selected);
			Iterator<Hex> surrit = surrounds.iterator();
			while(surrit.hasNext())
			{
				Hex s = surrit.next();
				AxialHexCoord ac = s.getCoords();
				ArrayList<Point> cornerFracs = HexCoordUtils.hexCornersFractional2D(ac);
				Color oldc = g.getColor();
				oldw = g.getLineWidth();
				g.setColor(Color.blue);
				g.setLineWidth(3.0f);
				for(int i = 0; i < 6; i++)
				{
					int j = i + 1;
					if(i == 5)
						j = 0;
					
					int sx = (int)(cornerFracs.get(i).getX() * this.hexSizePixels);
					int sy = (int)(cornerFracs.get(i).getY() * this.hexSizePixels);
					
					int ex = (int)(cornerFracs.get(j).getX() * this.hexSizePixels);
					int ey = (int)(cornerFracs.get(j).getY() * this.hexSizePixels);
					
					
					g.drawLine(sx + this.viewoffsetx,sy + this.viewoffsety,ex + this.viewoffsetx,ey + this.viewoffsety);
				}
				g.setColor(oldc);
				g.setLineWidth(oldw);
			}
			
			if(this.pathToSelected != null)
			{
				Iterator<Hex> path = this.pathToSelected.iterator();
				while(path.hasNext())
				{
					Hex s = path.next();
					AxialHexCoord ac = s.getCoords();
					ArrayList<Point> cornerFracs = HexCoordUtils.hexCornersFractional2D(ac);
					Color oldc = g.getColor();
					oldw = g.getLineWidth();
					g.setColor(Color.darkGray);
					g.setLineWidth(4.0f);
					for(int i = 0; i < 6; i++)
					{
						int j = i + 1;
						if(i == 5)
							j = 0;
						
						int sx = (int)(cornerFracs.get(i).getX() * this.hexSizePixels);
						int sy = (int)(cornerFracs.get(i).getY() * this.hexSizePixels);
						
						int ex = (int)(cornerFracs.get(j).getX() * this.hexSizePixels);
						int ey = (int)(cornerFracs.get(j).getY() * this.hexSizePixels);
						
						
						g.drawLine(sx + this.viewoffsetx,sy + this.viewoffsety,ex + this.viewoffsetx,ey + this.viewoffsety);
					}
					g.setColor(oldc);
					g.setLineWidth(oldw);
				}
			}
			
			AxialHexCoord ac = this.selected.getCoords();
			ArrayList<Point> cornerFracs = HexCoordUtils.hexCornersFractional2D(ac);
			Color oldc = g.getColor();
			oldw = g.getLineWidth();
			g.setColor(Color.cyan);
			g.setLineWidth(4.0f);
			for(int i = 0; i < 6; i++)
			{
				int j = i + 1;
				if(i == 5)
					j = 0;
				
				int sx = (int)(cornerFracs.get(i).getX() * this.hexSizePixels);
				int sy = (int)(cornerFracs.get(i).getY() * this.hexSizePixels);
				
				int ex = (int)(cornerFracs.get(j).getX() * this.hexSizePixels);
				int ey = (int)(cornerFracs.get(j).getY() * this.hexSizePixels);
				
				
				g.drawLine(sx + this.viewoffsetx,sy + this.viewoffsety,ex + this.viewoffsetx,ey + this.viewoffsety);
			}
			g.setColor(oldc);
			g.setLineWidth(oldw);
		}
		
		g.clearClip();
		
		//Render to left control area here
		g.setClip(new Rectangle(0,this.viewportTLy,this.viewportTLx,768-this.viewportTLy));
		if(this.selected != null)
		{
			Hex hex = this.selected;
			AxialHexCoord ac = hex.getCoords();
			OddQOffsetHexCoord oq = ac.getOddQOffset();
			String out = "";
			out += hex.getType()+" ";
			out += "("+oq.x+","+oq.y+")";
			
			this.fontMainText.drawString(10.0f, (float)(10+this.viewportTLy), out, Color.gray);
		}
		g.clearClip();
		
		//Render the viewport frame
		g.drawLine(this.viewportTLx,this.viewportTLy,this.viewportBRx,this.viewportTLy);
		g.drawLine(this.viewportBRx,this.viewportTLy,this.viewportBRx,this.viewportBRy);
		g.drawLine(this.viewportBRx,this.viewportBRy,this.viewportTLx,this.viewportBRy);
		g.drawLine(this.viewportTLx,this.viewportBRy,this.viewportTLx,this.viewportTLy);
	}
	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		
		int mouseX = this.input.getMouseX();
		int mouseY = this.input.getMouseY();
		
		if(this.input_state == SimpleDrawHexes.INPUT_STATE_MCLICKDRAG)
		{
			int dx = mouseX - this.lastMouseX;
			int dy = mouseY - this.lastMouseY;
			
			this.viewoffsetx += dx;
			this.viewoffsety += dy;
		}
		
		this.lastMouseX = mouseX;
		this.lastMouseY = mouseY;
	}
	
	public static void main (String[] args) {
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new SimpleDrawHexes());
			appgc.setDisplayMode(1024, 768, false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			System.out.println("Failed! "+ex.getMessage());
		}
	}
	
	@Override
	public void controlPressed(Command command) {
		
		if(command.toString().equals("[Command=click]"))
		{
			int clickX = this.input.getMouseX() - this.viewoffsetx;
			int clickY = this.input.getMouseY() - this.viewoffsety;
			
			float fx = (float)clickX / (float)this.hexSizePixels;
			float fy = (float)clickY / (float)this.hexSizePixels;
			Point p = new Point(fx,fy);
			AxialHexCoord ac = HexCoordUtils.fractional2DToHex(p);
			Hex h = this.hexgrid.get(ac);
			if(h != null)
			{
				this.selected = h;
				
				Hex corner = this.hexgrid.get(new AxialHexCoord(0,0));
				if(corner != null)
				{
					this.pathToSelected = this.hexgrid.getRoute(corner, h);
				}
			}
			else
				this.selected = null;
		}
		if(command.toString().equals("[Command=mclick]"))
		{
			this.input_state = SimpleDrawHexes.INPUT_STATE_MCLICKDRAG;
		}
		
	}

	@Override
	public void controlReleased(Command command) {
		if(command.toString().equals("[Command=mclick]"))
		{
			this.input_state = SimpleDrawHexes.INPUT_STATE_BASIC;
		}
	}
	
	public void mouseWheelMoved(int change) {
		change /= 120;
		this.hexSizePixels += 16*change;
		if(this.hexSizePixels < 32)
			this.hexSizePixels = 32;
		if(this.hexSizePixels > 128)
			this.hexSizePixels = 128;
	}
	
}
