package com.example.twitterlike.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.HashSet;
import java.util.Set;

@Node("User")
public class UserNode {

    @Id
    private Long id;

    @Property("username")
    private String username;

    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    private Set<UserNode> following = new HashSet<>();

    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.INCOMING)
    private Set<UserNode> followers = new HashSet<>();

    public UserNode() {
    }

    public UserNode(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<UserNode> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserNode> following) {
        this.following = following;
    }

    public Set<UserNode> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<UserNode> followers) {
        this.followers = followers;
    }
}
