package org.brasbat.entityview.entityviewreact.controller;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController()
@RequestMapping("/entity/repository")
public class RepositioryInfoController
{
    Repositories repositories;

    Map<String, Class<?>> nameToEntityClassMap = null;

    public RepositioryInfoController(ListableBeanFactory listableBeanFactory)
    {
        this.repositories = new Repositories(listableBeanFactory);
    }


    private void assertEntityMap()
    {
        if (nameToEntityClassMap != null)
        {
            return;
        }
        nameToEntityClassMap = new HashMap<>();
        for (Class<?> entityClass : repositories)
        {
            nameToEntityClassMap.put(entityClass.getSimpleName(), entityClass);
        }
    }

    @GetMapping("")
    public List<String> getEntityNames()
    {
        assertEntityMap();
        ArrayList<String> result = new ArrayList<>(nameToEntityClassMap.keySet());
        Collections.sort(result);
        return result;
    }
}
