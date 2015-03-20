package hexgrid.coords;

public class CubeHexCoord extends HexCoord {

	public int x;
	public int y;
	public int z;

	public CubeHexCoord(int x, int y, int z)
	{
		if(x+y+z != 0)
			throw new HexCoordValidationException("Cube Hex Co-ordinate validation failed (x+y+z != 0): ("+x+", "+y+", "+z+")");
		
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString()
	{
		return "c("+this.x+","+this.y+","+this.z+")";
	}

	@Override
	public AxialHexCoord getAxial() {
		return HexCoordUtils.cubeToAxial(this);
	}

	@Override
	public CubeHexCoord getCube() {
		return this;
	}

	@Override
	public OddQOffsetHexCoord getOddQOffset() {
		return HexCoordUtils.cubeToOddQ(this);
	}
}
