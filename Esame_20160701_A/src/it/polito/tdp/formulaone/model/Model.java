package it.polito.tdp.formulaone.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
	private DefaultDirectedWeightedGraph<Driver, DefaultWeightedEdge> grafo;

	public static List<Integer> getAllSeason() {
		FormulaOneDAO dao = new FormulaOneDAO();
		return dao.getAllYearsOfRace();
	}

	public String risultatiPilota(Integer year) {
		Driver migliorPilota = null;
		String risultato="";
		double max=0;
		grafo = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		FormulaOneDAO dao = new FormulaOneDAO();
		dao.popolaGrafo(grafo, year);
		for(DefaultWeightedEdge e: grafo.edgeSet()) {
			System.out.println(grafo.getEdgeWeight(e)+"\n");
		}
		for(Driver d: grafo.vertexSet()) {
			double risultatoPilota=0;
			for(DefaultWeightedEdge edge: grafo.outgoingEdgesOf(d)) {
				risultatoPilota+=grafo.getEdgeWeight(edge);
			}
			for(DefaultWeightedEdge edge: grafo.incomingEdgesOf(d)) {
				risultatoPilota-=grafo.getEdgeWeight(edge);
			}
			if(risultatoPilota>max) {
				max=risultatoPilota;
				migliorPilota=d;
				risultato="Il miglior pilota nell'anno selezionato e: "+d.getDriverId()+" con un risultato di: "+max;
			}
		}
		return risultato;
	}
	
	


}
