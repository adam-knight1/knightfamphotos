package com.fam.knightfam.photo_logic.repository;

import com.fam.knightfam.photo_logic.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}