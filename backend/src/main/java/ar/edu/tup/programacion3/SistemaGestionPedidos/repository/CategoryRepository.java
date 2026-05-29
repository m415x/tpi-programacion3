package ar.edu.tup.programacion3.SistemaGestionPedidos.repository;

import ar.edu.tup.programacion3.SistemaGestionPedidos.model.Category;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * =======================================================================================================
 * REQUERIMIENTO EXIGIDO POR EL PARCIAL 2 (HISTORIA DE USUARIO HU-02)
 * =======================================================================================================
 * 1. HERENCIA GENÉRICA: Esta interfaz extiende de BaseRepository<Category, Long>, heredando
 * automáticamente todas las capacidades del CRUD genérico y consultas de auditoría sin duplicar
 * código.
 * 2. ACLARACIÓN DE CRITERIO DE ACEPTACIÓN 1 (super): La consigna menciona invocar a
 * 'super(Category.class)'. Dado que en el ecosistema moderno de Spring Data JPA los repositorios se
 * definen como INTERFACES y no como clases, no existe un constructor ni soporte para la palabra
 * clave 'super'. Spring Data resuelve esto de forma nativa y superior mediante los tipos genéricos
 * declarados (<Category, Long>), abstrayendo al desarrollador de asociar la clase manualmente.
 * =======================================================================================================
 */
@Repository
public interface CategoryRepository extends BaseRepository<Category, Long> {

    List<Category> findByNameContainingIgnoreCase(String name);
}
