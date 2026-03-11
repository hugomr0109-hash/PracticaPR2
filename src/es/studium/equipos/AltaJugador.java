package es.studium.equipos;

import java.awt.Button;
import java.awt.Choice;
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

public class AltaJugador extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Alta de Jugador");
    
    // Campos según tu tabla 'jugadores'
    Label lblNombre = new Label("Nombre:");
    TextField txtNombre = new TextField(20);
    
    Label lblApellidos = new Label("Apellidos:");
    TextField txtApellidos = new TextField(20);
    
    Label lblSalario = new Label("Salario:");
    TextField txtSalario = new TextField(20);
    
    Label lblNumero = new Label("Dorsal:");
    TextField txtNumero = new TextField(20);
    
    Label lblEquipo = new Label("Equipo:");
    Choice choEquipos = new Choice(); // Desplegable para la FK
    
    Button btnAceptar = new Button("Aceptar");
    Button btnLimpiar = new Button("Limpiar");
    
    // Feedback
    Dialog dlgMensaje = new Dialog(ventana, "Aviso", true);
    Label lblMensaje = new Label("");
    Button btnOk = new Button("Ok");

    // Credenciales (ajustadas a tu Login)
    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/equipos";
    String login = "root";
    String password = "Hugo_2007";

    public AltaJugador() {
        ventana.setLayout(new FlowLayout());
        ventana.setSize(250, 400);
        ventana.addWindowListener(this);

        // Rellenar el Choice con los equipos de la BD antes de mostrar la ventana
        rellenarEquipos();

        ventana.add(lblNombre);
        ventana.add(txtNombre);
        ventana.add(lblApellidos);
        ventana.add(txtApellidos);
        ventana.add(lblSalario);
        ventana.add(txtSalario);
        ventana.add(lblNumero);
        ventana.add(txtNumero);
        ventana.add(lblEquipo);
        ventana.add(choEquipos);
        
        ventana.add(btnAceptar);
        ventana.add(btnLimpiar);

        btnAceptar.addActionListener(this);
        btnLimpiar.addActionListener(this);

        // Dialog
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

    private void rellenarEquipos() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, login, password);
            // Solo necesitamos el ID y el Nombre para el Choice
            ps = con.prepareStatement("SELECT idEquipo, nombreEquipo FROM equipos");
            rs = ps.executeQuery();
            
            choEquipos.add("Seleccione un equipo...");
            while (rs.next()) {
                // Guardamos "ID - Nombre" para luego poder sacar el ID fácilmente
                choEquipos.add(rs.getInt("idEquipo") + " - " + rs.getString("nombreEquipo"));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar equipos: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); if (con != null) con.close(); } catch (SQLException e) {}
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnAceptar)) {
            if (choEquipos.getSelectedIndex() != 0) { // Validar que ha elegido un equipo
                insertarJugador();
            } else {
                lblMensaje.setText("Debes elegir un equipo");
                dlgMensaje.setVisible(true);
            }
        } else if (e.getSource().equals(btnLimpiar)) {
            limpiar();
        } else if (e.getSource().equals(btnOk)) {
            dlgMensaje.setVisible(false);
        }
    }

    private void insertarJugador() {
        String sql = "INSERT INTO jugadores (nombreJugador, apellidosJugador, salarioJugador, numeroJugador, idEquipoFK) VALUES (?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(url, login, password);
            ps = con.prepareStatement(sql);
            
            // Extraer el ID del equipo desde el String del Choice ("5 - Betis" -> "5")
            String seleccion = choEquipos.getSelectedItem();
            int idEquipo = Integer.parseInt(seleccion.split(" - ")[0]);

            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtApellidos.getText());
            ps.setDouble(3, Double.parseDouble(txtSalario.getText()));
            ps.setInt(4, Integer.parseInt(txtNumero.getText()));
            ps.setInt(5, idEquipo);

            ps.executeUpdate();
            
            lblMensaje.setText("Jugador dado de alta");
            dlgMensaje.setVisible(true);
            limpiar();
        } catch (Exception ex) {
            lblMensaje.setText("Error en el alta");
            dlgMensaje.setVisible(true);
            ex.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); if (con != null) con.close(); } catch (SQLException e) {}
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        txtApellidos.setText("");
        txtSalario.setText("");
        txtNumero.setText("");
        choEquipos.select(0);
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
}