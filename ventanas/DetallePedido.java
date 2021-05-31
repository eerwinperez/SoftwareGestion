
package ventanas;

import clases.Conexion;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;


public class DetallePedido extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso, idVenta;

    public DetallePedido() {
        initComponents();
        llenarComboBoxes();

        ConfiguracionGralJFrame();

    }

    public DetallePedido(String usuario, String permiso, String idVenta) {
        initComponents();
        this.usuario = usuario;
        this.permiso = permiso;
        this.idVenta = idVenta;
        llenarComboBoxes();
        CargarDatos(this.idVenta);
        if (!permiso.equals("Gerente") && !permiso.equals("Administrador")) {
            inhabilitarCampos();
        }
        
        
        ConfiguracionGralJFrame();

        
    }
    
    public void ConfiguracionGralJFrame(){
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Detalle del pedido *** "+"Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
    }

    public void llenarComboBoxes() {

        ArrayList<String> listaVendedores = new ArrayList<>();

        try {

            String consultavendedor = "select nombreComisionista from comisionistas";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultavendedor);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String vendedor = rs.getString("nombreComisionista");
                listaVendedores.add(vendedor);
            }

            for (String vendedor : listaVendedores) {
                jComboBox_vendedor.addItem(vendedor);
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en leer los comisionistas RegistroVentas llenarComboBoxes()");
        }

        ArrayList<String> listaTipoTrabajo = new ArrayList<>();
        try {

            String consultaTipo = "select tipo from tipotrabajo";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaTipo);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                listaTipoTrabajo.add(tipo);
            }

            for (String tipo : listaTipoTrabajo) {
                jComboBox_tipotrabajo.addItem(tipo);
            }

            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en leer los tipos de trabajo RegistroVentas llenarComboBoxes()");
        }

        ArrayList<String> listaPapeles = new ArrayList<String>();
        try {

            String consultaPapel = "select nombrePapel from papeles";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaPapel);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String tipo = rs.getString("nombrePapel");
                listaPapeles.add(tipo);
            }

            for (String tipo : listaPapeles) {
                jComboBox_original.addItem(tipo);
                jComboBox_copia1.addItem(tipo);
                jComboBox_copia2.addItem(tipo);
                jComboBox_copia3.addItem(tipo);

            }

            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en leer los tipos de trabajo RegistroVentas llenarComboBoxes()");
        }

    }

    public void Limpiar() {

        jTextField_descripcion.setText("");
        jTextField_cantidad.setText("");
        jComboBox_tipotrabajo.setSelectedItem(0);
        jTextField_precio.setText("");
        jTextField_tamaño.setText("");
        jTextField_colorTinta.setText("");
        jTextField_numeroInicial.setText("");
        jTextField_numerofinal.setText("");
        jTextField_acabado.setText("");
        jComboBox_original.setSelectedIndex(0);
        jComboBox_copia1.setSelectedIndex(0);
        jComboBox_copia2.setSelectedIndex(0);
        jComboBox_copia3.setSelectedIndex(0);
        jTextField_observaciones.setText("");

    }

    public void inhabilitarCampos() {
        
    }

    public void CargarDatos(String idVenta) {
        String consulta = "select ventas.Idventa, ventas.FechaventaSistema, clientes.nombreCliente, ventas.Vendedor, "
                + "ventas.descripcionTrabajo, ventas.Cantidad, ventas.tipoTrabajo, ventas.precio, ventas.tamaño, ventas.colorTinta, "
                + "ventas.numeracionInicial, ventas.numeracionFinal, ventas.acabado, ventas.papelOriginal, "
                + "ventas.copia1, ventas.copia2, ventas.copia3, ventas.observaciones, ventas.fechaEntrega, clientes.tipoCliente from ventas INNER JOIN clientes on "
                + "ventas.Idcliente=clientes.idCliente where ventas.Idventa=?";

        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idVenta);
            
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                jLabel_cliente.setText(rs.getString("clientes.nombreCliente"));
                jComboBox_vendedor.setSelectedItem(rs.getString("ventas.Vendedor"));
                jTextField_descripcion.setText(rs.getString("ventas.descripcionTrabajo"));
                jTextField_cantidad.setText(rs.getString("ventas.Cantidad"));
                jComboBox_tipotrabajo.setSelectedItem(rs.getString("ventas.tipoTrabajo"));
                jTextField_tamaño.setText(rs.getString("ventas.tamaño"));
                jTextField_colorTinta.setText(rs.getString("ventas.colorTinta"));
                jTextField_numeroInicial.setText(rs.getString("ventas.numeracionInicial"));
                jTextField_numerofinal.setText(rs.getString("ventas.numeracionFinal"));
                jTextField_acabado.setText(rs.getString("ventas.acabado"));
                jComboBox_original.setSelectedItem(rs.getString("ventas.papelOriginal"));
                jComboBox_copia1.setSelectedItem(rs.getString("ventas.copia1"));
                jComboBox_copia2.setSelectedItem(rs.getString("ventas.copia2"));
                jComboBox_copia3.setSelectedItem(rs.getString("ventas.copia3"));
                jTextField_observaciones.setText(rs.getString("ventas.observaciones"));
                jDateChooser_fechaentrega.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("ventas.fechaEntrega")));
                jTextField_precio.setText(rs.getString("ventas.precio"));
                jLabel_idVenta.setText(rs.getString("ventas.Idventa"));
            }

            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en consultar el detalle del pedido en la base de datos. DetallePedidos CargarDatos()");
        e.printStackTrace();
        }

    }

    public int ConsultarAbonos(String idVenta) {
        int abonos = 0;

        String consulta = "select abonos from ventas where Idventa='" + idVenta + "'";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                abonos = rs.getInt("abonos");
            }

            cn.close();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null, "Error en consultar el saldo de la venta No. " + idVenta + " en la base de datos."
                    + " ConsultarAbono() DetallePedido");
            e.printStackTrace();
        }

        return abonos;
    }

    public int ConsultarSaldo(String idVenta) {
        int saldo = 0;

        String consulta = "select saldo from ventas where Idventa='" + idVenta + "'";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                saldo = rs.getInt("saldo");
            }

            cn.close();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null, "Error en consultar el saldo de la venta No. " + idVenta + " en la base de datos."
                    + " ConsultarAbono() DetallePedido");
            e.printStackTrace();
        }

        return saldo;
    }

    public int BuscarIdCliente(String cliente) {
        int IdCliente = -1;

        try {
            String consulta = "select idCliente from clientes where nombreCliente =?";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, cliente);
            
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                IdCliente = rs.getInt("idCliente");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en encontrar el Id correspondiente al cliente seleccionado");
            e.printStackTrace();
        }

        return IdCliente;
    }

    public void ActualizarPedido(String vendedor, String idCliente, String cantidad, String descripcion, 
            String tipoTrabajo, double unitario, String precio, String tamaño, String fecha, String colorTinta, 
            String numeroInicial, String numeroFinal, String acabado, String original, String copia1, String copia2, 
            String copia3, String observaciones, String usuario, String idVenta){
        
        String consulta = "update ventas set Vendedor=?, Idcliente=?, Cantidad=?, descripcionTrabajo=?, "
                        + "tipoTrabajo=?, unitario=?, precio=?, tamaño=?, fechaEntrega=?, "
                        + "colorTinta=?, numeracionInicial=?, numeracionFinal=?, "
                        + "acabado=?, papelOriginal=?, copia1=?, copia2=?, "
                        + "copia3=?, observaciones=?, registradoPor=? where Idventa=?";

                try {
                    Connection cn = Conexion.Conectar();
                    PreparedStatement pst = cn.prepareStatement(consulta);
                    
                    pst.setString(1, vendedor);
                    pst.setString(2, idCliente);
                    pst.setString(3, cantidad);
                    pst.setString(4, descripcion);
                    pst.setString(5, tipoTrabajo);
                    pst.setDouble(6, unitario);
                    pst.setString(7, precio);
                    pst.setString(8, tamaño);
                    pst.setString(9, fecha);
                    pst.setString(10, colorTinta);
                    pst.setString(11, numeroInicial);
                    pst.setString(12, numeroFinal);
                    pst.setString(13, acabado);
                    pst.setString(14, original);
                    pst.setString(15, copia1);
                    pst.setString(16, copia2);
                    pst.setString(17, copia3);
                    pst.setString(18, observaciones);                                    
                    pst.setString(19, usuario);
                    pst.setString(20, idVenta);
                    
                    
                    pst.executeUpdate();
                    cn.close();
                    JOptionPane.showMessageDialog(null, "Datos de pedido actualizado");
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error en actualizar la ventas #" + this.idVenta + ". Contacte al administrador. DetallePedido ActualizarPedido()");
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

        jButton_editarInformacion = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel_cliente = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel_idVenta = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox_vendedor = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField_descripcion = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_cantidad = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBox_tipotrabajo = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jTextField_tamaño = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField_colorTinta = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField_numeroInicial = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField_numerofinal = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jTextField_acabado = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jComboBox_original = new javax.swing.JComboBox<>();
        jComboBox_copia1 = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jComboBox_copia2 = new javax.swing.JComboBox<>();
        jComboBox_copia3 = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jTextField_observaciones = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jDateChooser_fechaentrega = new com.toedter.calendar.JDateChooser();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTextField_precio = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton_editarInformacion.setText("Editar informacion");
        jButton_editarInformacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_editarInformacionActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel_cliente.setText("jLabel5");

        jLabel1.setText("Id Venta");

        jLabel_idVenta.setText("jLabel19");

        jLabel3.setText("Cliente: ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_idVenta)
                    .addComponent(jLabel_cliente))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel_idVenta))
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel_cliente))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Vendedor / Comisionista", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel2.setText("Vendedor");

        jComboBox_vendedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No aplica" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jComboBox_vendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_vendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Detalle del pedido", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel5.setText("Descripcion");

        jTextField_descripcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_descripcionKeyTyped(evt);
            }
        });

        jLabel4.setText("Cantidad");

        jTextField_cantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_cantidadKeyTyped(evt);
            }
        });

        jLabel6.setText("Tipo");

        jComboBox_tipotrabajo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No aplica" }));
        jComboBox_tipotrabajo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_tipotrabajoActionPerformed(evt);
            }
        });

        jLabel8.setText("Tamaño");

        jTextField_tamaño.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_tamañoKeyTyped(evt);
            }
        });

        jLabel10.setText("Color tinta");

        jTextField_colorTinta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_colorTintaKeyTyped(evt);
            }
        });

        jLabel11.setText("Numero inicial");

        jTextField_numeroInicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_numeroInicialKeyTyped(evt);
            }
        });

        jLabel9.setText("Numero final");

        jTextField_numerofinal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_numerofinalKeyTyped(evt);
            }
        });

        jLabel13.setText("Acabado");

        jTextField_acabado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_acabadoKeyTyped(evt);
            }
        });

        jLabel14.setText("Papel original");

        jLabel15.setText("Copia 1");

        jComboBox_original.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No aplica" }));

        jComboBox_copia1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No aplica" }));
        jComboBox_copia1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_copia1ActionPerformed(evt);
            }
        });

        jLabel16.setText("Copia 2");

        jLabel17.setText("Copia 3");

        jComboBox_copia2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No aplica", " " }));

        jComboBox_copia3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No aplica" }));

        jLabel18.setText("Observaciones del pedido");

        jTextField_observaciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_observacionesKeyTyped(evt);
            }
        });

        jLabel12.setText("Fecha entrega");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox_tipotrabajo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_tamaño, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(22, 22, 22)
                                .addComponent(jTextField_colorTinta, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11)
                                .addGap(38, 38, 38)
                                .addComponent(jTextField_numeroInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField_numerofinal, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField_acabado, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel15))
                                .addGap(27, 27, 27)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox_copia3, 0, 300, Short.MAX_VALUE)
                                    .addComponent(jComboBox_copia2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jComboBox_original, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jComboBox_copia1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jLabel17)
                            .addComponent(jLabel16)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(27, 27, 27)
                                .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(jLabel12)
                                .addGap(18, 18, 18)
                                .addComponent(jDateChooser_fechaentrega, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField_tamaño, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox_tipotrabajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_colorTinta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField_numeroInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField_numerofinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jTextField_acabado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jComboBox_original, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jComboBox_copia1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jComboBox_copia2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jComboBox_copia3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18)
                        .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jDateChooser_fechaentrega, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setText("Precio de venta");

        jTextField_precio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_precioKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_precio, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField_precio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(jButton_editarInformacion)))
                .addGap(0, 29, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jButton_editarInformacion)))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox_tipotrabajoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_tipotrabajoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_tipotrabajoActionPerformed

    private void jButton_editarInformacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_editarInformacionActionPerformed

        //Capturamos los datos del formulario
        String idCliente = String.valueOf(BuscarIdCliente(jLabel_cliente.getText().trim()));
