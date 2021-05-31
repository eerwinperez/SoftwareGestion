/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author erwin
 */
public class ListadoRemitir extends javax.swing.JFrame {

    String usuario, permiso, cliente;
    DefaultTableModel modeloPendientes, modeloRemito;

    /**
     * Creates new form ListadoRemitir
     */
    public ListadoRemitir() {
        initComponents();
        InhabilitarCampos();
        SetearModeloTablas();
        LlenarTabla(ConsultarIdCliente(this.cliente));
        ConfiguracionGralJFrame();
    }

    public ListadoRemitir(String usuario, String permiso, String cliente) {
        this.usuario = usuario;
        this.permiso = permiso;
        this.cliente = cliente;

        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
    }
    
    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Generacion de remisiones *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void IniciarCaracteristicasGenerales() {
        SetearDatos();
        InhabilitarCampos();
        SetearModeloTablas();
        LlenarTabla(ConsultarIdCliente(this.cliente));
    }

    public void SetearDatos() {
        jTextField_cliente.setText(this.cliente);
        jTextField_idCliente.setText(ConsultarIdCliente(cliente));
    }

    public void SetearModeloTablas() {
        modeloPendientes = (DefaultTableModel) jTable_pendientes.getModel();
        modeloRemito = (DefaultTableModel) jTable_remito.getModel();
    }

    public void InhabilitarCampos() {
        jTextField_cliente.setEnabled(false);
        jTextField_idCliente.setEnabled(false);
        jLabel_cantidad.setVisible(false);
        jLabel_fila.setVisible(false);
        jLabel_filaEliminar.setVisible(false);
        jTextField_IdVenta.setEnabled(false);
        jTextField_descripcion.setEnabled(false);
        jTextField_IdVentaEliminar.setEnabled(false);
        jTextField_descripcionEliminar.setEnabled(false);
        jTextField_cantidadEliminar.setEnabled(false);

    }

    public void LimpiarCamposPendiente() {
        jLabel_cantidad.setText("");
        jLabel_fila.setText("");
        jTextField_IdVenta.setText("");
        jTextField_descripcion.setText("");
        jTextField_cantidad.setText("");
    }

    public void LimpiarCamposRemito() {
        jLabel_filaEliminar.setText("");
        jTextField_IdVentaEliminar.setText("");
        jTextField_cantidadEliminar.setText("");
        jTextField_descripcionEliminar.setText("");
    }

