import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class inicio {

    public JPanel panel_principal; // Panel principal que contiene la interfaz de inicio
    private JTextField txtusuario; // Campo para el correo
    private JPasswordField txtllave; // Campo para la contraseña
    private JButton login_boton; // Botón para iniciar sesión
    private JLabel imageLabel; // Etiqueta para imagen decorativa (si se usa)
    public JButton registrarse_boton; // Botón para abrir la ventana de registro

    // constructor
    public inicio() {

        // Listener para el boton de login
        login_boton.addActionListener(e -> {
            String correo = txtusuario.getText().trim();
            String contraseña = new String(txtllave.getPassword());

            // Validación de campos vacíos
            if (correo.isEmpty() || contraseña.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debes completar todos los campos");
                return;
            }

            // Intento de conexión a la base de datos para validar credenciales
            try (Connection conn = ConexionBD.getConnection()) {
                String sql = "SELECT * FROM usuarios WHERE correo = ? AND contraseña = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, correo);
                stmt.setString(2, contraseña);

                ResultSet rs = stmt.executeQuery();

                //datos correctos
                if (rs.next()) {
                    int id_usuario = rs.getInt("id"); //obtener id
                    JOptionPane.showMessageDialog(null, "¡Bienvenido!");

                    // Abrir la interfaz principal con el id del usuario
                    JFrame principal = new JFrame("Sistema Principal");
                    principal.setContentPane(new interfaz_final(id_usuario).getPanel());
                    principal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    principal.pack();
                    principal.setLocationRelativeTo(null);
                    principal.setVisible(true);

                    // Cerrar la ventana actual de inicio de sesión
                    SwingUtilities.getWindowAncestor(panel_principal).dispose();
                } else {
                    // Si las credenciales son incorrectas
                    JOptionPane.showMessageDialog(null, "Credenciales incorrectas");
                }

                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error de conexión: " + ex.getMessage());
            }
        });

        // listener para el boton de registro de usuario
        registrarse_boton.addActionListener(e -> {
            // Crear y mostrar la ventana de registro
            JFrame registroFrame = new JFrame("Registro de Usuario");
            registroFrame.setContentPane(new registrar().getPanel());
            registroFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            registroFrame.pack();
            registroFrame.setLocationRelativeTo(null);
            registroFrame.setVisible(true);

            // Cerrar la ventana actual de inicio de sesión al abrir registro
            SwingUtilities.getWindowAncestor(panel_principal).dispose();
        });
    }
}
