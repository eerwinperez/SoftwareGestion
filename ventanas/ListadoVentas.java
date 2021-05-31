/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.ImageIcon;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author erwin
 */
public class ListadoVentas extends javax.swing.JFrame {

    TableRowSorter trs;
    DefaultTableModel modelo;
    String usuario, permiso;

    /**
     * Creates new form RegistroVentas
     */
    public ListadoVentas() {
        initComponents();      
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

    }

    public ListadoVentas(String usuario, String permiso) {
        initComponents();
        this.usuario = usuario;
        this.permiso = permiso;
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado historico de ventas *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void IniciarCaracteristicasGenerales() {
        InhabilitarCampos();
        SettearModelo();
        LlenarTabla();
    }

    public void SettearModelo() {
        modelo = (DefaultTableModel) jTable_tablapedisos.getModel();
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(SwingConstants.RIGHT);
        jTable_tablapedisos.getColumnModel().getColumn(6).setCellRenderer(tcr);
    }

    public void LlenarTabla() {

        try {
            String consulta = "select ventas.Idventa, ventas.FechaventaSistema, clientes.nombreCliente, ventas.descripcionTrabajo, "
                    + "ventas.Cantidad, ventas.tipoTrabajo, ventas.precio, ventas.tamaño, ventas.colorTinta, "
                    + "ventas.numeracionInicial, ventas.numeracionFinal, ventas.acabado, ventas.papelOriginal, "
                    + "ventas.copia1, ventas.copia2, ventas.copia3, ventas.observaciones, ventas.fechaEntrega, "
                    + "clientes.tipoCliente from ventas INNER JOIN clientes where ventas.Idcliente=clientes.idCliente order by ventas.Idventa desc";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable_tablapedisos.getModel();

            while (rs.next()) {

                Object[] listado = new Object[8];

                listado[0] = rs.getString("ventas.Idventa");
                listado[1] = rs.getString("ventas.FechaventaSistema");
                listado[2] = rs.getString("clientes.nombreCliente");
                listado[3] = rs.getString("clientes.tipoCliente");
                listado[4] = rs.getString("ventas.descripcionTrabajo") + " - " + rs.getString("ventas.tamaño") + " - " + rs.getString("ventas.colorTinta");
                listado[5] = rs.getString("ventas.Cantidad");
                listado[6] = rs.getString("ventas.precio");
                listado[6] = ConvertirIntAMoneda(Double.parseDouble(listado[6].toString()));
                listado[7] = rs.getString("ventas.observaciones");

                modelo.addRow(listado);
            }

            jTable_tablapedisos.setModel(modelo);

            cn.close();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null, "Error en llenar la tabla de pedidos. Contante al administrador "
                    + "ListadoVentas LlenarTabla()");
            e.printStackTrace();
        }

    }

