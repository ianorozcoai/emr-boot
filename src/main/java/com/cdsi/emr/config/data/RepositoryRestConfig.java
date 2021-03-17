package com.cdsi.emr.config.data;

import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

import lombok.AllArgsConstructor;

@Configuration @AllArgsConstructor
public class RepositoryRestConfig implements RepositoryRestConfigurer {

    private EntityManager entityManager;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(this.entityManager.getMetamodel().getEntities().stream()
                .map(EntityType::getJavaType)
                .collect(Collectors.toList())
                .toArray(new Class[0]));
    }

}