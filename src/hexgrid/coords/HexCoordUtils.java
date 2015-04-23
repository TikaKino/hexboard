package hexgrid.coords;

import hexgrid.Point;

import java.util.ArrayList;

/**
 * Utility functions to handle individual co-ordinate points, converting between the possible systems.
 * 
 * @author	ms1r08
 * @see		http://www.redblobgames.com/grids/hexagons/
 */
public class HexCoordUtils {
	
	//Cube to x functions
	public static AxialHexCoord cubeToAxial(CubeHexCoord ch)
	{
		int q = ch.x;
		int r = ch.z;
		return new AxialHexCoord(q,r);
	}
	
	public static OddQOffsetHexCoord cubeToOddQ(CubeHexCoord ch)
	{
		int ox = ch.x;
		int oy = ch.z + (ch.x - (ch.x & 1))/2;
		return new OddQOffsetHexCoord(ox,oy);
	}
	
	public static CubeFloatHexCoord cubeToCubeFloat(CubeHexCoord ch)
	{
		return new CubeFloatHexCoord((float)ch.x,(float)ch.y,(float)ch.z);
	}
	
	//Round CubeFloat back to Cube
	public static CubeHexCoord cubeFloatToCube(CubeFloatHexCoord fch)
	{
		float rx = Math.round(fch.x);
		float ry = Math.round(fch.y);
		float rz = Math.round(fch.z);
		
		int diffx = (int)Math.round(Math.abs(rx - fch.x));
		int diffy = (int)Math.round(Math.abs(ry - fch.y));
		int diffz = (int)Math.round(Math.abs(rz - fch.z));
		
		int cx = (int)rx;
		int cy = (int)ry;
		int cz = (int)rz;
		
		if(diffx > diffy && diffx > diffz)
			cx = -cy-cz;
		else if (diffy > diffz)
			cy = -cx-cz;
		else
			cz = -cx-cy;
		
		return new CubeHexCoord(cx,cy,cz);
	}
	
	//Axial to x functions
	public static CubeHexCoord axialToCube(AxialHexCoord ah)
	{
		int x = ah.q;
		int z = ah.r;
		int y = -x-z;
		return new CubeHexCoord(x,y,z);
	}
	
	public static OddQOffsetHexCoord axialToOddQ(AxialHexCoord ah)
	{
		return cubeToOddQ(axialToCube(ah));
	}
	
	
	//OddQ to x functions
	public static CubeHexCoord oddQToCube(OddQOffsetHexCoord oh)
	{
		int cx = oh.x;
		int cz = oh.y - (oh.x - (oh.x & 1))/2;
		int cy = -cx-cz;
		return new CubeHexCoord(cx,cy,cz);
	}
	
	public static AxialHexCoord oddQToAxial(OddQOffsetHexCoord oh)
	{
		return cubeToAxial(oddQToCube(oh));
	}
	
	
	//Other functions
	public static AxialHexCoord fractional2DToHex(Point p)
	{	
		float x = p.getX();
		float y = p.getY();

		float q = x * (float)(2.0f/3.0f);
		float r = (-x / 3.0f) + ((((float)Math.sqrt(3))/3.0f) * y);
		
		float cx = q;
		float cz = r;
		float cy = -q-r;
		
		CubeFloatHexCoord cfh = new CubeFloatHexCoord(cx,cy,cz);
		CubeHexCoord ch = HexCoordUtils.cubeFloatToCube(cfh);
		return ch.getAxial();
	}
	
	public static Point hexToFractional2D(AxialHexCoord ac)
	{
		float fx = (float)ac.q * 3/2;
		float fy = (float)Math.sqrt(3) * ((float)ac.r + (float)ac.q/2);
		return new Point(fx,fy);
	}
	
	private static Point leftHexCornerFractional2D(AxialHexCoord ac)
	{
		Point center = HexCoordUtils.hexToFractional2D(ac);
		return new Point(center.getX()-1.0f,center.getY());
	}
	
	private static Point rightHexCornerFractional2D(AxialHexCoord ac)
	{
		Point center = HexCoordUtils.hexToFractional2D(ac);
		return new Point(center.getX()+1.0f,center.getY());
	}
	
	public static ArrayList<Point> hexCornersFractional2D(AxialHexCoord ac)
	{
		ArrayList<Point> out = new ArrayList<Point>(6);
		//For consistency and to reduce rounding errors, each hex "owns" the two corners level with its 
		//centre; i=0 and i=3 by the standard model.
		//Top left corner is owned by a(-1,0); Top right by a(1,-1); Bottom left by a(-1,1) and Bottom right by a(1,0)
		//order is TL,TR,R,BR,BL,L
		//This is better than using sin and cos to calculate points from the center of each hex individually
		//as that leads to subtly different coordinates for each point thanks to the joys of float precision errors
		
		out.add(HexCoordUtils.rightHexCornerFractional2D(new AxialHexCoord(ac.q-1,ac.r)));
		out.add(HexCoordUtils.leftHexCornerFractional2D(new AxialHexCoord(ac.q+1,ac.r-1)));
		out.add(HexCoordUtils.rightHexCornerFractional2D(ac));
		out.add(HexCoordUtils.leftHexCornerFractional2D(new AxialHexCoord(ac.q+1,ac.r)));
		out.add(HexCoordUtils.rightHexCornerFractional2D(new AxialHexCoord(ac.q-1,ac.r+1)));
		out.add(HexCoordUtils.leftHexCornerFractional2D(ac));
		
		return out;
	}
	
