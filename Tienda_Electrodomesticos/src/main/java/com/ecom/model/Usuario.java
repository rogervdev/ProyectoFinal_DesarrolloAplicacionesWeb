package com.ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @NotBlank(message = "La imagen de perfil es obligatoria")
    @Size(max = 150, message = "La imagen de perfil no puede exceder los 150 caracteres")
    @Column(name = "profile_image", nullable = false, length = 150)
    private String profileImage;

    @NotBlank(message = "El rol es obligatorio")
    @Column(name = "rol", nullable = false, length = 50)
    private String rol;

    @Column(name = "is_enable", nullable = false)
    private Boolean isEnable = true;

    @Column(name = "cuenta_no_bloqueada")
    private Boolean cuentaNoBloqueada = true;

    @Column(name = "intento_fallido")
    private Integer intentoFallido = 0;

    @Column(name = "lock_time")
    private Date lockTime;

    @Size(max = 255, message = "Token de restablecimiento demasiado largo")
    @Column(name = "reset_token", length = 255)
    private String resetToken;
}