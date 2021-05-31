/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author erwin
 */
public class ListadoDeAbonosFacturas extends javax.swing.JFrame {
    
    String usuario, permiso;
    DefaultTableModel modelo;

    /**
     * Creates new form ListadoDeAbonosFacturas
     */
    public ListadoDeAbonosFacturas() {
        initComponents();
        IniciarCaracteristicasGenerales();
        
        ConfiguracionGralJFrame();
    }
    
    public ListadoDeAbonosFacturas(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
    }
    
    public void IniciarCaracteristicasGenerales(){
          settearModelo();
          LlenarTabla();      
    }
    
    public void ConfiguracionGralJFrame(){
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado abonos de facturas *** "+"Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
    }
    
    public void settearModelo() {        
        modelo = (DefaultTableModel) jTable_listadoAbonos.getModel();
        
    }
    
    public void LlenarTabla() {
        
        modelo = (DefaultTableModel) jTable_listadoAbonos.getModel();
        
        String consulta = "select abonosfacturas.fechaAbonoSistema, abonosfacturas.idAbono, abonosfacturas.idFactura, "
                + "abonosfacturas.valorAbono, clientes.nombreCliente, abonosfacturas.observaciones, "
                + "abonosfacturas.registradoPor from abonosfacturas inner join facturas on "
                + "abonosfacturas.idFactura=facturas.idFactura inner join clientes on "
                + "facturas.idCliente=clientes.idCliente";
        
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {                
                Object[] listado = new Object[7];
                listado[0] = rs.getString("abonosfacturas.fechaAbonoSistema");
                listado[1] = rs.getString("abonosfacturas.idAbono");
                listado[2] = rs.getString("abonosfacturas.idFactura");
                listado[3] = MetodosGenerales.ConvertirIntAMoneda(rs.getInt("abonosfacturas.valorAbono"));
//                listado[3] = rs.getString("abonosfacturas.valorAbono");
                listado[4] = rs.getString("clientes.nombreCliente");
                listado[5] = rs.getString("abonosfacturas.observaciones");
                listado[6] = rs.getString("abonosfacturas.registradoPor");
                
                modelo.addRow(listado);
            }
            
            jTable_listadoAbonos.setModel(modelo);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error en leer el listado de abonos de facturas. ListadoDeAbonosFacturas llenarTabla()");
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
        jTable_listadoAbonos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_listadoAbonos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha", "No. Abono", "No. Factura", "Valor abono", "Cliente", "Observaciones", "Registrado Por"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_listadoAbonos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(jTable_listadoAbonos);
        if (jTable_listadoAbonos.getColumnModel().getColumnCount() > 0) {
            jTable_listadoAbonos.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable_listadoAbonos.getColumnModel().getColumn(1).setPreferredWidth(70);
            jTable_listadoAbonos.getColumnModel().getColumn(2).setPreferredWidth(70);
            jTable_listadoAbonos.getColumnModel().getColumn(3).setPreferredWidth(120);
            jTable_listadoAbonos.getColumnModel().getColumn(4).setPreferredWidth(230);
            jTable_listadoAbonos.getColumnModel().getColumn(5).setPreferredWidth(200);
            jTable_listadoAbonos.getColumnModel().getColumn(6).setPreferredWidth(120);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 866, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(ListadoDeAbonosFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoDeAbonosFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoDeAbonosFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoDeAbonosFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoDeAbonosFacturas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_listadoAbonos;
    // End of variables declaration//GEN-END:variables
}