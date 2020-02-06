package org.brasbat.entityview.entityviewreact.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class EntityGraphService
{

	private final Repositories repositories;
	private final EntityManager entityManager;

	public EntityGraphService(ListableBeanFactory listableBeanFactory, EntityManager em)
	{
		this.repositories = new Repositories(listableBeanFactory);
		this.entityManager = em;
	}

	public String buildGraphizDotModel()
	{
		Graph graph = from(entityManager.getMetamodel());
		StringBuilder sb = new StringBuilder("digraph G {\n");
		graph.getNodes().forEach(n -> sb.append(n.getName() + "\n"));
		graph.getEdges().forEach(e -> sb.append(e.getFrom() + " -> " + e.getTo() + "\n"));
		sb.append("}");
		return sb.toString();
	}

	private Graph from(final Metamodel jpa)
	{
		return new Graph(jpa.getEntities().stream().map(EntityType::getName).map(Node::new).collect(toList()),
			jpa.getEntities().stream().map(this::visitEntity).flatMap(Collection::stream).collect(toSet()));
	}

	private Set<Edge> visitEntity(final Type<?> type)
	{
		if (!EntityType.class.isInstance(type))
		{
			return Collections.emptySet();
		}
		final EntityType<?> e = EntityType.class.cast(type);
		return e.getDeclaredAttributes().stream().filter(a -> a.isCollection() || a.isAssociation()).map(a -> {
			// extract the relationship type
			final Type aType = a.isCollection() ? PluralAttribute.class.cast(a).getElementType() : SingularAttribute.class.cast(a).getType();

			// extract target type (alias of the entity if possible or java type)
			final String to = EntityType.class.isInstance(aType) ? EntityType.class.cast(aType).getName() : aType.getJavaType().getName();

			// build the edge finally
			return new Edge(e.getName(), to, a.getName());
		}).collect(toSet());
	}
}
