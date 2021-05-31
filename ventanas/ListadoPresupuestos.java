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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 *
 * @author erwin
 */
public class ListadoPresupuestos extends javax.swing.JFrame {

    String usuario, permiso;
    DefaultTableModel modelo;

    /**
     * Creates new form Presupuestos
     */
    public ListadoPresupuestos() {
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
        
    }

    public ListadoPresupuestos(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
        //Inhabilitamos los campos a los que no debe tener acceso los usuarios Asistentes
//        if (permiso.equals("Asistente")) {
//            InhabilitarParaAsistente();
//        }       

    }
    
    public void ConfiguracionGralJFrame(){
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado de presupuestos *** "+"Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
    }
    
    public void IniciarCaracteristicasGenerales(){
        SettearTabla();
        InhabilitarCampos();   
        llenarTabla();
    }

    public void InhabilitarCampos() {
        jTextField_NumeroPresup.setEnabled(false);
        jTextField_descripcion.setEnabled(false);
        jLabel_fila.setVisible(false);

    }

    public void limpiarCampos() {
        jTextField_NumeroPresup.setText("");
        jTextField_descripcion.setText("");

    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_Presupuestos.getRowCount(); i++) {
            this.modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void SettearTabla() {
        modelo = (DefaultTableModel) jTable_Presupuestos.getModel();
    }

    public void llenarTabla() {
        modelo = (DefaultTableModel) jTable_Presupuestos.getModel();

        String consulta = "SELECT presupuestos.idPresupuesto, presupuestos.fecha, presupuestos.descripcion, "
                + "presupuestos.fechaInicio, presupuestos.fechaFin, IF(sum(itemspresupuesto.valorPresupuestado "
                + "is null), 0, sum(itemspresupuesto.valorPresupuestado)) as presupuestado, presupuestos.estado, "
                + "presupuestos.registradoPor from presupuestos left join itemspresupuesto on "
                + "presupuestos.idPresupuesto=itemspresupuesto.idPresupuesto group by presupuestos.idPresupuesto";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[8];
                nuevo[0] = rs.getString("presupuestos.idPresupuesto");
                nuevo[1] = rs.getString("presupuestos.fecha");
                nuevo[2] = rs.getString("presupuestos.descripcion");
                nuevo[3] = rs.getString("presupuestos.fechaInicio");
                nuevo[4] = rs.getString("presupuestos.fechaFin");
                nuevo[5] = rs.getString("presupuestado");
                nuevo[5] = ConvertirIntAMoneda(Double.parseDouble(nuevo[5].toString()));
                nuevo[6] = rs.getString("presupuestos.estado");
                nuevo[7] = rs.getString("presupuestos.registradoPor");
                modelo.addRow(nuevo);
            }

            jTable_Presupuestos.setModel(modelo);
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer la tabla de presupuestos ListadoPresupuestos llenarTabla()");
            e.printStackTrace();
        }

    }

