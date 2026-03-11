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

public class ConsultaCiudad extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Consulta Ciudades");
    TextArea txtListado = new TextArea();
    Button btnVolver = new Button("Volver");
    Panel pnlInferior = new Panel();

    String url = "jdbc:mysql://localhost:3306/equipos";
    String login = "root";
    String password = "Hugo_2007";

    public ConsultaCiudad() {
        ventana.setLayout(new BorderLayout());
        ventana.setSize(500, 400);
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
            ResultSet rs = st.executeQuery("SELECT * FROM ciudades");

            txtListado.setText(""); 
            while (rs.next()) {
                int id = rs.getInt("idCiudad");
                String nombre = rs.getString("nombreCiudad");
                String pais = rs.getString("paisCiudad");
                
                txtListado.append(id + " - " + nombre + " - " + pais + "\n");
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