package vistas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import interfaces.InterfazEntornoGrafico;
import paquetes.PaqueteSala;


public abstract class VentanaCliente extends JFrame implements InterfazEntornoGrafico<String>,  ActionListener, WindowListener{
	/**
	 * Agregado por el IDE
	 */
	private static final long serialVersionUID = 1L;
	protected JTextField txtNick;
	protected JTextField txtMensaje;
	protected JButton btnEnviarNick;
	protected JButton btnEnviarMensaje;
	protected JButton btnDesconectar;
	protected JButton btnSalir;
	
	protected JButton btnCrearSala;
	protected JButton btnUnirseSala;
	protected JButton btnBorrarSala;
	
	protected JList <String> listadoMensajes;
	//protected JList <String> listadoSalas;
	protected JList <String> listadoSalas;
	
	protected DefaultListModel<String> modeloMensajes = new DefaultListModel<String>();
	protected DefaultListModel<String> modeloSalas = new DefaultListModel<String>();
	
	/**
	 * Constructor de la clase
	 */
	public VentanaCliente() {
		super();
		this.configurarInterfaz();
	}
	
	/**
	 * Funcion para configurar la interfaz
	 */
	void configurarInterfaz() {
		this.setTitle("Cliente Java Swing");
		// Creo el panel general que lo contiene todo
		JPanel pGeneral = new JPanel();
		this.add(pGeneral);
		
		// Creo un panel para el chat. LLevara el panel de nick, el listado, y el panel de mensajes
		JPanel pChat = new JPanel();
		pChat.setLayout(new BoxLayout(pChat, BoxLayout.Y_AXIS));
		pGeneral.add(pChat);
		
		// Creo el panel de nick
		JPanel pNick = new JPanel();
		pNick.add(new JLabel("Introduce tu Nick"));
		this.txtNick = new JTextField();
		this.txtNick.setColumns(15);
		this.btnEnviarNick = new JButton("Enviar nick");
		this.btnEnviarNick.addActionListener(this);
		pNick.add(this.txtNick);
		pNick.add(this.btnEnviarNick);
		pChat.add(pNick);
		
		// Creo el listado y su scroll
		this.listadoMensajes = new JList<String>();
		this.listadoMensajes.setModel(this.modeloMensajes);
		JScrollPane scroll = new JScrollPane(this.listadoMensajes);
		scroll.setMinimumSize(new Dimension(300,200));
		scroll.revalidate();
		pChat.add(scroll);
		
		// Creo el panel horizontal para el mensaje y su boton de enviar
		JPanel pMensaje = new JPanel();
		this.txtMensaje = new JTextField();
		this.txtMensaje.setColumns(20);
		this.btnEnviarMensaje = new JButton("Enviar mensaje");
		this.btnEnviarMensaje.addActionListener(this);
		pMensaje.add(txtMensaje);
		pMensaje.add(btnEnviarMensaje);
		pChat.add(pMensaje);
		
		
		// Ahora en el lado central, pondremos las salas
		JPanel pSalas = new JPanel();
		pSalas.setBackground(Color.orange);
		pSalas.setBorder(BorderFactory.createEmptyBorder(2,4,2,4));
		pSalas.setLayout(new BoxLayout(pSalas, BoxLayout.Y_AXIS));
		pGeneral.add(pSalas);
		
		pSalas.add(new JLabel("Salas Disponibles"));
		this.listadoSalas = new JList<String>();
		this.listadoSalas.setModel(this.modeloSalas);
		JScrollPane scrollSalas = new JScrollPane(this.listadoSalas);
		scrollSalas.setPreferredSize(new Dimension(100,200));
		pSalas.add(scrollSalas);
		
		// Luego ponemos el resto de botones
		JPanel pBotones = new JPanel();
		pBotones.setLayout(new BoxLayout(pBotones, BoxLayout.Y_AXIS));
		pGeneral.add(pBotones);
		
		pBotones.add(new JLabel(""));
		
		this.btnCrearSala = new JButton(" Crear sala ");
		this.btnCrearSala.setActionCommand("Crear sala");
		this.btnCrearSala.addActionListener(this);
		pBotones.add(this.btnCrearSala);
		
		pBotones.add(new JLabel (" "));
		
		this.btnUnirseSala = new JButton("     Unirse    ");
		this.btnUnirseSala.setActionCommand("Unirse");
		this.btnUnirseSala.addActionListener(this);
		pBotones.add(this.btnUnirseSala);
		
		pBotones.add(new JLabel(" "));
		
		this.btnBorrarSala = new JButton("     Borrar    ");
		this.btnBorrarSala.setActionCommand("Borrar");
		this.btnBorrarSala.addActionListener(this);
		pBotones.add(this.btnBorrarSala);

		pBotones.add(new JLabel(" "));
		pBotones.add(new JLabel(" "));
		this.btnDesconectar = new JButton("Desconectar");
		this.btnDesconectar.addActionListener(this);
		pBotones.add(this.btnDesconectar);
		pBotones.add(new JLabel(" "));
		
		this.btnSalir = new JButton("         Salir       ");
		this.btnSalir.setActionCommand("Salir");
		this.btnSalir.addActionListener(this);
		pBotones.add(this.btnSalir);
		
		// Establecemos los valores del JFrame
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.setResizable(false);
		
		// Agregamos el escuchador de ventana
		this.addWindowListener(this);
	}
	
	/**
	 * Metodo de la interfaz ActionListener para escuchar los botones
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Boton pulsado " + e.getActionCommand());
		
		switch(e.getActionCommand().toString()) {
			case "Enviar nick" :
				System.out.println("Enviar nick pulsado");
				this.enviarNick(this.txtNick.getText().toString());
					
				break;
			case "Enviar mensaje" :
				System.out.println("Enviar mensaje pulsado");
				if(this.txtMensaje.getText().length() > 0) {
					String cadena = this.txtMensaje.getText().trim();
					this.enviarMensaje(cadena);				
				}

				break;
			case "Crear sala" :
				System.out.println("Crear sala pulsado");
				this.crearSala();
				break;
			case "Unirse" :
				System.out.println("Unirse pulsado");
				this.unirseSala(this.listadoSalas.getSelectedValue());
				break;
		
			case "Borrar" :
				System.out.println("Borrar pulsado");
				this.borrarSala(this.listadoSalas.getSelectedValue());
				break;
			case "Desconectar" :
				System.out.println("Desconectar pulsado");
				clickDesconectar();
				break;
			case "Salir" :
				System.out.println("Salir pulsado");
				salir();
				break;
		}
		
	}
	
	public abstract void enviarNick(String nickEnvio);
	public abstract void enviarMensaje(String nickEnvio);
	public abstract void crearSala();
	public abstract void unirseSala(String valor);
	public abstract void borrarSala(String valor);
	public abstract void clickDesconectar();
	public abstract void salir();

	/*****************************************************************************************************
	 * Funcion de la interfaz InterfazEntornoGrafico
	 * @param texto el texto a escribir
	 * @param propio indica si el mensaje a escribir lo crea el propio usuario
	 */
	@Override
	public void escribirTexto(String texto, boolean propio) {
		// TODO Auto-generated method stub
		
	}

	/******************************************************************************************************
	 * Funcion para limpiar el area de mensajes
	 */
	@Override
	public void limpiarAreaTexto() {
		this.listadoMensajes.removeAll();	
	}
	
	/******************************************************************************************
	 * 
	 * Metodos vacios para el listener de ventanas
	 * 
	 * ****************************************************************************************
	 */
	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
}