    public void Eliminar() {
        int fila = jTable_tablapedisos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona una fila");
        } else {
            modelo.removeRow(fila);
        }
    }

    public void LimpiarTabla() {

        for (int i = 0; i < jTable_tablapedisos.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }

    }

    public void InhabilitarCampos() {
        jTextField_cliente.setEnabled(false);
        jTextField_descripcion.setEnabled(false);
        jTextField_idVenta.setEnabled(false);
    }

    public static String ConvertirIntAMoneda(double dato) {
        String result = "";
        DecimalFormat objDF = new DecimalFormat("$ ###, ###");
        result = objDF.format(dato);

        return result;
    }

    public static String ConvertirMonedaAInt(String numero) {
        String MonedaParseada = "";

        try {
            MonedaParseada = new DecimalFormat("$ ###, ###").parse(numero).toString();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return MonedaParseada;
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
        jTable_tablapedisos = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jTextField_filtroCliente = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_idVenta = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_descripcion = new javax.swing.JTextField();
        jButton_verdetalle = new javax.swing.JButton();
        jButton_cargarVenta = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_tablapedisos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id venta", "Fecha", "Cliente", "Tipo Cliente", "Descripcion", "Cantidad", "Precio", "Observaciones"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_tablapedisos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_tablapedisos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_tablapedisosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_tablapedisos);
        if (jTable_tablapedisos.getColumnModel().getColumnCount() > 0) {
            jTable_tablapedisos.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable_tablapedisos.getColumnModel().getColumn(1).setPreferredWidth(90);
            jTable_tablapedisos.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTable_tablapedisos.getColumnModel().getColumn(3).setPreferredWidth(70);
            jTable_tablapedisos.getColumnModel().getColumn(4).setPreferredWidth(230);
            jTable_tablapedisos.getColumnModel().getColumn(5).setPreferredWidth(70);
            jTable_tablapedisos.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTable_tablapedisos.getColumnModel().getColumn(7).setPreferredWidth(190);
        }

        jLabel4.setText("Filtrar Cliente");

        jTextField_filtroCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_filtroClienteKeyTyped(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Venta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel2.setText("Id Venta");

        jLabel1.setText("Cliente");

        jLabel3.setText("Descripcion");

        jButton_verdetalle.setText("Ver detalle");
        jButton_verdetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_verdetalleActionPerformed(evt);
            }
        });

        jButton_cargarVenta.setText("Cargar venta");
        jButton_cargarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cargarVentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_idVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_verdetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_cargarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_verdetalle)
                        .addComponent(jButton_cargarVenta))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jTextField_idVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_filtroCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 988, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_filtroCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_tablapedisosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_tablapedisosMouseClicked

        int fila = jTable_tablapedisos.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione una fila para editar el pedido");
        } else {

            jTextField_cliente.setText(jTable_tablapedisos.getValueAt(fila, 2).toString());
            jTextField_descripcion.setText(jTable_tablapedisos.getValueAt(fila, 4).toString());
            jTextField_idVenta.setText(jTable_tablapedisos.getValueAt(fila, 0).toString());

//            String idVenta=jTable_tablapedisos.getValueAt(fila, 0).toString();
//            new DetallePedido(usuario, permiso, idVenta).setVisible(true);
//            dispose();
        }


    }//GEN-LAST:event_jTable_tablapedisosMouseClicked

    private void jButton_verdetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_verdetalleActionPerformed
        //Verificamos que se haya seleccionado un pedido               
        String idVenta = jTextField_idVenta.getText().trim();
        if (!idVenta.equals("")) {
            new DetallePedido(usuario, permiso, idVenta).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una de las ventas");
        }


    }//GEN-LAST:event_jButton_verdetalleActionPerformed

    private void jButton_cargarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cargarVentaActionPerformed
        //Verificamos que se haya seleccionado un pedido               
        String idVenta = jTextField_idVenta.getText().trim();
        String cliente = jTextField_cliente.getText().trim();
        if (!idVenta.equals("")) {
            new RegistroVentas(usuario, permiso, cliente, idVenta).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una de las ventas");
        }

    }//GEN-LAST:event_jButton_cargarVentaActionPerformed

    private void jTextField_filtroClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_filtroClienteKeyTyped
        jTextField_filtroCliente.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                trs.setRowFilter(RowFilter.regexFilter("(?i)" + jTextField_filtroCliente.getText(), 2));
            }

        });

        trs = new TableRowSorter(jTable_tablapedisos.getModel());
        jTable_tablapedisos.setRowSorter(trs);
    }//GEN-LAST:event_jTextField_filtroClienteKeyTyped

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
            java.util.logging.Logger.getLogger(ListadoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoVentas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_cargarVenta;
    private javax.swing.JButton jButton_verdetalle;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_tablapedisos;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_descripcion;
    private javax.swing.JTextField jTextField_filtroCliente;
    private javax.swing.JTextField jTextField_idVenta;
    // End of variables declaration//GEN-END:variables
}
