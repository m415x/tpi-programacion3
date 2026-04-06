const listaCategorias = document.querySelector("#lista-categorias")
const cargarCategorias = (categorias) => {
    categorias.forEach(categoria => {
        const li = document.createElement("li")
        const a = document.createElement("a")
        a.href = "#"
        a.textContent = categoria
        li.appendChild(a)
        listaCategorias.appendChild(li)
    })
}

const contenedorProductos = document.querySelector("#contenedor-productos")
const cargarProductos = (productos) => {
    contenedorProductos.innerHTML = ""

    if (productos.length > 0) {
        productos.forEach(producto => {
            const article = document.createElement("article")
            article.classList.add("producto__card")

            article.innerHTML = `
            <img class="producto__imagen" src="${producto.imagen}" alt="${producto.nombre}">
            <div class="producto__contenido">
                <div class="producto__cuerpo">
                    <p class="producto__categoria">${producto.categoria}</p>
                    <h3 class="producto__nombre">${producto.nombre}</h3>
                    <p class="producto__descripcion">${producto.descripcion}</p>
                </div>
                <div class="producto__pie">
                    <p class="producto__precio"><strong>${producto.precio.toLocaleString()}</strong></p>
                    <button class="btn btn__tertiary btn__tertiary--add-product" onclick="alert('Has agregado: ${producto.nombre}')">
                        Agregar al carrito
                    </button>
                </div>
            </div>
        `
            contenedorProductos.appendChild(article)
        })
    } else {
        const emptyResult = document.createElement("p")
        emptyResult.classList.add("empty-result")
        emptyResult.textContent = "No se encontraron productos"
        contenedorProductos.appendChild(emptyResult)
    }
}

const inputSearch = document.querySelector("#input-search")
inputSearch.addEventListener("input", (e) => {
    const productosFiltrados = productos.filter(
        producto => producto.nombre.toLowerCase()
            .includes(e.target.value.toLowerCase())
    )
    cargarProductos(productosFiltrados)
})

// Llamada a las funciones
document.addEventListener("DOMContentLoaded", () => {
    cargarCategorias(categorias)
    cargarProductos(productos)
})