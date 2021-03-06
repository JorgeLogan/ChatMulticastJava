package paquetes;

import java.io.Serializable;

public class PaqueteSala implements Serializable{

	/**
	 * Solicitado por el IDE
	 */
	private static final long serialVersionUID = 1L;

	// Atributos para la sala
	private String creador; // Para eliminarla, solo puede su creador
	private String nombre;
	private String grupo;
	private String ipRemota;
	private int puerto;
	private int tamMaxBuffer;
	
	// Constructor vacio
	public PaqueteSala() {}
	
	// Constructor con parametros
	public PaqueteSala(String creador, String nombre, String grupo, String ipRemota, int puerto, int tamMaxBuffer) {
		super();
		this.creador = creador;
		this.nombre = nombre;
		this.grupo = grupo;
		this.ipRemota = ipRemota;
		this.puerto = puerto;
		this.tamMaxBuffer = tamMaxBuffer;
	}

	// Getters y Setters
	public String getCreador() {
		return creador;
	}

	public void setCreador(String creador) {
		this.creador = creador;
	}
	
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

	@Override
	public String toString() {
		return "Nombre: " + this.getNombre() + " IP Remota: " + this.getIpRemota() + " Direccion: " + this.getGrupo()
			+ " Puerto: " + this.getPuerto() + " Tam maximo: " + this.getTamMaxBuffer();
	}
}
