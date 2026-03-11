package es.studium.equipos;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConsultaJugador extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Consulta Jugadores");
    TextArea txtListado = new TextArea();
    Button btnVolver = new Button("Volver");
    Panel pnlInferior = new Panel();

    String url = "jdbc:mysql://localhost:3306/equipos";
    String login = "root";
    String password = "Hugo_2007";

    public ConsultaJugador() {
        ventana.setLayout(new BorderLayout());
        ventana.setSize(600, 450); 
        ventana.addWindowListener(this);

        ventana.add(txtListado, BorderLayout.CENTER);
        
        pnlInferior.add(btnVolver);
        ventana.add(pnlInferior, BorderLayout.SOUTH);
        
        btnVolver.addActionListener(this);

        cargar();

        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void cargar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, login, password);
            Statement st = con.createStatement();
            
            String sql = "SELECT j.idJugador, j.nombreJugador, j.apellidosJugador, j.numeroJugador, e.nombreEquipo " +
                         "FROM jugadores j JOIN equipos e ON j.idEquipoFK = e.idEquipo";
            
            ResultSet rs = st.executeQuery(sql);

            txtListado.setText(""); 
            while (rs.next()) {
                txtListado.append(rs.getInt("idJugador") + " - " + 
                                 rs.getString("nombreJugador") + " " + 
                                 rs.getString("apellidosJugador") + " - Nº" + 
                                 rs.getInt("numeroJugador") + " - " + 
                                 rs.getString("nombreEquipo") + "\n");
            }

            con.close();
        } catch (Exception e) {
            txtListado.setText("Error: " + e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnVolver) {
            ventana.dispose();
        }
    }

    public void windowClosing(WindowEvent e) {
        ventana.dispose();
    }
}