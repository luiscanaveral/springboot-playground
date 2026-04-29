# Diagrams

## Components Overview
| Component | Role | Connection |
|-----------|------|------------|
| **SQLite** | Relational data (users, tweets, likes, comments) | `./.local/data/twitterlike.db` (JDBC) |
| **Neo4j** | Graph relationships (user follows) | `bolt://localhost:7687` (Spring Data Neo4j) |
| **Redis** | Hot read cache (60s TTL) | `localhost:6379` (Spring Data Redis) |

## Feed Flow Sequence Diagrams

### Tweet Creation with Fan-out

```mermaid
sequenceDiagram
    participant Client
    participant Controller as TwitterController
    participant TweetService
    participant SQL as SQL Database (Tweets)
    participant Neo4j as Neo4j (Followers)
    participant Feed as Feed (Fan-out)

    Client->>Controller: POST /api/tweets?userId=X&content=...
    Controller->>TweetService: createTweet(userId, content)
    TweetService->>SQL: Save Tweet (INSERT)
    SQL-->>TweetService: Tweet saved (with ID)
    TweetService->>Neo4j: findFollowerIdsByUserId(userId)
    Neo4j-->>TweetService: Set of follower IDs
    TweetService->>Feed: Fan-out tweet to each follower
    Feed-->>TweetService: Fan-out complete
    TweetService-->>Controller: Return saved Tweet
    Controller-->>Client: 200 OK (Tweet)
```

### Feed Retrieval

```mermaid
sequenceDiagram
    participant Client
    participant Controller as TwitterController
    participant Neo4jService as Neo4jFriendshipService
    participant Neo4j as Neo4j (Following)
    participant TweetService
    participant SQL as SQL Database (Tweets)

    Client->>Controller: GET /api/users/{userId}/feed
    Controller->>Neo4jService: getFollowingIds(userId)
    Neo4jService->>Neo4j: Query FOLLOWS relationships
    Neo4j-->>Neo4jService: Set of following IDs
    Neo4jService-->>Controller: Return following IDs
    Controller->>TweetService: getFeedTweets(userId)
    TweetService->>SQL: SELECT * FROM tweets WHERE user_id IN (followingIds, userId) ORDER BY created_at DESC LIMIT 50
    SQL-->>TweetService: List of Tweets
    TweetService-->>Controller: Return Tweets
    Controller-->>Client: 200 OK (List of Tweets)
```

### Follow/Unfollow Flow

```mermaid
sequenceDiagram
    participant Client
    participant Controller as TwitterController
    participant Neo4jService as Neo4jFriendshipService
    participant Neo4j as Neo4j

    Client->>Controller: POST /api/users/{userId}/follow?followeeId=Y
    Controller->>Neo4jService: follow(userId, followeeId)
    Neo4jService->>Neo4j: Find follower UserNode
    Neo4jService->>Neo4j: Find followee UserNode
    Neo4jService->>Neo4j: Add FOLLOWS relationship (follower->followee)
    Neo4j-->>Neo4jService: Relationship created
    Neo4jService-->>Controller: Void
    Controller-->>Client: 200 OK
```

## Cache Flow Diagrams

### Read Flow (Cached)

```mermaid
flowchart TD
    Client -->|GET Request| Controller
    Controller --> Service
    Service -->|"@Cacheable"| Redis
    Redis -->|Hit| Service
    Service --> Controller
    Controller -->|Response| Client
    Redis -->|Miss| Service
    Service --> SQLite
    Service --> Neo4j
    SQLite --> Service
    Neo4j --> Service
    Service -->|"Store Result"| Redis
    Service --> Controller
```

### Write Flow (Cache Eviction)

```mermaid
flowchart TD
    Client -->|POST/PUT/DELETE| Controller
    Controller --> Service
    Service -->|Update| SQLite
    Service -->|Update| Neo4j
    Service -->|"@CacheEvict"| Redis
    Redis -->|"Invalidate Keys"| Service
    Service --> Controller
    Controller -->|Response| Client
```

### Cache Read Sequence

```mermaid
sequenceDiagram
    participant Client
    participant Service
    participant Redis
    participant SQLite
    participant Neo4j

    Client->>Service: getTweetById(id)
    Service->>Redis: Check cache "tweets::id"
    alt Cache Hit
        Redis-->>Service: Return cached Tweet
        Service-->>Client: Return Tweet
    else Cache Miss
        Service->>SQLite: SELECT * FROM tweets WHERE id = ?
        SQLite-->>Service: Tweet data
        Service->>Neo4j: (if needed) Get relationships
        Neo4j-->>Service: Relationship data
        Service->>Redis: Store result with TTL 60s
        Service-->>Client: Return Tweet
    end
```

### Cache Write Sequence

```mermaid
sequenceDiagram
    participant Client
    participant Service
    participant SQLite
    participant Neo4j
    participant Redis

    Client->>Service: createTweet(userId, content)
    Service->>SQLite: INSERT INTO tweets...
    SQLite-->>Service: Tweet saved
    Service->>Neo4j: (if needed) Update relationships
    Neo4j-->>Service: Done
    Service->>Redis: @CacheEvict("feedTweets", allEntries=true)
    Redis-->>Service: Cache invalidated
    Service-->>Client: Return saved Tweet
```

## Cache Details
- **Cached Data**: Tweets, users, like counts, user feeds (see `application.yml` for full cache names)
- **TTL**: 60 seconds (60000ms)
- **Eviction**: Triggered on all write operations (create/update/delete) to maintain consistency
- **Null Caching**: Disabled to avoid caching missing data

## Database Interaction Summary
1. **Relational Data**: Users, tweets, likes, comments → SQLite via JPA
2. **Graph Data**: User follow relationships → Neo4j via Spring Data Neo4j
3. **Hot Reads**: Frequently accessed data → Redis (with fallback to DB on miss)
4. **Feed Generation**: Uses Neo4j to fetch following IDs, then queries SQLite for tweets (result cached in Redis as `feedTweets`)
