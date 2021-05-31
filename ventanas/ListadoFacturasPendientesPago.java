/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
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
public class ListadoFacturasPendientesPago extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso;

    /**
     * Creates new form ListadoFacturas
     */
    public ListadoFacturasPendientesPago() {
        initComponents();
        
        IniciarCaracteristicasGenerales();        
        ConfiguracionGralJFrame();
    }

    public ListadoFacturasPendientesPago(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
    }
    
    public void IniciarCaracteristicasGenerales(){
        jTextField_cliente.setEnabled(false);
        jTextField_factura.setEnabled(false);
        jTextField_idCliente.setEnabled(false);
        jLabel_fila.setVisible(false);
        SettearModelo();
        llenarTabla();
               
    }
    
    public void ConfiguracionGralJFrame(){
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado de facturas pendientes de pago *** "+"Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
    }

    public void SettearModelo() {
        modelo = (DefaultTableModel) jTable_listafacturas.getModel();
    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_listafacturas.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void llenarTabla() {
        try {

            String consulta = "select facturas.fechaFactura, facturas.idFactura, facturas.idCliente, clientes.nombreCliente,"
                    + " facturas.valorFacturado, facturas.valorFacturado - IF(SUM(abonosfacturas.valorAbono) "
                    + "is null, 0, SUM(abonosfacturas.valorAbono)) as saldo, facturas.condiciondePago from facturas "
                    + "left join clientes on facturas.idCliente=clientes.idCliente left join abonosfacturas on "
                    + "facturas.idFactura=abonosfacturas.idFactura GROUP by idFactura having saldo>0";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable_listafacturas.getModel();

            while (rs.next()) {
                Object[] facturas = new Object[8];

                facturas[0] = rs.getString("facturas.fechaFactura");
                facturas[1] = rs.getString("facturas.idCliente");
                facturas[2] = rs.getString("clientes.nombreCliente");
                facturas[3] = rs.getString("facturas.idFactura");
//                facturas[4] = rs.getString("facturas.valorFacturado");
                facturas[4] = MetodosGenerales.ConvertirIntAMoneda(rs.getInt("facturas.valorFacturado"));
//                facturas[5] = rs.getString("saldo");
                facturas[5] = MetodosGenerales.ConvertirIntAMoneda(rs.getInt("saldo"));
                facturas[6] = "Pendiente";
                facturas[7] = rs.getString("facturas.condiciondePago");

                modelo.addRow(facturas);
            }
            jTable_listafacturas.setModel(modelo);

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos en la tabla facturas desde la base de datos ListadoFacturas llenarTabla()");
            e.printStackTrace();
        }
    }

    public String[] consultarDatosCliente(String idCliente) {

        String[] datosCliente = new String[5];

        String consulta = "select nombreCliente, identificacion, direccion, municipio, telefono, email from "
                + "clientes where idCliente=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idCliente);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                datosCliente[0] = rs.getString("nombreCliente");
                datosCliente[1] = rs.getString("identificacion");
                datosCliente[2] = rs.getString("direccion") + " - " + rs.getString("municipio");
                datosCliente[3] = rs.getString("telefono");
                datosCliente[4] = rs.getString("email");

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en leer los datos del cliente ListadoFacturas ConsultarDatosCliente()");
        }

        return datosCliente;
    }

    public ArrayList<String[]> ConsultarElementosFactura(String factura) {
        //Declaramos un ArraList para agregar las cadenas de String con la informacion de los elementos de la factura
        ArrayList<String[]> informacion = new ArrayList<>();

        String consulta = "select elementosfactura.idVenta, elementosfactura.cantidadFacturada, ventas.descripcionTrabajo, "
                + "elementosfactura.precioUnitario, elementosfactura.subtotal, elementosfactura.factura from "
                + "elementosfactura inner join ventas on elementosfactura.idVenta=ventas.Idventa inner join facturas "
                + "on elementosfactura.factura=facturas.idFactura where facturas.idFactura=?";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, factura);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                String[] detalle = new String[6];
                detalle[0] = rs.getString("elementosfactura.idVenta");
                detalle[1] = rs.getString("elementosfactura.cantidadFacturada");
                detalle[2] = rs.getString("ventas.descripcionTrabajo");
                detalle[3] = rs.getString("elementosfactura.precioUnitario");
                detalle[4] = rs.getString("elementosfactura.subtotal");

                informacion.add(detalle);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en leer los elementos de la factura. Contacte al administrador "
                    + "ListadoFacturas ConsultarElementosFactura()");
            e.printStackTrace();

        }

        return informacion;
    }

    public void imprimirFactura(String numeroFactura, String fecha, String idCliente) {
        //Definimos las ruta de donde se tomara la plantilla de la factra
        //String rutaArchivoACopiar = "D:" + File.separator + "Erwin" + File.separator + "ApacheNetbeans" + File.separator + "GraficasJireh_1" + File.separator + "Factura.xlsx";

        String rutaArchivoACopiar ="Docs" + File.separator + "Factura.xlsx";
        try {
            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);

            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();
            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            String[] datos = consultarDatosCliente(idCliente);
            //Definimos la ruta donde se guardara la factura            
            String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Factura "
                    + "" + numeroFactura + " - " + datos[0] + ".xlsx";

            //Datos de la cuarta fila
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda42 = fila4.getCell(2);
            celda42.setCellValue(datos[0]);
            XSSFCell celda44 = fila4.getCell(4);
            celda44.setCellValue(Integer.parseInt(numeroFactura));

            //Datos de la quinta fila
            XSSFRow fila5 = hoja.getRow(5);
            XSSFCell celda52 = fila5.getCell(2);
            celda52.setCellValue(datos[1]);
            XSSFCell celda54 = fila5.getCell(4);
            try {
                celda54.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(fecha));
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(null, "Error en la lectura de la fecha ListadoFacturas imprimirFactura()");
            }

            //Datos de la sexta fila
            XSSFRow fila6 = hoja.getRow(6);
            XSSFCell celda62 = fila6.getCell(2);
            celda62.setCellValue(datos[2]);
            XSSFCell celda64 = fila6.getCell(4);
            celda64.setCellValue(jTable_listafacturas.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 7).toString());

            //Datos de la septima fila
            XSSFRow fila7 = hoja.getRow(7);
            XSSFCell celda72 = fila7.getCell(2);
            celda72.setCellValue(datos[3]);

            //Datos de la octava fila
            XSSFRow fila8 = hoja.getRow(8);
            XSSFCell celda82 = fila8.getCell(2);
            celda82.setCellValue(datos[4]);

            //Traemos el detalle de los items facturados
            ArrayList<String[]> listado = ConsultarElementosFactura(numeroFactura);

            //Declaramos una variable para capturar el total de la factura
            int total = 0;
            //Declaramos la variable con la fila donde se inicia a agregar la informacion
            int filaInicio = 11;

            //Recorremos la informacion de cada item y lo agregamos a las celdas correspondientes
            for (String[] elemento : listado) {
                XSSFRow fila = hoja.getRow(filaInicio);
                for (int i = 0; i < elemento.length; i++) {

                    if (i == 4) {
                        total += Double.parseDouble(elemento[i]);
                    }

                    XSSFCell celda = fila.getCell(i);
                    if (i == 0 || i == 1 || i == 3 || i == 4) {
                        celda.setCellValue(Double.parseDouble(elemento[i]));
                    } else if (i == 2) {
                        celda.setCellValue(elemento[i]);
                    }
                }
                filaInicio++;
            }

            XSSFRow fila40 = hoja.getRow(40);
            XSSFCell celda404 = fila40.getCell(4);
            celda404.setCellValue(total);

            FileOutputStream ultimo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(ultimo);
            ultimo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error en generar la factura. Asegurse se no tener una factura abierta y vuelta a intentarlo. ImprimirRecibo GenerarRecibo()");
            e.printStackTrace();
        }

    }

    public void RegistrarIngreso(String numeroFactura, int ingreso, String fecha, String observaciones, String usuario) {

        String consulta = "insert into abonosfacturas (idFactura, valorAbono, fechaAbonoSistema, observaciones, registradoPor) "
                + "values ('" + numeroFactura + "', '" + ingreso + "', '" + fecha + "', '" + observaciones + "', '" + usuario + "')";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(null, "Abono registrado");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en registrar el abono de la factura. ListadoFacturas RegistrarIngreso()");
            e.printStackTrace();
        }
    }

    public void CambiarEstadoFactura(String idFactura) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String consulta = "update facturas set estadoPago='Saldado', fechaSaldado=? where idFactura=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, fecha);
            pst.setString(2, idFactura);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "Estado de la factura actualizado a saldado");

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el estado de la factura ListadoFacturas CambiarEstadoFactura()");
        }

    }

    public void ImprimirInformeDeudaFacturas(JTable tabla) {

        //String rutaArchivoACopiar = "D:" + File.separator + "Erwin" + File.separator + "ApacheNetbeans" + File.separator + "GraficasJireh_1" + File.separator + "Informe deuda Empresas.xlsx";
        String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Informe de Deudas Empresas.xlsx";
        String rutaArchivoACopiar = "Docs" + File.separator + "Informe deuda Empresas.xlsx";
        
        try {
            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();

            XSSFSheet hoja = nuevoLibro.getSheetAt(0);
            //Completamos los datos del usuario y fecha            
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda11=fila1.getCell(1);
            celda11.setCellValue(new Date());
            
            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda21 = fila2.getCell(1);
            celda21.setCellValue(this.usuario);

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
                        case 0 -> celda.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(tabla.getValueAt(i, j).toString()));                       
                        case 1,3 -> celda.setCellValue(Double.parseDouble(tabla.getValueAt(i, j).toString()));                            
                        case 4,5 -> celda.setCellValue(Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(tabla.getValueAt(i, j).toString())));                           
                        default -> celda.setCellValue(tabla.getValueAt(i, j).toString());
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_listafacturas = new javax.swing.JTable();
        jButton_imprimir = new javax.swing.JButton();
        jLabel_fila = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_factura = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_idCliente = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_ingreso = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField_observaciones = new javax.swing.JTextField();
        jButton_registrarIngreso = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_listafacturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha", "Id Cliente", "Cliente", "No. Factura", "Valor facturado", "Saldo pendiente", "Estado", "Condicion de pago"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_listafacturas.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_listafacturas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_listafacturasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_listafacturas);
        if (jTable_listafacturas.getColumnModel().getColumnCount() > 0) {
            jTable_listafacturas.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable_listafacturas.getColumnModel().getColumn(1).setPreferredWidth(70);
            jTable_listafacturas.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTable_listafacturas.getColumnModel().getColumn(3).setPreferredWidth(70);
            jTable_listafacturas.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTable_listafacturas.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTable_listafacturas.getColumnModel().getColumn(6).setPreferredWidth(120);
            jTable_listafacturas.getColumnModel().getColumn(7).setPreferredWidth(180);
        }

        jButton_imprimir.setText("Imprimir factura");
        jButton_imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_imprimirActionPerformed(evt);
            }
        });

        jLabel_fila.setText("jLabel4");

        jButton1.setText("Imprimir informe");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Factura a abonar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("No. Factura");

        jTextField_factura.setEditable(false);

        jLabel3.setText("Id Cliente");

        jLabel2.setText("Cliente");

        jTextField_cliente.setEditable(false);

        jLabel4.setText("Valor abono");

        jTextField_ingreso.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_ingresoKeyTyped(evt);
            }
        });

        jLabel5.setText("Observaciones");

        jTextField_observaciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_observacionesKeyTyped(evt);
            }
        });

        jButton_registrarIngreso.setText("Registrar ingreso");
        jButton_registrarIngreso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_registrarIngresoActionPerformed(evt);
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
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_factura, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_idCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_ingreso, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addComponent(jButton_registrarIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_factura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_idCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_ingreso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_registrarIngreso))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel_fila))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 845, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(322, 322, 322))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(118, 118, 118)
                        .addComponent(jLabel_fila)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_imprimir)
                    .addComponent(jButton1))
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_imprimirActionPerformed

        String numeroFactura = jTextField_factura.getText().trim();
        //Verificamos que se haya seleccionado una factura
        if (numeroFactura.equals("")) {
            JOptionPane.showMessageDialog(null, "Seleccione la factura a imprimir");
        } else {
            //Capturamos el resto de datos
            String fecha = jTable_listafacturas.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 0).toString();
            String idCliente = jTextField_idCliente.getText().trim();
            imprimirFactura(numeroFactura, fecha, idCliente);

        }


    }//GEN-LAST:event_jButton_imprimirActionPerformed

    private void jTable_listafacturasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_listafacturasMouseClicked

        int fila = jTable_listafacturas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona una fila valida");
        } else {
            jTextField_cliente.setText(jTable_listafacturas.getValueAt(fila, 2).toString());
            jTextField_factura.setText(jTable_listafacturas.getValueAt(fila, 3).toString());
            jTextField_idCliente.setText(jTable_listafacturas.getValueAt(fila, 1).toString());
            jLabel_fila.setText(String.valueOf(fila));
        }

    }//GEN-LAST:event_jTable_listafacturasMouseClicked

    private void jButton_registrarIngresoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_registrarIngresoActionPerformed

        String numeroFactura = jTextField_factura.getText().trim();

        //Verificamos que se haya seleccionado una factura del listado
        if (!numeroFactura.equals("")) {
            //Capturamos el valor de la fila
            int fila = Integer.parseInt(jLabel_fila.getText().trim());
            //Verificamos que la factura tenga saldo pendiente
            if (!jTable_listafacturas.getValueAt(fila, 6).toString().equals("Saldado")) {
                //Verificamos que la casilla valor ingreso y observaciones esten completas y que esten correctamente ingresados
                try {
                    if (!jTextField_ingreso.getText().trim().equals("") && !jTextField_observaciones.getText().trim().equals("")) {
                        //Capturamos el valor de ingreso, el saldo adeudado y las observaciones
                        int ingreso = Integer.parseInt(jTextField_ingreso.getText().trim());

                        int saldo = Integer.parseInt(MetodosGenerales.ConvertirMonedaAInt(jTable_listafacturas.getValueAt(fila, 5).toString()));

                        //Verificamos que el saldo ingresado no sea superior al saldo adeudado
                        if (ingreso <= saldo) {
                            //Capturamos el resto de datos que se necesitan para registrar el ingreso                            
                            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                            String observaciones = jTextField_observaciones.getText().trim();
                            RegistrarIngreso(numeroFactura, ingreso, fecha, observaciones, this.usuario);
                            //Si el valor ingresado es igual al saldo, cambiamos el estado de la factura a saldado en la base de datos
                            //Esto para el calculo de indicadores
                            if (ingreso == saldo) {
                                CambiarEstadoFactura(numeroFactura);
                            }
                            dispose();
                            new ListadoFacturasPendientesPago(this.usuario, this.permiso).setVisible(true);

                        } else {
                            JOptionPane.showMessageDialog(this, "No es posible registrar un ingreso mayor al saldo adeudado");
                        }

                    } else {
                        JOptionPane.showMessageDialog(this, "Debe completar los campos ingreso y observaciones");
                    }

                } catch (HeadlessException | NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Asegurese de haber ingresado correctamente la informacion. El valor de ingres "
                            + "debe ser unicamente numerico, sin puntos ni comas");
                }

            } else {
                JOptionPane.showMessageDialog(this, "No es posible registrar ingresos a facturas ya saldadas");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una factura");
        }

    }//GEN-LAST:event_jButton_registrarIngresoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //Verificamos que existan datos en la tabla
        int filas = jTable_listafacturas.getRowCount();
        if (filas > 0) {
            ImprimirInformeDeudaFacturas(jTable_listafacturas);
        } else {
            JOptionPane.showMessageDialog(this, "No existen deudas de empresas");
        }


    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField_ingresoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_ingresoKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
        if (c == '0' && jTextField_ingreso.getText().length() == 0) {
            evt.consume();
        }
        
        if (jTextField_ingreso.getText().trim().length()==11) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_ingresoKeyTyped

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
            java.util.logging.Logger.getLogger(ListadoFacturasPendientesPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoFacturasPendientesPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoFacturasPendientesPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoFacturasPendientesPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoFacturasPendientesPago().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_imprimir;
    private javax.swing.JButton jButton_registrarIngreso;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel_fila;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_listafacturas;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_factura;
    private javax.swing.JTextField jTextField_idCliente;
    private javax.swing.JTextField jTextField_ingreso;
    private javax.swing.JTextField jTextField_observaciones;
    // End of variables declaration//GEN-END:variables
}
