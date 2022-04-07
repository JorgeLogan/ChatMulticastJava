package hilos_escucha;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;

import clases.FuncionesConversion;
import clases.Sala;
import paquetes.PaqueteChat;

public class EscuchaSalaModeler extends HiloEscucha{
	// Sala a escuchar
	private Sala sala = null;
	private DefaultListModel<String> modelo;
	
	public EscuchaSalaModeler(Sala sala, DefaultListModel<String> modelo) {
		this.sala = sala;
		this.modelo = modelo;
	}
	
	@Override
	public void escucha() {
		// Nos ponemos a la escucha en la sala...
		PaqueteChat paquete = sala.escucharMensaje();
		
		// Recibimos el paquete! Lo muestro por consola, lo convierto a fomato HTML y lo paso al JLabel
		System.out.println("---------------> recibo " + paquete.getMensaje());
		String mensaje;
		if(paquete.getMensaje().contains(" dice:") == false){
			mensaje = FuncionesConversion.cadenaHTML(paquete.getNombreUsuario(), paquete.getMensaje());	
		}else {
			mensaje = paquete.getMensaje();
		}
		
		System.out.println("Intentamos poner en el JList --> " + mensaje);
		this.modelo.addElement(mensaje);
		
		// Para no tener un modeler demasiado largo, si pasa cierto numero de mensajes, borramos el primero
		if(this.modelo.size() > 10) this.modelo.remove(0);
	}
	
	@Override
	public void cerrarHilo() {
		this.salirHilo = true;
		System.out.println("Hilo de escucha de sala cerrado");
	}
}
