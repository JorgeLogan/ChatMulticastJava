package vistas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public abstract  class VentanaServidor extends JFrame implements  ActionListener, WindowListener{
	
	/**
	 * Version pedida por el IDE
	 */
	private static final long serialVersionUID = 1L;
	
	// Atributos //
	protected JButton btnConectar;
	protected JButton btnDesconectar;
	protected JButton btnSalir;
	protected JList <String>listadoMensajes;
	protected JList <String>listadoUsuarios;
	
	// Para agregar elementos a los listados usamos modelos
	protected DefaultListModel<String> modeloMensajes;
	protected DefaultListModel<String> modeloUsuarios;
	
	private JScrollPane scrollMensajes;
	private JScrollPane scrollUsuarios;
	
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
		pConexion.setAlignmentX(CENTER_ALIGNMENT);
		// Agrego sus componentes
		this.btnConectar = new JButton("Conectar");
		this.btnDesconectar = new JButton("Desconectar");

		pConexion.add(this.btnConectar);
		pConexion.add(new JLabel("                                      "));
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
		// Cremo el panel de mensajes
		this.listadoMensajes = new JList<String>();
		this.modeloMensajes = new DefaultListModel<String>();
		this.listadoMensajes.setModel(modeloMensajes);
		this.scrollMensajes = new JScrollPane(this.listadoMensajes);
		this.scrollMensajes.setPreferredSize(new Dimension(500,300));
		pMensajesVert.add(this.scrollMensajes);
		
		// Creo un panel para los usuarios
		JPanel pUsuarios = new JPanel();
		pHorizontal.add(pUsuarios);
		
		// En el panel de los usuarios tendremos en vertical su listado y el boton de salir
		JPanel pUsuariosVert = new JPanel();
		pUsuariosVert.setLayout(new BoxLayout(pUsuariosVert, BoxLayout.Y_AXIS));
		pUsuarios.add(pUsuariosVert);
		pUsuariosVert.add(new JLabel("Usuarios"));
		this.listadoUsuarios = new JList<String>();
		this.modeloUsuarios = new DefaultListModel<String>();
		this.listadoUsuarios.setModel(modeloUsuarios);
		this.scrollUsuarios = new JScrollPane(this.listadoUsuarios);
		this.scrollUsuarios.setPreferredSize(new Dimension(200,270));
		pUsuariosVert.add(this.scrollUsuarios);
			
		this.btnSalir = new JButton("Salir de la aplicación");
		this.btnSalir.setHorizontalAlignment(SwingConstants.CENTER);
		this.btnSalir.setAlignmentX(CENTER_ALIGNMENT);;
		pUsuariosVert.add(this.btnSalir);
		
		// Damos funcionalidad a los botones
		this.btnConectar.addActionListener(this);
		this.btnDesconectar.addActionListener(this);
		this.btnSalir.addActionListener(this);
		
		// Modificaciones finales del frame
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.setResizable(false);		
	
		// Damos el listener de ventanas
		this.addWindowListener(this);
	}
	
	// Metodos a implementar en la clase final.
	public abstract void clickConectar();
	public abstract void clickDesconectar();
	public abstract void clickSalir();

	


	
	//**********************************************************************************************
	//
	//		METODOS IMPLEMENTADOS PARA LA INTERFAZ DEL MANEJO DE VENTANAS
	//		Solo necesito uno, para que el cierre se haga correctamente
	//
	//**********************************************************************************************
	
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

	//**********************************************************************************************
	//
	//		METODOS IMPLEMENTADOS PARA LA INTERFAZ DE ESCUCHA DE EVENTOS (botones)
	//
	//**********************************************************************************************
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "Conectar":
			this.clickConectar();
			break;
		case "Desconectar":
			this.clickDesconectar();
			break;
		case "Salir de la aplicación":
			this.clickSalir();
			break;
		}
	}
}