//    public void InhabilitarParaAsistente() {
//        jButton_cambiarEstado.setEnabled(false);
//        jButton_agregarGastos.setEnabled(false);
//        jButton_editarRubros.setEnabled(false);
//    }

    public void CambiarEstadoPresupuesto(String idpresupuesto, String estado) {

        String consulta = "update presupuestos set estado=? where idPresupuesto=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, estado);
            pst.setString(2, idpresupuesto);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Estado de presupuesto actualizado");

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en actualizar el estado del presupuesto");
        }
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

    public void GenerarInformeEconomico(String idPresupuesto, String descripcionPresupuesto, String fechaInicial,
            String fechaFinal, String usuario) {

        //Llenamos el archivo excel
        String rutaPlantilla = "Docs" + File.separator + "Informe economico.xlsx";
        String rutaAGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Informe economico.xlsx";

        try {

            FileInputStream plantilla = new FileInputStream(rutaPlantilla);
            XSSFWorkbook wb = new XSSFWorkbook(plantilla);
            plantilla.close();
            XSSFSheet hoja = wb.getSheetAt(0);
            //Datos fila 1
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda10 = fila1.getCell(0);
            celda10.setCellValue("Desde " + fechaInicial + " hasta " + fechaFinal + " - " + descripcionPresupuesto);
            //Datos fila 3
            XSSFRow fila3 = hoja.getRow(3);
            XSSFCell celda31 = fila3.getCell(1);
            celda31.setCellValue(new Date());
            //Datos fila 4
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda41 = fila4.getCell(1);
            celda41.setCellValue(usuario);

            //Consultamos el listado de conceptos del presupuesto
            ArrayList<String[]> lista = ConsultarGastos(idPresupuesto);
            //Consultamos el listado detallado de gastos del presupuesto
            ArrayList<String []> listaGastos=ConsultarListadoDeGastos(idPresupuesto);
            //Consultamos el listado consolidado de gastos del presupuesto
            ArrayList<String []> listaGastosConsolidados=ConsultarGastosSumaConsolidada(idPresupuesto);
            
            //Llenamos los valores de la tabla
            //Los datos empiezan a cargarse desde la fila 15
            int filaInicial = 15;
//          int sumaGastos = 0;
            int sumaPresupuesto = 0;

            for (int i = 0; i < lista.size(); i++) {
                XSSFRow fila = hoja.getRow(filaInicial);
                for (int j = 0; j < 6; j++) {
                    XSSFCell celda = fila.getCell(j);
                    switch (j) {
                        case 0, 4 -> celda.setCellValue(Double.parseDouble(lista.get(i)[j]));
                        case 5-> {
                            for (int k = 0; k < listaGastosConsolidados.size(); k++) {
                                if (lista.get(i)[2].equals(listaGastosConsolidados.get(k)[0])) {
                                    celda.setCellValue(Double.parseDouble(listaGastosConsolidados.get(k)[1]));
                                    break;
                                }
                            }
                        }
                        default -> celda.setCellValue(lista.get(i)[j]);
                    }

                }
                
                
                
                sumaPresupuesto += Integer.parseInt(lista.get(i)[4]);
                filaInicial++;
            }
            filaInicial++;

            //Agregamos los totales al final de la tabla
            XSSFRow filatotales = hoja.getRow(filaInicial);
            XSSFCell celdaTextoSubt = filatotales.getCell(3);
            celdaTextoSubt.setCellValue("SUBTOTAL");
            XSSFCell celdaSubPresup = filatotales.getCell(4);
            celdaSubPresup.setCellValue(sumaPresupuesto);
//            XSSFCell celdaSubGastado = filatotales.getCell(5);
//            celdaSubGastado.setCellValue(sumaGastos);

            //Datos fila 11 hoja 1
            XSSFRow fila11 = hoja.getRow(11);
            XSSFCell celda115 = fila11.getCell(5);
            

            //********************************************HOJA 2
            //Agregamos los datos de la hoja partidas
            ArrayList<String[]> listaPartidas = ConsultarPartidas(idPresupuesto);

            XSSFSheet hoja2 = wb.getSheetAt(1);

            //Datos fila1 hoja 2
            XSSFRow fila1Hoja2 = hoja2.getRow(1);
            XSSFCell celda10Hoja2 = fila1Hoja2.getCell(0);
            celda10Hoja2.setCellValue("Desde " + fechaInicial + " hasta " + fechaFinal + " - " + descripcionPresupuesto);

            //Datos fila3 hoja 2
            XSSFRow fila3Hoja2 = hoja2.getRow(3);
            XSSFCell celda31Hoja2 = fila3Hoja2.getCell(1);
            celda31Hoja2.setCellValue(new Date());

            //Datos fila4 hoja 2
            XSSFRow fila4Hoja2 = hoja2.getRow(4);
            XSSFCell celda41Hoja2 = fila4Hoja2.getCell(1);
            celda41Hoja2.setCellValue(usuario);

            //Datos de la tabla empiezan en la fila 7
            int filaInicio = 7;
            int sumaPartidas = 0;

            for (int i = 0; i < listaPartidas.size(); i++) {
                XSSFRow filaHoja2 = hoja2.getRow(filaInicio);
               
                for (int j = 0; j < 6; j++) {
                    XSSFCell celdaHoja2 = filaHoja2.getCell(j);
                    switch (j) {
                        case 0, 3 ->
                            celdaHoja2.setCellValue(Double.parseDouble(listaPartidas.get(i)[j]));
                        case 1 ->
                            celdaHoja2.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(listaPartidas.get(i)[j]));
                        case 2, 4, 5 ->
                            celdaHoja2.setCellValue(listaPartidas.get(i)[j]);
                    }

                }
                filaInicio++;
                sumaPartidas += Integer.parseInt(listaPartidas.get(i)[3]);
            }
            
            filaInicio++;
            //Llenamos los datos de subtotal
            XSSFRow filatotalesHoja2 = hoja2.getRow(4);
            XSSFCell celdaTextoSubtHoja2 = filatotalesHoja2.getCell(5);
            celdaTextoSubtHoja2.setCellValue((double)sumaPartidas);
            
            //Completamos el dato del resumen de la primera hoja que se calcularon en la segunda hoja
            
            //Fila 7
            XSSFRow fila7=hoja.getRow(7);
            XSSFCell celda75=fila7.getCell(5);
            celda75.setCellValue(sumaPartidas);

            //********************************************HOJA 2            
            //Cargamos los datos de las hoja 3 (Gastos)
            
            XSSFSheet hoja3 = wb.getSheetAt(2);
            
            //Llenamos los valores de la fila 2
            XSSFRow fila1Hoja3 = hoja3.getRow(1);
            XSSFCell celda10Hoja3 = fila1Hoja3.getCell(0);
            celda10Hoja3.setCellValue("Desde " + fechaInicial + " hasta " + fechaFinal + " - " + descripcionPresupuesto);
            
            //Llenamos los valores de la fila 4
            XSSFRow fila3Hoja3 = hoja3.getRow(3);
            XSSFCell celda31Hoja3 = fila3Hoja3.getCell(1);
            celda31Hoja3.setCellValue(new Date());
            
            //Llenamos los valores de la fila 5
            XSSFRow fila4Hoja3 = hoja3.getRow(4);
            XSSFCell celda41Hoja3 = fila4Hoja3.getCell(1);
            celda41Hoja3.setCellValue(usuario);
            
            
            
            //Llenamos los datos de la tabla
            int sumaGastos=0;           
            int filaInicioHoja3=8;
            
            for (int i = 0; i < listaGastos.size(); i++) {
                XSSFRow filaTablaHoja3 = hoja3.getRow(filaInicioHoja3);
                for (int j = 0; j < 8; j++) {
                    XSSFCell celdaTablaHoja3 = filaTablaHoja3.getCell(j);
                    switch(j){
                        case 0 -> celdaTablaHoja3.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(listaGastos.get(i)[j]));
                        case 1, 2, 3, 5, 6, 7 -> celdaTablaHoja3.setCellValue(listaGastos.get(i)[j]);
                        case 4 -> celdaTablaHoja3.setCellValue(Double.parseDouble(listaGastos.get(i)[j]));
                    }
                }
                filaInicioHoja3++;
                if (listaGastos.get(i)[5].equals("Registrado")) {
                    sumaGastos+=Double.parseDouble(listaGastos.get(i)[4]);
                }
                
            }
            
            // Completamos el total de gastos en la hoja uno y la hoja 3
            XSSFCell celda46Hoja3 = fila4Hoja3.getCell(6);
            celda46Hoja3.setCellValue((double)sumaGastos);
            celda115.setCellValue(sumaGastos);
            
            //Completamos los campos de la hoja detalle de entradas diarias
            
            ArrayList<String[]> listadoAbonosEntradasDiarias=ConsultarAbonosEntradasDiarias(fechaInicial, fechaFinal);
            
            XSSFSheet hoja4=wb.getSheetAt(3);
            XSSFRow fila1Hoja4 = hoja4.getRow(1);
            XSSFCell celda10Hoja4 = fila1Hoja4.getCell(0);
            celda10Hoja4.setCellValue("Desde " + fechaInicial + " hasta " + fechaFinal + " - " + descripcionPresupuesto);
            
            XSSFRow fila3Hoja4 = hoja4.getRow(3);
            XSSFCell celda31Hoja4 = fila3Hoja4.getCell(1);
            celda31Hoja4.setCellValue(new Date());
            
            XSSFRow fila4Hoja4 = hoja4.getRow(4);
            XSSFCell celda41Hoja4=fila4Hoja4.getCell(1);
            celda41Hoja4.setCellValue(usuario);
            
            
            
            //Completamos la tabla de datos
            int filaInicialHoja4=7;
            int sumaAbonos=0;
            
            for (int i = 0; i < listadoAbonosEntradasDiarias.size(); i++) {
                XSSFRow primeraFilaTablaHoja4 = hoja4.getRow(filaInicialHoja4);
                for (int j = 0; j < 7; j++) {
                    XSSFCell primeraCeldaTablaHoja4 = primeraFilaTablaHoja4.getCell(j);
                    
                    switch(j){
                        case 0, 1, 5 -> primeraCeldaTablaHoja4.setCellValue(Double.parseDouble(listadoAbonosEntradasDiarias.get(i)[j]));
                        case 2 -> primeraCeldaTablaHoja4.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(listadoAbonosEntradasDiarias.get(i)[j]));
                        default -> primeraCeldaTablaHoja4.setCellValue((String)(listadoAbonosEntradasDiarias.get(i)[j]));
                    }
                }
                filaInicialHoja4++;
                sumaAbonos+=Double.parseDouble(listadoAbonosEntradasDiarias.get(i)[5]);
            }
            
            //Ponemos el valor total de abonos en la hoja 4
            
            XSSFCell celda45Hoja4=fila4Hoja4.getCell(5);
            celda45Hoja4.setCellValue((double)sumaAbonos);
            
            //Ponemos el valor total de abono en la hoja 1 (resumen)
            
            XSSFRow fila8Hoja1= hoja.getRow(8);
            XSSFCell celda85Hoja1=fila8Hoja1.getCell(5);
            celda85Hoja1.setCellValue((double)sumaAbonos);
            
            //Completamos los datos de la hoja Ingreso de empresas
            
            ArrayList<String []> listadoDeAbonosFacturas = ConsultarAbonosFacturas(fechaInicial, fechaFinal);
            
            
            XSSFSheet hoja5=wb.getSheetAt(4);
            
            XSSFRow fila1Hoja5 = hoja5.getRow(1);
            XSSFCell celda10Hoja5 = fila1Hoja5.getCell(0);
            celda10Hoja5.setCellValue("Desde " + fechaInicial + " hasta " + fechaFinal + " - " + descripcionPresupuesto);
            
            XSSFRow fila3Hoja5 = hoja5.getRow(3);
            XSSFCell celda31Hoja5 = fila3Hoja5.getCell(1);
            celda31Hoja5.setCellValue(new Date());
            
            XSSFRow fila4Hoja5=hoja5.getRow(4);
            XSSFCell celda41Hoja5 = fila4Hoja5.getCell(1);
            celda41Hoja5.setCellValue(usuario);
            
            
            
            //Llenamos los datos de la tabla abonos facturas
            int filaInicialAbonosFactura=7;
            int sumaAbonosFacturas=0;
            
            for (int i = 0; i < listadoDeAbonosFacturas.size(); i++) {
                XSSFRow primeraFilaTablaHoja5 = hoja5.getRow(filaInicialAbonosFactura);
                for (int j = 0; j < 7; j++) {
                    XSSFCell primeraCeldaTablaHoja5 = primeraFilaTablaHoja5.getCell(j);
                    
                    switch(j){
                        case 0, 1, 5 -> primeraCeldaTablaHoja5.setCellValue(Double.parseDouble(listadoDeAbonosFacturas.get(i)[j]));
                        case 2 -> primeraCeldaTablaHoja5.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(listadoDeAbonosFacturas.get(i)[j]));
                        default -> primeraCeldaTablaHoja5.setCellValue((String)(listadoDeAbonosFacturas.get(i)[j]));
                    }
                }
                filaInicialAbonosFactura++;
                sumaAbonosFacturas+=Double.parseDouble(listadoDeAbonosFacturas.get(i)[5]);
                
            }
            
            //Total de abonos empresas
            XSSFCell celda45Hoja5 = fila4Hoja5.getCell(5);
            celda45Hoja5.setCellValue((double)sumaAbonosFacturas);
            
            //Ponemos el valor total de abonos por factura en el resumen de la hoja 1
            
            XSSFRow fila9Hoja1 = hoja.getRow(9);
            XSSFCell celda95Hoja1 = fila9Hoja1.getCell(5);
            celda95Hoja1.setCellValue((double)sumaAbonosFacturas);
            
            //Completamos la fila total efectivo que resulta de sumar las partidas y los ingresos por entradas diarias y empresas
            
            XSSFRow fila10Hoja1 = hoja.getRow(10);
            XSSFCell celda105Hoja1 = fila10Hoja1.getCell(5);
            celda105Hoja1.setCellValue((double)(sumaPartidas+sumaAbonos+sumaAbonosFacturas));
            
            //Completamos la ganancia que es la resta de total efectivo menos gastos
            XSSFRow fila12Hoja1 = hoja.getRow(12);
            XSSFCell celda125Hoja1 = fila12Hoja1.getCell(5);
            celda125Hoja1.setCellValue((double)(sumaPartidas+sumaAbonos+sumaAbonosFacturas-sumaGastos));
            
            //***********************************
            FileOutputStream nuevo = new FileOutputStream(rutaAGuardar);
            wb.write(nuevo);
            nuevo.close();
            JOptionPane.showMessageDialog(this, "Informe generado");
            MetodosGenerales.abrirArchivo(rutaAGuardar);

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error en leer la plantilla y generar el informe. "
                    + "ListadoPresupuestos GenerarInformeEconomico()\n\n" + e);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error en generar el archivo\n" + ex);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Error en parsear la fecha\n" + ex);
        }

    }

    public ArrayList<String[]> ConsultarGastos(String idPresupuesto) {

        ArrayList<String[]> listaDatos = new ArrayList<>();

        String consulta="select itemspresupuesto.idGasto, maestrogastos.idGasto, tipogastos.tipoGasto, "
                + "maestrogastos.idRubro, modalidadgasto.Descripcion, maestrogastos.descripcion, "
                + "itemspresupuesto.valorPresupuestado from itemspresupuesto inner join maestrogastos on itemspresupuesto.idGasto=maestrogastos.id inner join "
                + "modalidadgasto on maestrogastos.idModalidad=modalidadgasto.idModalidad inner join tipogastos on "
                + "maestrogastos.idGasto=tipogastos.idGasto left join gastospresupuestos on itemspresupuesto.idGasto=gastospresupuestos.idConcepto "
                + "where itemspresupuesto.idPresupuesto=? group by itemspresupuesto.idGasto order by maestrogastos.idGasto, "
                + "maestrogastos.idRubro";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String[] dato = new String[5];
                dato[0] = rs.getString("itemspresupuesto.idGasto");
                dato[1] = rs.getString("tipogastos.tipoGasto");
                dato[2] = rs.getString("maestrogastos.descripcion");
                dato[3] = rs.getString("modalidadgasto.Descripcion");
                dato[4] = rs.getString("itemspresupuesto.valorPresupuestado");
//                dato[5] = rs.getString("sumaGastado");
                listaDatos.add(dato);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el presupuesto y sus gastos relacionados. "
                    + "ListadoPresupuestos GenerarInformeEconomico()\n" + e);
        }

        return listaDatos;
    }

    public ArrayList<String[]> ConsultarPartidas(String idPresupuesto) {

        ArrayList<String[]> lista = new ArrayList<>();
        String consulta = "select id, fecha, concepto, valor, observaciones, registradoPor from partidaspresupuestos "
                + "where idPresupuesto=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                String[] dato = new String[6];
                dato[0] = rs.getString("id");
                dato[1] = rs.getString("fecha");
                dato[2] = rs.getString("concepto");
                dato[3] = rs.getString("valor");
                dato[4] = rs.getString("observaciones");
                dato[5] = rs.getString("registradoPor");
                lista.add(dato);

            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer las partidas del presupuesto ListadoPresupuestos "
                    + "ConsultarPartidas()\n" + e);
        }

        return lista;
    }
    
    public ArrayList<String []> ConsultarListadoDeGastos(String idPresupuesto){
        
        ArrayList<String []> listadoGastos = new ArrayList<>();
        String consulta = "select gastospresupuestos.fechaGasto, maestrogastos.descripcion, gastospresupuestos.observaciones, "
                + "gastospresupuestos.factura, gastospresupuestos.valor, gastospresupuestos.estado, gastospresupuestos.registradoPor, "
                + "gastospresupuestos.observAutoriza from gastospresupuestos inner join maestrogastos on "
                + "gastospresupuestos.idConcepto=maestrogastos.id where gastospresupuestos.idPrespuesto=?";
        
        Connection cn =  Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {                
                String [] gasto = new String [8];
                gasto[0]=rs.getString("gastospresupuestos.fechaGasto");
                gasto[1]=rs.getString("maestrogastos.descripcion");
                gasto[2]=rs.getString("gastospresupuestos.observaciones");
                gasto[3]=rs.getString("gastospresupuestos.factura");
                gasto[4]=rs.getString("gastospresupuestos.valor");
                gasto[5]=rs.getString("gastospresupuestos.estado");
                gasto[6]=rs.getString("gastospresupuestos.registradoPor");
                gasto[7]=rs.getString("gastospresupuestos.observAutoriza");
                
                listadoGastos.add(gasto);
            }
            
            cn.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el listado de gastos\n"+e);
        }
        
        return listadoGastos;
    }
    
    public ArrayList<String []> ConsultarGastosSumaConsolidada(String idPresupuesto){
        
        ArrayList<String []> listaGastosConsolidados=new ArrayList<>();
        String consulta = "SELECT maestrogastos.descripcion, SUM(valor) as suma from gastospresupuestos "
                + "inner join maestrogastos on maestrogastos.id=gastospresupuestos.idConcepto where "
                + "idPrespuesto=? and gastospresupuestos.estado='Registrado' group by idConcepto";
        
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {                
                String [] nuevo= new String[2];
                nuevo[0]=rs.getString("maestrogastos.descripcion");
                nuevo[1]=rs.getString("suma");
                listaGastosConsolidados.add(nuevo);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los gastos consolidados del presupuesto\n"+e);
        }
        return listaGastosConsolidados;
    }
    
    public ArrayList<String[]> ConsultarAbonosEntradasDiarias(String fechaInicial, String fechaFinal){
        
        ArrayList<String []> listadeAbonosEntradasDiarias = new ArrayList<>();
        String consulta="select abonos.idAbono, abonos.fechaAbonoSistema, abonos.idVenta, clientes.nombreCliente, abonos.observaciones, "
                + "abonos.valorAbono, abonos.registradoPor from abonos inner join ventas on "
                + "abonos.idVenta=ventas.Idventa inner join clientes on ventas.Idcliente=clientes.idCliente "
                + "where abonos.fechaAbonoSistema BETWEEN ? and ? ORDER BY abonos.idAbono desc";
        
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaInicial);
            pst.setString(2, fechaFinal);
            
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {                
                String [] abono = new String[7];
                abono[0]=rs.getString("abonos.idAbono");
                abono[1]=rs.getString("abonos.idVenta");
                abono[2]=rs.getString("abonos.fechaAbonoSistema");
                abono[3]=rs.getString("clientes.nombreCliente");
                abono[4]=rs.getString("abonos.observaciones");
                abono[5]=rs.getString("abonos.valorAbono");
                abono[6]=rs.getString("abonos.registradoPor");
                
                listadeAbonosEntradasDiarias.add(abono);
            }
            
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el listado de abonos\n"+e);
        }          
        return listadeAbonosEntradasDiarias;
    }
    
    public ArrayList<String []> ConsultarAbonosFacturas(String fechaDesde, String fechaHasta){
        ArrayList<String []> listadoAbonosFacturas = new ArrayList<>();
        
        String consulta="SELECT abonosfacturas.idAbono, abonosfacturas.idFactura, abonosfacturas.fechaAbonoSistema, "
                + "clientes.nombreCliente, abonosfacturas.observaciones, abonosfacturas.valorAbono, "
                + "abonosfacturas.registradoPor from abonosfacturas inner join facturas on "
                + "abonosfacturas.idFactura=facturas.idFactura inner join clientes on "
                + "facturas.idCliente=clientes.idCliente where abonosfacturas.fechaAbonoSistema "
                + "between ? and ?";


        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaDesde);
            pst.setString(2, fechaHasta);
            
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {                
                String [] abono = new String[7];
                abono[0]=rs.getString("abonosfacturas.idAbono");
                abono[1]=rs.getString("abonosfacturas.idFactura");
                abono[2]=rs.getString("abonosfacturas.fechaAbonoSistema");
                abono[3]=rs.getString("clientes.nombreCliente");
                abono[4]=rs.getString("abonosfacturas.observaciones");
                abono[5]=rs.getString("abonosfacturas.valorAbono");
                abono[6]=rs.getString("abonosfacturas.registradoPor");
            
                listadoAbonosFacturas.add(abono);
            }
            
            cn.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el listado de abonos de facturas\n"+e);
        }
        
        
        return listadoAbonosFacturas;
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
        jTable_Presupuestos = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel_fila = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_NumeroPresup = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_descripcion = new javax.swing.JTextField();
        jButton_cargarRubros = new javax.swing.JButton();
        jButton_agregarPartida = new javax.swing.JButton();
        jButton_editarRubros = new javax.swing.JButton();
        jButton_agregarGastos = new javax.swing.JButton();
        jComboBox_estado = new javax.swing.JComboBox<>();
        jButton_cambiarEstado = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_Presupuestos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Presup.", "Fecha", "Descripcion", "Desde", "Hasta", "Presupuestado", "Estado", "Registrado por"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_Presupuestos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_Presupuestos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_PresupuestosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_Presupuestos);
        if (jTable_Presupuestos.getColumnModel().getColumnCount() > 0) {
            jTable_Presupuestos.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable_Presupuestos.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_Presupuestos.getColumnModel().getColumn(2).setPreferredWidth(250);
            jTable_Presupuestos.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTable_Presupuestos.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTable_Presupuestos.getColumnModel().getColumn(5).setPreferredWidth(110);
            jTable_Presupuestos.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTable_Presupuestos.getColumnModel().getColumn(7).setPreferredWidth(120);
        }

        jLabel_fila.setText("jLabel4");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Presupuesto a Gestionar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Presupuesto");

        jLabel3.setText("Descripcion");

        jButton_cargarRubros.setText("Cargar rubros");
        jButton_cargarRubros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cargarRubrosActionPerformed(evt);
            }
        });

        jButton_agregarPartida.setText("Agregar partida");
        jButton_agregarPartida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregarPartidaActionPerformed(evt);
            }
        });

        jButton_editarRubros.setText("Ver detalle");
        jButton_editarRubros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_editarRubrosActionPerformed(evt);
            }
        });

        jButton_agregarGastos.setText("Agregar gastos");
        jButton_agregarGastos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregarGastosActionPerformed(evt);
            }
        });

        jComboBox_estado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Abierto", "Cerrado", "Pendiente Aut.", "Rechazado" }));

        jButton_cambiarEstado.setText("Cambiar estado");
        jButton_cambiarEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cambiarEstadoActionPerformed(evt);
            }
        });

        jButton1.setText("Informe Economico");
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_NumeroPresup, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox_estado, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton_cambiarEstado)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton_cargarRubros, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_editarRubros, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_agregarGastos, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_agregarPartida, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addGap(140, 140, 140))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_NumeroPresup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox_estado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_cambiarEstado))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_cargarRubros)
                    .addComponent(jButton_agregarGastos)
                    .addComponent(jButton_agregarPartida)
                    .addComponent(jButton_editarRubros)
                    .addComponent(jButton1))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(80, 80, 80)
                        .addComponent(jLabel_fila))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 953, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jLabel_fila)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(141, 141, 141)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_PresupuestosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_PresupuestosMouseClicked
        int fila = jTable_Presupuestos.getSelectedRow();
        if (fila != -1) {
            jTextField_NumeroPresup.setText(jTable_Presupuestos.getValueAt(fila, 0).toString());
            jTextField_descripcion.setText(jTable_Presupuestos.getValueAt(fila, 2).toString());
            jLabel_fila.setText(String.valueOf(fila));
            jComboBox_estado.setSelectedItem(jTable_Presupuestos.getValueAt(fila, 6).toString());

        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una fila");
        }
    }//GEN-LAST:event_jTable_PresupuestosMouseClicked

    private void jButton_cargarRubrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cargarRubrosActionPerformed

        String idPresupuesto = jTextField_NumeroPresup.getText().trim();
        String descripcionPresupuesto=jTextField_descripcion.getText().trim();
        //Verificamos que se haya seleccionado un presupuesto
        if (!idPresupuesto.equals("")) {
            //Verificamos que el presupuesto no tenga ya cargados sus rubros
            //Integer.parseInt(jTable_Presupuestos.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 5).toString()) > 0
            if (Integer.parseInt(ConvertirMonedaAInt(jTable_Presupuestos.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 5).toString())) > 0) {
                JOptionPane.showMessageDialog(this, "El presupuesto seleccionado ya tiene rubros cargados, para "
                        + "editarlos o adicionar rubros vaya a la opcion Editar.\nNota: Solo el Gerente "
                        + "puede editar los presupuestos");

            } else {
                dispose();
                new CargarRubrosPresupuesto(this.usuario, this.permiso, idPresupuesto, descripcionPresupuesto).setVisible(true);

            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un presupuesto");
        }


    }//GEN-LAST:event_jButton_cargarRubrosActionPerformed

    private void jButton_agregarPartidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregarPartidaActionPerformed
        //Verificamos que se haya seleccionado un presupuesto
        String idpresupuesto = jTextField_NumeroPresup.getText().trim();

        if (!idpresupuesto.equals("")) {
            //Verificamos que el presupuesto este abierto
            if (jTable_Presupuestos.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 6).toString().equals("Abierto")) {
                //Capturamos los datos faltantes
                String descripPresupuesto = jTextField_descripcion.getText().trim();
                new AgregarDineroPresupuesto(this.usuario, this.permiso, idpresupuesto, descripPresupuesto).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Solo se pueden agregar partidas a presupuestos cuyo estado sea: Abierto");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione el presupuesto");
        }

    }//GEN-LAST:event_jButton_agregarPartidaActionPerformed

    private void jButton_editarRubrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_editarRubrosActionPerformed
        //Verificamos que se haya seleccionado un presupuesto
        String idpresupuesto = jTextField_NumeroPresup.getText().trim();

        if (!idpresupuesto.equals("")) {
            //Verificamos que el presupuesto este abierto
            //if (jTable_Presupuestos.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 6).toString().equals("Abierto")) {
                //Capturamos los datos faltantes
                String descripPresupuesto = jTextField_descripcion.getText().trim();
                new EdicionRubros(usuario, permiso, idpresupuesto, descripPresupuesto).setVisible(true);
            //} else {
            //    JOptionPane.showMessageDialog(this, "Solo se pueden editar los conceptos de presupuestos cuyo estado sea: Abierto");
            //}

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione el presupuesto");
        }
    }//GEN-LAST:event_jButton_editarRubrosActionPerformed

    private void jButton_cambiarEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cambiarEstadoActionPerformed
        //Verificamos que se haya seleccionado un presupuesto
        String idpresupuesto = jTextField_NumeroPresup.getText().trim();

        if (!idpresupuesto.equals("")) {
            String estado = jComboBox_estado.getSelectedItem().toString();
            CambiarEstadoPresupuesto(idpresupuesto, estado);
            limpiarCampos();
            limpiarTabla(modelo);
            llenarTabla();

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un presupuesto");
        }

    }//GEN-LAST:event_jButton_cambiarEstadoActionPerformed

    private void jButton_agregarGastosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregarGastosActionPerformed
        //Verificamos que se haya seleccionado un presupuesto
        String idPresupuesto = jTextField_NumeroPresup.getText().trim();
        if (!idPresupuesto.equals("")) {
            //Verificamos que el presupuesto este abierto
            if (jTable_Presupuestos.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 6).toString().equals("Abierto")) {
                //Capturamos los datos faltantes
                String presupuesto = jTextField_descripcion.getText().trim();
                new RegistrarGastosPresupuesto(this.usuario, this.permiso, idPresupuesto, presupuesto).setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "Solo se pueden agregar gastos a los presupuestos cuyo estado sea: Abierto");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione el presupuesto");
        }


    }//GEN-LAST:event_jButton_agregarGastosActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //Verificamos que se haya seleccionado un presupuesto
        String idPresupuesto = jTextField_NumeroPresup.getText().trim();

        if (!idPresupuesto.equals("")) {
            String fila = jLabel_fila.getText().trim();
            String descripcionPresupuesto = jTable_Presupuestos.getValueAt(Integer.parseInt(fila), 2).toString();
            String fechaInicial = jTable_Presupuestos.getValueAt(Integer.parseInt(fila), 3).toString();
            String fechaFinal = jTable_Presupuestos.getValueAt(Integer.parseInt(fila), 4).toString();

            GenerarInformeEconomico(idPresupuesto, descripcionPresupuesto, fechaInicial, fechaFinal, this.usuario);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione el presupuesto");
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
            java.util.logging.Logger.getLogger(ListadoPresupuestos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoPresupuestos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoPresupuestos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoPresupuestos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoPresupuestos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_agregarGastos;
    private javax.swing.JButton jButton_agregarPartida;
    private javax.swing.JButton jButton_cambiarEstado;
    private javax.swing.JButton jButton_cargarRubros;
    private javax.swing.JButton jButton_editarRubros;
    private javax.swing.JComboBox<String> jComboBox_estado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel_fila;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Presupuestos;
    private javax.swing.JTextField jTextField_NumeroPresup;
    private javax.swing.JTextField jTextField_descripcion;
    // End of variables declaration//GEN-END:variables
}
