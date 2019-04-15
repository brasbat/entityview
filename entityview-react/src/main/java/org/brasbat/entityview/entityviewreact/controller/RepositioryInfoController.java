package org.brasbat.entityview.entityviewreact.controller;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
            List list = IteratorUtils.toList(crudRepository.findAll().iterator());
            List result = new ArrayList();
            list.forEach(e -> result.add(entityClass.cast(e)));
            DataResponse dataResponse = new DataResponse(columns, result);
            assignColumnTypeMap(entityClass, dataResponse);
            assignEnumInfo(entityClass, dataResponse);
            return dataResponse;

        }
        throw new Exception("Entity has no CrudRepository");
    }

    private static void assignColumnTypeMap(Class<?> entityClass, DataResponse response)
    {
        Map<String, String> result = new HashMap<>();
        for (Field f : entityClass.getDeclaredFields())
        {
            String name = f.getName();
            Class<?> type = f.getType();
            if (type.isEnum())
            {
                result.put(name, "enum");
            } else
            {
                result.put(name, type.getSimpleName().toLowerCase());
            }
            if (f.getAnnotation(Id.class) != null)
            {
                response.setIdColumn(name);
            }
        }
        response.setColumnToColumnTypeMap(result);
    }

    private static void assignEnumInfo(Class<?> entityClass, DataResponse response)
    {
        Map<String, List<String>> result = new HashMap<>();

        for (Field f : entityClass.getDeclaredFields())
        {
            String name = f.getName();
            Class<?> type = f.getType();
            if (type.isEnum())
            {
                try
                {
                    Enum[] enumValues = getEnumValues(type);
                    result.put(name, Arrays.stream(enumValues).map(e -> e.name()).collect(Collectors.toList()));

                } catch (IllegalAccessException | NoSuchFieldException e)
                {
                    e.printStackTrace();
                }
            }
        }
        response.setEnumColumnToValuesMap(result);
    }

    private static <E extends Enum> E[] getEnumValues(Class<?> enumClass)
            throws NoSuchFieldException, IllegalAccessException
    {
        Field f = enumClass.getDeclaredField("$VALUES");
        System.out.println(f);
        System.out.println(Modifier.toString(f.getModifiers()));
        f.setAccessible(true);
        Object o = f.get(null);
        return (E[]) o;
    }

    class DataResponse
    {
        List<String> columns;
        List content;
        String idColumn;
        Map<String, List<String>> enumColumnToValuesMap;
        Map<String, String> columnToColumnTypeMap;

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

        public String getIdColumn()
        {
            return idColumn;
        }

        public void setIdColumn(String idColumn)
        {
            this.idColumn = idColumn;
        }

        public Map<String, List<String>> getEnumColumnToValuesMap()
        {
            return enumColumnToValuesMap;
        }

        public void setEnumColumnToValuesMap(Map<String, List<String>> enumColumnToValuesMap)
        {
            this.enumColumnToValuesMap = enumColumnToValuesMap;
        }

        public Map<String, String> getColumnToColumnTypeMap()
        {
            return columnToColumnTypeMap;
        }

        public void setColumnToColumnTypeMap(Map<String, String> columnToColumnTypeMap)
        {
            this.columnToColumnTypeMap = columnToColumnTypeMap;
        }
    }
}
