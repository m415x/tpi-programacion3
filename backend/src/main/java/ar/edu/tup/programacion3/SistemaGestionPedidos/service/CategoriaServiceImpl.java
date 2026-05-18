package ar.edu.tup.programacion3.SistemaGestionPedidos.service;

import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.categoria.CategoriaCreate;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.categoria.CategoriaDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.categoria.CategoriaEdit;
import ar.edu.tup.programacion3.SistemaGestionPedidos.dto.producto.ProductoDto;
import ar.edu.tup.programacion3.SistemaGestionPedidos.entity.Categoria;
import ar.edu.tup.programacion3.SistemaGestionPedidos.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public CategoriaDto save(CategoriaCreate categoriaCreate) {

        Categoria categoria = categoriaCreate.toEntity();
        categoria = categoriaRepository.save(categoria);

        return CategoriaDto.toDto(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDto findById(Long id) {

        Categoria categoria =
                categoriaRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró categoría con id: " + id));

        return CategoriaDto.toDto(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDto> findAll() {

        return categoriaRepository.findAll().stream().map(CategoriaDto::toDto).toList();
    }

    @Override
    @Transactional
    public CategoriaDto update(CategoriaEdit categoriaEdit, Long idCategoria) {

        Categoria categoria =
                categoriaRepository
                        .findById(idCategoria)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró categoría con id: " + idCategoria));

        categoriaEdit.applyTo(categoria);
        categoria = categoriaRepository.save(categoria);

        return CategoriaDto.toDto(categoria);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        Categoria categoria =
                categoriaRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "No se encontró categoría con id: " + id));

        categoria.setEliminado(true);

        if (categoria.getProductos() != null) {
            categoria
                    .getProductos()
                    .forEach(
                            producto -> {
                                producto.setEliminado(true);
                            });
        }

        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDto> findCategoriesByName(String name) {

        return categoriaRepository.findByNombreContainingIgnoreCase(name).stream()
                .map(CategoriaDto::toDto)
                .toList();
    }
}
