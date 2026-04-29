package com.example.twitterlike.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserNodeRepository extends Neo4jRepository<UserNode, Long> {
    Optional<UserNode> findByUsername(String username);

    @Query("MATCH (u:User {id: $userId})<-[:FOLLOWS]-(follower:User) RETURN follower.id as id")
    Set<Long> findFollowerIdsByUserId(Long userId);
}