	public static ArrayList<AxialHexCoord> getSurroundingHexCoords(AxialHexCoord ac)
	{
		ArrayList<AxialHexCoord> out = new ArrayList<AxialHexCoord>(6);
		
		out.add(new AxialHexCoord(ac.q  ,ac.r-1));
		out.add(new AxialHexCoord(ac.q+1,ac.r-1));
		out.add(new AxialHexCoord(ac.q+1,ac.r  ));
		out.add(new AxialHexCoord(ac.q  ,ac.r+1));
		out.add(new AxialHexCoord(ac.q-1,ac.r+1));
		out.add(new AxialHexCoord(ac.q-1,ac.r  ));
		
		return out;
	}
	
	
	//Tests
	private static void coordTestCube(int x, int y, int z)
	{
		CubeHexCoord startcube;
		AxialHexCoord axial;
		OddQOffsetHexCoord oddq;
		CubeHexCoord endcube;
		
		startcube = new CubeHexCoord(x,y,z);
		System.out.println("New test: "+startcube);
		
		axial = HexCoordUtils.cubeToAxial(startcube);
		endcube = HexCoordUtils.axialToCube(axial);
		System.out.println("Axial: "+startcube+" => "+axial+" => "+endcube);
		
		oddq = HexCoordUtils.cubeToOddQ(startcube);
		endcube = HexCoordUtils.oddQToCube(oddq);
		System.out.println("OddQ Offset: "+startcube+" => "+oddq+" => "+endcube);
	}
	
	private static void coordTestAxial(int q, int r)
	{
		AxialHexCoord startaxial;
		AxialHexCoord endaxial;
		CubeHexCoord cube;
		OddQOffsetHexCoord oddq;
		
		startaxial = new AxialHexCoord(q,r);
		System.out.println("New test: "+startaxial);
		
		cube = HexCoordUtils.axialToCube(startaxial);
		endaxial = HexCoordUtils.cubeToAxial(cube);
		System.out.println("Cube: "+startaxial+" => "+cube+" => "+endaxial);
		
		oddq = HexCoordUtils.axialToOddQ(startaxial);
		endaxial = HexCoordUtils.oddQToAxial(oddq);
		System.out.println("OddQ: "+startaxial+" => "+oddq+" => "+endaxial);
	}
	
	private static void coordTestOddQ(int x, int y)
	{
		OddQOffsetHexCoord startoddq;
		OddQOffsetHexCoord endoddq;
		CubeHexCoord cube;
		AxialHexCoord axial;
		
		startoddq = new OddQOffsetHexCoord(x,y);
		System.out.println("New test: "+startoddq);
		
		cube = HexCoordUtils.oddQToCube(startoddq);
		endoddq = HexCoordUtils.cubeToOddQ(cube);
		System.out.println("Cube: "+startoddq+" => "+cube+" => "+endoddq);
		
		axial = HexCoordUtils.oddQToAxial(startoddq);
		endoddq = HexCoordUtils.axialToOddQ(axial);
		System.out.println("Axial: "+startoddq+" => "+axial+" => "+endoddq);
	}
	
	private static void coordTestCubeFloat(float fx, float fy, float fz)
	{
		CubeFloatHexCoord start;
		CubeHexCoord cube;
		
		start = new CubeFloatHexCoord(fx,fy,fz);
		cube = HexCoordUtils.cubeFloatToCube(start);
		System.out.println("Rounding "+start+" => "+cube);
	}
	
	private static void coordEqualityTests(HexCoord hc)
	{
		System.out.println("Equality Testing: "+hc);
		boolean eAxial = hc.equals(hc.getAxial());
		boolean eCube = hc.equals(hc.getCube());
		boolean eOddQ = hc.equals(hc.getOddQOffset());
		System.out.println("Values: "+hc.getAxial()+", "+hc.getCube()+", "+hc.getOddQOffset());
		System.out.println("Equalities: "+eAxial+", "+eCube+", "+eOddQ);
	}
	
	private static void coordEqualityTests(HexCoord hc,HexCoord hc2)
	{
		System.out.println("Equality Testing: "+hc+", "+hc2+": "+hc.equals(hc2));
	}
	
	public static void main(String[] args)
	{
		coordTestCube(0,0,0);
		System.out.println();
		coordTestCube(1,-1,0);
		System.out.println();
		coordTestCube(-10,5,5);
		
		System.out.println();
		coordTestAxial(0,0);
		System.out.println();
		coordTestAxial(1,0);
		System.out.println();
		coordTestAxial(0,-1);
		System.out.println();
		coordTestAxial(4,-10);
		
		System.out.println();
		coordTestOddQ(0,0);
		System.out.println();
		coordTestOddQ(1,0);
		System.out.println();
		coordTestOddQ(0,-1);
		System.out.println();
		coordTestOddQ(5,-10);
		
		System.out.println();
		coordTestCubeFloat(0.0f,0.0f,0.0f);
		coordTestCubeFloat(1.7f,-1.7f,0.0f);
		coordTestCubeFloat(-0.5f,0.5f,0.0f);
		coordTestCubeFloat(0.0f,0.5f,-0.5f);
		
		System.out.println();
		coordEqualityTests(new AxialHexCoord(1,-2));
		System.out.println();
		coordEqualityTests(new CubeHexCoord(1,-2,1));
		System.out.println();
		coordEqualityTests(new OddQOffsetHexCoord(1,-2));
		System.out.println();
		coordEqualityTests(new CubeFloatHexCoord(1.3,-1.7,0.4));
		
		System.out.println();
		coordEqualityTests(new AxialHexCoord(1,-2),new CubeHexCoord(1,1,-2));
		coordEqualityTests(new AxialHexCoord(1,-2),new AxialHexCoord(1,-2));
		coordEqualityTests(new AxialHexCoord(1,-2),new CubeHexCoord(1,5,-6));
		coordEqualityTests(new AxialHexCoord(1,-2),new AxialHexCoord(4,-2));
	}
}
