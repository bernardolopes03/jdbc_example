package dataaccess.rdgw;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dataaccess.DataSource;
import dataaccess.exception.PersistenceException;
import dataaccess.exception.RecordNotFoundException;

public class ProductRowDataGateway {

	private int id;
	private int prodCod;
	private String description;
	private double faceValue;
	private double qty;
	private String discountEligibility;
	private int unitId;

	private static final String	INSERT_PRODUCT_SQL = "insert into product (prodCod, description, faceValue, qty, discountEligibility, unit_Id)"
			+ " values(?,?,?,?,?,?)"; //TODO complete me




	private static final String	UPDATE_STOCK_SQL = "update product set qty = ? where id = ?"; //TODO complete me




	private static final String GET_PRODUCT_BY_PROD_COD_SQL = "select id, prodcod, description, facevalue, qty, discounteligibility, unit_id " +
			"from product " +
			"where prodcod = ?";; //TODO complete me
	
	
	
	private static final String GET_PRODUCT_BY_ID_SQL = "select id, prodcod, description, facevalue, qty, discounteligibility, unit_id " +
			"from product" +
			"where unit_id = ?"; //TODO complete me

	private static final String ELIGIBLE = "E";
	private static final String NOT_ELIGIBLE = "N";

	// Construtor completo
	public ProductRowDataGateway(int prodCod, String description, double faceValue,
			double qty, boolean eligibleForDiscount, int unitId) {
		this.prodCod = prodCod;
		this.description = description;
		this.faceValue = faceValue;
		this.qty = qty;
		setEligibleForDiscount(eligibleForDiscount);
		this.unitId = unitId;
	}

	// Construtor de 4 parâmetros compatível com o exemplo de teste do README.md
	public ProductRowDataGateway(int prodCod, String description, double faceValue, double qty) {
		this(prodCod, description, faceValue, qty, false, 1);}

	public int getProductId() { return id; }
	public int getProdCod() { return prodCod; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public double getFaceValue() { return faceValue; }
	public void setFaceValue(double faceValue) { this.faceValue = faceValue; }
	public double getQty() { return qty; }
	public void setQty(double qty) { this.qty = qty; }
	public boolean isEligibleForDiscount() { return ELIGIBLE.equals(discountEligibility); }
	public void setEligibleForDiscount(boolean eligibleForDiscount) { 
		this.discountEligibility = eligibleForDiscount ? ELIGIBLE : NOT_ELIGIBLE; }
	public int getUnitId() { return unitId; }
	public void setUnitId(int unitId) { this.unitId = unitId; }

	public static ProductRowDataGateway findWithProdCod (int prodCod) throws PersistenceException {
		//TODO complete me
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_PRODUCT_BY_PROD_COD_SQL)) {
			statement.setInt(1, prodCod);
			try (ResultSet rs = statement.executeQuery()) {
				return loadProduct(rs);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting a product by its code", e);
		}
		
	}

	public static ProductRowDataGateway find (int id) throws PersistenceException {
		//TODO complete me
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_PRODUCT_BY_ID_SQL)) {
			statement.setInt(1, id);
			try (ResultSet rs = statement.executeQuery()) {
				return loadProduct(rs);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting a product by its id", e);
		}
	}

	public void updateStockValue () throws PersistenceException {
		//TODO complete me
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(UPDATE_STOCK_SQL)) {
			statement.setDouble(1, qty);
			statement.setInt(2, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException("Internal error updating product stock", e);
		}
	}

	private static ProductRowDataGateway loadProduct(ResultSet rs) throws RecordNotFoundException {
		//TODO complete me
			try {
			if (!rs.next()) {
				throw new RecordNotFoundException("Product does not exist");
			}
			ProductRowDataGateway newProduct = new ProductRowDataGateway(
					rs.getInt("prodcod"),
					rs.getString("description"),
					rs.getDouble("facevalue"),
					rs.getDouble("qty"),
					ELIGIBLE.equals(rs.getString("discounteligibility")),
					rs.getInt("unit_id")
			);
			newProduct.id = rs.getInt("id");
			return newProduct;
		} catch (SQLException e) {
			throw new RecordNotFoundException("Product does not exist", e);
		}	
	}
}
