package paquetes;

import java.io.Serializable;

/**
 * Clase para los envios por TCP
 * Llevara el nick de los clientes, y un booleano, que indicará si un nick
 * ya existente quiere desconectarse y que el servidor lo libere de su listado
 * 
 * Tambien la uso para enviar desde clientes conectados las salas nuevas que creen,
 * para que cuando llege uno nuevo, pueda recuperar el listado de salas
 * 
 * @author Jorge
 *
 */
public class PaqueteLogin implements Serializable{
	/**
	 * Sugerido por el IDE
	 */
	private static final long serialVersionUID = 1L;
	// Atributos
	private String nick; // El nick a comprobar
	private boolean desconectar; // Por si el usuario quiere desconectarse, o es aceptado por el server
	private PaqueteSala paqueteSalaNueva = null;
	
	// Constructor vacio
	public PaqueteLogin(){}

	// Constructor con el parametro
	public PaqueteLogin(String nick, boolean desconectar) {
		this.nick = nick;
		this.desconectar = desconectar;
		this.paqueteSalaNueva = null; // Ya estaba en la declaración, pero prefiero que quede claro visualmente
	}
	
	// Getters y Setters
	public PaqueteSala getPaqueteSalaNueva() {
		return paqueteSalaNueva;
	}

	public void setPaqueteSalaNueva(PaqueteSala paqueteSalaNueva) {
		this.paqueteSalaNueva = paqueteSalaNueva;
	}

	// Constructor para las salas creadas por clientes conectados
	public PaqueteLogin(PaqueteSala sala) {
		this.nick = "SALA";
		this.desconectar = false;
		this.paqueteSalaNueva = sala;
	}
	
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public boolean isDesconectar() {
		return desconectar;
	}

	public void setDesconectar(boolean desconectar) {
		this.desconectar = desconectar;
	}
	
	/*
	 * Para visualizar los datos
	 */
	@Override
	public String toString() {
		return "Nick: " + this.getNick() + "  Quiere desconectar: " + this.isDesconectar();
	}
}