    public void LlenarTabla(String idCliente) {
        modeloPendientes = (DefaultTableModel) jTable_pendientes.getModel();

        String consulta = "select ventas.Idventa, ventas.descripcionTrabajo, ventas.tamaño, ventas.colorTinta, "
                + "ventas.Cantidad -if(sum(elementosremision.cantidad)is null, 0, sum(elementosremision.cantidad)) "
                + "as saldo from ventas left join elementosremision on ventas.Idventa=elementosremision.idVenta "
                + "where ventas.Idcliente=? group by ventas.Idventa having saldo>0";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idCliente);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[3];
                nuevo[0] = rs.getString("ventas.Idventa");
                nuevo[1] = rs.getString("ventas.descripcionTrabajo") + " - " + rs.getString("ventas.tamaño") + " - " + rs.getString("ventas.colorTinta");
                nuevo[2] = rs.getString("saldo");
                modeloPendientes.addRow(nuevo);
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer las ventas asociadas al cliente seleccionado\n"
                    + "ListadoRemitir LlenarTabla\n" + e);
            e.printStackTrace();
        }

    }

    public String ConsultarIdCliente(String cliente) {

        String idCliente = "";
        String consulta = "select idCliente from clientes where nombreCliente=?";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, cliente);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                idCliente = rs.getString("idCliente");
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar el id del cliente ListadoRemitir ConsultarIdCliente()\n" + e);
        }

        return idCliente;
    }

    public void RegistrarRemito(String fecha, String idCliente, String entrega, String telefono, String observaciones,
            int aleatorio, String usuario) {

        String consulta = "insert into remision (fecha, idCliente, Entrega, telefono, observaciones, aleatorio, "
                + "registradoPor) values (?, ?, ?, ?, ?, ?, ?)";

        Connection cn = Conexion.Conectar();
        try {

            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fecha);
            pst.setString(2, idCliente);
            pst.setString(3, entrega);
            pst.setString(4, telefono);
            pst.setString(5, observaciones);
            pst.setInt(6, aleatorio);
            pst.setString(7, usuario);

            pst.execute();
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar la remision ListadoRemitir RegistrarRemito()");
            e.printStackTrace();
        }

    }

    public String ConsultarRemito(String fecha, int aleatorio) {
        String numeroRemito = "";
        String consulta = "select id from remision where fecha=? and aleatorio=?";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fecha);
            pst.setInt(2, aleatorio);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                numeroRemito = rs.getString("id");
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar el numero de remision ListadoRemitir ConsultarRemito()\n" + e);
            e.printStackTrace();
        }

        return numeroRemito;
    }

    public void RegistrarElementosRemito(String idRemito, JTable jTable_remito) {

        String consulta = "insert into elementosremision (idRemision, idVenta, cantidad) values (?, ?, ?)";
        for (int i = 0; i < jTable_remito.getRowCount(); i++) {
            Connection cn = Conexion.Conectar();
            try {
                PreparedStatement pst = cn.prepareStatement(consulta);
                pst.setString(1, idRemito);
                pst.setString(2, jTable_remito.getValueAt(i, 0).toString());
                pst.setString(3, jTable_remito.getValueAt(i, 2).toString());

                pst.execute();

                cn.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error en agregar los elemenos a la remision ListadoRemitir RegistrarElementosRemito()\n" + e);
                e.printStackTrace();
            }
        }

    }

    public void EliminarElementoDeTablaPendiente(JTable jTable_pendientes, String fila) {

        modeloPendientes = (DefaultTableModel) jTable_pendientes.getModel();
        modeloPendientes.removeRow(Integer.parseInt(fila));
        jTable_pendientes.setModel(modeloPendientes);

    }

    public void AgregarElementoATablaRemito(JTable jTable_remito, String idVenta, String descripcion, String cantidadAAgregar) {
        modeloRemito = (DefaultTableModel) jTable_remito.getModel();
        Object[] nuevo = new Object[3];
        nuevo[0] = idVenta;
        nuevo[1] = descripcion;
        nuevo[2] = cantidadAAgregar;

        modeloRemito.addRow(nuevo);
        jTable_remito.setModel(modeloRemito);

    }

    public void EliminarItemRemito(JTable jTable_remito, int fila) {
        modeloRemito = (DefaultTableModel) jTable_remito.getModel();
        modeloRemito.removeRow(fila);
        jTable_remito.setModel(modeloRemito);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_idCliente = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable_remito = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable_pendientes = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField_Entrega = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField_Telefono = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField_observaciones = new javax.swing.JTextField();
        jButton_remision = new javax.swing.JButton();
        jLabel_fila = new javax.swing.JLabel();
        jLabel_cantidad = new javax.swing.JLabel();
        jLabel_filaEliminar = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jTextField_IdVenta = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField_descripcion = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField_cantidad = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jButton_agregar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jTextField_IdVentaEliminar = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField_descripcionEliminar = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField_cantidadEliminar = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jButton_Eliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Id Cliente");

        jLabel2.setText("Cliente");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_idCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_idCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jLabel3.setText("Listado pendiente por remitir");

        jTable_remito.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id Venta", "Descripcion", "Cantidad"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_remito.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_remito.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_remitoMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable_remito);
        if (jTable_remito.getColumnModel().getColumnCount() > 0) {
            jTable_remito.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable_remito.getColumnModel().getColumn(1).setPreferredWidth(400);
            jTable_remito.getColumnModel().getColumn(2).setPreferredWidth(70);
        }

        jLabel4.setText("Listado remision");

        jTable_pendientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id Venta", "Descripcion", "Cantidad"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_pendientes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_pendientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_pendientesMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable_pendientes);
        if (jTable_pendientes.getColumnModel().getColumnCount() > 0) {
            jTable_pendientes.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable_pendientes.getColumnModel().getColumn(1).setPreferredWidth(400);
            jTable_pendientes.getColumnModel().getColumn(2).setPreferredWidth(70);
        }

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos generales de la remision", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel5.setText("Entrega");

        jLabel6.setText("Telefono");

        jLabel7.setText("Observaciones");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_Entrega, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField_observaciones)))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField_Entrega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField_Telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jButton_remision.setText("Generar remision");
        jButton_remision.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_remisionActionPerformed(evt);
            }
        });

        jLabel_fila.setText("jLabel11");

        jLabel_cantidad.setText("jLabel12");

        jLabel_filaEliminar.setText("jLabel14");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Venta a remitir", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel9.setText("Descripcion");

        jLabel10.setText("Cant");

        jLabel8.setText("Id Venta");

        jButton_agregar.setText("Agregar");
        jButton_agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField_IdVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jTextField_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton_agregar)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField_IdVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_agregar))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Item a eliminar del listado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel11.setText("Descripcion");

        jLabel12.setText("Cant");

        jLabel13.setText("Id Venta");

        jButton_Eliminar.setText("Eliminar");
        jButton_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_EliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField_IdVentaEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_descripcionEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jTextField_cantidadEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton_Eliminar)))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTextField_IdVentaEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField_descripcionEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jTextField_cantidadEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_Eliminar))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(180, 180, 180)
                                .addComponent(jLabel_fila)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel_cantidad))
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(329, 329, 329)
                                .addComponent(jLabel_filaEliminar)
                                .addGap(62, 62, 62)
                                .addComponent(jButton_remision))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 544, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 544, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(4, 4, 4)))))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel_fila)
                            .addComponent(jLabel_cantidad))
                        .addGap(5, 5, 5)
                        .addComponent(jLabel3)))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton_remision)
                            .addComponent(jLabel_filaEliminar)))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_pendientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_pendientesMouseClicked
        int fila = jTable_pendientes.getSelectedRow();
        if (fila != -1) {
            jLabel_cantidad.setText(jTable_pendientes.getValueAt(fila, 2).toString());
            jLabel_fila.setText(String.valueOf(fila));
            jTextField_IdVenta.setText(jTable_pendientes.getValueAt(fila, 0).toString());
            jTextField_descripcion.setText(jTable_pendientes.getValueAt(fila, 1).toString());
            jTextField_cantidad.setText(jTable_pendientes.getValueAt(fila, 2).toString());
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila");
        }
    }//GEN-LAST:event_jTable_pendientesMouseClicked

    private void jButton_agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregarActionPerformed
        //Verificamos que se haya seleccionado un pedido
        String idVenta = jTextField_IdVenta.getText().trim();
        if (!idVenta.equals("")) {
            //Verificamos que la cantidad a agregar no sea mayor a la cantidad pendiente
            String cantidadPendiente = jLabel_cantidad.getText().trim();
            String cantidadAAgregar = jTextField_cantidad.getText().trim();
            String descripcion = jTextField_descripcion.getText().trim();
            String fila = jLabel_fila.getText().trim();
            if (Integer.parseInt(cantidadAAgregar) <= Integer.parseInt(cantidadPendiente)) {

                EliminarElementoDeTablaPendiente(jTable_pendientes, fila);
                AgregarElementoATablaRemito(jTable_remito, idVenta, descripcion, cantidadAAgregar);
                LimpiarCamposPendiente();

            } else {
                JOptionPane.showMessageDialog(this, "No es posible agregar mayor cantidad a la cantidad pendiente");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una venta para agregar");
        }

    }//GEN-LAST:event_jButton_agregarActionPerformed

    private void jButton_remisionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_remisionActionPerformed

        String idCliente = jTextField_idCliente.getText().trim();
        String entrega = jTextField_Entrega.getText().trim();
        String telefono = jTextField_Telefono.getText().trim();
        String observaciones = jTextField_observaciones.getText().trim();
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        int aleatorio = (int) (Math.random() * 1000000000);

        //Verificamos que la tabla no este vacia
        if (jTable_remito.getRowCount() > 0) {
            //Verificamos que se hallan completado los campos obligatorios
            if (!entrega.equals("") && !telefono.equals("")) {
                //Verificamos que no sean mas de 15 elementos
                if (jTable_remito.getRowCount() <= 15) {
                    RegistrarRemito(fecha, idCliente, entrega, telefono, observaciones, aleatorio, this.usuario);
                    String idRemito = ConsultarRemito(fecha, aleatorio);
                    RegistrarElementosRemito(idRemito, jTable_remito);
                    dispose();
                    new ListadoRemitos(this.usuario, this.permiso).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "No es posible generar la remision con mas de 15 elementos\n"
                            + "Elimine elementos de la lista");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Complete los datos de la persona encargada de entregar los pedidos");
            }

        } else {
            JOptionPane.showMessageDialog(this, "No hay datos para generar la remision");
        }
    }//GEN-LAST:event_jButton_remisionActionPerformed

    private void jButton_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_EliminarActionPerformed
        //Verificamos que se haya seleccionado un elemento
        String idVenta = jTextField_IdVentaEliminar.getText().trim();
        if (!idVenta.equals("")) {
            int fila = Integer.parseInt(jLabel_filaEliminar.getText().trim());
            EliminarItemRemito(jTable_remito, fila);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una venta");
        }
    }//GEN-LAST:event_jButton_EliminarActionPerformed

    private void jTable_remitoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_remitoMouseClicked
        int fila = jTable_remito.getSelectedRow();
        if (fila != -1) {
            jTextField_IdVentaEliminar.setText(jTable_remito.getValueAt(fila, 0).toString());
            jTextField_descripcionEliminar.setText(jTable_remito.getValueAt(fila, 1).toString());
            jTextField_cantidadEliminar.setText(jTable_remito.getValueAt(fila, 2).toString());
            jLabel_filaEliminar.setText(String.valueOf(fila));
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila");
        }
    }//GEN-LAST:event_jTable_remitoMouseClicked

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
            java.util.logging.Logger.getLogger(ListadoRemitir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoRemitir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoRemitir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoRemitir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoRemitir().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Eliminar;
    private javax.swing.JButton jButton_agregar;
    private javax.swing.JButton jButton_remision;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_cantidad;
    private javax.swing.JLabel jLabel_fila;
    private javax.swing.JLabel jLabel_filaEliminar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable_pendientes;
    private javax.swing.JTable jTable_remito;
    private javax.swing.JTextField jTextField_Entrega;
    private javax.swing.JTextField jTextField_IdVenta;
    private javax.swing.JTextField jTextField_IdVentaEliminar;
    private javax.swing.JTextField jTextField_Telefono;
    private javax.swing.JTextField jTextField_cantidad;
    private javax.swing.JTextField jTextField_cantidadEliminar;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_descripcion;
    private javax.swing.JTextField jTextField_descripcionEliminar;
    private javax.swing.JTextField jTextField_idCliente;
    private javax.swing.JTextField jTextField_observaciones;
    // End of variables declaration//GEN-END:variables
}
