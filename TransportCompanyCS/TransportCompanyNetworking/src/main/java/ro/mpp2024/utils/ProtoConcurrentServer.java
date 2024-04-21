package ro.mpp2024.utils;

import ro.mpp2024.protobuffprotocol.ProtoWorker;
import ro.mpp2024.services.TransportCompanyObserver;
import ro.mpp2024.services.TransportCompanyService;

import java.net.Socket;

public class ProtoConcurrentServer extends AbstractConcurrentServer{
    private TransportCompanyService service;
    public ProtoConcurrentServer(int port, TransportCompanyService service) {
        super(port);
        this.service = service;
        System.out.println("ProtoConcurrentServer");

    }

    @Override
    protected Thread createWorker(Socket client) {
        ProtoWorker worker = new ProtoWorker(service, client);
        Thread tw = new Thread(worker);
        return tw;
    }
}
