package lk.jiat.rmi;

import lk.jiat.rmi.Client.Message;
import lk.jiat.rmi.Client.UserService;
import lk.jiat.rmi.model.Data;
import lk.jiat.rmi.model.User;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

public class RMIClient {
    public static void main(String[] args) {

        try {
            //Locate Registry
//            Registry registry = LocateRegistry.getRegistry("localhost", 6000);

            //Naming
//         UserService userservice = (UserService) Naming.lookup("rmi://localhost:6000/user_service");

            //InialContext

            Properties prop = new Properties();
            prop.put(Context.PROVIDER_URL, "rmi://localhost:6000");
            prop.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");

            InitialContext ctx = new InitialContext(prop);
            UserService userservice = (UserService) ctx.lookup("user_service");


//            String[] list = registry.list();
//            for (String s : list) {
//                System.out.println(s);
//            }

//            Message message = (Message) registry.lookup("message_service");
//            String msg = message.hello();
//            Data data  = message.getData();
//            System.out.println(msg.toString());

//            UserService userservice = (UserService) registry.lookup("user_service");
            userservice.addUser(1, new User(1, "Manilka", "Homagama", "Manilka@gmail.com"));

            userservice.getAllUsers().forEach(user -> System.out.println(user.getName()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
