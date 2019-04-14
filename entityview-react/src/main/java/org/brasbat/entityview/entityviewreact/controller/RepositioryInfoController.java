package org.brasbat.entityview.entityviewreact.controller;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/entity/repository")
public class RepositioryInfoController
{
    Repositories repositories;

    Map<String, Class<?>> nameToEntityClassMap = null;

    public RepositioryInfoController(ListableBeanFactory listableBeanFactory)
    {
        this.repositories = new Repositories(listableBeanFactory);
    }

    @PostConstruct
    public void assertEntityMap()
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

    @GetMapping("/info/{entityName}")
    public List<String> getEntityInfo(@PathVariable String entityName) throws Exception
    {
        System.out.println("GETTING INFO FOR [" + entityName + "]");
        Class<?> entityClass = nameToEntityClassMap.get(entityName);
        if (entityClass == null)
        {
            throw new Exception("Entity [" + entityName + "] is unknown here");
        }
        return Arrays.stream(entityClass.getDeclaredFields()).map(f -> f.getName()).collect(Collectors.toList());
    }

    @GetMapping("/data/{entityName}")
    public DataResponse getEntityData(@PathVariable String entityName) throws Exception
    {
        Class<?> entityClass = nameToEntityClassMap.get(entityName);
        if (entityClass == null)
        {
            throw new Exception("Entity [" + entityName + "] is unknown here");
        }
        List<String> columns = Arrays.stream(entityClass.getDeclaredFields()).map(f -> f.getName()).collect(Collectors.toList());
        Optional<Object> repositoryFor = repositories.getRepositoryFor(entityClass);
        Repository repository = (Repository) repositoryFor.get();
        if (repository instanceof CrudRepository)
        {
            CrudRepository crudRepository = (CrudRepository) repository;
//            List collect =
            List list = IteratorUtils.toList(crudRepository.findAll().iterator());
            List result = new ArrayList();
            list.forEach(e -> result.add(entityClass.cast(e)));
            return new DataResponse(columns, result);

        }
        throw new Exception("Entity has no CrudRepository");
    }

    class DataResponse
    {
        List<String> columns;
        List content;

        public DataResponse(List<String> columns, List content)
        {
            this.columns = columns;
            this.content = content;
        }

        public List<String> getColumns()
        {
            return columns;
        }

        public void setColumns(List<String> columns)
        {
            this.columns = columns;
        }

        public List getContent()
        {
            return content;
        }

        public void setContent(List content)
        {
            this.content = content;
        }
    }
}
