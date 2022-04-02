package paquetes;

import java.io.Serializable;

public class PaqueteChat implements Serializable {
	/**
	 * ID serial propuesto por el IDE
	 */
	private static final long serialVersionUID = 1L;
	// Atributos
	private String nombreUsuario;
	private String mensaje;	
	
	// Constructores
	public PaqueteChat() {}
	
	public PaqueteChat(String nombreUsuario, String mensaje) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.mensaje = mensaje;
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
}
