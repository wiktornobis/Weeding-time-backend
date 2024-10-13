package com.weeding.time.app.service;

import com.weeding.time.app.model.ApplicationUser;
import com.weeding.time.app.repository.ApplicationUserRepository;
import com.weeding.time.app.util.PasswordUtil;
import com.weeding.time.app.util.SaltUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ApplicationUserService {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

}
