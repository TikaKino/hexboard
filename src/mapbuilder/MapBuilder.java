package mapbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import hexgrid.Hex;
import hexgrid.HexGrid;
import hexgrid.coords.OddQOffsetHexCoord;
//import hexgrid.terraininfo.Terrain;
//import hexgrid.terraininfo.TerrainManager;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class MapBuilder<T extends Hex> {

	protected Document xmlDoc;
	protected HexFactory<T> factory;
	
	public MapBuilder(String filename,HexFactory<T> factory) throws MapBuildException
	{
		this.factory = factory;
		SAXBuilder jdomBuilder = new SAXBuilder();
		try {
			
			this.xmlDoc = jdomBuilder.build(filename);
			
			//Basic integrity checks
			if(!this.xmlDoc.getRootElement().getName().equals("map"))
				throw new MapBuildException("Invalid map: root element not named map");
			if(this.xmlDoc.getRootElement().getChildren("info").isEmpty())
				throw new MapBuildException("Invalid map: /map/info missing");
			if(this.xmlDoc.getRootElement().getChildren("info").get(0).getChildren("name").isEmpty())
				throw new MapBuildException("Invalid map: /map/info/name missing");
			if(this.xmlDoc.getRootElement().getChildren("info").get(0).getChildren("width").isEmpty())
				throw new MapBuildException("Invalid map: /map/info/width missing");
			if(this.xmlDoc.getRootElement().getChildren("info").get(0).getChildren("height").isEmpty())
				throw new MapBuildException("Invalid map: /map/info/height missing");
			if(this.xmlDoc.getRootElement().getChildren("info").get(0).getChildren("defaulthex").isEmpty())
				throw new MapBuildException("Invalid map: /map/info/defaulthex missing");
			if(this.xmlDoc.getRootElement().getChildren("hexes").isEmpty())
				throw new MapBuildException("Invalid map: /map/hexes missing");
			
		} catch (JDOMException e) {
			throw new MapBuildException("Invalid map xml: "+e.getMessage());
		} catch (IOException e) {
			throw new MapBuildException("Map file could not be loaded: "+e.getMessage());
		}
	}
	
	public String getMapName()
	{
		Element e = xmlDoc.getRootElement().getChildren("info").get(0).getChildren("name").get(0);
		return e.getTextNormalize();
	}
	
	public int getMapWidth() throws MapBuildException
	{
		String text = this.xmlDoc.getRootElement().getChildren("info").get(0).getChildren("width").get(0).getTextNormalize();
		try {
			Integer val = Integer.parseInt(text);
			return val.intValue();
		} catch(NumberFormatException e) {
			throw new MapBuildException("Invalid map: width is not an integer");
		}
	}
	
	public int getMapHeight() throws MapBuildException
	{
		String text = this.xmlDoc.getRootElement().getChildren("info").get(0).getChildren("height").get(0).getTextNormalize();
		try {
			Integer val = Integer.parseInt(text);
			return val.intValue();
		} catch(NumberFormatException e) {
			throw new MapBuildException("Invalid map: height is not an integer");
		}
	}
	
	/*private T populateHex(T hex, Element hexEl, String elementLoc) throws MapBuildException
	{
		if(hexEl.getChildren("terrain").isEmpty())
			throw new MapBuildException("Invalid map: "+elementLoc+"/terrain missing");
		if(hexEl.getChildren("height").isEmpty())
			throw new MapBuildException("Invalid map: "+elementLoc+"/height missing");
		
		String terrainName = hexEl.getChildren("terrain").get(0).getTextNormalize();
		TerrainManager tMng = TerrainManager.getInstance();
		Terrain terrain = tMng.getTerrain(terrainName);
		if(terrain == null)
			throw new MapBuildException("Invalid map: "+elementLoc+"/terrain is not a valid terrain type ("+terrainName+")");
		
		String heightstr = hexEl.getChildren("height").get(0).getTextNormalize();
		int height = 0;
		try {
			Integer val = Integer.parseInt(heightstr);
			height = val.intValue();
		} catch (NumberFormatException e) {
			throw new MapBuildException("Invalid map: "+elementLoc+"/height not an integer");
		}
		
		hex.setTerrain(terrain);
		hex.setHeight(height);
		
		if(!hexEl.getChildren("roads").isEmpty())
		{
			OddQOffsetHexCoord oq = hex.getCoords().getOddQOffset();
			int roadnum = 0;
			Iterator<Element> roads = hexEl.getChildren("roads").get(0).getChildren("road").iterator();
			while(roads.hasNext())
			{
				roadnum++;
				Element road = roads.next();
				String xs = road.getAttributeValue("x");
				String ys = road.getAttributeValue("y");
				if(xs == null)
					throw new MapBuildException("Invalid map: "+elementLoc+"/roads/road["+roadnum+"]/@x missing");
				if(ys == null)
					throw new MapBuildException("Invalid map: "+elementLoc+"/roads/road["+roadnum+"]/@y missing");
				int x = 0;
				int y = 0;
				try {
					Integer val = Integer.parseInt(xs);
					x = val.intValue();
				} catch (NumberFormatException e) {
					throw new MapBuildException("Invalid map: "+elementLoc+"/roads/road["+roadnum+"]/@x not an integer");
				}
				try {
					Integer val = Integer.parseInt(ys);
					y = val.intValue();
				} catch (NumberFormatException e) {
					throw new MapBuildException("Invalid map: "+elementLoc+"/roads/road["+roadnum+"]/@y not an integer");
				}
				oq = new OddQOffsetHexCoord(x,y);
				
				if(road.getChildren("terrain").isEmpty())
					throw new MapBuildException("Invalid map: "+elementLoc+"/roads/road["+roadnum+"]/terrain missing.");
				String roadTerrainName = road.getChildren("terrain").get(0).getTextTrim();
				Terrain roadTerrain = tMng.getTerrain(roadTerrainName);
				if(roadTerrain == null)
					throw new MapBuildException("Invalid map: "+elementLoc+"/roads/road["+roadnum+"]/terrain is not a valid terrain type ("+roadTerrainName+")");
				
				hex.setRoadTo(oq.getAxial(), roadTerrain);
			}
		}
		
		return hex;
	}*/
	
	//Only returns the explicitly defined hexes, doesn't fill in width and height with default hexes.
	public HexGrid<T> getExplicitHexGrid() throws MapBuildException 
	{
		Iterator<Element> hexes = this.xmlDoc.getRootElement().getChildren("hexes").get(0).getChildren("hex").iterator();
		HexGrid<T> hg = new HexGrid<T>(this.getMapWidth(),this.getMapHeight());
		int elNum = 0;
		
		while(hexes.hasNext())
		{
			Element hexEl = hexes.next();
			elNum++;
			
			String xs = hexEl.getAttributeValue("x");
			if(xs == null)
				throw new MapBuildException("Invalid map: /map/hexes/hex["+elNum+"]/@x missing");
			String ys = hexEl.getAttributeValue("y");
			if(ys == null)
				throw new MapBuildException("Invalid map: /map/hexes/hex["+elNum+"]/@y missing");
			
			int x = 0;
			int y = 0;
			
			try {
				Integer val = Integer.parseInt(xs);
				x = val.intValue();
			} catch (NumberFormatException e) {
				throw new MapBuildException("Invalid map: /map/hexes/hex["+elNum+"]/@x not an integer");
			}
			
			try {
				Integer val = Integer.parseInt(ys);
				y = val.intValue();
			} catch (NumberFormatException e) {
				throw new MapBuildException("Invalid map: /map/hexes/hex["+elNum+"]/@y not an integer");
			}
			
			OddQOffsetHexCoord oq = new OddQOffsetHexCoord(x,y);
			//T hex = new T(oq.getAxial(),null);
			T hex = this.factory.produceHex(oq.getAxial(),hexEl);
			
			if(hg.containsKey(hex.getCoords()))
				throw new MapBuildException("Invalid map: /map/hexes/hex["+elNum+"] is a duplicate hex: ["+oq+"]");
			
			//hex = this.populateHex(hex, hexEl, "/map/hexes/hex["+elNum+"]");
			
			hg.put(hex.getCoords(), hex);
		}
		
		return hg;
	}
	
	public HexGrid<T> getHexGrid() throws MapBuildException
	{
		HexGrid<T> hg = this.getExplicitHexGrid();
		Element hexEl = this.xmlDoc.getRootElement().getChildren("info").get(0).getChildren("defaulthex").get(0);
		
		for(int x = 0; x < this.getMapWidth(); x++)
		{
			for(int y = 0; y < this.getMapWidth(); y++)
			{
				OddQOffsetHexCoord oq = new OddQOffsetHexCoord(x,y);
				if(hg.containsKey(oq.getAxial()))
					continue;
				
				T hex = this.factory.produceHex(oq.getAxial(),hexEl);
				//hex = this.populateHex(hex, hexEl, "/map/info/defaulthex");
				hg.put(hex.getCoords(), hex);
			}
		}
		
		return hg;
	}
	
	public static String getElementLocString(Element e)
	{
		Element working = e;
		ArrayList<Element> chain = new ArrayList<Element>();
		while(working != null)
		{
			chain.add(working);
			working = working.getParentElement();
		}
		Iterator<Element> chainIt = chain.iterator();
		String out = "";
		while(chainIt.hasNext())
		{
			working = chainIt.next();
			out += "/"+working.getName();
		}
		return out;
	}
}
