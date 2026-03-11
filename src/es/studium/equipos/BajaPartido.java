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

public class BajaPartido extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Baja de Partido");
    Choice choPartidos = new Choice();
    Button btnBorrar = new Button("Borrar");

    Dialog dlgConfirmar = new Dialog(ventana, "¿Seguro?", true);
    Label lblPregunta = new Label("¿Desea eliminar este partido del calendario?");
    Button btnSi = new Button("Sí");
    Button btnNo = new Button("No");

    Dialog dlgMensaje = new Dialog(ventana, "Aviso", true);
    Label lblMensaje = new Label("");
    Button btnOk = new Button("Ok");

    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/equipos";
    String login = "root";
    String password = "Hugo_2007";

    public BajaPartido() {
        ventana.setLayout(new FlowLayout());
        ventana.setSize(500, 150);
        ventana.addWindowListener(this);

        rellenarChoice();

        ventana.add(new Label("Seleccione Partido:"));
        ventana.add(choPartidos);
        ventana.add(btnBorrar);
        btnBorrar.addActionListener(this);

        dlgConfirmar.setLayout(new FlowLayout());
        dlgConfirmar.setSize(350, 100);
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
        choPartidos.removeAll();
        choPartidos.add("Seleccione un partido...");
        try {
            Class.forName(driver);
            Connection con = DriverManager.getConnection(url, login, password);
            
            String sql = "SELECT p.idPartido, p.fechaPartido, e1.nombreEquipo as local, e2.nombreEquipo as visitante " +
                         "FROM partidos p " +
                         "JOIN equipos e1 ON p.idEquipoLocalFK = e1.idEquipo " +
                         "JOIN equipos e2 ON p.idEquipoVisitanteFK = e2.idEquipo";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                choPartidos.add(rs.getInt("idPartido") + " - " + 
                                 rs.getString("fechaPartido") + ": " + 
                                 rs.getString("local") + " vs " + 
                                 rs.getString("visitante"));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnBorrar)) {
            if (choPartidos.getSelectedIndex() != 0) {
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
        String id = choPartidos.getSelectedItem().split(" - ")[0];
        try {
            Connection con = DriverManager.getConnection(url, login, password);
            PreparedStatement ps = con.prepareStatement("DELETE FROM partidos WHERE idPartido = ?");
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();

            lblMensaje.setText("Partido eliminado correctamente.");
            dlgMensaje.setVisible(true);
            
            rellenarChoice();
            con.close();
        } catch (SQLException ex) {
            lblMensaje.setText("Error al eliminar el partido.");
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