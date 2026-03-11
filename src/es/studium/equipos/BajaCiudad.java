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

public class BajaCiudad extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Baja de Ciudad");
    Choice choCiudades = new Choice();
    Button btnBorrar = new Button("Borrar");

    // Diálogo de Confirmación (Fase 6: Pregunta Sí/No)
    Dialog dlgConfirmar = new Dialog(ventana, "¿Está seguro?", true);
    Button btnSi = new Button("Sí");
    Button btnNo = new Button("No");
    Label lblPregunta = new Label("¿Desea eliminar esta ciudad?");

    // Diálogo de Mensaje/Retroalimentación
    Dialog dlgMensaje = new Dialog(ventana, "Aviso", true);
    Label lblMensaje = new Label("");
    Button btnOk = new Button("Ok");

    // Credenciales
    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/equipos";
    String login = "root";
    String password = "Hugo_2007";

    public BajaCiudad() {
        ventana.setLayout(new FlowLayout());
        ventana.setSize(300, 150);
        ventana.addWindowListener(this);

        rellenarChoice();

        ventana.add(new Label("Seleccione Ciudad:"));
        ventana.add(choCiudades);
        ventana.add(btnBorrar);

        btnBorrar.addActionListener(this);

        // Configurar Diálogo Confirmación
        dlgConfirmar.setLayout(new FlowLayout());
        dlgConfirmar.setSize(250, 100);
        dlgConfirmar.add(lblPregunta);
        dlgConfirmar.add(btnSi);
        dlgConfirmar.add(btnNo);
        btnSi.addActionListener(this);
        btnNo.addActionListener(this);
        dlgConfirmar.setLocationRelativeTo(null);

        // Configurar Diálogo Mensaje
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
        choCiudades.add("Seleccione una ciudad...");
        try {
            Class.forName(driver);
            Connection con = DriverManager.getConnection(url, login, password);
            PreparedStatement ps = con.prepareStatement("SELECT idCiudad, nombreCiudad FROM ciudades");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                choCiudades.add(rs.getInt("idCiudad") + " - " + rs.getString("nombreCiudad"));
            }
            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnBorrar)) {
            if (choCiudades.getSelectedIndex() != 0) {
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
        String id = choCiudades.getSelectedItem().split(" - ")[0];
        try {
            Connection con = DriverManager.getConnection(url, login, password);
            // Fase 6: Uso de PreparedStatement
            PreparedStatement ps = con.prepareStatement("DELETE FROM ciudades WHERE idCiudad = ?");
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();

            lblMensaje.setText("Ciudad eliminada con éxito.");
            dlgMensaje.setVisible(true);
            
            ps.close();
            con.close();
            rellenarChoice(); // Actualizar la lista
        } catch (SQLException ex) {
            // Manejo de error si hay FK vinculadas (Partidos en esa ciudad)
            lblMensaje.setText("No se puede borrar: tiene partidos asignados.");
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