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

public class ConsultaPartido extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Consulta Partidos");
    TextArea txtListado = new TextArea();
    Button btnVolver = new Button("Volver");
    Panel pnlInferior = new Panel();

    String url = "jdbc:mysql://localhost:3306/equipos";
    String login = "root";
    String password = "Hugo_2007";

    public ConsultaPartido() {
        ventana.setLayout(new BorderLayout());
        ventana.setSize(700, 450); 
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
            
            String sql = "SELECT p.idPartido, p.fechaPartido, c.nombreCiudad, " +
                         "e1.nombreEquipo AS Local, e2.nombreEquipo AS Visitante " +
                         "FROM partidos p " +
                         "JOIN ciudades c ON p.idCiudadFK = c.idCiudad " +
                         "JOIN equipos e1 ON p.idEquipoLocalFK = e1.idEquipo " +
                         "JOIN equipos e2 ON p.idEquipoVisitanteFK = e2.idEquipo";
            
            ResultSet rs = st.executeQuery(sql);

            txtListado.setText(""); 
            while (rs.next()) {
                txtListado.append(rs.getInt("idPartido") + " - " + 
                                 rs.getString("fechaPartido") + " - " + 
                                 rs.getString("nombreCiudad") + " - " + 
                                 rs.getString("Local") + " VS " + 
                                 rs.getString("Visitante") + "\n");
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