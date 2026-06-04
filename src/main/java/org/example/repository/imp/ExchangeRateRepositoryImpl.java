package org.example.repository.imp;

import lombok.RequiredArgsConstructor;
import org.example.repository.ExchangeRateRepository;
import org.example.repository.entity.ExchangeRateEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeRateRepositoryImpl implements ExchangeRateRepository {

    private final Logger logger = LoggerFactory.getLogger(ExchangeRateRepositoryImpl.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<ExchangeRateEntity> exchangeRateEntityRowMapper = (rs, rowNum) -> ExchangeRateEntity.builder()
            .id(rs.getLong("id"))
            .rate(rs.getBigDecimal("rate"))
            .fromCurrency(rs.getString("from_currency"))
            .toCurrency(rs.getString("to_currency"))
            .scale(rs.getBigDecimal("scale"))
            .build();

    @Override
    public List<ExchangeRateEntity> findAll() {
        String query = "SELECT from_currency, id, to_currency, rate, scale FROM cur_ex.exchange_rate ";

        return jdbcTemplate.query(query, new MapSqlParameterSource(), exchangeRateEntityRowMapper);
    }

    @Override
    public void insert(ExchangeRateEntity exchangeRate) {
        String query = """
                    INSERT INTO cur_ex.exchange_rate
                    (from_currency, to_currency, rate, "scale")
                    VALUES(:fromCurrency, :toCurrency , :rate, :scale);
                """;

        MapSqlParameterSource paramSource = new MapSqlParameterSource()
                .addValue("fromCurrency", exchangeRate.getFromCurrency())
                .addValue("toCurrency", exchangeRate.getToCurrency())
                .addValue("rate", exchangeRate.getRate())
                .addValue("scale", exchangeRate.getScale());

        int affectedRows = jdbcTemplate.update(query, paramSource);
        logger.info("Inserted " + affectedRows + " rows");
    }

    @Override
    public void delete(ExchangeRateEntity exchangeRate) {
        String query = """
                    DELETE FROM cur_ex.exchange_rate
                    WHERE
                        FROM_CURRENCY = :fromCurrency
                        AND TO_CURRENCY = :toCurrency
                        AND RATE = :rate
                        AND SCALE = :scale
                """;

        MapSqlParameterSource paramSource = new MapSqlParameterSource()
                .addValue("fromCurrency", exchangeRate.getFromCurrency())
                .addValue("toCurrency", exchangeRate.getToCurrency())
                .addValue("rate", exchangeRate.getRate())
                .addValue("scale", exchangeRate.getScale());

        int affectedRows = jdbcTemplate.update(query, paramSource);
        logger.info("Deleted " + affectedRows + " rows");
    }

}
