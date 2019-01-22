package org.brasbat.entitiview.spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.Repositories;
import org.vaadin.crudui.crud.AddOperationListener;
import org.vaadin.crudui.crud.UpdateOperationListener;
import org.vaadin.crudui.crud.impl.GridCrud;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("repositories")
@UIScope
@SpringComponent
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class RepositoryOverviewUI extends VerticalLayout
{
    private Map<Class<?>, CrudRepository> entityToRepoMap;
    private VerticalLayout layout = new VerticalLayout();
    private VerticalLayout mainContent = new VerticalLayout();

    public RepositoryOverviewUI(ListableBeanFactory listableBeanFactory)
    {
    	this.setSizeFull();
        Repositories repos = new Repositories(listableBeanFactory);
        entityToRepoMap = new HashMap<>();
        ComboBox<ListBoxItem> listBox = new ComboBox<>();
        listBox.setRenderer(new ComponentRenderer<>(item -> new Label(item.getText())));
        for (Class<?> entityClass : repos)
        {
            Optional<Object> repositoryFor = repos.getRepositoryFor(entityClass);
            Repository repository = (Repository) repositoryFor.get();
            if (repository instanceof CrudRepository)
            {
                CrudRepository crudRepository = (CrudRepository) repository;

                entityToRepoMap.put(entityClass, crudRepository);
            }

        }
        List<Class> sortedEntities = new ArrayList<>(entityToRepoMap.keySet());
        Collections.sort(sortedEntities, (a, b) -> a.getSimpleName().compareTo(b.getSimpleName()));
        sortedEntities.forEach(entity -> listBox.setItems(sortedEntities.stream().map(e -> new ListBoxItem(e.getSimpleName(), e, entityToRepoMap.get(e))).collect(Collectors.toList())));
        listBox.addValueChangeListener(e ->
        {
            if (e.getValue() != null) setCrudForEntity(e.getValue().entity, e.getValue().repository);
        });
        layout.setSizeFull();
        mainContent.setSizeFull();
        layout.add(new Html("<h3>Get a simple access to your entities configured in your spring-boot project"), listBox, mainContent);
        this.add(layout);
    }

    private void setCrudForEntity(Class<?> entityClass, Repository repo)
    {
        mainContent.removeAll();
        mainContent.add(new Html("<h4>" + entityClass.getSimpleName() + " (" + entityClass.getName() + ")</h4>"));
        if (repo instanceof CrudRepository)
        {
            CrudRepository crudRepository = (CrudRepository) repo;
            GridCrud<?> crud = new GridCrud(entityClass);
            crud.setFindAllOperation(() -> IteratorUtils.toList(crudRepository.findAll().iterator()));
            crud.setAddOperation(new AddOperationListener()
            {
                @Override
                public Object perform(Object o)
                {
                    return crudRepository.save(o);
                }
            });
            crud.setUpdateOperation(new UpdateOperationListener()
            {
                @Override
                public Object perform(Object o)
                {
                    return crudRepository.save(o);
                }
            });
            crud.setDeleteOperationVisible(false);
            mainContent.add(crud);
        } else
        {
            mainContent.add(new Label("Sorry, no CrudRepository available for chosen Entity class"));
        }
    }

    class ListBoxItem extends H3
    {
        Class entity;
        Repository repository;

        public ListBoxItem(String html, Class entity, Repository repository)
        {
            super(html);
            this.entity = entity;
            this.repository = repository;
        }
        @Override
        public String toString()
        {
            return entity.getSimpleName();
        }
    }
}
