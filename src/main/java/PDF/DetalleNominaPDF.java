/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PDF;

import Main.conexionMariaDB;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author maico
 */
public class DetalleNominaPDF {
    
    public void generarPDF(int idNomina) {

        Connection cn = null;

        try {

            // Conectar a la base de datos
            conexionMariaDB conectar = new conexionMariaDB();
            cn = conectar.conectar();

            // Cargar el reporte compilado
            InputStream archivoReporte = getClass()
                    .getResourceAsStream(
                            "/reportes/detalle_nomina.jasper"
                    );

            if (archivoReporte == null) {
                JOptionPane.showMessageDialog(
                        null,
                        "No se encontró el archivo detalle_nomina.jasper",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JasperReport reporte = (JasperReport) JRLoader.loadObject(
                    archivoReporte
            );

            // Parámetros
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("id_nomina", idNomina);

            // Generar el reporte
            JasperPrint imprimir = JasperFillManager.fillReport(
                    reporte,
                    parametros,
                    cn
            );

            // Nombre del archivo
            String nombreArchivo = "Detalle_Nomina_" + idNomina + ".pdf";

            // Exportar a PDF
            JasperExportManager.exportReportToPdfFile(
                    imprimir,
                    nombreArchivo
            );

            JOptionPane.showMessageDialog(
                    null,
                    "PDF generado correctamente:\n" + nombreArchivo,
                    "Reporte",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al generar el PDF:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();

        } finally {

            try {
                if (cn != null) {
                    cn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
