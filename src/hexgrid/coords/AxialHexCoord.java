package hexgrid.coords;

public class AxialHexCoord extends HexCoord {

	public int q;
	public int r;

	public AxialHexCoord(int q, int r)
	{
		this.q = q;
		this.r = r;
	}
	
	public String toString()
	{
		return "a("+this.q+","+this.r+")";
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof AxialHexCoord)
		{
			AxialHexCoord a = (AxialHexCoord)o;
			if(a.q == this.q && a.r == this.r)
				return true;
		}
		else if(o instanceof HexCoord)
		{
			AxialHexCoord a = ((HexCoord)o).getAxial();
			if(a.q == this.q && a.r == this.r)
				return true;
		}
		
		return false;
	}

	@Override
	public AxialHexCoord getAxial() {
		return this;
	}

	@Override
	public CubeHexCoord getCube() {
		return HexCoordUtils.axialToCube(this);
	}

	@Override
	public OddQOffsetHexCoord getOddQOffset() {
		return HexCoordUtils.axialToOddQ(this);
	}
}
