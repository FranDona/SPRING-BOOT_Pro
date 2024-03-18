package com.soltel.islantilla.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.soltel.islantilla.models.ClientesModel;
import com.soltel.islantilla.models.ReservasModel;
import com.soltel.islantilla.services.ClientesService;
import com.soltel.islantilla.services.ReservasService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;



@RestController

@RequestMapping("/reservas")
public class ReservasController {

    // Como atributos, introduzco los servicios de ambas tablas
    private final ClientesService clientesService;
    private final ReservasService reservasService;

    // Inyecto en la clase ambos servicios en el constructor
    @Autowired
    public ReservasController (ClientesService clientesService,
    ReservasService reservasService) {
        this.clientesService = clientesService;
        this.reservasService = reservasService;
    }

    // Método para consultar
    // Endpoint de ejemplo: [GET] http://localhost:8100/reservas/consultar
    @GetMapping("/consultar")
    public ResponseEntity<List<ReservasModel>> getAllReservas() {
        return ResponseEntity.ok(reservasService.findAllReservas());
    }
    
    // Método para consultar por clave principal (hab, entrada)
    // Endpoint de ejemplo: [GET] http://localhost:8100/reservas/consultar/118/2024-03-23
    // OJO, hay que convertir la fecha -> @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)) 
    @GetMapping("/consultar/{hab}/{entrada}")
    public ResponseEntity<?> getReservaById (@PathVariable int hab, 
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate entrada) {
        Optional<ReservasModel> reserva = reservasService.findReservaById(hab, entrada);
        if(reserva.isPresent()) {
            return ResponseEntity.ok(reserva.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Reserva no encontrada!");
        }
    }
    
    
    //Metodo para insertar
    //Endpoint de ejemplo [POST]
    //http://localhost:8100/reservas/insertar/118/2024-04-6/12345678M/110.65/reserva_20240318_003.pdf/spa,masajes,balinesa
    @PostMapping("/insertar/{hab}/{entrada}/{nif}/{preico}/{rutaPdf}/{opciones}")
    public ResponseEntity<?> createReserva(@PathVariable int hab, @PathVariable LocalDate entrada, @PathVariable String nif, @PathVariable float preico,
    @PathVariable String rutaPdf, @PathVariable String opciones) {

        Optional<ReservasModel> reserva = reservasService.findReservaById(hab, entrada);

        // Tengo que buscar el clietne a partir del nif
        Optional<ClientesModel> clienteBuscado = clientesService.findByClientesByNif(nif);

        if(reserva.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reserva existente!");
        } else if(clienteBuscado.isPresent()){
            ReservasModel nuevaReserva = new ReservasModel();
            ClientesModel cliente = clienteBuscado.get();

            nuevaReserva.setHab(hab);
            nuevaReserva.setEntrada(entrada);
            nuevaReserva.setCliente(cliente);
            nuevaReserva.setPrecio(preico);
            nuevaReserva.setRutaPdf(rutaPdf);
            nuevaReserva.setOpciones(opciones);

            // Inserción
            ReservasModel reservaGuardada = reservasService.saveReserva(nuevaReserva);
            return ResponseEntity.ok(reservaGuardada);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente no existe!");
        }
    }

    //Metodo para actualizar
    //Endpoint de ejemplo [PUT]  http://localhost:8100/clientes/insertar/23456789G/Sara/40/1
    //http://localhost:8100/reservas/insertar/118/2024-04-6/12345678M/110.65/reserva_20240318_003.pdf/spa,masajes,balinesa
    @PutMapping("/insertar/{hab}/{entrada}/{nif}/{preico}/{rutaPdf}/{opciones}")
    public ResponseEntity<?> updateReserva(@PathVariable int hab, @PathVariable LocalDate entrada, 
                                           @PathVariable String nif, @PathVariable float preico,
                                           @PathVariable String rutaPdf, @PathVariable String opciones) {

        // 1º Busco si la reserva YA existe
        Optional<ReservasModel> reserva = reservasService.findReservaById(hab, entrada);
        // 2º Tengo que buscar el cliente a partir del nif
        Optional<ClientesModel> clienteBuscado = clientesService.findByClientesByNif(nif);

        if(!reserva.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reserva no existente!");
        } else if(clienteBuscado.isPresent()){
            ReservasModel reservaActualizada = reserva.get();
            ClientesModel cliente = clienteBuscado.get();

            reservaActualizada.setCliente(cliente);
            reservaActualizada.setPrecio(preico);
            reservaActualizada.setRutaPdf(rutaPdf);
            reservaActualizada.setOpciones(opciones);

            // Inserción
            ReservasModel reservaGuardada = reservasService.saveReserva(reservaActualizada);
            return ResponseEntity.ok(reservaGuardada);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente no existe!");
        }
    }

    public ClientesService getClientesService() {
        return clientesService;
    }

    public ReservasService getReservasService() {
        return reservasService;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
