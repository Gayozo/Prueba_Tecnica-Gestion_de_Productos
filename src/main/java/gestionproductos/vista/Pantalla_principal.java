package gestionproductos.vista;

import gestionproductos.modelo.Producto;
import gestionproductos.persistencia_y_JDBC.Persistencia_producto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class Pantalla_principal extends JFrame {

    // Instancia para la persistencia conectada a PostgreSQL
    private final Persistencia_producto persistenciaProducto = new Persistencia_producto();

    // Componentes del Formulario
    private JTextField txtId;
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtCategoria;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JComboBox<String> cmbEstado;

    // Botones de acción
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;

    // Componentes de Búsqueda y Filtros
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnLimpiarFiltro;
    private JButton btnBajoStock;
    private JButton btnAjustarStock;

    // Tabla y Modelo Reactivo
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;

    public Pantalla_principal() {
        setTitle("Gestión de Productos - Prueba Técnica");
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        inicializarComponentes();
        configurarEventosUI();
        cargarTablaDesdeBD(); // Carga reactiva de los datos reales desde PostgreSQL
    }

    private void inicializarComponentes() {
        // Panel Superior: Búsqueda y Filtros
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSuperior.setBorder(new TitledBorder("Búsqueda y Filtros"));

        panelSuperior.add(new JLabel("Buscar (Código/Nombre):"));
        txtBuscar = new JTextField(16);
        panelSuperior.add(txtBuscar);

        btnBuscar = new JButton("Buscar");
        btnLimpiarFiltro = new JButton("Mostrar Todos");
        btnBajoStock = new JButton("Alerta: Bajo Stock (< 5)");
        btnBajoStock.setBackground(new Color(255, 235, 238));

        panelSuperior.add(btnBuscar);
        panelSuperior.add(btnLimpiarFiltro);
        panelSuperior.add(btnBajoStock);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel Izquierdo: Formulario
        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.setBorder(new EmptyBorder(0, 10, 10, 0));
        panelIzquierdo.setPreferredSize(new Dimension(340, 0));

        JPanel panelFormulario = new JPanel(new GridLayout(7, 2, 8, 8));
        panelFormulario.setBorder(new TitledBorder("Datos del Producto"));

        txtId = new JTextField();
        txtId.setEditable(false);
        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtCategoria = new JTextField();
        txtPrecio = new JTextField();
        txtStock = new JTextField();
        cmbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});

        panelFormulario.add(new JLabel("ID (Automático):"));
        panelFormulario.add(txtId);
        panelFormulario.add(new JLabel("Código (*):"));
        panelFormulario.add(txtCodigo);
        panelFormulario.add(new JLabel("Nombre (*):"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Categoría:"));
        panelFormulario.add(txtCategoria);
        panelFormulario.add(new JLabel("Precio (*):"));
        panelFormulario.add(txtPrecio);
        panelFormulario.add(new JLabel("Stock Inicial (*):"));
        panelFormulario.add(txtStock);
        panelFormulario.add(new JLabel("Estado:"));
        panelFormulario.add(cmbEstado);

        JPanel panelBotonesForm = new JPanel(new GridLayout(2, 2, 6, 6));
        btnNuevo = new JButton("Limpiar / Nuevo");
        btnGuardar = new JButton("Guardar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");

        panelBotonesForm.add(btnNuevo);
        panelBotonesForm.add(btnGuardar);
        panelBotonesForm.add(btnModificar);
        panelBotonesForm.add(btnEliminar);

        panelIzquierdo.add(panelFormulario, BorderLayout.CENTER);
        panelIzquierdo.add(panelBotonesForm, BorderLayout.SOUTH);

        add(panelIzquierdo, BorderLayout.WEST);

        // Panel Central: JTable con modelo estandar de tabla
        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));
        panelCentro.setBorder(new EmptyBorder(0, 0, 10, 10));

        String[] columnas = {"ID", "Código", "Nombre", "Categoría", "Precio", "Stock", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evitar edición directa sobre las celdas de la tabla
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.setBorder(new TitledBorder("Listado de Productos Registrados"));

        panelCentro.add(scrollTabla, BorderLayout.CENTER);

        // Panel Inferior: Control de Stock
        JPanel panelAccionesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAjustarStock = new JButton("Ajustar Stock del Producto Seleccionado");
        panelAccionesTabla.add(btnAjustarStock);
        panelCentro.add(panelAccionesTabla, BorderLayout.SOUTH);

        add(panelCentro, BorderLayout.CENTER);
    }

    private void configurarEventosUI() {
        // Selección de fila para visualizar y editar datos
        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaProductos.getSelectedRow();
                if (fila != -1) {
                    txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                    txtCodigo.setText(modeloTabla.getValueAt(fila, 1).toString());
                    txtNombre.setText(modeloTabla.getValueAt(fila, 2).toString());
                    txtCategoria.setText(modeloTabla.getValueAt(fila, 3).toString());
                    txtPrecio.setText(modeloTabla.getValueAt(fila, 4).toString());
                    txtStock.setText(modeloTabla.getValueAt(fila, 5).toString());
                    cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 6).toString());
                }
            }
        });

        // Limpiar campos del formulario
        btnNuevo.addActionListener(e -> limpiarCampos());

        // Registrar Producto
        btnGuardar.addActionListener(e -> registrarProducto());

        // Modificar Producto
        btnModificar.addActionListener(e -> modificarProducto());

        // Eliminar Producto con confirmación previa
        btnEliminar.addActionListener(e -> eliminarProducto());

        // Buscar productos por código o nombre
        btnBuscar.addActionListener(e -> buscarProductos());

        // Mostrar listado completo
        btnLimpiarFiltro.addActionListener(e -> {
            txtBuscar.setText("");
            cargarTablaDesdeBD();
        });

        // Consultar productos con bajo stock (menores a 5)
        btnBajoStock.addActionListener(e -> filtrarBajoStock());

        // Ajustar stock
        btnAjustarStock.addActionListener(e -> ajustarStockSeleccionado());
    }

    private void cargarTabla(List<Producto> lista) {
        modeloTabla.setRowCount(0);
        for (Producto p : lista) {
            modeloTabla.addRow(new Object[]{
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                p.getPrecio(),
                p.getStock(),
                p.getEstado()
            });
        }
    }

    private void cargarTablaDesdeBD() {
        try {
            cargarTabla(persistenciaProducto.listarTodos());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar la base de datos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarProducto() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String stockStr = txtStock.getText().trim();
        String estado = cmbEstado.getSelectedItem().toString();

        //Validación de Campos obligatorios incompletos
        if (codigo.isEmpty() || nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos obligatorios (*).", "Datos Inválidos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            int stock = Integer.parseInt(stockStr);

            // Validación precio mayor a 0 y stock mayor o igual a 0
            if (precio <= 0) {
                JOptionPane.showMessageDialog(this, "El precio debe ser estrictamente mayor a cero.", "Validación de Precio", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "El stock no puede ser negativo.", "Validación de Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validación de no duplicar códigos
            if (persistenciaProducto.existeCodigo(codigo, null)) {
                JOptionPane.showMessageDialog(this, "El código '" + codigo + "' ya se encuentra registrado.", "Código Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Producto nuevo = new Producto(codigo, nombre, categoria, precio, stock, estado);
            if (persistenciaProducto.insertar(nuevo)) {
                JOptionPane.showMessageDialog(this, "Producto registrado exitosamente.");
                limpiarCampos();
                cargarTablaDesdeBD(); // Reflejo inmediato en el listado
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio y Stock deben contener valores numéricos válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar en base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarProducto() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione primero un producto de la tabla para modificar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(txtId.getText());
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String categoria = txtCategoria.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            String estado = cmbEstado.getSelectedItem().toString();

            if (codigo.isEmpty() || nombre.isEmpty() || precio <= 0 || stock < 0) {
                JOptionPane.showMessageDialog(this, "Verifique los datos: campos vacíos, precio <= 0 o stock negativo.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar si el codigo nuevo es igual a otro
            if (persistenciaProducto.existeCodigo(codigo, id)) {
                JOptionPane.showMessageDialog(this, "El código '" + codigo + "' pertenece a otro producto registrado.", "Código Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Producto prod = new Producto(id, codigo, nombre, categoria, precio, stock, estado);
            if (persistenciaProducto.actualizar(prod)) {
                JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente.");
                limpiarCampos();
                cargarTablaDesdeBD();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio y Stock deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar en base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarProducto() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea eliminar el producto seleccionado?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(txtId.getText());
                if (persistenciaProducto.eliminar(id)) {
                    JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente.");
                    limpiarCampos();
                    cargarTablaDesdeBD();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar en base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarProductos() {
        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            cargarTablaDesdeBD();
            return;
        }
        try {
            cargarTabla(persistenciaProducto.buscarPorCodigoONombre(criterio));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la búsqueda: " + eMessage(ex), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarBajoStock() {
        try {
            cargarTabla(persistenciaProducto.listarBajoStock(5));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al filtrar por bajo stock: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajustarStockSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla para ajustar su stock.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        String stockActual = modeloTabla.getValueAt(fila, 5).toString();

        String nuevoStockStr = JOptionPane.showInputDialog(
            this,
            "Stock actual: " + stockActual + "\nIngrese la nueva cantidad de stock:",
            "Ajustar Stock",
            JOptionPane.QUESTION_MESSAGE
        );

        if (nuevoStockStr != null && !nuevoStockStr.trim().isEmpty()) {
            try {
                int nuevoStock = Integer.parseInt(nuevoStockStr.trim());
                if (nuevoStock < 0) {
                    JOptionPane.showMessageDialog(this, "El stock no puede quedar con un valor negativo.", "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (persistenciaProducto.actualizarStock(id, nuevoStock)) {
                    JOptionPane.showMessageDialog(this, "Stock actualizado exitosamente.");
                    cargarTablaDesdeBD();
                    limpiarCampos();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un número entero válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar stock: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtCodigo.setText("");
        txtNombre.setText("");
        txtCategoria.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        cmbEstado.setSelectedIndex(0);
        tablaProductos.clearSelection();
    }

    private String eMessage(Exception ex) {
        return ex.getMessage() != null ? ex.getMessage() : "Error desconocido";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Pantalla_principal().setVisible(true);
        });
    }
}