/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author erwin
 */
public class ListadoAbonosEntradasDiarias extends javax.swing.JFrame {

    String usuario, permiso;
    DefaultTableModel modelo;

    /**
     * Creates new form ImprimirRecibo
     */
    public ListadoAbonosEntradasDiarias() {
        initComponents();
        IniciarCaracteristicasGenerales();

        ConfiguracionGralJFrame();
    }

    public ListadoAbonosEntradasDiarias(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        IniciarCaracteristicasGenerales();

        ConfiguracionGralJFrame();
    }
    
    public void IniciarCaracteristicasGenerales(){
        
        jLabel_idVenta.setVisible(false);
        jTextField_idAbono.setEnabled(false);
        jTextField_cliente.setEnabled(false);
        SettearModelo();
        llenarTabla();
    }
    
    public void ConfiguracionGralJFrame(){
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado de abonos registrados *** "+"Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
    }

    public void SettearModelo() {
        this.modelo = (DefaultTableModel) jTable_abonos.getModel();
    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_abonos.getRowCount(); i++) {
            this.modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void llenarTabla() {
        try {
            String consulta = "select abonos.idAbono, abonos.fechaAbonoSistema, abonos.idVenta, clientes.nombreCliente, "
                    + "abonos.valorAbono, abonos.registradoPor from abonos inner join ventas on "
                    + "abonos.idVenta=ventas.Idventa inner join clientes on ventas.Idcliente=clientes.idCliente "
                    + "ORDER BY abonos.idAbono desc";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable_abonos.getModel();

            while (rs.next()) {
                Object[] listadoAbonos = new Object[6];

                listadoAbonos[0] = rs.getString("abonos.idAbono");
                listadoAbonos[1] = rs.getString("abonos.idVenta");
                listadoAbonos[2] = rs.getString("abonos.fechaAbonoSistema");
                listadoAbonos[3] = rs.getString("clientes.nombreCliente");
//                listadoAbonos[4] = rs.getString("abonos.valorAbono");
                listadoAbonos[4] = MetodosGenerales.ConvertirIntAMoneda(rs.getInt("abonos.valorAbono"));
                listadoAbonos[5] = rs.getString("abonos.registradoPor");

                modelo.addRow(listadoAbonos);
            }
            jTable_abonos.setModel(modelo);
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos en la tabla abonos desde la base de datos."
                    + "ImprimirRecibos LlenarTabla()");
        }
    }

