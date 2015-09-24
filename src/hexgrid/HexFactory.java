package hexgrid;

import hexgrid.coords.AxialHexCoord;
import hexgrid.terraininfo.Terrain;

public abstract class HexFactory<T extends Hex> {

	public abstract T produceHex(AxialHexCoord hexcoord, Terrain terrain);
	
}
