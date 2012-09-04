#!/bin/bash

echo '--- Term query (id=1) ---'
curl 'http://localhost:9200/serverfault/post/_search?pretty=true' -XGET -d \
'{
    "fields": ["title"],
    "query": {
        "term": { "_id": 1 }
    }
}'

echo '--- Term query (body contains "bash") ---'
curl 'http://localhost:9200/serverfault/post/_search?pretty=true' -XGET -d \
'{
    "size": 10,
    "fields": ["title"],
    "query": {
        "term": { "body": "bash" }
    }
}'

echo '--- Boolean query (body OR title contain "bash") ---'
curl 'http://localhost:9200/serverfault/post/_search?pretty=true' -XGET -d \
'{
    "size": 10,
    "fields": ["title"],
    "query": {
        "bool": {
            "should": [
                { "term": { "body": "bash" } },
                { "term": { "title": "bash" } }
            ]
        }
    }
}'

echo '--- Boolean query (body contains "bash" but NOT "bsd") ---'
curl 'http://localhost:9200/serverfault/post/_search?pretty=true' -XGET -d \
'{
    "size": 10,
    "fields": ["title"],
    "query": {
        "bool": {
            "must":     [ { "term": { "body": "bash" } } ],
            "must_not": [ { "term": { "body": "bsd"  } } ]
        }
    }
}'

echo '--- Phrase query (title contains expression "raid controller") ---'
curl 'http://localhost:9200/serverfault/post/_search?pretty=true' -XGET -d \
'{
    "size": 10,
    "fields": ["title"],
    "query": {
        "match_phrase": {
            "title": "raid controller",
            "slop": 0
        }
    }
}'
