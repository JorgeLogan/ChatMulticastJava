package clases;

import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

import hilos_escucha.EscuchaSalaModeler;
import interfaces.InterfazConexion;
import paquetes.PaqueteSala;
import vistas.VentanaServidor;

public class Servidor extends VentanaServidor {
	/**
	 * Solicitado por el IDE
	 */
	private static final long serialVersionUID = 1L;
	
	// Atributos
	private String nick = "Servidor";
	private Sala agora = null;
	private EscuchaSalaModeler escuchaAgora = null;	// Para la escucha de mensajes de la sala Agora
	private Login login= null;	// Para el control del login de los clientes
	private final String direccionGrupo = "225.2.3.4";
	
	// Constructor de la clase
	public Servidor() {
		super();
		this.gestionControles(false);
	}
	
	/**
	 * Funcion para hacer ejecutable la clase Servidor
	 * @param args no se usará ningún parámetro
	 */
	public static void main(String[] args) {
		new Servidor();
	}

	@Override
	public void clickConectar() {
		/* Conectar lleva varios pasos:
			Crear una sala Multicast llamada Agora
			Ponerse a escucha de clientes por TCP --> Hilo 1
			Si éstos son aceptados, pasarles un paquete con la información para la sala
			Ademas, necesitamos estar a la escucha de nuestra propia sala --> Hilo 2
		*/
		System.out.println("Pulsado conectar");
		// Creamos la sala
		this.agora = new Sala(this.nick ,InterfazConexion.HOST, this.direccionGrupo, "Sala Agora", 5678, 1000);
	
		// Escuchamos la sala Agora en el listado de mensajes a traves del modelo
		this.escuchaAgora = new EscuchaSalaModeler(this.agora, this.modeloMensajes);
		
		// Iniciamos el hilo de escucha
		this.escuchaAgora.start();
		
		// Ahora nos preparamos para la escucha de clientes con el objeto de login, que se pondra a la escucha de clientes
		// Preparo el listado de salas
		List<PaqueteSala> salas = new LinkedList<PaqueteSala>();
		salas.add(this.agora.getPaqueteSala());
		this.login = new Login(salas, InterfazConexion.PUERTO_TCP, this.modeloUsuarios);

		// Ponemos los controles en modo conectado
		this.gestionControles(true);
	}

	@Override
	public void clickDesconectar() {
		System.out.println("Pulsado desconectar");
		
		// Limpiamos los mensajes y los usuarios (se acabó lo que se daba)
		this.modeloMensajes.clear();
		this.modeloUsuarios.clear();
			
		// Cerramos la sala agora nada mas podamos
		if(agora!= null) {
			System.out.println("Cerrando sala Agora...");
			agora.cerrarSala();
		}
		
		if(escuchaAgora!= null) {
			System.out.println("Cerrando hilo de escucha de la sala Agora en el Servidor...");
			escuchaAgora.cerrarHilo();
		}
		
		
		if(login!= null) {
			System.out.println("Cerrando hilo login....");
			login.cerrarHilo();
		}
		
		// Ponemos los controles en modo desconectaco
		this.gestionControles(false);
	}

	/**
	 * Funcion para el click del boton de Salir
	 */
	@Override
	public void clickSalir() {
		System.out.println("Pulsado salir... desconectando...");
		this.clickDesconectar();
		this.dispose();
	}

	/**
	 * Funcion para gestionar los controles según la conexión
	 * @param conectado indica si es para modo conectado o desconectado
	 */
	private void gestionControles(boolean conectado) {
		this.btnConectar.setEnabled(!conectado);
		this.btnDesconectar.setEnabled(conectado);
	}
	
	/**
	 * Para la gestion del cierre de ventana
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		System.out.println("Cerrando ventana servidor");
		this.clickDesconectar();
	}
}
