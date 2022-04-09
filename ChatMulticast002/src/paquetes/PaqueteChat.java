package paquetes;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class PaqueteChat implements Serializable {
	/**
	 * ID serial propuesto por el IDE
	 */
	private static final long serialVersionUID = 1L;
	// Atributos del cliente
	private String nombreUsuario;
	private String mensaje;
	//private List<PaqueteSala> salasDisponibles = new LinkedList<PaqueteSala>();
	private PaqueteSala sala = null;
	private boolean borrarSala = false; // Puede ser que la sala quiera borrarse
	
	
	// Constructores
	public PaqueteChat() {}
	
	public PaqueteChat(String nombreUsuario, String mensaje) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.mensaje = mensaje;
	}
	/*
	public PaqueteChat(List<PaqueteSala> listado) {
		this.salasDisponibles = listado;
	}
	*/
	
	public PaqueteChat(String usuario, PaqueteSala sala, boolean borrarSala) {
		this.nombreUsuario = usuario;
		this.sala = sala;
		this.mensaje = "SALA";
		this.borrarSala = borrarSala;
	}
	
	// Getters y Setters
	public String getNombreUsuario() {
		return nombreUsuario;
	}
	
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public PaqueteSala getNuevaSala() {
		return sala;
	}

	public void setNuevaSala(PaqueteSala nuevaSala) {
		this.sala = nuevaSala;
	}
	
	public boolean getBorrarSala() {
		return this.borrarSala;
	}
	
	public void setBorrarSala(boolean b) {
		this.borrarSala = b;
	}
	
	
}
