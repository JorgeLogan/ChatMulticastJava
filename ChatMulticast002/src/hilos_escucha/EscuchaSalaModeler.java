package hilos_escucha;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;

import clases.FuncionesConversion;
import clases.Sala;
import paquetes.PaqueteChat;

public class EscuchaSalaModeler extends HiloEscucha{
	// Sala a escuchar
	private Sala sala = null;
	private DefaultListModel<JLabel> modelo;
	
	public EscuchaSalaModeler(Sala sala, DefaultListModel<JLabel> modelo) {
		this.sala = sala;
		this.modelo = modelo;
	}
	
	@Override
	public void escucha() {
		// Nos ponemos a la escucha en la sala...
		PaqueteChat paquete = sala.escucharMensaje();
		
		// Recibimos el paquete! Lo muestro por consola, lo convierto a fomato HTML y lo paso al JLabel
		System.out.println("---------------> recibo " + paquete.getMensaje());
		String mensaje = FuncionesConversion.cadenaHTML(paquete.getNombreUsuario(), paquete.getMensaje());
		System.out.println("Intentamos poner en el JList --> " + mensaje);
		this.modelo.addElement(new JLabel(mensaje));
	}
	
	@Override
	public void cerrarHilo() {
		this.salirHilo = true;
		System.out.println("Hilo de escucha de sala cerrado");
	}
}
