package hexgrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.jgrapht.traverse.ClosestFirstIterator;

import hexgrid.coords.*;

public class HexGrid extends HashMap<AxialHexCoord,Hex>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8676970814894460628L;

	protected ListenableDirectedWeightedGraph<Hex,DefaultWeightedEdge> groundMobilityGraph;
	protected ConnectivityInspector<Hex,DefaultWeightedEdge> groundMobilityInspector;
	
	public HexGrid(int width, int height)
	{
		super(width*height);
		
		this.groundMobilityGraph = new ListenableDirectedWeightedGraph<Hex,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.groundMobilityInspector = new ConnectivityInspector<Hex,DefaultWeightedEdge>(this.groundMobilityGraph);
		this.groundMobilityGraph.addGraphListener(this.groundMobilityInspector);
	}
	
	public Hex put(AxialHexCoord ac, Hex h)
	{
		Hex h2 = super.put(ac,h);
		
		//Remove previous version (if any) from the graph
		if(h2 != null)
			this.groundMobilityGraph.removeVertex(h2);
		
		//Add new hex to the mobility graph
		this.groundMobilityGraph.addVertex(h);
		
		//work out the hexes surrounding this one
		ArrayList<AxialHexCoord> surround = HexCoordUtils.getSurroundingHexCoords(ac);
		Iterator<AxialHexCoord> it = surround.iterator();
		while(it.hasNext())
		{
			AxialHexCoord s = it.next();
			Hex target = this.get(s);
			if(target != null)
			{
				DefaultWeightedEdge e = this.groundMobilityGraph.addEdge(h,target);
				if(e != null)
					this.groundMobilityGraph.setEdgeWeight(e, target.getEntryCost("ground", h));
				
				e = this.groundMobilityGraph.addEdge(target,h);
				if(e != null)
					this.groundMobilityGraph.setEdgeWeight(e, h.getEntryCost("ground", target));
			}
		}
		
		return h2;
	}
	
	public Hex remove(Object o)
	{
		Hex rem = super.remove(o);
		if(rem != null)
			this.groundMobilityGraph.removeVertex(rem);
		return rem;
	}
	
	public ArrayList<Hex> getAdjacentGroundHexes(Hex source)
	{
		ArrayList<Hex> out = new ArrayList<Hex>(6);
		Set<DefaultWeightedEdge> edges = this.groundMobilityGraph.outgoingEdgesOf(source);
		Iterator<DefaultWeightedEdge> it = edges.iterator();
		while(it.hasNext())
		{
			DefaultWeightedEdge e = it.next();
			Hex h = this.groundMobilityGraph.getEdgeTarget(e);
			if(h != null)
				out.add(h);
		}
		
		return out;
		
	}
	
	public ArrayList<Hex> getGroundRoute(Hex source, Hex target)
	{
		DijkstraShortestPath<Hex,DefaultWeightedEdge> pather = new DijkstraShortestPath<Hex,DefaultWeightedEdge>(this.groundMobilityGraph,source,target);
		List<DefaultWeightedEdge> edges = pather.getPathEdgeList();
		ArrayList<Hex> out = new ArrayList<Hex>(edges.size()+1);
		Iterator<DefaultWeightedEdge> path = edges.iterator();
		while(path.hasNext())
		{
			DefaultWeightedEdge e = path.next();
			Hex h = this.groundMobilityGraph.getEdgeTarget(e);
			if(h != null)
				out.add(h);
		}
		return out;
	}
	
	public ArrayList<Hex> getPossibleGroundHexes(Hex source, double maxDistance)
	{
		ArrayList<Hex> out = new ArrayList<Hex>();
		
		ClosestFirstIterator<Hex,DefaultWeightedEdge> it = new ClosestFirstIterator<Hex,DefaultWeightedEdge>(this.groundMobilityGraph,source,maxDistance);
		while(it.hasNext())
			out.add(it.next());
		
		return out;
	}
}
