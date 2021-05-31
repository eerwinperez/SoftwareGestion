/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Clientes;
import clases.Conexion;
import clases.MetodosGenerales;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author erwin
 */
public class RegistroClientes extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso;

    /**
     * Creates new form RegistroClientes
     */
    public RegistroClientes() {
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

    }

    public RegistroClientes(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

        if (permiso.equals("Asistente")) {
            InhabilitarSegunPermiso();
        }

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Clientes *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void IniciarCaracteristicasGenerales() {
        InhabilitarCampos();
        llenarTabla();
        llenarComboBoxMunicipios();
    }

    public void InhabilitarSegunPermiso() {
        jButton_actualizar.setEnabled(false);
    }

    public void InhabilitarCampos() {
        jTextField_id.setEnabled(false);

    }

    public void llenarComboBoxMunicipios() {
        String consulta = "select municipio from municipios";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String municipio = rs.getString("municipio");
                jComboBox_municipio.addItem(municipio);
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los municipios\n" + e);
        }

    }

    public void LimpiarFormulario() {

        jTextField_id.setText("");
        jTextField_id.setEnabled(false);
        jTextField1_nombreCliente.setText("");
        jTextField1_nombreCliente.setEnabled(true);
        jTextField3_direccion.setText("");
        jTextField3_direccion.setEnabled(true);
        jTextField3_identificacion.setText("");
        jTextField3_identificacion.setEnabled(true);
        jTextField4_telefono.setText("");
        jTextField4_telefono.setEnabled(true);
        jComboBox_municipio.setSelectedIndex(0);
        jComboBox_municipio.setEnabled(true);
        jComboBox_sector.setSelectedIndex(0);
        jComboBox_sector.setEnabled(true);
        jTextField_email.setText("");

    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable1_clientes.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void llenarTabla() {
        try {
            String consulta = "select idCliente, nombreCliente, identificacion, tipoCliente, direccion, municipio, "
                    + "telefono, sector, email, registradoPor from clientes";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable1_clientes.getModel();

            while (rs.next()) {
                Object[] empleados = new Object[10];

                empleados[0] = rs.getString("idCliente");
                empleados[1] = rs.getString("nombreCliente");
                empleados[2] = rs.getString("identificacion");
                empleados[3] = rs.getString("direccion");
                empleados[4] = rs.getString("municipio");
                empleados[5] = rs.getString("telefono");
                empleados[6] = rs.getString("email");
                empleados[7] = rs.getString("sector");
                empleados[8] = rs.getString("tipoCliente");
                empleados[9] = rs.getString("registradoPor");

                modelo.addRow(empleados);
            }
            jTable1_clientes.setModel(modelo);
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos en la tabla clientes desde la base de datos");
        }
    }

    public void CapturarDatosTabla() {

        int fila = jTable1_clientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona una fila");
        } else {

            String Id = jTable1_clientes.getValueAt(fila, 0).toString();
            String nombreCliente = jTable1_clientes.getValueAt(fila, 1).toString();
            String identificacion = jTable1_clientes.getValueAt(fila, 2).toString();
            String direccion = jTable1_clientes.getValueAt(fila, 3).toString();
            String municipio = jTable1_clientes.getValueAt(fila, 4).toString();
            String telefono = jTable1_clientes.getValueAt(fila, 5).toString();
            String sector = jTable1_clientes.getValueAt(fila, 7).toString();
            String email = jTable1_clientes.getValueAt(fila, 6).toString();

            jTextField_id.setText(Id);
            jTextField_id.setEnabled(false);
            jTextField1_nombreCliente.setText(nombreCliente);
            jTextField3_identificacion.setText(identificacion);
            jTextField3_direccion.setText(direccion);
            jComboBox_municipio.setSelectedItem(municipio);
            jTextField4_telefono.setText(telefono);
            jComboBox_sector.setSelectedItem(sector);
            jTextField_email.setText(email);
        }
    }

