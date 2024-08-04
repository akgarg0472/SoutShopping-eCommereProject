package com.akgarg.ecommerce.model.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Message implements Serializable {

    private static final long serialVersionUID = -2274836373485684L;

    private String message;
    private String type;

}