package clases;

import java.awt.Desktop;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;

public class MetodosGenerales {

    public static String ConvertirPrimeraLetraMayus(String texto) {

        String primera = texto.substring(0, 1);
        String restantes = texto.substring(1, texto.length());

        primera = primera.toUpperCase();
        String nuevoTexto = primera + restantes;

        return nuevoTexto;
    }

    public static String ConvertirPrimerasLetrasMayus(String texto) {

        char[] nuevotexto = texto.toCharArray();

        for (int i = 0; i < nuevotexto.length - 1; i++) {

            if (i == 0) {
                nuevotexto[i] = Character.toUpperCase(nuevotexto[i]);
            } else if (nuevotexto[i] == ' ') {
                nuevotexto[i + 1] = Character.toUpperCase(nuevotexto[i + 1]);

            }
        }
        String textoCambiado = "";
        for (int i = 0; i < nuevotexto.length; i++) {
            textoCambiado += nuevotexto[i];
        }
        return textoCambiado;

    }

    public static boolean validacionCriterioRegistroUsuario(String tipoPermiso, String tipoPermisoARegistrar) {

        boolean bandera = false;
        if (tipoPermiso.equals(tipoPermisoARegistrar)) {
            bandera = true;
        }
        return bandera;
    }

    public static ArrayList<Integer> CopiarArchivo(String rutaArchivoACopiar) {

        ArrayList<Integer> nuevo = new ArrayList<Integer>();

        //"D:/Erwin/ApacheNetbeans/GraficasJireh_1/OT.xlsx"
        try {
            FileInputStream OT = new FileInputStream(rutaArchivoACopiar);
            int byteArchivoEntrada = OT.read();

            while (byteArchivoEntrada != -1) {
                nuevo.add(byteArchivoEntrada);
                byteArchivoEntrada = OT.read();
            }

            OT.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return nuevo;
    }

    public static String crearOT(ArrayList<Integer> nuevo, String rutaParaGuardar, String Idventa, String Cliente) {

        String ruta = rutaParaGuardar + "/" + "OT - IdVenta  " + Idventa + " - Cliente " + Cliente + ".xlsx";

        //C:/Users/erwin/Desktop
        try {

            FileOutputStream nuevaOT = new FileOutputStream(ruta);

            for (Integer integer : nuevo) {
                nuevaOT.write(integer);
            }

            nuevaOT.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return ruta;
    }

    public static String crearListadoPendientes(ArrayList<Integer> nuevo, String rutaParaGuardar) {

        String ruta = rutaParaGuardar + "/Listado pendientes.xlsx";

        //C:/Users/erwin/Desktop
        try {

            FileOutputStream Listado = new FileOutputStream(ruta);

            for (Integer integer : nuevo) {
                Listado.write(integer);
            }

            Listado.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return ruta;
    }

    public static String crearReciboAbono(ArrayList<Integer> nuevo, String rutaParaGuardar) {

        String ruta = rutaParaGuardar + "/Recibo.xlsx";

        //C:/Users/erwin/Desktop
        try {

            FileOutputStream Listado = new FileOutputStream(ruta);

            for (Integer integer : nuevo) {
                Listado.write(integer);
            }

            Listado.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return ruta;
    }

    public static String crearFactura(ArrayList<Integer> nuevo, String rutaParaGuardar) {

        String ruta = rutaParaGuardar + "/Factura.xlsx";

        //C:/Users/erwin/Desktop
        try {

            FileOutputStream Listado = new FileOutputStream(ruta);

            for (Integer integer : nuevo) {
                Listado.write(integer);
            }

            Listado.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return ruta;
    }

    //Este metodo abre el archivo excel generado con la informacion de la OT
    public static void abrirArchivo(String rutaDocumento) {
        try {

            //Objeto de tipo File y como argumento la ruta.
            File archivoAAbrir = new File(rutaDocumento);
            boolean estaAbierto = false; //Asumimos de entrada que el archivo esta siendo usado por otro programo

            try {

                FileUtils.touch(archivoAAbrir); //Verificamos si el archivo esta abierto por o no por otro programo
                estaAbierto = true;

            } catch (IOException e) {
                estaAbierto = false;
                JOptionPane.showMessageDialog(null, "El archivo que intentas sobreescribir se encuentra abierto o esta siendo usado por otra aplicacion");
            }

            if (estaAbierto = true) {
                Desktop.getDesktop().open(archivoAAbrir);
            }

        } catch (HeadlessException | IOException e) {
            JOptionPane.showMessageDialog(null, "No se ha podido abrir el archivo. Asegurese que el archivo exista");
        }

    }

    public static void cambiarFecha() {
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat nueva = new SimpleDateFormat("dd-MM-yyyy");
        String strFecha = "2007-12-25";
        Date fecha = null;

        try {

            fecha = formatoDelTexto.parse(strFecha);
            SimpleDateFormat formatear = new SimpleDateFormat("dd-MM-yyyy");
            String fechanueva = formatear.format(fecha);

            System.out.println(fechanueva);

        } catch (ParseException ex) {

            ex.printStackTrace();

        }

    }

    public static long RestarFechas(String primerafecha, String segundafecha) {
        long diferencia = 0;
        try {
            Date primera = new SimpleDateFormat("yyyy-MM-dd").parse(primerafecha);
            Date segunda = new SimpleDateFormat("yyyy-MM-dd").parse(segundafecha);

            long dif = segunda.getTime() - primera.getTime();
            TimeUnit tiempo = TimeUnit.DAYS;
            diferencia = tiempo.convert(dif, TimeUnit.MILLISECONDS);

        } catch (Exception e) {

            e.printStackTrace();
        }
        return diferencia;
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
    
    

    public static void main(String[] args) {

        cambiarFecha();
    }

}
