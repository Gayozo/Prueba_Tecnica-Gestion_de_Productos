package main.java.gestionproductos.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Pantalla_principal extends JFrame {

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

    // Tabla y Modelo
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;

    public Pantalla_principal() {
        setTitle("Gestión de Productos");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Panel Superior: Búsqueda - Acciones Rápidas
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSuperior.setBorder(new TitledBorder("Búsqueda y Filtros"));

        panelSuperior.add(new JLabel("Buscar (Código/Nombre):"));
        txtBuscar = new JTextField(15);
        panelSuperior.add(txtBuscar);

        btnBuscar = new JButton("Buscar");
        btnLimpiarFiltro = new JButton("Mostrar Todos");
        btnBajoStock = new JButton("Alerta: Bajo Stock (< 5)");
        btnBajoStock.setBackground(new Color(255, 235, 238));

        panelSuperior.add(btnBuscar);
        panelSuperior.add(btnLimpiarFiltro);
        panelSuperior.add(btnBajoStock);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel Izquierdo: Formulario para Registro - Modificación
        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.setBorder(new EmptyBorder(0, 10, 10, 0));
        panelIzquierdo.setPreferredSize(new Dimension(320, 0));

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
        panelFormulario.add(new JLabel("Stock Inicial:"));
        panelFormulario.add(txtStock);
        panelFormulario.add(new JLabel("Estado:"));
        panelFormulario.add(cmbEstado);

        // Subpanel botones del formulario
        JPanel panelBotonesForm = new JPanel(new GridLayout(2, 2, 5, 5));
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

        // Panel Central: Listado (JTable con DefaultTableModel)
        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));
        panelCentro.setBorder(new EmptyBorder(0, 0, 10, 10));

        String[] columnas = {"ID", "Código", "Nombre", "Categoría", "Precio", "Stock", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Deshabilitar edición directa en celdas
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.setBorder(new TitledBorder("Listado de Productos Registrados"));

        panelCentro.add(scrollTabla, BorderLayout.CENTER);

        // Barra inferior del listado: Ajuste rápido de stock
        JPanel panelAccionesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAjustarStock = new JButton("Ajustar Stock del Producto Seleccionado");
        panelAccionesTabla.add(btnAjustarStock);
        panelCentro.add(panelAccionesTabla, BorderLayout.SOUTH);

        add(panelCentro, BorderLayout.CENTER);

        // Asignar Eventos Básicos de Interfaz
        configurarEventosUI();
    }

    private void configurarEventosUI() {
        // Evento al hacer clic en una fila de la tabla: cargar datos al formulario
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

        // Limpiar campos para un nuevo registro
        btnNuevo.addActionListener(e -> limpiarCampos());

        // Diálogo para ajustar stock
        btnAjustarStock.addActionListener(e -> {
            int fila = tablaProductos.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            solicitarAjusteStock(fila);
        });

        // Confirmación requerida para eliminar
        btnEliminar.addActionListener(e -> {
            int fila = tablaProductos.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de que desea eliminar el producto seleccionado?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirmacion == JOptionPane.YES_OPTION) {
                modeloTabla.removeRow(fila);
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
            }
        });
    }

    private void solicitarAjusteStock(int fila) {
        String stockActualStr = modeloTabla.getValueAt(fila, 5).toString();
        String nuevoStockStr = JOptionPane.showInputDialog(
                this,
                "Stock actual: " + stockActualStr + "\nIngrese la nueva cantidad de stock:",
                "Ajustar Stock",
                JOptionPane.QUESTION_MESSAGE
        );

        if (nuevoStockStr != null && !nuevoStockStr.trim().isEmpty()) {
            try {
                int nuevoStock = Integer.parseInt(nuevoStockStr.trim());
                if (nuevoStock < 0) {
                    JOptionPane.showMessageDialog(this, "El stock no puede ser negativo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                modeloTabla.setValueAt(nuevoStock, fila, 5);
                txtStock.setText(String.valueOf(nuevoStock));
                JOptionPane.showMessageDialog(this, "Stock actualizado exitosamente.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
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
}