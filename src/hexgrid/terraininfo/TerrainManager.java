package hexgrid.terraininfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class TerrainManager {
	
	private static final TerrainManager instance = new TerrainManager();
	
	protected Document xmlDoc;
	protected HashMap<String,Terrain> terrains;
	
	protected TerrainManager()
	{
		this.xmlDoc = null;
		this.terrains = null;
	}
	
	public static TerrainManager getInstance()
	{
		return TerrainManager.instance;
	}
	
	public void initTerrainManager(String terrainfile) throws TerrainDataException
	{
		this.xmlDoc = null;
		
		SAXBuilder jdomBuilder = new SAXBuilder();
		try {
			this.xmlDoc = jdomBuilder.build(terrainfile);
			this.terrains = new HashMap<String,Terrain>();
			
			//Basic integrity checks
			if(!this.xmlDoc.getRootElement().getName().equals("tdata"))
				throw new TerrainDataException("Invalid Terrain Data: root element not named tdata");
			if(this.xmlDoc.getRootElement().getChildren("terrains").isEmpty())
				throw new TerrainDataException("Invalid Terrain Data: /tdata/terrains missing.");
			//if(this.xmlDoc.getRootElement().getChildren("roads").isEmpty())
				//throw new TerrainDataException("Invalid Terrain Data: /tdata/roads missing.");
			
			Iterator<Element> terrainsIt = this.xmlDoc.getRootElement().getChildren("terrains").get(0).getChildren("terrain").iterator();
			int terrainNum = 0;
			while(terrainsIt.hasNext())
			{
				terrainNum++;
				Element terrainEl = terrainsIt.next();
				String terrainName = terrainEl.getAttributeValue("name");
				if(terrainName == null || terrainName.equals(""))
					throw new TerrainDataException("Invalid Terrain Data: /tdata/terrains/terrain["+terrainNum+"]/@name missing or empty.");
				if(this.terrains.containsKey(terrainName))
					throw new TerrainDataException("Invalid Terrain Data: /tdata/terrains/terrain["+terrainNum+"]/@name is a duplicate.");
				if(terrainEl.getChildren("movementcosts").isEmpty())
					throw new TerrainDataException("Invalid Terrain Data: /tdata/terrains/terrain["+terrainNum+"]/movementcosts missing.");
				if(terrainEl.getChildren("movementcosts").get(0).getChildren("default").isEmpty())
					throw new TerrainDataException("Invalid Terrain Data: /tdata/terrains/terrain["+terrainNum+"]/movementcosts/default missing.");
				String costStr = terrainEl.getChildren("movementcosts").get(0).getChildren("default").get(0).getTextNormalize();
				double costDbl;
				
				try {
					costDbl = Double.parseDouble(costStr);
				} catch(NumberFormatException e) {
					throw new TerrainDataException("Invalid Terrain Data: /tdata/terrains/terrain["+terrainNum+"]/movementcosts/default is not a number.");
				}
				
				Terrain terrain = new Terrain(terrainName,costDbl);
				
				Iterator<Element> mobCosts = terrainEl.getChildren("movementcosts").get(0).getChildren("mobilitycost").iterator();
				int mobNum = 0;
				while(mobCosts.hasNext())
				{
					mobNum++;
					Element mobCost = mobCosts.next();
					String mobType = mobCost.getAttributeValue("type");
					if(mobType == null || mobType.equals(""))
						throw new TerrainDataException("Invalid Terrain Data: /tdata/terrains/terrain["+terrainNum+"]/movementcosts/mobilitycost["+mobNum+"]/@type missing or empty.");
					String mobCostStr = mobCost.getTextNormalize();
					double mobCostDbl;
					try {
						mobCostDbl = Double.parseDouble(mobCostStr);
					} catch (NumberFormatException e) {
						throw new TerrainDataException("Invalid Terrain Data: /tdata/terrains/terrain["+terrainNum+"]/movementcosts/mobilitycost["+mobNum+"] is not a number.");
					}
					terrain.setEntryCost(mobType, mobCostDbl);
				}
				
				this.terrains.put(terrainName, terrain);
			}
			
		} catch (JDOMException e) {
			throw new TerrainDataException("Invalid terrain xml in "+terrainfile+": "+e.getMessage());
		} catch (IOException e) {
			throw new TerrainDataException("Terrain file "+terrainfile+" could not be loaded: "+e.getMessage());
		}
	}
	
	public Terrain getTerrain(String terrainName)
	{
		if(this.xmlDoc == null)
			throw new TerrainDataRuntimeException("Terrain data requested on uninitialized TerrainManager.");
		
		if(this.terrains == null)
			throw new TerrainDataRuntimeException("Terrain data hashmap not created in TerrainManager; initialization must have failed.");
		
		return this.terrains.get(terrainName);
	}
}
