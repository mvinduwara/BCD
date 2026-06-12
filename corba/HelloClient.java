import TestApp.Hello;
import TestApp.HelloHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class HelloClient {
    public static void main(String[] args) {

        ORB orb = ORB.init(args, null);
        try {
            org.omg.CORBA.Object objref = orb.resolve_initial_references("NameService");

            NamingContextExt ncRef = NamingContextExtHelper.narrow(objref);

            Hello href = HelloHelper.narrow(ncRef.resolve_str("Hello"));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
