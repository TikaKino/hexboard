package hexgrid.terraininfo;

import java.util.HashMap;

public class Terrain {
	
	protected String terrainName;
	protected String terrainAbbreviation;
	
	protected double defaultMovementCost;
	protected HashMap<String,Double> movementCosts;
	
	public Terrain(String terrainName, double defaultEntryCost)
	{
		this.terrainName = terrainName;
		this.terrainAbbreviation = "";
		this.defaultMovementCost = defaultEntryCost;
		this.movementCosts = new HashMap<String,Double>(10);
	}
	
	public String getTerrainName()
	{
		return this.terrainName;
	}
	
	public String toString()
	{
		return this.terrainName;
	}
	
	public String getAbbreviation()
	{
		return this.terrainAbbreviation;
	}
	
	protected void setAbbreviation(String abbreviation)
	{
		this.terrainAbbreviation = abbreviation;
	}
	
	public double getDefaultEntryCost()
	{
		return this.defaultMovementCost;
	}
	
	protected void setEntryCost(String mobilityType, double moveCost)
	{
		this.movementCosts.put(mobilityType, new Double(moveCost));
	}
	
	public boolean hasDefinedEntryCost(String mobilityType)
	{
		return this.movementCosts.containsKey(mobilityType);
	}
	
	public double getEntryCost(String mobilityType)
	{
		Double cost = this.movementCosts.get(mobilityType);
		if(cost == null)
			return this.getDefaultEntryCost();
		
		return cost.doubleValue();
	}
}
