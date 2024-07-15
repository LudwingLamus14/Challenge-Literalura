package com.alura.literalura.repository;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Idioma;
import com.alura.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibrosRepository extends JpaRepository<Libro, Long> {

    List<Libro> findByIdiomas(Idioma idioma);

    @Query("SELECT a FROM Autor a JOIN a.libros l")
    List<Autor> mostrarAutores();

    @Query("SELECT a FROM Autor a JOIN a.libros l WHERE a.fechaDeNacimiento <= :year AND (a.fechaDeMuerte >= :year OR a.fechaDeMuerte IS NULL)")
    List<Autor> mostrarAutoresVivos(@Param("year") String year);
}
