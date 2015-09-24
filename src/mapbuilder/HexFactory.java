package mapbuilder;

import hexgrid.Hex;
import hexgrid.coords.AxialHexCoord;
import org.jdom2.Element;

public abstract class HexFactory<T extends Hex> {

	public abstract T produceHex(AxialHexCoord hexcoord, Element hexElement) throws MapBuildException;
	
}
