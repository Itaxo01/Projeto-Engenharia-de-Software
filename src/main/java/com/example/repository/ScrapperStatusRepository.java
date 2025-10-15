package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.model.ScrapperStatus;

@Repository
public interface ScrapperStatusRepository extends JpaRepository<ScrapperStatus, Long> {
    ScrapperStatus findFirstByOrderByIdDesc();

    default ScrapperStatus ultimoStatus(){
        return findFirstByOrderByIdDesc();
    }

    default ScrapperStatus salvar(ScrapperStatus scrapperStatus) {
        return save(scrapperStatus);
    }
}
