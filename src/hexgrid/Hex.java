package hexgrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import hexgrid.coords.AxialHexCoord;
import hexgrid.coords.HexCoordUtils;

public class Hex {

	protected AxialHexCoord hexcoord;
	protected String type;
	protected int height;
	private HashMap<AxialHexCoord,Boolean> roads;
	protected ArrayList<AxialHexCoord> surroundingHexCoords;
	
	public Hex(AxialHexCoord hexcoord)
	{
		this.hexcoord = hexcoord;
		this.type = "Open";
		this.height = 0;
		
		//Initialise roads map
		this.roads = new HashMap<AxialHexCoord,Boolean>(6);
		this.surroundingHexCoords = HexCoordUtils.getSurroundingHexCoords(hexcoord);
		Iterator<AxialHexCoord> it = this.surroundingHexCoords.iterator();
		while(it.hasNext())
		{
			AxialHexCoord co = it.next();
			this.roads.put(co, new Boolean(false));
		}
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
	
	public String getTypeAbbreviation()
	{
		if(this.type.equals("Open"))
			return "O";
		if(this.type.equals("Light Woods"))
			return "LW";
		if(this.type.equals("Heavy Woods"))
			return "HW";
		if(this.type.equals("Rough"))
			return "R";
		
		return "?";
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	public int getHeight()
	{
		return this.height;
	}
	
	public boolean hasRoadTo(AxialHexCoord target)
	{
		Boolean b = this.roads.get(target);
		
		if(b == null)
			throw new HexGridNeighbourException("Cannot get presence of road from "+this.hexcoord+" to non-neighbour "+target);
		
		return b.booleanValue();
	}
	
	public void setRoadTo(AxialHexCoord target)
	{
		if(!this.surroundingHexCoords.contains(target))
			throw new HexGridNeighbourException("Cannot set presence of road from "+this.hexcoord+" to non-neighbour "+target);
	}
	
	public double getEntryCost(String mobilityType, Hex source)
	{
		if(!this.surroundingHexCoords.contains(source.getCoords()))
		{
			String msg = "Cannot get entry cost to "+this.hexcoord+" from non-neighbour "+source.getCoords();
			throw new HexGridNeighbourException(msg);
		}
		
		double cost = 1.0;
		
		if(mobilityType.equals("Air"))
			return cost;
		
		int heightDiff = Math.abs(this.getHeight() - source.getHeight());
		if(heightDiff != 0 && (mobilityType.equals("Infantry") || mobilityType.equals("Wheeled") || mobilityType.equals("Tracked")))
			cost += (double)heightDiff;
		
		//Determine if there is a road between these hexes; if not, apply mobility penalties for terrain type.
		if(source.hasRoadTo(this.getCoords()) && this.hasRoadTo(source.getCoords()))
		{
			//Any road-specific mobility modifications here
			if(mobilityType.equals("Wheeled"))
				cost *= 0.5;
			else if(mobilityType.equals("Tracked"))
				cost *= 0.75;
		}
		else
		{
			//Any mobility modifications cancelled by the presence of a road here
			if(this.getType().equals("Light Woods") && (mobilityType.equals("Wheeled") || mobilityType.equals("Tracked")))
				cost += 1.0;
		
			if(this.getType().equals("Heavy Woods") && (mobilityType.equals("Wheeled") || mobilityType.equals("Tracked")))
				cost += 2.0;
		}
		
		return cost;
	}
}