//        String cliente = jLabel_cliente.getText().trim();
        String vendedor = jComboBox_vendedor.getSelectedItem().toString();
        String descripcion = jTextField_descripcion.getText().trim();
        String cantidad = jTextField_cantidad.getText().trim();
        String tipoTrabajo = jComboBox_tipotrabajo.getSelectedItem().toString();
        String tamaño = jTextField_tamaño.getText().trim();
        String precio=jTextField_precio.getText().trim();
        double unitario = Double.parseDouble(precio)/Double.parseDouble(cantidad);
        String colorTinta = jTextField_colorTinta.getText().trim();
        String numeroInicial = jTextField_numeroInicial.getText().trim();
        String numeroFinal = jTextField_numerofinal.getText().trim();
        String acabado = jTextField_acabado.getText().trim();
        String original = jComboBox_original.getSelectedItem().toString();
        String copia1 = jComboBox_copia1.getSelectedItem().toString();
        String copia2 = jComboBox_copia2.getSelectedItem().toString();
        String copia3 = jComboBox_copia3.getSelectedItem().toString();
        String observaciones = jTextField_observaciones.getText().trim();

        String fecha;
        if (jDateChooser_fechaentrega.getDate() == null) {
            fecha = null;
        } else {
            fecha = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_fechaentrega.getDate());
        }


            //Verificamos que todos los campos que son obligatorios esten completos
            if (!descripcion.equals("") && !cantidad.equals("") && !tipoTrabajo.equals("") && !tamaño.equals("") && !colorTinta.equals("")
                    && fecha != null && !cantidad.equals("") && !precio.equals("")) {

                ActualizarPedido(vendedor, idCliente, cantidad, descripcion, tipoTrabajo, unitario, precio, tamaño, fecha, colorTinta, numeroInicial, 
                        numeroFinal, acabado, original, copia1, copia2, copia3, observaciones, this.usuario, this.idVenta);              
                
                dispose();
                new ListadoVentas(this.usuario, this.permiso).setVisible(true);

            } else {
                JOptionPane.showMessageDialog(null, "Los campos DESCRIPCION, CANTIDAD, TIPO, TAMAÑO, COLOR, FECHA DE ENTREGA Y "
                        + "PRECIO DE VENTA son obligatorios");
            }



    }//GEN-LAST:event_jButton_editarInformacionActionPerformed

    private void jComboBox_copia1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_copia1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_copia1ActionPerformed

    private void jTextField_descripcionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_descripcionKeyTyped
        if (jTextField_descripcion.getText().trim().length()==250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_descripcionKeyTyped

    private void jTextField_cantidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_cantidadKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
        if (c=='0' && jTextField_cantidad.getText().trim().length()==0) {
            evt.consume();
        }
        
        if (jTextField_cantidad.getText().trim().length()==11) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_cantidadKeyTyped

    private void jTextField_tamañoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_tamañoKeyTyped
        if (jTextField_tamaño.getText().trim().length()==50) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_tamañoKeyTyped

    private void jTextField_colorTintaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_colorTintaKeyTyped
        if (jTextField_colorTinta.getText().trim().length()==150) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_colorTintaKeyTyped

    private void jTextField_numeroInicialKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_numeroInicialKeyTyped
        if (jTextField_numeroInicial.getText().trim().length()==50) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_numeroInicialKeyTyped

    private void jTextField_numerofinalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_numerofinalKeyTyped
        if (jTextField_numerofinal.getText().trim().length()==50) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_numerofinalKeyTyped

    private void jTextField_acabadoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_acabadoKeyTyped
        if (jTextField_acabado.getText().trim().length()==70) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_acabadoKeyTyped

    private void jTextField_observacionesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_observacionesKeyTyped
        if (jTextField_observaciones.getText().trim().length()==250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_observacionesKeyTyped

    private void jTextField_precioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_precioKeyTyped
        char c = evt.getKeyChar();
        
        if (!Character.isDigit(c)) {
            evt.consume();
        }
        if (c=='0' && jTextField_precio.getText().trim().length()==0) {
            evt.consume();
        }
        
        if (jTextField_precio.getText().trim().length()==11) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_precioKeyTyped

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
            java.util.logging.Logger.getLogger(DetallePedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DetallePedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DetallePedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DetallePedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DetallePedido().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_editarInformacion;
    private javax.swing.JComboBox<String> jComboBox_copia1;
    private javax.swing.JComboBox<String> jComboBox_copia2;
    private javax.swing.JComboBox<String> jComboBox_copia3;
    private javax.swing.JComboBox<String> jComboBox_original;
    private javax.swing.JComboBox<String> jComboBox_tipotrabajo;
    private javax.swing.JComboBox<String> jComboBox_vendedor;
    private com.toedter.calendar.JDateChooser jDateChooser_fechaentrega;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_cliente;
    private javax.swing.JLabel jLabel_idVenta;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jTextField_acabado;
    private javax.swing.JTextField jTextField_cantidad;
    private javax.swing.JTextField jTextField_colorTinta;
    private javax.swing.JTextField jTextField_descripcion;
    private javax.swing.JTextField jTextField_numeroInicial;
    private javax.swing.JTextField jTextField_numerofinal;
    private javax.swing.JTextField jTextField_observaciones;
    private javax.swing.JTextField jTextField_precio;
    private javax.swing.JTextField jTextField_tamaño;
    // End of variables declaration//GEN-END:variables
}
