package org.brasbat.entityview.entityviewreact.controller;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@CrossOrigin(origins = "http://localhost:3000", methods = { RequestMethod.GET, RequestMethod.POST })
@RestController
@RequestMapping("/entity/api/repository")
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
		return Arrays.stream(entityClass.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
	}

	@GetMapping("/info/dateformat")
	public String getDateFormat()
	{
		return DateTimeFormatter.ISO_ZONED_DATE_TIME.toString();
	}

	@PostMapping("/data/{entityName}")
	public void save(@PathVariable String entityName, @RequestBody String newValue) throws Exception
	{
		System.out.println(newValue);
		Class<?> entityClass = nameToEntityClassMap.get(entityName);
		Gson g = new Gson();
		Object o = g.fromJson(newValue, entityClass);
		Optional<Object> repositoryFor = repositories.getRepositoryFor(entityClass);
		Repository repository = (Repository) repositoryFor.get();
		if (repository instanceof CrudRepository)
		{
			CrudRepository crudRepository = (CrudRepository) repository;
			crudRepository.save(o);
		}
	}

	@GetMapping("/data/{entityName}")
	public DataResponse getEntityData(@PathVariable String entityName) throws Exception
	{
		Class<?> entityClass = nameToEntityClassMap.get(entityName);
		if (entityClass == null)
		{
			throw new Exception("Entity [" + entityName + "] is unknown here");
		}
		List<String> columns = getColumnsFromClass(entityClass);
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

	private List<String> getColumnsFromClass(Class<?> entityClass)
	{
		PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(entityClass);
		return Arrays.stream(propertyDescriptors).filter((d) -> !d.getName().equals("class")).map((d) -> d.getName()).collect(Collectors.toList());
	}

	private static void assignColumnTypeMap(Class<?> entityClass, DataResponse response)
	{

		PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(entityClass);
		List<PropertyDescriptor> propertyDescriptorList = Arrays.stream(propertyDescriptors).filter((d) -> !d.getName().equals("class")).collect(
			Collectors.toList());
		Map<String, String> result = new HashMap<>();
		propertyDescriptorList.forEach(
			p -> result.put(p.getName(), p.getPropertyType().isEnum() ? "enum" : p.getPropertyType().getSimpleName().toLowerCase()));

		for (Field f : entityClass.getDeclaredFields())
		{
			if ((f.getAnnotation(Id.class) != null || f.getAnnotation(javax.persistence.Id.class) != null))
			{
				response.setIdColumn(f.getName());
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

				}
				catch (IllegalAccessException | NoSuchFieldException e)
				{
					e.printStackTrace();
				}
			}
		}
		response.setEnumColumnToValuesMap(result);
	}

	private static <E extends Enum> E[] getEnumValues(Class<?> enumClass) throws NoSuchFieldException, IllegalAccessException
	{
		Field f = enumClass.getDeclaredField("$VALUES");
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
