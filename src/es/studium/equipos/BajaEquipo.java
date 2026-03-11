package es.studium.equipos;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BajaEquipo extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Baja de Equipo");
    Choice choEquipos = new Choice();
    Button btnBorrar = new Button("Borrar");

    // Diálogo de Confirmación (Fase 6: Pregunta Sí/No)
    Dialog dlgConfirmar = new Dialog(ventana, "¿Seguro?", true);
    Label lblPregunta = new Label("¿Desea eliminar este equipo y sus datos?");
    Button btnSi = new Button("Sí");
    Button btnNo = new Button("No");

    // Diálogo de Mensaje
    Dialog dlgMensaje = new Dialog(ventana, "Aviso", true);
    Label lblMensaje = new Label("");
    Button btnOk = new Button("Ok");

    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/equipos";
    String login = "root";
    String password = "Hugo_2007";

    public BajaEquipo() {
        ventana.setLayout(new FlowLayout());
        ventana.setSize(350, 150);
        ventana.addWindowListener(this);

        rellenarChoice();

        ventana.add(new Label("Seleccione Equipo:"));
        ventana.add(choEquipos);
        ventana.add(btnBorrar);
        btnBorrar.addActionListener(this);

        // Configuración Confirmación
        dlgConfirmar.setLayout(new FlowLayout());
        dlgConfirmar.setSize(300, 100);
        dlgConfirmar.add(lblPregunta);
        dlgConfirmar.add(btnSi);
        dlgConfirmar.add(btnNo);
        btnSi.addActionListener(this);
        btnNo.addActionListener(this);
        dlgConfirmar.setLocationRelativeTo(null);

        // Configuración Mensaje
        dlgMensaje.setLayout(new FlowLayout());
        dlgMensaje.setSize(300, 100);
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
            Class.forName(driver);
            Connection con = DriverManager.getConnection(url, login, password);
            PreparedStatement ps = con.prepareStatement("SELECT idEquipo, nombreEquipo FROM equipos");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                choEquipos.add(rs.getInt("idEquipo") + " - " + rs.getString("nombreEquipo"));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnBorrar)) {
            if (choEquipos.getSelectedIndex() != 0) {
                dlgConfirmar.setVisible(true);
            }
        } else if (e.getSource().equals(btnSi)) {
            dlgConfirmar.setVisible(false);
            ejecutarBaja();
        } else if (e.getSource().equals(btnNo)) {
            dlgConfirmar.setVisible(false);
        } else if (e.getSource().equals(btnOk)) {
            dlgMensaje.setVisible(false);
        }
    }

    private void ejecutarBaja() {
        String id = choEquipos.getSelectedItem().split(" - ")[0];
        try {
            Connection con = DriverManager.getConnection(url, login, password);
            PreparedStatement ps = con.prepareStatement("DELETE FROM equipos WHERE idEquipo = ?");
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();

            lblMensaje.setText("Equipo eliminado correctamente.");
            dlgMensaje.setVisible(true);
            rellenarChoice();
            con.close();
        } catch (SQLException ex) {
            // Error típico de restricción de clave foránea
            lblMensaje.setText("Error: El equipo tiene jugadores o partidos.");
            dlgMensaje.setVisible(true);
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (dlgConfirmar.isActive()) {
        	dlgConfirmar.setVisible(false);
        }
        else if (dlgMensaje.isActive()) {
        	dlgMensaje.setVisible(false);
        }
        else {
        	ventana.dispose();
        }
    }
}