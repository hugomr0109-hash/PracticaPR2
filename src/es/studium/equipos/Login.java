package es.studium.equipos;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends WindowAdapter implements ActionListener
{
	Frame ventana = new Frame("Login");
	
	Label lblUsuario = new Label("Usuario:");
	TextField txtUsuario = new TextField(15);
	
	Label lblClave = new Label("Clave:");
	TextField txtClave = new TextField(15);
	
	Button btnAceptar = new Button("Aceptar");
	Button btnLimpiar = new Button("Limpiar");

	Dialog dlgMensaje = new Dialog(ventana, "Aviso", true);
	Label lblMensaje = new Label("                                       ");
	Button btnCerrarMensaje = new Button("OK");

	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/equipos";
	String login = "root";
	String password = "Hugo_2007";
	String sentenciaSQL = "SELECT tipoUsuario FROM usuarios WHERE nombreUsuario = ? AND claveUsuario = ?";

	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet rs = null;

	public Login()
	{
		ventana.setLayout(new FlowLayout());
		ventana.setSize(240, 160);
		ventana.addWindowListener(this);
		
		txtClave.setEchoChar('*');

		btnAceptar.addActionListener(this);
		btnLimpiar.addActionListener(this);

		ventana.add(lblUsuario);
		ventana.add(txtUsuario);
		ventana.add(lblClave);
		ventana.add(txtClave);
		ventana.add(btnAceptar);
		ventana.add(btnLimpiar);
		
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);

		dlgMensaje.setLayout(new FlowLayout());
		dlgMensaje.setSize(200, 100);
		dlgMensaje.addWindowListener(this);
		btnCerrarMensaje.addActionListener(this);
		dlgMensaje.add(lblMensaje);
		dlgMensaje.add(btnCerrarMensaje);
		dlgMensaje.setResizable(false);
		dlgMensaje.setLocationRelativeTo(null);

		ventana.setVisible(true);
	}

	public static void main(String[] args)
	{
		new Login();
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		if(dlgMensaje.isActive())
		{
			dlgMensaje.setVisible(false);
		}
		else
		{
			// Salir
			System.exit(0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent evento)
	{
		if (evento.getSource().equals(btnLimpiar))
		{
			txtUsuario.setText("");
			txtClave.setText("");
			txtUsuario.requestFocus();
		}
		else if(evento.getSource().equals(btnCerrarMensaje))
		{
			dlgMensaje.setVisible(false);
		}
		else if (evento.getSource().equals(btnAceptar))
		{
			try
			{
				Class.forName(driver);
				connection = DriverManager.getConnection(url, login, password);
				System.out.println("Conexión establecida para Login");

				statement = connection.prepareStatement(sentenciaSQL);
				statement.setString(1, txtUsuario.getText());
				statement.setString(2, txtClave.getText());
				
				rs = statement.executeQuery();

				if (rs.next())
				{
					String tipoUsuario = rs.getString("tipoUsuario");
					System.out.println("Login correcto. Perfil: " + tipoUsuario);
					String tipo = rs.getString("tipoUsuario");
				    new MenuPrincipal(tipo);
				    ventana.dispose();
					
					
					ventana.dispose();
				}
				else
				{
					lblMensaje.setText("Usuario o clave incorrecta.");
					dlgMensaje.setVisible(true);
				}
			}
			catch(ClassNotFoundException cnfe)
			{
				System.err.println("Error de driver");
			}
			catch(SQLException se)
			{
				System.err.println("Error de conexión: url, usuario o clave");
				lblMensaje.setText("Error en la Base de Datos.");
				dlgMensaje.setVisible(true);
			}
			finally
			{
				try
				{
					if(rs != null) {
						rs.close();
					}
					if(statement != null) {
						statement.close();
					}
					if(connection != null) {
						connection.close();
					}
				}
				catch(SQLException e)
				{
					System.err.println("Error al cerrar conexión");
				}
			}
		}
	}
}