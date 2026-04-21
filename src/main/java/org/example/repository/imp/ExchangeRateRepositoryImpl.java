package org.example.repository.imp;

import org.example.config.PropertiesLoader;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.entity.ExchangeRateEntity;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateRepositoryImpl implements ExchangeRateRepository {

    private final String URL = PropertiesLoader.getProperty("database.url");
    private final String USERNAME = PropertiesLoader.getProperty("database.username");
    private final String PASSWORD = PropertiesLoader.getProperty("database.password");

    private final Connection connection;

    public ExchangeRateRepositoryImpl() {
        try {
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExchangeRateEntity> findAll() {

        List<ExchangeRateEntity> exchangeRates = new ArrayList<>();

        String query = "SELECT from_currency, id, to_currency, rate, scale FROM cur_ex.exchange_rate ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            //preparedStatement.setLong(1, 1L);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ExchangeRateEntity exchangeRate = ExchangeRateEntity.builder()
                        .fromCurrency(resultSet.getString("from_currency"))
                        .toCurrency(resultSet.getString("to_currency"))
                        .rate(resultSet.getBigDecimal("rate"))
                        .scale(resultSet.getBigDecimal("scale"))
                        .build();

                exchangeRates.add(exchangeRate);
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return exchangeRates;
    }

    @Override
    public void insert(ExchangeRateEntity exchangeRate) {
        String query = """
            INSERT INTO cur_ex.exchange_rate
            (from_currency, id, to_currency, rate, "scale")
            VALUES(?, nextval('cur_ex.exchange_rate_id_seq'::regclass), ?, ?, ?);
        """;

        int affectedRows;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, exchangeRate.getFromCurrency());
            preparedStatement.setString(2, exchangeRate.getToCurrency());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());
            preparedStatement.setBigDecimal(4, exchangeRate.getScale());
            affectedRows = preparedStatement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Inserted " + affectedRows + " rows");
    }

    @Override
    public void delete(ExchangeRateEntity exchangeRate) {
        String query = """
            DELETE FROM cur_ex.exchange_rate
            WHERE
                FROM_CURRENCY = ?
                AND TO_CURRENCY = ?
                AND RATE = ?
                AND SCALE = ?
        """;

        int affectedRows;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, exchangeRate.getFromCurrency());
            preparedStatement.setString(2, exchangeRate.getToCurrency());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());
            preparedStatement.setBigDecimal(4, exchangeRate.getScale());
            affectedRows = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Inserted " + affectedRows + " rows");
    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
