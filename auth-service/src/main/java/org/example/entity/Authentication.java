package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "AUTH")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Authentication
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "ID shouldn't be NULL or EMPTY")
    private long id;

    @NotBlank(message = "Username shouldn't be NULL or EMPTY")
    private String username;

    @NotBlank(message = "Password shouldn't be NULL or EMPTY")
    private String password;

    @NotBlank(message = "Message Title shouldn't be NULL or EMPTY")
    private String authMessageTitle;

    @NotBlank(message = "Message Content shouldn't be NULL or EMPTY")
    private String authMessageContent;
}
