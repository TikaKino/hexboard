package hexgrid;

import java.util.ArrayList;
import java.util.HashMap;

import hexgrid.coords.AxialHexCoord;
import hexgrid.coords.HexCoordUtils;
import hexgrid.terraininfo.Terrain;

public class Hex {

	protected AxialHexCoord hexcoord;
	protected Terrain terrain;
	protected int height;
	private HashMap<AxialHexCoord,Terrain> roads;
	protected ArrayList<AxialHexCoord> surroundingHexCoords;
	
	public Hex(AxialHexCoord hexcoord, Terrain terrain)
	{
		this.hexcoord = hexcoord;
		this.terrain = terrain;
		this.height = 0;
		
		//Initialise roads map
		this.roads = new HashMap<AxialHexCoord,Terrain>(6);
		this.surroundingHexCoords = HexCoordUtils.getSurroundingHexCoords(hexcoord);
	}
	
	public AxialHexCoord getCoords()
	{
		return this.hexcoord;
	}
	
	public void setTerrain(Terrain terrain)
	{
		this.terrain = terrain;
	}
	
	public Terrain getTerrain()
	{
		return this.terrain;
	}
	
	public String getTypeAbbreviation()
	{
		if(this.terrain.getTerrainName().equals("Open"))
			return "O";
		if(this.terrain.getTerrainName().equals("Light Woods"))
			return "LW";
		if(this.terrain.getTerrainName().equals("Heavy Woods"))
			return "HW";
		if(this.terrain.getTerrainName().equals("Rough"))
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
		if(!this.surroundingHexCoords.contains(target))
			throw new HexGridNeighbourException("Cannot get presence of road from "+this.hexcoord+" to non-neighbour "+target);
		
		Terrain t = this.roads.get(target);
		
		if(t == null)
			return false;
		
		return true;
	}
	
	public Terrain getRoadTo(AxialHexCoord target)
	{
		if(!this.hasRoadTo(target))
			return null;
		
		return this.roads.get(target);
	}
	
	public void setRoadTo(AxialHexCoord target, Terrain roadTerrain)
	{
		if(!this.surroundingHexCoords.contains(target))
			throw new HexGridNeighbourException("Cannot set presence of road from "+this.hexcoord+" to non-neighbour "+target);
		
		if(roadTerrain == null)
			this.roads.remove(target);
		
		this.roads.put(target, roadTerrain);
	}
	
	public void removeRoadTo(AxialHexCoord target)
	{
		if(!this.surroundingHexCoords.contains(target))
			throw new HexGridNeighbourException("Cannot remove of road from "+this.hexcoord+" to non-neighbour "+target);
		
		this.roads.remove(target);
	}
	
	public double getEntryCost(String mobilityType, Hex source)
	{
		if(!this.surroundingHexCoords.contains(source.getCoords()))
		{
			String msg = "Cannot get entry cost to "+this.hexcoord+" from non-neighbour "+source.getCoords();
			throw new HexGridNeighbourException(msg);
		}
		
		double cost = 1.0;
		
		//Determine if there is a road between these hexes. If so, use the cost of that road; if not, use the cost of the terrain.
		if(source.hasRoadTo(this.getCoords()) && this.hasRoadTo(source.getCoords()))
		{
			//Cost of road
			double roadTo = this.getRoadTo(source.getCoords()).getEntryCost(mobilityType);
			double roadFrom = source.getRoadTo(this.getCoords()).getEntryCost(mobilityType);
			cost = Math.max(roadTo, roadFrom);
		}
		else
		{
			//Cost of terrain
			cost = this.terrain.getEntryCost(mobilityType);
		}
		
		//int heightDiff = Math.abs(this.getHeight() - source.getHeight());
		//if(heightDiff != 0 && (mobilityType.equals("Infantry") || mobilityType.equals("Wheeled") || mobilityType.equals("Tracked")))
			//cost += (double)heightDiff;
		
		return cost;
	}
}
