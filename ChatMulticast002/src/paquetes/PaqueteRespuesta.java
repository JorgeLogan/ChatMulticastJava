package paquetes;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * El paquete respuesta enviará un mensaje, un booleano de aceptacion, y en caso
 * de ser aceptado, un listado de las salas actuales disponibles
 * @author Jorge
 *
 */
public class PaqueteRespuesta implements Serializable{
	/**
	 * Solicitado por el IDE
	 */
	private static final long serialVersionUID = 1L;
	
	// Atributos del mensaje
	private String mensaje;
	private boolean aceptado;
	private List<PaqueteSala> paquetesSala = new LinkedList<PaqueteSala>();
	
	// Constructor vacio
	public PaqueteRespuesta() {}
	
	// Constructor de la clase con los parametros
	public PaqueteRespuesta(String mensaje, boolean aceptado, List<PaqueteSala> paquetesSala) {
		super();
		this.mensaje = mensaje;
		this.aceptado = aceptado;
		this.paquetesSala = paquetesSala;
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
	public List<PaqueteSala> getPaquetesSala() {
		return this.paquetesSala;
	}
	public void setPaquetesSala(List<PaqueteSala> paquete) {
		this.paquetesSala = paquete;
	}
	
	@Override
	public String toString() {
		String salida = "Mensaje: " + this.getMensaje() + " Aceptado: " + this.isAceptado();
		if(this.paquetesSala!= null) {
			for(int i=0; i<this.paquetesSala.size(); i++)
			salida += "\n -->  Paquete: " + this.paquetesSala.get(i).toString() + "";
		}
		return salida;
	}
}
