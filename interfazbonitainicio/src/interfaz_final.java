import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class interfaz_final {

    private int id_usuario;                  // ID del usuario que ha iniciado sesión
    private int id_proyecto_seleccionado = -1; // ID del proyecto actualmente seleccionado
    private int fila_actual_proyecto = 1;      // Control de filas para FormLayout en proyectos
    private int fila_actual_tarea = 1;         // Control de filas para FormLayout en tareas

    private JPanel Interfaz_fin;
    private JButton agregar_proyecto_boton;
    private JButton agregar_tarea_boton;
    private JButton administrar_usuario_boton;
    private JButton eliminar_proyecto_boton;
    private JButton proyecto_boton;
    private JCheckBox marcar_tarea;
    private JTextField fecha_tarea;
    private JTextField nombre_tarea;
    private JButton eliminar_tarea_boton;
    private JLabel titulo_programa;
    private JPanel señalar_proyectos;
    private JPanel Proyecto;
    private JPanel tarea;
    private JPanel lista_proyectos;
    private JPanel Lista_tareas;
    private JPanel panel_indica_proyecto;
    private JLabel label_proyecto_seleccionado;

    //Devuelve el panel para insertar en el JFrame de arranque.
    public JPanel getPanel() {
        return Interfaz_fin;
    }

    // Constructor principal.Inicializa la interfaz, carga los proyectos del usuario y asigna listeners
    public interfaz_final(int id_usuario) {
        this.id_usuario = id_usuario;

        Proyecto.setVisible(false); // Oculta plantilla de proyecto al inicio

        lista_proyectos.setLayout(new FormLayout("pref", "")); // Configurar layout de proyectos
        Lista_tareas.setLayout(new FormLayout("pref", ""));    // Configurar layout de tareas

        cargarProyectos(); // Cargar proyectos al iniciar la interfaz

        agregar_proyecto_boton.addActionListener(e -> agregarProyecto());
        agregar_tarea_boton.addActionListener(e -> agregarTarea());
        administrar_usuario_boton.addActionListener(e -> abrirAdministrarUsuario());

        cargarNombreUsuarioEnBoton(); // Mostrar nombre del usuario en el botón
    }


    //Carga y muestra el nombre del usuario en el boton superior.
    private void cargarNombreUsuarioEnBoton() {
        try (Connection conn = ConexionBD.getConnection()) {
            String sql = "SELECT nombres, apellido_paterno, apellido_materno FROM usuarios WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id_usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nombreCompleto = rs.getString("nombres") + " " +
                        rs.getString("apellido_paterno") + " " +
                        rs.getString("apellido_materno");
                administrar_usuario_boton.setText(nombreCompleto);
            } else {
                administrar_usuario_boton.setText("Usuario");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error cargando nombre: " + ex.getMessage());
            administrar_usuario_boton.setText("Usuario");
        }
    }

    /**
     * Abre el diálogo de administración de usuario.
     */
    private void abrirAdministrarUsuario() {
        administrar_usuario dialog = new administrar_usuario(
                (JFrame) SwingUtilities.getWindowAncestor(Interfaz_fin),
                id_usuario
        );
        dialog.setVisible(true);
    }


     //Crea visualmente un panel de proyecto con botón de eliminar.

    private JPanel crearProyectoVisual(String nombreProyecto, int id_proyecto) {
        JPanel nuevoProyecto = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblNombre = new JLabel(nombreProyecto);
        JButton btnEliminar = new JButton("X");

        btnEliminar.addActionListener(e -> eliminarProyecto(id_proyecto));

        nuevoProyecto.add(lblNombre);
        nuevoProyecto.add(btnEliminar);

        return nuevoProyecto;
    }


    //Carga todos los proyectos del usuario desde la base de datos y los muestra en la interfaz.

    private void cargarProyectos() {
        fila_actual_proyecto = 1;
        lista_proyectos.removeAll();

        try (Connection conn = ConexionBD.getConnection()) {
            String sql = "SELECT * FROM proyectos WHERE id_usuario = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id_usuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id_proyecto = rs.getInt("id");
                String nombre = rs.getString("nombre");

                JPanel panelProyecto = crearProyectoVisual(nombre, id_proyecto);
                panelProyecto.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        id_proyecto_seleccionado = id_proyecto;
                        cargarTareas(id_proyecto);
                        actualizarLabelProyectoSeleccionado(nombre);
                    }
                });

                ((FormLayout) lista_proyectos.getLayout()).appendRow(com.jgoodies.forms.layout.RowSpec.decode("pref"));
                CellConstraints cc = new CellConstraints();
                lista_proyectos.add(panelProyecto, cc.xy(1, fila_actual_proyecto));
                fila_actual_proyecto++;
            }

            lista_proyectos.revalidate();
            lista_proyectos.repaint();

            seleccionarPrimerProyectoSiEsNecesario();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error cargando proyectos: " + ex.getMessage());
        }
    }

    //Selecciona automáticamente el primer proyecto si no hay uno seleccionado.

    private void seleccionarPrimerProyectoSiEsNecesario() {
        if (id_proyecto_seleccionado == -1 && fila_actual_proyecto > 1) {
            try (Connection conn = ConexionBD.getConnection()) {
                String sql = "SELECT id, nombre FROM proyectos WHERE id_usuario = ? LIMIT 1";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id_usuario);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    id_proyecto_seleccionado = rs.getInt("id");
                    cargarTareas(id_proyecto_seleccionado);
                    actualizarLabelProyectoSeleccionado(rs.getString("nombre"));
                } else {
                    actualizarLabelProyectoSeleccionado("");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error seleccionando proyecto: " + ex.getMessage());
            }
        } else if (fila_actual_proyecto <= 1) {
            actualizarLabelProyectoSeleccionado("");
        }
    }

    //Permite al usuario agregar un proyecto solicitando su nombre.
    private void agregarProyecto() {
        String nombre = JOptionPane.showInputDialog(null, "Ingrese el nombre del proyecto:");
        if (nombre != null && !nombre.trim().isEmpty()) {
            try (Connection conn = ConexionBD.getConnection()) {
                String sql = "INSERT INTO proyectos (id_usuario, nombre) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, id_usuario);
                stmt.setString(2, nombre.trim());
                stmt.executeUpdate();

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    id_proyecto_seleccionado = generatedKeys.getInt(1);
                }

                cargarProyectos();
                JOptionPane.showMessageDialog(null, "Proyecto agregado correctamente.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error agregando proyecto: " + ex.getMessage());
            }
        }
    }

    //Permite al usuario eliminar un proyecto y sus tareas.
    private void eliminarProyecto(int id_proyecto) {
        int confirm = JOptionPane.showConfirmDialog(null, "¿Deseas eliminar este proyecto y sus tareas?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = ConexionBD.getConnection()) {
                PreparedStatement stmtTareas = conn.prepareStatement("DELETE FROM tareas WHERE id_proyecto = ?");
                stmtTareas.setInt(1, id_proyecto);
                stmtTareas.executeUpdate();

                PreparedStatement stmtProyecto = conn.prepareStatement("DELETE FROM proyectos WHERE id = ?");
                stmtProyecto.setInt(1, id_proyecto);
                stmtProyecto.executeUpdate();

                cargarProyectos();
                JOptionPane.showMessageDialog(null, "Proyecto y tareas eliminados correctamente.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error eliminando proyecto: " + ex.getMessage());
            }
        }
    }

    // Crea visualmente un panel para cada tarea con controles de nombre, fecha, completado y eliminacion.
    private JPanel crearTareaVisual(String nombre, String fecha, boolean completada, int id_tarea) {
        JPanel nuevaTarea = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JCheckBox check = new JCheckBox();
        check.setSelected(completada);
        check.addItemListener(e -> actualizarEstadoTarea(id_tarea, check.isSelected()));

        JTextField txtNombre = new JTextField(nombre, 10);
        JTextField txtFecha = new JTextField(fecha != null ? fecha : "", 8);

        JButton btnEliminar = new JButton("X");
        btnEliminar.addActionListener(e -> eliminarTarea(id_tarea));

        nuevaTarea.add(check);
        nuevaTarea.add(txtNombre);
        nuevaTarea.add(txtFecha);
        nuevaTarea.add(btnEliminar);

        return nuevaTarea;
    }

    //Actualiza el estado de completado de una tarea en la base de datos
    private void actualizarEstadoTarea(int id_tarea, boolean completada) {
        try (Connection conn = ConexionBD.getConnection()) {
            String sql = "UPDATE tareas SET completada = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, completada);
            stmt.setInt(2, id_tarea);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error actualizando tarea: " + ex.getMessage());
        }
    }

    //Carga todas las tareas de un proyecto y las muestra en la interfaz.
    private void cargarTareas(int id_proyecto) {
        fila_actual_tarea = 1;
        Lista_tareas.removeAll();
        Lista_tareas.setLayout(new FormLayout("pref", ""));

        try (Connection conn = ConexionBD.getConnection()) {
            String sql = "SELECT * FROM tareas WHERE id_proyecto = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id_proyecto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JPanel panelTarea = crearTareaVisual(
                        rs.getString("nombre"),
                        rs.getString("fecha"),
                        rs.getBoolean("completada"),
                        rs.getInt("id")
                );
                ((FormLayout) Lista_tareas.getLayout()).appendRow(com.jgoodies.forms.layout.RowSpec.decode("pref"));
                CellConstraints cc = new CellConstraints();
                Lista_tareas.add(panelTarea, cc.xy(1, fila_actual_tarea));
                fila_actual_tarea++;
            }

            Lista_tareas.revalidate();
            Lista_tareas.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error cargando tareas: " + ex.getMessage());
        }
    }

    //Permite al usuario agregar una tarea al proyecto seleccionado
    private void agregarTarea() {
        if (id_proyecto_seleccionado == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un proyecto antes de agregar una tarea.");
            return;
        }

        String nombre = JOptionPane.showInputDialog(null, "Nombre de la tarea:");
        String fecha = JOptionPane.showInputDialog(null, "Fecha (YYYY-MM-DD):");

        if (nombre != null && !nombre.trim().isEmpty() && fecha != null && !fecha.trim().isEmpty()) {
            try (Connection conn = ConexionBD.getConnection()) {
                String sql = "INSERT INTO tareas (id_proyecto, nombre, fecha) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id_proyecto_seleccionado);
                stmt.setString(2, nombre.trim());
                stmt.setString(3, fecha.trim());
                stmt.executeUpdate();
                cargarTareas(id_proyecto_seleccionado);
                JOptionPane.showMessageDialog(null, "Tarea agregada correctamente.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error agregando tarea: " + ex.getMessage());
            }
        }
    }

    //Permite eliminar una tarea específica
    private void eliminarTarea(int id_tarea) {
        int confirm = JOptionPane.showConfirmDialog(null, "¿Eliminar esta tarea?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = ConexionBD.getConnection()) {
                String sql = "DELETE FROM tareas WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id_tarea);
                stmt.executeUpdate();
                cargarTareas(id_proyecto_seleccionado);
                JOptionPane.showMessageDialog(null, "Tarea eliminada correctamente.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error eliminando tarea: " + ex.getMessage());
            }
        }
    }

    //Actualiza el label que muestra el nombre del proyecto seleccionado
    private void actualizarLabelProyectoSeleccionado(String nombreProyecto) {
        label_proyecto_seleccionado.setText(
                nombreProyecto != null && !nombreProyecto.isEmpty()
                        ? "Proyecto seleccionado: " + nombreProyecto
                        : ""
        );
    }
}
