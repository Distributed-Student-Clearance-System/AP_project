package com.distributedclearance.server.networking;

public class DepartmentLauncher {

    public static void main(String[] args) {

        Thread financeThread =
                new Thread(() -> {

                    DepartmentServer finance =
                            new DepartmentServer(
                                    6001,
                                    "FINANCE"
                            );

                    finance.startServer();
                });

        Thread libraryThread =
                new Thread(() -> {

                    DepartmentServer library =
                            new DepartmentServer(
                                    6002,
                                    "LIBRARY"
                            );

                    library.startServer();
                });

        Thread registrarThread =
                new Thread(() -> {

                    DepartmentServer registrar =
                            new DepartmentServer(
                                    6003,
                                    "REGISTRAR"
                            );

                    registrar.startServer();
                });

        financeThread.start();
        libraryThread.start();
        registrarThread.start();
    }
}