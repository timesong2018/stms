package com.ukefu.webim.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ukefu.webim.web.model.Contacts;

public interface ContactsRepository extends  JpaRepository<Contacts, String> {
}
