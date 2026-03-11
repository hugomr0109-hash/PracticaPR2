package es.studium.equipos;

import java.awt.*;
import java.awt.event.*;

public class MenuPrincipal extends WindowAdapter implements ActionListener {
    Frame ventana = new Frame("Equipos - Menú Principal");
    
    Panel pnlTablas = new Panel();
    Button btnEquipos = new Button("Equipos");
    Button btnJugadores = new Button("Jugadores");
    Button btnCiudades = new Button("Ciudades");
    Button btnPartidos = new Button("Partidos");
    
    Panel pnlCRUD = new Panel();
    Button btnAlta = new Button("Alta");
    Button btnBaja = new Button("Baja");
    Button btnModificacion = new Button("Modificación");
    Button btnConsulta = new Button("Consulta");

    String tipoUsuario;
    String tablaActiva = "Equipos";
    

    public MenuPrincipal(String tipo) {
        this.tipoUsuario = tipo;
        
        ventana.setLayout(new BorderLayout());
        ventana.setSize(600, 450);
        ventana.addWindowListener(this);

        pnlTablas.setLayout(new FlowLayout());
        pnlTablas.add(btnEquipos);
        pnlTablas.add(btnJugadores);
        pnlTablas.add(btnCiudades);
        pnlTablas.add(btnPartidos);
        ventana.add(pnlTablas, BorderLayout.NORTH);

        pnlCRUD.setLayout(new GridLayout(4, 1, 10, 20)); 
        pnlCRUD.add(btnAlta);
        pnlCRUD.add(btnBaja);
        pnlCRUD.add(btnModificacion);
        pnlCRUD.add(btnConsulta);
        
        Panel pnlCentrador = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 80));
        pnlCentrador.add(pnlCRUD);
        ventana.add(pnlCentrador, BorderLayout.CENTER);

        if (tipoUsuario.equalsIgnoreCase("Basico")) {
        	btnBaja.setVisible(false);
            btnBaja.setEnabled(false);
            btnModificacion.setVisible(false);
            btnModificacion.setEnabled(false);
            btnConsulta.setVisible(false);
            btnConsulta.setEnabled(false);
        }

        btnEquipos.addActionListener(this);
        btnJugadores.addActionListener(this);
        btnCiudades.addActionListener(this);
        btnPartidos.addActionListener(this);
        btnAlta.addActionListener(this);
        btnBaja.addActionListener(this);
        btnModificacion.addActionListener(this);
        btnConsulta.addActionListener(this);

        ventana.setResizable(false);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object fuente = e.getSource();

        if (fuente.equals(btnEquipos)) {
        	tablaActiva = "Equipos";
        }
        else if (fuente.equals(btnJugadores)) {
        	tablaActiva = "Jugadores";
        }
        else if (fuente.equals(btnCiudades)) {
        	tablaActiva = "Ciudades";
        }
        else if (fuente.equals(btnPartidos)) {
        	tablaActiva = "Partidos";
        }

        if (fuente.equals(btnAlta)) {
            abrirAlta();
        } else if (fuente.equals(btnBaja)) {
            abrirBaja();
        } else if (fuente.equals(btnModificacion)) {
            abrirModificacion();
        } else if (fuente.equals(btnConsulta)) {
            abrirConsulta();
        }
    }

    private void abrirAlta() {
        if (tablaActiva.equals("Equipos")) {
            new AltaEquipo();
        } else if (tablaActiva.equals("Jugadores")) {
            new AltaJugador();
        } else if (tablaActiva.equals("Ciudades")) {
            new AltaCiudad();
        }else if (tablaActiva.equals("Partidos")) {
            new AltaPartido();
        }
        ventana.setVisible(false);
    }

    private void abrirBaja() {
    	if (tablaActiva.equals("Equipos")) {
            new BajaEquipo();
        } else if (tablaActiva.equals("Jugadores")) {
            new BajaJugador();
        } else if (tablaActiva.equals("Ciudades")) {
            new BajaCiudad();
        }else if (tablaActiva.equals("Partidos")) {
            new BajaPartido();
        }
        ventana.setVisible(false);
    }

    private void abrirModificacion() {
    	if (tablaActiva.equals("Equipos")) {
            new ModificarEquipo();
        } else if (tablaActiva.equals("Ciudades")) {
            new ModificarCiudad();
        } 
        ventana.setVisible(false);
    }

    private void abrirConsulta() {
    	if (tablaActiva.equals("Equipos")) {
            new ConsultaEquipo();
        } else if (tablaActiva.equals("Jugadores")) {
            new ConsultaJugador();
        } else if (tablaActiva.equals("Ciudades")) {
            new ConsultaCiudad();
        }else if (tablaActiva.equals("Partidos")) {
            new ConsultaPartido();
        }
        ventana.setVisible(false);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }
}