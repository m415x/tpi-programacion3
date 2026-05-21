package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.category.CategoryEdit;
import java.util.List;

public interface CategoryService {

    public CategoryDto save(CategoryCreate categoryCreate);

    public CategoryDto findById(Long id);

    public List<CategoryDto> findAll();

    public CategoryDto update(CategoryEdit categoryEdit, Long id);

    public void deleteById(Long id);

    // Método personalizado
    public List<CategoryDto> findCategoriesByName(String name);
}
