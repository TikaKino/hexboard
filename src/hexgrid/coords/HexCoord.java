package hexgrid.coords;

public abstract class HexCoord {

	public abstract AxialHexCoord getAxial();
	public abstract CubeHexCoord getCube();
	public abstract OddQOffsetHexCoord getOddQOffset();
	
	public boolean equals(Object o)
	{
		if(!(o instanceof HexCoord) || o == null)
			return false;
		
		HexCoord hc = (HexCoord)o;
		
		if(hc.getAxial().equals(this.getAxial()))
			return true;
		
		return false;
	}
	
	public int hashCode()
	{
		return this.getAxial().toString().hashCode();
	}
}
