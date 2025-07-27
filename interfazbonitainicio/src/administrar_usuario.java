import javax.swing.*;
import java.sql.*;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;

public class administrar_usuario extends JDialog {

    private JPanel opcion_usuario;      // Panel principal del diálogo
    private JButton cerrarSesionButton; // Botón para cerrar sesión
    private JLabel nombre_label;        // Muestra el nombre completo del usuario
    private JLabel correo_label;        // Muestra el correo del usuario

    private int id_usuario;             // ID del usuario actual


    public administrar_usuario(JFrame owner, int id_usuario) {
        super(owner, "Administrar Usuario", true); // Diálogo modal
        this.id_usuario = id_usuario;

        initComponents(); // Inicializar componentes visuales

        setContentPane(opcion_usuario);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        cargarDatosUsuario(); // Cargar y mostrar datos del usuario en los labels

        cerrarSesionButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Deseas cerrar sesión?",
                    "Cerrar Sesión",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                owner.dispose(); // Cierra la ventana principal interfaz_final
                dispose();       // Cierra este diálogo de administración
                // Aquí podrías abrir nuevamente la ventana de login si deseas.
            }
        });
    }

    //Carga los datos del usuario (nombre completo y correo) desde la base de datos

    private void cargarDatosUsuario() {
        try (Connection conn = ConexionBD.getConnection()) {
            String sql = "SELECT nombres, apellido_paterno, apellido_materno, correo FROM usuarios WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id_usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nombreCompleto = rs.getString("nombres") + " " +
                        rs.getString("apellido_paterno") + " " +
                        rs.getString("apellido_materno");
                String correo = rs.getString("correo");

                nombre_label.setText(nombreCompleto);
                correo_label.setText(correo);
            } else {
                nombre_label.setText("No disponible");
                correo_label.setText("No disponible");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error cargando datos del usuario: " + ex.getMessage());
        }
    }

    // Inicializa los componentes gráficos y organiza la estructura visual del diálogo
    private void initComponents() {
        opcion_usuario = new JPanel();
        cerrarSesionButton = new JButton("Cerrar sesión");
        nombre_label = new JLabel("Nombre");
        correo_label = new JLabel("Correo");

        // Layout principal en columnas (vertical)
        opcion_usuario.setLayout(new BoxLayout(opcion_usuario, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Administrar Usuario");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar el título

        // Panel para mostrar nombre del usuario
        JPanel nombrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nombrePanel.add(new JLabel("Usuario:"));
        nombrePanel.add(nombre_label);

        // Panel para mostrar correo del usuario
        JPanel correoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        correoPanel.add(new JLabel("Correo:"));
        correoPanel.add(correo_label);

        cerrarSesionButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar botón

        // Añadir componentes al panel principal con separaciones visuales
        opcion_usuario.add(titulo);
        opcion_usuario.add(Box.createVerticalStrut(10)); // Espacio
        opcion_usuario.add(nombrePanel);
        opcion_usuario.add(Box.createVerticalStrut(10)); // Espacio
        opcion_usuario.add(correoPanel);
        opcion_usuario.add(Box.createVerticalStrut(20)); // Espacio
        opcion_usuario.add(cerrarSesionButton);
    }
}
