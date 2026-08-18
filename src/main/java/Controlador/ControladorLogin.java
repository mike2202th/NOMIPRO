package Controlador;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import ConexionDB.LoginDAO;
import ConexionDB.Login;
import Vistas.Login1;
import Main.Menu;
import java.sql.SQLException;


public class ControladorLogin implements ActionListener {


    private LoginDAO dao = new LoginDAO();
    private Login1 login1;

    public ControladorLogin(Login1 login1) {

        this.login1 = login1;

        this.login1.btnRegistrar.addActionListener(this);

        limpiarCampos();
    }
    public void limpiarCampos() {
        login1.txtUsuario.setText("");
        login1.jpwClave.setText("");
    }


     public boolean validarCampos() {

        if (login1.txtUsuario.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                login1,
                "El campo de usuario no debe estar vacío!",
                "Error!",
                JOptionPane.ERROR_MESSAGE
            );

            login1.txtUsuario.requestFocus();
            return false;
        }

        if (login1.jpwClave.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                login1,
                "El campo de contraseña no debe estar vacío!",
                "Error!",
                JOptionPane.ERROR_MESSAGE
            );

            login1.jpwClave.requestFocus();
            return false;
        }

        return true;
    }

    public void AccionarLogin() throws SQLException {

        if (!validarCampos()) {
            return;
        }

        String usuario = login1.txtUsuario.getText();
        String clave = new String(login1.jpwClave.getPassword());

        System.out.println("Usuario: " + usuario);
        System.out.println("Verificando datos...");

        String nombre = dao.realizarLogin(usuario, clave);

        if (nombre != null) {

    JOptionPane.showMessageDialog(
        login1,
        "Ingresado con éxito!",
        "Éxito!",
        JOptionPane.INFORMATION_MESSAGE
    );

    login1.dispose();

    Menu m = new Menu(nombre);

    m.setLocationRelativeTo(null);
    m.setVisible(true);

        } else {

    JOptionPane.showMessageDialog(
        login1,
        "El usuario o la clave son incorrectos.",
        "Error!",
        JOptionPane.ERROR_MESSAGE
    );

    limpiarCampos();
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == login1.btnRegistrar) {

            System.out.println("BOTÓN PRESIONADO");

            try {
                AccionarLogin();
            } catch (SQLException ex) {
                System.getLogger(ControladorLogin.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
    }

}
