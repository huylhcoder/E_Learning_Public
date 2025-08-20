package com.fpoly.entity;

import java.util.Date;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "invalidatedTokens")
public class InvalidatedToken {

    @Id
    String id;

    @Column(name = "expiry_time", nullable = false)
    Date expiryTime;

}