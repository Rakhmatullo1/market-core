package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.entity.Worker;
import com.rahmatullo.comfortmarket.service.dto.WorkerDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring")
public abstract class WorkerMapper {

    @Mapping(target = "username", source = "worker.user.username")
    @Mapping(target = "role", expression = "java(worker.getUser().getRole().name())")
    @Mapping(target = "premiseInfo", source = "worker.premise.name")
    @Mapping(target = "phoneNumber", source = "worker.user.phoneNumber", defaultValue = "null")
    @Mapping(target = "fullName", source = "worker.user.fullName")
    @Mapping(target = "enabled", source = "worker.user.enabled")
    @Mapping(target = "ownerName", expression = "java(worker.getOwner().getUsername())")
    public abstract WorkerDto toWorkerDto(Worker worker);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "premise", source = "premise")
    @Mapping(target = "owner", source = "owner")
    public abstract Worker toWorker(User user, Premise premise, User owner);
}
