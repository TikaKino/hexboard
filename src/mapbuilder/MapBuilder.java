package mapbuilder;

import java.io.IOException;
import java.util.Iterator;

import hexgrid.Hex;
import hexgrid.HexGrid;
import hexgrid.coords.OddQOffsetHexCoord;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class MapBuilder {

	protected Document xmlDoc;
	
	public MapBuilder(String filename) throws MapBuildException
	{
		SAXBuilder jdomBuilder = new SAXBuilder();
		try {
			
			this.xmlDoc = jdomBuilder.build(filename);
			
			//Basic integrity checks
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
			//if(this.xmlDoc.getRootElement().getChildren("info").get(0).getChildren("defaulthex").get(0).getChildren("height").isEmpty())
				//throw new MapBuildException("Invalid map: /map/info/defaulthex/height missing");
			//if(this.xmlDoc.getRootElement().getChildren("info").get(0).getChildren("defaulthex").get(0).getChildren("terrain").isEmpty())
				//throw new MapBuildException("Invalid map: /map/info/defaulthex/terrain missing");
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
	
	private Hex populateHex(Hex hex, Element hexEl, String elementLoc) throws MapBuildException
	{
		if(hexEl.getChildren("terrain").isEmpty())
			throw new MapBuildException("Invalid map: "+elementLoc+"/terrain missing");
		if(hexEl.getChildren("height").isEmpty())
			throw new MapBuildException("Invalid map: "+elementLoc+"/height missing");
		
		String terrain = hexEl.getChildren("terrain").get(0).getTextNormalize();
		String heightstr = hexEl.getChildren("height").get(0).getTextNormalize();
		int height = 0;
		try {
			Integer val = Integer.parseInt(heightstr);
			height = val.intValue();
		} catch (NumberFormatException e) {
			throw new MapBuildException("Invalid map: "+elementLoc+"/height not an integer");
		}
		
		hex.setType(terrain);
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
				oq = new OddQOffsetHexCoord(oq.x+x,oq.y+y);
				hex.setRoadTo(oq.getAxial());
			}
		}
		
		return hex;
	}
	
	//Only returns the explicitly defined hexes, doesn't fill in width and height with default hexes.
	public HexGrid getExplicitHexGrid() throws MapBuildException 
	{
		Iterator<Element> hexes = this.xmlDoc.getRootElement().getChildren("hexes").get(0).getChildren("hex").iterator();
		HexGrid hg = new HexGrid(this.getMapWidth(),this.getMapHeight());
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
			Hex hex = new Hex(oq.getAxial());
			
			if(hg.containsKey(hex.getCoords()))
				throw new MapBuildException("Invalid map: /map/hexes/hex["+elNum+"] is a duplicate hex: ["+oq+"]");
			
			hex = this.populateHex(hex, hexEl, "/map/hexes/hex["+elNum+"]");
			
			hg.put(hex.getCoords(), hex);
		}
		
		return hg;
	}
	
	public HexGrid getHexGrid() throws MapBuildException
	{
		HexGrid hg = this.getExplicitHexGrid();
		Element hexEl = this.xmlDoc.getRootElement().getChildren("info").get(0).getChildren("defaulthex").get(0);
		
		for(int x = 0; x < this.getMapWidth(); x++)
		{
			for(int y = 0; y < this.getMapWidth(); y++)
			{
				OddQOffsetHexCoord oq = new OddQOffsetHexCoord(x,y);
				if(hg.containsKey(oq.getAxial()))
					continue;
				
				Hex hex = new Hex(oq.getAxial());
				hex = this.populateHex(hex, hexEl, "/map/info/defaulthex");
				hg.put(hex.getCoords(), hex);
			}
		}
		
		return hg;
	}
	
	///////////////////////////////////////////////////////
	public static void main(String[] args)
	{
		try {
			MapBuilder mfr = new MapBuilder("maptest.xml");
			System.out.println(mfr.getMapName());
			mfr.getHexGrid();
		} catch (MapBuildException e) {
			System.out.println("Error building map: "+e.getMessage());
		}
	}
}
