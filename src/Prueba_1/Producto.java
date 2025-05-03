package Prueba_1;

public class Producto {
    private int idProducto;
    private String nombre;
    private double precio;
    private int cantidadStock;

    public Producto(int idProducto, String nombre, double precio, int cantidadStock) {
        this.idProducto = idProducto;
        this.nombre = nombre;
 
        this.precio = precio;
        this.cantidadStock = cantidadStock;
    }



    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidadStock() {
        return cantidadStock;
    }

    public void setCantidadStock(int cantidadStock) {
        this.cantidadStock = cantidadStock;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", cantidadStock=" + cantidadStock +
                '}';
    }
}