//    public void ActualizarCliente() {
//
//        if (jTextField_id.getText().trim().equals("")) {
//            JOptionPane.showMessageDialog(null, "Debes seleccionar un cliente para poder editarlo");
//        } else {
//
//            int id = Integer.parseInt(jTextField_id.getText().trim());
//            String nombreCliente = jTextField1_nombreCliente.getText().trim().toUpperCase();
//            String identificacion = jTextField3_identificacion.getText().trim();
//            String tipoCliente = jComboBox_tipocliente.getSelectedItem().toString().trim();
//            String direccion = MetodosGenerales.ConvertirPrimerasLetrasMayus(jTextField3_direccion.getText().trim());
//            String municipio = jComboBox_municipio.getSelectedItem().toString();
//            String telefono = jTextField4_telefono.getText().trim();
//            String sector = jComboBox_sector.getSelectedItem().toString().trim();
//            String email = jTextField_email.getText().trim();
//
//            if (!nombreCliente.equals("") && !direccion.equals("") && !municipio.equals("")
//                    && !telefono.equals("") && !sector.equals("") && !email.equals("")) {
//
//                Clientes nuevo = new Clientes(id, nombreCliente, identificacion, tipoCliente, direccion, municipio, telefono, sector, email);
//                nuevo.ActualizarCliente(usuario);
//
//            } else {
//                JOptionPane.showMessageDialog(null, "Complete todos los campos");
//            }
//        }
//
//    }

    public boolean VerificarNIT(String NIT, String RazonSocial) {

        boolean verificacion = false;

        ArrayList<String> listaClientes = new ArrayList<>();
        ArrayList<String> listaRazonSocial = new ArrayList<>();

        try {
            String consulta = "select identificacion, nombreCliente from clientes";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String NITcliente = rs.getString("identificacion");
                String RazonesSociales = rs.getString("nombreCliente");
                listaClientes.add(NITcliente);
                listaRazonSocial.add(RazonesSociales);
            }

            for (int i = 0; i < listaClientes.size(); i++) {
                if (listaClientes.get(i).equals(NIT) || listaRazonSocial.get(i).equals(RazonSocial)) {
                    verificacion = true;
                    break;
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en leer los NIT de clientes. RegistroClientes VerificarNIT()");
        }

        return verificacion;
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
        jTable1_clientes = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTextField_id = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextField1_nombreCliente = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField3_identificacion = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jComboBox_tipocliente = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jTextField3_direccion = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jComboBox_municipio = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jTextField4_telefono = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField_email = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBox_sector = new javax.swing.JComboBox<>();
        jButton_crear = new javax.swing.JButton();
        jButton_actualizar = new javax.swing.JButton();
        jButton_limpiarCampos = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1_clientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Cliente", "Cedula / NIT", "Direccion", "Municipio", "Telefono", "Email", "Sector", "Tipo de cliente", "Registrado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1_clientes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1_clientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1_clientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1_clientes);
        if (jTable1_clientes.getColumnModel().getColumnCount() > 0) {
            jTable1_clientes.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTable1_clientes.getColumnModel().getColumn(1).setPreferredWidth(180);
            jTable1_clientes.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable1_clientes.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTable1_clientes.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTable1_clientes.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTable1_clientes.getColumnModel().getColumn(6).setPreferredWidth(150);
            jTable1_clientes.getColumnModel().getColumn(7).setPreferredWidth(70);
            jTable1_clientes.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTable1_clientes.getColumnModel().getColumn(9).setPreferredWidth(100);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel7.setText("Id");

        jLabel1.setText("Nombre Cliente: ");

        jTextField1_nombreCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1_nombreClienteKeyTyped(evt);
            }
        });

        jLabel2.setText("Cedula / NIT:");

        jTextField3_identificacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField3_identificacionKeyTyped(evt);
            }
        });

        jLabel8.setText("Tipo de cliente:");

        jComboBox_tipocliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Persona", "Empresa" }));

        jLabel3.setText("Direccion:");

        jTextField3_direccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField3_direccionKeyTyped(evt);
            }
        });

        jLabel4.setText("Municipio:");

        jComboBox_municipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_municipioActionPerformed(evt);
            }
        });

        jLabel5.setText("Telefono:");

        jTextField4_telefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField4_telefonoKeyTyped(evt);
            }
        });

        jLabel9.setText("Email");

        jTextField_email.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_emailKeyTyped(evt);
            }
        });

        jLabel6.setText("Sector:");

        jComboBox_sector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Privado", "Publico" }));

        jButton_crear.setText("Crear");
        jButton_crear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_crearActionPerformed(evt);
            }
        });

        jButton_actualizar.setText("Actualizar");
        jButton_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_actualizarActionPerformed(evt);
            }
        });

        jButton_limpiarCampos.setText("Limpiar campos");
        jButton_limpiarCampos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_limpiarCamposActionPerformed(evt);
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
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField1_nombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField3_identificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox_tipocliente, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField3_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox_municipio, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField4_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_email, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox_sector, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56)
                        .addComponent(jButton_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_limpiarCampos, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1_nombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField3_identificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jComboBox_tipocliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField4_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jTextField3_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(jComboBox_municipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox_sector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jButton_crear)
                    .addComponent(jButton_actualizar)
                    .addComponent(jButton_limpiarCampos))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 934, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox_municipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_municipioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_municipioActionPerformed

    private void jButton_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_actualizarActionPerformed

        String id = jTextField_id.getText().trim();

        //Verificamos que se haya seleccionado el cliente           
        if (!id.equals("")) {
            
            String nombreCliente = jTextField1_nombreCliente.getText().trim().toUpperCase();
            String identificacion = jTextField3_identificacion.getText().trim();
            String tipoCliente = jComboBox_tipocliente.getSelectedItem().toString().trim();
            String direccion = MetodosGenerales.ConvertirPrimerasLetrasMayus(jTextField3_direccion.getText().trim());
            String municipio = jComboBox_municipio.getSelectedItem().toString();
            String telefono = jTextField4_telefono.getText().trim();
            String sector = jComboBox_sector.getSelectedItem().toString().trim();
            String email = jTextField_email.getText().trim();
            //Verificamos que se hayan completado el resto de datos
            if (!nombreCliente.equals("") && !direccion.equals("") && !municipio.equals("")
                    && !telefono.equals("") && !sector.equals("") && !email.equals("")) {

                Clientes nuevo = new Clientes(Integer.parseInt(id), nombreCliente, identificacion, tipoCliente, direccion, municipio, telefono, sector, email);
                nuevo.ActualizarCliente(this.usuario);
                LimpiarFormulario();
                limpiarTabla(modelo);
                llenarTabla();

            } else {
                JOptionPane.showMessageDialog(null, "Complete todos los campos");
            }
            
            

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para actualizar sus datos");
        }

    }//GEN-LAST:event_jButton_actualizarActionPerformed

    private void jButton_crearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_crearActionPerformed

        //Verificamos que todos los datos esten ingresados
        jTextField_id.setText("");
        String nombreCliente = jTextField1_nombreCliente.getText().trim().toUpperCase();
        String identificacion = jTextField3_identificacion.getText().trim();
        String tipoCliente = jComboBox_tipocliente.getSelectedItem().toString().trim();
        String direccion = MetodosGenerales.ConvertirPrimerasLetrasMayus(jTextField3_direccion.getText().trim());
        String municipio = jComboBox_municipio.getSelectedItem().toString();
        String telefono = jTextField4_telefono.getText().trim();
        String sector = jComboBox_sector.getSelectedItem().toString().trim();
        String email = jTextField_email.getText().trim();

        if (!nombreCliente.equals("") && !identificacion.equals("") && !direccion.equals("") && !municipio.equals("")
                && !telefono.equals("") && !sector.equals("") && !email.equals("")) {

            //Verificamos que se el cliente no exista ya en la base de datos
            boolean verificacion = VerificarNIT(identificacion, nombreCliente);
            if (!verificacion) {
                Clientes nuevo = new Clientes(nombreCliente, identificacion, tipoCliente, direccion, municipio, telefono, sector, email);
                nuevo.CrearCliente(this.usuario);
                limpiarTabla(modelo);
                llenarTabla();
            } else {
                JOptionPane.showMessageDialog(null, "El NIT/Cedula o el nombre del cliente que intenta registrar \nya existe en la base de datos");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
        }

    }//GEN-LAST:event_jButton_crearActionPerformed

    private void jTable1_clientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1_clientesMouseClicked
        CapturarDatosTabla();

    }//GEN-LAST:event_jTable1_clientesMouseClicked

    private void jButton_limpiarCamposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_limpiarCamposActionPerformed

        LimpiarFormulario();
    }//GEN-LAST:event_jButton_limpiarCamposActionPerformed

    private void jTextField1_nombreClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1_nombreClienteKeyTyped
        if (jTextField1_nombreCliente.getText().trim().length()==100) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField1_nombreClienteKeyTyped

    private void jTextField3_identificacionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3_identificacionKeyTyped
        if (jTextField3_identificacion.getText().trim().length()==50) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField3_identificacionKeyTyped

    private void jTextField3_direccionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3_direccionKeyTyped
        if (jTextField3_direccion.getText().trim().length()==100) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField3_direccionKeyTyped

    private void jTextField4_telefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4_telefonoKeyTyped
        if (jTextField4_telefono.getText().trim().length()==150) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField4_telefonoKeyTyped

    private void jTextField_emailKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_emailKeyTyped
        if (jTextField_email.getText().trim().length()==250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_emailKeyTyped

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
            java.util.logging.Logger.getLogger(RegistroClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistroClientes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_actualizar;
    private javax.swing.JButton jButton_crear;
    private javax.swing.JButton jButton_limpiarCampos;
    private javax.swing.JComboBox<String> jComboBox_municipio;
    private javax.swing.JComboBox<String> jComboBox_sector;
    private javax.swing.JComboBox<String> jComboBox_tipocliente;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1_clientes;
    private javax.swing.JTextField jTextField1_nombreCliente;
    private javax.swing.JTextField jTextField3_direccion;
    private javax.swing.JTextField jTextField3_identificacion;
    private javax.swing.JTextField jTextField4_telefono;
    private javax.swing.JTextField jTextField_email;
    private javax.swing.JTextField jTextField_id;
    // End of variables declaration//GEN-END:variables
}
