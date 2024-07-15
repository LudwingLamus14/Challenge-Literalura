package com.alura.literalura.principal;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.LibrosRepository;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvertirDatos;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner scan = new Scanner(System.in);
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvertirDatos convierteDato = new ConvertirDatos();
    private LibrosRepository repositorio;

    public Principal (LibrosRepository repository){
        this.repositorio=repository;
    }

    public void mostrarMenu(){

        var opcion = -1;
        while (opcion != 0){

            var menu = """
                    ****************************************************
                    Bienvenido a la API de literalura, elija una opción:
                    ****************************************************
                    1- Buscar libro en Web por título
                    2- Listar libros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos en determinado año
                    5- Listar libros por idioma
                    0- Salir
                    ****************************************************
                    """;
            System.out.println(menu);
            opcion = scan.nextInt();
            scan.nextLine();

            switch (opcion){
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnUnDeterminadoAno();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    //Buscar libro en la API
    private DatosLibro obtenerLibroAPI(){
        System.out.println("Ingrese el nombre del libro que desea buscar:");
        var tituloLibro = scan.nextLine();
        var url = URL_BASE + "?search=" + tituloLibro.replace(" ", "+");

        // Llamada a la API para obtener los datos
        var json = consumoAPI.obtenerDatosApI(url);
        var datosBusqueda = convierteDato.obtenerDatos(json, Datos.class);

        // Filtrar el resultado por el título del libro
        Optional<DatosLibro> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        // Mostrar resultado
        if (libroBuscado.isPresent()) {
            System.out.println("Libro encontrado:");
            return libroBuscado.get();
        } else {
            System.out.println("Libro no encontrado, intente con otro título.\n");
            return null;
        }
    }

    //Buscar libro por titulo
    private void buscarLibroPorTitulo(){
        DatosLibro datosLibro = obtenerLibroAPI();

        if (datosLibro != null) {
            Libro libro = new Libro(datosLibro);
            List<Autor> autores = new ArrayList<>();

            for (DatosAutor datosAutor : datosLibro.autor()) {
                Autor autor = new Autor(datosAutor);
                autor.setLibros(libro);
                autores.add(autor);
            }

            libro.setAutor(autores);

            try {
                repositorio.save(libro);
                System.out.println(libro.getTitulo() + " Libro guardado exitosamente");
            } catch (DataIntegrityViolationException e) {
                System.out.println("Libro ya registrado en la base de datos, intente con otro.\n");
            }
        } else {
            System.out.println("No se encontraron datos del libro.\n");
        }
    }

    //Mostar libros registrados
    private void listarLibrosRegistrados() {
        List<Libro> mostrarListaLibros = repositorio.findAll();
        mostrarListaLibros.forEach(l -> System.out.println(
                "+++++++++ LIBRO +++++++++" +
                        "\nTítulo: " + l.getTitulo()+
                        "\nIdioma: " + l.getIdiomas()+
                        "\nAutor: " + l.getAutor().stream().map(Autor::getNombre).collect(Collectors.joining()) +
                        "\nNúmero de descargas: " + l.getNumeroDeDescargas() +
                        "\n"
        ));
    }

    //Mostrar autores registrados
    private void listarAutoresRegistrados(){
        List<Autor> listaAutores = repositorio.mostrarAutores();

        Map<String, List<String>> autoresYLibros = listaAutores.stream()
                .collect(Collectors.groupingBy(
                        Autor::getNombre,
                        Collectors.mapping(a -> a.getLibros().getTitulo(), Collectors.toList())
                ));

        for (Map.Entry<String, List<String>> entry : autoresYLibros.entrySet()) {
            String nombreAutor = entry.getKey();
            List<String> librosAutor = entry.getValue();

            Autor autor = listaAutores.stream()
                    .filter(a -> a.getNombre().equals(nombreAutor))
                    .findFirst()
                    .orElse(null);

            if (autor != null) {
                StringBuilder detallesAutor = new StringBuilder();
                detallesAutor.append("+++++++++ AUTOR +++++++++")
                        .append("\nNombre: ").append(nombreAutor)
                        .append("\nFecha de nacimiento: ").append(autor.getFechaDeNacimiento())
                        .append("\nFecha de muerte: ").append(autor.getFechaDeMuerte())
                        .append("\nLibros: ").append(String.join(", ", librosAutor))
                        .append("\n");

                System.out.println(detallesAutor.toString());
            }
        }
    }

    // Mostrar autores vivos en un determinado año
    private void listarAutoresVivosEnUnDeterminadoAno() {
        System.out.println("Ingresa el año a consultar:");
        String anio = scan.nextLine();

        List<Autor> autoresVivos = repositorio.mostrarAutoresVivos(anio);

        if (autoresVivos.isEmpty()) {
            System.out.println("Sin autores vivos en el año indicado...\n");
            return;
        }

        Map<String, List<String>> autoresYLibros = autoresVivos.stream()
                .collect(Collectors.groupingBy(
                        Autor::getNombre,
                        Collectors.mapping(a -> a.getLibros().getTitulo(), Collectors.toList())
                ));

        for (Map.Entry<String, List<String>> entry : autoresYLibros.entrySet()) {
            String nombreAutor = entry.getKey();
            List<String> librosAutor = entry.getValue();

            Autor autor = autoresVivos.stream()
                    .filter(a -> a.getNombre().equals(nombreAutor))
                    .findFirst()
                    .orElse(null);

            if (autor != null) {
                StringBuilder detallesAutor = new StringBuilder();
                detallesAutor.append("+++++++++ AUTOR +++++++++")
                        .append("\nNombre: ").append(nombreAutor)
                        .append("\nFecha de nacimiento: ").append(autor.getFechaDeNacimiento())
                        .append("\nFecha de muerte: ").append(autor.getFechaDeMuerte())
                        .append("\nLibros: ").append(String.join(", ", librosAutor))
                        .append("\n");

                System.out.println(detallesAutor.toString());
            }
        }
    }

    // Listar libros por idioma
    private void listarLibrosPorIdioma() {
        System.out.println("""
            Escriba el idioma del libro:
            ES: Español
            EN: Inglés
            FR: Francés
            IT: Italiano
            PT: Portugués
            """);

        String idiomaSeleccionado = scan.nextLine().toUpperCase();

        try {
            Idioma idioma = Idioma.valueOf(idiomaSeleccionado);
            List<Libro> librosPorIdioma = repositorio.findByIdiomas(idioma);

            if (librosPorIdioma.isEmpty()) {
                System.out.println("No se encontraron libros en el idioma seleccionado...\n");
                return;
            }

            for (Libro libro : librosPorIdioma) {
                StringBuilder detallesLibro = new StringBuilder();
                detallesLibro.append("+++++++++ LIBRO +++++++++")
                        .append("\nTítulo: ").append(libro.getTitulo())
                        .append("\nIdioma: ").append(libro.getIdiomas())
                        .append("\nAutor: ").append(libro.getAutor().stream().map(Autor::getNombre).collect(Collectors.joining(", ")))
                        .append("\nNúmero de descargas: ").append(libro.getNumeroDeDescargas())
                        .append("\n");

                System.out.println(detallesLibro.toString());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Idioma no existe...\n");
        }
    }











}