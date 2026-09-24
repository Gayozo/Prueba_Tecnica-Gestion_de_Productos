package gestionproductos.persistencia_y_JDBC;

import gestionproductos.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Persistencia_producto {

     // 1: Registrar producto en la db.
    public boolean insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (codigo, nombre, categoria, precio, stock, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getCategoria());
            ps.setDouble(4, producto.getPrecio());
            ps.setInt(5, producto.getStock());
            ps.setString(6, producto.getEstado());

            return ps.executeUpdate() > 0;
        }
    }

    // 2: Listar todos los productos registrados.
    public List<Producto> listarTodos() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id, codigo, nombre, categoria, precio, stock, estado FROM productos ORDER BY id ASC";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }
        }
        return lista;
    }

    // 3: Buscar productos por código o por nombre (coincidencia parcial no es sensible a mayúsculas).
    public List<Producto> buscarPorCodigoONombre(String criterio) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id, codigo, nombre, categoria, precio, stock, estado FROM productos " +
                     "WHERE LOWER(codigo) LIKE ? OR LOWER(nombre) LIKE ? ORDER BY id ASC";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String comodin = "%" + criterio.toLowerCase().trim() + "%";
            ps.setString(1, comodin);
            ps.setString(2, comodin);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearProducto(rs));
                }
            }
        }
        return lista;
    }

    // 5: Modificar datos de un producto existente.
    public boolean actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET codigo = ?, nombre = ?, categoria = ?, precio = ?, stock = ?, estado = ? WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getCategoria());
            ps.setDouble(4, producto.getPrecio());
            ps.setInt(5, producto.getStock());
            ps.setString(6, producto.getEstado());
            ps.setInt(7, producto.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // 6: Eliminar un producto por ID.
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // 7: Consultar productos con cantidad disponible inferior al límite definido.
    public List<Producto> listarBajoStock(int limite) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id, codigo, nombre, categoria, precio, stock, estado FROM productos WHERE stock < ? ORDER BY stock ASC";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearProducto(rs));
                }
            }
        }
        return lista;
    }

    // 8: Ajustar la cantidad disponible de un producto específico.
    public boolean actualizarStock(int id, int nuevoStock) throws SQLException {
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, nuevoStock);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        }
    }

    // Validar si un código ya existe para evitar duplicados.
    // Si idExcluir es distinto de null, excluye ese registro.
    public boolean existeCodigo(String codigo, Integer idExcluir) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM productos WHERE LOWER(codigo) = LOWER(?)");
        if (idExcluir != null) {
            sql.append(" AND id <> ?");
        }

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            ps.setString(1, codigo.trim());
            if (idExcluir != null) {
                ps.setInt(2, idExcluir);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Método privado para mapear el ResultSet a una instancia de Producto.
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                rs.getDouble("precio"),
                rs.getInt("stock"),
                rs.getString("estado")
        );
    }
}