import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        // Crea una ventana principal (JFrame) con el título "inicio".
        JFrame frame = new JFrame("Inicio de Sesión");

        // Establece el contenido de la ventana usando el panel principal de la clase 'inicio'.
        frame.setContentPane(new inicio().panel_principal);

        // Configura la operación por defecto al cerrar la ventana:
        // EXIT_ON_CLOSE indica que se cerrara la aplicación completamente.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ajusta el tamaño de la ventana al tamaño preferido de sus componentes.
        frame.pack();

        // Centra la ventana en la pantalla.
        frame.setLocationRelativeTo(null);

        // Hace visible la ventana para que el usuario pueda interactuar con ella.
        frame.setVisible(true);
    }
}
