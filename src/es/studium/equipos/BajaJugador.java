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

public class BajaJugador extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Baja de Jugador");
    Choice choJugadores = new Choice();
    Button btnBorrar = new Button("Borrar");

    Dialog dlgConfirmar = new Dialog(ventana, "¿Seguro?", true);
    Label lblPregunta = new Label("¿Desea eliminar este jugador definitivamente?");
    Button btnSi = new Button("Sí");
    Button btnNo = new Button("No");

    Dialog dlgMensaje = new Dialog(ventana, "Aviso", true);
    Label lblMensaje = new Label("");
    Button btnOk = new Button("Ok");

    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/equipos";
    String login = "root";
    String password = "Hugo_2007";

    public BajaJugador() {
        ventana.setLayout(new FlowLayout());
        ventana.setSize(400, 150);
        ventana.addWindowListener(this);

        rellenarChoice();

        ventana.add(new Label("Seleccione Jugador:"));
        ventana.add(choJugadores);
        ventana.add(btnBorrar);
        btnBorrar.addActionListener(this);

        dlgConfirmar.setLayout(new FlowLayout());
        dlgConfirmar.setSize(300, 100);
        dlgConfirmar.add(lblPregunta);
        dlgConfirmar.add(btnSi);
        dlgConfirmar.add(btnNo);
        btnSi.addActionListener(this);
        btnNo.addActionListener(this);
        dlgConfirmar.setLocationRelativeTo(null);

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
        choJugadores.removeAll();
        choJugadores.add("Seleccione un jugador...");
        try {
            Class.forName(driver);
            Connection con = DriverManager.getConnection(url, login, password);
            PreparedStatement ps = con.prepareStatement("SELECT idJugador, nombreJugador, apellidosJugador FROM jugadores");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                choJugadores.add(rs.getInt("idJugador") + " - " + 
                                 rs.getString("nombreJugador") + " " + 
                                 rs.getString("apellidosJugador"));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnBorrar)) {
            if (choJugadores.getSelectedIndex() != 0) {
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
        String id = choJugadores.getSelectedItem().split(" - ")[0];
        try {
            Connection con = DriverManager.getConnection(url, login, password);
            PreparedStatement ps = con.prepareStatement("DELETE FROM jugadores WHERE idJugador = ?");
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();

            lblMensaje.setText("Jugador eliminado con éxito.");
            dlgMensaje.setVisible(true);
            rellenarChoice();
            con.close();
        } catch (SQLException ex) {
            lblMensaje.setText("Error al eliminar el jugador.");
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