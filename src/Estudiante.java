import java.text.DecimalFormat;
import java.util.*;

public class Estudiante {
    public static String[][] informacion;
    public static String[] nombres;
    public static List<String> inconsistenciasParciales;
    public static List<String> inconsistenciasQuices;
    public static List<String> inconsistenciasTalleres;
    public static double[] definitivas;
    public static double notaTallerMayor;
    public static double notaTallerMenor;
    public static double notaQuizMayor;
    public static double notaQuizMenor;
    public static double notaParcialMayor;
    public static double notaParcialMenor;

    public static String[] obtenerNombres(String[][] datos){
        String[] nombres = new String[datos.length];
        for(int i = 0; i<datos.length;i++){
            nombres[i] = datos[i][1];
        }
        return nombres;
    }
    public static double[] obtenerParciales(String[][] datos){
        double[] parciales = new double[datos.length];
        for(int i = 0; i<datos.length;i++){
            parciales[i] = validarNotaString(datos[i][2]);
        }
        return parciales;
    }
    public static double[] obtenerQuices(String[][] datos){
        double[] quices = new double[datos.length];
        for(int i = 0; i<datos.length;i++){
            quices[i] = promedioQuices(validarNotaString(datos[i][3]), validarNotaString(datos[i][4]), validarNotaString(datos[i][5]));
        }
        return quices;
    }
    public static double[] obtenerTalleres(String[][] datos){
        double[] talleres = new double[datos.length];
        for(int i = 0; i<datos.length;i++){
            talleres[i] = promedioTalleres(validarNotaString(datos[i][6]), validarNotaString(datos[i][7]));
        }
        return talleres;
    }

    public static double promedioTalleres(double nota1,double nota2){
        return (nota1+nota2)/2;
    }
    public static double promedioQuices(double nota1,double nota2,double nota3){
        return (nota1+nota2+nota3)/3;
    }


    public static List<String> obtenerInconsistencias(String[][] notas, int pos){
        double notaDouble;
        List<String> inconsistencias = new ArrayList<>();
        for (String[] nota : notas) {
            try {
                if (nota[pos].contains(",")) {
                    inconsistencias.add("Se encontraron notas con ','");
                    continue;
                }
                notaDouble = Double.parseDouble(nota[pos].replace(",", "."));
            } catch (NumberFormatException ex) {
                inconsistencias.add("Se encontraron caracteres distintos a números");
                continue;
            }
            if (notaDouble > 5.0 || notaDouble < 0.0) {
                inconsistencias.add("Se encontraron notas mayores a 5.0 o menores a 0.0");
            } else {
                inconsistencias.add("No se encontraron inconsistencias");
            }
        }
        return inconsistencias;
    }

    public static String obtenerAcciones(String inconsistencias){
        if(inconsistencias.equals("No se encontraron inconsistencias")){
            return "No se encontraron inconsistencias";
        }else{
            return "La celda fue rellenada con 0.0";
        }
    }
    public static double validarNotaString(String nota) {
        double notaDouble;
        try {
            nota = nota.trim();
            notaDouble = Double.parseDouble(nota.replace(",", "."));
        } catch (NumberFormatException ex) {
            notaDouble = 0.0;
        }
        if(notaDouble > 5.0 || notaDouble < 0.0){
            return 0.0;
        }else {
            return notaDouble;
        }
    }


    //Método que retorna las notas menores y mayores de un double[], el boolean flag, es para saber que se
    // va a devolver, si la nota menor o la nota mayor, esto con el fin de no hacer dos métodos.
    public static double devolverMayorYMenor(double[] notas,boolean flag){
        double mayor = notas[0];
        double menor = notas[0];
        for (double nota : notas) {
            if (nota > mayor) {
                mayor = nota;
            }
            if (nota < menor) {
                menor = nota;
            }
        }
        if(flag){
            return mayor;
        }else{
            return menor;
        }
    }
    //Método que calcula la nota final, pasándole 3 notas y retorna esa nota final.
    public static double[] calcularNota(double[] parciales,double[] quices, double[] talleres){
        double[] definitivas = new double[parciales.length];
        for(int i=0; i< parciales.length;i++){
            definitivas[i] = (parciales[i]*0.50)+(quices[i]*0.30)+(talleres[i]*0.20);
        }
        return definitivas;
    }

