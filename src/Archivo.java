import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.PresetColor;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Archivo  {
    public static boolean entro = false;
    public static boolean error = false;
    public static void leerArchivoInterfaz(){
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel", "xlsx");
        JFileChooser fc = new JFileChooser();
        Scanner sn = new Scanner(System.in);
        fc.setFileFilter(filter);
        // Mostrar la ventana para abrir archivo y recoger la respuesta
        // En el parámetro del showOpenDialog se indica la ventana
        // al que estará asociado. Con el valor this se asocia a la
        // ventana que la abre.
        fc.setDialogTitle("Open file");
        //Comprobar si se ha pulsado Aceptar
        System.out.println("Elija el archivo EXCEL");
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            //Crear un objeto File con el archivo elegido.
            File archivoElegido = fc.getSelectedFile();
            entro = true;
            abrirArchivo(archivoElegido.getAbsolutePath());
            //Mostrar el nombre del archivo en un campo de text
        }else{
            try {
                System.out.println("Para poder usar el programa, debe seleccionar un archivo EXCEL");
                System.out.println("¿Desea hacer el proceso de nuevo? pulse s o S para confirmar. Cualquier otra tecla para salir: ");
                String op = sn.nextLine();
                if (op.equals("s") || op.equals("S")) {
                    Archivo.leerArchivoInterfaz();
                } else {
                    System.out.println("Hasta pronto!!");
                }
            } catch (InputMismatchException ex) {
                System.out.println("Hasta pronto!!");
            }
        }
    }

    public static void leerArchivoConsola(){
        Scanner sn = new Scanner(System.in);
        boolean salir;
        do {
            try {
                salir = false;
                System.out.println("Ingrese el nombre exacto del archivo. NO INGRESE LA EXTENSION, el programa la ingresará por usted : ");
                String nombre = sn.nextLine();
                System.out.println("Ingrese la ruta exacta donde se encuentra el archivo EXCEL. Eje: C:\\Users\\USUARIO\\Desktop: ");
                String ruta = sn.nextLine();
                String rutaArchivo = ruta + "\\" + nombre + ".xlsx";
                abrirArchivo(rutaArchivo);
                while (error) {
                    try {
                        System.out.println("No se ha encontrado el archivo. Por favor revise el nombre y la ruta ingresada");
                        System.out.println("¿Desea hacer el proceso de nuevo? pulse s o S para confirmar. Cualquier otra tecla para salir: ");
                        String op = sn.nextLine();
                        if (op.equals("s") || op.equals("S")) {
                            leerArchivoConsola();
                        } else {
                            System.out.println("Hasta pronto!!");
                            error = false;
                            return;
                        }
                    } catch (InputMismatchException ex) {
                        System.out.println("Hasta pronto!!");
                        error = false;
                        return;
                    }
                }
            } catch (InputMismatchException ex) {
                System.out.println("Formato ingresado no valido");
                salir = true;
            }
        }while(salir);
    }
    public static void abrirArchivo(String rutaArchivo){
        try (FileInputStream file = new FileInputStream(rutaArchivo)) {
            // leer archivo excel
            XSSFWorkbook worbook = new XSSFWorkbook(file);
            Estudiante.informacion = convertirAMatriz(worbook);
            String[] nombres;
            double[] parciales,talleres,quices,definitivas;
            nombres = Estudiante.obtenerNombres(Estudiante.informacion);
            Estudiante.inconsistenciasParciales = Estudiante.obtenerInconsistencias(Estudiante.informacion,2);
            Estudiante.inconsistenciasTalleres = Estudiante.obtenerInconsistencias(Estudiante.informacion,6);
            Estudiante.inconsistenciasQuices = Estudiante.obtenerInconsistencias(Estudiante.informacion,3);
            Estudiante.nombres = nombres;
            parciales = Estudiante.obtenerParciales(Estudiante.informacion);
            talleres = Estudiante.obtenerTalleres(Estudiante.informacion);
            quices = Estudiante.obtenerQuices(Estudiante.informacion);
            definitivas = Estudiante.calcularNota(parciales,quices,talleres);
            Estudiante.inicializarNotas(parciales,talleres,quices);
            Estudiante.definitivas = definitivas;
            Logica.menu(definitivas,nombres);
        } catch (Exception e) {
            error = true;
            e.getMessage();
        }
    }

    public static void escribirArchivo(){
        String nombreArchivo="calificacion.xlsx";
        String directorioActual = System.getProperty("user.dir");
        String rutaArchivo= directorioActual+"\\"+nombreArchivo;
        String hoja="NotasDefinitivas";
        String hoja2 = "ListaOrdenada";
        String hoja3 = "Inconsistencias";
        String hoja4 = "Histograma";
        String hoja5 = "Diagrama tipo pastel";
        DecimalFormat df = new DecimalFormat("#.##");
        String[] nombres = Estudiante.nombres;
        double[] notas = Estudiante.definitivas;
        String[] nombresOrdenadosArray = new String[nombres.length];
        List<String> nombresOrdenados;
        XSSFWorkbook libro= new XSSFWorkbook();
        XSSFSheet hoja1 = libro.createSheet(hoja);
        XSSFSheet listaOrdenadaHoja = libro.createSheet(hoja2);
        XSSFSheet inconsistenciasHoja = libro.createSheet(hoja3);
        XSSFSheet histogramaHoja = libro.createSheet(hoja4);
        XSSFSheet diagramaHoja = libro.createSheet(hoja5);
        listaOrdenada(listaOrdenadaHoja,libro,notas,nombres);
        inconsistencias(inconsistenciasHoja,libro,nombres);
        histograma(histogramaHoja,notas);
        diagramaPastel(diagramaHoja,notas);
        //cabecera de la hoja de excel
        String [] header= new String[]{"Nombres","Notas finales","Mensaje","Cantidad de aprobados","Cantidad de no aprobados","Promedio general","Desviación Estándar",
                "Listado de estudiantes que sacaron 5.0","Calificaciones máximas y mínimas de cada nota","Listado Ordenado","","","","","","","","",""};
        String[][] datos = new String[notas.length][nombres.length];
        hoja1.addMergedRegion(new CellRangeAddress(0,0,8,10));
        nombresOrdenados = Estudiante.obtuvieronCinco(notas,nombres);
        for(int i= 0;i<nombresOrdenados.size();i++){
            nombresOrdenadosArray[i] = nombresOrdenados.get(i);
        }


        for(int i = 0; i<notas.length;i++){
            datos[i][1] = String.valueOf(df.format(notas[i]));
            datos[i][0] = nombres[i];
            datos[i][2] = (notas[i] > 3.0) ? "Aprobada":" Desaprobada";
            datos[i][7] = nombresOrdenadosArray[i];
        }
        int nroAprobaron = Estudiante.cantidadAprobaron(notas,true);
        int nroNoAprobaron = Estudiante.cantidadAprobaron(notas,false);
        double promedio = Estudiante.promedio(notas);

        datos[0][3] = String.valueOf(nroAprobaron);
        datos[0][4] = String.valueOf(nroNoAprobaron);
        datos[0][5] = String.valueOf(df.format(promedio));
        datos[0][6] = Estudiante.desviacionEstandar(notas);
        datos[0][8] = "Parciales";
        datos[1][8] = String.valueOf(df.format(Estudiante.notaParcialMayor));
        datos[2][8] = String.valueOf(df.format(Estudiante.notaParcialMenor));
        datos[0][9] = "Quices";
        datos[1][9] = String.valueOf(df.format(Estudiante.notaQuizMayor));
        datos[2][9] = String.valueOf(df.format(Estudiante.notaQuizMenor));
        datos[0][10] = "Talleres";
        datos[1][10] = String.valueOf(df.format(Estudiante.notaTallerMayor));
        datos[2][10] = String.valueOf(df.format(Estudiante.notaTallerMenor));

        recorrerYGuardarEnElArchivo(hoja1,libro,datos,notas.length,header);

        File file;
        file = new File(rutaArchivo);
        try (FileOutputStream fileOuS = new FileOutputStream(file)){
            if (file.exists()) {// si el archivo existe se elimina
                file.delete();
                System.out.println("Archivo eliminado");
            }
            libro.write(fileOuS);
            fileOuS.flush();
            fileOuS.close();
            System.out.println("Archivo Creado en la ruta: " + directorioActual);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void listaOrdenada(XSSFSheet hoja2,XSSFWorkbook libro,double[] notas,String[] nombres){
        DecimalFormat df = new DecimalFormat("#.##");
        int contador = 0;
        //cabecera de la hoja de excel
        String [] header= new String[]{"Nombres", "Notas finales"};
        List<Double> listaOrdenada;
        List<String> nombresOrdenados;
        String[][] datos = new String[notas.length][nombres.length];
        listaOrdenada = Estudiante.listaOrdenada(notas);
        nombresOrdenados = Estudiante.nombresOrdenados(listaOrdenada,notas,nombres);

        for(int i = 0;i<notas.length;i++){
            contador++;
            datos[i][1] = df.format(listaOrdenada.get(i));
            datos[i][0] = contador + "." + nombresOrdenados.get(i);
        }
        recorrerYGuardarEnElArchivo(hoja2,libro,datos,notas.length,header);
    }

    public static void inconsistencias(XSSFSheet inconsistenciasHoja,XSSFWorkbook libro,String[] nombres){
        int contador = 0;
        //cabecera de la hoja de excel
        String [] header= new String[]{"Nombres", "Inconsistencias encontradas en parciales","Acciones tomadas por el programa","inconsistencias encontradas en quices",
                "Acciones tomadas por el programa","inconsistencias encontradas en talleres","Acciones tomadas por el programa"};
        String[][] datos = new String[nombres.length][nombres.length];

        for(int i = 0;i<nombres.length;i++){
            contador++;
            datos[i][0] = contador + "." + nombres[i];
            datos[i][1] = Estudiante.inconsistenciasParciales.get(i);
            datos[i][2] = Estudiante.obtenerAcciones(Estudiante.inconsistenciasParciales.get(i));
            datos[i][3] = Estudiante.inconsistenciasQuices.get(i);
            datos[i][4] = Estudiante.obtenerAcciones(Estudiante.inconsistenciasQuices.get(i));
            datos[i][5] = Estudiante.inconsistenciasTalleres.get(i);
            datos[i][6] = Estudiante.obtenerAcciones(Estudiante.inconsistenciasTalleres.get(i));
        }
        recorrerYGuardarEnElArchivo(inconsistenciasHoja,libro,datos,nombres.length,header);
    }

    public static void diagramaPastel(XSSFSheet diagramaHoja,double[] notas){
        int aprobaron=0,noAprobaron = 0;
        for(double nota:notas){
            if(nota>3.0){
                aprobaron++;
            }else{
                noAprobaron++;
            }
        }
        // Create row and put some cells in it. Rows and cells are 0 based.
        Row row = diagramaHoja.createRow((short) 0);

        Cell cell = row.createCell((short) 0);
        cell.setCellValue("Aprobaron");

        cell = row.createCell((short) 1);
        cell.setCellValue("No aprobaron");

        row = diagramaHoja.createRow((short) 1);

        cell = row.createCell((short) 0);
        cell.setCellValue(aprobaron);

        cell = row.createCell((short) 1);
        cell.setCellValue(noAprobaron);


        XSSFDrawing drawing = diagramaHoja.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 4, 7, 20);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Proporciones de aprobados y no aprobados");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFDataSource<String> text = XDDFDataSourcesFactory.fromStringCellRange(diagramaHoja,
                new CellRangeAddress(0, 0, 0, 1));

        XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(diagramaHoja,
                new CellRangeAddress(1, 1, 0, 1));

        XDDFChartData data = chart.createData(ChartTypes.PIE3D, null, null);// chart.createData(ChartTypes.PIE,
        // null, null);
        data.setVaryColors(true);
        data.addSeries(text, values);
        chart.plot(data);
    }

    public static void histograma(XSSFSheet HistogramaHoja,double[] notas){
        int intervalo1 = 0;
        int intervalo2 = 0;
        int intervalo3 = 0;
        int intervalo4 = 0;
        int intervalo5 = 0;
        for(double nota:notas){
            if(nota>=0.0 && nota<1.0) {
                intervalo1++;
            }
            if(nota>=1.0 && nota<2.0){
                intervalo2++;
            }
            if(nota>=2.0 && nota<3.0){
                intervalo3++;
            }
            if(nota>=3.0 && nota<4.0){
                intervalo4++;
            }
            if(nota>=4.0 && nota<=5.0) {
                intervalo5++;
            }
        }
        String datos[] = new String[] {"[0,0-1,0)","[1,0-2,0)","[2,0-3,0)","[3,0-4,0)","[4,0-5,0]"};
        int[] cantidades = new int[]{intervalo1,intervalo2,intervalo3,intervalo4,intervalo5};
        // Create row and put some cells in it. Rows and cells are 0 based.

        Row row = HistogramaHoja.createRow((short) 0);

        Cell cell = row.createCell((short) 0);
        cell.setCellValue("Intervalos");

        cell = row.createCell((short) 1);
        cell.setCellValue("Cantidades");

        row = HistogramaHoja.createRow((short) 1);

        cell = row.createCell((short) 0);
        cell.setCellValue(datos[0]);

        cell = row.createCell((short) 1);
        cell.setCellValue(cantidades[0]);

        row = HistogramaHoja.createRow((short) 2);

        cell = row.createCell((short) 0);
        cell.setCellValue(datos[1]);

        cell = row.createCell((short) 1);
        cell.setCellValue(cantidades[1]);

        row = HistogramaHoja.createRow((short) 3);

        cell = row.createCell((short) 0);
        cell.setCellValue(datos[2]);

        cell = row.createCell((short) 1);
        cell.setCellValue(cantidades[2]);

        row = HistogramaHoja.createRow((short) 4);

        cell = row.createCell((short) 0);
        cell.setCellValue(datos[3]);

        cell = row.createCell((short) 1);
        cell.setCellValue(cantidades[3]);

        row = HistogramaHoja.createRow((short) 5);

        cell = row.createCell((short) 0);
        cell.setCellValue(datos[4]);

        cell = row.createCell((short) 1);
        cell.setCellValue(cantidades[4]);




        XSSFDrawing drawing = HistogramaHoja.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Histograma de las notas finales");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Notas finales");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Cantidades");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFDataSource<Double> xs = XDDFDataSourcesFactory.fromNumericCellRange(HistogramaHoja, new CellRangeAddress(1, 5, 0, 0));
        XDDFNumericalDataSource<Double> ys1 = XDDFDataSourcesFactory.fromNumericCellRange(HistogramaHoja, new CellRangeAddress(1, 5, 1, 1));


        XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series1 = data.addSeries(xs, ys1);
        series1.setTitle(datos[0], null);
        chart.plot(data);

        XDDFBarChartData bar = (XDDFBarChartData) data;
        bar.setBarDirection(BarDirection.COL);
        solidFillSeries(data, 0, PresetColor.TURQUOISE);

    }
    public static void solidFillSeries(XDDFChartData data, int index, PresetColor color) {
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(color));
        XDDFChartData.Series series = data.getSeries(index);
        XDDFShapeProperties properties = series.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setFillProperties(fill);
        series.setShapeProperties(properties);
    }


    public static String[][] convertirAMatriz(XSSFWorkbook workbook){
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> col = sheet.rowIterator();
        String[][] resultado = new String[obtenerNroFilas(sheet)-1][obtenerNroColumnas(col.next())-1 ];
        int i=0,j=0;
        while (col.hasNext()){
            Row row = col.next();
            Iterator<Cell> celdas = row.cellIterator();
            while(celdas.hasNext()){
                Cell celda = celdas.next();
                DataFormatter dataFormatter = new DataFormatter();
                String valor = dataFormatter.formatCellValue(celda);
                if (celda == null || celda.getCellType() == CellType.BLANK){
                    resultado[i][j] = "0.0";
                }else{
                    resultado[i][j] = valor;
                }
                j++;
            }
            j=0;
            i++;
        }
        return resultado;
    }
    public static int obtenerNroFilas(XSSFSheet sheet){
        Iterator<Row> row = sheet.rowIterator();
        int cont = 0;
        while(row.hasNext()){
            row.next();
            cont++;
        }
        return cont;
    }
    public static int obtenerNroColumnas(Row row){
        Iterator<Cell> celdas = row.cellIterator();
        int cont = 0;
        while(celdas.hasNext()){
            celdas.next();
            cont++;
        }
        return cont;
    }
    public static void recorrerYGuardarEnElArchivo(XSSFSheet hoja,XSSFWorkbook libro,String[][] datos,int tamano,String[] header){
        //poner negrita a la cabecera
        CellStyle style = libro.createCellStyle();
        Font font = libro.createFont();
        font.setBold(true);
        style.setFont(font);

        //generar los datos para el documento
        for (int i = 0; i <= tamano; i++) {
            XSSFRow row=hoja.createRow(i);//se crea las filas
            for (int j = 0; j < header.length; j++) {
                XSSFCell cell= row.createCell(j);//se crea las celdas para la cabecera, junto con la posición
                //se crea las celdas para el contenido, junto con la posición
                if (i==0) {//para la cabecera
                    cell.setCellStyle(style); // se añade el style crea anteriormente
                    cell.setCellValue(header[j]);//se añade el contenido
                }else{//para el contenido
                    cell.setCellValue(datos[i-1][j]); //se añade el contenido
                }
            }
        }
    }

}
