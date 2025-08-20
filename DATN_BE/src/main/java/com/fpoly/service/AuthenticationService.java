package com.fpoly.service;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.crypto.MACVerifier;

import lombok.experimental.NonFinal;

import com.fpoly.dto.IntrospectRequest;
import com.fpoly.dto.IntrospectResponse;
import com.fpoly.exception.AppException;
import com.fpoly.exception.ErrorCode;
import com.fpoly.repository.InvalidatedTokenRepository;

@Service
public class AuthenticationService {

}
