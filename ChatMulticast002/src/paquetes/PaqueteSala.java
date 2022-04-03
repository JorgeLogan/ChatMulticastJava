package paquetes;

import java.io.Serializable;

public class PaqueteSala implements Serializable{

	/**
	 * Solicitado por el IDE
	 */
	private static final long serialVersionUID = 1L;

	
	// Atributos para la sala
	private String nombre;
	private String grupo;
	private String ipRemota;
	private int puerto;
	private int tamMaxBuffer;
	
	// Constructor vacio
	public PaqueteSala() {}
	
	// Constructor con parametros
	public PaqueteSala(String nombre, String grupo, String ipRemota, int puerto, int tamMaxBuffer) {
		super();
		this.nombre = nombre;
		this.grupo = grupo;
		this.ipRemota = ipRemota;
		this.puerto = puerto;
		this.tamMaxBuffer = tamMaxBuffer;
	}

	// Getters y Setters
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getIpRemota() {
		return ipRemota;
	}

	public void setIpRemota(String ipRemota) {
		this.ipRemota = ipRemota;
	}

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

	public int getTamMaxBuffer() {
		return tamMaxBuffer;
	}

	public void setTamMaxBuffer(int tamMaxBuffer) {
		this.tamMaxBuffer = tamMaxBuffer;
	}	
}
