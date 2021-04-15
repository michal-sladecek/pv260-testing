package pv260.customeranalysis;

import static com.googlecode.catchexception.CatchException.catchException;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import pv260.customeranalysis.entities.Customer;
import pv260.customeranalysis.entities.Product;
import pv260.customeranalysis.exceptions.CantUnderstandException;
import pv260.customeranalysis.exceptions.GeneralException;
import pv260.customeranalysis.interfaces.AnalyticalEngine;
import pv260.customeranalysis.interfaces.ErrorHandler;
import pv260.customeranalysis.interfaces.NewsList;
import pv260.customeranalysis.interfaces.Storage;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CustomerAnalysisTest {



    /**
     * Verify the ErrorHandler is invoked when one of the AnalyticalEngine methods
     * throws exception and the exception is not re-thrown from the CustomerAnalysis.
     * The exception is passed to the ErrorHandler directly, not wrapped.
     */
    @Test
    public void testErrorHandlerInvokedWhenEngineThrows() throws GeneralException {
        ErrorHandler handler = mock(ErrorHandler.class);
        Product product = mock(Product.class);
        AnalyticalEngine engine = mock(AnalyticalEngine.class);
        when(engine.interesetingCustomers(product)).thenThrow(new CantUnderstandException());
        Storage storage = mock(Storage.class);
        NewsList newsList = mock(NewsList.class);

        CustomerAnalysis analysis = new CustomerAnalysis(asList(engine),storage, newsList, handler);
        catchException(() -> analysis.findInterestingCustomers(product));

        verify(handler).handle(isA(CantUnderstandException.class));

    }

    /**
     * Verify that if first AnalyticalEngine fails by throwing an exception,
     * subsequent engines are tried with the same input.
     * Ordering of engines is given by their order in the List passed to
     * constructor of AnalyticalEngine
     */
    @Test
    public void testSubsequentEnginesTriedIfOneFails() throws GeneralException {
        Product product = mock(Product.class);
        ErrorHandler handler = mock(ErrorHandler.class);
        AnalyticalEngine engine1 = mock(AnalyticalEngine.class);
        AnalyticalEngine engine2 = mock(AnalyticalEngine.class);
        AnalyticalEngine engine3 = mock(AnalyticalEngine.class);
        when(engine1.interesetingCustomers(product)).thenThrow(new CantUnderstandException());
        when(engine2.interesetingCustomers(product)).thenThrow(new CantUnderstandException());
        when(engine3.interesetingCustomers(product)).thenThrow(new CantUnderstandException());
        Storage storage = mock(Storage.class);
        NewsList newsList = mock(NewsList.class);

        CustomerAnalysis analysis = new CustomerAnalysis(asList(engine1, engine2, engine3), storage, newsList, handler);

        catchException(() -> analysis.findInterestingCustomers(product));

        verify(engine2).interesetingCustomers(product);
        verify(engine3).interesetingCustomers(product);
    }

    /**
     * Verify that as soon as the first AnalyticalEngine succeeds,
     * this result is returned as result and no subsequent
     * AnalyticalEngine is invoked for this input
     */
    @Test
    public void testNoMoreEnginesTriedAfterOneSucceeds() throws GeneralException {
        Product product = mock(Product.class);
        ErrorHandler handler = mock(ErrorHandler.class);
        AnalyticalEngine engine1 = mock(AnalyticalEngine.class);
        when(engine1.interesetingCustomers(product)).thenReturn(asList(new Customer(1,"test",2)));
        AnalyticalEngine engine2 = mock(AnalyticalEngine.class);
        AnalyticalEngine engine3 = mock(AnalyticalEngine.class);
        Storage storage = mock(Storage.class);
        NewsList newsList = mock(NewsList.class);

        CustomerAnalysis analysis = new CustomerAnalysis(asList(engine1, engine2, engine3), storage, newsList, handler);
        List<Customer> result = analysis.findInterestingCustomers(product);
        assertEquals("test",result.get(0).getName());
        assertEquals(2,result.get(0).getCredit());
        assertEquals(1,result.get(0).getId());

        verify(engine2,never()).interesetingCustomers(product);
        verify(engine3,never()).interesetingCustomers(product);
    }

    /**
     * Verify that once Offer is created for the Customer,
     * this order is persisted in the Storage before being
     * added to the NewsList
     * HINT: you might use mockito InOrder
     */
    @Test
    public void testOfferIsPersistedBefreAddedToNewsList() throws GeneralException {


    }

    /**
     * Verify that Offer is created for every selected Customer for the given Product
     * test with at least two Customers selected by the AnalyticalEngine
     * HINT: you might use mockito ArgumentCaptor 
     */
    @Test
    public void testOfferContainsProductAndCustomer() throws GeneralException {
      
    }

}
