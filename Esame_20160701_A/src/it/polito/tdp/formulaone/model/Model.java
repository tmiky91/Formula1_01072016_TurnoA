package it.polito.tdp.formulaone.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
	private Map<Integer, Driver> idMap;
	private SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge> grafo;
	
	public Model() {
		idMap = new HashMap<>();
	}

	public List<Season> getSeasons() {
		FormulaOneDAO dao = new FormulaOneDAO();
		return dao.getAllSeasons();
	}

	public String creaGrafo(Season s) {
		FormulaOneDAO dao = new FormulaOneDAO();
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		String risultato="";
		double max=0;
		Driver migliore=null;
		dao.getAllDrivers(idMap);
		List<VittoriePiloti> vittorie = dao.getVittoriePiloti(idMap, s);
		for(VittoriePiloti v: vittorie) {
			if(!grafo.containsVertex(v.getD1())) {
				grafo.addVertex(v.getD1());
			}
			if(!grafo.containsVertex(v.getD2())) {
				grafo.addVertex(v.getD2());
			}
			DefaultWeightedEdge edge = grafo.getEdge(v.getD1(), v.getD2());
			if(edge==null) {
				Graphs.addEdgeWithVertices(grafo, v.getD1(), v.getD2(), v.getPeso());
			}
		}
		System.out.println("Grafo creato! Vertici: "+grafo.vertexSet().size()+" Archi: "+grafo.edgeSet().size());
		for(Driver d: grafo.vertexSet()) {
			double somma=0;
			for(DefaultWeightedEdge edge : grafo.outgoingEdgesOf(d)) {
				somma+=grafo.getEdgeWeight(edge);
			}
			for(DefaultWeightedEdge edge : grafo.incomingEdgesOf(d)) {
				somma-=grafo.getEdgeWeight(edge);
			}
			if(somma>max) {
				max=somma;
				migliore = d;
				risultato="Miglior pilota nell'anno selezionato: "+migliore.getSurname();
			}
		}
		return risultato;
	}


}
