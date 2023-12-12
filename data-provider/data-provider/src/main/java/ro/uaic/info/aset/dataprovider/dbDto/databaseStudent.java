package ro.uaic.info.aset.dataprovider.dbDto;

import jakarta.persistence.Entity;

import jakarta.persistence.Id;

@Entity
public class databaseStudent {
    @Id
    private Long id;
    private String username;
    private String email;

    // getters and setters
}
