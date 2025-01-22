package com.example.OnlineContactManager.dao;

import com.example.OnlineContactManager.models.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
    @Query("SELECT c FROM Contact c WHERE c.cid = :cid")
    public   Contact getContactByCid(Integer cid);


    @Query("SELECT c FROM Contact c WHERE c.user.id = :uid")
    public List<Contact> findContactsByUserUid(Integer uid);





}
