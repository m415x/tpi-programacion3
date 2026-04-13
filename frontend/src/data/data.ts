import type { IProduct } from "@interfaces/IProduct";

export const categorias: string[] = [
    "Hamburguesas",
    "Pizzas",
    "Papas Fritas",
    "Bebidas",
];

export const productos: IProduct[] = [
    {
        id: 1,
        name: "Hamburguesa Triple",
        description: "Triple carne, cheddar y bacon",
        price: 25000.5,
        image: "/src/assets/hamburguesa.webp",
        category: "Hamburguesas",
    },
    {
        id: 2,
        name: "Pizza Muzzarella",
        description: "Salsa casera y orégano",
        price: 18000.0,
        image: "/src/assets/pizza.jpeg",
        category: "Pizzas",
    },
    {
        id: 3,
        name: "Papas con Cheddar",
        description: "Papas fritas con salsa cheddar y verdeo",
        price: 12000.0,
        image: "/src/assets/papas-fritas.jpg",
        category: "Papas Fritas",
    },
    {
        id: 4,
        name: "Gaseosa 500ml",
        description: "Línea Coca-Cola",
        price: 5000.0,
        image: "/src/assets/coca-cola.jpg",
        category: "Bebidas",
    },
    {
        id: 5,
        name: "Hamburguesa Veggie",
        description: "Medallón de lentejas, lechuga y tomate",
        price: 22000.0,
        image: "/src/assets/hamburguesa-veggie.jpg",
        category: "Hamburguesas",
    },
    {
        id: 6,
        name: "Pizza Especial",
        description: "Jamón, morrones y aceitunas",
        price: 21000.0,
        image: "/src/assets/pizza-especial.jpg",
        category: "Pizzas",
    },
    {
        id: 7,
        name: "Cerveza Artesanal",
        description: "Pinta de 500ml",
        price: 7000.0,
        image: "/src/assets/pinta.jpg",
        category: "Bebidas",
    },
];
