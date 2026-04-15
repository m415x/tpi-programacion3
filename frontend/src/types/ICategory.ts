// Interfaz que define la estructura de un ICategory en la tienda
export interface ICategory {
    id: number;
    eliminado: boolean;
    createdAt: string;
    nombre: string;
    descripcion: string;
}
