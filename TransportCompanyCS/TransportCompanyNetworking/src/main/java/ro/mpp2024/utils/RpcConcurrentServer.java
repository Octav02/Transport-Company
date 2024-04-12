package ro.mpp2024.utils;

import ro.mpp2024.rpcprotocol.ClientRpcWorker;
import ro.mpp2024.services.TransportCompanyService;

import java.net.Socket;

public class RpcConcurrentServer extends AbstractConcurrentServer {
    private final TransportCompanyService service;

    public RpcConcurrentServer(int port, TransportCompanyService service) {
        super(port);
        this.service = service;
        System.out.println("RpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientRpcWorker worker = new ClientRpcWorker(service, client);
        Thread tw = new Thread(worker);
        return tw;
    }
}

