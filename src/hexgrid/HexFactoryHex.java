package hexgrid;

import hexgrid.coords.AxialHexCoord;
import hexgrid.terraininfo.Terrain;

public class HexFactoryHex extends HexFactory<Hex> {

	@Override
	public Hex produceHex(AxialHexCoord hexcoord, Terrain terrain) {
		return new Hex(hexcoord,terrain);
	}

}
