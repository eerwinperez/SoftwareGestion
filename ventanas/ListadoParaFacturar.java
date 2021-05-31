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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author erwin
 */
public class ListadoParaFacturar extends javax.swing.JFrame {

    DefaultTableModel modelo, modeloAFacturar;
    String usuario, permiso, cliente, idCliente;

    /**
     * Creates new form ListadoPendiente
     */
    public ListadoParaFacturar() {

        initComponents();

 
        SettearModelo();
        llenarTabla();
        InhabilitarCampos();
        ConfiguracionGralJFrame();
   
    }

    public ListadoParaFacturar(String usuario, String permiso, String cliente, String idCliente) {
        this.usuario = usuario;
        this.permiso = permiso;
        this.cliente = cliente;
        this.idCliente = idCliente;
        initComponents();
        IniciarCaracteristicasGenerales();
   
        ConfiguracionGralJFrame();
        

 
    }
    
    public void SettearDatos(){
        jTextField_idcliente.setText(this.idCliente);
        jTextField_cliente.setText(this.cliente);
          
    }
    
    public void IniciarCaracteristicasGenerales(){
        SettearModelo();
        llenarTabla();
        SettearDatos();
        InhabilitarCampos();
    }
    
    public void ConfiguracionGralJFrame(){
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Facturacion *** "+"Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
    }

    public void SettearModelo() {
        modelo = (DefaultTableModel) jTable_listadopendientes.getModel();
        modeloAFacturar = (DefaultTableModel) jTable_elementosfactura.getModel();
    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_listadopendientes.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }
    
    public void InhabilitarCampos(){
        
        jLabel_fila.setVisible(false);
        jLabel_cantidad.setVisible(false);
        jTextField_idVenta.setEnabled(false);
        jTextField_descripcion.setEnabled(false);
        jTextField_unitario.setEnabled(false);
        jTextField_subtotal.setEnabled(false);
        jTextField_cliente.setEnabled(false);
        jTextField_idcliente.setEnabled(false);
        jTextField_total.setEnabled(false);
        
    }

    public boolean VerificarSiHayFacturas() {
        boolean siHay = true;

        return siHay;
    }

    public void llenarTabla() {

        String consulta = "select ventas.Idventa, ventas.Cantidad - IF(sum(elementosfactura.cantidadFacturada) is null, 0, sum(elementosfactura.cantidadFacturada)) as cantidadPendiente, "
                + "ventas.descripcionTrabajo, ventas.tamaño, ventas.colorTinta, ventas.unitario from ventas "
                + "left join elementosfactura ON ventas.Idventa=elementosfactura.idVenta where ventas.Idcliente=? "
                + "GROUP by ventas.Idventa";

        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, this.idCliente);

            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable_listadopendientes.getModel();

