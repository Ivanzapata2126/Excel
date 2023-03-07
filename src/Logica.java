import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Logica {
    public static void menu(double[] notas,String[] nombres){
        Scanner sn = new Scanner(System.in);
        DecimalFormat df = new DecimalFormat("#.##");
        List<Double> notasOrdenadas = Estudiante.listaOrdenada(notas);
        List<String> nombresOrdenados = Estudiante.nombresOrdenados(notasOrdenadas,notas,nombres);
        boolean salir = false;
        int option; //Guardaremos la opción del usuario

        while (!salir) {
            System.out.print("");
            System.out.println("-------------------- Menu ------------------- ");
            System.out.println("1.Ver nota definitiva de cada estudiante");
            System.out.println("2.Ver cantidad de estudiantes que aprobaron");
            System.out.println("3.Ver cantidad de estudiantes que no aprobaron");
            System.out.println("4.Ver promedio general de las notas definitivas");
            System.out.println("5.Ver lista de estudiantes que obtuvieron 5");
            System.out.println("6.Ver la desviación estándar");
            System.out.println("7.Ver lista ordenada por calificación final");
            System.out.println("8.Ver calificación máxima y mínima de cada nota");
            System.out.println("9.Ver el tiempo total de procesamiento, esto implicaría detener el programa");
            System.out.println("10.Exportar todo a un archivo EXCEL");
            System.out.println("11.Salir");
            //Se hace dentro de un try-Catch para recibir si la persona ingresó un valor distinto
            try {
                boolean flag = true;
                System.out.print("Ingrese una opción: ");
                option = sn.nextInt();
                //Switch case, para facilitar la election de la persona, recibe la elección,
                // tomada anteriormente en el menú y las notas, para pasarlas a los
                // métodos mencionados anteriormente
                switch (option) {
                    case 1:
                        Estudiante.notaDefinitivaEstudiante(notas,nombres);
                        break;
                    case 2:
                        System.out.printf("La cantidad de estudiantes que aprobaron fue de: "+Estudiante.cantidadAprobaron(notas,flag) + "\n");
                        break;
                    case 3:
                        System.out.printf("La cantidad de estudiantes que desaprobaron fue de: "+Estudiante.cantidadAprobaron(notas,!flag) + "\n");
                        break;
                    case 4:
                        //Formateamos la salida con DecimalFormat, una clase propia de Java, solo se verán 2 decimales después de la coma.
                        System.out.print("El promedio de notas es de: " + df.format(Estudiante.promedio(notas)) + "\n");
                        break;
                    case 5:
                        Estudiante.mostrarSacaronCinco(notas,nombres);
                        break;
                    case 6:
                        System.out.println("La desviación estándar es de: " + Estudiante.desviacionEstandar(notas));
                        break;
                    case 7:
                        Estudiante.mostrarListaOrdenada(notasOrdenadas,nombresOrdenados);
                        break;
                    case 8:
                        Estudiante.calificacionMinYMax();
                        break;
                    case 9:
                        salir=true;
                        Archivo.error = false;
                        break;
                    case 10:
                        Archivo.escribirArchivo();
                        System.out.print("¡Hasta la próxima!");
                        salir = true;
                        Archivo.error = false;
                        break;
                    case 11:
                        System.out.print("¡Hasta la próxima!");
                        Archivo.error = false;
                        salir = true;
                        break;
                    default:
                        System.out.println("Solo números entre 1 y 11");
                }
            } catch (InputMismatchException e) {
                System.out.println("Debes ingresar un número");
                sn.next();
            }
        }
    }

    public static void main(String[] args) {
        //tomamos el tiempo de inicio, gracias a System.nanoTime
        long tiempoInicio = System.nanoTime();
        Scanner sn = new Scanner(System.in);
        int option;
        System.out.println(" ----------Bienvenido----------");
        try {
            System.out.println("1.Escribir manualmente la ruta donde se encuentra el archivo EXCEL");
            System.out.println("2.Abrir interfaz y seleccionar archivo EXCEL");
            System.out.println("Presione cualquier otra tecla para salir: ");
            option = sn.nextInt();
            if (option == 1) {
                Archivo.leerArchivoConsola();
            }
            if (option == 2) {
                Archivo.leerArchivoInterfaz();
            } else {
                System.out.println("Hasta la próxima");
            }
        } catch (InputMismatchException ex) {
        }
        //tomamos el tiempo de finalización, gracias a System.nanoTime
        long tiempoFinal = System.nanoTime();
        long acumuladorTiempo = 0;
        //Restamos ambos tiempos para obtener el tiempo gastado
        acumuladorTiempo += tiempoFinal-tiempoInicio;
        System.out.println("El tiempo total de ejecución fue de: " + acumuladorTiempo + " nanosegundos");
    }
}
