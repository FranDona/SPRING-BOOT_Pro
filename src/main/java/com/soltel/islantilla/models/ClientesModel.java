package com.soltel.islantilla.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class ClientesModel {

    // Atributos (campos BBDD)

    @Id
    private String nif;

    @Column(name = "nombre") //No es necesario ponerlo si se van a llamar igual
    private String nombre;

    @Column
    private int edad;

    @Column
    private boolean sexo;

    //Setter y Getter
    
}
