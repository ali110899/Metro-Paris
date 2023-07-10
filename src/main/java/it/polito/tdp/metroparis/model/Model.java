package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	private Graph<Fermata, DefaultEdge> grafo;
	List<Fermata> fermate;
	private Map<Integer,Fermata> fermateIdMap;
	
	public void creaGrafo() {
		
		/*
		 * Grafo non orientato (linee sono bidirezionali)
		 * Grafo non pesato
		 * Grafo semplice
		 */
		//Creo oggetto grafo
		this.grafo= new SimpleGraph<Fermata,DefaultEdge>(DefaultEdge.class);
		//Aggiungo vertici
		MetroDAO dao = new MetroDAO();
		this.fermate = dao.readFermate();
		
		Graphs.addAllVertices(this.grafo, this.fermate);
		
		//Aggiungo archi
		/*
	
		//METODO 1
		//itero su tutte le fermate 
		for(Fermata partenza : this.grafo.vertexSet()) {
			for(Fermata arrivo : this.grafo.vertexSet()) {
				//chiedo a un altro metodo se sono connesse da un arco
				if(dao.isFermateConnesse(partenza, arrivo)==true) {
					this.grafo.addEdge(partenza, arrivo);
				}
			}
		}
		
		//METODO 2
		//Metodo più veloce(Query SQL complessa): itero solo 
		//sulle fermate che sono collegate a quella di partenza
		for(Fermata partenza : this.grafo.vertexSet()) {
			//NB la mappa deve essere vuota
			List<Fermata> collegate = dao.trovaCollegate(partenza, fermateIdMap);
		
			for(Fermata arrivo : collegate) {
				this.grafo.addEdge(partenza, arrivo);
			}
		}
		*/
		//METODO 3
		//Ancora più veloce(Query SQL semplificata): passo mappaIdFermate partenza 
		//e ritorno lista fermate arrivo associate a quella di partenza
		fermateIdMap = new HashMap<Integer, Fermata>();
		for(Fermata f : this.fermate) {
			this.fermateIdMap.put(f.getIdFermata(), f);
		}
		
		for(Fermata partenza : this.grafo.vertexSet()) {
			//NB la mappa deve essere vuota
			List<Fermata> collegate = dao.trovaIdCollegate(partenza, fermateIdMap);
		
			for(Fermata arrivo : collegate) {
				this.grafo.addEdge(partenza, arrivo);
			}
		}
		
		
		System.out.println("Grafo creato con "+this.grafo.vertexSet().size()+" vertici e "
				+this.grafo.edgeSet().size()+" linee\n");
		System.out.println(this.grafo);
	}
	
	//Determina il percorso minimo da fare
	public List<Fermata> percorso(Fermata partenza, Fermata arrivo) {
		
		//Visita il grafo partendo da "partenza"
		BreadthFirstIterator<Fermata, DefaultEdge> visita = new BreadthFirstIterator<>(this.grafo, partenza);  
		
		//Estraggo varie fermate e metto nella lista
		//List<Fermata> fermatePossibili = new ArrayList<Fermata>();
		while(visita.hasNext()) { //finchè ci sono fermate visitabili
			Fermata f = visita.next(); //copio fermata visitata
			//fermatePossibili.add(f); //aggiungo fermata visitata
		}
		//System.out.println(fermatePossibili);
		
		List<Fermata> percorso = new ArrayList<Fermata>();
		Fermata corrente = arrivo;
		percorso.add(arrivo); //devo aggiungere il vertice iniziale
		
		DefaultEdge e = visita.getSpanningTreeEdge(corrente);
		while(e!=null) {
		//faccio percorso all'indietro --> è univoca!
			Fermata precedente = Graphs.getOppositeVertex(this.grafo, e, corrente); //basta trovare vertice opposto!
			//aggiung le mie fermate partendo dall'ultima posizione
			percorso.add(0, precedente);
			//stampa lista al contrario: dal fondo all'inizio
			//percorso.add(precedente);
			corrente=precedente;
			
			e = visita.getSpanningTreeEdge(corrente);
		}		
		return  percorso;
	}
	
	public List<Fermata> getAllFrmate() {
		MetroDAO dao = new MetroDAO();
		List<Fermata> listaFermate = dao.readFermate();
		
		return listaFermate;
	}	
	
	public boolean isGrafoLoaded() {
		
		//controllo se ho qualche vertice nel grafo
		return this.grafo.vertexSet().size()>0;
	}
}
