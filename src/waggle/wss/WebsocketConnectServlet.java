package waggle.wss;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@WebServlet(name = "WebsocketConnectServlet", urlPatterns = { "/home" } )
@ServerEndpoint("/trial")
public class WebsocketConnectServlet extends HttpServlet {
    private static Set<Session> connectedSockets = Collections.synchronizedSet(new HashSet<Session>());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<h1>Soum</h1>");
        out.flush();
    }

    @OnOpen
    public void onOpen(Session session) {
        boolean isAdded = connectedSockets.add(session);
        System.out.println("Server: Session opened. " + session.getId());
        System.out.println("Size of connectedSockets Set: " + connectedSockets.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message: " + message + " received on Session: " + session.getId());
        System.out.println("Size of connectedSocket Set: " + connectedSockets.size());

        // Broadcast the message to all connected clients.

        Iterator<Session> iterator = connectedSockets.iterator();

        while ( iterator.hasNext()) {
            iterator.next().getAsyncRemote().sendText("ECHO: " + message);
        }
    }

    @OnClose
    public void onClose(Session session) {
        connectedSockets.remove(session);
        System.out.println("Connection closed for Socket: " + session.getId());
        System.out.println("Size of connectedSockets Set: " + connectedSockets.size());
    }
}
