package org.una.programmingIII.utemp_app.config;

import org.una.programmingIII.utemp_app.services.responses.PageResponse;

import java.util.Optional;

/**
 * Propósito: Este es un interface que define las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) que cualquier servicio que maneje entidades debería implementar. Establece un contrato que asegura que cualquier clase que implemente esta interfaz deberá proporcionar estas operaciones básicas.
 * Funciones Principales:
 * Declarar métodos para encontrar, crear, actualizar y eliminar entidades.
 * Promover la reutilización de código y la consistencia en la implementación de operaciones CRUD en diferentes servicios.
 *
 * @param <T>
 * @param <ID>
 */
public interface CrudService<T, ID> {//Frontend

    PageResponse<T> findAll(int page, int size) throws Exception;

    Optional<T> findById(ID id) throws Exception;

    T create(T entity) throws Exception;

    Optional<T> update(ID id, T entity) throws Exception;

    boolean delete(ID id, boolean isPermanentDelete) throws Exception;

    Optional<T> findByIdentifier(String identifier) throws Exception;
}