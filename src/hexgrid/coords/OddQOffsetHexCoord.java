package hexgrid.coords;

public class OddQOffsetHexCoord extends HexCoord {
	
	public int x;
	public int y;
	
	public OddQOffsetHexCoord(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public String toString()
	{
		return "oq("+this.x+","+this.y+")";
	}

	@Override
	public AxialHexCoord getAxial() {
		return HexCoordUtils.oddQToAxial(this);
	}

	@Override
	public CubeHexCoord getCube() {
		return HexCoordUtils.oddQToCube(this);
	}

	@Override
	public OddQOffsetHexCoord getOddQOffset() {
		return this;
	}
}
