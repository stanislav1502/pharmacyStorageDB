package org.magistraturasgi.pharmacystorage;

import java.sql.*;

public class DBUtil {
	
	private PreparedStatement getProductsPrepared = null;
	private PreparedStatement getClientsPrepared = null;
	private PreparedStatement getSuppliersPrepared = null;
	private PreparedStatement getShippersPrepared = null;
	private PreparedStatement getInboundsPrepared = null;
	private PreparedStatement getOutboundsPrepared = null;

	private static final String GET_PRODUCTS_QUERY = "SELECT e.product_id AS \"Id\", e.product_name AS \"Name\", e.product_quantity AS \"Quantity\", e.product_price AS \"Price\", e.product_accesslevel AS \"Access Level\" FROM products e";
	private static final String GET_CLIENTS_QUERY = "SELECT e.client_id AS \"Id\", e.client_name AS \"Name\", e.client_address AS \"Address\", e.client_accesslevel AS \"Access Level\" FROM clients e";
	private static final String INSERT_CLIENT_QUERY = "INSERT INTO clients VALUES(?, ?, ?, ?)";
	private static final String GET_SHIPPERS_QUERY = "SELECT e.shipper_id AS \"Id\", e.shipper_name AS \"Name\", e.shipper_speed.time_amount AS \"Time\", e.shipper_speed.time_scale AS \"Speed\", e.shipper_price AS \"Price\" FROM shippers e";
	private static final String INSERT_SHIPPER_QUERY = "INSERT INTO shippers VALUES(?, ?, ?, ?, ?)";
	private static final String GET_SUPPLIERS_QUERY = "SELECT e.supplier_id AS \"Id\", e.supplier_name AS \"Name\" FROM suppliers e";
	private static final String INSERT_SUPPLIER_QUERY = "INSERT INTO suppliers VALUES(?, ?)";
	private static final String GET_INBOUNDS_QUERY = "SELECT e.inbound_id AS \"Id\", e.shipment_status AS \"Status\", e.inbound_product.product_name AS \"Product\", e.shipment_quantity AS \"Quantity\", e.shipment_price AS \"Price\", e.inbound_supplier.supplier_name AS \"Supplier\", e.inbound_arrive_date  AS \"Arrives\" FROM inbound_shipments e";
	private static final String INSERT_INBOUND_QUERY = "INSERT INTO inbound_shipments VALUES(?, ?, ?, ?, ?)";
	private static final String GET_OUTBOUNDS_QUERY = "SELECT e.outbound_id AS \"Id\", e.shipment_status AS \"Status\", e.outbound_product.product_name AS \"Product\", e.shipment_quantity AS \"Quantity\", e.shipment_price AS \"Price\", e.outbound_client.client_name AS \"Client\", e.outbound_shipper.shipper_name AS \"Shipper\", e.outbound_requested_date AS \"Requested\", e.outbound_sent_date AS \"Sent\"  FROM outbound_shipments e";
	private static final String INSERT_OUTBOUND_QUERY = "INSERT INTO outbound_shipments VALUES(?, ?, ?, ?, ?, ?)";

	private static Connection cachedConnection = null;
	private static final DBUtil instance = new DBUtil();


	private DBUtil() {}
	
	public static DBUtil getInstance(){
		return instance;
	}
	
	public static Connection getConnection()
	{
		try {
			if (cachedConnection == null ||
					cachedConnection.isClosed() ||
					!cachedConnection.isValid(10)){
				System.out.println("Attempting to get a new connection to DB!");
				DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
				cachedConnection = DriverManager.getConnection("jdbc:oracle:thin:@172.16.251.135:1521:orcl", "c##ex23_stanislav_coursework", "stanislav236303");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cachedConnection;
	}
	
	private PreparedStatement getProductsStatement(){
		if (getProductsPrepared == null){
			try {
				getProductsPrepared = getConnection().prepareStatement(GET_PRODUCTS_QUERY);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return getProductsPrepared;
	}
	public ResultSet getProducts(){
		ResultSet result = null;
		try {
			PreparedStatement stmt = getProductsStatement();
			result = stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private PreparedStatement getClientsStatement() throws SQLException {
		if (getClientsPrepared == null) {
			getClientsPrepared = getConnection().prepareStatement(GET_CLIENTS_QUERY);
		}
		return getClientsPrepared;
	}

	public ResultSet getClients() {
		ResultSet result = null;
		try {
			PreparedStatement stmt = getClientsStatement();
			result = stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private PreparedStatement getShippersStatement() throws SQLException {
		if (getShippersPrepared == null) {
			getShippersPrepared = getConnection().prepareStatement(GET_SHIPPERS_QUERY);
		}
		return getShippersPrepared;
	}

	public ResultSet getShippers() {
		ResultSet result = null;
		try {
			PreparedStatement stmt = getShippersStatement();
			result = stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private PreparedStatement getSuppliersStatement() throws SQLException {
		if (getSuppliersPrepared == null) {
			getSuppliersPrepared = getConnection().prepareStatement(GET_SUPPLIERS_QUERY);
		}
		return getSuppliersPrepared;
	}

	public ResultSet getSuppliers() {
		ResultSet result = null;
		try {
			PreparedStatement stmt = getSuppliersStatement();
			result = stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private PreparedStatement getInboundsStatement() throws SQLException {
		if (getInboundsPrepared == null) {
			getInboundsPrepared = getConnection().prepareStatement(GET_INBOUNDS_QUERY);
		}
		return getInboundsPrepared;
	}

	public ResultSet getInbounds() {
		ResultSet result = null;
		try {
			PreparedStatement stmt = getInboundsStatement();
			result = stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private PreparedStatement getOutboundsStatement() throws SQLException {
		if (getOutboundsPrepared == null) {
			getOutboundsPrepared = getConnection().prepareStatement(GET_OUTBOUNDS_QUERY);
		}
		return getOutboundsPrepared;
	}

	public ResultSet getOutbounds() {
		ResultSet result = null;
		try {
			PreparedStatement stmt = getOutboundsStatement();
			result = stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
