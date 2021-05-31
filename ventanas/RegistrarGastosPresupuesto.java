/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author erwin
 */
public class RegistrarGastosPresupuesto extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso, idPresupuesto, presupuesto;

    /**
     * Creates new form RegistrarGastosPresupuesto
     */
    public RegistrarGastosPresupuesto() {
        initComponents();
        SettearDatos();
        InhabilitarCampos();

 
    }

    public RegistrarGastosPresupuesto(String usuario, String permiso, String idPresupuesto, String presupuesto) {
        this.usuario = usuario;
        this.permiso = permiso;
        this.idPresupuesto = idPresupuesto;
        this.presupuesto = presupuesto;
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();        
        
        if (permiso.equals("Asistente")) {
            jTextField_comentarioAutoriza.setEnabled(false);
            jButton_autorizar.setEnabled(false);
        }
        


    }

    public void SettearDatos() {
        jTextField_idPresup.setText(this.idPresupuesto);
        jTextField_descripcionPresup.setText(this.presupuesto);

    }
    
    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Registrar gastos a presupuesto *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }
    
    public void IniciarCaracteristicasGenerales() {
        llenarTabla(idPresupuesto);
        SettearDatos();
        InhabilitarCampos();
        llenarComboBox(idPresupuesto);
    }
    
    public void limpiarCampos(){
        jComboBox_conceptos.setSelectedIndex(0);
        jTextField_valor.setText("");
        jTextField_descripcionGasto.setText("");
        jTextField_idConcepto.setText("");
        jTextField_concepto.setText("");
        jTextField_comentarioAutoriza.setText("");
        jLabel3_fila.setText("");
        
    }

    public void llenarComboBox(String idpresupuesto) {
        String consulta = "select maestrogastos.descripcion from maestrogastos "
                + "inner join itemspresupuesto on maestrogastos.id=itemspresupuesto.idGasto where "
                + "itemspresupuesto.idPresupuesto=?";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idpresupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                jComboBox_conceptos.addItem(rs.getString("maestrogastos.descripcion"));
            }

            cn.close();
        } catch (Exception e) {
        }
    }

