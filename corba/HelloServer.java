import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;
import TestApp.Hello;
import TestApp.HelloHelper;

public class HelloServer {
    public static void main(String[] args) {

        ORB orb = ORB.init(args, null);
        try {
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            HelloWorldImple hello = new HelloWorldImple();

            org.omg.CORBA.Object obj = rootpoa.servant_to_reference(hello);

            Hello href = HelloHelper.narrow(obj);

            org.omg.CORBA.Object obj2 = orb.resolve_initial_references("NameService");

            NamingContextExt ncRef = NamingContextExtHelper.narrow(obj2);

            NameComponent path[] = ncRef.to_name("Hello");

            ncRef.rebind(path, href);
            System.out.println("CORBA Server ready");
            orb.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
