/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;

/**
 *
 * @author erwin
 */
public class InfomeVentas extends javax.swing.JFrame {

    String usuario, permiso;

    /**
     * Creates new form InfomeVentas
     */
    public InfomeVentas() {
        initComponents();
        llenarComboBoxTiposDeTrabajo();
        llenarComboBoxClientes();
        ConfiguracionGralJFrame();
    }

    public InfomeVentas(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
    }
    
    public void IniciarCaracteristicasGenerales(){
        llenarComboBoxTiposDeTrabajo();
        llenarComboBoxClientes();
    }
    
    public void ConfiguracionGralJFrame(){
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Informes *** "+"Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
    }

    public void GenerarInformeVentas(String fechaDesde, String fechaHasta, String tipoCliente, String usuario) {

//        String rutaArchivoACopiar = "D:"+File.separator+"Erwin"+File.separator + "ApacheNetbeans" + File.separator
//                + "GraficasJireh_1" + File.separator + "src" + File.separator + "documentos" + File.separator + "Informe de ventas.xlsx";
        String rutaArchivoACopiar = "Docs" + File.separator + "Informe de ventas.xlsx";

        String rutaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Informe de ventas.xlsx";

        try {

            FileInputStream archivo = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivo);
            archivo.close();
            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            //Datos fila 1
            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda20 = fila2.getCell(0);
            celda20.setCellValue("Desde " + fechaDesde + " hasta " + fechaHasta);

            //Datos fila 3
            XSSFRow fila3 = hoja.getRow(3);
            XSSFCell celda31 = fila3.getCell(1);
            celda31.setCellValue(new Date());

            //Datos fila 4
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda41 = fila4.getCell(1);
            celda41.setCellValue(usuario);
            
            
            //Datos de las ventas, empieza en la fila 7
            int filaEmpieza = 7;
            ArrayList<String[]> datos = ConsultarVentas(fechaDesde, fechaHasta, tipoCliente);

            //Declaramos una variable para acumular el total de ventas
            int sumaTotal=0;
            
            for (int i = 0; i < datos.size(); i++) {
                XSSFRow fila = hoja.getRow(filaEmpieza);
//                XSSFRow fila = hoja.createRow(filaEmpieza);
                for (int j = 0; j < 9; j++) {
                    XSSFCell celda = fila.getCell(j);
//                    XSSFCell celda = fila.createCell(j);
                    switch (j) {
                        case 0, 5, 7, 8 ->
                            celda.setCellValue(Double.parseDouble(datos.get(i)[j]));                            
                        case 1 ->
                            celda.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(datos.get(i)[j]));
                        default ->
                            celda.setCellValue(datos.get(i)[j]);
                    }
                    if (j==8) {
                        sumaTotal+=Double.parseDouble(datos.get(i)[j]);
                    }
                }
                filaEmpieza++;
            }
            
            //Incluimos en la fila 4 la suma de las ventas            
            XSSFCell celda48 = fila4.getCell(8);
            celda48.setCellValue(sumaTotal);

            FileOutputStream salida = new FileOutputStream(rutaGuardar);
            nuevoLibro.write(salida);
            salida.close();
            JOptionPane.showMessageDialog(this, "Informe generado con exito");
            
