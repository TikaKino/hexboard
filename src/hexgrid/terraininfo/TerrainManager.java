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
				
				Terrain terrain = new Terrain(terrainName);
				
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
