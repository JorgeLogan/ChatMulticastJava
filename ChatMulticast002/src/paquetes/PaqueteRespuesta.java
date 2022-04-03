package paquetes;

import java.io.Serializable;

public class PaqueteRespuesta implements Serializable{
	/**
	 * Solicitado por el IDE
	 */
	private static final long serialVersionUID = 1L;
	
	// Atributos del mensaje
	private String mensaje;
	private boolean aceptado;
	private PaqueteSala paqueteSala;
	
	// Constructor vacio
	public PaqueteRespuesta() {}
	
	// Constructor de la clase con los parametros
	public PaqueteRespuesta(String mensaje, boolean aceptado, PaqueteSala paqueteSala) {
		super();
		this.mensaje = mensaje;
		this.aceptado = aceptado;
		this.paqueteSala = paqueteSala;
	}
	
	// Getters y Setters
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public boolean isAceptado() {
		return aceptado;
	}
	public void setAceptado(boolean aceptado) {
		this.aceptado = aceptado;
	}
	public PaqueteSala getPaqueteSala() {
		return this.paqueteSala;
	}
	public void setPaqueteSala(PaqueteSala paquete) {
		this.paqueteSala = paquete;
	}
	
}