    public String[] LeerDatosAbono(String idAbono, String idVenta) {

        String datosRecibo[] = new String[13];

        String consulta = "select clientes.nombreCliente, clientes.identificacion, clientes.telefono, "
                + "ventas.descripcionTrabajo, ventas.Cantidad, ventas.tamaño, abonos.valorAbono, "
                + "(select if(sum(abonos.valorAbono) is null, 0, sum(abonos.valorAbono)) as totalAbonos "
                + "from abonos where abonos.idVenta=?) as totalAbonos, ventas.precio, abonos.registradoPor "
                + "from clientes inner join ventas on clientes.idCliente=ventas.Idcliente inner join abonos on "
                + "ventas.Idventa=abonos.idVenta and abonos.idAbono=?";

        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, idVenta);
            pst.setString(2, idAbono);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                datosRecibo[0] = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                datosRecibo[1] = idAbono;
                datosRecibo[2] = rs.getString("clientes.nombreCliente");
                datosRecibo[3] = rs.getString("clientes.identificacion");
                datosRecibo[4] = rs.getString("clientes.telefono");
                datosRecibo[5] = rs.getString("ventas.descripcionTrabajo");
                datosRecibo[6] = rs.getString("ventas.Cantidad");
                datosRecibo[7] = rs.getString("ventas.tamaño");
                datosRecibo[8] = rs.getString("abonos.valorAbono");
                datosRecibo[9] = rs.getString("totalAbonos");
                datosRecibo[10] = rs.getString("ventas.precio");                             
                datosRecibo[11] = String.valueOf(Integer.parseInt(datosRecibo[10])-Integer.parseInt(datosRecibo[9]));
                datosRecibo[12] = rs.getString("abonos.registradoPor");

            }
                cn.close();
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en leer los datos del abono ImprimirRecibo LeerDatosAbono()");

        }

        return datosRecibo;
    }

    public void GenerarRecibo(String[] datosRecibo, String idVenta) {

        //String rutaArchivoACopiar = "D:"+File.separator+"Erwin"+File.separator+"ApacheNetbeans"+File.separator+"GraficasJireh_1"+File.separator+"Recibo.xlsx";
        String rutaArchivoACopiar="Docs" + File.separator + "Recibo.xlsx";
        String rutaParaGuardar = System.getProperty("user.home")+File.separator+"Desktop"+File.separator+"Recibo "+datosRecibo[1]+" Cliente "+datosRecibo[2]+".xlsx";
        
        //Leemos el archivo que vamos a modificar y creamos el XSSFWorkbook
        try {
            
            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);

            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            //Datos de la primera fila
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda15 = fila1.getCell(5);
            celda15.setCellValue(new SimpleDateFormat("dd-MM-yyyy").parse(datosRecibo[0]));
            XSSFCell celda115 = fila1.getCell(15);
            celda115.setCellValue(Double.valueOf(datosRecibo[1]));

            //Datos segunda fila
            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda25 = fila2.getCell(5);
            celda25.setCellValue(datosRecibo[2]);
            XSSFCell celda210 = fila2.getCell(10);
            celda210.setCellValue(datosRecibo[3]);
            XSSFCell celda215 = fila2.getCell(15);
            celda215.setCellValue(datosRecibo[4]);

            //Datos tercera fila
            XSSFRow fila3 = hoja.getRow(3);
            XSSFCell celda35 = fila3.getCell(5);
            celda35.setCellValue(datosRecibo[5]);
            XSSFCell celda312 = fila3.getCell(12);
            celda312.setCellValue(datosRecibo[6]);
            XSSFCell celda315 = fila3.getCell(15);
            celda315.setCellValue(datosRecibo[7]);

            //Datos cuarta fila
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda45 = fila4.getCell(5);
            celda45.setCellValue(Double.valueOf(datosRecibo[8]));
            XSSFCell celda49 = fila4.getCell(9);
            celda49.setCellValue(Double.valueOf(datosRecibo[9]));

            //Datos quinta fila
            XSSFRow fila5 = hoja.getRow(5);
            XSSFCell celda55 = fila5.getCell(5);
            celda55.setCellValue(Double.valueOf(datosRecibo[10]));
            XSSFCell celda58 = fila5.getCell(8);
            celda58.setCellValue(Double.valueOf(datosRecibo[11]));
            XSSFCell celda514 = fila5.getCell(14);
            celda514.setCellValue(Double.valueOf(idVenta));
            
            //Datos octava fila
            XSSFRow fila8 = hoja.getRow(7);
            XSSFCell celda87 = fila8.getCell(5);
            celda87.setCellValue(datosRecibo[12]);

            FileOutputStream ultimo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(ultimo);
            ultimo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en general el recibo. Asegurate de no tenr abierto otro recibo. Si el"
                    + " problema persiste, contacta al administrador. ImprimirRecibo GenerarRecibo()");
            e.printStackTrace();
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_abonos = new javax.swing.JTable();
        jLabel_idVenta = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTextField_idAbono = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jButton_generarRecibo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_abonos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Abono", "No. Venta", "Fecha abono", "Cliente", "Valor abono", "Registrado por"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_abonos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_abonos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_abonosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_abonos);
        if (jTable_abonos.getColumnModel().getColumnCount() > 0) {
            jTable_abonos.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable_abonos.getColumnModel().getColumn(1).setPreferredWidth(70);
            jTable_abonos.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable_abonos.getColumnModel().getColumn(3).setPreferredWidth(250);
            jTable_abonos.getColumnModel().getColumn(4).setPreferredWidth(120);
            jTable_abonos.getColumnModel().getColumn(5).setPreferredWidth(120);
        }

        jLabel_idVenta.setText("jLabel1");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Abono a imprimir", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("No. Abono");

        jLabel2.setText("Cliente");

        jButton_generarRecibo.setText("Generar Recibo");
        jButton_generarRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generarReciboActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_idAbono, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jButton_generarRecibo)
                .addContainerGap(111, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_idAbono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_generarRecibo))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel_idVenta)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_idVenta)
                .addGap(27, 27, 27)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_abonosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_abonosMouseClicked

        int fila = jTable_abonos.getSelectedRow();
        //Verificamos que se haya seleccionado un abono
        if (fila != -1) {
            //Capturamos los datos para hacer la consulta de la informacion del abono en la base de datos
            jTextField_idAbono.setText(jTable_abonos.getValueAt(fila, 0).toString().trim());
            jLabel_idVenta.setText(jTable_abonos.getValueAt(fila, 1).toString().trim());
            jTextField_cliente.setText(jTable_abonos.getValueAt(fila, 3).toString().trim());
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona una fila valida");
        }

    }//GEN-LAST:event_jTable_abonosMouseClicked

    private void jButton_generarReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generarReciboActionPerformed
        //Verificamos que se haya seleccionado un abono para imprimir
        String idAbono = jTextField_idAbono.getText().trim();
        if (!idAbono.equals("")) {
            //Una vez verificado, capturamos el idventa
            String idVenta = jLabel_idVenta.getText().trim();
            String[] datosRecibo = LeerDatosAbono(idAbono, idVenta);
            GenerarRecibo(datosRecibo, idVenta);

        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un abono para generar el recibo");
        }

    }//GEN-LAST:event_jButton_generarReciboActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ListadoAbonosEntradasDiarias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoAbonosEntradasDiarias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoAbonosEntradasDiarias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoAbonosEntradasDiarias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoAbonosEntradasDiarias().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_generarRecibo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel_idVenta;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_abonos;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_idAbono;
    // End of variables declaration//GEN-END:variables
}
