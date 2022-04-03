package hilos_escucha;

import javax.swing.DefaultListModel;

import clases.Sala;
import paquetes.PaqueteChat;

public class EscuchaSalaModeler extends HiloEscucha{
	// Sala a escuchar
	private Sala sala = null;
	private DefaultListModel modelo;
	
	public EscuchaSalaModeler(Sala sala, DefaultListModel modelo) {
		this.sala = sala;
		this.modelo = modelo;
	}
	
	@Override
	public void escucha() {
		
		PaqueteChat paquete = sala.escucharMensaje();
		String mensaje = paquete.getNombreUsuario() + "\n\t" + paquete.getMensaje();
		this.modelo.addElement(mensaje);
	}

}
