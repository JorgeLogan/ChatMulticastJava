package paquetes;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

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
	
	/**
	 * La idea en este paquete tambien es incluir un listado de las salas disponibles
	 * Cuando se envie este mensaje, se emitira en un mensaje solo para las salas disponibles
	 * Dependiendo del tamaño que vea en las pruebas del paquete de solo salas, igual envio todo
	 * en el mismo paquete, o igual envio 2 seguidos, uno con mensaje del cliente, y otro con sus
	 * salas, aun no lo tengo seguro. L verdad es que me parece que me estoy pasando enviando datos,
	 * pero no se me ocurre otra forma
	 */
	private List<PaqueteSala> salasDisponibles = new LinkedList<PaqueteSala>();
	
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
