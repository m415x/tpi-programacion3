/**
 * Datos de productos y categorías para la aplicación
 */

export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  imagen: string;
  categoria: string;
}

export const categorias: string[] = [
  "Hamburguesas",
  "Pizzas",
  "Papas Fritas",
  "Bebidas",
];

export const productos: Producto[] = [
  {
    id: 1,
    nombre: "Hamburguesa Triple",
    descripcion: "Triple carne, cheddar y bacon",
    precio: 25000.5,
    imagen: "/src/assets/hamburguesa.webp",
    categoria: "Hamburguesas",
  },
  {
    id: 2,
    nombre: "Pizza Muzzarella",
    descripcion: "Salsa casera y orégano",
    precio: 18000.0,
    imagen: "/src/assets/pizza.jpeg",
    categoria: "Pizzas",
  },
  {
    id: 3,
    nombre: "Papas con Cheddar",
    descripcion: "Papas fritas con salsa cheddar y verdeo",
    precio: 12000.0,
    imagen: "/src/assets/papas-fritas.jpg",
    categoria: "Papas Fritas",
  },
  {
    id: 4,
    nombre: "Gaseosa 500ml",
    descripcion: "Línea Coca-Cola",
    precio: 5000.0,
    imagen: "/src/assets/coca-cola.jpg",
    categoria: "Bebidas",
  },
  {
    id: 5,
    nombre: "Hamburguesa Veggie",
    descripcion: "Medallón de lentejas, lechuga y tomate",
    precio: 22000.0,
    imagen: "/src/assets/hamburguesa-veggie.jpg",
    categoria: "Hamburguesas",
  },
  {
    id: 6,
    nombre: "Pizza Especial",
    descripcion: "Jamón, morrones y aceitunas",
    precio: 21000.0,
    imagen: "/src/assets/pizza-especial.jpg",
    categoria: "Pizzas",
  },
  {
    id: 7,
    nombre: "Cerveza Artesanal",
    descripcion: "Pinta de 500ml",
    precio: 7000.0,
    imagen: "/src/assets/pinta.jpg",
    categoria: "Bebidas",
  },
];
