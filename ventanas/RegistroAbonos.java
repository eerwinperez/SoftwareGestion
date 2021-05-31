/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
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

/**
 *
 * @author erwin
 */
public class RegistroAbonos extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso;

    /**
     * Creates new form RegistroAbonos
     */
    public RegistroAbonos() {
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

        

    }

    public RegistroAbonos(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

        



    }
    
    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Registro de abonos *** " + "Usuario: " + usuario + " - " + permiso);
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
        llenarTabla();
    }
    
    public void InhabilitarCampos(){
        jTextField_cliente.setEnabled(false);
        jTextField_idventa.setEnabled(false);
        jLabel_fila.setVisible(false);
    }

    public void SettearModelo() {
        modelo = (DefaultTableModel) jTable_pendientesdeAbono.getModel();
    }

    public void LimpiarFormulario() {

        jTextField_cliente.setText("");
        jTextField_observaciones.setText("");
        jTextField_valorabono.setText("");
        jTextField_idventa.setText("");

    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_pendientesdeAbono.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void llenarTabla() {
        try {
            String consulta = "select ventas.Idventa, ventas.Idcliente, ventas.Cantidad, ventas.descripcionTrabajo, ventas.tamaño, clientes.nombreCliente, ventas.FechaventaSistema, "
                    + "ventas.precio, IF(SUM(abonos.valorAbono) is null, 0, SUM(abonos.valorAbono)) as totalAbonos from "
                    + "ventas left join clientes on ventas.Idcliente=clientes.idCliente left join abonos on "
                    + "ventas.Idventa=abonos.idVenta where clientes.tipoCliente='Persona' group by ventas.Idventa "
                    + "ORDER by ventas.Idventa DESC";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable_pendientesdeAbono.getModel();

            while (rs.next()) {
                Object[] datos = new Object[8];

                datos[0] = rs.getString("ventas.Idventa");
                datos[1] = rs.getString("ventas.FechaventaSistema");
                datos[2] = rs.getString("ventas.Idcliente");
                datos[3] = rs.getString("clientes.nombreCliente");
                datos[4] = "Cant: " + rs.getString("ventas.Cantidad") + " - " + rs.getString("ventas.descripcionTrabajo") + ", tamaño " + rs.getString("ventas.tamaño");
//                datos[5] = rs.getInt("ventas.precio");
                int venta=rs.getInt("ventas.precio");
                int abonos=rs.getInt("totalAbonos");
                datos[5] = MetodosGenerales.ConvertirIntAMoneda(rs.getInt("ventas.precio"));
                //datos[6] = rs.getInt("totalAbonos");
                datos[6] = MetodosGenerales.ConvertirIntAMoneda(rs.getInt("totalAbonos"));

                if (abonos < venta) {
                    datos[7] = venta - abonos;
                    datos[7] = MetodosGenerales.ConvertirIntAMoneda((int)datos[7]);
                    modelo.addRow(datos);
                }

            }

            jTable_pendientesdeAbono.setModel(modelo);
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos en la tabla clientes desde la base de datos");
            e.printStackTrace();
        }
    }

    public void RegistrarAbono(String IdVenta, String valorAbono, String observaciones, String registradoPor) {

        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String consulta = "insert into abonos (idVenta, valorAbono, fechaAbonoSistema, fechaAbonoReal, observaciones, "
                + "registradoPor) values (?, ?, ?, ?, ?, ?)";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, IdVenta);
            pst.setString(2, valorAbono);
            pst.setString(3, fecha);
            pst.setString(4, fecha);
            pst.setString(5, observaciones);
            pst.setString(6, registradoPor);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Abono registrado");
            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al registrar el abono. RegistroAbonos RegistrarAbono()");
            e.printStackTrace();
        }

    }

    public void SaldarVenta(String idVenta, String Saldado) {

        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String consulta = "update ventas set fechaSaldado=?, estadoCuenta=? where Idventa=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, fecha);
            pst.setString(2, Saldado);
            pst.setString(3, idVenta);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "El estado del pedido ha sido actualizado a saldado");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en cambiar el estado del pedido. RegistroAbonos SaldarVenta()");
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
        jTable_pendientesdeAbono = new javax.swing.JTable();
        jButton_imprimirAbono = new javax.swing.JButton();
        jLabel_fila = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_idventa = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_valorabono = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_observaciones = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_pendientesdeAbono.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id venta", "Fecha pedido", "Id Cliente", "Cliente", "Descripcion", "Precio de venta", "Total abonos", "Saldo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_pendientesdeAbono.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_pendientesdeAbono.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_pendientesdeAbonoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_pendientesdeAbono);
        if (jTable_pendientesdeAbono.getColumnModel().getColumnCount() > 0) {
            jTable_pendientesdeAbono.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable_pendientesdeAbono.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_pendientesdeAbono.getColumnModel().getColumn(2).setPreferredWidth(70);
            jTable_pendientesdeAbono.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTable_pendientesdeAbono.getColumnModel().getColumn(4).setPreferredWidth(240);
            jTable_pendientesdeAbono.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTable_pendientesdeAbono.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTable_pendientesdeAbono.getColumnModel().getColumn(7).setPreferredWidth(100);
        }

        jButton_imprimirAbono.setText("Imprimir recibo");
        jButton_imprimirAbono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_imprimirAbonoActionPerformed(evt);
            }
        });

        jLabel_fila.setText("jLabel4");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Registrar abono", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Id venta");

        jTextField_idventa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_idventaActionPerformed(evt);
            }
        });

        jLabel4.setText("Cliente");

        jTextField_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_clienteActionPerformed(evt);
            }
        });

        jLabel2.setText("Valor abono");

        jTextField_valorabono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_valorabonoActionPerformed(evt);
            }
        });
        jTextField_valorabono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_valorabonoKeyTyped(evt);
            }
        });

        jLabel3.setText("Observaciones");

        jTextField_observaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_observacionesActionPerformed(evt);
            }
        });
        jTextField_observaciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_observacionesKeyTyped(evt);
            }
        });

        jButton1.setText("Registrar abono");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_idventa, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_valorabono, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jButton1)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_idventa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_valorabono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(jLabel_fila))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 982, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_imprimirAbono, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jLabel_fila))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jButton_imprimirAbono)
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField_observacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_observacionesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_observacionesActionPerformed

    private void jTextField_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_clienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_clienteActionPerformed

    private void jTextField_valorabonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_valorabonoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_valorabonoActionPerformed

    private void jTable_pendientesdeAbonoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_pendientesdeAbonoMouseClicked

        int fila = jTable_pendientesdeAbono.getSelectedRow();
        if (fila != -1) {

            jTextField_cliente.setText(jTable_pendientesdeAbono.getValueAt(fila, 3).toString());
            jTextField_idventa.setText(jTable_pendientesdeAbono.getValueAt(fila, 0).toString());
            jLabel_fila.setText(String.valueOf(fila));
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido");
        }

    }//GEN-LAST:event_jTable_pendientesdeAbonoMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        String IdVenta = jTextField_idventa.getText().trim();
        //Verificamos que el usuario haya seleccionado un pedido
        if (!IdVenta.equals("")) {
            //Capturamos el numero de la fila que seleccionamos en la tabla
            int fila = Integer.parseInt(jLabel_fila.getText().trim());
            //Verificamos que el usuario haya diligenciado el campo abono y capturamos el resto de datos
            String valorAbono = jTextField_valorabono.getText().trim();
            String observaciones = jTextField_observaciones.getText().trim();
            String cliente = jTextField_cliente.getText().trim();
            if (!valorAbono.equals("") && Integer.parseInt(valorAbono) > 0) {
                //Verificamos que el abono no sea superior al saldo adeudado
                String saldo=jTable_pendientesdeAbono.getValueAt(fila, 7).toString();
                if (Integer.parseInt(valorAbono) <= Integer.parseInt(MetodosGenerales.ConvertirMonedaAInt(saldo))) {
                    //Solicitados confirmacion del usuario para registrar el pedido
                    int confirmacion = JOptionPane.showConfirmDialog(this, "Confirmacion - ¿Desea registrar el abono"
                            + " de $" + valorAbono + " al cliente " + cliente + "?");

                    if (confirmacion == 0) {

                        RegistrarAbono(IdVenta, valorAbono, observaciones, this.usuario);
                        //Si el valor ingresado es igual al saldo, actualizamos el estado del pedido a saldado
                        if (Integer.parseInt(valorAbono) == Integer.parseInt(MetodosGenerales.ConvertirMonedaAInt(saldo))) {
                            SaldarVenta(IdVenta, "Saldado");
                        }
                        //Limpiamos el formulario, limpiamos la tabla y cargamos los datos actualizados
                        LimpiarFormulario();
                        limpiarTabla(modelo);
                        llenarTabla();

                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No es posible registrar un valor de abono mayor al saldo adeudado");
                }

            } else {
                JOptionPane.showMessageDialog(null, "Complete el campo abono con un valor superior a cero");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido para registrar el abono");
        }


    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton_imprimirAbonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_imprimirAbonoActionPerformed

        new ListadoAbonosEntradasDiarias(usuario, permiso).setVisible(true);

    }//GEN-LAST:event_jButton_imprimirAbonoActionPerformed

    private void jTextField_idventaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_idventaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_idventaActionPerformed

    private void jTextField_valorabonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_valorabonoKeyTyped
       char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
        if (c=='0' && jTextField_valorabono.getText().length()==0) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_valorabonoKeyTyped

    private void jTextField_observacionesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_observacionesKeyTyped
        if (jTextField_observaciones.getText().trim().length()==250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_observacionesKeyTyped

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
            java.util.logging.Logger.getLogger(RegistroAbonos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroAbonos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroAbonos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroAbonos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistroAbonos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_imprimirAbono;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel_fila;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_pendientesdeAbono;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_idventa;
    private javax.swing.JTextField jTextField_observaciones;
    private javax.swing.JTextField jTextField_valorabono;
    // End of variables declaration//GEN-END:variables
}
