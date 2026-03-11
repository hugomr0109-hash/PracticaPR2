package es.studium.equipos;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class ModificarEquipo extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Modificar Equipo");
    
    Label lblSeleccion = new Label("Seleccione Equipo a modificar:");
    Choice choEquipos = new Choice();
    Button btnCargar = new Button("Cargar Datos");

    Label lblNombre = new Label("Nuevo Nombre:");
    TextField txtNombre = new TextField(20);
    Label lblCategoria = new Label("Nueva Categoría:");
    TextField txtCategoria = new TextField(20);
    Label lblEstadio = new Label("Nuevo Estadio:");
    TextField txtEstadio = new TextField(20);
    
    Button btnModificar = new Button("Guardar Cambios");

    Dialog dlgMensaje = new Dialog(ventana, "Aviso", true);
    Label lblMensaje = new Label("");
    Button btnOk = new Button("Ok");

    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/equipos";
    String login = "root";
    String password = "Hugo_2007";

    public ModificarEquipo() {
        ventana.setLayout(new FlowLayout());
        ventana.setSize(450, 400);
        ventana.addWindowListener(this);

        rellenarChoice();

        ventana.add(lblSeleccion);
        ventana.add(choEquipos);
        ventana.add(btnCargar);
        ventana.add(lblNombre);
        ventana.add(txtNombre);
        ventana.add(new Label("                 "));
        ventana.add(lblCategoria);
        ventana.add(txtCategoria);
        ventana.add(new Label("                 "));
        ventana.add(lblEstadio);
        ventana.add(txtEstadio);
        ventana.add(btnModificar);

        txtNombre.setEnabled(false);
        txtCategoria.setEnabled(false);
        txtEstadio.setEnabled(false);
        btnModificar.setEnabled(false);

        btnCargar.addActionListener(this);
        btnModificar.addActionListener(this);

        dlgMensaje.setLayout(new FlowLayout());
        dlgMensaje.setSize(250, 100);
        dlgMensaje.add(lblMensaje);
        btnOk.addActionListener(this);
        dlgMensaje.add(btnOk);
        dlgMensaje.setLocationRelativeTo(null);

        ventana.setResizable(false);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void rellenarChoice() {
        choEquipos.removeAll();
        choEquipos.add("Seleccione un equipo...");
        try {
            Connection con = DriverManager.getConnection(url, login, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT idEquipo, nombreEquipo FROM equipos");
            while (rs.next()) {
                choEquipos.add(rs.getInt("idEquipo") + " - " + rs.getString("nombreEquipo"));
            }
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnCargar)) {
            if (choEquipos.getSelectedIndex() != 0) {
                cargarDatosEquipo();
            }
        } else if (e.getSource().equals(btnModificar)) {
            actualizarEquipo();
        } else if (e.getSource().equals(btnOk)) {
            dlgMensaje.setVisible(false);
        }
    }

    private void cargarDatosEquipo() {
        String id = choEquipos.getSelectedItem().split(" - ")[0];
        try {
            Connection con = DriverManager.getConnection(url, login, password);
            PreparedStatement ps = con.prepareStatement("SELECT * FROM equipos WHERE idEquipo = ?");
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNombre.setText(rs.getString("nombreEquipo"));
                txtCategoria.setText(rs.getString("categoriaEquipo"));
                txtEstadio.setText(rs.getString("nombreEstadioEquipo"));
                
                txtNombre.setEnabled(true);
                txtCategoria.setEnabled(true);
                txtEstadio.setEnabled(true);
                btnModificar.setEnabled(true);
            }
            con.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void actualizarEquipo() {
        String id = choEquipos.getSelectedItem().split(" - ")[0];
        try {
            Connection con = DriverManager.getConnection(url, login, password);
            String sql = "UPDATE equipos SET nombreEquipo = ?, categoriaEquipo = ?, nombreEstadioEquipo = ? WHERE idEquipo = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtCategoria.getText());
            ps.setString(3, txtEstadio.getText());
            ps.setInt(4, Integer.parseInt(id));
            
            ps.executeUpdate();
            
            lblMensaje.setText("Equipo actualizado con éxito.");
            dlgMensaje.setVisible(true);
            
            rellenarChoice(); 
            con.close();
        } catch (SQLException ex) {
            lblMensaje.setText("Error en la actualización.");
            dlgMensaje.setVisible(true);
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (dlgMensaje.isActive()) dlgMensaje.setVisible(false);
        else ventana.dispose();
    }
}