            MetodosGenerales.abrirArchivo(rutaGuardar);

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo para generar el informe. \n"
                    + "Asegurese de no tener abierto un archivo con el mismo nombre. Si el problema persiste, "
                    + "contacte al administrador \nInformeVentas GenerarInforme() \n" + ex);
            ex.printStackTrace();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error en generar el Workbook/Informe. Asegurese de no tener "
                    + "abierto un archivo con el mismo nombre. Si el problema persiste, contacte al administrador. "
                    + "InformeVentas GenerarInforme()");
            ex.printStackTrace();
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Error al parsear la fecha de String a Fecha");
            ex.printStackTrace();
        }

    }

    public ArrayList<String[]> ConsultarVentas(String fechaDesde, String fechaHasta, String tipoCliente) {
        ArrayList<String[]> datos = new ArrayList<>();
        String consulta = "";
        if (tipoCliente.equals("Todos")) {
            consulta = "select ventas.Idventa, ventas.FechaventaSistema, ventas.Vendedor, clientes.nombreCliente, "
                    + "clientes.tipoCliente, ventas.Cantidad, ventas.descripcionTrabajo, ventas.unitario, "
                    + "ventas.precio from ventas inner join clientes on ventas.Idcliente=clientes.idCliente "
                    + "where ventas.FechaventaSistema between ? and ?";
        } else if (tipoCliente.equals("Empresa") || tipoCliente.equals("Persona")) {
            consulta = "select ventas.Idventa, ventas.FechaventaSistema, ventas.Vendedor, clientes.nombreCliente, "
                    + "clientes.tipoCliente, ventas.Cantidad, ventas.descripcionTrabajo, ventas.unitario, "
                    + "ventas.precio from ventas inner join clientes on ventas.Idcliente=clientes.idCliente "
                    + "where clientes.tipoCliente=? and ventas.FechaventaSistema between ? and ?";
        }

        Connection cn = Conexion.Conectar();
        try {

            PreparedStatement pst = cn.prepareStatement(consulta);
            if (tipoCliente.equals("Todos")) {
                pst.setString(1, fechaDesde);
                pst.setString(2, fechaHasta);
            } else {
                pst.setString(1, tipoCliente);
                pst.setString(2, fechaDesde);
                pst.setString(3, fechaHasta);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String[] nuevo = new String[9];
                nuevo[0] = rs.getString("ventas.Idventa");
                nuevo[1] = rs.getString("ventas.FechaventaSistema");
                nuevo[2] = rs.getString("ventas.Vendedor");
                nuevo[3] = rs.getString("clientes.nombreCliente");
                nuevo[4] = rs.getString("clientes.tipoCliente");
                nuevo[5] = rs.getString("ventas.Cantidad");
                nuevo[6] = rs.getString("ventas.descripcionTrabajo");
                nuevo[7] = rs.getString("ventas.unitario");
                nuevo[8] = rs.getString("ventas.precio");

                datos.add(nuevo);

            }
            cn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al leer las ventas en las fechas indicadas. InformeVentas ConsultarVentas()");
        }

        return datos;
    }

    public void GenerarInformeIngresos(String fechaDesde, String fechaHasta, String tipoCliente, String usuario) {
//        String consulta1 = "";
//        String consulta2 = "";

        if (tipoCliente.equals("Todos")) {

            ArrayList<String[]> DatosAbonosEntradasDiarias = ConsultarAbonosEntradasDiarias(fechaDesde, fechaHasta);
            ArrayList<String[]> DatosAbonoFacturas = ConsultarAbonosFacturas(fechaDesde, fechaHasta);
            GenerarInformeConjunto(DatosAbonosEntradasDiarias, DatosAbonoFacturas, fechaDesde, fechaHasta);
        } else if (tipoCliente.equals("Persona")) {
            ArrayList<String[]> DatosAbonosEntradasDiarias = ConsultarAbonosEntradasDiarias(fechaDesde, fechaHasta);
            GenerarInforme(DatosAbonosEntradasDiarias, fechaDesde, fechaHasta);
        } else {
            ArrayList<String[]> DatosAbonoFacturas = ConsultarAbonosFacturas(fechaDesde, fechaHasta);
            GenerarInforme(DatosAbonoFacturas, fechaDesde, fechaHasta);
        }

    }

    public ArrayList<String[]> ConsultarAbonosEntradasDiarias(String fechaDesde, String fechaHasta) {

        ArrayList<String[]> listaAbonos = new ArrayList<>();
        String consulta = "select abonos.fechaAbonoSistema, abonos.idAbono, abonos.idVenta, clientes.nombreCliente, "
                + "abonos.valorAbono, abonos.observaciones, abonos.registradoPor, if(abonos.factura is null, 0, abonos.factura) "
                + "as factura from abonos inner join ventas on abonos.idVenta=ventas.Idventa inner join clientes "
                + "on ventas.Idcliente=clientes.idCliente where abonos.fechaAbonoSistema between ? "
                + "and ?";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaDesde);
            pst.setString(2, fechaHasta);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String[] infoAbono = new String[7];
                infoAbono[0] = rs.getString("abonos.fechaAbonoSistema");
                infoAbono[1] = rs.getString("abonos.idAbono");
                infoAbono[2] = rs.getString("abonos.idVenta");
                infoAbono[3] = rs.getString("clientes.nombreCliente");
                infoAbono[4] = rs.getString("abonos.valorAbono");
                String factura = rs.getString("factura");
                if (factura.equals("0")) {
                    infoAbono[5] = rs.getString("abonos.observaciones");
                } else {
                    infoAbono[5] = "Este abono fue facturado - Factura: " + factura + " - " + rs.getString("abonos.observaciones");
                }
                infoAbono[6] = rs.getString("abonos.registradoPor");

                listaAbonos.add(infoAbono);
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer la informacion de los abonos. "
                    + "ConsultarAbonosEntradasDiarias() InformeVentas");
        }

        return listaAbonos;
    }

    public ArrayList<String[]> ConsultarAbonosFacturas(String fechaDesde, String fechaHasta) {

        ArrayList<String[]> listaAbonosFactura = new ArrayList<>();
        String consulta = "select DISTINCT abonosfacturas.fechaAbonoSistema, abonosfacturas.idAbono, "
                + "abonosfacturas.idFactura, clientes.nombreCliente, abonosfacturas.valorAbono, "
                + "abonosfacturas.observaciones, abonosfacturas.registradoPor from abonosfacturas "
                + "inner join elementosfactura on abonosfacturas.idFactura=elementosfactura.factura "
                + "inner join ventas on elementosfactura.idVenta=ventas.Idventa inner join clientes "
                + "on ventas.Idcliente=clientes.idCliente where abonosfacturas.fechaAbonoSistema "
                + "between ? and ?";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaDesde);
            pst.setString(2, fechaHasta);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String[] infoAbono = new String[7];
                infoAbono[0] = rs.getString("abonosfacturas.fechaAbonoSistema");
                infoAbono[1] = rs.getString("abonosfacturas.idAbono");
                infoAbono[2] = rs.getString("abonosfacturas.idFactura");
                infoAbono[3] = rs.getString("clientes.nombreCliente");
                infoAbono[4] = rs.getString("abonosfacturas.valorAbono");
                infoAbono[5] = rs.getString("abonosfacturas.observaciones");
                infoAbono[6] = rs.getString("abonosfacturas.registradoPor");

                listaAbonosFactura.add(infoAbono);
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer la informacion de los abonos de las facturas. "
                    + "ConsultarAbonosEntradasDiarias() InformeVentas");
        }

        return listaAbonosFactura;
    }

    public void GenerarInformeConjunto(ArrayList<String[]> DatosAbonosEntradasDiarias, ArrayList<String[]> DatosAbonoFacturas, String fechaDesde, String fechaHasta) {
        String Plantilla = "Docs" + File.separator + "Informe de Ingresos.xlsx";
        String rutaAGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Informe de Ingresos.xlsx";

        try {
            FileInputStream plantillaAUtilizar = new FileInputStream(Plantilla);
            XSSFWorkbook Libro = new XSSFWorkbook(plantillaAUtilizar);
            XSSFSheet hoja = Libro.getSheetAt(0);

            //Datos fila 2
            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda20 = fila2.getCell(0);
            celda20.setCellValue("Desde " + fechaDesde + " hasta " + fechaHasta);

            //Datos fila 3
            XSSFRow fila3 = hoja.getRow(3);
            XSSFCell celda31 = fila3.getCell(1);
            celda31.setCellValue(new Date());

            //Datos fila 4
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda41 = fila4.getCell(1);
            celda41.setCellValue(this.usuario);

            //La fila desde donde se empiezan a agregar los datos al informe
            int filaEmpieza = 7;

            //Empezamos agregando los datos de los abonos de entradas diarias
            for (int i = 0; i < DatosAbonosEntradasDiarias.size(); i++) {
                XSSFRow fila = hoja.getRow(filaEmpieza);
                for (int j = 0; j < 7; j++) {
                    XSSFCell celda = fila.getCell(j);
                    switch (j) {
                        case 0 ->
                            celda.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(DatosAbonosEntradasDiarias.get(i)[j]));
                        case 1, 2, 4 ->
                            celda.setCellValue(Double.parseDouble(DatosAbonosEntradasDiarias.get(i)[j]));
                        default ->
                            celda.setCellValue(DatosAbonosEntradasDiarias.get(i)[j]);
                    }
                }
                filaEmpieza++;
            }
            //Agregamos la informacion concerniente a los pagos en las facturas
            for (int i = 0; i < DatosAbonoFacturas.size(); i++) {
                XSSFRow fila = hoja.getRow(filaEmpieza);
                for (int j = 0; j < 7; j++) {
                    XSSFCell celda = fila.getCell(j);
                    switch (j) {
                        case 0 ->
                            celda.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(DatosAbonoFacturas.get(i)[j]));
                        case 1, 2, 4 ->
                            celda.setCellValue(Double.parseDouble(DatosAbonoFacturas.get(i)[j]));
                        default ->
                            celda.setCellValue(DatosAbonoFacturas.get(i)[j]);
                    }
                }
                filaEmpieza++;
            }

            FileOutputStream salida = new FileOutputStream(rutaAGuardar);
            Libro.write(salida);
            salida.close();
            JOptionPane.showMessageDialog(this, "Informe generado con exito");
            MetodosGenerales.abrirArchivo(rutaAGuardar);
            
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo para generar el informe. \n"
                    + "Asegurese de no tener abierto un archivo con el mismo nombre. Si el problema persiste, "
                    + "contacte al administrador \nInformeVentas GenerarInforme() \n" + ex);
        } catch (IOException | ParseException ex) {
            JOptionPane.showMessageDialog(this, "Error en generar el Workbook/Informe. Asegurese de no tener "
                    + "abierto un archivo con el mismo nombre. Si el problema persiste, contacte al administrador. "
                    + "InformeVentas GenerarInforme() \n" + ex);
        }

    }

    public void GenerarInforme(ArrayList<String[]> DatosAbonosEntradasDiarias, String fechaDesde, String fechaHasta) {
        String Plantilla = "Docs" + File.separator + "Informe de Ingresos.xlsx";
        String rutaAGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Informe de Ingresos.xlsx";

        try {
            FileInputStream plantillaAUtilizar = new FileInputStream(Plantilla);
            XSSFWorkbook Libro = new XSSFWorkbook(plantillaAUtilizar);
            XSSFSheet hoja = Libro.getSheetAt(0);

            //Datos fila 2
            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda20 = fila2.getCell(0);
            celda20.setCellValue("Desde " + fechaDesde + " hasta " + fechaHasta);

            //Datos fila 3
            XSSFRow fila3 = hoja.getRow(3);
            XSSFCell celda31 = fila3.getCell(1);
            celda31.setCellValue(new Date());

            //Datos fila 4
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda41 = fila4.getCell(1);
            celda41.setCellValue(this.usuario);

            //La fila desde donde se empiezan a agregar los datos al informe
            int filaEmpieza = 7;

            //Empezamos agregando los datos de los abonos de entradas diarias
            for (int i = 0; i < DatosAbonosEntradasDiarias.size(); i++) {
                XSSFRow fila = hoja.getRow(filaEmpieza);
                for (int j = 0; j < 7; j++) {
                    XSSFCell celda = fila.getCell(j);
                    switch (j) {
                        case 0 ->
                            celda.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(DatosAbonosEntradasDiarias.get(i)[j]));
                        case 1, 2, 4 ->
                            celda.setCellValue(Double.parseDouble(DatosAbonosEntradasDiarias.get(i)[j]));
                        default ->
                            celda.setCellValue(DatosAbonosEntradasDiarias.get(i)[j]);
                    }
                }
                filaEmpieza++;
            }

            FileOutputStream salida = new FileOutputStream(rutaAGuardar);
            Libro.write(salida);
            salida.close();
            JOptionPane.showMessageDialog(this, "Informe generado con exito");
            MetodosGenerales.abrirArchivo(rutaAGuardar);

        } catch (FileNotFoundException ex) {

            JOptionPane.showMessageDialog(this, "Error al leer el archivo para generar el informe. \n"
                    + "Asegurese de no tener abierto un archivo con el mismo nombre. Si el problema persiste, "
                    + "contacte al administrador \nInformeVentas GenerarInforme() \n" + ex);

        } catch (IOException | ParseException ex) {
            JOptionPane.showMessageDialog(this, "Error en generar el Workbook/Informe. Asegurese de no tener "
                    + "abierto un archivo con el mismo nombre. Si el problema persiste, contacte al administrador. "
                    + "InformeVentas GenerarInforme() \n" + ex);

        }

    }

    public void llenarComboBoxTiposDeTrabajo() {
        String consulta = "select tipo from tipotrabajo";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                jComboBox_tipoTrabajo.addItem(tipo);
                jComboBox_tipoTrabajoTiempo.addItem(tipo);
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer los tipos de trabajo. InformeVentas llevanComboBoxTiposDeTrabajo()");
        }

    }

    public void llenarComboBoxClientes() {
        String consulta = "select nombreCliente from clientes";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String cliente = rs.getString("nombreCliente");
                jComboBox_clientes.addItem(cliente);
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer los clientes. InformeVentas llenarComboBoxClientes()");
        }

    }

    public void GenerarInformeTrabajosMasVendidos(String fechaDesde, String fechaHasta, String tipotrabajo) {

        String Plantilla = "Docs" + File.separator + "Informe de tipos de trabajo.xlsx";
        String rutaAGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Informe de tipos de trabajo.xlsx";

        try {
            FileInputStream nuevo = new FileInputStream(Plantilla);
            XSSFWorkbook wb = new XSSFWorkbook(nuevo);
            XSSFSheet hoja = wb.getSheetAt(0);

            //Datos de la fila 1
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda10 = fila1.getCell(0);
            celda10.setCellValue("Desde " + fechaDesde + " hasta " + fechaHasta);

            //  Datos tomados de la consuta
            ArrayList<String[]> lista = ConsultaInformacionTipoTrabajos(fechaDesde, fechaHasta, tipotrabajo);

            //Fila Inicial es la 4
            int filaInicial = 4;

            for (int i = 0; i < lista.size(); i++) {
                XSSFRow fila = hoja.getRow(filaInicial);
                for (int j = 0; j < 2; j++) {
                    XSSFCell celda = fila.getCell(j);
                    switch (j) {
                        case 0 ->
                            celda.setCellValue(lista.get(i)[j]);
                        case 1 ->
                            celda.setCellValue(Double.parseDouble(lista.get(i)[j]));
                    }
                }
                filaInicial++;
            }

            FileOutputStream salida = new FileOutputStream(rutaAGuardar);
            wb.write(salida);
            salida.close();

            JOptionPane.showMessageDialog(this, "Informe generado");
            MetodosGenerales.abrirArchivo(rutaAGuardar);
            
        } catch (IOException | NumberFormatException e) {

        }

    }

    public ArrayList<String[]> ConsultaInformacionTipoTrabajos(String fechaDesde, String fechaHasta, String tipotrabajo) {

        ArrayList<String[]> lista = new ArrayList<>();
        String consulta = "";
        if (tipotrabajo.equals("Todos")) {
            consulta = "select tipoTrabajo, if(sum(precio) is null, 0, sum(precio)) as precio from ventas where "
                    + "FechaventaSistema between ? and ? group by tipoTrabajo order by precio desc";

        } else {
            consulta = "select tipoTrabajo, if(sum(precio) is null, 0, sum(precio)) as precio from ventas where "
                    + "FechaventaSistema between ? and ? and tipoTrabajo=? order by precio desc";

        }

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);

            if (tipotrabajo.equals("Todos")) {
                pst.setString(1, fechaDesde);
                pst.setString(2, fechaHasta);
            } else {
                pst.setString(1, fechaDesde);
                pst.setString(2, fechaHasta);
                pst.setString(3, tipotrabajo);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String[] nuevo = new String[2];
                nuevo[0] = rs.getString("tipoTrabajo");
                nuevo[1] = rs.getString("precio");
                lista.add(nuevo);
            }

            cn.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en leer las ventas por tipo de trabajo en la base de datos. \n"
                    + "InformeVentas ConsultaInformacionTipoTrabajos() \n" + ex);
        }

        return lista;
    }

    public void CalcularTiempoPromedioDeProduccion(String fechaDesde, String fechaHasta, String tipotrabajo) {

        ArrayList<String[]> lista = ConsultarTiemposDeProduccion(fechaDesde, fechaHasta, tipotrabajo);
        //Verificamos que la lista no este vacia para evitar error por division cero
        long promedio = 0;
        int suma = 0;

        if (lista.size() > 0) {
            for (int i = 0; i < lista.size(); i++) {

                suma += MetodosGenerales.RestarFechas(lista.get(i)[0], lista.get(i)[1]);
            }

            promedio = Math.round(suma / lista.size());

            JOptionPane.showMessageDialog(this, "Fechas desde: " + fechaDesde + " hasta: " + fechaHasta + "\n\n"
                    + "Tipo de trabajo: " + tipotrabajo + "\n\n" + "Tiempo promedio de produccion: " + promedio + " días");

        } else {
            JOptionPane.showMessageDialog(this, "No hay trabajos en las fechas indicadas");
        }

    }

    public ArrayList<String[]> ConsultarTiemposDeProduccion(String fechaDesde, String fechaHasta, String tipotrabajo) {
        ArrayList<String[]> lista = new ArrayList<>();
        String consulta = "";

        if (tipotrabajo.equals("Todos")) {
            consulta = "select FechaventaSistema, fechaTerminacion from ventas where fechaTerminacion is not null and FechaventaSistema between ? and ?";

        } else {
            consulta = "select FechaventaSistema, fechaTerminacion from ventas where tipoTrabajo=? "
                    + "and fechaTerminacion is not null and FechaventaSistema between ? and ?";
        }

        Connection cn = Conexion.Conectar();
        try {

            PreparedStatement pst = cn.prepareStatement(consulta);

            if (tipotrabajo.equals("Todos")) {
                pst.setString(1, fechaDesde);
                pst.setString(2, fechaHasta);

            } else {
                pst.setString(1, tipotrabajo);
                pst.setString(2, fechaDesde);
                pst.setString(3, fechaHasta);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String[] dato = new String[2];
                dato[0] = rs.getString("FechaventaSistema");
                dato[1] = rs.getString("fechaTerminacion");

                lista.add(dato);
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer la lista de ventas y sus tiempos de produccion. "
                    + "InformeVentas ConsultarTiemposDeProduccion()\n" + e);
        }

        return lista;
    }

    public void CalcularTiempoPromedioDeCobro(String fechaDesde, String fechaHasta, String cliente) {

        if (cliente.equals("Todos")) {
             CalcularTiempoPromedioDeCobroTodosCliente(fechaDesde, fechaHasta);   
        } else {
            CalcularTiempoPromedioDeCobroCliente(fechaDesde, fechaHasta, cliente);            
        }   
    }
    
    public String[] ConsultarTipoCliente(String cliente) {
        String[] datos = new String[2];

        String consulta = "select tipoCliente, idCliente from clientes where nombreCliente=?";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, cliente);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                datos[0] = rs.getString("tipoCliente");
                datos[1] = rs.getString("idCliente");
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar el tipo de cliente de la BD\n " + e);
        }

        return datos;
    }

    public void CalcularTiempoPromedioDeCobroCliente(String fechaDesde, String fechaHasta, String cliente) {
        
   
        String consulta = "";
        String[] datosCliente = ConsultarTipoCliente(cliente);
        ArrayList<String[]> listaFechas = new ArrayList<>();

        if (datosCliente[0].equals("Empresa")) {
            consulta = "select fechaFactura, fechaSaldado from facturas where idCliente=? and estadoPago='Saldado'";
        } else {
            consulta = "select FechaventaSistema, fechaSaldado from ventas where estadoCuenta='Saldado' and Idcliente=?";
        }

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, datosCliente[1]);
            ResultSet rs = pst.executeQuery();
            
            if (datosCliente[0].equals("Empresa")) {
                while (rs.next()) {                    
                    String[] fechas = new String[2];
                    fechas[0] = rs.getString("fechaFactura");
                    fechas[1] = rs.getString("fechaSaldado");
                    listaFechas.add(fechas);
                }
            } else {
                while (rs.next()) {                    
                    String[] fechas = new String[2];
                    fechas[0] = rs.getString("FechaventaSistema");
                    fechas[1] = rs.getString("fechaSaldado");
                    listaFechas.add(fechas);
                }
            }
            //Declaramos una variable suma para calcular el promedio
            int suma=0;
            //Chequeamos que haya datos para mostrar o no
            if (listaFechas.size()>0) {
                for (int i = 0; i < listaFechas.size(); i++) {
                    suma+=MetodosGenerales.RestarFechas(listaFechas.get(i)[0], listaFechas.get(i)[1]);
                }
                
                int promedio=suma/listaFechas.size();
                
                JOptionPane.showMessageDialog(this, "Fechas desde: " + fechaDesde + " hasta: " + fechaHasta + "\n\n"
                    + "Cliente: " + cliente + "\n\n" + "Tiempo promedio de cobro: " + promedio + " días");
            } else {
                JOptionPane.showMessageDialog(this, "No hay datos en las fechas y para el cliente seleccionado");
            }          
            
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer las fechas del cliente\n" + e);
        }

       
    }
    
    public void CalcularTiempoPromedioDeCobroTodosCliente(String fechaDesde, String fechaHasta){
        //Consultamos los datos (fechas) de todos los clientes personas
        ArrayList<String []> listaEntradasDiarias= new ArrayList<>();
        String consulta = "select FechaventaSistema, fechaSaldado from ventas where estadoCuenta='Saldado'";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {                
                String [] nuevo = new String[2];
                nuevo[0]=rs.getString("FechaventaSistema");
                nuevo[1]=rs.getString("fechaSaldado");
                listaEntradasDiarias.add(nuevo);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar las fechas e informacion de clientes "
                    + "(Entradas duarias) InformeVentas CalcularTiempoPromedioDeCobroTodosCliente()\n"+e);
        }
        //Consultamos los datos (fechas) de todos los clientes Empresas
        ArrayList<String []> listaEmpresas= new ArrayList<>();
        String consulta1 = "select fechaFactura, fechaSaldado from facturas where estadoPago='Saldado'";
        Connection cn2 = Conexion.Conectar();
        try {
            PreparedStatement pst2 = cn2.prepareStatement(consulta1);
            ResultSet rs2 = pst2.executeQuery();
            while (rs2.next()) {                
                String [] nuevo = new String[2];
                nuevo[0]=rs2.getString("fechaFactura");
                nuevo[1]=rs2.getString("fechaSaldado");
                listaEmpresas.add(nuevo);
            }
            cn2.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar las fechas e informacion de clientes "
                    + "(Empresas) InformeVentas CalcularTiempoPromedioDeCobroTodosCliente()\n"+e);
        }
        
        //Comprobamos que las colecciones no estan vacias
        
        if ((listaEmpresas.size()+listaEntradasDiarias.size())>0) {
            int sumaEntradasDiarias=0;
            for (int i = 0; i < listaEntradasDiarias.size(); i++) {
                sumaEntradasDiarias+=MetodosGenerales.RestarFechas(listaEntradasDiarias.get(i)[0], listaEntradasDiarias.get(i)[1]);
            }
            
            int sumaEmpresas=0;
            for (int i = 0; i < listaEmpresas.size(); i++) {
                sumaEntradasDiarias+=MetodosGenerales.RestarFechas(listaEmpresas.get(i)[0], listaEmpresas.get(i)[1]);
            }
            
            int promedio = (sumaEmpresas+sumaEntradasDiarias)/(listaEntradasDiarias.size()+listaEmpresas.size());
            
            JOptionPane.showMessageDialog(this, "Fechas desde: " + fechaDesde + " hasta: " + fechaHasta + "\n\n"
                    + "Cliente: Todos\n\nTiempo promedio de cobro: " + promedio + " días");
            
        } else {
            JOptionPane.showMessageDialog(this, "No hay datos en las fechas seleccionadas");
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jDateChooser_desde = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser_hasta = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jComboBox_clienteVentas = new javax.swing.JComboBox<>();
        jButton_generarVentas = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jComboBox_clienteIngresos = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jButton_generarIngresos = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jComboBox_tipoTrabajo = new javax.swing.JComboBox<>();
        jButton_tipoTrabajo = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox_tipoTrabajoTiempo = new javax.swing.JComboBox<>();
        jButton_tipoTrabajoTiempo = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jComboBox_clientes = new javax.swing.JComboBox<>();
        jButton_tiempopromediocobro = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Rango de fechas del informe", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Desde");

        jDateChooser_desde.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jDateChooser_desdeKeyTyped(evt);
            }
        });

        jLabel2.setText("Hasta");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jDateChooser_desde, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jDateChooser_hasta, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jDateChooser_desde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jDateChooser_hasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informe de ventas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel4.setText("Tipo de cliente");

        jComboBox_clienteVentas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Empresa", "Persona" }));

        jButton_generarVentas.setText("Generar");
        jButton_generarVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generarVentasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(40, 40, 40)
                .addComponent(jComboBox_clienteVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addComponent(jButton_generarVentas)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_clienteVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jButton_generarVentas))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informe de ingresos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jComboBox_clienteIngresos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Empresa", "Persona" }));

        jLabel5.setText("Tipo de cliente");

        jButton_generarIngresos.setText("Generar");
        jButton_generarIngresos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generarIngresosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(42, 42, 42)
                .addComponent(jComboBox_clienteIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_generarIngresos)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_generarIngresos)
                    .addComponent(jComboBox_clienteIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informe de venta por tipo de trabajo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel6.setText("Trabajo");

        jComboBox_tipoTrabajo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos" }));

        jButton_tipoTrabajo.setText("Generar");
        jButton_tipoTrabajo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_tipoTrabajoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(34, 34, 34)
                .addComponent(jComboBox_tipoTrabajo, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_tipoTrabajo)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_tipoTrabajo)
                    .addComponent(jComboBox_tipoTrabajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informe de tiempos de produccion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel7.setText("Tiempos de produccion");

        jComboBox_tipoTrabajoTiempo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos" }));

        jButton_tipoTrabajoTiempo.setText("Generar");
        jButton_tipoTrabajoTiempo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_tipoTrabajoTiempoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addGap(34, 34, 34)
                .addComponent(jComboBox_tipoTrabajoTiempo, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_tipoTrabajoTiempo)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_tipoTrabajoTiempo)
                    .addComponent(jComboBox_tipoTrabajoTiempo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informe tiempo promedio de produccion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel8.setText("Tiempos prom Cobro");

        jComboBox_clientes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos" }));

        jButton_tiempopromediocobro.setText("Generar");
        jButton_tiempopromediocobro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_tiempopromediocobroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jComboBox_clientes, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_tiempopromediocobro)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_tiempopromediocobro)
                    .addComponent(jComboBox_clientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 41, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_generarVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generarVentasActionPerformed
        //Verificamos que las fechas se hayan seleccionado
        if (jDateChooser_desde.getDate() != null && jDateChooser_hasta.getDate() != null) {
            String fechaDesde = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_desde.getDate());
            String fechaHasta = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_hasta.getDate());
            //Verificamos de que tipo de clientes se quiere el informe
            String tipoCliente = jComboBox_clienteVentas.getSelectedItem().toString();

            GenerarInformeVentas(fechaDesde, fechaHasta, tipoCliente, this.usuario);
            dispose();

        } else {
            JOptionPane.showMessageDialog(this, "Selecciones ambas fechas");
        }
    }//GEN-LAST:event_jButton_generarVentasActionPerformed

    private void jButton_generarIngresosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generarIngresosActionPerformed
        //Verificamos que las fechas se hayan seleccionado
        if (jDateChooser_desde.getDate() != null && jDateChooser_hasta.getDate() != null) {
            String fechaDesde = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_desde.getDate());
            String fechaHasta = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_hasta.getDate());
            //Verificamos de que tipo de clientes se quiere el informe
            String tipoCliente = jComboBox_clienteIngresos.getSelectedItem().toString();

            GenerarInformeIngresos(fechaDesde, fechaHasta, tipoCliente, this.usuario);

        } else {
            JOptionPane.showMessageDialog(this, "Selecciones ambas fechas");
        }


    }//GEN-LAST:event_jButton_generarIngresosActionPerformed

    private void jButton_tipoTrabajoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_tipoTrabajoActionPerformed

        //Verificamos que las fechas se hayan seleccionado
        if (jDateChooser_desde.getDate() != null && jDateChooser_hasta.getDate() != null) {
            String fechaDesde = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_desde.getDate());
            String fechaHasta = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_hasta.getDate());
            //Verificamos de que tipo de clientes se quiere el informe
            String tipotrabajo = jComboBox_tipoTrabajo.getSelectedItem().toString();

            GenerarInformeTrabajosMasVendidos(fechaDesde, fechaHasta, tipotrabajo);

        } else {
            JOptionPane.showMessageDialog(this, "Selecciones ambas fechas");
        }

    }//GEN-LAST:event_jButton_tipoTrabajoActionPerformed

    private void jDateChooser_desdeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jDateChooser_desdeKeyTyped

    }//GEN-LAST:event_jDateChooser_desdeKeyTyped

    private void jButton_tipoTrabajoTiempoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_tipoTrabajoTiempoActionPerformed
        //Verificamos que las fechas se hayan seleccionado
        if (jDateChooser_desde.getDate() != null && jDateChooser_hasta.getDate() != null) {
            String fechaDesde = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_desde.getDate());
            String fechaHasta = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_hasta.getDate());
            //Verificamos de que tipo de clientes se quiere el informe
            String tipotrabajo = jComboBox_tipoTrabajoTiempo.getSelectedItem().toString();

            CalcularTiempoPromedioDeProduccion(fechaDesde, fechaHasta, tipotrabajo);

        } else {
            JOptionPane.showMessageDialog(this, "Selecciones ambas fechas");
        }
    }//GEN-LAST:event_jButton_tipoTrabajoTiempoActionPerformed

    private void jButton_tiempopromediocobroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_tiempopromediocobroActionPerformed
        //Verificamos que las fechas se hayan seleccionado
        if (jDateChooser_desde.getDate() != null && jDateChooser_hasta.getDate() != null) {
            String fechaDesde = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_desde.getDate());
            String fechaHasta = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_hasta.getDate());
            //Verificamos de que tipo de clientes se quiere el informe
            String cliente = jComboBox_clientes.getSelectedItem().toString();

            CalcularTiempoPromedioDeCobro(fechaDesde, fechaHasta, cliente);

        } else {
            JOptionPane.showMessageDialog(this, "Selecciones ambas fechas");
        }
    }//GEN-LAST:event_jButton_tiempopromediocobroActionPerformed

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
            java.util.logging.Logger.getLogger(InfomeVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InfomeVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InfomeVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InfomeVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InfomeVentas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_generarIngresos;
    private javax.swing.JButton jButton_generarVentas;
    private javax.swing.JButton jButton_tiempopromediocobro;
    private javax.swing.JButton jButton_tipoTrabajo;
    private javax.swing.JButton jButton_tipoTrabajoTiempo;
    private javax.swing.JComboBox<String> jComboBox_clienteIngresos;
    private javax.swing.JComboBox<String> jComboBox_clienteVentas;
    private javax.swing.JComboBox<String> jComboBox_clientes;
    private javax.swing.JComboBox<String> jComboBox_tipoTrabajo;
    private javax.swing.JComboBox<String> jComboBox_tipoTrabajoTiempo;
    private com.toedter.calendar.JDateChooser jDateChooser_desde;
    private com.toedter.calendar.JDateChooser jDateChooser_hasta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    // End of variables declaration//GEN-END:variables
}
