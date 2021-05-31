package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ConsolidadoPendienteFacturar extends javax.swing.JFrame {
    
    String usuario, permiso;
    DefaultTableModel modelo;
    
    public ConsolidadoPendienteFacturar() {
        initComponents();
        LlenarComboBoxClientes();
        SettearModelo();
        llenarTabla();
        ConfiguracionGralJFrame();
        
    }
    
    public ConsolidadoPendienteFacturar(String usuario, String permiso) {
        initComponents();
        this.usuario = usuario;
        this.permiso = permiso;
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
        
    }
    
    public void ConfiguracionGralJFrame(){
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado de pedidos pendientes por facturar *** "+"Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
    }
    
    public void IniciarCaracteristicasGenerales(){
        LlenarComboBoxClientes();
        SettearModelo();
        llenarTabla();        
    }
    
    public void SettearModelo() {
        modelo = (DefaultTableModel) jTable_tablaPendientes.getModel();
    }
    
    public void llenarTabla() {
        
        modelo = (DefaultTableModel) jTable_tablaPendientes.getModel();
        
        String consulta = "select ventas.Idventa, ventas.Idcliente, ventas.FechaventaSistema, clientes.nombreCliente, "
                + "ventas.Cantidad, ventas.Cantidad - IF(sum(elementosfactura.cantidadFacturada) is null, 0, "
                + "sum(elementosfactura.cantidadFacturada)) as cantidadPendiente, ventas.descripcionTrabajo, "
                + "ventas.tamaño, ventas.colorTinta, ventas.unitario from ventas left join elementosfactura ON "
                + "ventas.Idventa=elementosfactura.idVenta join clientes on ventas.Idcliente=clientes.idCliente "
                + "where clientes.tipoCliente='Empresa' GROUP by ventas.Idventa HAVING cantidadPendiente>0";
        
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                
                Object[] datos = new Object[8];
                datos[0] = rs.getString("ventas.Idventa");
                datos[1] = rs.getString("ventas.FechaventaSistema");
                datos[2] = rs.getString("clientes.nombreCliente");
                datos[3] = rs.getString("ventas.descripcionTrabajo") + " - " + rs.getString("ventas.tamaño") + " - " + rs.getString("ventas.colorTinta");
                datos[4] = rs.getString("ventas.Cantidad");
                int cantidadPendiente = rs.getInt("cantidadPendiente");
                datos[5] = rs.getInt("cantidadPendiente");
                int unitario=rs.getInt("ventas.unitario");
                datos[6] = MetodosGenerales.ConvertirIntAMoneda(unitario);
                //datos[6] = rs.getInt("ventas.unitario");
                //datos[7] = MetodosGenerales.ConvertirIntAMoneda((Integer.parseInt(datos[5].toString())) * (Integer.parseInt(datos[6].toString())));
                datos[7] = MetodosGenerales.ConvertirIntAMoneda(cantidadPendiente * unitario);
                
                modelo.addRow(datos);
            }
            
            jTable_tablaPendientes.setModel(modelo);
            
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer las ventas pendientes por facturar. Contacte al administrador. "
                    + "ConsolidadoPendienteFacturar, llenarTabla()");
            e.printStackTrace();
        }
        
    }
    
    public void GenerarReporte(JTable tabla) {
        
        //String rutaArchivoACopiar = "D:" + File.separator + "Erwin" + File.separator + "ApacheNetbeans" + File.separator + "GraficasJireh_1" + File.separator + "Pendientes por facturar consolidado.xlsx";
        String rutaArchivoACopiar ="Docs" + File.separator + "Pendientes por facturar consolidado.xlsx";
        String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Pendientes por facturar consolidado.xlsx";
        try {
            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();
            
            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            //Cargamos los datos de la cabecera
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda12 = fila1.getCell(2);
            celda12.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            
            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda22 = fila2.getCell(2);
            celda22.setCellValue(this.usuario);

            //Dado que la informacion se empezara a cargar desde la fila 5, establecemos ese inicio
            int filaInicio = 5;
            //Recorremos el numero de filas que tiene la tabla
            for (int i = 0; i < tabla.getRowCount(); i++) {
                XSSFRow fila = hoja.getRow(filaInicio);
                //Recorremos ahora las columas para obtener los datos
                for (int j = 0; j < tabla.getColumnCount(); j++) {
                    XSSFCell celda = fila.getCell(j);
                    //En funcion de la columna en la que nos encontremos se tomará el tipo de dato para el reporte
                    switch (j) {
                        case 1:
                            celda.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(tabla.getValueAt(i, j).toString()));
                            break;
                        case 0,4,5:
                            celda.setCellValue(Double.parseDouble(tabla.getValueAt(i, j).toString()));   
                            break;
                        case 6,7:
                            celda.setCellValue(Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(tabla.getValueAt(i, j).toString())));
                            break;
                        default:
                            celda.setCellValue(tabla.getValueAt(i, j).toString());
                    }
                    
                }
                filaInicio++;
                
            }
            FileOutputStream nuevo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(nuevo);
            nuevo.close();
            
            MetodosGenerales.abrirArchivo(rutaParaGuardar);
            
        } catch (IOException | NumberFormatException | ParseException e) {
            JOptionPane.showMessageDialog(null, "Error en generar el reporte de deuda de Empresas ");
            e.printStackTrace();
        }
        
    }
    
    public void LlenarComboBoxClientes(){
        
        ArrayList<String> listaClientes = new ArrayList<>();
        
        String consulta = "select clientes.nombreCliente, ventas.Cantidad - "
                + "IF(sum(elementosfactura.cantidadFacturada) is null, 0, sum(elementosfactura.cantidadFacturada)) "
                + "as cantidadPendiente from ventas left join elementosfactura ON "
                + "ventas.Idventa=elementosfactura.idVenta join clientes on ventas.Idcliente=clientes.idCliente "
                + "where clientes.tipoCliente='Empresa' GROUP by ventas.Idventa HAVING cantidadPendiente>0";
        
        Connection cn = Conexion.Conectar();
        try {
           PreparedStatement pst = cn.prepareStatement(consulta);
           ResultSet rs = pst.executeQuery();
           
            while (rs.next()) {                
                String nuevo=rs.getString("clientes.nombreCliente");
                listaClientes.add(nuevo);
            }
            cn.close();
            
            //Eliminamos los repetidos
            Set<String> hashSet = new HashSet<String>(listaClientes);
            listaClientes.clear();
            listaClientes.addAll(hashSet);
            
            for (String cliente : hashSet) {
                jComboBox_cliente.addItem(cliente);
            }
            
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer los clientes con deudas ConsolidadoPendientePorFacturar \n "
                    + "LlenarComboBoxClientes() " +e);
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
        jTable_tablaPendientes = new javax.swing.JTable();
        jButton_generarInforme = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jComboBox_cliente = new javax.swing.JComboBox<>();
        jButton_calcular = new javax.swing.JButton();
        jTextField_subtotal = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_tablaPendientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "IdVenta", "Fecha", "Cliente", "Descripcion", "Cant. vend", "Cant. pend. Fact", "P. Unitario", "Saldo pend."
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_tablaPendientes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(jTable_tablaPendientes);
        if (jTable_tablaPendientes.getColumnModel().getColumnCount() > 0) {
            jTable_tablaPendientes.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable_tablaPendientes.getColumnModel().getColumn(1).setPreferredWidth(90);
            jTable_tablaPendientes.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTable_tablaPendientes.getColumnModel().getColumn(3).setPreferredWidth(250);
            jTable_tablaPendientes.getColumnModel().getColumn(4).setPreferredWidth(70);
            jTable_tablaPendientes.getColumnModel().getColumn(5).setPreferredWidth(70);
            jTable_tablaPendientes.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTable_tablaPendientes.getColumnModel().getColumn(7).setPreferredWidth(100);
        }

        jButton_generarInforme.setText("Generar Informe");
        jButton_generarInforme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generarInformeActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Consultar total sin facturar por cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel8.setText("Totalizar cliente");

        jComboBox_cliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos" }));

        jButton_calcular.setText("Calcular");
        jButton_calcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_calcularActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(57, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jComboBox_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_calcular)
                .addGap(18, 18, 18)
                .addComponent(jTextField_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_calcular)
                    .addComponent(jTextField_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 934, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_generarInforme, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jButton_generarInforme)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_generarInformeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generarInformeActionPerformed
        //Verificamos que haya elementos en la tabla
        int cantidadFilas = jTable_tablaPendientes.getRowCount();
        if (cantidadFilas > 0) {
            GenerarReporte(jTable_tablaPendientes);
        } else {
            JOptionPane.showMessageDialog(this, "No hay datos para generar el informe");
        }
    }//GEN-LAST:event_jButton_generarInformeActionPerformed

    private void jButton_calcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_calcularActionPerformed
        //Verificamos que la tabla tenga datos
        int numeroFilas = jTable_tablaPendientes.getRowCount();
        if (numeroFilas > 0) {
            //Sumamos los valor segun lo seleccionado
            String tipo = jComboBox_cliente.getSelectedItem().toString();
            //Declaramos la variable que acumulara la suma
            int suma = 0;
            if (!tipo.equals("Todos")) {

                for (int i = 0; i < numeroFilas; i++) {
                    if (jTable_tablaPendientes.getValueAt(i, 2).equals(tipo)) {
                        suma += Integer.parseInt(MetodosGenerales.ConvertirMonedaAInt(jTable_tablaPendientes.getValueAt(i, 7).toString()));
                        jTextField_subtotal.setText(MetodosGenerales.ConvertirIntAMoneda(suma));
                    }
                }
            } else {
                for (int i = 0; i < numeroFilas; i++) {
                    suma += Integer.parseInt(MetodosGenerales.ConvertirMonedaAInt(jTable_tablaPendientes.getValueAt(i, 7).toString()));
                    jTextField_subtotal.setText(MetodosGenerales.ConvertirIntAMoneda(suma));
                }
            }

        } else {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla");
        }

    }//GEN-LAST:event_jButton_calcularActionPerformed

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
            java.util.logging.Logger.getLogger(ConsolidadoPendienteFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsolidadoPendienteFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsolidadoPendienteFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsolidadoPendienteFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConsolidadoPendienteFacturar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_calcular;
    private javax.swing.JButton jButton_generarInforme;
    private javax.swing.JComboBox<String> jComboBox_cliente;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_tablaPendientes;
    private javax.swing.JTextField jTextField_subtotal;
    // End of variables declaration//GEN-END:variables
}