    //Método que recorre las notas y devuelve una lista con las notas de cada estudiante
    public static void notaDefinitivaEstudiante(double[] notas,String[] nombres){
        System.out.println("----- Nota definitiva de cada estudiante -----");
        for (int i=0;i<notas.length;i++){
            DecimalFormat df = new DecimalFormat("#.##");
            //operador terciario, si la condition da true, se añadiría al mensaje aprobado, si es falso, desaprobado
            System.out.println((i+1)+"."+nombres[i]+": "+df.format(notas[i]) + ((notas[i] > 3.0) ? " Aprobada":" Desaprobada"));
        }
    }
    //Método que recorre las notas y mediante dos variables contadoras, cuenta la cantidad de estudiantes
    // aprobados y desaprobados, para no hacer dos métodos casi iguales, le pasé un segundo argumento como guia
    // si es true va a devolver la cantidad de aprobados, si es falso, los desaprobados
    public static int cantidadAprobaron(double[] notas,boolean flag){
        int aprobaron = 0;
        int desaprobaron = 0;
        for (double nota : notas) {
            if (nota < 3.0) {
                desaprobaron++;
            } else {
                aprobaron++;
            }
        }
        if(flag){
            return aprobaron;
        }else{
            return desaprobaron;
        }
    }
    //método que recorre las notas y retorna el promedio de estas
    public static double promedio(double[] notas){
        //acumulador
        float total = 0;
        for (double nota : notas) {
            total += nota;
        }
        //método.length que devuelve la longitud del arreglo notas
        return total/notas.length;
    }
    //Método que recorre las notas, y mediante un if válida que sea igual a cinco, si es así imprime al estudiante
    //que obtuvo esa nota
    public static List<String> obtuvieronCinco(double[] notas,String[] nombres){
        List<String> nombresOrdenados = new ArrayList<>();
        for (int i=0;i<notas.length;i++){
            if(notas[i] == 5){
                nombresOrdenados.add(nombres[i]);
            }
        }
        return nombresOrdenados;
    }
    public static void mostrarSacaronCinco(double[] notas,String[] nombres){
        List<String> sacaronCinco = obtuvieronCinco(notas,nombres);
        int contador = 0;
        System.out.println("---- Listado de estudiantes que sacaron 5.0 ----");
        for(String dato:sacaronCinco){
            //Contador para tener un conteo de la cantidad de estudiantes que sacaron 5
            contador++;
            System.out.printf(contador+"."+dato+ "\n");
        }
        //Sí ningún estudiante obtuvo 5, le mostrará el mensaje.
        if(contador == 0){
            System.out.print("Ningún estudiante obtuvo 5 \n");
        }
    }

    //Método que recibe el array de notas finales y devuelve la desviación estándar
    public static String desviacionEstandar(double[] notas){
        //Convertimos la media en flotante ej. 3.3
        float media = (float) promedio(notas);
        double desviacion = 0.0;
        for (double nota : notas) {
            desviacion += Math.pow((nota - media), 2);
        }
        double sq = desviacion/notas.length;
        //le sacamos raíz cuadrada a la division de la sumatoria anterior entre la población
        //que sería la cantidad de notas en el array, todo esto usando la librería Match de Java
        double desviacionEstandar = Math.sqrt(sq);
        //Formateamos la salida con DecimalFormat, una clase propia de Java, solo se verán 4 decimales después de la coma.
        DecimalFormat df = new DecimalFormat("#.####");
        return df.format(desviacionEstandar);
    }
    //Método que recibe el array de notas, y lo ordena de forma descendente.
    public static List<Double> listaOrdenada(double[] notas){
        //Se instancia una lista, ya que las listas de Java ofrecen ordenamientos sencillos
        List<Double> listaOrdenada = new ArrayList<>();
        //Recorremos el array y agregamos cada valor a la lista.
        for (double nota : notas) {
            listaOrdenada.add(nota);
        }
        //Ya con la lista llena con los valores, es más fácil ordenarla, Java ofrece el método .sort,
        //pero ese método las ordena de forma ascendente, por eso se pasa como parámetro Collections.reverserOrder()
        listaOrdenada.sort(Collections.reverseOrder());
        //Ya con la lista ordenada, solo es recorrerla junto con el array de notas, para poder saber las posiciones
        return listaOrdenada;
    }
    public static List<String> nombresOrdenados(List<Double> listaOrdenada,double[] notas, String[] nombres){
        List<String> nombresOrdenados = new ArrayList<>();
        for (double nota:listaOrdenada){
            for(int i = 0;i<notas.length;i++){
                if(nota == notas[i] && !nombresOrdenados.contains(nombres[i])){
                    nombresOrdenados.add(nombres[i]);
                }
            }
        }
        return nombresOrdenados;
    }
    public static void mostrarListaOrdenada(List<Double> notasOrdenadas,List<String> nombresOrdenados){
        int contador = 0;
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("----- Listado ordenado por calificacion final  -----");
        for(int i = 0;i<notasOrdenadas.size();i++){
            contador++;
            System.out.println((contador) + "." + nombresOrdenados.get(i) + "  NOTA: "+df.format(notasOrdenadas.get(i)));
        }
    }
    //Usando el método devolverMayorYMenor, guardamos en una variable el resultado de este método, y luego
    //solo lo imprimimos
    public static void parcialMayor(double[] parciales){
        notaParcialMayor = devolverMayorYMenor(parciales,true);
    }
    public static void parcialMenor(double[] parciales){
        notaParcialMenor = devolverMayorYMenor(parciales,false);
    }
    public static void tallerMayor(double[] talleres){
        notaTallerMayor = devolverMayorYMenor(talleres,true);
    }
    public static void tallerMenor(double[] talleres){
        notaTallerMenor =  devolverMayorYMenor(talleres,false);
    }
    public static void quizMayor(double[] quices){
        notaQuizMayor =  devolverMayorYMenor(quices,true);
    }
    public static void quizMenor(double[] quices){
        notaQuizMenor =  devolverMayorYMenor(quices,false);
    }
    public static void calificacionMinYMax(){
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("Nota mayor de los Parciales: " + df.format(notaParcialMayor) + " Nota menor: " + df.format(notaParcialMenor));
        System.out.println("Nota mayor de los talleres: " + df.format(notaTallerMayor) + " Nota menor: " + df.format(notaTallerMenor));
        System.out.println("Nota mayor de los Quices: " + df.format(notaQuizMayor) + " Nota menor: " + df.format(notaQuizMenor));
    }
    public static void inicializarNotas(double[] parciales,double[] talleres,double[] quices){
        parcialMayor(parciales);
        parcialMenor(parciales);
        tallerMayor(talleres);
        tallerMenor(talleres);
        quizMayor(quices);
        quizMenor(quices);
    }
}
