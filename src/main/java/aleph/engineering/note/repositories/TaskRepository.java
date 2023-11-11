package aleph.engineering.note.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.graphql.data.GraphQlRepository;

import aleph.engineering.note.domain.Task;

@GraphQlRepository
public interface TaskRepository extends ReactiveMongoRepository<Task, String> {
    
}
