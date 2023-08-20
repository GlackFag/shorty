package com.glackfag.shorty.services;

import com.glackfag.shorty.controllers.ApiController;
import com.glackfag.shorty.repositories.CreatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreatorService {
    private final CreatorRepository repository;

    @Autowired
    public CreatorService(CreatorRepository repository) {
        this.repository = repository;
    }

    public List<String> findAliasesByCreatorId(long creatorId){
        return repository.findAliasesByCreatorId(creatorId);
    }

    public List<String> findDestinationsByCreatorId(long creatorId){
        return repository.findDestinationsByCreatorId(creatorId);
    }

    public List<String> findFieldsByCreatorId(long creatorId, ApiController.Field field){
        return switch (field) {
            case DESTINATION -> findDestinationsByCreatorId(creatorId);
            case ALIAS -> findAliasesByCreatorId(creatorId);
        };
    }
}