//    public void llenarTablaCopia(String idPresupuesto) {
//
//        modelo = (DefaultTableModel) jTable1.getModel();
//
//        String consulta = "select itemspresupuesto.idGasto, maestrogastos.descripcion, itemspresupuesto.valorPresupuestado, "
//                + "IF(sum(gastospresupuestos.valor) is null,0, sum(gastospresupuestos.valor)) as gastos from itemspresupuesto "
//                + "left join gastospresupuestos on itemspresupuesto.idGasto=gastospresupuestos.idConcepto inner join "
//                + "maestrogastos on maestrogastos.id=itemspresupuesto.idGasto where itemspresupuesto.idPresupuesto=? "
//                + "GROUP by itemspresupuesto.idGasto";
//
//        try {
//            Connection cn = Conexion.Conectar();
//            PreparedStatement pst = cn.prepareStatement(consulta);
//            pst.setString(1, idPresupuesto);
//
//            ResultSet rs = pst.executeQuery();
//
//            while (rs.next()) {
//                Object[] nuevo = new Object[4];
//                nuevo[0] = rs.getString("itemspresupuesto.idGasto");
//                nuevo[1] = rs.getString("maestrogastos.descripcion");
//                nuevo[2] = rs.getString("itemspresupuesto.valorPresupuestado");
//                nuevo[3] = rs.getString("gastos");
//
//                modelo.addRow(nuevo);
//            }
//
//            jTable1.setModel(modelo);
//            cn.close();
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error en leer los conceptos del presupuesto y los gastos relacionados RegistrarGastosPresupuestos llenarTabla()");
//        }
//    }

    public void llenarTabla(String idPresupuesto) {

        modelo = (DefaultTableModel) jTable1.getModel();

        String consulta = "select gastospresupuestos.idConcepto, gastospresupuestos.fechaGasto, maestrogastos.descripcion, "
                + "gastospresupuestos.observaciones, gastospresupuestos.factura, gastospresupuestos.valor, gastospresupuestos.estado, "
                + "gastospresupuestos.registradoPor, gastospresupuestos.observAutoriza from gastospresupuestos "
                + "inner join maestrogastos on gastospresupuestos.idConcepto=maestrogastos.id where "
                + "gastospresupuestos.idPrespuesto=?";
       
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[9];
                nuevo[0] = rs.getString("gastospresupuestos.idConcepto");
                nuevo[1]=rs.getString("gastospresupuestos.fechaGasto");
                nuevo[2] = rs.getString("maestrogastos.descripcion");
                nuevo[3] = rs.getString("gastospresupuestos.observaciones");
                nuevo[4]=rs.getString("gastospresupuestos.factura");
                nuevo[5] = rs.getString("gastospresupuestos.valor");
                nuevo[5]= ConvertirIntAMoneda(Double.parseDouble(nuevo[5].toString()));
                nuevo[6] = rs.getString("gastospresupuestos.estado");
                nuevo[7] = rs.getString("gastospresupuestos.registradoPor");
                nuevo[8] = rs.getString("gastospresupuestos.observAutoriza");

                modelo.addRow(nuevo);
            }

            jTable1.setModel(modelo);
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer los gastos del presupuesto. RegistrarGastosPresupuestos llenarTabla()");
        }
    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            this.modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void InhabilitarCampos() {
        jTextField_idPresup.setEnabled(false);
        jTextField_descripcionPresup.setEnabled(false);
        jTextField_idConcepto.setEnabled(false);
        jTextField_concepto.setEnabled(false);
        jLabel3_fila.setVisible(false);
    }

    public void RegistrarGasto(String idPresupuesto, String fecha, int idconcepto, String factura, int ValorAIngresar, String observaciones, String estado, String registradoPor) {

        String consulta = "insert into gastospresupuestos (idPrespuesto, fechaGasto, idConcepto, factura, valor, observaciones, estado, registradoPor) values "
                + "(?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            pst.setString(2, fecha);
            pst.setInt(3, idconcepto);
            pst.setString(4, factura);
            pst.setInt(5, ValorAIngresar);
            pst.setString(6, observaciones);
            pst.setString(7, estado);
            pst.setString(8, registradoPor);
            
            
            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "Gasto registrado");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en insertar el gasto. RegistrarGastoPresupuesto RegistrarGasto()");
            e.printStackTrace();
        }
    }

    public void AutorizarGasto(String idpresupuesto, String idConcepto, String observaciones) {
        String consulta = "update gastospresupuestos set estado=?, observAutoriza=? where idPrespuesto=? and idConcepto=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, "Registrado");
            pst.setString(2, observaciones);
            pst.setString(3, idpresupuesto);
            pst.setString(4, idConcepto);

            pst.executeUpdate();
            cn.close();

            JOptionPane.showMessageDialog(this, "Gasto autorizado");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en autorizar el gasto. RegistrarGastosPresupuestos AutorizarGasto()");
            e.printStackTrace();
        }
    }

    public int ConsultarIdConcepto(String concepto) {
        int idConcepto = -1;
        String consulta = "Select id from maestrogastos where descripcion=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, concepto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                idConcepto = rs.getInt("id");
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar el Id del concepto. RegistrarGastosPresupuesto ConsultarIdConcepto");

        }

        return idConcepto;
    }

    public int ConsultarPresupuestado(String idpresupuesto, int idConcepto) {

        int valorPresupuestado = -1;
        String consulta = "select valorPresupuestado from itemspresupuesto where idPresupuesto=? and idGasto=?";

        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idpresupuesto);
            pst.setInt(2, idConcepto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                valorPresupuestado = rs.getInt("valorPresupuestado");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar el valor presupuestado. RegistrarGastosPresupuesto ConsultarPresupuestado()");
            e.printStackTrace();
        }

        return valorPresupuestado;
    }

    public int ConsultarSumaYaGastada(String idpresupuesto, int idConcepto) {
        int yagastado = 0;
        String consulta = "select if(sum(gastospresupuestos.valor) is null, 0, sum(gastospresupuestos.valor)) "
                + "as suma from gastospresupuestos where gastospresupuestos.idPrespuesto=? and "
                + "gastospresupuestos.idConcepto=?";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idpresupuesto);
            pst.setInt(2, idConcepto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                yagastado = rs.getInt("suma");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer la suma ya gastada. RegistrarGastosPresupuesto ConsultarSumaYaGastada()");
        }

        return yagastado;
    }
    
    public static String ConvertirIntAMoneda(double dato) {
        String result = "";
        DecimalFormat objDF = new DecimalFormat("$ ###, ###");
        result = objDF.format(dato);

        return result;
    }
    
    public static String ConvertirMonedaAInt(String numero){
        String MonedaParseada="";
        
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
        jTable1 = new javax.swing.JTable();
        jLabel3_fila = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_idPresup = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_descripcionPresup = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jComboBox_conceptos = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jTextField_valor = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField_descripcionGasto = new javax.swing.JTextField();
        jButton_agregar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jTextField_factura = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField_idConcepto = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_concepto = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField_comentarioAutoriza = new javax.swing.JTextField();
        jButton_autorizar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Fecha", "Concepto", "Descripcion gasto", "Factura", "Pagado", "Estado", "Registrado Por", "Comentarios Autorizacion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(150);
        }

        jLabel3_fila.setText("jLabel3");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Presupuesto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Id Presup.");

        jLabel2.setText("Descripcion");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jTextField_idPresup, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_descripcionPresup, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_idPresup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_descripcionPresup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Agregar gasto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jComboBox_conceptos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        jLabel5.setText("Valor");

        jTextField_valor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_valorKeyTyped(evt);
            }
        });

        jLabel6.setText("Descripcion gasto");

        jTextField_descripcionGasto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_descripcionGastoKeyTyped(evt);
            }
        });

        jButton_agregar.setText("Agregar");
        jButton_agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregarActionPerformed(evt);
            }
        });

        jLabel8.setText("Fact/Recibo No.");

        jTextField_factura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_facturaKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox_conceptos, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(24, 24, 24)
                        .addComponent(jTextField_factura, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField_valor, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jButton_agregar))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_descripcionGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_conceptos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField_descripcionGasto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jTextField_valor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_agregar)
                        .addComponent(jTextField_factura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel8))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Autorizar gastos que superan el valor presupuestado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel3.setText("Id");

        jLabel4.setText("Descripcion");

        jLabel7.setText("Comentarios");

        jButton_autorizar.setText("Autorizar gasto");
        jButton_autorizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_autorizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_idConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_concepto, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_comentarioAutoriza, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_autorizar)
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_idConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_concepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField_comentarioAutoriza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_autorizar))
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 921, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(19, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3_fila)
                        .addGap(179, 179, 179))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel3_fila))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregarActionPerformed
        
        
        //Validamos que se haya seleccionado un concepto de la lista
        String concepto = jComboBox_conceptos.getSelectedItem().toString().trim();
        if (!concepto.equals("")) {
            //Verificamos que se haya completado el valor y la descripcion del gasto
            String descripcionGasto = jTextField_descripcionGasto.getText().trim();
            String valor = jTextField_valor.getText().trim();
            String factura=jTextField_factura.getText().trim();
            int valorInt = Integer.parseInt(valor);
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            
            if (!descripcionGasto.equals("") && !valor.equals("") && !factura.equals("")) {
                int idConcepto = ConsultarIdConcepto(concepto);
                int valorPresupuestado = ConsultarPresupuestado(this.idPresupuesto, idConcepto);
                int SumaYaGastado = ConsultarSumaYaGastada(this.idPresupuesto, idConcepto);
                //Verificamos que la suma a ingresar mas a suma ya gastada en ese concepto, no sea superior al 
                //valor presupuestado

                if (valorInt + SumaYaGastado <= valorPresupuestado) {
                    //Si el valor a ingresar mas lo ya gastado no supera el presupuesto, se  solicita confirmacion y 
                    //registra el gasto
                    int confirmacion = JOptionPane.showConfirmDialog(this, "¿Desea registrar un gasto por $" + valorInt
                            + " bajo la descripcion: " + descripcionGasto + " al concepto " + concepto + "?");

                    if (confirmacion == 0) {
                        String estado = "Registrado";
                        RegistrarGasto(this.idPresupuesto, fecha, idConcepto, factura, valorInt, descripcionGasto, estado, this.usuario);
                        limpiarTabla(modelo);
                        llenarTabla(idPresupuesto);
                        limpiarCampos();
                    
                    }

                } else {
                    //Sino Le preguntamos al usuario si quiere pedir autorizacion de la gerencia para registrar un gasto
                    //que en suma supera el valor de los presupuestado
                    int eleccion = JOptionPane.showConfirmDialog(this, "La suma de los gastos registrados por el concepto "
                            + concepto + " supera el valor presupuestado($" + valorPresupuestado + "). ¿Desea pedir autorizacion para cargar el gasto de $"
                            + valorInt + " a dicho concepto?");

                    if (eleccion == 0) {

                        String estado = "Por Autorizar";
                        RegistrarGasto(this.idPresupuesto, fecha, idConcepto, factura, valorInt, descripcionGasto, estado, this.usuario);
                        limpiarTabla(modelo);
                        llenarTabla(idPresupuesto);
                        limpiarCampos();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Complete los campos descripcion y valor");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un concepto de la lista");
        }

    }//GEN-LAST:event_jButton_agregarActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int fila = jTable1.getSelectedRow();
        if (fila != -1) {
            jTextField_idConcepto.setText(jTable1.getValueAt(fila, 0).toString());
            jTextField_concepto.setText(jTable1.getValueAt(fila, 3).toString());
            jLabel3_fila.setText(String.valueOf(fila));
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un concepto");
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton_autorizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_autorizarActionPerformed
        //Verificamos que se haya seleccionado una fila
        String idConcepto = jTextField_idConcepto.getText().trim();

        if (!idConcepto.equals("")) {
            String fila = jLabel3_fila.getText().trim();
            String comentarios = jTextField_comentarioAutoriza.getText().trim();
            //Verificamos que haya completado los comentarios en la autorizacion
            if (!comentarios.equals("")) {
                //Verificamos que el estado de la fila seleccionada corresponda a pendiente de autorizacion
                String estado = jTable1.getValueAt(Integer.parseInt(fila), 6).toString();
              
                if (estado.equals("Por Autorizar")) {
                                       
                    AutorizarGasto(this.idPresupuesto, idConcepto, comentarios);
                    limpiarCampos();
                    limpiarTabla(modelo);
                    llenarTabla(idPresupuesto);
                } else {
                    JOptionPane.showMessageDialog(this, "No es posible autorizar gastos ya registrados");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Indique un comentario sobre la autorizacion");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un gasto para autorizar");
        }


    }//GEN-LAST:event_jButton_autorizarActionPerformed

    private void jTextField_valorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_valorKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
        if (c=='0' && jTextField_valor.getText().length()==0) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_valorKeyTyped

    private void jTextField_descripcionGastoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_descripcionGastoKeyTyped
        if (jTextField_descripcionGasto.getText().trim().length()==250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_descripcionGastoKeyTyped

    private void jTextField_facturaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_facturaKeyTyped
        if (jTextField_factura.getText().trim().length()==150) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_facturaKeyTyped

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
            java.util.logging.Logger.getLogger(RegistrarGastosPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistrarGastosPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistrarGastosPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistrarGastosPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistrarGastosPresupuesto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_agregar;
    private javax.swing.JButton jButton_autorizar;
    private javax.swing.JComboBox<String> jComboBox_conceptos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel3_fila;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField_comentarioAutoriza;
    private javax.swing.JTextField jTextField_concepto;
    private javax.swing.JTextField jTextField_descripcionGasto;
    private javax.swing.JTextField jTextField_descripcionPresup;
    private javax.swing.JTextField jTextField_factura;
    private javax.swing.JTextField jTextField_idConcepto;
    private javax.swing.JTextField jTextField_idPresup;
    private javax.swing.JTextField jTextField_valor;
    // End of variables declaration//GEN-END:variables
}
