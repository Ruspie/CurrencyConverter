package org.example.servlet;

import org.example.dto.ExchangeRateDto;
import org.example.service.CurrencyConverter;
import org.example.util.Consts;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/")
public class CurrencyConverterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        CurrencyConverter currencyConverter = (CurrencyConverter) getServletContext().getAttribute(Consts.CURRENCY_CONVERTER_SERVICE);

        List<ExchangeRateDto> exchangeRateDtos = currencyConverter.getAllExchangeRates();

        request.setAttribute("exchangeRateList", exchangeRateDtos.toArray());
        request.setAttribute("TEST_PARAM", "TEST VALUE");

        response.setContentType("text/html;charset=UTF-8");
        getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);

    }
}
