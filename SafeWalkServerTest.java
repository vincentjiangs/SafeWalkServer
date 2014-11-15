import org.junit.*;
import static org.junit.Assert.*;
import java.net.SocketTimeoutException;
import java.net.ServerSocket;
import java.io.IOException;
//import net.sf.webcat.annotations.*;

public class SafeWalkServerTest {
    final String ERR_INVALID_REQUEST = "ERROR: invalid request";
    final String HOST = "localhost";
    final boolean TIMEOUT = true;
    final int RETENTION = 100;
    
    /**
     * Send an invalid command. 
     **/
    @Test()
    public void testInvalidCommand() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = ":INVALID_COMMAND";
        Client c1 = new Client(HOST, port, cmd);
        Thread ct1 = new Thread(c1);
        ct1.start();
        ct1.join();
        
        assertEquals(ERR_INVALID_REQUEST, c1.getResult());
        
        cmd = ":SHUTDOWN";
        Client c2 = new Client(HOST, port, cmd);
        Thread ct2 = new Thread(c2);
        ct2.start();
        ct2.join();
        st.join();
    }
    
    /**
     * Test :RESET. 
     **/
    @Test
    public void testReset() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka,LWSN,PUSH,0";
        Client c1 = new Client(HOST, port, cmd);
        Thread ct1 = new Thread(c1);
        ct1.start();
        
        Thread.sleep(RETENTION);
        
        cmd = ":RESET";
        Client c2 = new Client(HOST, port, cmd);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct1.join();
        ct2.join();
        
        String exp = "ERROR: connection reset";
        assertEquals(exp, c1.getResult());
        exp = "RESPONSE: success";
        assertEquals(exp, c2.getResult());
        
        cmd = ":SHUTDOWN";
        Client c3 = new Client(HOST, port, cmd);
        Thread ct3 = new Thread(c3);
        ct3.start();
        ct3.join();
        st.join();
    }
    
    /**
     * Test :LIST_PENDING_REQUESTS. 
     **/
    @Test
    public void testListPendingRequests() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka,LWSN,PUSH,0";
        Client c1 = new Client(HOST, port, cmd, TIMEOUT);
        Thread ct1 = new Thread(c1);
        ct1.start();
        
        Thread.sleep(RETENTION);
        
        cmd = ":LIST_PENDING_REQUESTS";
        Client c2 = new Client(HOST, port, cmd);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct1.join();
        ct2.join();
        
        String exp = "[[Danushka, LWSN, PUSH, 0]]";
        assertEquals(exp, c2.getResult());
        
        cmd = ":SHUTDOWN";
        Client c3 = new Client(HOST, port, cmd);
        Thread ct3 = new Thread(c3);
        ct3.start();
        ct3.join();
        st.join();
    }
    
    /**
     * Test request order for :LIST_PENDING_REQUESTS. 
     **/
    @Test
    public void testListPendingRequestsOrder() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka,LWSN,PUSH,0";
        Client c1 = new Client(HOST, port, cmd, TIMEOUT);
        Thread ct1 = new Thread(c1);
        ct1.start();
        
        Thread.sleep(RETENTION);
        
        cmd = "Dinushi,LWSN,EE,0";
        Client c2 = new Client(HOST, port, cmd, TIMEOUT);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct1.join();
        ct2.join();
        
        cmd = ":LIST_PENDING_REQUESTS";
        Client c3 = new Client(HOST, port, cmd);
        Thread ct3 = new Thread(c3);
        ct3.start();
        ct3.join();
        
        String exp = "[[Danushka, LWSN, PUSH, 0], [Dinushi, LWSN, EE, 0]]";
        assertEquals(exp, c3.getResult());
        
        cmd = ":SHUTDOWN";
        Client c4 = new Client(HOST, port, ":SHUTDOWN");
        Thread ct4 = new Thread(c4);
        ct4.start();
        ct4.join();
        st.join();
    }
    
    /**
     * Test :SHUTDOWN. 
     **/
    @Test
    public void testShutdown() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka,LWSN,PUSH,0";
        Client c1 = new Client(HOST, port, cmd);
        Thread ct1 = new Thread(c1);
        ct1.start();
        
        Thread.sleep(RETENTION);
        
        cmd = ":SHUTDOWN";
        Client c2 = new Client(HOST, port, cmd);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct1.join();
        ct2.join();
        st.join();
        
        String exp = "ERROR: connection reset";
        assertEquals(exp, c1.getResult());
        exp = "RESPONSE: success";
        assertEquals(exp, c2.getResult());
    }
    
    /**
     * Test a request with an invalid FROM. 
     **/
    @Test
    public void testInvalidFrom() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka,FROM,PUSH,0";
        Client c1 = new Client(HOST, port, cmd);
        Thread ct1 = new Thread(c1);
        ct1.start();
        ct1.join();
        
        assertEquals(ERR_INVALID_REQUEST, c1.getResult());
        
        cmd = ":SHUTDOWN";
        Client c2 = new Client(HOST, port, cmd);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct2.join();
        st.join();
    }
    
    /**
     * Test a request with an invalid TO. 
     **/
    @Test
    public void testInvalidTo() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka,LWSN,TO,0";
        Client c1 = new Client(HOST, port, cmd);
        Thread ct1 = new Thread(c1);
        ct1.start();
        ct1.join();
        
        assertEquals(ERR_INVALID_REQUEST, c1.getResult());
        
        cmd = ":SHUTDOWN";
        Client c2 = new Client(HOST, port, cmd);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct2.join();
        st.join();
    }
    
    /**
     * Test sending a request with an invalid delimiter. 
     **/
    @Test
    public void testInvalidRequest1() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka:LWSN:TO:0";
        Client c1 = new Client(HOST, port, cmd);
        Thread ct1 = new Thread(c1);
        ct1.start();
        ct1.join();
        
        assertEquals(ERR_INVALID_REQUEST, c1.getResult());
        
        cmd = ":SHUTDOWN";
        Client c2 = new Client(HOST, port, cmd);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct2.join();
        st.join();
    }
    
    /**
     * Test sending a request with an invalid number of fields. 
     **/
    @Test
    public void testInvalidRequest2() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka,LWSN,0";
        Client c1 = new Client(HOST, port, cmd);
        Thread ct1 = new Thread(c1);
        ct1.start();
        ct1.join();
        
        assertEquals(ERR_INVALID_REQUEST, c1.getResult());
        
        cmd = ":SHUTDOWN";
        Client c2 = new Client(HOST, port, cmd);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct2.join();
        st.join();
    }
    
    /**
     * Test sending a request with FROM = *. 
     **/
    @Test
    public void testFromStar() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka,*,PUSH,0";
        Client c1 = new Client(HOST, port, cmd);
        Thread ct1 = new Thread(c1);
        ct1.start();
        ct1.join();
        
        assertEquals(ERR_INVALID_REQUEST, c1.getResult());
        
        cmd = ":SHUTDOWN";
        Client c2 = new Client(HOST, port, cmd);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct2.join();
        st.join();
    }
    
    /**
     * Test sending a request with FROM = TO. 
     **/
    @Test
    public void testToEqualsFrom() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka,PUSH,PUSH,0";
        Client c1 = new Client(HOST, port, cmd);
        Thread ct1 = new Thread(c1);
        ct1.start();
        ct1.join();
        
        assertEquals(ERR_INVALID_REQUEST, c1.getResult());
        
        cmd = ":SHUTDOWN";
        Client c2 = new Client(HOST, port, cmd);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct2.join();
        st.join();
    }
    
    /**
     * Test a scenario where there is an exact match. 
     **/
    @Test
    public void testExactMatch() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka,LWSN,PUSH,0";
        Client c1 = new Client(HOST, port, cmd);
        Thread ct1 = new Thread(c1);
        ct1.start();
        
        cmd = "Dinushi,LWSN,PUSH,0";
        Client c2 = new Client(HOST, port, cmd);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct1.join();
        ct2.join();
        
        String exp = "RESPONSE: [Dinushi, LWSN, PUSH, 0]";
        assertEquals(exp, c1.getResult());
        exp = "RESPONSE: [Danushka, LWSN, PUSH, 0]";
        assertEquals(exp, c2.getResult());
        
        cmd = ":SHUTDOWN";
        Client c3 = new Client(HOST, port, cmd);
        Thread ct3 = new Thread(c3);
        ct3.start();
        
        ct3.join();
        st.join();
    }
    
    /**
     * Test a scenario where the second request has * as TO.. 
     **/
    @Test
    public void testAnyMatch() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka,LWSN,PUSH,0";
        Client c1 = new Client(HOST, port, cmd);
        Thread ct1 = new Thread(c1);
        ct1.start();
        
        Thread.sleep(RETENTION);
        
        cmd = "Dinushi,LWSN,*,0";
        Client c2 = new Client(HOST, port, cmd);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct1.join();
        ct2.join();
        
        String exp = "RESPONSE: [Dinushi, LWSN, *, 0]";
        assertEquals(exp, c1.getResult());
        exp = "RESPONSE: [Danushka, LWSN, PUSH, 0]";
        assertEquals(exp, c2.getResult());
        
        cmd = ":SHUTDOWN";
        Client c3 = new Client(HOST, port, cmd);
        Thread ct3 = new Thread(c3);
        ct3.start();
        
        ct3.join();
        st.join();
    }
    
    /**
     * Same as "testAnyMatch" but with a different order of requests. 
     **/
    @Test
    public void testAnyMatch2() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka,LWSN,*,0";
        Client c1 = new Client(HOST, port, cmd);
        Thread ct1 = new Thread(c1);
        ct1.start();
        
        Thread.sleep(RETENTION);
        
        cmd = "Dinushi,LWSN,PUSH,0";
        Client c2 = new Client(HOST, port, cmd);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct1.join();
        ct2.join();
        
        String exp = "RESPONSE: [Dinushi, LWSN, PUSH, 0]";
        assertEquals(exp, c1.getResult());
        exp = "RESPONSE: [Danushka, LWSN, *, 0]";
        assertEquals(exp, c2.getResult());
        
        cmd = ":SHUTDOWN";
        Client c3 = new Client(HOST, port, cmd);
        Thread ct3 = new Thread(c3);
        ct3.start();
        
        ct3.join();
        st.join();
    }
    
    /**
     * Test first-come-first-serve. 
     **/
    @Test
    public void testFCFS() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Dihein,LWSN,PUSH,0";
        Client c1 = new Client(HOST, port, cmd);
        Thread ct1 = new Thread(c1);
        ct1.start();
        
        Thread.sleep(RETENTION);
        
        cmd = "Danushka,LWSN,EE,0";
        Client c2 = new Client(HOST, port, cmd, TIMEOUT);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        Thread.sleep(RETENTION);
        
        cmd = "Dinushi,LWSN,*,0";
        Client c3 = new Client(HOST, port, cmd);
        Thread ct3 = new Thread(c3);
        ct3.start();
        
        ct1.join();
        ct2.join();
        ct3.join();
        
        String exp = "RESPONSE: [Dinushi, LWSN, *, 0]";
        assertEquals(exp, c1.getResult());
        exp = "RESPONSE: [Dihein, LWSN, PUSH, 0]";
        assertEquals(exp, c3.getResult());
        
        cmd = ":LIST_PENDING_REQUESTS";
        Client c4 = new Client(HOST, port, cmd);
        Thread ct4 = new Thread(c4);
        ct4.start();
        ct4.join();
        
        exp = "[[Danushka, LWSN, EE, 0]]";
        assertEquals(exp, c4.getResult());
        
        cmd = ":SHUTDOWN";
        Client c5 = new Client(HOST, port, cmd);
        Thread ct5 = new Thread(c5);
        ct5.start();
        
        ct5.join();
        st.join();
    }
    
    /**
     * Test a scenario where there are two requests with TO = * but FROM is the same. 
     **/
    @Test
    public void testToBothStar() throws InterruptedException, IOException {
        SafeWalkServer s = new SafeWalkServer();
        int port = s.getLocalPort();
        Thread st = new Thread(s);
        st.start();
        
        String cmd = "Danushka,LWSN,*,0";
        Client c1 = new Client(HOST, port, cmd, TIMEOUT);
        Thread ct1 = new Thread(c1);
        ct1.start();
        
        Thread.sleep(RETENTION);
        
        cmd = "Dinushi,LWSN,*,0";
        Client c2 = new Client(HOST, port, cmd, TIMEOUT);
        Thread ct2 = new Thread(c2);
        ct2.start();
        
        ct1.join();
        ct2.join();
        
        cmd = ":LIST_PENDING_REQUESTS";
        Client c3 = new Client(HOST, port, cmd);
        Thread ct3 = new Thread(c3);
        ct3.start();
        ct3.join();
        
        String exp = "[[Danushka, LWSN, *, 0], [Dinushi, LWSN, *, 0]]";
        assertEquals(exp, c3.getResult());
        
        cmd = ":SHUTDOWN";
        Client c4 = new Client(HOST, port, cmd);
        Thread ct4 = new Thread(c4);
        ct4.start();
        
        ct4.join();
        st.join();
        
    }
}
