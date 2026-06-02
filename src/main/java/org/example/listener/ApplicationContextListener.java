package org.example.listener;

import org.example.exception.HttpNBRBLoaderException;
import org.example.service.CurrencyConverter;
import org.example.service.impl.CurrencyConverterImp;
import org.example.util.Consts;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;

@WebListener
public class ApplicationContextListener implements ServletContextListener {

    private CurrencyConverter currencyConverter;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        /*try {
            currencyConverter = new CurrencyConverterImp();
        } catch (IOException | HttpNBRBLoaderException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute(Consts.CURRENCY_CONVERTER_SERVICE, currencyConverter);*/

        ServletContextListener.super.contextInitialized(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
