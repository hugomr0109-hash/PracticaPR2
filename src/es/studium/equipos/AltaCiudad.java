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
import java.sql.SQLException;

public class AltaCiudad extends WindowAdapter implements ActionListener {
    // Componentes de la interfaz
    Frame ventana = new Frame("Alta de Ciudad");
    
    Label lblNombre = new Label("Nombre de la Ciudad:");
    TextField txtNombre = new TextField(20);
    
    Label lblPais = new Label("País:");
    TextField txtPais = new TextField(20);
    
    Button btnAceptar = new Button("Aceptar");
    Button btnLimpiar = new Button("Limpiar");
    
    // Dialog para mensajes de feedback
    Dialog dlgMensaje = new Dialog(ventana, "Aviso", true);
    Label lblMensaje = new Label("");
    Button btnOk = new Button("Ok");

    // Datos de conexión según tu script SQL
    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/equipos";
    String login = "root";
    String password = "Hugo_2007";

    public AltaCiudad() {
        ventana.setLayout(new FlowLayout());
        ventana.setSize(350, 220);
        ventana.addWindowListener(this);

        // Añadimos los componentes al Frame
        ventana.add(lblNombre);
        ventana.add(txtNombre);
        ventana.add(lblPais);
        ventana.add(txtPais);
        ventana.add(new Label("                 "));
        ventana.add(btnAceptar);
        ventana.add(btnLimpiar);

        // Listeners de los botones
        btnAceptar.addActionListener(this);
        btnLimpiar.addActionListener(this);

        // Configuración del Dialog de respuesta
        dlgMensaje.setLayout(new FlowLayout());
        dlgMensaje.setSize(200, 100);
        btnOk.addActionListener(this);
        dlgMensaje.add(lblMensaje);
        dlgMensaje.add(btnOk);
        dlgMensaje.setResizable(false);
        dlgMensaje.setLocationRelativeTo(null);

        ventana.setResizable(false);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnAceptar)) {
            insertarCiudad();
        } else if (e.getSource().equals(btnLimpiar)) {
            limpiar();
        } else if (e.getSource().equals(btnOk)) {
            dlgMensaje.setVisible(false);
        }
    }

    private void insertarCiudad() {
        // Fase 6: Uso de PreparedStatement para insertar en la tabla ciudades
        String sentenciaSQL = "INSERT INTO ciudades (nombreCiudad, paisCiudad) VALUES (?, ?)";
        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            Class.forName(driver);
            conexion = DriverManager.getConnection(url, login, password);
            ps = conexion.prepareStatement(sentenciaSQL);
            
            // Asignación de parámetros (?) basados en los campos de texto
            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtPais.getText());

            ps.executeUpdate();
            
            lblMensaje.setText("Ciudad guardada con éxito");
            dlgMensaje.setVisible(true);
            limpiar();
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error Driver: " + cnfe.getMessage());
        } catch (SQLException sqle) {
            lblMensaje.setText("Error al guardar");
            dlgMensaje.setVisible(true);
            System.err.println("Error SQL: " + sqle.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtPais.setText("");
        txtNombre.requestFocus();
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (dlgMensaje.isActive()) {
            dlgMensaje.setVisible(false);
        } else {
            ventana.dispose();
        }
    }

    public static void main(String[] args) {
        new AltaCiudad();
    }
}