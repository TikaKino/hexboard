package hexgrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
	
	protected HashMap<String,ListenableDirectedWeightedGraph<Hex,DefaultWeightedEdge>> mobilityGraphs;
	
	public HexGrid(int width, int height)
	{
		super(width*height);
		
		this.mobilityGraphs = new HashMap<String,ListenableDirectedWeightedGraph<Hex,DefaultWeightedEdge>>(10);
	}
	
	public void addMobilityType(String mobility)
	{	
		//If we've already got this mobility type, no need to do anything.
		if(this.mobilityGraphs.containsKey(mobility))
			return;
		
		ListenableDirectedWeightedGraph<Hex,DefaultWeightedEdge> graph = new ListenableDirectedWeightedGraph<Hex,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.mobilityGraphs.put(mobility, graph);
		
		//Add our existing hexes to the new graph, if any; otherwise, adding a new mobility type mid-runtime would screw everything up.
		Iterator<Hex> hexes = this.values().iterator();
		while(hexes.hasNext())
		{
			Hex hex = hexes.next();
			this.addToMobilityGraph(hex, mobility);
		}
	}
	
	private void removeFromAllMobilityGraphs(Hex h)
	{
		Iterator<ListenableDirectedWeightedGraph<Hex,DefaultWeightedEdge>> it = this.mobilityGraphs.values().iterator();
		while(it.hasNext())
			it.next().removeVertex(h);
	}
	
	private void addToMobilityGraph(Hex h,String mobility)
	{
		ListenableDirectedWeightedGraph<Hex,DefaultWeightedEdge> graph = this.mobilityGraphs.get(mobility); 
		
		//If this mobility graph already has this vertex, just move on.
		if(graph.containsVertex(h))
			return;
		
		graph.addVertex(h);
		
		//Add edges to and from existing neighbouring hexes, with costs supplied by the hexes themselves.
		ArrayList<AxialHexCoord> surroundList = HexCoordUtils.getSurroundingHexCoords(h.getCoords());
		Iterator<AxialHexCoord> surrounds = surroundList.iterator();
		while(surrounds.hasNext())
		{
			AxialHexCoord neighbourCoord = surrounds.next();
			Hex target = this.get(neighbourCoord);
			if(target != null)
			{
				DefaultWeightedEdge hToTarget = graph.addEdge(h,target);
				DefaultWeightedEdge targetToH = graph.addEdge(target,h);
				if(hToTarget != null)
					graph.setEdgeWeight(hToTarget,target.getEntryCost(mobility,h));
				if(targetToH != null)
					graph.setEdgeWeight(targetToH,h.getEntryCost(mobility,target));
			}
		}
	}
	
	private void addToAllMobilityGraphs(Hex h)
	{
		Iterator<String> it = this.mobilityGraphs.keySet().iterator();
		while(it.hasNext())
		{
			String mobility = it.next();
			this.addToMobilityGraph(h, mobility);
		}
	}
	
	public Hex put(AxialHexCoord ac, Hex h)
	{
		Hex h2 = super.put(ac,h);
		
		//Remove previous version (if any) from the graph
		if(h2 != null)
			this.removeFromAllMobilityGraphs(h2);
		
		//Add new hex to the mobility graph
		this.addToAllMobilityGraphs(h);
		
		return h2;
	}
	
	public Hex remove(Object o)
	{
		Hex rem = super.remove(o);
		if(rem != null)
			this.removeFromAllMobilityGraphs(rem);
		return rem;
	}
	
	public ArrayList<Hex> getGraphAdjacentHexes(Hex source,String mobilityType)
	{
		ArrayList<Hex> out = new ArrayList<Hex>(6);
		ListenableDirectedWeightedGraph<Hex,DefaultWeightedEdge> graph = this.mobilityGraphs.get(mobilityType);
		if(graph == null)
			throw new HexGridNonexistentMobilityException("Cannot get adjacent hexes for nonexistent mobility type "+mobilityType);
		
		Set<DefaultWeightedEdge> edges = graph.outgoingEdgesOf(source);
		Iterator<DefaultWeightedEdge> it = edges.iterator();
		while(it.hasNext())
		{
			DefaultWeightedEdge e = it.next();
			Hex h = graph.getEdgeTarget(e);
			if(h != null)
				out.add(h);
		}
		
		return out;
	}
	
	public ArrayList<Hex> getGraphShortestRoute(Hex source, Hex target, String mobilityType)
	{
		ListenableDirectedWeightedGraph<Hex,DefaultWeightedEdge> graph = this.mobilityGraphs.get(mobilityType);
		if(graph == null)
			throw new HexGridNonexistentMobilityException("Cannot get shortest route for nonexistent mobility type "+mobilityType);
		
		DijkstraShortestPath<Hex,DefaultWeightedEdge> pather = new DijkstraShortestPath<Hex,DefaultWeightedEdge>(graph,source,target);
		List<DefaultWeightedEdge> edges = pather.getPathEdgeList();
		ArrayList<Hex> out = new ArrayList<Hex>(edges.size()+1);
		out.add(source);
		Iterator<DefaultWeightedEdge> path = edges.iterator();
		while(path.hasNext())
		{
			DefaultWeightedEdge e = path.next();
			Hex h = graph.getEdgeTarget(e);
			if(h != null)
				out.add(h);
		}
		return out;
	}
	
	public ArrayList<Hex> getPossibleGraphHexes(Hex source, double maxDistance, String mobilityType)
	{
		ListenableDirectedWeightedGraph<Hex,DefaultWeightedEdge> graph = this.mobilityGraphs.get(mobilityType);
		if(graph == null)
			throw new HexGridNonexistentMobilityException("Cannot get possible movement hexes for nonexistent mobility type "+mobilityType);
		
		ArrayList<Hex> out = new ArrayList<Hex>();
		
		ClosestFirstIterator<Hex,DefaultWeightedEdge> it = new ClosestFirstIterator<Hex,DefaultWeightedEdge>(graph,source,maxDistance);
		while(it.hasNext())
			out.add(it.next());
		
		return out;
	}
}
