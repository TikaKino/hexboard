package mapbuilder;

import hexgrid.Hex;
import hexgrid.coords.AxialHexCoord;
import hexgrid.coords.OddQOffsetHexCoord;
import hexgrid.terraininfo.Terrain;
import hexgrid.terraininfo.TerrainManager;

import java.util.Iterator;

import org.jdom2.Element;

public class HexFactoryHex extends HexFactory<Hex> {

	@Override
	public Hex produceHex(AxialHexCoord hexcoord, Element hexElement) throws MapBuildException {
		Hex hex = new Hex(hexcoord,null);
		String elementLoc = MapBuilder.getElementLocString(hexElement); 
		if(hexElement.getChildren("terrain").isEmpty())
			throw new MapBuildException("Invalid map: "+elementLoc+"/terrain missing");
		if(hexElement.getChildren("height").isEmpty())
			throw new MapBuildException("Invalid map: "+elementLoc+"/height missing");
		
		String terrainName = hexElement.getChildren("terrain").get(0).getTextNormalize();
		TerrainManager tMng = TerrainManager.getInstance();
		Terrain terrain = tMng.getTerrain(terrainName);
		if(terrain == null)
			throw new MapBuildException("Invalid map: "+elementLoc+"/terrain is not a valid terrain type ("+terrainName+")");
		
		String heightstr = hexElement.getChildren("height").get(0).getTextNormalize();
		int height = 0;
		try {
			Integer val = Integer.parseInt(heightstr);
			height = val.intValue();
		} catch (NumberFormatException e) {
			throw new MapBuildException("Invalid map: "+elementLoc+"/height not an integer");
		}
		
		hex.setTerrain(terrain);
		hex.setHeight(height);
		
		if(!hexElement.getChildren("roads").isEmpty())
		{
			OddQOffsetHexCoord oq = hex.getCoords().getOddQOffset();
			int roadnum = 0;
			Iterator<Element> roads = hexElement.getChildren("roads").get(0).getChildren("road").iterator();
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
	}

}
