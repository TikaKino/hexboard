package hexgrid;

import hexgrid.coords.AxialHexCoord;

public class Hex {

	protected AxialHexCoord hexcoord;
	protected String type;
	
	public Hex(AxialHexCoord hexcoord)
	{
		this.hexcoord = hexcoord;
		this.type = "Open";
	}
	
	public AxialHexCoord getCoords()
	{
		return this.hexcoord;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public double getEntryCost(String mobilityType, Hex source)
	{
		return 1.0 + Math.random() * 2.0;
	}
}
