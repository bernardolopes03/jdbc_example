package dataaccess.rdgw;

import business.SaleStatus;
import dataaccess.DataSource;
import dataaccess.exception.PersistenceException;
import dataaccess.exception.RecordNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class SaleRowDataGateway {

    private int id;
    private Date date;
    private SaleStatus status;
    private int customerId;
    private double totalSale;
    private double totalDiscount;

    private static final String GET_SALE_SQL =
            "select id, open_date, status, customer_id, total_sale, total_discount " +
            "from sale " +
            "where id = ?";

    private static final String INSERT_SALE_SQL =
            "insert into sale (open_date, status, customer_id, total_sale, total_discount) " +
            "values (?, ?, ?, ?, ?)";

    public SaleRowDataGateway(int customerId, Date date) {
        this.customerId = customerId;
        this.date = date;
        this.status = SaleStatus.OPEN;
        this.totalSale = 0.0;
        this.totalDiscount = 0.0;
    }

    public int getId() { return id; }
    public Date getDate() { return date; }
    public SaleStatus getStatus() { return status; }
    public int getCustomerId() { return customerId; }
    public double getTotalSale() { return totalSale; }
    public double getDiscount() { return totalDiscount; }

    public void insert() throws PersistenceException {
        try (PreparedStatement statement = DataSource.INSTANCE.prepareGetGenKey(INSERT_SALE_SQL)) {
            statement.setDate(1, date);
            statement.setString(2, status.name());
            statement.setInt(3, customerId);
            statement.setDouble(4, totalSale);
            statement.setDouble(5, totalDiscount);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                rs.next();
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Internal error inserting sale!", e);
        }
    }

    public static SaleRowDataGateway find(int id) throws PersistenceException {
        try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_SALE_SQL)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return load(rs);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Internal error getting a sale by its id", e);
        }
    }

    private static SaleRowDataGateway load(ResultSet rs) throws SQLException, RecordNotFoundException {
        if (!rs.next()) {
            throw new RecordNotFoundException("Sale does not exist");
        }
        SaleRowDataGateway sale = new SaleRowDataGateway(
                rs.getInt("customer_id"),
                rs.getDate("open_date")
        );
        sale.id = rs.getInt("id");
        sale.status = SaleStatus.valueOf(rs.getString("status"));
        sale.totalSale = rs.getDouble("total_sale");
        sale.totalDiscount = rs.getDouble("total_discount");
        return sale;
    }
}