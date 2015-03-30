package hexgrid.terraininfo;

//import java.util.HashMap;

public class Terrain {
	
	protected String terrainName;
	//protected HashMap<String,HashMap<String,Double>> terrainEntryCosts; //(movement type => (source terrain type => cost)), with defaults 
	
	public Terrain(String terrainName)
	{
		this.terrainName = terrainName;
	}
	
	public String getTerrainName()
	{
		return this.terrainName;
	}
	
	public String toString()
	{
		return this.terrainName;
	}
}
