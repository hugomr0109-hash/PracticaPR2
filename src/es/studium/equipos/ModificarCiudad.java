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

public class ModificarCiudad extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Modificar Ciudad");
    
    Label lblSeleccion = new Label("Seleccione Ciudad a modificar:");
    Choice choCiudades = new Choice();
    Button btnCargar = new Button("Cargar Datos");

    Label lblNombre = new Label("Nuevo Nombre:");
    TextField txtNombre = new TextField(20);
    Label lblPais = new Label("Nuevo País:");
    TextField txtPais = new TextField(20);
    Button btnModificar = new Button("Guardar Cambios");

    Dialog dlgMensaje = new Dialog(ventana, "Aviso", true);
    Label lblMensaje = new Label("");
    Button btnOk = new Button("Ok");

    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/equipos";
    String login = "root";
    String password = "Hugo_2007";

    public ModificarCiudad() {
        ventana.setLayout(new FlowLayout());
        ventana.setSize(450, 350);
        ventana.addWindowListener(this);

        rellenarChoice();

        ventana.add(lblSeleccion);
        ventana.add(choCiudades);
        ventana.add(btnCargar);
        ventana.add(lblNombre);
        ventana.add(txtNombre);
        ventana.add(new Label("                 "));
        ventana.add(lblPais);
        ventana.add(txtPais);
        ventana.add(btnModificar);

        txtNombre.setEnabled(false);
        txtPais.setEnabled(false);
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
        choCiudades.removeAll();
        choCiudades.add("Seleccione...");
        try {
            Connection con = DriverManager.getConnection(url, login, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT idCiudad, nombreCiudad FROM ciudades");
            while (rs.next()) {
                choCiudades.add(rs.getInt("idCiudad") + " - " + rs.getString("nombreCiudad"));
            }
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnCargar)) {
            if (choCiudades.getSelectedIndex() != 0) {
                cargarDatosCiudad();
            }
        } else if (e.getSource().equals(btnModificar)) {
            actualizarCiudad();
        } else if (e.getSource().equals(btnOk)) {
            dlgMensaje.setVisible(false);
        }
    }

    private void cargarDatosCiudad() {
        String id = choCiudades.getSelectedItem().split(" - ")[0];
        try {
            Connection con = DriverManager.getConnection(url, login, password);
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ciudades WHERE idCiudad = ?");
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNombre.setText(rs.getString("nombreCiudad"));
                txtPais.setText(rs.getString("paisCiudad"));
                
                txtNombre.setEnabled(true);
                txtPais.setEnabled(true);
                btnModificar.setEnabled(true);
            }
            con.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void actualizarCiudad() {
        String id = choCiudades.getSelectedItem().split(" - ")[0];
        try {
            Connection con = DriverManager.getConnection(url, login, password);
            PreparedStatement ps = con.prepareStatement("UPDATE ciudades SET nombreCiudad = ?, paisCiudad = ? WHERE idCiudad = ?");
            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtPais.getText());
            ps.setInt(3, Integer.parseInt(id));
            
            ps.executeUpdate();
            
            lblMensaje.setText("Ciudad modificada con éxito.");
            dlgMensaje.setVisible(true);
            
            rellenarChoice();
            con.close();
        } catch (SQLException ex) {
            lblMensaje.setText("Error al modificar.");
            dlgMensaje.setVisible(true);
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (dlgMensaje.isActive()) dlgMensaje.setVisible(false);
        else ventana.dispose();
    }
}