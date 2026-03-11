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

public class AltaEquipo extends WindowAdapter implements ActionListener {
    // Componentes de la interfaz
    Frame ventana = new Frame("Alta de Equipo");
    
    Label lblNombre = new Label("Nombre del Equipo:");
    TextField txtNombre = new TextField(20);
    
    Label lblCategoria = new Label("Categoría:");
    TextField txtCategoria = new TextField(20);
    
    Label lblEstadio = new Label("Nombre del Estadio:");
    TextField txtEstadio = new TextField(20);
    
    Button btnAceptar = new Button("Aceptar");
    Button btnLimpiar = new Button("Limpiar");
    
    // Dialog para mensajes de feedback (Fase 6)
    Dialog dlgMensaje = new Dialog(ventana, "Aviso", true);
    Label lblMensaje = new Label("");
    Button btnOk = new Button("Ok");

    // Datos de conexión según tu script SQL
    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/equipos";
    String login = "root";
    String password = "Hugo_2007";

    public AltaEquipo() {
        ventana.setLayout(new FlowLayout());
        ventana.setSize(250, 280);
        ventana.addWindowListener(this);

        // Añadimos los componentes
        ventana.add(lblNombre);
        ventana.add(txtNombre);
        ventana.add(lblCategoria);
        ventana.add(txtCategoria);
        ventana.add(lblEstadio);
        ventana.add(txtEstadio);
        
        ventana.add(btnAceptar);
        ventana.add(btnLimpiar);

        // Listeners
        btnAceptar.addActionListener(this);
        btnLimpiar.addActionListener(this);

        // Configuración del Dialog
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
            insertarEquipo();
        } else if (e.getSource().equals(btnLimpiar)) {
            limpiar();
        } else if (e.getSource().equals(btnOk)) {
            dlgMensaje.setVisible(false);
        }
    }

    private void insertarEquipo() {
        // Fase 6: Uso obligatorio de PreparedStatement
        String sentenciaSQL = "INSERT INTO equipos (nombreEquipo, categoriaEquipo, nombreEstadioEquipo) VALUES (?, ?, ?)";
        Connection conexion = null;
        PreparedStatement ps = null;

        try {
            Class.forName(driver);
            conexion = DriverManager.getConnection(url, login, password);
            ps = conexion.prepareStatement(sentenciaSQL);
            
            // Asignación de parámetros (?)
            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtCategoria.getText());
            ps.setString(3, txtEstadio.getText());

            ps.executeUpdate();
            
            lblMensaje.setText("Equipo guardado con éxito");
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
        txtCategoria.setText("");
        txtEstadio.setText("");
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

    // Para probar la ventana de forma independiente si quieres
    public static void main(String[] args) {
        new AltaEquipo();
    }
}