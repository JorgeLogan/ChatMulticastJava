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
	private PaqueteSala nuevaSala = null;
	
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
	
	public PaqueteChat(String usuario, PaqueteSala sala) {
		this.nombreUsuario = usuario;
		this.nuevaSala = sala;
		this.mensaje = "SALA";
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
		return nuevaSala;
	}

	public void setNuevaSala(PaqueteSala nuevaSala) {
		this.nuevaSala = nuevaSala;
	}
	
	
}
