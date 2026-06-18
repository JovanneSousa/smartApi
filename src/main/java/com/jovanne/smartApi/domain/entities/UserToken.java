package com.jovanne.smartApi.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserToken {
    private String id;
    private String name;
    private List<Claim> claims;
}
