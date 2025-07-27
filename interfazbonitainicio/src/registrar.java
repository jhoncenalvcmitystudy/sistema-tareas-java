import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class registrar {

    private JPanel panel_registro;
    private JPasswordField txt_confirmar_llave1;
    private JTextField txtnombre;
    private JButton btn_registrar;
    private JPasswordField txt_llave_1;
    private JTextField txt_apellido_paterno;
    private JTextField txt_apellido_materno;
    private JTextField correo;


    public registrar() {
        btn_registrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Recolectar datos ingresados por el usuario
                String nombre = txtnombre.getText().trim();
                String apPaterno = txt_apellido_paterno.getText().trim();
                String apMaterno = txt_apellido_materno.getText().trim();
                String email = correo.getText().trim();
                String pass = new String(txt_llave_1.getPassword());
                String passConfirm = new String(txt_confirmar_llave1.getPassword());

                // Verificar que ningún campo este vacío
                if (nombre.isEmpty() || apPaterno.isEmpty() || apMaterno.isEmpty() ||
                        email.isEmpty() || pass.isEmpty() || passConfirm.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
                    return;
                }

                // Validar coincidencia de contraseñas
                if (!pass.equals(passConfirm)) {
                    JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden.");
                    return;
                }

                // Validar que el correo sea de Gmail
                if (!email.endsWith("@gmail.com")) {
                    JOptionPane.showMessageDialog(null, "El correo debe ser una dirección de Gmail (@gmail.com).");
                    return;
                }

                // Conexión e inserción en base de datos
                try (Connection conn = ConexionBD.getConnection()) {
                    // Verificar si el correo ya está registrado
                    String checkSql = "SELECT COUNT(*) FROM usuarios WHERE correo = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                    checkStmt.setString(1, email);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(null, "Ya existe un usuario con este correo.");
                        return;
                    }

                    // Insertar el nuevo usuario
                    String sql = "INSERT INTO usuarios (nombres, apellido_paterno, apellido_materno, correo, contraseña) " +
                            "VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    stmt.setString(1, nombre);
                    stmt.setString(2, apPaterno);
                    stmt.setString(3, apMaterno);
                    stmt.setString(4, email);
                    stmt.setString(5, pass);

                    stmt.executeUpdate();

                    // Obtener el ID generado para el nuevo usuario
                    int id_usuario = -1;
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        id_usuario = generatedKeys.getInt(1);
                    } else {
                        JOptionPane.showMessageDialog(null, "No se pudo obtener el ID del usuario registrado.");
                        return;
                    }

                    JOptionPane.showMessageDialog(null, "Registro exitoso.");

                    // Abrir la interfaz principal (interfaz_final) con el usuario registrado
                    JFrame principal = new JFrame("Sistema Principal");
                    principal.setContentPane(new interfaz_final(id_usuario).getPanel());
                    principal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    principal.pack();
                    principal.setLocationRelativeTo(null);
                    principal.setVisible(true);

                    // Cerrar ventana de registro
                    SwingUtilities.getWindowAncestor(panel_registro).dispose();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al registrar: " + ex.getMessage());
                }
            }
        });
    }

    public JPanel getPanel() {
        return panel_registro;
    }

    private void createUIComponents() {
        //no es necesario llenar
    }
}