            while (rs.next()) {
                Object[] empleados = new Object[5];

                empleados[0] = rs.getString("ventas.Idventa");
                empleados[1] = rs.getInt("cantidadPendiente");
                empleados[2] = rs.getString("ventas.descripcionTrabajo") + " - " + rs.getString("ventas.tamaño") + " - " + rs.getString("ventas.colorTinta");
                empleados[3] = rs.getDouble("ventas.unitario");
                //empleados[3] = MetodosGenerales.ConvertirIntAMoneda(rs.getDouble("ventas.unitario"));
                empleados[4] = Math.round((int) empleados[1] * (double) empleados[3]);
                empleados[4] = MetodosGenerales.ConvertirIntAMoneda(Math.round((int) empleados[1] * (double) empleados[3]));

                if ((int) empleados[1] > 0) {
                    modelo.addRow(empleados);
                }

            }
            jTable_listadopendientes.setModel(modelo);

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos en la tabla pendientes desde la base de datos");
            e.printStackTrace();
        }
    }

    public void EliminarItemDelListado(String fila) {

        modelo = (DefaultTableModel) jTable_listadopendientes.getModel();
        modelo.removeRow(Integer.parseInt(fila));
        jTable_listadopendientes.setModel(modelo);

    }

    public void limpiarTabla() {
        jTextField_cantidad.setText("");
        jTextField_condicionPago.setText("");
        jTextField_descripcion.setText("");
        jTextField_idVenta.setText("");
        jTextField_subtotal.setText("");
        jTextField_unitario.setText("");
        jLabel_fila.setText("");
        jLabel_cantidad.setText("");
    }

    public void AgregarATablaFacturar(String idVenta, String cantidad, String descripcion, String pUnitario, int subtotal) {
        modeloAFacturar = (DefaultTableModel) jTable_elementosfactura.getModel();

        Object[] datos = new Object[5];
        datos[0] = idVenta;
        datos[1] = cantidad;
        datos[2] = descripcion;
        datos[3] = pUnitario;
        datos[4] = subtotal;
        modeloAFacturar.addRow(datos);

        jTable_elementosfactura.setModel(modeloAFacturar);
    }

    public void CargarFactura(JTable tablafacturas, String fecha, String condiciondePago, int aleatorioSeguridad, int valorFacturado) {

        try {
            String consulta = "insert into facturas (fechaFactura, idCliente, condiciondePago, "
                    + "codigoSeguridad, estadoPago, valorFacturado, saldo, registradoPor) values ('" + fecha + "', '" + this.idCliente + "', "
                    + "'" + condiciondePago + "', '" + aleatorioSeguridad + "', 'Pendiente', '" + valorFacturado + "', '" + valorFacturado + "', '" + this.usuario + "')";

            
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.executeUpdate();
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en insertar la factura ListadoParaFacturar CargarFactura()");
            e.printStackTrace();
        }

        //Obtenemos la factura recientemente registrada
        String factura = "";
        try {
            String consulta2 = "select idFactura from facturas where codigoSeguridad='" + aleatorioSeguridad + "' and fechaFactura='" + fecha + "'";
            Connection cn2 = Conexion.Conectar();
            PreparedStatement pst2 = cn2.prepareStatement(consulta2);
            ResultSet rs2 = pst2.executeQuery();

            while (rs2.next()) {
                factura = rs2.getString("idFactura");
            }
            System.out.println("La factura es " + factura);
            cn2.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error en consultar el numero de factura ListadoParaFacturar CargarFactura()");
        }

        //Registramos elemento a elemento seleccionado relacionando en cada uno el numero de factura.
        for (int i = 0; i < tablafacturas.getRowCount(); i++) {

            String idVenta = tablafacturas.getValueAt(i, 0).toString();
            String cantidad = tablafacturas.getValueAt(i, 1).toString();
            String pUnitario = tablafacturas.getValueAt(i, 3).toString();
            String subtotal = tablafacturas.getValueAt(i, 4).toString();

            String consulta3 = "insert into elementosfactura (idVenta, cantidadFacturada, precioUnitario, subtotal, factura) "
                    + "values ('" + idVenta + "', '" + cantidad + "', '" + pUnitario + "', '" + subtotal + "', '" + factura + "')";
            System.out.println(consulta3);
            try {
                Connection cn3 = Conexion.Conectar();
                PreparedStatement pst3 = cn3.prepareStatement(consulta3);
                pst3.executeUpdate();
                cn3.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error en registrar el elemento de la factura. ListadoParaFactura CargarFactura()");
                e.printStackTrace();
            }
        }
        
        

    }

    public static String ConvertirIntAMoneda(double dato) {
        String result = "";
        DecimalFormat objDF = new DecimalFormat("$ ###, ###");
        result = objDF.format(dato);

        return result;
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
        jTable_listadopendientes = new javax.swing.JTable();
        jButton_facturar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField_condicionPago = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable_elementosfactura = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel_fila = new javax.swing.JLabel();
        jLabel_cantidad = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTextField_idcliente = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextField_idVenta = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField_cantidad = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField_descripcion = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField_unitario = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField_subtotal = new javax.swing.JTextField();
        jButton_calcular = new javax.swing.JButton();
        jButton_agregar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jTextField_total = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_listadopendientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id venta", "Cantidad", "Descripcion", "P. unitario", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_listadopendientes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_listadopendientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_listadopendientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_listadopendientes);
        if (jTable_listadopendientes.getColumnModel().getColumnCount() > 0) {
            jTable_listadopendientes.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable_listadopendientes.getColumnModel().getColumn(1).setPreferredWidth(70);
            jTable_listadopendientes.getColumnModel().getColumn(2).setPreferredWidth(290);
            jTable_listadopendientes.getColumnModel().getColumn(3).setPreferredWidth(110);
            jTable_listadopendientes.getColumnModel().getColumn(4).setPreferredWidth(110);
        }

        jButton_facturar.setText("Facturar");
        jButton_facturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_facturarActionPerformed(evt);
            }
        });

        jLabel3.setText("Condicion de pago");

        jTable_elementosfactura.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id venta", "Cantidad", "Descripcion", "P. unitario", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_elementosfactura.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_elementosfactura.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_elementosfacturaMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable_elementosfactura);
        if (jTable_elementosfactura.getColumnModel().getColumnCount() > 0) {
            jTable_elementosfactura.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable_elementosfactura.getColumnModel().getColumn(1).setPreferredWidth(70);
            jTable_elementosfactura.getColumnModel().getColumn(2).setPreferredWidth(290);
            jTable_elementosfactura.getColumnModel().getColumn(3).setPreferredWidth(110);
            jTable_elementosfactura.getColumnModel().getColumn(4).setPreferredWidth(110);
        }

        jLabel9.setText("LISTADO DE PEDIDOS PENDIENTES POR FACTURAR");

        jLabel10.setText("LISTADO DE PEDIDOS A FACTURAR");

        jLabel_fila.setText("jLabel8");

        jLabel_cantidad.setText("jLabel8");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel8.setText("Id:");

        jLabel12.setText("Cliente:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_idcliente, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(90, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField_idcliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informacion del Item a facturar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel4.setText("Id Venta");

        jLabel11.setText("Cantidad");

        jTextField_cantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_cantidadActionPerformed(evt);
            }
        });
        jTextField_cantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_cantidadKeyTyped(evt);
            }
        });

        jLabel5.setText("Descripcion");

        jLabel6.setText("P.Unit");

        jLabel7.setText("Subtotal");

        jButton_calcular.setText("Calcular");
        jButton_calcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_calcularActionPerformed(evt);
            }
        });

        jButton_agregar.setText("Agregar a factura");
        jButton_agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_idVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(24, 24, 24)
                        .addComponent(jTextField_unitario, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_calcular)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_agregar)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_idVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel11)
                    .addComponent(jTextField_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField_unitario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_agregar)
                    .addComponent(jButton_calcular))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Consultar total de factura", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jButton1.setText("Calcular");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(163, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField_total, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jTextField_total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(67, 67, 67)
                                .addComponent(jLabel_fila)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel_cantidad))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField_condicionPago, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton_facturar, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel_fila)
                        .addComponent(jLabel_cantidad)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_condicionPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_facturar))
                .addGap(23, 23, 23))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_listadopendientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_listadopendientesMouseClicked
        int fila = jTable_listadopendientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un elemento de la tabla");
        } else {
            String idVenta = jTable_listadopendientes.getValueAt(fila, 0).toString();
            String cantidad = jTable_listadopendientes.getValueAt(fila, 1).toString();
            String descripcion = jTable_listadopendientes.getValueAt(fila, 2).toString();
            String pUnitario = jTable_listadopendientes.getValueAt(fila, 3).toString();
            String subtotal = MetodosGenerales.ConvertirMonedaAInt(jTable_listadopendientes.getValueAt(fila, 4).toString());

            jTextField_idVenta.setText(idVenta);
            jTextField_cantidad.setText(cantidad);
            jTextField_descripcion.setText(descripcion);
            jTextField_unitario.setText(pUnitario);
            jTextField_subtotal.setText(subtotal);
            jLabel_fila.setText(String.valueOf(fila));
            jLabel_cantidad.setText(cantidad);
        }

    }//GEN-LAST:event_jTable_listadopendientesMouseClicked

    private void jButton_facturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_facturarActionPerformed

        int aleatorioSeguridad = (int) (Math.random() * 100000000);//Un aleatorio que me permite registrar en dos tablas en simultaneao
        //teniendo en cuenta que una de las tablas depende de la otra
        String condiciondePago = jTextField_condicionPago.getText().trim();
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        //Verificamos inicialmente que la tabla tenga datos cargados y que la condicion de pago este completada
        if (jTable_elementosfactura.getRowCount() > 0 && !condiciondePago.equals("")) {
            //Validamos que no se hayan facturado mas de 15 items por que esto es lo maximo que admite la plantilla excel de factura

            if (jTable_elementosfactura.getRowCount() <= 15) {

                //Obtenemos el valor total de la factura para ponerlo en el campo valorFacturado de la tabla facturas
                int valorFacturado = 0;
                for (int i = 0; i < jTable_elementosfactura.getRowCount(); i++) {
                    valorFacturado += Integer.parseInt(jTable_elementosfactura.getValueAt(i, 4).toString());
                }
                
                CargarFactura(jTable_elementosfactura, fecha, condiciondePago, aleatorioSeguridad, valorFacturado);
                dispose();
                new ListadoFacturasPendientesPago(this.usuario, this.permiso).setVisible(true);
                
                
            } else {
                JOptionPane.showMessageDialog(this, "No es posible facturar mas de 15 items. Elimine elementos de la lista");
            }

        } else {
            //Si no hay datos en la tabla se envia mensaje informativo
            JOptionPane.showMessageDialog(null, "No hay datos en la tabla para facturar o asegurese de que el campo condicion de pago este completo.");
        }

    }//GEN-LAST:event_jButton_facturarActionPerformed

    private void jTable_elementosfacturaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_elementosfacturaMouseClicked
        int fila = jTable_elementosfactura.getSelectedRow();
        if (fila != -1) {
            modelo = (DefaultTableModel) jTable_elementosfactura.getModel();
            modelo.removeRow(fila);
            jTable_elementosfactura.setModel(modelo);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila valida");
        }
    }//GEN-LAST:event_jTable_elementosfacturaMouseClicked

    private void jTextField_cantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_cantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_cantidadActionPerformed

    private void jTextField_cantidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_cantidadKeyTyped

        char tecla = evt.getKeyChar();
        if (!Character.isDigit(tecla)) {
            evt.consume();
        }
        if (tecla=='0' && jTextField_cantidad.getText().trim().length()==0) {
            evt.consume();
        }
        if (jTextField_cantidad.getText().trim().length()==11) {
            evt.consume();
        }


    }//GEN-LAST:event_jTextField_cantidadKeyTyped

    private void jButton_calcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_calcularActionPerformed

        try {
            jTextField_subtotal.setText(String.valueOf(Math.round(Float.parseFloat(jTextField_cantidad.getText()) * Float.parseFloat(jTextField_unitario.getText()))));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Asegurese dehaber ingresado la cantidad correctamente. No se admiten"
                    + " espacios. Use punto (.) para indicar cantidades decimales");
        }

    }//GEN-LAST:event_jButton_calcularActionPerformed

    private void jButton_agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregarActionPerformed

        String idVenta = jTextField_idVenta.getText().trim();
        String cantidad = jTextField_cantidad.getText().trim();
        String descripcion = jTextField_descripcion.getText().trim();
        String pUnitario = jTextField_unitario.getText().trim();

        //Verificamos que se haya seleccionado un elemento de la lista para facturar
        if (!idVenta.equals("")) {

            //Verificamos que se haya completado la cantidad            
            if (!cantidad.equals("")) {

                //Verificamos que no se facture una cantidad mayor a la cantidad pendiente por facturar
                if (Integer.parseInt(cantidad) <= Integer.parseInt(jLabel_cantidad.getText().trim())) {
                    //Verificamos que se haya seleccionado un elemento de la lista
                    if (!idVenta.equals("")) {
                        //Al presionar agregar, sacamos del listado de pendientes por facturar el item seleccionado
                        int subtotal = (int) Math.round(Double.parseDouble(pUnitario) * (Integer.parseInt(cantidad)));
                        EliminarItemDelListado(jLabel_fila.getText().trim());

                        AgregarATablaFacturar(idVenta, cantidad, descripcion, pUnitario, subtotal);
                        limpiarTabla();

                    } else {
                        JOptionPane.showMessageDialog(null, "Debes seleccionar un elemento del listado de pedidos para agregarlo a la factura");

                    }
                } else {
                    int confirmacion = JOptionPane.showConfirmDialog(this, "¿Desea facturar mayor cantidad a la registrada en la venta?");
                    if (confirmacion == 0) {
//                        String razon = JOptionPane.showInputDialog(this, "Deje una nota");
//                        if (razon.length() > 0) {
                        //Al presionar agregar, sacamos del listado de pendientes por facturar el item seleccionado
                        int subtotal = (int) Math.round(Double.parseDouble(pUnitario) * (Integer.parseInt(cantidad)));
                        EliminarItemDelListado(jLabel_fila.getText().trim());

                        AgregarATablaFacturar(idVenta, cantidad, descripcion, pUnitario, subtotal);
                        limpiarTabla();

//                        } else {
//                            JOptionPane.showMessageDialog(this, "Deje una nota");
//                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Edite la cantidad a facturas, esta no debe ser mayor a la cantidad registrada en la venta");
                    }
                }

            } else {
                JOptionPane.showMessageDialog(this, "Complete la cantidad");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un elemento de la lista de pedidos por facturar");
        }


    }//GEN-LAST:event_jButton_agregarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (jTable_elementosfactura.getRowCount() > 0) {
            int suma = 0;
            for (int i = 0; i < jTable_elementosfactura.getRowCount(); i++) {
                suma += Integer.parseInt(jTable_elementosfactura.getValueAt(i, 4).toString());
            }
            jTextField_total.setText(ConvertirIntAMoneda(suma));
        } else {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(ListadoParaFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoParaFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoParaFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoParaFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoParaFacturar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_agregar;
    private javax.swing.JButton jButton_calcular;
    private javax.swing.JButton jButton_facturar;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_cantidad;
    private javax.swing.JLabel jLabel_fila;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable_elementosfactura;
    private javax.swing.JTable jTable_listadopendientes;
    private javax.swing.JTextField jTextField_cantidad;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_condicionPago;
    private javax.swing.JTextField jTextField_descripcion;
    private javax.swing.JTextField jTextField_idVenta;
    private javax.swing.JTextField jTextField_idcliente;
    private javax.swing.JTextField jTextField_subtotal;
    private javax.swing.JTextField jTextField_total;
    private javax.swing.JTextField jTextField_unitario;
    // End of variables declaration//GEN-END:variables
}
