package hilos_escucha;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;

import clases.FuncionesConversion;
import clases.Sala;
import paquetes.PaqueteChat;
import paquetes.PaqueteSala;

public class EscuchaSalaModeler extends HiloEscucha{
	// Sala a escuchar
	private Sala sala = null;
	private DefaultListModel<String> modeloMensajes;
	private List<PaqueteSala> salasDisponibles;
	
	public EscuchaSalaModeler(Sala sala, DefaultListModel<String> modeloM, List<PaqueteSala> listaPaquetes) {
		this.sala = sala;
		this.modeloMensajes = modeloM;
		this.salasDisponibles = listaPaquetes;
	}
	
	@Override
	public synchronized void escucha() {
		// Nos ponemos a la escucha en la sala...
		PaqueteChat paquete = sala.escucharMensaje();
		
		if(paquete == null) {
			System.out.println("ELPAQUETE RECIBIDO ES NULO. TODO NULO");
			return;
		}
		
		// Recibimos el paquete! Lo muestro por consola, lo convierto a fomato HTML y lo paso al JLabel..
		// A no ser que el mensaje sea SALA. En ese caso, solo informamos de que se recibe la sala
		System.out.println("---------------> recibo " + paquete.getMensaje());
		String mensaje = "";
		if(paquete.getMensaje().contains(" dice:") == false && paquete.getMensaje().contains("SALA") == false){
			mensaje = FuncionesConversion.cadenaHTML(paquete.getNombreUsuario(), paquete.getMensaje());	
		}else {
			if(paquete.getMensaje().contains("SALA"))
			mensaje = FuncionesConversion.cadenaHTML(paquete.getNombreUsuario(), "SE HA CREADO UNA SALA NUEVA: " + paquete.getNuevaSala().toString());
			System.out.println("SE HA CREADO UNA SALA NUEVA: " + paquete.getNuevaSala().toString());
			this.salasDisponibles.add(paquete.getNuevaSala());
		}
		
		System.out.println("Intentamos poner en el JList --> " + mensaje);
		this.modeloMensajes.addElement(mensaje);
		
		// Para no tener un modeler demasiado largo, si pasa cierto numero de mensajes, borramos el primero
		if(this.modeloMensajes.size() > 10) this.modeloMensajes.remove(0);
	}
	
	@Override
	public void cerrarHilo() {
		this.salirHilo = true;
		System.out.println("Hilo de escucha de sala cerrado");
	}
}
