package hexgrid.coords;

public class CubeFloatHexCoord extends HexCoord {

	public double x;
	public double y;
	public double z;
	
	public CubeFloatHexCoord(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString()
	{
		return "cf("+this.x+","+this.y+","+this.z+")";
	}

	@Override
	public AxialHexCoord getAxial() {
		return HexCoordUtils.cubeToAxial(HexCoordUtils.cubeFloatToCube(this));
	}

	@Override
	public CubeHexCoord getCube() {
		return HexCoordUtils.cubeFloatToCube(this);
	}

	@Override
	public OddQOffsetHexCoord getOddQOffset() {
		return HexCoordUtils.cubeToOddQ(HexCoordUtils.cubeFloatToCube(this));
	}
}
