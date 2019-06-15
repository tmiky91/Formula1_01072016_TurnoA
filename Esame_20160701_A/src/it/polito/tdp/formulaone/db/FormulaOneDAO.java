package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.formulaone.model.Circuit;
import it.polito.tdp.formulaone.model.Constructor;
import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Season;


public class FormulaOneDAO {

	public List<Integer> getAllYearsOfRace() {
		
		String sql = "SELECT distinct year FROM races ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Integer> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(rs.getInt("year"));
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Season> getAllSeasons() {
		
		String sql = "SELECT year, url FROM seasons ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Season> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(new Season(Year.of(rs.getInt("year")), rs.getString("url"))) ;
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Circuit> getAllCircuits() {

		String sql = "SELECT circuitId, name FROM circuits ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Circuit> list = new ArrayList<>();
			while (rs.next()) {
				list.add(new Circuit(rs.getInt("circuitId"), rs.getString("name")));
			}

			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Constructor> getAllConstructors() {

		String sql = "SELECT constructorId, name FROM constructors ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Constructor> constructors = new ArrayList<>();
			while (rs.next()) {
				constructors.add(new Constructor(rs.getInt("constructorId"), rs.getString("name")));
			}

			conn.close();
			return constructors;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}


	public static void main(String[] args) {
		FormulaOneDAO dao = new FormulaOneDAO() ;
		
		List<Integer> years = dao.getAllYearsOfRace() ;
		System.out.println(years);
		
		List<Season> seasons = dao.getAllSeasons() ;
		System.out.println(seasons);

		
		List<Circuit> circuits = dao.getAllCircuits();
		System.out.println(circuits);

		List<Constructor> constructors = dao.getAllConstructors();
		System.out.println(constructors);
		
	}

	public void popolaGrafo(DefaultDirectedWeightedGraph<Driver, DefaultWeightedEdge> grafo, Integer year) {
		//QUERY CHE NON GESTISCE I NULL(COME DA TESTO)
		final String sql=	"select r1.raceId as idR, r1.driverId as idP1, r1.position as p1, r2.driverId as idP2, r2.position as p2, count(*) as cnt " + 
							"from results as r1, results as r2, races as r " + 
							"where r1.raceId=r2.raceId " + 
							"and r.raceId=r1.raceId " + 
							"and r.raceId=r2.raceId " + 
							"and r1.driverId != r2.driverId " + 
							"and r1.position < r2.position " + 
							"and r1.position is not null " + 
							"and r2.position is not null " + 
							"and r.year=? " + 
							"group by r1.driverId, r2.driverId";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, year);
			ResultSet rs = st.executeQuery();

			
			while (rs.next()) {
				Driver d1 = new Driver(rs.getInt("idP1"));
				Driver d2 = new Driver(rs.getInt("idP2"));
				int peso = rs.getInt("cnt");
				
				if(!grafo.containsVertex(d1)) {
					grafo.addVertex(d1);
				}
				if(!grafo.containsVertex(d2)) {
					grafo.addVertex(d2);
				}
				if(!grafo.containsEdge(d1, d2) && !grafo.containsEdge(d2, d1)){
					DefaultWeightedEdge arco = grafo.addEdge(d1, d2);
					grafo.setEdgeWeight(arco, peso);
				}
//				if(!grafo.containsEdge(d1, d2) && !grafo.containsEdge(d2, d1)) {
//					DefaultWeightedEdge arco = grafo.addEdge(d1, d2);
//					DefaultWeightedEdge arco2 = grafo.addEdge(d2, d1);
//					grafo.setEdgeWeight(arco, peso);
//					grafo.setEdgeWeight(arco2, peso);
//				}
//				else if(grafo.containsEdge(d1, d2)){
//					DefaultWeightedEdge arco = grafo.getEdge(d1, d2);
//					grafo.setEdgeWeight(arco, peso);
//				}
//				else {
//					DefaultWeightedEdge arco = grafo.getEdge(d2, d1);
//					grafo.setEdgeWeight(arco, peso);
//				}
			}

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}	
	}

	
	//QUERY CHE GESTISCE I NULL (DA CONTROLLARE)
//	select r1.raceId as idR, r1.driverId as idP1, r1.position as p1, r2.driverId as idP2, r2.position as p2, count(*) as cnt
//	from results as r1, results as r2, races as r
//	where r1.raceId=r2.raceId
//	and r.raceId=r1.raceId
//	and r.raceId=r2.raceId
//	and r1.driverId != r2.driverId
//	and r1.position < r2.position 
//	and r1.position = case
//	when r1.position = "NULL"
//	then 50
//	else r1.position
//	end
//	and r2.position = case
//	when r2.position = "NULL"
//	then 50
//	else r2.position
//	end
//	and r.year=2000
//	group by r1.driverId, r2.driverId
}
