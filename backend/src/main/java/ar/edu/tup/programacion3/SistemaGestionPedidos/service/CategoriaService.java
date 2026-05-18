package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.categoria.CategoriaCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.categoria.CategoriaDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.categoria.CategoriaEdit;

import java.util.List;

public interface CategoriaService {

    public CategoriaDto save(CategoriaCreate categoriaCreate);

    public CategoriaDto findById(Long id);

    public List<CategoriaDto> findAll();

    public CategoriaDto update(CategoriaEdit categoriaEdit, Long idCategoria);

    public void deleteById(Long id);

    // Método personalizado
    public List<CategoriaDto> findCategoriesByName(String name);
}
