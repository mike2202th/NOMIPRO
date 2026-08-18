/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package Main;

import Controlador.ControladorLogin;
import Vistas.Login1;

/**
 *
 * @author maico
 */
public class SistemadeNomina {

    public static void main(String[] args) {
       
        Login1 login1 = new Login1();
        ControladorLogin cl = new ControladorLogin(login1);
        login1.setVisible(true);
        login1.setLocationRelativeTo(null);     
    }
}
