package vistas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import interfaces.InterfazEntornoGrafico;

public class VentanaServidor extends JFrame implements InterfazEntornoGrafico{
	
	/**
	 * Version pedida por el IDE
	 */
	private static final long serialVersionUID = 1L;
	
	// Atributos //
	protected JButton btnConectar;
	protected JButton btnDesconectar;
	protected JButton btnSalir;
	protected JList <JLabel>listadoMensajes;
	protected JList <JLabel>listadoUsuarios;
	protected JList <JLabel>listadoSalas;
	
	private JScrollPane scrollMensajes;
	private JScrollPane scrollUsuarios;
	private JScrollPane scrollSalas;
	
	/**
	 * Constructor de la clase
	 */
	public VentanaServidor() {
		super();
		
		this.setTitle("Servidor Swing");
		// Creo el panel general
		JPanel pGeneral = new JPanel();
		pGeneral.setLayout(new BoxLayout(pGeneral, BoxLayout.Y_AXIS));
		this.add(pGeneral);
		
		
		//Creo un panelSuperior para los botones de conectar/desconectar
		JPanel pSuperior = new JPanel();
		pGeneral.add(pSuperior);
		
		// Creo un panel para los botones de conexion/desconexion
		JPanel pConexion = new JPanel();
		pSuperior.add(pConexion);
		
		// Agrego sus componentes
		this.btnConectar = new JButton("Conectar");
		this.btnDesconectar = new JButton("Desconectar");

		pConexion.add(this.btnConectar);
		pConexion.add(new JLabel("                "));
		pConexion.add(this.btnDesconectar);
		
		
		// Creo un panel para el resto de paneles en modo horizontal
		JPanel pHorizontal = new JPanel();
		pGeneral.add(pHorizontal);
		
		// Creo un panel para los mensajes
		JPanel pMensajes = new JPanel();
		pHorizontal.add(pMensajes);
		
		JPanel pMensajesVert = new JPanel();
		pMensajesVert.setLayout(new BoxLayout(pMensajesVert, BoxLayout.Y_AXIS));
		pMensajes.add(pMensajesVert);
		
		pMensajesVert.add(new JLabel("Mensajes"));
		this.listadoMensajes = new JList<JLabel>();
		this.scrollMensajes = new JScrollPane(this.listadoMensajes);
		this.scrollMensajes.setPreferredSize(new Dimension(200,300));
		pMensajesVert.add(this.scrollMensajes);
		
		// Creo un panel para los usuarios
		JPanel pUsuarios = new JPanel();
		pHorizontal.add(pUsuarios);
		JPanel pUsuariosVert = new JPanel();
		pUsuariosVert.setLayout(new BoxLayout(pUsuariosVert, BoxLayout.Y_AXIS));
		pUsuarios.add(pUsuariosVert);
		pUsuariosVert.add(new JLabel("Clientes conectados"));
		this.listadoUsuarios = new JList<JLabel>();
		this.scrollUsuarios = new JScrollPane(this.listadoUsuarios);
		this.scrollUsuarios.setPreferredSize(new Dimension(200,300));
		pUsuariosVert.add(this.scrollUsuarios);
		
		// Creo un panel para las salas y el boton de salir
		JPanel pSalas = new JPanel();
		pHorizontal.add(pSalas);
		JPanel pSalasVert = new JPanel();
		pSalasVert.setLayout(new BoxLayout(pSalasVert, BoxLayout.Y_AXIS));
		
		pSalas.add(pSalasVert);
		pSalasVert.add(new JLabel("Salas disponibles"));
		this.listadoSalas = new JList<JLabel>();
		this.scrollSalas = new JScrollPane(this.listadoSalas);
		this.scrollSalas.setPreferredSize(new Dimension(200,275));
		pSalasVert.add(this.scrollSalas);
		
		this.btnSalir = new JButton("Salir de la aplicación");
		this.btnSalir.setHorizontalAlignment(SwingConstants.CENTER);
		this.btnSalir.setAlignmentX(CENTER_ALIGNMENT);;
		pSalasVert.add(this.btnSalir);
		
		// Modificaciones finales del frame
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.setResizable(false);		
	}

	
	/**
	 * Funcion principal
	 * @param args
	 */
	public static void main(String[] args) {
		new VentanaServidor();
	}


	@Override
	public void escribirTexto(Object texto, boolean propio) {
		System.out.println("Debo escribir: " + texto);
		JLabel mensaje = new JLabel(texto.toString());
		if(propio) mensaje.setForeground(Color.green);
		else mensaje.setForeground(Color.blue);
		
		
		this.listadoMensajes.add(mensaje);
		this.listadoMensajes.repaint();
		
	}


	@Override
	public void limpiarAreaTexto() {
		this.listadoMensajes.removeAll();
		
	}
}
