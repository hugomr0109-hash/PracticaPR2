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

public class AltaPartido extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Alta de Partido");

    Label lblFecha = new Label("Fecha (AAAA-MM-DD):");
    TextField txtFecha = new TextField(20);

    Label lblCiudad = new Label("Ciudad:");
    Choice choCiudades = new Choice();

    Label lblLocal = new Label("Equipo Local:");
    Choice choLocal = new Choice();

    Label lblVisitante = new Label("Equipo Visitante:");
    Choice choVisitante = new Choice();

    Button btnAceptar = new Button("Aceptar");
    Button btnLimpiar = new Button("Limpiar");

    Dialog dlgMensaje = new Dialog(ventana, "Aviso", true);
    Label lblMensaje = new Label("");
    Button btnOk = new Button("Ok");

    // Credenciales de tu Login
    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/equipos";
    String login = "root";
    String password = "Hugo_2007";

    public AltaPartido() {
        ventana.setLayout(new FlowLayout());
        ventana.setSize(280, 420);
        ventana.addWindowListener(this);

        // Rellenar todos los desplegables desde la BD
        rellenarCombos();

        ventana.add(lblFecha);
        ventana.add(txtFecha);
        ventana.add(lblCiudad);
        ventana.add(choCiudades);
        ventana.add(lblLocal);
        ventana.add(choLocal);
        ventana.add(lblVisitante);
        ventana.add(choVisitante);

        ventana.add(btnAceptar);
        ventana.add(btnLimpiar);

        btnAceptar.addActionListener(this);
        btnLimpiar.addActionListener(this);

        // Configuración Dialog
        dlgMensaje.setLayout(new FlowLayout());
        dlgMensaje.setSize(220, 100);
        btnOk.addActionListener(this);
        dlgMensaje.add(lblMensaje);
        dlgMensaje.add(btnOk);
        dlgMensaje.setResizable(false);
        dlgMensaje.setLocationRelativeTo(null);

        ventana.setResizable(false);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void rellenarCombos() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, login, password);

            // 1. Rellenar Ciudades
            ps = con.prepareStatement("SELECT idCiudad, nombreCiudad FROM ciudades");
            rs = ps.executeQuery();
            choCiudades.add("Seleccione Ciudad...");
            while (rs.next()) {
                choCiudades.add(rs.getInt("idCiudad") + " - " + rs.getString("nombreCiudad"));
            }

            // 2. Rellenar Equipos (para Local y Visitante)
            ps = con.prepareStatement("SELECT idEquipo, nombreEquipo FROM equipos");
            rs = ps.executeQuery();
            choLocal.add("Seleccione Local...");
            choVisitante.add("Seleccione Visitante...");
            while (rs.next()) {
                String item = rs.getInt("idEquipo") + " - " + rs.getString("nombreEquipo");
                choLocal.add(item);
                choVisitante.add(item);
            }
        } catch (Exception e) {
            System.err.println("Error cargando combos: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); if (con != null) con.close(); } catch (SQLException e) {}
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnAceptar)) {
            if (validar()) {
                insertarPartido();
            }
        } else if (e.getSource().equals(btnLimpiar)) {
            limpiar();
        } else if (e.getSource().equals(btnOk)) {
            dlgMensaje.setVisible(false);
        }
    }

    private boolean validar() {
        if (choCiudades.getSelectedIndex() == 0 || choLocal.getSelectedIndex() == 0 || choVisitante.getSelectedIndex() == 0) {
            lblMensaje.setText("Selecciona todos los campos");
            dlgMensaje.setVisible(true);
            return false;
        }
        if (choLocal.getSelectedIndex() == choVisitante.getSelectedIndex()) {
            lblMensaje.setText("El local y visitante son iguales");
            dlgMensaje.setVisible(true);
            return false;
        }
        return true;
    }

    private void insertarPartido() {
        String sql = "INSERT INTO partidos (fechaPartido, idCiudadFK, idEquipoLocalFK, idEquipoVisitanteFK) VALUES (?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DriverManager.getConnection(url, login, password);
            ps = con.prepareStatement(sql);

            // Extraemos los IDs de los Choice (formato "ID - Nombre")
            int idCiu = Integer.parseInt(choCiudades.getSelectedItem().split(" - ")[0]);
            int idLoc = Integer.parseInt(choLocal.getSelectedItem().split(" - ")[0]);
            int idVis = Integer.parseInt(choVisitante.getSelectedItem().split(" - ")[0]);

            ps.setString(1, txtFecha.getText()); // Formato YYYY-MM-DD para MySQL
            ps.setInt(2, idCiu);
            ps.setInt(3, idLoc);
            ps.setInt(4, idVis);

            ps.executeUpdate();

            lblMensaje.setText("Partido registrado");
            dlgMensaje.setVisible(true);
            limpiar();
        } catch (Exception ex) {
            lblMensaje.setText("Error: Verifique la fecha");
            dlgMensaje.setVisible(true);
        } finally {
            try { if (ps != null) ps.close(); if (con != null) con.close(); } catch (SQLException e) {}
        }
    }

    private void limpiar() {
        txtFecha.setText("");
        choCiudades.select(0);
        choLocal.select(0);
        choVisitante.select(0);
        txtFecha.requestFocus();
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