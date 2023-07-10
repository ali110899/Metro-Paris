package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.Connessione;
import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {

	public List<Fermata> readFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	public List<Linea> readLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}
	
	public boolean isFermateConnesse(Fermata p, Fermata a) {
		
		String sql = "SELECT count(*) AS c "
				+"FROM connessione "
				+"WHERE id_stazP=? and id_stazA=?";


		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
	
			st.setInt(1, p.getIdFermata());
			st.setInt(2, a.getIdFermata());
			
			ResultSet res = st.executeQuery();
			res.first();
			
			//dove c è il numero di linee presenti tra le 2 fermate
			int c= res.getInt("c");
			
			st.close();
			conn.close();
			
			return c!=0;

		} catch (SQLException e) {
			e.printStackTrace();
			//throw new RuntimeException("Errore di connessione al Database.");
			return false;
		}

	}
	
	public List<Fermata> trovaCollegate(Fermata partenza, Map<Integer, Fermata> mappaFermate) {
		
		//lista di fermate arrivo corrispondenti alla fermaa di partenza
		String sql = "SELECT *"
				+ "FROM fermata "
				+ "WHERE id_fermata IN ( "
				+ "SELECT id_stazA "
				+ "FROM connessione "
				+ "WHERE id_stazP=? "
				+ "GROUP BY id_stazA)" ;

        List<Fermata> fermate = new ArrayList<Fermata>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
	
			st.setInt(1, partenza.getIdFermata());
			
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				Fermata f = new Fermata(res.getInt("id_fermata"), res.getString("nome"), new LatLng(res.getDouble("coordX"), res.getDouble("coordY")));
				fermate.add(f);
			}

			st.close();
			conn.close();
			return fermate;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
			
		}

		
	}
	

	public List<Fermata> trovaIdCollegate(Fermata partenza, Map<Integer, Fermata> mappaFermate) {
		
		//lista di Idfermate arrivo associate alla fermata partenza 
		String sql = "SELECT id_stazA "
				+ "FROM connessione "
				+ "WHERE id_stazP=? "
				+ "GROUP BY id_stazA";

        List<Fermata> fermate = new ArrayList<Fermata>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
	
			st.setInt(1, partenza.getIdFermata());
			
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				
				Integer idFermata = res.getInt("id_stazA");
				
				fermate.add(mappaFermate.get(idFermata));
			}

			st.close();
			conn.close();
			return fermate;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
			
		}

		
	}
	

}
