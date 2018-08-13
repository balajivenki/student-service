package com.qualys.meetup.service;

import com.qualys.meetup.entity.StudentEntity;
import com.qualys.meetup.exception.ServiceException;
import info.archinnov.achilles.generated.manager.StudentEntity_Manager;
import info.archinnov.achilles.type.strategy.InsertStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

/**
 * Created by aagarwal on 6/27/2018.
 */
@Service
public class CassandraService {

	@Autowired
	private StudentEntity_Manager studentEntityManager;

	public void saveStudent(StudentEntity studentEntity) {
		try {
			studentEntityManager.crud()
					.insert(studentEntity)
					.withInsertStrategy(InsertStrategy.NOT_NULL_FIELDS)
					.execute();
		} catch (Exception e) {
			throw new ServiceException("Unable to Save", e);
		}
	}

	public Mono<StudentEntity> getStudentById(UUID studentId) {
		return Mono.fromFuture(studentEntityManager.dsl().select()
				.allColumns_FromBaseTable().where().studentId().Eq(studentId).getOneAsync());
	}

	public Flux<StudentEntity> getAllStudents() {
		List<StudentEntity> studentEntityList = studentEntityManager.dsl().select().allColumns_FromBaseTable()
				.without_WHERE_Clause().getList();
		return Flux.fromIterable(studentEntityList);
	}

	public Mono<List<StudentEntity>> getStudentById(List<UUID> studentIds) {

		return Flux.fromIterable(studentIds)
				.flatMap(studentId -> Flux.fromIterable(getList(studentId)).subscribeOn(Schedulers.parallel()), studentIds.size())
				.collectList();

	}

	private List<StudentEntity> getList(UUID studentId) {
		return studentEntityManager.dsl().select()
				.allColumns_FromBaseTable().where().studentId().Eq(studentId).getList();
	}
}
