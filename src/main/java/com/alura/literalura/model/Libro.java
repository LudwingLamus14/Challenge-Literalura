package com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @Enumerated(EnumType.STRING)
    private Idioma idiomas;
    private int numeroDeDescargas;
    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Autor> autor;

    public Libro(){}

    public Libro(DatosLibro datosLibro) {
        this.titulo = datosLibro.titulo();
        this.idiomas =Idioma.fromString(datosLibro.idiomas().stream().limit(1).collect(Collectors.joining()));
        this.numeroDeDescargas = datosLibro.numeroDeDescargas();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Idioma getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(Idioma idiomas) {
        this.idiomas = idiomas;
    }

    public int getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(int numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }

    public List<Autor> getAutor() {
        return autor;
    }

    public void setAutor(List<Autor> autor) {
        autor.forEach(a-> a.setLibros(this));
        this.autor = autor;
    }

    @Override
    public String toString() {
        return
                "titulo='" + titulo + '\'' +
                        ", idiomas=" + idiomas +
                        ", numeroDeDescargas=" + numeroDeDescargas +
                        ", autores=" + autor;
    }
}
