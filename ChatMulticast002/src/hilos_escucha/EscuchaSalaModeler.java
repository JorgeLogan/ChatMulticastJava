package hilos_escucha;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;

import clases.Cliente;
import clases.FuncionesConversion;
import clases.Sala;
import paquetes.PaqueteChat;
import paquetes.PaqueteSala;

/**
 * Clase EscuchaSalaModeler
 * Esta clase sirve para escuchar los mensajes que se suceden en una sala en particular
 * 
 * @author Jorge Alvarez Ceñal
 *
 */
public class EscuchaSalaModeler extends HiloEscucha{
	// Sala a escuchar
	private Sala sala = null; // La sala a escuchar
	private JList<String> jListado; // El JList usado. Necesario para posicionarlo al final de la lista
	private DefaultListModel<String> modeloMensajes; // El modelo necesario para colocar los mensajes recibidos
	private List<PaqueteSala> salasDisponibles; // Lleva el control de las salas disponibles en la sesion
	
	// Constructor con los elementos
	public EscuchaSalaModeler(Sala sala, JList<String> jListado, DefaultListModel<String> modeloM, 
			List<PaqueteSala> listaPaquetes) {
		
		this.sala = sala;
		this.jListado = jListado;
		this.modeloMensajes = modeloM;
		this.salasDisponibles = listaPaquetes;
	}
	
	/**
	 * Metodo para la escucha. Lo hago sincronizado para evitar problemas
	 */
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
		
		if(paquete.getMensaje().contains(" dice:") == false){
			mensaje = FuncionesConversion.cadenaHTML(paquete.getNombreUsuario(), paquete.getMensaje());	
		}else {
			mensaje = paquete.getMensaje();
		}
		
		if(paquete.getNuevaSala()!= null) {
			if(paquete.getBorrarSala() == false) {
				mensaje = FuncionesConversion.cadenaHTML(paquete.getNombreUsuario(), "SE HA CREADO UNA SALA NUEVA: " + paquete.getNuevaSala().toString());
				System.out.println("SE HA CREADO UNA SALA NUEVA: " + paquete.getNuevaSala().toString());
				this.salasDisponibles.add(paquete.getNuevaSala());								
			}
			else {
				this.salasDisponibles.remove(paquete.getNuevaSala());
				System.out.println("------------------------------------------borramos sala " + paquete.getNuevaSala().getNombre());
			}

		}
		
		System.out.println("Intentamos poner en el JList --> " + mensaje);
		this.modeloMensajes.addElement(mensaje);
		
		// Nos aseguramos de que el mensaje es visible en el listado
		jListado.ensureIndexIsVisible(this.modeloMensajes.size()-1);
		
		// Para no tener un modeler demasiado largo, si pasa cierto numero de mensajes, borramos el primero
		if(this.modeloMensajes.size() > 15) this.modeloMensajes.remove(0);
	}
	
	
	/**
	 * Metodo para poder cerrar el hilo, y no dejar el proceso abierto
	 */
	@Override
	public void cerrarHilo() {
		this.salirHilo = true;
		System.out.println("Hilo de escucha de sala cerrado");
	} 
}
